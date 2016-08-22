package com.pensum.pensumapplication.fragments;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pensum.pensumapplication.models.Task;

import java.util.List;

/**
 * Created by eddietseng on 8/19/16.
 */
public class MyTasksFragment extends GridFragment {
//
//    public static MyTasksFragment newInstance(int page) {
//        return (MyTasksFragment) GridFragment.newInstance(page);
//    }
//    public static MyTasksFragment newInstance() {
//        return (MyTasksFragment) GridFragment.newInstance(0);
//    }

    public void populateTasks() {
        // Construct query to execute
        ParseQuery<Task> query = ParseQuery.getQuery(Task.class);
        query.whereEqualTo("posted_by", ParseUser.getCurrentUser());
        // Configure limit and sort order
        query.setLimit(MAX_TASKS_TO_SHOW);
        query.orderByDescending("createdAt");
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL

        query.findInBackground(new FindCallback<Task>() {
            public void done(List<Task> tasksFromQuery, ParseException e) {
                if (e == null) {
                    int previousContentSize = tasks.size();
                    tasks.clear();
                    adapter.notifyItemRangeRemoved(0, previousContentSize);
                    tasks.addAll(tasksFromQuery);
                    adapter.notifyItemRangeInserted(0, tasksFromQuery.size());
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });
    }
}
