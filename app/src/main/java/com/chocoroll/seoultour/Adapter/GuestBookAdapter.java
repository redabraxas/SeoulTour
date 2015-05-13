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

import com.chocoroll.seoultour.Model.GuestBook;
import com.chocoroll.seoultour.Model.Tour;
import com.chocoroll.seoultour.R;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by RA on 2015-05-13.
 */
public class GuestBookAdapter extends ArrayAdapter<GuestBook> {

    private ArrayList<GuestBook> items;
    private Context context;

    public GuestBookAdapter(Context context, int textViewResourceId, ArrayList<GuestBook> items) {
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
        final GuestBook p = items.get(position);
        if (p != null) {

            ((TextView)  v.findViewById(R.id.name)).setText(p.getName());
            ((TextView)  v.findViewById(R.id.date)).setText(p.getDate());
            ((TextView)  v.findViewById(R.id.content)).setText(p.getContent());

        }
        return v;
    }

}
