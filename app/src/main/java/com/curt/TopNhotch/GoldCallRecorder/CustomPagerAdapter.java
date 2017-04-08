package com.curt.TopNhotch.GoldCallRecorder;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Kurt on 6/8/2016.
 */
public class CustomPagerAdapter extends FragmentPagerAdapter {

    private Fragment[] fragments;
    private String[] titles;
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    public CustomPagerAdapter(FragmentManager manager,Fragment[] fragments,String[] titles){
        super(manager);
        this.fragments = fragments;
        this.titles = titles;
    }
    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    public Fragment getFragment(int position){
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
