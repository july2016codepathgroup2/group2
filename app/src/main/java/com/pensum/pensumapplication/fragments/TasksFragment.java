package com.pensum.pensumapplication.fragments;

import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.fragments.FilterSearchDialogFragment.FilterSearchDialogListener;
import com.pensum.pensumapplication.models.Task;

import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends GridFragment implements FilterSearchDialogListener {
    private String typeFilter;
    private ParseGeoPoint locationFilter;
    private Double budgetFilter;

    @Override
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
                    adapter.notifyItemRangeInserted(0, tasksFromQuery.size());
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });
    }

    // This is called when the filter search dialog is completed and the results have been passed
    @Override
    public void onFinishFilterSearchDialog(String type, ParseGeoPoint location, double budget) {
        typeFilter = type;
        locationFilter = location;
        budgetFilter = budget;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        setupSearchMenuItem(menu);
        setupFilterSearchMenuItem(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setupSearchMenuItem(Menu menu) {
        MenuItem searchItem  = menu.findItem(R.id.miSearch);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // Quick fix for case-insensitive search, won't scale
                ParseQuery<Task> titleQuery = ParseQuery.getQuery(Task.class).whereMatches("title", "("+query+")", "i");
                ParseQuery<Task> descriptionQuery = ParseQuery.getQuery(Task.class).whereMatches("description", "("+query+")", "i");
                ParseQuery<Task> typeQuery = ParseQuery.getQuery(Task.class).whereMatches("type", "("+query+")", "i");
                ParseQuery<Task> postedByQuery = ParseQuery.getQuery(Task.class).whereMatches("posted_by", "("+query+")", "i");

                List<ParseQuery<Task>> queries = new ArrayList<>();
                queries.add(titleQuery);
                queries.add(descriptionQuery);
                queries.add(typeQuery);
                queries.add(postedByQuery);

                if (typeFilter != null && !typeFilter.isEmpty()) {
                    queries.add(ParseQuery.getQuery(Task.class).whereMatches("type", "("+query+")", "i"));
                }
                if (locationFilter != null) {
                    ParseQuery<Task> locationQuery = ParseQuery.getQuery(Task.class).whereNear("location", locationFilter);
                    queries.add(locationQuery);
                }
                if (budgetFilter > -1) {
                    queries.add(ParseQuery.getQuery(Task.class).whereGreaterThanOrEqualTo("budget", budgetFilter));
                }

                ParseQuery<Task> mainQuery = ParseQuery.or(queries);
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
    }

    private void setupFilterSearchMenuItem(Menu menu) {
        MenuItem filterItem = menu.findItem(R.id.miFilterSearch);
        filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                FragmentManager fm = getFragmentManager();
                FilterSearchDialogFragment filterSearchDialogFragment = FilterSearchDialogFragment.newInstance();
                filterSearchDialogFragment.setTargetFragment(TasksFragment.this, 300);
                filterSearchDialogFragment.show(fm, "fragment_Filter_Search");
                return true;
            }
        });
    }

}
