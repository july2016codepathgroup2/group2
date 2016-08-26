package com.pensum.pensumapplication.fragments;

import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pensum.pensumapplication.models.Task;

import java.util.List;

/**
 * Created by violetaria on 8/24/16.
 */
public class MyCompletedTasks extends GridFragment{
    public void populateTasks() {
        ParseQuery<Task> query = ParseQuery.getQuery(Task.class);
        query.whereEqualTo("candidate", ParseUser.getCurrentUser());
        query.whereEqualTo("status","completed");
        query.setLimit(MAX_TASKS_TO_SHOW);
        query.orderByDescending("createdAt");

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

    public void showDetailFragment(Task task) {
        FragmentManager fm = getFragmentManager();
        TaskDetailFragment taskDetailFragment =
                TaskDetailFragment.newInstance(task.getObjectId());
        taskDetailFragment.show(fm, "fragment_task_detail");
    }
}
