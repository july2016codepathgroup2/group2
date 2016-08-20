package com.pensum.pensumapplication.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.adapters.TasksAdapter;
import com.pensum.pensumapplication.models.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by violetaria on 8/16/16.
 */
public class GridFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private final int MAX_TASKS_TO_SHOW = 50;

    private int page;
    private ArrayList<Task> tasks;
    private RecyclerView rvTasks;
    private TasksAdapter adapter;

    public static GridFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        GridFragment fragment = new GridFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt(ARG_PAGE);
        tasks = new ArrayList<>();
        adapter = new TasksAdapter(tasks, getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frame_tasks, container, false);
        rvTasks = (RecyclerView) view.findViewById(R.id.rvTasks);
        rvTasks.setAdapter(adapter);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvTasks.setLayoutManager(gridLayoutManager);
        populateTasks();
        return view;
    }

    public void setMyLocation(){
        // TODO handle this here
    }

    private void populateTasks() {
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
                    tasks.clear();
                    tasks.addAll(tasksFromQuery);
                    adapter.notifyItemRangeInserted(0, tasksFromQuery.size());
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });

    }
}
