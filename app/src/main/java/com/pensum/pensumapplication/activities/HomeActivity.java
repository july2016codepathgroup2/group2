package com.pensum.pensumapplication.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseUser;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.fragments.AcceptTaskDialogFragment;
import com.pensum.pensumapplication.fragments.AddTaskFragment;
import com.pensum.pensumapplication.fragments.ChatFragment;
import com.pensum.pensumapplication.fragments.CompleteTaskDialogFragment;
import com.pensum.pensumapplication.fragments.ContactOwnerFragment;
import com.pensum.pensumapplication.fragments.ConversationFragment;
import com.pensum.pensumapplication.fragments.HomeFragment;
import com.pensum.pensumapplication.fragments.MessagesFragment;
import com.pensum.pensumapplication.fragments.MyAcceptedTasks;
import com.pensum.pensumapplication.fragments.MyCompletedTasks;
import com.pensum.pensumapplication.fragments.MyPostedTasks;
import com.pensum.pensumapplication.fragments.ProfileFragment;
import com.pensum.pensumapplication.fragments.TaskDetailFragment;
import com.pensum.pensumapplication.helpers.KeyboardHelper;
import com.pensum.pensumapplication.models.Conversation;
import com.pensum.pensumapplication.models.Task;

public class HomeActivity extends AppCompatActivity implements AddTaskFragment.OnTaskSavedListener,
        HomeFragment.OnAddTaskListener, TaskDetailFragment.OnTaskDetailActionListener,
        MessagesFragment.OnConversationClickedListener {
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);
        drawerToggle = setupDrawerToggle();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

        //Initial load
        nvDrawer.getMenu().getItem(0).setChecked(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new HomeFragment()).commit();
        setTitle(R.string.home);

        pd = new ProgressDialog(this);
        pd.setTitle("Logging out...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass = null;
        switch (menuItem.getItemId()) {
            case R.id.home:
                fragmentClass = HomeFragment.class;
                break;

            case R.id.profile:
                fragmentClass = ProfileFragment.class;
                break;

            case R.id.my_posted_tasks:
                fragmentClass = MyPostedTasks.class;
                break;

            case R.id.my_accepted_tasks:
                fragmentClass = MyAcceptedTasks.class;
                break;

            case R.id.my_completed_tasks:
                fragmentClass = MyCompletedTasks.class;
                break;

            case R.id.messages:
                fragmentClass = MessagesFragment.class;
                break;

            case R.id.logOut:
                logOutUser();
                break;

            default:
                fragmentClass = HomeFragment.class;
        }

        if(fragmentClass != null) {
            if(fragmentClass==ProfileFragment.class) {
                fragment = ProfileFragment.newInstance(null);
            }
            else {
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addToBackStack("fragment");
            fragmentTransaction.replace(R.id.flContent, fragment).commit();

            // Highlight the selected item has been done by NavigationView
            menuItem.setChecked(true);
            // Set action bar title
            setTitle(menuItem.getTitle());
            // Close the navigation drawer
            mDrawer.closeDrawers();
        }
    }

    private void logOutUser() {
        pd.show();
        ParseUser.logOut();
        pd.dismiss(); //might not need this
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE! Make sure to override the method with only a single `Bundle` argument
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }


    @Override
    public void onNewTaskCreated(Task task) {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            KeyboardHelper.hideKeyboard(this);
            fm.popBackStack();
        }
        if (task != null) {

        } else {
            Toast.makeText(this, "You're offline task not saved.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNewTaskEdited(Task task) { // TODO Can be combined with onNewTaskCreated()?
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            KeyboardHelper.hideKeyboard(this);
            fm.popBackStack();
        }
        if (task != null) {

        } else {
            Toast.makeText(this, "You're offline task not saved.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLaunchAddTask() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flContent, AddTaskFragment.newInstance(null));
        ft.addToBackStack("add task fragment");
        ft.commit();
    }

    @Override
    public void launchContactOwnerDialog(Task task) {
        FragmentManager fm = getSupportFragmentManager();
        ContactOwnerFragment contactOwnerFragment = ContactOwnerFragment.newInstance(task.getObjectId());
        contactOwnerFragment.show(fm, "fragment_contact_owner");
    }

    @Override
    public void launchAcceptCandidateDialog(Task task) {
        FragmentManager fm = getSupportFragmentManager();
        AcceptTaskDialogFragment acceptTaskFragment = AcceptTaskDialogFragment.newInstance(task.getObjectId());
        acceptTaskFragment.show(fm, "fragment_accept_task");
    }

    @Override
    public void launchProfileFragment(String userId) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack("profile fragment");
        ft.replace(R.id.flContent, ProfileFragment.newInstance(userId)).commit();
    }

    @Override
    public void launchEditTaskFragment(Task task) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flContent, AddTaskFragment.newInstance(task));
        ft.addToBackStack("edit task fragment");
        ft.commit();
    }

    @Override
    public void launchConversationsFragment(Task task) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ConversationFragment conversationFragment = ConversationFragment.newInstance(task.getObjectId());
        ft.replace(R.id.flContent, conversationFragment);
        ft.addToBackStack("convo list");
        ft.commit();
    }

    @Override
    public void launchChatFragment(Conversation conversation) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ChatFragment chatFragment = ChatFragment.newInstance(conversation.getObjectId());
        ft.replace(R.id.flContent, chatFragment);
        ft.addToBackStack("chat fragment");
        ft.commit();
    }

    public void launchCompleteTaskDialogFragment(Task task){
        FragmentManager fm = getSupportFragmentManager();
        CompleteTaskDialogFragment completeTaskDialogFragment = CompleteTaskDialogFragment.newInstance(task.getObjectId());
        completeTaskDialogFragment.show(fm, "fragment_complete_task");
    }

}
