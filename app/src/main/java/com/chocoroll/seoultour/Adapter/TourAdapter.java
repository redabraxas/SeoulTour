package com.chocoroll.seoultour.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
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
import java.io.InputStream;
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
            new DownloadImageTask((ImageView) v.findViewById(R.id.thumbnail))
                    .execute(p.getThumbnail());


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
            ((Button)v.findViewById(R.id.btnDetail)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((tourAdapterListner)context).showDetailTour(p);
                }
            });
        }
        return v;
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
