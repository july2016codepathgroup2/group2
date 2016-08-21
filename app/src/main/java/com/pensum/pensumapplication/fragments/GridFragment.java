package com.pensum.pensumapplication.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.adapters.TasksAdapter;
import com.pensum.pensumapplication.models.Task;

import java.util.ArrayList;
import java.util.Arrays;
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
        setHasOptionsMenu(true);
        page = getArguments().getInt(ARG_PAGE);
        tasks = new ArrayList<>();
        adapter = new TasksAdapter(tasks, getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frame_tasks, container, false);

        adapter.setOnItemClickListener(new TasksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                showTaskDetailFragment(tasks.get(position));
            }
        });

        rvTasks = (RecyclerView) view.findViewById(R.id.rvTasks);
        rvTasks.setAdapter(adapter);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvTasks.setLayoutManager(gridLayoutManager);

        populateTasks();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem  = menu.findItem(R.id.miSearch);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ParseQuery<Task> titleQuery = ParseQuery.getQuery(Task.class).whereContains("title", query);
                ParseQuery<Task> descriptionQuery = ParseQuery.getQuery(Task.class).whereContains("description", query);
                ParseQuery<Task> typeQuery = ParseQuery.getQuery(Task.class).whereContains("type", query);
                ParseQuery<Task> postedByQuery = ParseQuery.getQuery(Task.class).whereContains("posted_by", query);

                ParseQuery<Task> mainQuery = ParseQuery.or(Arrays.asList(titleQuery, descriptionQuery,
                        typeQuery, postedByQuery));

                mainQuery.findInBackground(new FindCallback<Task>() {
                    @Override
                    public void done(List<Task> items, ParseException e) {
                        if (e == null) {
                            // Access the array of results here
                            int previousContentSize = tasks.size();
                            tasks.clear();
                            adapter.notifyItemRangeRemoved(0, previousContentSize);

                            tasks.addAll(items);
                            adapter.notifyItemRangeRemoved(0, items.size());
                        } else {
                            Log.d("item", "Error: " + e.getMessage());
                        }
                    }
                });

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
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

    private void showTaskDetailFragment(Task task) {
        FragmentManager fm = getFragmentManager();
        TaskDetailFragment taskDetailFragment =
                TaskDetailFragment.newInstance(task.getObjectId());
        taskDetailFragment.show(fm, "fragment_task_detail");
    }
}
