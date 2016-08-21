package com.pensum.pensumapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.models.Task;

public class TaskDetailFragment extends DialogFragment {

    private TextView tvDescriptionLabel;
    private TextView tvTitle;
    private Task task;
    private TextView tvBudget;
    private Button btnContact;
    private OnContactOwnerListener listener;

    public TaskDetailFragment() {

    }

    public interface OnContactOwnerListener {
        public void launchContactOwnerDialog(Task task);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof  OnContactOwnerListener) {
            listener = (OnContactOwnerListener) context;
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
        return inflater.inflate(R.layout.fragment_task_detail, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvDescriptionLabel = (TextView) view.findViewById(R.id.tvDescription);
        tvBudget = (TextView) view.findViewById(R.id.tvBudget);
        btnContact = (Button) view.findViewById(R.id.btnContact);
        fetchSelectedTaskAndPopulateView();
    }

    private void fetchSelectedTaskAndPopulateView() {
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
                    populateViews();
                }
            }
        });
    }

    private void populateViews() {
        tvDescriptionLabel.setText(task.getDescription());
        tvTitle.setText(task.getTitle());
        tvBudget.setText("$" + task.getBudget().toString());
        // TODO create button programattically vs in the xml, right now it flashes in and out
        try {
            ParseUser postedBy = task.getPostedBy().fetchIfNeeded();
            if (TextUtils.equals(postedBy.getObjectId(),ParseUser.getCurrentUser().getObjectId())) {
                btnContact.setVisibility(View.INVISIBLE);
            } else {
                btnContact.setVisibility(View.VISIBLE);
                btnContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.launchContactOwnerDialog(task);
                    }
                });
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
