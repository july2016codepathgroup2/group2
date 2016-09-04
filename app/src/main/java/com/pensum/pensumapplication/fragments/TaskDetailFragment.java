package com.pensum.pensumapplication.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.models.Task;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class TaskDetailFragment extends Fragment {

    @BindView(R.id.tvTitle)TextView tvTitle;
    @BindView(R.id.tvStatus)TextView tvStatus;
    @BindView(R.id.tvBudget)TextView tvBudget;
    @BindView(R.id.ivTaskDetailOwnerProf)ImageView ivTaskDetailOwnerProf;
    @BindView(R.id.rlTaskDetail) RelativeLayout rlTaskDetail;
    @BindView(R.id.ivTaskPic) ImageView ivTaskPic;

    private Task task;
    private OnTaskDetailActionListener listener;
    private Unbinder unbinder;
    private SupportMapFragment mapFragment;

    public TaskDetailFragment() {

    }

    public interface OnTaskDetailActionListener {
        void launchContactOwnerDialog(Task task);
        void launchProfileFragment(String userId);
        void launchEditTaskFragment(Task task);
//        void launchAcceptCandidateDialog(Task task);
        void launchConversationsFragment(Task task);
        void launchCompleteTaskDialogFragment(Task task);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof  OnTaskDetailActionListener) {
            listener = (OnTaskDetailActionListener) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement TaskDetailFragment.OnContactOwnerListener");
        }
    }

    public static TaskDetailFragment newInstance(String taskId) {
        TaskDetailFragment fragment = new TaskDetailFragment();
        Bundle args = new Bundle();
        args.putString("task_id", taskId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view;
        fetchSelectedTask();

        if (TextUtils.equals(task.getPostedBy().getObjectId(),ParseUser.getCurrentUser().getObjectId())){
            view = inflater.inflate(R.layout.fragment_task_detail_owner, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_task_detail, container, false);
            mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.detailMap));
            if (mapFragment != null) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap map) {
                        LatLng location =
                                new LatLng(task.getLocation().getLatitude(),task.getLocation().getLongitude());

                        loadMap(map,location);
                    }
                });
            } else {
                Toast.makeText(getContext(), "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
            }
        }
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateViews(view);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

        if(mapFragment!=null) {
            getFragmentManager().beginTransaction().remove(mapFragment).commit();
            mapFragment=null;
        }
    }

    private void fetchSelectedTask() {
        String taskId =  getArguments().getString("task_id");
        ParseQuery<Task> query = ParseQuery.getQuery(Task.class);
        //TODO The update data will not show
        // First try to find from the cache and only then go to network
        // query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK); // or CACHE_ONLY
        // Execute the query to find the object with ID
        query.include("posted_by");
        try {
            task = query.get(taskId);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void populateViews(View view) {
        tvTitle.setText(task.getTitle());
        tvBudget.setText(NumberFormat.getCurrencyInstance().format(task.getBudget()));
        tvStatus.setText(task.getStatus().toUpperCase());

        switch(task.getStatus()){
            case "open":
                tvStatus.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                break;

            case "accepted":
                tvStatus.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorIconGreenDark));
                break;

            case "completed":
                tvStatus.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorIconGreenLight));
                break;
        }

        if(task.getTaskPic()!=null) {
            try {
                ParseFile parseFile = task.getTaskPic();
                byte[] data = parseFile.getData();
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                ivTaskPic.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ImageButton ibEditTask = (ImageButton) view.findViewById(R.id.ibEditTask);

        Button btnAction = (Button) view.findViewById(R.id.btnAction);
        ParseUser postedBy = task.getPostedBy();

        if (TextUtils.equals(postedBy.getObjectId(),ParseUser.getCurrentUser().getObjectId())) {
            if(TextUtils.equals(task.getStatus(),"open")){
                btnAction.setText(getResources().getString(R.string.accept));
                btnAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.launchConversationsFragment(task);
                    }
                });

                if(task.getHasBidder())
                    btnAction.setEnabled(true);
                else
                    btnAction.setEnabled(false);

            } else if (TextUtils.equals(task.getStatus(),"accepted")){
                btnAction.setText(getResources().getString(R.string.complete));
                btnAction.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         listener.launchCompleteTaskDialogFragment(task);
                     }
                });
                ibEditTask.setImageResource(0);
            } else {
                btnAction.setVisibility(View.INVISIBLE);
                ibEditTask.setImageResource(0);
            }


            ibEditTask.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     listener.launchEditTaskFragment(task);
                 }
            });
        } else {
            btnAction.setText(getResources().getString(R.string.contact));
            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.launchContactOwnerDialog(task);
                }
            });

            final String userId = postedBy.getObjectId();
            ivTaskDetailOwnerProf.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     listener.launchProfileFragment(userId);
                 }
            });
        }

        String imageUrl = postedBy.getString("profilePicUrl");
        if (imageUrl != null){
            Picasso.with(getContext()).load(imageUrl).
                    transform(new CropCircleTransformation()).into(ivTaskDetailOwnerProf);
        }
    }

    protected void loadMap(GoogleMap googleMap, LatLng location) {
        if(googleMap!=null) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            googleMap.getUiSettings().setCompassEnabled(false);
            googleMap.getUiSettings().setRotateGesturesEnabled(false);
            googleMap.getUiSettings().setScrollGesturesEnabled(false);
            googleMap.getUiSettings().setTiltGesturesEnabled(false);
            googleMap.getUiSettings().setZoomControlsEnabled(false);
            googleMap.getUiSettings().setZoomGesturesEnabled(false);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15));

            BitmapDescriptor defaultMarker =
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);

            googleMap.addMarker(new MarkerOptions()
                    .position(location)
                    .icon(defaultMarker));
        }
    }
}

