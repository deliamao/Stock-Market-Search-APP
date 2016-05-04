package com.sms.deliamao.stockmarketsearch;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by deliamao on 5/3/16.
 */
public class GenerateCurrent extends AppCompatActivity {
    private static final String TAG = "GenerateCurrent";
    ListView stockList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current);
        Bundle extras = getIntent().getExtras();
        String[] rTitle = extras.getStringArray("stockTitle");
        String[] rContent= extras.getStringArray("stockContent");
        stockList =(ListView) findViewById(R.id.listView);
        stockList.setAdapter(new StockAdapter(this,rTitle,rContent));
    }



    public class StockAdapter extends ArrayAdapter<String> {
        String[] title;
        String[] contents;
        Context c;
        LayoutInflater inflater = null;
        //only set up no image

        public StockAdapter(Context context, String[] title, String[] contents){
            super(context, R.layout.list_model,title);
            this.c = context;
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
            if(convertView == null){
                inflater =(LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_model,null);
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
}
