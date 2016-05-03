package com.sms.deliamao.stockmarketsearch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private AutoCompleteTextView suggestionTextView;
    AsyncTask<Void, Void, Void> mTask;
    ArrayList<String> autoCompleteList;
    String url = ""; //  to call the http://deliancapp-env.us-west-1.elasticbeanstalk.com/index.php/index.php
    String quoteJsonString = "";
    Context context;
    FavouriteStockManager mFavouriteStockManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFavouriteStockManager = new FavouriteStockManager(this);

        setContentView(R.layout.activity_main);
        context = this;
        if (savedInstanceState != null) {
            Log.d(TAG, "onCreate() Restoring previous state");
            /* restore state */
        }

        suggestionTextView=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);

        AutoCompleteAdapter autoCompleteAdapter = new AutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line);

        suggestionTextView.setAdapter(autoCompleteAdapter);
        suggestionTextView.setThreshold(1); // setup how many letter need to call
        Log.d(TAG, "this is test");

        Button button1 = (Button)findViewById(R.id.clear);
        button1.setOnClickListener(mClearListener);

        Button button2 = (Button)findViewById(R.id.getQuote);
        button2.setOnClickListener(mGetQuoteListener);

    }

    private class AsyncFetchStockSymbols extends AsyncTask<String, Void, ArrayList<String>>{
        private static final String TAG = "AsyncFetchStockSymbols";

        @Override
        protected ArrayList<String> doInBackground(String... constraint) {
            ArrayList<String> stockSymbols = new ArrayList<String>();
            Log.e(TAG, "Fetching symbol: " + constraint[0]);
            final String stockSymbolUrl = "http://deliancapp-env.us-west-1.elasticbeanstalk.com/index.php/index.php?input=";
            HttpURLConnection conn = null;
            try{
                URL u = new URL(stockSymbolUrl + constraint[0]);
                conn = (HttpURLConnection) u.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("content-length","0");
                conn.setUseCaches(false);
                conn.setAllowUserInteraction(false);
                conn.setConnectTimeout(20000);
                conn.setReadTimeout(20000);
                conn.connect();
                int status = conn.getResponseCode();
                if(status == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while((line=br.readLine()) != null){
                        sb.append(line + "\n");
                    }
                    br.close();
                    String jsonString = sb.toString();
                    JSONArray jo = null;
                    try {
                        jo = new JSONArray(jsonString);
                        for(int i = 0; i<jo.length();i++ ){
                            JSONObject e = jo.getJSONObject(i);
                            String symbol = e.getString("Symbol");
                            Log.d(TAG, "get: " + symbol);
                            stockSymbols.add(symbol);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error to parse stock json: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "Fail to fetch stock symbols: " + constraint[0] + ". Code:" + status);
                }
            } catch (Exception e) {
                Log.e(TAG, "Fail to connect: " + stockSymbolUrl + ", Message:" + e.toString());
            }
            return stockSymbols;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {}
    }

    private class AutoCompleteAdapter extends ArrayAdapter {
        ArrayList<String> mSymbols;
        public AutoCompleteAdapter(Context context, int textViewResourceId){
            super(context, textViewResourceId);
            mSymbols = new ArrayList<String>();
        }

        @Override
        public Filter getFilter() {
            Filter autoCompleteFilter = new Filter(){
                @Override
                protected FilterResults performFiltering(CharSequence constraint){
                    FilterResults filterResults = new FilterResults();
                    if(constraint != null) {
                        try {
                            mSymbols = new AsyncFetchStockSymbols().execute(new String[]{constraint.toString()}).get();
                        } catch(Exception e) {
                            Log.e("AsyncFetchStockSymbols", e.getMessage());
                        }
                        // Now assign the values and count to the FilterResults object
                        filterResults.values = mSymbols;
                        filterResults.count = mSymbols.size();
                    }
                    return filterResults;
                }
                @Override
                protected void publishResults(CharSequence contraint, FilterResults results) {
                    if(results != null && results.count > 0) {
                        Log.d(TAG, "Result numbers: " + results.count);
                        clear();
                        for (String symbol : (ArrayList<String>)results.values) {
                            add(symbol);
                        }
                        notifyDataSetChanged();
                    }
                    else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return autoCompleteFilter;
        }
    }
    // when the clear button was clicked
    private OnClickListener mClearListener = new OnClickListener() {
        public void onClick(View v) {
            // do something when the button is clicked
            AutoCompleteTextView inputInfo = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
            //get inputInfo value by getText()；
            inputInfo.setText("");
        }
    };

    // handle get quote function
    private OnClickListener mGetQuoteListener = new OnClickListener() {
        public void onClick(View v) {
            // do something when the button is clicked
            AutoCompleteTextView inputInfo = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
            Log.d(TAG, "getquote: " + inputInfo.getText().toString());
            //validate if the input is blank;
            if(inputInfo.getText().toString().length()== 0){
                Log.d(TAG, "length0 " + inputInfo.getText().toString());
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("please enter a Stock Name/Symbol")
                        .setCancelable(false)
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }else{
                // handle the input not empty
                final String quoteURL = "http://deliancapp-env.us-west-1.elasticbeanstalk.com/index.php/index.php?symbolVal=" + inputInfo.getText().toString();
                mTask = new AsyncTask<Void, Void, Void> () {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }
                    @Override
                    protected Void doInBackground(Void... params) {
                        HttpURLConnection c = null;
                        try {
                            URL u = new URL(quoteURL);
                            c = (HttpURLConnection) u.openConnection();
                            c.setRequestMethod("GET");
                            c.setRequestProperty("Content-length", "0");
                            c.setUseCaches(false);
                            c.setAllowUserInteraction(false);
                            c.setConnectTimeout(5000);
                            c.setReadTimeout(5000);
                            c.connect();
                            int status = c.getResponseCode();
                            if (status == 200) {
                                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                                StringBuilder sb = new StringBuilder();
                                String line;
                                while ((line = br.readLine()) != null) {
                                    sb.append(line+"\n");
                                }
                                br.close();
                                quoteJsonString = sb.toString();
                                return null;
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;

                    }

                    protected void onPostExecute(Void result) {
                        super.onPostExecute(result);
                        JSONObject jo = null;
                        try {
                            jo = new JSONObject(quoteJsonString);
                            if(jo.has("Message")){
                                Log.d(TAG, "joMessage:" + jo.getString("Message"));
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Invalid Symbol")
                                        .setCancelable(false)
                                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //do things
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();

                            }else{
                                if(jo.has("Status") && jo.getString("Status").equals("SUCCESS")){
                                    Log.d(TAG, "has Status" + jo.getString("Status"));
                                    Intent intentOfDetail = new Intent(MainActivity.this, ResultActivity.class);
                                    intentOfDetail.putExtra("QuoteReturnString", quoteJsonString);
                                    Log.d(TAG, "getQuote: " + quoteJsonString);
                                    startActivity(intentOfDetail);

                                }else{
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setMessage("Symbol return Status is false, chooose another one")
                                            .setCancelable(false)
                                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    //do things
                                                    dialog.cancel();
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }
                                // use the intent function to transfer this to another activity
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                mTask.execute();
            }
        }
    };
    // when the get quote button was click
    // Refresh MainActivity Views. e.g. back button to navigate back.
    public void onResume() {  //
        Log.d(TAG, "onResume");
        super.onResume();
        //Refresh Favourite lists here.
        ArrayList<String> farvouriteList = mFavouriteStockManager.getAllFavourites();
        Log.d(TAG, "Favourite list: " + Arrays.toString(farvouriteList.toArray()));
    }
}

