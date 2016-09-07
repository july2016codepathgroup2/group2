package com.pensum.pensumapplication.fragments;

import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.fragments.FilterSearchDialogFragment.FilterSearchDialogListener;
import com.pensum.pensumapplication.helpers.SearchHelper;
import com.pensum.pensumapplication.models.Conversation;
import com.pensum.pensumapplication.models.Task;

import java.util.List;

public class TasksFragment extends GridFragment implements FilterSearchDialogListener {
    private String typeFilter;
    private ParseGeoPoint locationFilter;
    private double budgetFilter;
    private String zipCode;
    private SearchView searchView;

    @Override
    public void populateTasks() {
        // Construct query to execute
        ParseQuery<Task> query = ParseQuery.getQuery(Task.class);
        query.whereNotEqualTo("posted_by", ParseUser.getCurrentUser());
        query.whereEqualTo("status","open");
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
    public void onFinishFilterSearchDialog(String type, ParseGeoPoint location, double budget, String zipCode) {
        typeFilter = type;
        locationFilter = location;
        budgetFilter = budget;
        this.zipCode = zipCode;

        String query = searchView.getQuery().toString();
        FindCallback<Task> finishSearchCallback = new FindCallback<Task>() {
            @Override
            public void done(List<Task> items, ParseException e) {
                if (e == null) {
                    // Access the array of results here
                    addAll(items);
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        };
        SearchHelper.searchForTasksWith(query, typeFilter, locationFilter, budgetFilter, finishSearchCallback);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        setupSearchMenuItem(menu);
        setupFilterSearchMenuItem(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setupSearchMenuItem(Menu menu) {
        final MenuItem searchItem = menu.findItem(R.id.miSearch);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                FindCallback<Task> finishSearchCallback = new FindCallback<Task>() {
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
                };

                SearchHelper.searchForTasksWith(query, typeFilter, locationFilter,
                        budgetFilter, finishSearchCallback);
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

        ImageView searchCloseButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
        searchCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                populateTasks();
                EditText etSearch = (EditText) searchView.findViewById(R.id.search_src_text);
                // Clear the text from the EditText View
                etSearch.setText("");
                //Clear query
                searchView.setQuery("", false);
                //Collapse the action view
                searchView.onActionViewCollapsed();
                //Collapse the search widget
                searchItem.collapseActionView();
            }
        });
    }

    private void setupFilterSearchMenuItem(Menu menu) {
        MenuItem filterItem = menu.findItem(R.id.miFilterSearch);
        filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                FragmentManager fm = getFragmentManager();
                FilterSearchDialogFragment filterSearchDialogFragment =
                        FilterSearchDialogFragment.newInstance(typeFilter, budgetFilter, zipCode);
                filterSearchDialogFragment.setTargetFragment(TasksFragment.this, 300);
                filterSearchDialogFragment.show(fm, "fragment_Filter_Search");
                return true;
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
