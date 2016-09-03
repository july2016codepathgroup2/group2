package com.pensum.pensumapplication.fragments;

import android.os.Bundle;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pensum.pensumapplication.models.Conversation;
import com.pensum.pensumapplication.models.Task;

import java.util.List;

/**
 * Created by violetaria on 9/3/16.
 */
public class MyPostedTasksFilteredFragment extends GridFragment {

    private String statusFilter;

    public MyPostedTasksFilteredFragment() {
    }


    public static MyPostedTasksFilteredFragment newInstance(String statusFilter) {
        MyPostedTasksFilteredFragment fragment = new MyPostedTasksFilteredFragment();
        Bundle args = new Bundle();
        args.putString("status_filter", statusFilter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        statusFilter = getArguments().getString("status_filter");
        super.onCreate(savedInstanceState);
    }

    public void populateTasks() {
        ParseQuery<Task> query = ParseQuery.getQuery(Task.class);
        query.whereEqualTo("posted_by", ParseUser.getCurrentUser());
        query.whereEqualTo("status", statusFilter);
        query.setLimit(MAX_TASKS_TO_SHOW);
        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<Task>() {
            public void done(List<Task> tasksFromQuery, ParseException e) {
                if (e == null) {
                    addAll(tasksFromQuery);
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });
    }

    public void showDetailFragment(Task task, Conversation conversation) {
        listener.showDetailFragment(task, conversation);
    }

    @Override
    public void onSwipeDelete(String id) {
        // Not applicable
    }
}
