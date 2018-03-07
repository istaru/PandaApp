package com.shhb.supermoon.panda.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.jaeger.library.StatusBarUtil;
import com.shhb.supermoon.panda.R;
import com.shhb.supermoon.panda.adapter.MainAdapter;
import com.shhb.supermoon.panda.application.MainApplication;
import com.shhb.supermoon.panda.fragment.Fragment1;
import com.shhb.supermoon.panda.fragment.Fragment2;
import com.shhb.supermoon.panda.fragment.Fragment3;
import com.shhb.supermoon.panda.fragment.Fragment4;
import com.shhb.supermoon.panda.view.CustomViewPager;

import java.util.ArrayList;

/**
 * Created by superMoon on 2017/7/31.
 */

public class MainActivity extends BaseActivity{
    /** 底部按钮的ViewPager */
    private static CustomViewPager buttonView;
    /** 底部按钮*/
    private static BottomNavigationBar navigationBar;
    /** 去掉首页ViewPager点击动画*/
    private static boolean isAnimation = false;
    private ArrayList<Fragment> fragments;
    private MainAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        setSwipeBackEnable(false);
        initView();
        initBtn();
        initFragments();
        setCurrentPage(0);
    }

    private void initView() {
        mViewNeedOffset = findViewById(R.id.view_need_offset);
        buttonView = (CustomViewPager) findViewById(R.id.viewPager);
        buttonView.setScanScroll(true);
        buttonView.setOffscreenPageLimit(4);
        navigationBar = (BottomNavigationBar) findViewById(R.id.navigation_bar);
    }

    /**
     * 初始化底部菜单按钮
     */
    private void initBtn() {
        navigationBar
                .setInActiveColor(R.color.gray)//设置未选中的Item的颜色，包括图片和文字
                .setActiveColor(R.color.app_color)////设置选中的Item的颜色，包括图片和文字
                .setMode(BottomNavigationBar.MODE_FIXED)//没有切换动画且都有文字（MODE_SHIFTING:换挡模式;MODE_DEFAULT）     如果设置的Mode为MODE_FIXED，将使用BACKGROUND_STYLE_STATIC 。
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)//（RIPPLE：波纹动画、STATIC：没有波纹动画） 如果Mode为MODE_SHIFTING将使用BACKGROUND_STYLE_RIPPLE。
                .setBarBackgroundColor(R.color.white);//设置navigationBar的背景颜色
//        BadgeItem badge = new BadgeItem()
//                .setBorderWidth(2)//Badge的Border(边界)宽度
//                .setBorderColor("#FF0000")//Badge的Border颜色
//                .setBackgroundColor(ContextCompat.getColor(context,R.color.btn_unselect))//Badge背景颜色
//                .setGravity(Gravity.RIGHT| Gravity.TOP)//位置，默认右上角
//                .setText("2")//显示的文本
//                .setTextColor(ContextCompat.getColor(context,R.color.white))//文本颜色
//                .setAnimationDuration(1000)
//                .setHideOnSelect(true);//当选中状态时消失，非选中状态显示
        navigationBar
                .addItem(new BottomNavigationItem(R.mipmap.tab1_on,"首页").setInactiveIconResource(R.mipmap.tab1))
                .addItem(new BottomNavigationItem(R.mipmap.tab2_on,"贷款").setInactiveIconResource(R.mipmap.tab2))
                .addItem(new BottomNavigationItem(R.mipmap.tab3_on,"意向贷").setInactiveIconResource(R.mipmap.tab3))
                .addItem(new BottomNavigationItem(R.mipmap.tab4_on,"我的").setInactiveIconResource(R.mipmap.tab4))
                .initialise();
        navigationBar.setTabSelectedListener(tabSelectedListener);
    }

    /**
     * 底部菜单按钮滑动事件
     */
    private BottomNavigationBar.OnTabSelectedListener tabSelectedListener = new BottomNavigationBar.OnTabSelectedListener() {
        @Override
        public void onTabSelected(int position) {//未选中 -> 选中
            buttonView.setCurrentItem(position,isAnimation);
        }

        @Override
        public void onTabUnselected(int position) {//选中 -> 未选中
        }

        @Override
        public void onTabReselected(int position) {//选中 -> 选中
//            if(position == 3){
//                setBlackStatusBar();
//            } else {
//                clearBlackStatusBar();
//            }
        }
    };

    /**
     * 初始化Fragment
     */
    private void initFragments() {
        fragments = new ArrayList<>();
        fragments.add(Fragment1.newInstance());
        fragments.add(Fragment2.newInstance());
        fragments.add(Fragment3.newInstance());
        fragments.add(Fragment4.newInstance());
        buttonView.addOnPageChangeListener(pageChangeListener);
        viewPagerAdapter = new MainAdapter(getSupportFragmentManager(),fragments);
        buttonView.setAdapter(viewPagerAdapter);
    }

    /**
     * Fragment滑动事件
     */
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            navigationBar.selectTab(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    /**
     * 设置展示页面
     * @param i
     */
    public static void setCurrentPage(int i) {
        navigationBar.setFirstSelectedPosition(i);
        buttonView.setCurrentItem(i,isAnimation);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private long mExitTime;
    private void exit(){
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            MainApplication.exit();
        }
    }
}
