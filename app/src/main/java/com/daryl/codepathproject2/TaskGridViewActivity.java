package com.daryl.codepathproject2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.daryl.codepathproject2.adapters.TasksAdapter;
import com.daryl.codepathproject2.models.Task;
import com.daryl.codepathproject2.models.User;

import java.util.ArrayList;

public class TaskGridViewActivity extends AppCompatActivity {

    ArrayList<Task> tasks;
    private RecyclerView rvTasks;
    private TasksAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_gridview);

        rvTasks = (RecyclerView) findViewById(R.id.rvTasks);


        tasks = new ArrayList<>();
        adapter = new TasksAdapter(tasks, this);
        rvTasks.setAdapter(adapter);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvTasks.setLayoutManager(gridLayoutManager);

        initializeFakeTasks();
    }

    private void initializeFakeTasks() {
        Task taskOne = new Task();
        taskOne.setAuthor(new User("Daryl", R.drawable.daryl_profile_picture));
        taskOne.setType("Cleaning");
        taskOne.setTitle("Clean my room");
        taskOne.setBudget(30);

        Task taskTwo = new Task();
        taskTwo.setAuthor(new User("Terri", R.drawable.terri_profile_picture));
        taskTwo.setType("Pet Care");
        taskTwo.setTitle("Watch my dog for a week");
        taskTwo.setBudget(250);

        Task taskThree = new Task();
        taskThree.setAuthor(new User("Eddie", R.drawable.eddie_profile_picture));
        taskThree.setType("Electronics");
        taskThree.setTitle("Fix my fridge");
        taskThree.setBudget(200);

        Task taskFour = new Task();
        taskFour.setAuthor(new User("Nathan", R.drawable.nathan_profile_picture));
        taskFour.setType("Pet Care");
        taskFour.setTitle("Walk my dog");
        taskFour.setBudget(20);

        Task taskFive = new Task();
        taskFive.setAuthor(new User("Tim", R.drawable.tim_profile_picture));
        taskFive.setType("Cleaning");
        taskFive.setTitle("Do my laundry");
        taskFive.setBudget(20);

        Task taskSix = new Task();
        taskSix.setAuthor(new User("Nick", R.drawable.nick_profile_picture));
        taskSix.setType("Electronics");
        taskSix.setTitle("Fix my oven");
        taskSix.setBudget(300);

        Task taskSeven = new Task();
        taskSeven.setAuthor(new User("Person 1", R.drawable.person1_profile_picture));
        taskSeven.setType("Cleaning");
        taskSeven.setTitle("Clean stuff");
        taskSeven.setBudget(20);

        Task taskEight = new Task();
        taskEight.setAuthor(new User("Person 2", R.drawable.person2_profile_picture));
        taskEight.setType("Cleaning");
        taskEight.setTitle("Do my laundry");
        taskEight.setBudget(20);

        Task taskNine = new Task();
        taskNine.setAuthor(new User("Person 3", R.drawable.person3_profile_picture));
        taskNine.setType("Cleaning");
        taskNine.setTitle("Do my laundry");
        taskNine.setBudget(20);

        Task taskTen = new Task();
        taskTen.setAuthor(new User("Person 4", R.drawable.person4_profile_picture));
        taskTen.setType("Cleaning");
        taskTen.setTitle("Do my laundry");
        taskTen.setBudget(20);

        tasks.add(taskOne);
        tasks.add(taskTwo);
        tasks.add(taskThree);
        tasks.add(taskFour);
        tasks.add(taskFive);
        tasks.add(taskSix);
        tasks.add(taskSeven);
        tasks.add(taskEight);
        tasks.add(taskNine);
        tasks.add(taskTen);
        adapter.notifyItemRangeInserted(0, 10);
    }
}
