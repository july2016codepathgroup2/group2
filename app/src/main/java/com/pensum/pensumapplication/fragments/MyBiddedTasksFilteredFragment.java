package com.pensum.pensumapplication.fragments;

import android.os.Bundle;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pensum.pensumapplication.models.Conversation;
import com.pensum.pensumapplication.models.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by violetaria on 9/3/16.
 */
public class MyBiddedTasksFilteredFragment extends GridFragment {

    private String statusFilter;

    public MyBiddedTasksFilteredFragment() {
    }

    public static MyBiddedTasksFilteredFragment newInstance(String statusFilter) {
        MyBiddedTasksFilteredFragment fragment = new MyBiddedTasksFilteredFragment();
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
        ParseQuery<Conversation> query = ParseQuery.getQuery("Conversation");
        query.whereEqualTo("candidate", ParseUser.getCurrentUser());
        query.whereEqualTo("status",statusFilter);
        query.include("task");
        query.include("posted_by");
        query.include("candidate");
        query.setLimit(MAX_TASKS_TO_SHOW);
        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<Conversation>() {
            public void done(List<Conversation> conversationsFromQuery, ParseException e) {
                if (e == null) {
                    ArrayList<Task> tasks = new ArrayList<Task>();
                    for(Conversation c: conversationsFromQuery){
                        tasks.add(c.getTask());
                    }
                    addAll(tasks);
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
