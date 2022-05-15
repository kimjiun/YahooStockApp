package com.kimjiun.yahoostockapp.yahoo.json;

import java.util.List;

public class YahooStockQuoteResponse {
    private List<YahooStockResult> result;
    private String error;

    public List<YahooStockResult> getResult() {
        return result;
    }

    public String getError() {
        return error;
    }
}
