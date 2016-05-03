package com.sms.deliamao.stockmarketsearch;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TableLayout;

/**
 * Created by deliamao on 5/2/16.
 */
public class ResultActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MenuItem mFavouriteButton;
    private FavouriteStockManager mFavouriteStockManager;
    private JSONObject mStockQuote;
    private String mStockId;
    private String mStockName;

    String quoteJson;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init any table here.
        mFavouriteStockManager = new FavouriteStockManager(this);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.e(TAG, "Expect QuoteReturnString, but no Extras found.");
            finish();
        } else {
            try {
                mStockQuote = new JSONObject(extras.getString("QuoteReturnString"));
                mStockId = mStockQuote.getString("Symbol");
                mStockName = mStockQuote.getString("Name");
            } catch (JSONException e) {
                Log.e(TAG, "Unable to parse QuoteReturnString.");
                finish();
            }
        }
        Log.d(TAG, mStockQuote.toString());

        // Init any UI views in following.
        setContentView(R.layout.stock_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mStockName);
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
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();*/
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    // Add favourite and facebook share button
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_view, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onPrepareOptionsMenu (Menu menu) {
        mFavouriteButton = menu.findItem(R.id.action_favorite);
        if (mFavouriteStockManager.isFavourite(mStockId)) {
            mFavouriteButton.setIcon(R.drawable.ic_filled_star);
        } else {
            mFavouriteButton.setIcon(R.drawable.ic_star_outline);
        }
        return true;
    }

    public boolean is_favourite = false;

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:
                Log.d(TAG, "action_favorite");
                if (mFavouriteStockManager.isFavourite(mStockId)) {
                    mFavouriteStockManager.removeFavourite(mStockId);
                    item.setIcon(R.drawable.ic_star_outline);
                } else {
                    mFavouriteStockManager.addFavourite(mStockId);
                    item.setIcon(R.drawable.ic_filled_star);
                }
                return true;
            case R.id.action_fb:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                Log.d(TAG, "action_fb");
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
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

