package com.flyco.tablayoutsamples.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.gome.ecmall.tablayout.widget.SlidingTabLayout;
import com.gome.ecmall.tablayout.listener.OnTabSelectListener;
import com.flyco.tablayoutsamples.R;
import com.flyco.tablayoutsamples.utils.ViewFindUtils;

import java.util.ArrayList;

/**
 * 抢购
 */
public class RushBuyTitleSlidingTabActivity extends AppCompatActivity {
    private Context mContext = this;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private final String[] mTitles = {
            "10:00", "12:00", "14:00" , "16:00", "18:00", "18:00", "20:00"
    };
    private final String[] mSubTitles = {
            "12月22日", "12月22日", "12月22日", "12月22日", "12月22日","12月22日", "12月22日"
    };

    private MyPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_tab3);

        for (String title : mTitles) {
            mFragments.add(SimpleCardFragment.getInstance(title));
        }

        View decorView = getWindow().getDecorView();

        ViewPager vp = ViewFindUtils.find(decorView, R.id.vp);
        mAdapter = new RushBuyTitleSlidingTabActivity.MyPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(mAdapter);

        /**自定义部分属性*/
        SlidingTabLayout tabLayout_2 = ViewFindUtils.find(decorView, R.id.tl_2);

        tabLayout_2.setViewPager(vp,mTitles,mSubTitles);
        tabLayout_2.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                Toast.makeText(mContext, "选中 position--->" + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabReselect(int position) {
                Toast.makeText(mContext, "重复选中 position--->" + position, Toast.LENGTH_SHORT).show();
            }
        });
        tabLayout_2.setCurrentTab(3);

    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }
}
