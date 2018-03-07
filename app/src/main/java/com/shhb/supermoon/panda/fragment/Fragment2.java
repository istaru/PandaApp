package com.shhb.supermoon.panda.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shhb.supermoon.panda.R;

/**
 * Created by superMoon on 2017/7/31.
 */

public class Fragment2 extends BaseNavPagerFragment{

    public static Fragment2 newInstance() {
        Fragment2 fragment = new Fragment2();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment2_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected String[] getTitles() {
        return new String[]{"极速贷", "小额贷"};
    }

    @Override
    protected Fragment getFragment(int position) {
        if(0 == position){
            return RapidlyFragment.newInstance(position);
        } else {
            return SmallFragment.newInstance(position);
        }
    }
}
