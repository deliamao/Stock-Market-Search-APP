package com.sms.deliamao.stockmarketsearch;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Filter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private AutoCompleteTextView suggestionTextView;
    ArrayList<String> autoCompleteList;
    String url = ""; //  to call the http://deliancapp-env.us-west-1.elasticbeanstalk.com/index.php/index.php
    String jsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            Log.d(TAG, "onCreate() Restoring previous state");
            /* restore state */
        }
        Log.d(TAG, "onCreate() debug debug");

        suggestionTextView=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);

        AutoCompleteAdapter autoCompleteAdapter = new AutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line);

        suggestionTextView.setAdapter(autoCompleteAdapter);
        suggestionTextView.setThreshold(1); // setup how many letter need to call
        Log.d(TAG, "this is test");
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
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
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
                            String symbol = e.getString("Symbol") +" "+e.getString("Name") + "(" + e.getString("Exchange") + ")";
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
                e.printStackTrace();
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
    public void clearInput(View view){
        AutoCompleteTextView inputInfo = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
        //get inputInfo value by getText()；
        inputInfo.setText("");

    }

    // when the get quote button was click
    public void getQuote(View view){
        if(validate()){

        }
    }

    public boolean validate(){
        EditText inputInfo = (EditText) findViewById(R.id.stock_info);
        return true;
        // to validate the input is right or not
    }
}