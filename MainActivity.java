package com.sms.deliamao.stockmarketsearch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    // when the clear button was clicked
    public void clearInput(View view){
        EditText inputInfo = (EditText) findViewById(R.id.stock_info);
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
