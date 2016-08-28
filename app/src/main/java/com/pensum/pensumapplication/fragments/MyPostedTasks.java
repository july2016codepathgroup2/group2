package com.pensum.pensumapplication.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pensum.pensumapplication.models.Conversation;
import com.pensum.pensumapplication.fragments.profile.SimpleItemTouchHelperCallback;
import com.pensum.pensumapplication.models.Task;

import java.util.List;

/**
 * Created by eddietseng on 8/19/16.
 */
public class MyPostedTasks extends GridFragment {
//
//    public static MyPostedTasks newInstance(int page) {
//        return (MyPostedTasks) GridFragment.newInstance(page);
//    }
//    public static MyPostedTasks newInstance() {
//        return (MyPostedTasks) GridFragment.newInstance(0);
//    }

    private ItemTouchHelper mItemTouchHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        //Add swipe ability
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvTasks);

        return view;
    }

    public void populateTasks() {
        ParseQuery<Task> query = ParseQuery.getQuery(Task.class);
        query.whereEqualTo("posted_by", ParseUser.getCurrentUser());
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

    public void showDetailFragment(Task task, Conversation conversation) {
        FragmentManager fm = getFragmentManager();
        TaskDetailFragment taskDetailFragment =
                TaskDetailFragment.newInstance(task.getObjectId());
        taskDetailFragment.show(fm, "fragment_task_detail");
    }

    @Override
    public void onSwipeDelete(String id) {
        // Delete Task in Parse
        ParseQuery<Task> query = ParseQuery.getQuery(Task.class);
        query.getInBackground(id, new GetCallback<Task>() {
            @Override
            public void done(Task object, ParseException e) {
                if (e == null)
                    object.deleteInBackground();
                else
                    Toast.makeText(getContext(),"Delete failed",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
