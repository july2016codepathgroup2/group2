package com.pensum.pensumapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.adapters.TasksAdapter;
import com.pensum.pensumapplication.models.Conversation;
import com.pensum.pensumapplication.models.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by violetaria on 8/16/16.
 */
public abstract class GridFragment extends Fragment
        implements TasksAdapter.SwipeDeleteListener {
    public final int MAX_TASKS_TO_SHOW = 50;

    public ArrayList<Task> tasks;
    protected RecyclerView rvTasks;
    public TasksAdapter adapter;
    public TaskDetailListener listener;

    public interface TaskDetailListener {
        void showDetailFragment(Task task, Conversation conversation);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        tasks = new ArrayList<>();
        adapter = new TasksAdapter(tasks, getContext(), this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TaskDetailListener) {
            listener = (TaskDetailListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement TaskDetailListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frame_tasks, container, false);

        adapter.setOnItemClickListener(new TasksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                //Searching for conversation
                final Task clickedTask = tasks.get(position);
                ParseQuery<Conversation> query = ParseQuery.getQuery("Conversation");
                query.whereEqualTo("task", clickedTask);
                query.whereEqualTo("owner", clickedTask.getPostedBy());
                query.whereEqualTo("candidate", ParseUser.getCurrentUser());
                query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
                query.findInBackground(new FindCallback<Conversation>() {
                    @Override
                    public void done(List<Conversation> conversations, ParseException e) {
                        Conversation conversation = null;
                        if (conversations.size() > 0) {
                            conversation = conversations.get(0);
                        }
                        showDetailFragment(clickedTask, conversation);
                    }
                });
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

    public void setMyLocation(){
        // TODO handle this here
    }

    abstract void populateTasks();

    abstract void showDetailFragment(Task task, Conversation conversation);

    @Override
    public abstract void onSwipeDelete(String id);
}
