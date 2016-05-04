package com.sms.deliamao.stockmarketsearch;

import com.facebook.FacebookSdk;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import  com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.LikeView;

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

    ListView stockList;

    String quoteJson;
    String  newJson;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
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

        /*************************** headle news feed code end  *******************************/
       if(extras != null){
            Log.d(TAG, "none object " );
            newJson = extras.getString("newsReturnString");
           // get the whole news feed;
            Log.d(TAG, "newsReturnString: " );
        }
       //test
        //Intent intentOfDetail2 = new Intent(ResultActivity.this, TestHistorical.class);
        //startActivity(intentOfDetail2);


        /*************************** headle news feed  code end  *******************************/
// Crazy ViewList problem
        /*************************** headle stock detail code *******************************/
        String[] rcontent = new String[11];
        String [] rtitle = {"NAME","SYMBOL","LASTPRICE","CHANGE","TIMESTAPM","MARKETCAP","VOLUME","CHANGEYTD","HIGH","LOW","OPEN"};
        if (extras!= null){
            quoteJson = extras.getString("QuoteReturnString");
            Log.d(TAG, "extras de quote result" + quoteJson);
            Log.d(TAG, "extras de quote result" + rtitle[5]);
            JSONObject quoteObject = null;
            try{
                quoteObject = new JSONObject(quoteJson);
                rcontent[0] = quoteObject.getString("Name");
                rcontent[1] = quoteObject.getString("Symbol");
                rcontent[2] = quoteObject.getString("LastPrice");
                rcontent[3] = quoteObject.getString("Change");
                rcontent[4] = quoteObject.getString("Timestamp");
                rcontent[5] = quoteObject.getString("MarketCap");
                rcontent[6] = quoteObject.getString("Volume");
                rcontent[7] = quoteObject.getString("ChangeYTD");
                rcontent[8] = quoteObject.getString("High");
                rcontent[9] = quoteObject.getString("Low");
                rcontent[10] = quoteObject.getString("Open");
                Log.d(TAG, "extra de quote result" + rcontent[5]);
                //D New Actioin
                /*
                Intent intentOfDetail = new Intent(ResultActivity.this, GenerateCurrent.class);
                intentOfDetail.putExtra("stockTitle", rtitle);
                intentOfDetail.putExtra("stockContent", rcontent);
                startActivity(intentOfDetail);
                */
                // D New Action
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        View v = getLayoutInflater().inflate(R.layout.current, null);
        Log.d(TAG, "view" + v);
        stockList =(ListView) v.findViewById(R.id.listView);
        Log.d(TAG, "stockList" + stockList);

        stockList.setAdapter(new StockAdapter(this,rtitle,rcontent));
        Log.d(TAG, "setAdapther" + this);
    }


    // new Action

    public class StockAdapter extends ArrayAdapter<String> {
        String[] title;
        String[] contents;
        Context c;
        LayoutInflater inflater = null;
        //only set up no image

        public StockAdapter(Context context, String[] title, String[] contents){
            super(context, R.layout.list_model,title);
            this.c = context;
            Log.d(TAG, "context" + c);
            this.title = title;
            this.contents = contents;
            Log.d(TAG, "extra de quote result" + title[5]);
        }

        public class ViewHolder{
            TextView stockTitle;
            TextView  sdCont;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            Log.d(TAG, "this is the view" + convertView);
            if(convertView == null){
                inflater =(LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_model,null);
                Log.d(TAG, "convertView" + contents[position]);
                //convertView = getLayoutInflater().inflate(R.layout.list_model, parent, false);

            }

            final ViewHolder holder = new ViewHolder();
            holder.stockTitle = (TextView) convertView.findViewById(R.id.textView1);
            holder.sdCont =(TextView) convertView.findViewById(R.id.textView2);
            //Assign Data
            holder.stockTitle.setText(title[position]);
            holder.sdCont.setText(contents[position]);
            Log.d(TAG, "Adapther test" + contents[position]);
            return convertView;
        }

    }
    /*************************** headle stock detail code end  *******************************/
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
                // face book part 4 part
                // share URI
                ShareLinkContent uricontent = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("https://developers.facebook.com"))
                        .build();
               // share image  // it should byahood image

                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                // it should be the action fb share~~~
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

