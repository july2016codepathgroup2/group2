package com.pensum.pensumapplication.fragments;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.pensum.pensumapplication.models.Task;

import java.util.List;

/**
 * Created by violetaria on 8/21/16.
 */
public class TasksGridFragment extends GridFragment {

    public static TasksGridFragment newInstance(int page) {
        return (TasksGridFragment) GridFragment.newInstance(page);
    }

    public void populateTasks() {
        // Construct query to execute
        ParseQuery<Task> query = ParseQuery.getQuery(Task.class);
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
                    adapter.notifyItemRangeInserted(0, tasksFromQuery.size()-1);
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });
    }

}
