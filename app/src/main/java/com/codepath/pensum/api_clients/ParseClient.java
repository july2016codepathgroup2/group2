package com.codepath.pensum.api_clients;

import android.util.Log;

import com.codepath.pensum.models.Task;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by violetaria on 8/17/16.
 */
public class ParseClient {
    static final int MAX_TASKS_TO_SHOW = 50;
    List<Task> tasks;

    public List<Task> getTasks(){
        ParseQuery<Task> query = ParseQuery.getQuery(Task.class);
        query.setLimit(MAX_TASKS_TO_SHOW);
        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<Task>() {
            public void done(List<Task> newTasks, ParseException e) {
                if (e == null) {
                    tasks = newTasks;
                } else {
                    Log.e("message", "Error Loading Tasks" + e);
                }
            }
        });

        return tasks;
    }
}