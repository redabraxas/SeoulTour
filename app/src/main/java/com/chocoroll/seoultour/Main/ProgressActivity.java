package com.chocoroll.seoultour.Main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chocoroll.seoultour.Adapter.ProgressAdapter;
import com.chocoroll.seoultour.Model.District;
import com.chocoroll.seoultour.Model.Item;
import com.chocoroll.seoultour.R;
import com.chocoroll.seoultour.Retrofit.Retrofit;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ProgressActivity extends Activity {
    ProgressDialog dialog;
    ArrayList<Item> arrayList= new ArrayList<Item>();
    ProgressAdapter adepter;
    ArrayList<District> distList;
    ListView listView;

    int stampSum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        getStampList();

        Intent intent = getIntent();
        distList = intent.getParcelableArrayListExtra("districtList");
    }

    void getStampList(){

        dialog = new ProgressDialog(ProgressActivity.this);
        dialog.setMessage("스탬프 리스트를 가져오는 중입니다...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();


        TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String phoneID = telephony.getDeviceId();    //device id

        final JsonObject info = new JsonObject();
        info.addProperty("phoneID",phoneID);

        new Thread(new Runnable() {
            public void run() {
                try {
                    RestAdapter restAdapter = new RestAdapter.Builder()
                                .setEndpoint(Retrofit.ROOT)  //call your base url
                                .build();
                        Retrofit sendreport = restAdapter.create(Retrofit.class); //this is how retrofit create your api
                        sendreport.getStampList(info, new Callback<JsonArray>() {

                        @Override
                        public void success(JsonArray jsonElements, Response response) {

                            dialog.dismiss();

                            for(int i=0; i<jsonElements.size(); i++) {
                                JsonObject deal = (JsonObject) jsonElements.get(i);
                                int count = (deal.get("count")).getAsInt();
                                int code = (deal.get("districtCode")).getAsInt();
                                String name="";
                                if(code ==0){
                                    name = "서울";
                                    stampSum = count;
                                }else{
                                    name = distList.get(code-1).getName();
                                    arrayList.add(new Item(code, name, count));
                                }

                                Log.e("distList",name);
                            }
                            init();
                            totalProgress();

                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            dialog.dismiss();

                            AlertDialog.Builder builder = new AlertDialog.Builder(ProgressActivity.this);
                            builder.setTitle("네트워크가 불안정합니다.")        // 제목 설정
                                    .setMessage("네트워크를 확인해주세요")        // 메세지 설정
                                    .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        //확인 버튼 클릭시 설정
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                        }
                                    });

                            AlertDialog dialog = builder.create();    // 알림창 객체 생성
                            dialog.show();    // 알림창 띄우기
                        }
                    });
                }
                catch (Throwable ex) {

                }
            }
        }).start();

    }

    void init(){
        adepter = new ProgressAdapter(getApplicationContext(), R.layout.model_progress, arrayList);

        // 리스트 표현하기
        listView = (ListView)findViewById(android.R.id.list);
        listView.setAdapter(adepter);

        // 리스트 디자인
        listView.setDivider(new ColorDrawable(Color.LTGRAY)); // 리스트간 경계선
        listView.setDividerHeight(1);
    }

    void totalProgress(){
        int  totalSum = 120 ;

        ProgressBar totalBar = (ProgressBar)findViewById(R.id.totalBar);
        totalBar.setProgress(stampSum / totalSum * 100);

        TextView tx = (TextView)findViewById(R.id.totalBarText);
        tx.setText(stampSum  + " / " + totalSum);
    }
}
