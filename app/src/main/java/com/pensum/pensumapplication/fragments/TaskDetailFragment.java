package com.pensum.pensumapplication.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class TaskDetailFragment extends DialogFragment {

    @BindView(R.id.tvTitle)TextView tvTitle;
    @BindView(R.id.tvStatus)TextView tvStatus;
    @BindView(R.id.tvBudget)TextView tvBudget;
    @BindView(R.id.ivTaskDetailOwnerProf)ImageView ivTaskDetailOwnerProf;
//    @BindView(R.id.rvTaskImages)RecyclerView rvTaskImages;
    @BindView(R.id.rlTaskDetail) RelativeLayout rlTaskDetail;
    @BindView(R.id.ivTaskPic) ImageView ivTaskPic;

    private Task task;
    private OnTaskDetailActionListener listener;
    private Unbinder unbinder;

    public TaskDetailFragment() {

    }

    public interface OnTaskDetailActionListener {
        void launchContactOwnerDialog(Task task);
        void launchProfileFragment(String userId);
        void launchEditTaskFragment(Task task);
        void launchAcceptCandidateDialog(Task task);
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
            view = inflater.inflate(R.layout.fragment_task_detail_owner, container);
        } else {
            view = inflater.inflate(R.layout.fragment_task_detail, container);
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
        tvStatus.setText(task.getStatus());
//        if(task.getImages() == null || task.getImages().length() < 1) {
//            rvTaskImages.setVisibility(View.GONE);
//        }

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

        Button btnAction = (Button) view.findViewById(R.id.btnAction);
        ParseUser postedBy = task.getPostedBy();
         if (TextUtils.equals(postedBy.getObjectId(),ParseUser.getCurrentUser().getObjectId())) {

             if(TextUtils.equals(task.getStatus(),"open")){
                 btnAction.setText(getResources().getString(R.string.accept));
                 btnAction.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         listener.launchAcceptCandidateDialog(task);
                         dismiss();
                     }
                 });
             } else if (TextUtils.equals(task.getStatus(),"accepted")){
                 btnAction.setText(getResources().getString(R.string.complete));
                 btnAction.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         listener.launchCompleteTaskDialogFragment(task);
                         dismiss();
                     }
                 });
             }

             ImageButton ibEditTask = (ImageButton) view.findViewById(R.id.ibEditTask);

             ibEditTask.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     listener.launchEditTaskFragment(task);
                     dismiss();
                 }
             });
            } else {
             btnAction.setText(getResources().getString(R.string.contact));
             btnAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.launchContactOwnerDialog(task);
                        dismiss();
                    }
                });
            }

            final String userId = postedBy.getObjectId();
            ivTaskDetailOwnerProf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.launchProfileFragment(userId);

                    // Close the dialog and return back to the parent
                    dismiss();
                }
            });
            String imageUrl = postedBy.getString("profilePicUrl");
            if (imageUrl != null){
                Picasso.with(getContext()).load(imageUrl).
                        transform(new CropCircleTransformation()).into(ivTaskDetailOwnerProf);
            }
    }
}

