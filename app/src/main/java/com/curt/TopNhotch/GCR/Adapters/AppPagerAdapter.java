package com.curt.TopNhotch.GCR.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class AppPagerAdapter extends FragmentPagerAdapter {

    private Fragment[] fragments;
    private String[] titles;

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    public AppPagerAdapter(FragmentManager manager, Fragment[] fragments, String[] titles){
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
