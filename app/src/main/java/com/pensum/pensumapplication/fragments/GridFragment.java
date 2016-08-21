package com.pensum.pensumapplication.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.adapters.TasksAdapter;
import com.pensum.pensumapplication.models.Task;

import java.util.ArrayList;

/**
 * Created by violetaria on 8/16/16.
 */
public abstract class GridFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    public final int MAX_TASKS_TO_SHOW = 50;

    private int page;
    public ArrayList<Task> tasks;
    private RecyclerView rvTasks;
    public TasksAdapter adapter;

    public static GridFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        GridFragment fragment = new TasksGridFragment();
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

    public void setMyLocation(){
        // TODO handle this here
    }

    abstract void populateTasks();

    private void showTaskDetailFragment(Task task) {
        FragmentManager fm = getFragmentManager();
        TaskDetailFragment taskDetailFragment =
                TaskDetailFragment.newInstance(task.getObjectId());
        taskDetailFragment.show(fm, "fragment_task_detail");
    }
}
