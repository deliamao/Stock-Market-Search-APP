package com.sms.deliamao.stockmarketsearch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

/**
 * Created by deliamao on 5/3/16.
 */
public class TestHistorical extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_historical);
<<<<<<< HEAD
        WebView webViewer = (WebView) findViewById(R.id.historical_chart_webView);
=======
        WebView webViewer = (WebView) findViewById(R.id.webView);
>>>>>>> 5977602b1ef32dc97e834d0d7d03cd6ed85b612d
        webViewer.loadUrl("fragment_historical.html");

    }
}
