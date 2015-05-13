package com.chocoroll.seoultour.Retrofit;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by RA on 2015-05-12.
 */
public interface Retrofit {
    public static final String ROOT = "http://seoultour.dothome.co.kr";

    @POST("/getStampList.php")
    public void getStampList(@Body JsonObject info, Callback<JsonArray> callback);

    @POST("/getStamp.php")
    public void getStamp(@Body JsonObject info, Callback<String> callback);
    @POST("/checkStamp.php")
    public void checkStamp(@Body JsonObject info, Callback<String> callback);

    @POST("/getGuestBook.php")
    public void getGuestBook(@Body JsonObject info, Callback<JsonArray> callback);
    @POST("/sendGuestBook.php")
    public void sendGuestBook(@Body JsonObject info, Callback<String> callback);

}
