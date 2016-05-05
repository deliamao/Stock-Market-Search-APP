package com.sms.deliamao.stockmarketsearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by deliamao on 5/2/16.
 */
public class PageHistoryFragment extends Fragment {
    private String content;

    public static PageHistoryFragment newInstance(String S) {
        PageHistoryFragment fragment = new PageHistoryFragment();
        fragment.content = S;
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_historical, container, false);
    }
}

