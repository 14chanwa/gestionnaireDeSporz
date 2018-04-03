package com.minastelien.quentin.gestionnairedesporz;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.minastelien.quentin.gestionnairedesporz.Fragments.Fragment_stats_games;
import com.minastelien.quentin.gestionnairedesporz.Fragments.Fragment_stats_roles;

/**
 * This class displays statistics according to the database.
 * Created by Quentin on 01/05/2016.
 */
public class Activity_stats extends AppCompatActivity {

    private final String KEY_CURRENT_FRAME = "current_frame";
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    // Tab titles
    private String[] tabNames = {"Rôles", "Jeux"};

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initialization
        mViewPager = findViewById(R.id.pager);
        mTabLayout = findViewById(R.id.activity_stats_sliding_tabs);

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int index) {

                switch (index) {
                    case 0:
                        // Roles fragment activity
                        return new Fragment_stats_roles();
                    case 1:
                        // Games fragment activity
                        return new Fragment_stats_games();
                }

                return null;
            }

            @Override
            public int getCount() {
                // get item count - equal to number of tabs
                return 2;
            }
        });

        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                mTabLayout.addTab(mTabLayout.newTab().setText(tabNames[0]));
                mTabLayout.addTab(mTabLayout.newTab().setText(tabNames[1]));
                mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            }
        });

        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                if (savedInstanceState != null) {
                    int current_frame_id_restored = savedInstanceState.getInt(KEY_CURRENT_FRAME);
                    mViewPager.setCurrentItem(current_frame_id_restored);
                    mTabLayout.getTabAt(current_frame_id_restored).select();
                }
            }
        });

        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        mTabLayout.getTabAt(position).select();
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        });

        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        mViewPager.setCurrentItem(tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_FRAME, mViewPager.getCurrentItem());
    }

}
