package com.sms.deliamao.stockmarketsearch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TableLayout;

/**
 * Created by deliamao on 5/2/16.
 */
public class ResultActivity extends AppCompatActivity {
    String quoteJson;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_detail);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            quoteJson = extras.getString("QuoteReturnString");

        }
        /*
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();*/
    }
}
