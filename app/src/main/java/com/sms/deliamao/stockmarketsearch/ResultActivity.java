package com.sms.deliamao.stockmarketsearch;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by deliamao on 5/2/16.
 */
public class ResultActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;

    String quoteJson;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_detail);
        // you may need to set up toolbar in here later

        viewPager = (ViewPager)findViewById(R.id.viewpager);
        ViewPagerAdapter  viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),getApplicationContext());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout = (TabLayout)findViewById(R.id.tabs);

        // if without below code it won't stop
        tabLayout.setupWithViewPager(viewPager);


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });
        /*
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            quoteJson = extras.getString("QuoteReturnString");

        }*/



        /*
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();*/
    }


    private class ViewPagerAdapter extends FragmentPagerAdapter{
        private String fragments [] = {"CURRENT", "HISTORICAL", "NEWS"};
        public ViewPagerAdapter(FragmentManager supportFragmentManger, Context applicationContext){
            super(supportFragmentManger);

        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    return new Fragment1();
                case 1:
                    return new Fragment2();
                case 2:
                    return new Fragment3();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragments[position];
        }
    }








}



