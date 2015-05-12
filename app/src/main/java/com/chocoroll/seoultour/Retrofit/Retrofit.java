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
    @POST("/login/login.php")
    public void login(@Body JsonObject info, Callback<String> callback);
    @POST("/listview/categorylist.php")
    public void getDealList(@Body JsonObject info, Callback<JsonArray> callback);

}
