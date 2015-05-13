package com.chocoroll.seoultour.Main;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.chocoroll.seoultour.Model.District;
import com.chocoroll.seoultour.Model.Tour;
import com.chocoroll.seoultour.Adapter.TourAdapter;
import com.chocoroll.seoultour.R;
import com.chocoroll.seoultour.Retrofit.Retrofit;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ActionBarActivity implements TourAdapter.tourAdapterListner{

    ProgressDialog dialog;


    ArrayList<District> districtList;
    GoogleMap map;
    ListView listView;
    ArrayList<Tour> tourList;
    TourAdapter mAdapter;
    String curCode;


    SlidingDrawer slidingDrawer;
    Button slideHandleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature();
        setContentView(R.layout.activity_main);

        mapInit();



        listView = (ListView)findViewById(R.id.listView);
        tourList = new ArrayList<Tour>();
        mAdapter= new TourAdapter(this, R.layout.model_tour, tourList);

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setDivider(new ColorDrawable(Color.LTGRAY));
        listView.setDividerHeight(1);
        listView.setAdapter(mAdapter);


        slideHandleButton = (Button) findViewById(R.id.slideHandleButton);
        slidingDrawer = (SlidingDrawer) findViewById(R.id.SlidingDrawer);
        slidingDrawer.bringToFront();
        slidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
            }
        });

        slidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
            }
        });



    }

    void mapInit(){

        dialog = new ProgressDialog(this);
        dialog.setMessage("서울 지도를 가져오는 중입니다...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        listView.setVisibility(View.GONE);

        // 기본맵 셋팅
        map = ((MapFragment)getFragmentManager()
                .findFragmentById(R.id.map)).getMap();
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        map.setMyLocationEnabled(true);
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        // 서울 중앙으로 카메라 이동
        double latitude =0;
        double longitude =0;
        LatLng Loc = new LatLng(latitude, longitude);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(Loc, 16));

        // 서울 각 구를 불러와서 리스트에 추가
        districtList = new ArrayList<District>();

        String file = "district.json";
        String result = "";
        try {
            InputStream is = getAssets().open(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            result = new String(buffer, "utf-8");
            JSONObject json = new JSONObject(result);
            JSONArray jsonArray = json.getJSONArray("DESCRIPTION");
            for(int i=0; i< jsonArray.length(); i++){
                json = jsonArray.getJSONObject(i);
                // 시군구 코드
                String code = json.getString("SIG_CD");
                // 이름
                String name = json.getString("SIG_KOR_NM");
                // 위도
                double mapx = json.getDouble("LAT");
                //경도
                double mapy = json.getDouble("LNG");
                districtList.add(new District(code,name,mapx,mapy));
            }
        } catch (Exception e) {
        }

        // 각 구에 마커찍기
        for(int i=0; i<districtList.size(); i++){
            District district = districtList.get(i);
            LatLng pos = new LatLng(district.getMapx(), district.getMapy());
            MarkerOptions options = new MarkerOptions();
            options.position(pos);
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            options.title(district.getName());
            options.snippet(district.getCode());
            map.addMarker(options);

        }


        // 마커 클릭 리스너
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            public boolean onMarkerClick(Marker marker) {

                String code = marker.getSnippet();
                setMapTourList(code);

                // 마커 위치로 이동하며 확대
                LatLng pos = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 16));
                return false;
            }
        });

        dialog.dismiss();
    }

    void setMapTourList(String code){

        dialog = new ProgressDialog(this);
        dialog.setMessage("구의 관광지를 가져오는 중입니다...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        curCode = code;
        tourList.clear();
        // 여기서 구 code를 통해 관광지 리스트 정보를 모두 가져와서 추가한다.









        listView.setVisibility(View.VISIBLE);
        mAdapter.notifyDataSetChanged();

        // 각 관광지에에 마커찍기
        for(int i=0; i<tourList.size(); i++){
            Tour tour = tourList.get(i);
            LatLng pos = new LatLng(tour.getMapx(), tour.getMapy());
            MarkerOptions options = new MarkerOptions();
            options.position(pos);
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            options.title(tour.getName());
            map.addMarker(options);
        }

        dialog.dismiss();

    }


    @Override
    public void setMapTour(double x, double y) {

        // 마커 위치로 이동하며 확대
        LatLng pos = new LatLng(x,y);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 16));

    }

    @Override
    public void showDetailTour(final Tour tour) {

        dialog = new ProgressDialog(this);
        dialog.setMessage("관광지 정보를 가져오는 중입니다...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        // 투어리스트를 가져와서 슬라이딩 드로워로 띄운다.




        // 이미지 띄우기
        Bitmap bit = null;
        try {
            //웹사이트에 접속 (사진이 있는 주소로 접근)
            URL Url = new URL(tour.getThumbnail());
            // 웹사이트에 접속 설정
            URLConnection urlcon = Url.openConnection();
            // 연결하시오
            urlcon.connect();
            // 이미지 길이 불러옴
            int imagelength = urlcon.getContentLength();
            // 스트림 클래스를 이용하여 이미지를 불러옴
            BufferedInputStream bis = new BufferedInputStream(urlcon.getInputStream(), imagelength);
            // 스트림을 통하여 저장된 이미지를 이미지 객체에 넣어줌
            bit = BitmapFactory.decodeStream(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((ImageView) findViewById(R.id.thumbnailDetail)).setImageBitmap(bit);


        // 관광지 띄우기
        String str = "["+tour.getName()+"]";
        ((TextView) findViewById(R.id.tourTitle)).setText(str);

        // 스탬프찍기
        ((Button)findViewById(R.id.btnStamp)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStamp(tour.getContentID());
            }
        });


                // 방명록보기
        ((Button)findViewById(R.id.btnGuestBook)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuestBookDialog dialog = new GuestBookDialog(getApplicationContext(),curCode, tour.getContentID());
                dialog.show();

            }
        });


        dialog.dismiss();
        slideHandleButton.performClick();
    }


    void getStamp(final String contentID){

        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("스탬프를 정보를 가져오는 중입니다...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        final JsonObject info = new JsonObject();
        info.addProperty("code",curCode);
        info.addProperty("contentID",contentID);
        info.addProperty("phoneID","");
//        regist task1 = new regist();
//        task1.execute(this);

        new Thread(new Runnable() {
            public void run() {
                try {

                    RestAdapter restAdapter = new RestAdapter.Builder()
                            .setEndpoint(Retrofit.ROOT)  //call your base url
                            .build();
                    Retrofit sendreport = restAdapter.create(Retrofit.class); //this is how retrofit create your api
                    sendreport.checkStamp(info, new Callback<String>() {

                        @Override
                        public void success(final String result, Response response) {

                            dialog.dismiss();
                            if (result.equals("true")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
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
                            } else {
                                StampDialog dialog = new StampDialog(getApplicationContext(), curCode, contentID);
                                dialog.show();
                            }



                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            dialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

//
//    // 처음 실행 시 기기 아이디를 등록한다!!
//    public class regist extends AsyncTask<Context, Integer , String> {
//
//        @Override
//        protected String doInBackground(Context... params) {
//            // TODO Auto-generated method stub
//            String regId;
//
//            GCMRegistrar.checkDevice(params[0]);
//            GCMRegistrar.checkManifest(params[0]);
//
//            regId = GCMRegistrar.getRegistrationId(params[0]);
//
//            if (regId.equals("")) {
//                GCMRegistrar.register(params[0], PROJECT_ID);
//                regId = GCMRegistrar.getRegistrationId(params[0]);
//            }
//
//            return regId;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            // TODO Auto-generated method stub
//            super.onPostExecute(result);
//
//            registPhoneId(result);
//
//        }
//
//    }




    @SuppressWarnings("deprecation")
    @Override
    public void onBackPressed() {
        if (slidingDrawer.isOpened()) {
            slidingDrawer.close ();
        } else {

            super.onBackPressed();
        }
    }

}
