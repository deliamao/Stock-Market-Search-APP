package com.sms.deliamao.stockmarketsearch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.TabLayout;

/**
 * Created by deliamao on 5/1/16.
 */
public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_detail);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
    }
}
