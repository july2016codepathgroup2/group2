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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.models.Task;

import java.text.NumberFormat;

public class TaskDetailFragment extends DialogFragment {

    private TextView tvDescriptionLabel;
    private TextView tvTitle;
    private Task task;
    private TextView tvBudget;
//    private Button btnContact;
    private OnContactOwnerListener listener;
    private TextView tvStatus;
    private RelativeLayout rlTaskDetail;

    public TaskDetailFragment() {

    }

    public interface OnContactOwnerListener {
        public void launchContactOwnerDialog(Task task);
        public void launchAcceptCandidateDialog(Task task);
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
//        btnContact = (Button) view.findViewById(R.id.btnContact);
        tvStatus = (TextView) view.findViewById(R.id.tvStatus);
        rlTaskDetail = (RelativeLayout) view.findViewById(R.id.rlTaskDetail);

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
        tvBudget.setText(NumberFormat.getCurrencyInstance().format(task.getBudget()));
        tvStatus.setText(task.getStatus());
        try {
            ParseUser postedBy = task.getPostedBy().fetchIfNeeded();
            Button button = new Button(getContext());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            button.setLayoutParams(params); //causes layout update
            if (TextUtils.equals(postedBy.getObjectId(),ParseUser.getCurrentUser().getObjectId())) {
                button.setText(getResources().getString(R.string.accept));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.launchAcceptCandidateDialog(task);
                    }
                });
            } else {
                button.setText(getResources().getString(R.string.contact));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.launchContactOwnerDialog(task);
                    }
                });
            }
            rlTaskDetail.addView(button);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
