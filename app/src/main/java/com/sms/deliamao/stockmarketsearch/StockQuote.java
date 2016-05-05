package com.sms.deliamao.stockmarketsearch;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wenjieli on 5/3/16.
 */
public class StockQuote {
    private String symbol;
    private String name;
    private String timestamp;
    private double price;
    private double change;
    private double changePercent;
    private double ChangeYTD;
    private double marketCap;
    private double volume;
    private double high;
    private double low;
    private double open;


    public StockQuote() {}
    public StockQuote(JSONObject json) {
        /*
        rcontent[0] = quoteObject.getString("Name");
        rcontent[1] = quoteObject.getString("Symbol");
        rcontent[2] = quoteObject.getString("LastPrice");
        rcontent[3] = quoteObject.getString("Change");
        rcontent[4] = quoteObject.getString("Timestamp");
        rcontent[5] = quoteObject.getString("MarketCap");
        rcontent[6] = quoteObject.getString("Volume");
        rcontent[7] = quoteObject.getString("ChangeYTD");
        rcontent[8] = quoteObject.getString("High");
        rcontent[9] = quoteObject.getString("Low");
        rcontent[10] = quoteObject.getString("Open");
        */
        parseFromJSONObject(json);
    }
    public void parseFromJSONObject(JSONObject json) {
        try {
            symbol = json.getString("Symbol");
            name = json.getString("Name");
            price = json.getDouble("LastPrice");
            change = json.getDouble("Change");
            changePercent = json.getDouble("ChangePercent");
            marketCap = json.getDouble("MarketCap");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String toJSONString() {
        JSONObject json = new JSONObject();
        try {
            json.accumulate("Symbol", symbol);
            json.accumulate("Name", name);
            json.accumulate("LastPrice", price);
            json.accumulate("Change", change);
            json.accumulate("ChangePercent", changePercent);
            json.accumulate("MarketCap", marketCap);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public static StockQuote fromJSONString(String jsonString) {
        StockQuote quote = new StockQuote();
        try {
            JSONObject json = new JSONObject(jsonString);
            quote.parseFromJSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return quote;
    }

    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public double getMarketCap() {
        return marketCap;
    }
    public double getChangePercent() {
        return changePercent;
    }
}
