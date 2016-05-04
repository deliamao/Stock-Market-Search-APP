package com.sms.deliamao.stockmarketsearch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private AutoCompleteTextView suggestionTextView;
    AsyncTask<Void, Void, Void> mTask;
    AsyncTask<Void, Void, Void> newsTask;
    AsyncTask<Void, Void, Void> hischartTask;
    ArrayList<String> autoCompleteList;
    String quoteJsonString = "";
    String newsJsonString ="";
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
        button1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // do something when the button is clicked
                AutoCompleteTextView inputInfo = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
                //get inputInfo value by getText()；
                inputInfo.setText("");
            }
        });

        Button button2 = (Button)findViewById(R.id.getQuote);
        button2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AutoCompleteTextView inputInfo = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
                String symbol = inputInfo.getText().toString();
                if (verifyQuoteInput()) {
                    getQuote(symbol);
                }
            }
        });

        // Create favourite stocks ListView.
        ArrayAdapter adapter = new FavouriteStockAdapter(this);
        ListView favouriteStocksView = (ListView) findViewById(R.id.favourite_stocks_view);
        favouriteStocksView.setDivider(null);
        favouriteStocksView.setAdapter(adapter);
        favouriteStocksView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                FavouriteStockAdapter adapter  = (FavouriteStockAdapter)parent.getAdapter();
                StockQuote clickStock = adapter.getItem(position);
                getQuote(clickStock.getSymbol());
            }
        });
        updateStockFavouriteDataAndView();
    }

    private class AsyncFetchStockSymbols extends AsyncTask<String, Void, ArrayList<String>>{
        private static final String TAG = "AsyncFetchStockSymbols";
        @Override
        protected ArrayList<String> doInBackground(String... constraint) {
            ArrayList<String> stockSymbols = new ArrayList<String>();
            Log.e(TAG, "Fetching symbol: " + constraint[0]);
            String url = WebFetchHelper.STOCK_SYMBOL_URL + constraint[0];
            String jsonString = WebFetchHelper.fetchUrl(url);
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

    private class FavouriteStockAdapter extends ArrayAdapter<StockQuote> {

        private List<StockQuote> stockList;
        private Context context;

        public FavouriteStockAdapter(Context ctx) {
            super(ctx, R.layout.favourite_stock_item);
            this.context = ctx;
            this.stockList = new ArrayList<StockQuote>();
        }
        public int getCount() {
            return stockList.size();
        }
        public StockQuote getItem(int position) {
            return stockList.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.favourite_stock_item, parent, false);
            }
            TextView  symbolView = (TextView) convertView.findViewById(R.id.stock_symbol);
            TextView nameView = (TextView) convertView.findViewById(R.id.stock_name);
            TextView priceView = (TextView) convertView.findViewById(R.id.stock_price);
            TextView changeView = (TextView) convertView.findViewById(R.id.stock_change);
            TextView  marketCapView = (TextView) convertView.findViewById(R.id.stock_market_cap);
            StockQuote stock = stockList.get(position);
            symbolView.setText(stock.getSymbol());
            nameView.setText(stock.getName());
            priceView.setText("$ " + String.format("%.2f", stock.getPrice()));
            changeView.setText((stock.getChangePercent() >= 0 ? "+ " : "- ") + String.format("%.2f", Math.abs(stock.getChangePercent())) + "%");
            if (stock.getChangePercent() > 0) {
                changeView.setBackgroundColor(Color.GREEN);
            } else {
                changeView.setBackgroundColor(Color.RED);
            }
            marketCapView.setText("Market Cap: " + String.format("$%.2f", stock.getMarketCap() / 1000000000) + " Billion");
            return convertView;
        }

        public void updateDataSet(ArrayList<StockQuote> newDataSet) {
            stockList.clear();
            stockList.addAll(newDataSet);
            notifyDataSetChanged();
        }
    }

    void updateStockFavouriteDataAndView() {
        AsyncTask<ArrayList<StockQuote>, Void, ArrayList<StockQuote>> fetchQuotesTask = new AsyncTask<ArrayList<StockQuote>, Void, ArrayList<StockQuote>> () {
            @Override
            protected ArrayList<StockQuote> doInBackground(ArrayList<StockQuote>... quotes) {
                ArrayList<StockQuote> stockQuotes = new ArrayList<StockQuote>();
                for (StockQuote quote : quotes[0]) {
                    Log.d(TAG, "Fetching stock quote for symbol: " + quote.getSymbol());
                    String url = WebFetchHelper.STOCK_QUOTE_URL + quote.getSymbol();
                    String jsonString = WebFetchHelper.fetchUrl(url);
                    JSONObject jo = null;
                    StockQuote stockQuote = null;
                    try {
                        stockQuote = new StockQuote(new JSONObject(jsonString));
                    } catch (JSONException e) {
                        Log.e(TAG, "Error to parse stock json: " + e.getMessage());
                    }
                    stockQuotes.add(stockQuote);
                }
                return stockQuotes;
            }
            @Override
            protected void onPostExecute(ArrayList<StockQuote> result) {
                HashMap<String, StockQuote> stockQuoteMap = new HashMap<String, StockQuote>();
                for (StockQuote q : result) {
                    if (q != null) {
                        stockQuoteMap.put(q.getSymbol(), q);
                    }
                }
                ArrayList<StockQuote> newDataSet = new ArrayList<StockQuote>();
                for (StockQuote quote : mFavouriteStockManager.getAllFavourites()) {
                    Log.d(TAG, "to update favourite " + quote.getSymbol() + "Message: " + quote.toJSONString());
                    if (stockQuoteMap.containsKey(quote.getSymbol())) {
                        mFavouriteStockManager.addOrUpdateFavourite(quote);
                        Log.d(TAG, "updated to favourite");
                    }
                }
            }
        };
        ArrayList<StockQuote> favouriteStocks = mFavouriteStockManager.getAllFavourites();
        fetchQuotesTask.execute(favouriteStocks);
        updateStockFavouriteView();
    }
    void updateStockFavouriteView() {
        ListView favouriteStockView = (ListView) findViewById(R.id.favourite_stocks_view);
        FavouriteStockAdapter adapter = (FavouriteStockAdapter) favouriteStockView.getAdapter();
        ArrayList<StockQuote> favouriteStocks = mFavouriteStockManager.getAllFavourites();
        adapter.updateDataSet(favouriteStocks);
    }

    boolean verifyQuoteInput() {
        AutoCompleteTextView inputInfo = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
        String symbol = inputInfo.getText().toString();
        Log.d(TAG, "getquote: " + inputInfo.getText().toString());
        //validate if the input is blank;
        if(symbol.length()== 0) {
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
            return false;
        }
        return true;
    }
    // handle get quote function
    void getQuote(String symbol) {
        // handle get Quote Detial
        //Intent intentOfDetail = new Intent(MainActivity.this, ResultActivity.class);

        /*************************** headle news feed code end  *******************************/
        final String newsURL = "http://deliancapp-env.us-west-1.elasticbeanstalk.com/index.php/index.php?bingVal=" + symbol;
        newsTask = new AsyncTask<Void, Void, Void> () {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected Void doInBackground(Void... params) {
                HttpURLConnection c = null;
                try {
                    URL u = new URL(newsURL);
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
                        Log.d(TAG, "MainGetnewsString0: ");
                        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line+"\n");
                        }
                        br.close();
                        //newsJsonString = sb.toString();
                        return null;
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
                return null;

            }

            protected void onPostExecute(Void result) {}
        };
        newsTask.execute();

        /*************************** headle news feed  code end  *******************************/
        /// handle stock detail problem
        final String quoteURL = "http://deliancapp-env.us-west-1.elasticbeanstalk.com/index.php/index.php?symbolVal=" + symbol;
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
                        Log.d(TAG, "stock detail has Message or not:" + jo.getString("Message"));
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
                            intentOfDetail.putExtra("newsReturnString", newsJsonString);
                            startActivity(intentOfDetail);
                            Log.d(TAG, "getQuote: " + quoteJsonString);

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
    // when the get quote button was click
    // Refresh MainActivity Views. e.g. back button to navigate back.
    public void onResume() {  //
        Log.d(TAG, "onResume");
        super.onResume();
        //Refresh Favourite lists here.
        updateStockFavouriteView();
    }
}

