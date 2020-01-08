package com.flyco.tablayoutsamples.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.flyco.tablayoutsamples.ui.SimpleCardFragment;
import com.gome.ecmall.tablayout.entity.TabEntity;
import com.gome.ecmall.tablayout.widget.HomeTitleTabLayout;
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
//        private final String[] mTitles = {
//            "10:00", "12:00", "14:00"
//    };
//    private final String[] mSubTitles = {
//            "12月22日", "12月22日", "12月22日"
//    };

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
        HomeTitleTabLayout tabLayout_1 = ViewFindUtils.find(decorView, R.id.tl_1);
        HomeTitleTabLayout tabLayout_2 = ViewFindUtils.find(decorView, R.id.tl_2);
        HomeTitleTabLayout tabLayout_3 = ViewFindUtils.find(decorView, R.id.tl_3);

        confitTab1(tabLayout_1);
        confitTab2(tabLayout_2);
        confitTab3(tabLayout_3);

        tabLayout_1.setViewPager(vp);
        tabLayout_2.setViewPager(vp);
        tabLayout_3.setViewPager(vp);
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

    private void confitTab3(HomeTitleTabLayout tabLayout){
        TabEntity config = new TabEntity();
        config.type = TabEntity.TYPE_3;
        config.titleSelectColor ="#F20C59";
        config.titleUnSelectColor ="#262C32";

        config.subTitleTextSelectColor ="#88F20C59";
        config.subTitleTextUnSelectColor ="#88262C32";

        config.tagTitles = mTitles;
        config.tagSubTitles = mSubTitles;

        config.showTagDivider =false;

        tabLayout.initTabLayout(config);
    }

    private void confitTab1(HomeTitleTabLayout tabLayout){
        TabEntity config = new TabEntity();
        config.type = TabEntity.TYPE_1;
        config.titleSelectColor ="#F20C59";
        config.titleUnSelectColor ="#262C32";

        config.subTitleTextSelectColor ="#88F20C59";
        config.subTitleTextUnSelectColor ="#88262C32";

        config.showTagDivider =true;

        config.tagSelectBgColor="#FFA1A1";
        config.tagUnSelectBgColor="#FFFFFF";
        config.tagTitles = mTitles;

        tabLayout.initTabLayout(config);
    }

    /**
     * 圆角标签
     * @param tabLayout
     */
    private void confitTab2(HomeTitleTabLayout tabLayout){
        TabEntity config = new TabEntity();
        config.type = TabEntity.TYPE_2;
        config.titleSelectColor ="#F20C59";
        config.titleUnSelectColor ="#262C32";

        config.subTitleTextSelectColor ="#88F20C59";
        config.subTitleTextUnSelectColor ="#88262C32";

        config.showTagDivider =true;

        config.tagSelectBgColor="#FFA1A1";
        config.tagUnSelectBgColor="#FFFFFF";
        config.tagTitles = mTitles;

        tabLayout.initTabLayout(config);
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
