package com.pensum.pensumapplication.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pensum.pensumapplication.R;
import com.pensum.pensumapplication.adapters.DashboardPagerAdapter;

/**
 * Created by eddietseng on 8/19/16.
 */
public class HomeFragment extends Fragment {
    public static String POSITION = "POSITION";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_dashboard, container, false);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        viewPager.setAdapter(new DashboardPagerAdapter(getChildFragmentManager(), getActivity()));

        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

}