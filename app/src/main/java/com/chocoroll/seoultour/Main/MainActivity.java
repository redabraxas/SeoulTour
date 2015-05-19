package com.chocoroll.seoultour.Main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.chocoroll.seoultour.Adapter.TourAdapter;
import com.chocoroll.seoultour.Model.District;
import com.chocoroll.seoultour.Model.Tour;
import com.chocoroll.seoultour.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends Activity implements TourAdapter.tourAdapterListner{

    ProgressDialog dialog;


    ArrayList<District> districtList= new ArrayList<District>();;
    GoogleMap map;
    ListView listView;
    ArrayList<Tour> tourList  = new ArrayList<Tour>();
    TourAdapter mAdapter;
    String curCode;


    SlidingDrawer slidingDrawer;
    Button slideHandleButton;


    boolean homeFlag = true;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button btnProgress = (Button) findViewById(R.id.btnProgress);
        btnProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProgressActivity.class);
                intent.putParcelableArrayListExtra("districtList", districtList);
                startActivity(intent);
            }
        });


        Button btnHome = (Button) findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeFlag = true;
                map.clear();
                mapInit();

            }
        });

        listView = (ListView)findViewById(R.id.listView);
        mAdapter= new TourAdapter(this, R.layout.model_tour, tourList);

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setDivider(new ColorDrawable(Color.LTGRAY));
        listView.setDividerHeight(1);
        listView.setAdapter(mAdapter);


        slideHandleButton = (Button) findViewById(R.id.slideHandleButton);
        slidingDrawer = (SlidingDrawer) findViewById(R.id.SlidingDrawer);
        slidingDrawer.bringToFront();


        mapInit();

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
        map.setMyLocationEnabled(true);
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        // 서울 중앙으로 카메라 이동
        double latitude =37.538153;
        double longitude =126.988177;
        LatLng Loc = new LatLng(latitude, longitude);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(Loc, 12));

        // 서울 각 구를 불러와서 리스트에 추가
        String file = "district.json";
        String result = "";
        try {
            InputStream is = getAssets().open(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            result = new String(buffer, "utf-8");

            JSONArray jsonArray = new JSONArray(result);
            for(int i=0; i< jsonArray.length(); i++){
                JSONObject json = jsonArray.getJSONObject(i);
                // 시군구 코드
                String code = json.getString("OBJECTID");
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
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.district_marker));
            map.addMarker(options);
        }



        // 마커 클릭 리스너
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            public boolean onMarkerClick(Marker marker) {

                homeFlag = false;
                String code = marker.getSnippet();
                setMapTourList(code);

                // 마커 위치로 이동하며 확대
                LatLng pos = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 18));
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

        // 요청할 API     / 지역코드 조회:areaCode / 지역기반 관광정보 조회:areaBasedList / 공통정보 조회:detailCommon
        String url;
        url = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/";
        url += "areaBasedList?";
        //인증키
        url += "ServiceKey=gm3kVO0YC2KCvStmz2Ljk%2FRRhLwLNOdNcAuW%2Fdsv9uUOHAFWDyFcmwAfXiLbJa4lmQQ5D778Q619jgPmG8kdNg%3D%3D";
        //서울(areaCode=1)과 시군구코드정보(sigunguCode) 입력
        url += "&areaCode=1&sigunguCode="+code+"&numOfRows=30&pageNo=1";
        //기기 OS, 프로그램 이름
        url += "&MobileOS=ETC&MobileApp=Testing";

        new GetTourInfoTask().execute(url);



    }


    @Override
    public void setMapTour(double x, double y) {

        // 마커 위치로 이동하며 확대
        LatLng pos = new LatLng(x, y);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 16));

    }

    @Override
    public void showDetailTour(final Tour tour) {

        dialog = new ProgressDialog(this);
        dialog.setMessage("관광지 정보를 가져오는 중입니다...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();


        // 이미지 띄우기
        new DownloadImageTask((ImageView) findViewById(R.id.thumbnailDetail))
                .execute(tour.getThumbnail());

        // 관광지 띄우기
        String str = "["+tour.getName()+"]";
        ((TextView) findViewById(R.id.tourTitle)).setText(str);

        // 스탬프찍기
        ((Button)findViewById(R.id.btnStamp)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StampDialog dialog = new StampDialog(MainActivity.this, curCode, districtList.get(Integer.valueOf(curCode)-1).getName(),
                        tour.getContentID(), tour.getName());
                dialog.show();
            }
        });


                // 방명록보기
        ((Button)findViewById(R.id.btnGuestBook)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuestBookDialog dialog = new GuestBookDialog(MainActivity.this,curCode, tour.getContentID());
                dialog.show();

            }
        });

        // 주소 띄우기
        ((TextView) findViewById(R.id.tour_addr)).setText(tour.getAddr());

        // 전화번호 띄우기
        ((TextView) findViewById(R.id.tour_tel)).setText(tour.getTel());

        // 소개 띄우기
        ((TextView) findViewById(R.id.tour_overview)).setText(tour.getOverView());



        dialog.dismiss();
        slideHandleButton.performClick();
    }


    // AsyncTask클래스  // html문서 파싱하기 _ 소스 text로 얻어오기
    public class GetTourInfoTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {   // 메인 실행 단계
            try {
                return (String) downloadUrl((String) urls[0]);
            } catch (IOException e) {
                return "다운로드 실패";
            }
        }

        private String downloadUrl(String myurl) throws IOException {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(myurl);   // 입력된 url
                conn = (HttpURLConnection) url.openConnection(); // 리소스 연결
                BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());   // byte단위로 저장
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "utf-8")); // 문자 단위로 변환
                String line = null;
                String page = "";
                while ((line = bufreader.readLine()) != null) {   // 문자를 줄단위로
                    page += line;
                }
                return page;
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {

            tourList.clear();


            String[] tag = {"contentid", "title", "addr1", "mapx", "mapy", "tel", "contenttypeid", "firstimage2"};


            try {
                // 파서 생성
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(new StringReader(result));

                // 이벤트 가져오기
                int eventType = xpp.getEventType();
                boolean bSet = false;
                String s_tag = "";

                String contentID="", thumbnail = "", addr="", tel="", title="";
                int contentTypeID=0;
                Double mapx = 0.0, mapy=0.0;


                while (eventType != XmlPullParser.END_DOCUMENT) {


                    if (eventType == XmlPullParser.START_DOCUMENT) {

                        ;
                    } else if (eventType == XmlPullParser.START_TAG) {
                        String tag_name = xpp.getName();
                        // 태그가 name 혹은 address인 경우 set을 true로
                        for(int i=0; i<tag.length; i++)
                            if (tag_name.equals(tag[i])) {
                                bSet = true;
                                s_tag = tag[i];
                                break;
                            }

                    } else if (eventType == XmlPullParser.TEXT) {

                        if (bSet) {
                            String data = xpp.getText();

                            switch (s_tag) {
                                case "contentid":
                                    contentID = data;
                                    break;
                                case "addr1":
                                    addr = data;
                                    break;
                                case "mapx":
                                    mapx = Double.parseDouble(data);
                                    break;
                                case "mapy":
                                    mapy = Double.parseDouble(data);
                                    break;
                                case "tel":
                                    tel = data;
                                    break;
                                case "firstimage2":
                                    thumbnail = data;
                                    break;
                                case "title":
                                    title = data;
                                    break;
                                case "contenttypeid":
                                    contentTypeID = Integer.valueOf(data);
                                    break;
                                default:
                                    break;

                            }


                            bSet = false;
                        }
                    } else if (eventType == XmlPullParser.END_TAG) {

                        String tag_name=xpp.getName();
                        if(tag_name.equals("item")){

                            if(contentTypeID != 39 && contentTypeID != 28){
                                tourList.add(new Tour(title, thumbnail,mapy,mapx, contentID, contentTypeID, addr, tel));
                            }

                        }

                    }
                    eventType = xpp.next();
                }
            }
            catch (Exception e) {
                Log.e("Exception", e.getMessage());
            }



            map.clear();

            // 각 관광지에에 마커찍기
            for(int i=0; i<tourList.size(); i++){

                Tour tour = tourList.get(i);

                LatLng pos = new LatLng(tour.getMapx(), tour.getMapy());


                MarkerOptions options = new MarkerOptions();
                options.position(pos);
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                options.title(tour.getName());
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.tour_marker));
                map.addMarker(options);

                String url;
                url = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailCommon?";
                url += "ServiceKey=gm3kVO0YC2KCvStmz2Ljk%2FRRhLwLNOdNcAuW%2Fdsv9uUOHAFWDyFcmwAfXiLbJa4lmQQ5D778Q619jgPmG8kdNg%3D%3D";
                url += "&contentId=" + tour.getContentID() + "&overviewYN=Y";
                url += "&MobileOS=ETC&MobileApp=Testing";

                new GetTourOverviewTask().execute(url, String.valueOf(i));

            }



            // 마커 클릭 리스너
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                public boolean onMarkerClick(Marker marker) {

                    // 마커 위치로 이동하며 확대
                    LatLng pos = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 18));
                    return false;
                }
            });


            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(mAdapter);
            dialog.dismiss();







        }
    }
    public class GetTourOverviewTask extends AsyncTask<String, Void, String> {
        int index = 0;

        @Override
        protected String doInBackground(String... urls) {   // 메인 실행 단계
            try {
                index = Integer.valueOf(urls[1]);
                return (String) downloadUrl((String) urls[0]);
            } catch (IOException e) {
                return "다운로드 실패";
            }
        }

        private String downloadUrl(String myurl) throws IOException {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(myurl);   // 입력된 url
                conn = (HttpURLConnection) url.openConnection(); // 리소스 연결
                BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());   // byte단위로 저장
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "utf-8")); // 문자 단위로 변환
                String line = null;
                String page = "";
                while ((line = bufreader.readLine()) != null) {   // 문자를 줄단위로
                    page += line;
                }
                return page;
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                // 파서 생성
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(new StringReader(result));

                // 이벤트 가져오기
                int eventType = xpp.getEventType();
                boolean bSet = false;

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        ;
                    } else if (eventType == XmlPullParser.START_TAG) {
                        String tag_name = xpp.getName();
                        // 태그가 name 혹은 address인 경우 set을 true로
                        if (tag_name.equals("overview"))
                            bSet = true;
                    } else if (eventType == XmlPullParser.TEXT) {
                        if (bSet) {
                            String data = xpp.getText();

                            tourList.get(index).setOverView(data);

                            bSet = false;

                        }
                    } else if (eventType == XmlPullParser.END_TAG) {
                        ;
                    }
                    eventType = xpp.next();
                }
            }
            catch (Exception e) {
                Log.e("Exception", e.getMessage());
            }

        }
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onBackPressed() {
        if (slidingDrawer.isOpened()) {
            slidingDrawer.close ();
        } else if(!homeFlag){
            map.clear();
            mapInit();
        }else{
            super.onBackPressed();
        }


    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
