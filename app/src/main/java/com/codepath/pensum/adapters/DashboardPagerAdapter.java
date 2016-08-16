package com.codepath.pensum.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.pensum.fragments.TasksFragment;

/**
 * Created by violetaria on 8/16/16.
 */
public class DashboardPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String [] { "Grid", "Map" };
    private Context context;

    public DashboardPagerAdapter(FragmentManager fm, Context context){
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        // don't have to use the same fragment type here,
        //  a grid fragment for position 0
        //  have a map fragment for position 1 based on what sent in
        return TasksFragment.newInstance(position + 1);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
