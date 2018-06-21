package com.curt.TopNhotch.GCR.Utilities.UIUtils;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

public class TabCreator {
    
    private ViewPager pager;
    TabLayout tabLayout;
    private TabPagerAdapter tabPagerAdapter;
    private FragmentManager fragmentManager;
    private ArrayList<Tab> tabs;

    public TabCreator(ViewPager pager, TabLayout tabLayout,ArrayList<Tab> tabs,FragmentManager fragmentManager){
        this.pager = pager;
        this.tabLayout = tabLayout;
        this.fragmentManager = fragmentManager;
        this.tabs = tabs;
        this.tabPagerAdapter = new TabPagerAdapter(this.fragmentManager,this.tabs);
        this.pager.setAdapter(tabPagerAdapter);
        this.tabLayout.setupWithViewPager(pager);
        this.tabLayout.setSelectedTabIndicatorHeight(0);
        setTabLayoutTabs();
        tabLayout.getTabAt(1).select();
    }

    public void setTabTextColors(int color){
        tabLayout.setTabTextColors(color,color);
    }

    public void getTabAt(){

    }

    private void setTabLayoutTabs(){

        for(int i = 0; i < tabs.size();i++){
            tabs.get(i).setTab(tabLayout.getTabAt(i));
        }
    }


    public static class Tab<T extends Fragment>{

        private T fragment;
        private String title;
        private Drawable icon = null;
        private TabLayout.Tab tab;
        private int customView;

        public Tab(T fragment, String title, Drawable icon){
            this.fragment = fragment;
            this.title =  title;
            this.icon = icon;
        }

        public Tab(T fragment,Drawable icon){
            this.fragment = fragment;
            this.icon = icon;
        }
        public Tab(T fragment,int customView){
            this.fragment = fragment;
            this.customView = customView;
        }

        public Tab(T fragment,String title){
            this.fragment = fragment;
            this.title =  title;
        }

        public T getFragment(){
            return this.fragment;
        }

        private void setTab(TabLayout.Tab tab){
            this.tab = tab;
            if(this.icon != null){
                this.tab.setIcon(this.icon);
            }else{
                this.tab.setCustomView(customView);
            }
        }
    }

    private class TabPagerAdapter extends FragmentStatePagerAdapter{
        private ArrayList<Tab> tabs;
        @Override
        public CharSequence getPageTitle(int position){
            Tab tab = tabs.get(position);
            return tab.title;
        }

        public TabPagerAdapter(FragmentManager fm, ArrayList<Tab> tabs) {
            super(fm);
            this.tabs = tabs;
        }

        @Override
        public Fragment getItem(int position){

            return tabs.get(position).getFragment();
        }

        @Override
        public int getCount() {
            return tabs.size();
        }
    }
}
