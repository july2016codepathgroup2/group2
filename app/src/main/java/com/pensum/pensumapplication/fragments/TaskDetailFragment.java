package com.pensum.pensumapplication.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.models.Task;

public class TaskDetailFragment extends DialogFragment {

    private TextView tvDescriptionLabel;
    private Task task;

    public TaskDetailFragment() {

    }

    public static TaskDetailFragment newInstance(String title, String taskId) {
        TaskDetailFragment fragment = new TaskDetailFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("task_id", taskId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_detail, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvDescriptionLabel = (TextView) view.findViewById(R.id.tvDescription);
        String title = getArguments().getString("title");
        fetchSelectedTask();
        getDialog().setTitle(title);
        tvDescriptionLabel.setText(task.getDescription());
    }

    private void fetchSelectedTask() {
        String taskId =  getArguments().getString("task_id");
        ParseQuery<Task> query = ParseQuery.getQuery(Task.class);
        // First try to find from the cache and only then go to network
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK); // or CACHE_ONLY
        // Execute the query to find the object with ID
        query.getInBackground(taskId, new GetCallback<Task>() {
            public void done(Task item, ParseException e) {
                if (e == null) {
                    // item was found
                    task = item;
                }
            }
        });
    }
}
