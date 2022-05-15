package com.kimjiun.yahoostockapp.yahoo;

import com.kimjiun.yahoostockapp.yahoo.json.YahooStockResults;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface YahooService {
    // CHANGE HERE
    String APIKEY = "YOUR KEY";

    @Headers("X-API-KEY: " + APIKEY)
    @GET("quote?format=json")
    Single<YahooStockResults> yqlQuery(
            @Query("region") String region,
            @Query("lang") String lang,
            @Query("symbols") String symbols
    );
}
