package com.shhb.supermoon.panda.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shhb.supermoon.panda.R;
import com.shhb.supermoon.panda.view.CustomViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by superMoon on 2017/7/31.
 */

public abstract class BaseNavPagerFragment extends BaseFragment {
    private CustomViewPager viewPager;
    private TabLayout tabLayout;
    private Adapter mAdapter;
    private String[] titles;

    protected abstract String[] getTitles();

    protected abstract Fragment getFragment(int position);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new Adapter(getChildFragmentManager());
        titles = getTitles();
        for (int i = 0; i < titles.length; i++) {
            mAdapter.addFragment(getFragment(i), titles[i]);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment2_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(tabChangeListener);
        viewPager = (CustomViewPager) view.findViewById(R.id.viewPager);
        viewPager.setScanScroll(true);
        viewPager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private TabLayout.OnTabSelectedListener tabChangeListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            viewPager.setCurrentItem(tab.getPosition(),false);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    protected static class Adapter extends FragmentPagerAdapter {
        private List<Fragment> fragments = new ArrayList<>();
        private List<CharSequence> titles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, CharSequence title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}
