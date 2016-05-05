package com.sms.deliamao.stockmarketsearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import org.apache.commons.io.IOUtils;
import java.io.IOException;

/**
 * Created by deliamao on 5/2/16.
 */
public class PageHistoryFragment extends Fragment {

    private static final String TAG = "PageHistoryFragment";
    private String content;

    public static PageHistoryFragment newInstance(String s) {
        PageHistoryFragment fragment = new PageHistoryFragment();
        fragment.content = s;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historical, container, false);
        WebView wv = (WebView) view.findViewById(R.id.historical_chart_webView);

        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        /*
        String content = "";
        try {
            content = IOUtils.toString(getAssets().open("historical.html")).replaceAll("%test%", symbol);
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }*/

        wv.loadDataWithBaseURL("file:///android_asset/historical.html", content, "text/html", "UTF-8", null);
        return view;
    }
}

