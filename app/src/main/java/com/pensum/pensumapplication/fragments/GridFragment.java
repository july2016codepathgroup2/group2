package com.pensum.pensumapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.activities.HomeActivity;
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
        populateTasks();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((HomeActivity) getActivity()).updateTitle();
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
                // if task owner and accepted, find the convo for the winning candidate
                // if task owner and not accepted, doesn't matter
                // if not tas owner, find my candidate conversation
                ParseQuery<Conversation> query = ParseQuery.getQuery("Conversation");
                if (TextUtils.equals(clickedTask.getPostedBy().getObjectId(),ParseUser.getCurrentUser().getObjectId())) {
                    if(clickedTask.getStatus().equals("accepted")){
                        query.whereEqualTo("status","accepted");
                    } else {
                        query.whereEqualTo("status","bidding");
                    }
                } else {
                    query.whereEqualTo("candidate", ParseUser.getCurrentUser());
                }
                query.whereEqualTo("task", clickedTask);
//                query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
                Log.d("DEBUG","click task id: " + clickedTask.getObjectId());
                query.findInBackground(new FindCallback<Conversation>() {
                    @Override
                    public void done(List<Conversation> conversations, ParseException e) {
                        if(e == null){
                            Conversation conversation = null;
                            if (conversations.size() > 0) {
                                conversation = conversations.get(0);
                                Log.d("DEBUG","conversation " +  conversation.getObjectId());
                            }
                            showDetailFragment(clickedTask, conversation);
                        } else {
                            Log.e("message", "Error Loading Messages" + e);
                    }
                    }
                });
            }
        });

        rvTasks = (RecyclerView) view.findViewById(R.id.rvTasks);
        rvTasks.setAdapter(adapter);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvTasks.setLayoutManager(gridLayoutManager);

        return view;
    }

    public void setMyLocation(){
        // TODO handle this here
    }


    abstract void populateTasks();

    abstract void showDetailFragment(Task task, Conversation conversation);

    public void addAll(List<Task> tasksToAdd) {
        int previousContentSize = tasks.size();
        tasks.clear();
        adapter.notifyItemRangeRemoved(0, previousContentSize);
        tasks.addAll(tasksToAdd);
        adapter.notifyItemRangeInserted(0, tasksToAdd.size());
    }

    public void add(Task task) {
        tasks.add(task);
        adapter.notifyItemInserted(tasks.size() - 1);
    }

    @Override
    public abstract void onSwipeDelete(String id);
}
