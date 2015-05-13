package com.chocoroll.seoultour.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chocoroll.seoultour.Model.Tour;
import com.chocoroll.seoultour.R;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by RA on 2015-05-13.
 */
public class TourAdapter extends ArrayAdapter<Tour> {

    public interface tourAdapterListner{
        void setMapTour(double x, double y);
        void showDetailTour(Tour tour);
    }

    private ArrayList<Tour> items;
    private Context context;

    public TourAdapter(Context context, int textViewResourceId, ArrayList<Tour> items) {
        super(context, textViewResourceId, items);
        this.items = items;
        this.context = context;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.model_tour, null);
        }
        final Tour p = items.get(position);
        if (p != null) {

            // 웹 이미지 띄우기
            Bitmap bit = null;
            try {
                //웹사이트에 접속 (사진이 있는 주소로 접근)
                URL Url = new URL(p.getThumbnail());
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
            ((ImageView) v.findViewById(R.id.thumbnail)).setImageBitmap(bit);

            // 이름 띄우기
            String str = "["+p.getName()+"]";
            ((TextView)  v.findViewById(R.id.name)).setText(str);

            // 지도 위치보기
            ((Button)v.findViewById(R.id.btnPos)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((tourAdapterListner)context).setMapTour(p.getMapx(), p.getMapy());
                }
            });

            // 상세보기
            ((Button)v.findViewById(R.id.btnPos)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((tourAdapterListner)context).showDetailTour(p);
                }
            });
        }
        return v;
    }


}
