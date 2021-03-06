package com.chocoroll.seoultour.Main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.chocoroll.seoultour.R;
import com.chocoroll.seoultour.Retrofit.Retrofit;
import com.google.gson.JsonObject;


import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by RA on 2015-05-13.
 */
public class StampDialog extends Dialog {
    ProgressDialog dialog;


    Context context;
    String code;
    String districtName;
    String contentID;
    String tourName;

    ImageView imageStamp;

    public StampDialog(Context context, String code, String name, String contentID, String tourName) {
        super(context);
        this.context = context;
        this.code= code;
        this.districtName = name;
        this.contentID = contentID;
        this.tourName = tourName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_stamp);

        setTitle(districtName+" > "+tourName);

        imageStamp = (ImageView) findViewById(R.id.imageStamp);
        imageStamp.setTag(R.string.stamp_state, "ready");

        getStamp();

        imageStamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(imageStamp.getTag(R.string.stamp_state).equals("done")){
                    dialog.dismiss();

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("스탬프 찍기 실패")        // 제목 설정
                            .setMessage("이미 스탬프를 찍으셨어요~")        // 메세지 설정
                            .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                // 확인 버튼 클릭시 설정
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            });
                    AlertDialog dialog = builder.create();    // 알림창 객체 생성
                    dialog.show();    // 알림창 띄우기
                }else{
                    checkStamp();
                }

            }
        });

    }

    void checkStamp(){

        dialog = new ProgressDialog(context);
        dialog.setMessage("스탬프를 찍는 중입니다...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();


        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneID = telephony.getDeviceId();    //device id

        final JsonObject info = new JsonObject();
        info.addProperty("code",code);
        info.addProperty("name",districtName);
        info.addProperty("contentID",contentID);
        info.addProperty("phoneID",phoneID);


        new Thread(new Runnable() {
            public void run() {
                try {

                    RestAdapter restAdapter = new RestAdapter.Builder()
                            .setEndpoint(Retrofit.ROOT)  //call your base url
                            .build();
                    Retrofit sendreport = restAdapter.create(Retrofit.class); //this is how retrofit create your api
                    sendreport.checkStamp(info, new Callback<String>() {

                        @Override
                        public void success(String result, Response response) {

                            dialog.dismiss();

                            if(result.equals("success")){

                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("스탬프 찍기 성공")        // 제목 설정
                                        .setMessage("스탬프를 찍으셨어요~")        // 메세지 설정
                                        .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            // 확인 버튼 클릭시 설정
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                            }
                                        });
                                AlertDialog dialog = builder.create();    // 알림창 객체 생성
                                dialog.show();    // 알림창 띄우기

                                imageStamp.setImageResource(R.drawable.stampred);
                                imageStamp.setTag(R.string.stamp_state, "done");
                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("스탬프 찍기 실패")        // 제목 설정
                                        .setMessage("네트워크 오류로 스탬프를 찍기 실패했어요")        // 메세지 설정
                                        .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            // 확인 버튼 클릭시 설정
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                            }
                                        });
                                AlertDialog dialog = builder.create();    // 알림창 객체 생성
                                dialog.show();    // 알림창 띄우기

                            }



                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            dialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("네트워크가 불안정합니다.")        // 제목 설정
                                    .setMessage("네트워크를 확인해주세요")        // 메세지 설정
                                    .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        // 확인 버튼 클릭시 설정
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




    void getStamp(){

        dialog = new ProgressDialog(context);
        dialog.setMessage("스탬프를 정보를 가져오는 중입니다...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneID = telephony.getDeviceId();    //device id

        final JsonObject info = new JsonObject();
        info.addProperty("code", code);
        info.addProperty("contentID",contentID);
        info.addProperty("phoneID",phoneID);



        new Thread(new Runnable() {
            public void run() {
                try {

                    RestAdapter restAdapter = new RestAdapter.Builder()
                            .setEndpoint(Retrofit.ROOT)  //call your base url
                            .build();
                    Retrofit sendreport = restAdapter.create(Retrofit.class); //this is how retrofit create your api
                    sendreport.getStamp(info, new Callback<String>() {

                        @Override
                        public void success(final String result, Response response) {

                            dialog.dismiss();
                            if (result.equals("done")) {
                                imageStamp.setImageResource(R.drawable.stampred);
                                imageStamp.setTag(R.string.stamp_state, "done");
                            } else {
                                imageStamp.setImageResource(R.drawable.stampgray);
                                imageStamp.setTag(R.string.stamp_state, "ready");
                            }


                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            dialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("네트워크가 불안정합니다.")        // 제목 설정
                                    .setMessage("네트워크를 확인해주세요")        // 메세지 설정
                                    .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        // 확인 버튼 클릭시 설정
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
}
