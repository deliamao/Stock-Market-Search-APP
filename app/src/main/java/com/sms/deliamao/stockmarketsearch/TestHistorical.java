package com.sms.deliamao.stockmarketsearch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;

/**
 * Created by deliamao on 5/3/16.
 */
public class TestHistorical extends AppCompatActivity {
    private static final String TAG = "HistoricalChart";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historical);
        WebView webViewer = (WebView) findViewById(R.id.webView);
        Log.d(TAG,"this is a test"+ webViewer);
        webViewer.loadUrl("../android_asset/historical.html");
        setContentView(R.layout.historical);


    }
}
