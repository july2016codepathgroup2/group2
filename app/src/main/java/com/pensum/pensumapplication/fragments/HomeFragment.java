package com.pensum.pensumapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.adapters.DashboardPagerAdapter;

/**
 * Created by eddietseng on 8/19/16.
 */
public class HomeFragment extends Fragment {
    FloatingActionButton fab;

    private OnAddTaskListener listener;

    public HomeFragment(){

    }

    public interface OnAddTaskListener  {
        void onLaunchAddTask();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddTaskListener) {
            listener = (OnAddTaskListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement HomeFragment.OnAddTaskListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        fab = (FloatingActionButton) view.findViewById(R.id.new_task_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onLaunchAddTask();
            }
        });
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        lp.setAnchorId(View.NO_ID);
        lp.gravity = Gravity.BOTTOM | GravityCompat.END;
        fab.setLayoutParams(lp);

        viewPager.setAdapter(new DashboardPagerAdapter(getChildFragmentManager(), getActivity()));

        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

}