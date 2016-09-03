package com.pensum.pensumapplication.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.pensum.pensumapplication.fragments.MyBiddedTasksFilteredFragment;

/**
 * Created by violetaria on 9/3/16.
 */
public class BiddedTasksPagerAdapter extends SmartFragmentStatePagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String [] { "Bidding", "Won", "Declined" };
    private Context context;

    public BiddedTasksPagerAdapter(FragmentManager fm, Context context){
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return MyBiddedTasksFilteredFragment.newInstance("bidding");
        } else if (position == 1) {
            return MyBiddedTasksFilteredFragment.newInstance("accepted");
        } else if (position == 2) {
            return MyBiddedTasksFilteredFragment.newInstance("declined");
        }
        else {
            return null;
        }

    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}