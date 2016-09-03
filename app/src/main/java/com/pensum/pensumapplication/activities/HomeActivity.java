package com.pensum.pensumapplication.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseUser;
import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.fragments.AcceptTaskDialogFragment;
import com.pensum.pensumapplication.fragments.AddTaskFragment;
import com.pensum.pensumapplication.fragments.ChatFragment;
import com.pensum.pensumapplication.fragments.CompleteTaskDialogFragment;
import com.pensum.pensumapplication.fragments.ContactOwnerFragment;
import com.pensum.pensumapplication.fragments.ConversationFragment;
import com.pensum.pensumapplication.fragments.GridFragment;
import com.pensum.pensumapplication.fragments.HomeFragment;
import com.pensum.pensumapplication.fragments.MapFragment;
import com.pensum.pensumapplication.fragments.MessagesFragment;
import com.pensum.pensumapplication.fragments.MyBidsOnTasksFragment;
import com.pensum.pensumapplication.fragments.MyPostedTasksFragment;
import com.pensum.pensumapplication.fragments.MyPostedTasksFragmentV2;
import com.pensum.pensumapplication.fragments.ProfileFragment;
import com.pensum.pensumapplication.fragments.TaskDetailFragment;
import com.pensum.pensumapplication.helpers.KeyboardHelper;
import com.pensum.pensumapplication.models.Conversation;
import com.pensum.pensumapplication.models.Task;

public class HomeActivity extends AppCompatActivity implements AddTaskFragment.OnTaskSavedListener,
        HomeFragment.OnAddTaskListener, TaskDetailFragment.OnTaskDetailActionListener,
        MessagesFragment.OnConversationClickedListener, MapFragment.InfoWindowListener,
        GridFragment.TaskDetailListener, ConversationFragment.ConversationListener {
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
        disableNavigationViewScrollbars(nvDrawer);
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

        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        // Update your UI here.
                        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.flContent);
                        String name = fragment.getClass().toString();

                        //TODO Temp implementation, don't know if there is a better way to handle this
                        // The single selection for submenu doesn't seem to work
                        // The Group of the submenu can't be selected,
                        // so the selection will be wrong when jump back from sub item to a main item
                        if (fragment.getClass() == HomeFragment.class) {
                            nvDrawer.getMenu().getItem(0).setChecked(true);
                            nvDrawer.getMenu().getItem(1).setChecked(false);
                            nvDrawer.getMenu().getItem(2).setChecked(false);
                            nvDrawer.getMenu().getItem(3).setChecked(false);
                            nvDrawer.getMenu().getItem(4).setChecked(false);
                            setTitle(R.string.home);
                        } else if (fragment.getClass() == ProfileFragment.class) {
                            nvDrawer.getMenu().getItem(1).setChecked(true);
                            nvDrawer.getMenu().getItem(0).setChecked(false);
                            nvDrawer.getMenu().getItem(2).setChecked(false);
                            nvDrawer.getMenu().getItem(3).setChecked(false);
                            nvDrawer.getMenu().getItem(4).setChecked(false);
                            setTitle(R.string.profile);
                        } else if (fragment.getClass() == MyPostedTasksFragment.class) {
                            nvDrawer.getMenu().getItem(2).setChecked(true);
                            nvDrawer.getMenu().getItem(0).setChecked(false);
                            nvDrawer.getMenu().getItem(1).setChecked(false);
                            nvDrawer.getMenu().getItem(3).setChecked(false);
                            nvDrawer.getMenu().getItem(4).setChecked(false);
                            setTitle(R.string.posted);
                        }  else if (fragment.getClass() == MyBidsOnTasksFragment.class) {
                            nvDrawer.getMenu().getItem(3).setChecked(true);
                            nvDrawer.getMenu().getItem(0).setChecked(false);
                            nvDrawer.getMenu().getItem(1).setChecked(false);
                            nvDrawer.getMenu().getItem(2).setChecked(false);
                            nvDrawer.getMenu().getItem(4).setChecked(false);
                            setTitle(R.string.bidding);
                        } else if (fragment.getClass() == MessagesFragment.class) {
                            nvDrawer.getMenu().getItem(4).setChecked(true);
                            nvDrawer.getMenu().getItem(0).setChecked(false);
                            nvDrawer.getMenu().getItem(1).setChecked(false);
                            nvDrawer.getMenu().getItem(2).setChecked(false);
                            nvDrawer.getMenu().getItem(3).setChecked(false);
                            setTitle(R.string.message);
                        }
                    }
                }
        );
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

    private void disableNavigationViewScrollbars(NavigationView navigationView) {
        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                //Disable scrollbar
                navigationMenuView.setVerticalScrollBarEnabled(false);
                //Disable scrolling shadow
                navigationMenuView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass = null;
        String name = null;
        switch (menuItem.getItemId()) {
            case R.id.home:
                fragmentClass = HomeFragment.class;
                name = "home fragment";
                break;

            case R.id.profile:
                fragmentClass = ProfileFragment.class;
                name = "profile fragment";
                break;

            case R.id.my_posted_tasks:
                fragmentClass = MyPostedTasksFragment.class;
                name = "my posted tasks fragment";
                break;

            case R.id.my_posted_tasks_v2:
                fragmentClass = MyPostedTasksFragmentV2.class;
                name = "my posted tasks fragment v2";
                break;

            case R.id.my_bids_on_tasks:
                fragmentClass = MyBidsOnTasksFragment.class;
                name = "my bids on tasks fragment";
                break;

            case R.id.messages:
                fragmentClass = MessagesFragment.class;
                name = "messages fragment";
                break;

            case R.id.logOut:
                logOutUser();
                break;

            default:
                fragmentClass = HomeFragment.class;
                name = "home fragment";
        }

        if (fragmentClass != null) {
            if (fragmentClass == ProfileFragment.class) {
                fragment = ProfileFragment.newInstance(null);
            } else {
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addToBackStack(name);
            fragmentTransaction.replace(R.id.flContent, fragment).commit();

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
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
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
        ProfileFragment profileFragment = ProfileFragment.newInstance(userId);

        // Check that the device is running lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.flContent);
            if (fragment instanceof TaskDetailFragment) {
                Log.d("TaskD","Test transaction");
                TaskDetailFragment taskDetailFragment = (TaskDetailFragment)fragment;

                // Inflate transitions to apply
                Transition changeTransform = TransitionInflater.from(this).
                        inflateTransition(R.transition.change_image_transform);
                Transition explodeTransform = TransitionInflater.from(this).
                        inflateTransition(android.R.transition.explode);

                // Setup exit transition on first fragment
                taskDetailFragment.setSharedElementReturnTransition(changeTransform);
                taskDetailFragment.setExitTransition(explodeTransform);

                // Setup enter transition on second fragment
                profileFragment.setSharedElementEnterTransition(changeTransform);
                profileFragment.setEnterTransition(explodeTransform);

                // Find the shared element (in Fragment A)
                ImageView ivTaskDetailOwnerProf = (ImageView) findViewById(R.id.ivTaskDetailOwnerProf);

                // Add second fragment by replacing first
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent, profileFragment)
                        .addToBackStack("transaction profile fragment")
                        .addSharedElement(ivTaskDetailOwnerProf, "profilePic");

                // Apply the transaction
                ft.commit();
            }
        }
        else {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.addToBackStack("profile fragment");
            ft.replace(R.id.flContent, profileFragment).commit();
        }
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

    public void launchCompleteTaskDialogFragment(Task task) {
        FragmentManager fm = getSupportFragmentManager();
        CompleteTaskDialogFragment completeTaskDialogFragment = CompleteTaskDialogFragment.newInstance(task.getObjectId());
        completeTaskDialogFragment.show(fm, "fragment_complete_task");
    }

    @Override
    public void infoWindowClicked(Task task) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        TaskDetailFragment taskDetailFragment =
                TaskDetailFragment.newInstance(task.getObjectId());
        ft.replace(R.id.flContent, taskDetailFragment);
        ft.addToBackStack("task detail");
        ft.commit();
    }

    @Override
    public void showDetailFragment(Task task, Conversation conversation) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        TaskDetailFragment taskDetailFragment =
                TaskDetailFragment.newInstance(task.getObjectId());
        ft.replace(R.id.flContent, taskDetailFragment);
        ft.addToBackStack("task detail");
        ft.commit();
    }

}
