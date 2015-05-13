package com.chocoroll.seoultour.Main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.chocoroll.seoultour.Adapter.GuestBookAdapter;
import com.chocoroll.seoultour.Model.GuestBook;
import com.chocoroll.seoultour.R;
import com.chocoroll.seoultour.Retrofit.Retrofit;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by RA on 2015-05-13.
 */
public class GuestBookDialog extends Dialog {
    ProgressDialog dialog;


    Context context;

    ListView listView;
    ArrayList<GuestBook> guestBookList;
    GuestBookAdapter mAdapter;

    String code;
    String contentID;

    public GuestBookDialog(Context context, String code, String contentID) {
        super(context);
        this.context = context;
        this.code= code;
        this.contentID = contentID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_guestbook);



        listView = (ListView)findViewById(R.id.listViewGuestBook);

        guestBookList = new ArrayList<GuestBook>();
        mAdapter= new GuestBookAdapter(context, R.layout.model_guestbook, guestBookList);

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setDivider(new ColorDrawable(Color.YELLOW));
        listView.setDividerHeight(1);


        Button btnOK = (Button) findViewById(R.id.btn_ok);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = ((EditText)findViewById(R.id.editname)).getText().toString();
                String content = ((EditText)findViewById(R.id.editcontent)).getText().toString();

                JsonObject info = new JsonObject();
                info.addProperty("name",name);
                info.addProperty("content",content);
                sendGuestBook(info);



            }
        });


        getGuestBook();
    }

    void getGuestBook(){

        dialog = new ProgressDialog(context);
        dialog.setMessage("방명록을 가져오는 중입니다...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        guestBookList.clear();

        final JsonObject info = new JsonObject();
        info.addProperty("code",code);
        info.addProperty("contentID",contentID);
        sendGuestBook(info);


        new Thread(new Runnable() {
            public void run() {
                try {

                    RestAdapter restAdapter = new RestAdapter.Builder()
                            .setEndpoint(Retrofit.ROOT)  //call your base url
                            .build();
                    Retrofit sendreport = restAdapter.create(Retrofit.class); //this is how retrofit create your api
                    sendreport.getGuestBook(info, new Callback<JsonArray>() {

                        @Override
                        public void success(JsonArray jsonElements, Response response) {

                            dialog.dismiss();

                            for (int i = 0; i < jsonElements.size(); i++) {
                                JsonObject guestbook = (JsonObject) jsonElements.get(i);
                                String name = (guestbook.get("name")).getAsString();
                                String date = (guestbook.get("date")).getAsString();
                                String content = (guestbook.get("content")).getAsString();

                                guestBookList.add(new GuestBook(name,date,content));

                            }

                            listView.setAdapter(mAdapter);
                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            dialog.dismiss();
                            Log.e("error", retrofitError.getCause().toString());
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




    void sendGuestBook(final JsonObject info){


        dialog = new ProgressDialog(context);
        dialog.setMessage("방명록을 작성하는 중입니다...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();


        new Thread(new Runnable() {
            public void run() {
                try {

                    RestAdapter restAdapter = new RestAdapter.Builder()
                            .setEndpoint(Retrofit.ROOT)  //call your base url
                            .build();
                    Retrofit sendreport = restAdapter.create(Retrofit.class); //this is how retrofit create your api
                    sendreport.sendGuestBook(info, new Callback<String>() {

                        @Override
                        public void success(String result, Response response) {

                            dialog.dismiss();
                            getGuestBook();

                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            dialog.dismiss();
                            Log.e("error", retrofitError.getCause().toString());
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