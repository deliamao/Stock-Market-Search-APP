package com.sms.deliamao.stockmarketsearch;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by wenjieli on 5/2/16.
 */
public class FavouriteStockManager {
    public FavouriteStockManager(Context context) {
        mContext = context;
    }

    public static final String PREFS_NAME = "MyFavourites";
    public void addFavourite(String symbol) {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(symbol, true);
        editor.commit();

    }
    public boolean isFavourite(String symbol) {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
        return settings.getBoolean(symbol, false);
    }
    public void removeFavourite(String symbol) {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(symbol);
        editor.commit();
    }

    public ArrayList<String> getAllFavourites() {
        ArrayList<String> allFavourites = new ArrayList<String>();
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
        Map<String, ?> allEntries = settings.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            allFavourites.add(entry.getKey());
        }
        return allFavourites;
    }
    private Context mContext;
}
