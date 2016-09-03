package com.pensum.pensumapplication.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.fragments.profile.SimpleItemTouchHelperCallback;
import com.pensum.pensumapplication.models.Conversation;
import com.pensum.pensumapplication.models.Task;

import java.util.List;

/**
 * Created by eddietseng on 8/19/16.
 */
public class MyPostedTasksFragment extends GridFragment implements FilterPostedTasksDialogFragment.FilterPostedTasksDialogListener{
    private boolean posted;
    private boolean accepted;
    private boolean completed;

    private ItemTouchHelper mItemTouchHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        posted = true;
        accepted = true;
        completed = true;
    }

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
        if(posted){
            query.whereEqualTo("status","open");
        }
        if(accepted){
            query.whereEqualTo("status","accepted");
        }
        if(completed){
            query.whereEqualTo("status","completed");
        }
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


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.posted_tasks, menu);
        setupFilterSearchMenuItem(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    private void setupFilterSearchMenuItem(Menu menu) {
        MenuItem filterItem = menu.findItem(R.id.miFilterPostedTasks);
        filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                FragmentManager fm = getFragmentManager();
                FilterPostedTasksDialogFragment filterPostedTasksDialogFragment =
                        FilterPostedTasksDialogFragment.newInstance(posted, accepted, completed);
                filterPostedTasksDialogFragment.setTargetFragment(MyPostedTasksFragment.this, 300);
                filterPostedTasksDialogFragment.show(fm, "fragment_filter_posted_tasks");
                return true;
            }
        });
    }

    @Override
    public void onFinishFilterPostedTasksDialog(boolean posted, boolean accepted, boolean completed) {
        this.posted = posted;
        this.accepted = accepted;
        this.completed = completed;
        populateTasks();
    }
}
