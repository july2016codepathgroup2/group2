package com.pensum.pensumapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
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

    @BindView(R.id.tvDescription)TextView tvDescriptionLabel;
    @BindView(R.id.tvTitle)TextView tvTitle;
    @BindView(R.id.tvBudget)TextView tvBudget;
    @BindView(R.id.btnContact)Button btnContact;
    @BindView(R.id.ivTaskDetailOwnerProf)ImageView ivTaskDetailOwnerProf;
    @BindView(R.id.rvTaskImages)RecyclerView rvTaskImages;
    @BindView(R.id.ibEditTask)ImageButton ibEditTask;

    private Task task;
    private OnTaskDetailActionListener listener;
    private Unbinder unbinder;

    public TaskDetailFragment() {

    }

    public interface OnTaskDetailActionListener {
        void launchContactOwnerDialog(Task task);
        void launchProfileFragment(String userId);
        void launchEditTaskFragment(Task task);
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
        View view = inflater.inflate(R.layout.fragment_task_detail, container);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchSelectedTaskAndPopulateView();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void fetchSelectedTaskAndPopulateView() {
        String taskId =  getArguments().getString("task_id");
        ParseQuery<Task> query = ParseQuery.getQuery(Task.class);
        //TODO The update data will not show
        // First try to find from the cache and only then go to network
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK); // or CACHE_ONLY
        // Execute the query to find the object with ID
        query.getInBackground(taskId, new GetCallback<Task>() {
            public void done(Task item, ParseException e) {
                if (e == null) {
                    // item was found
                    task = item;
                    populateViews();
                }
            }
        });
    }

    private void populateViews() {
        tvDescriptionLabel.setText(task.getDescription());
        tvTitle.setText(task.getTitle());
        tvBudget.setText(NumberFormat.getCurrencyInstance().format(task.getBudget()));
        if(task.getImages() == null || task.getImages().length() < 1)
            rvTaskImages.setVisibility(View.GONE);

        // TODO create button programattically vs in the xml, right now it flashes in and out
        try {
            ParseUser postedBy = task.getPostedBy().fetchIfNeeded();
            if (TextUtils.equals(postedBy.getObjectId(),ParseUser.getCurrentUser().getObjectId())) {
                btnContact.setVisibility(View.INVISIBLE);
                ibEditTask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.launchEditTaskFragment(task);
                        dismiss();
                    }
                });
            } else {
                final String userId = postedBy.getObjectId();

                ibEditTask.setVisibility(View.INVISIBLE);
                btnContact.setVisibility(View.VISIBLE);
                btnContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.launchContactOwnerDialog(task);
                    }
                });

                ivTaskDetailOwnerProf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.launchProfileFragment(userId);

                        // Close the dialog and return back to the parent
                        dismiss();
                    }
                });
            }

            String imageUrl = postedBy.getString("profilePicUrl");
            if (imageUrl != null){
                Picasso.with(getContext()).load(imageUrl).
                        transform(new CropCircleTransformation()).into(ivTaskDetailOwnerProf);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
