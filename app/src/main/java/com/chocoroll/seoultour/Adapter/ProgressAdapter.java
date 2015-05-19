package com.chocoroll.seoultour.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chocoroll.seoultour.Model.Item;
import com.chocoroll.seoultour.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015-05-19.
 */
public class ProgressAdapter extends ArrayAdapter<Item> {
    ArrayList<Item> items;
    Context context;

    public ProgressAdapter(Context context, int resource, ArrayList<Item> objects) {
        super(context, resource, objects);

        this.context = context;     // 외부에서 사용하려면 따로 필요해서..
        items = objects;            // 따로 필요해서..
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;   // 각 아이템이 늘어날때, 계속 뷰로 늘리지 않고 swap해준다.
        if(v == null){
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.model_progress, null);
        }

        Item temp = items.get(position);
        if(temp != null)
        {
            TextView gu = (TextView) v.findViewById(R.id.gu);
            ProgressBar pb = (ProgressBar) v.findViewById(R.id.bar);
            TextView barNum = (TextView) v.findViewById(R.id.barNum);

            if( gu != null ) {
                gu.setText( temp.getName());
            }
            if( pb != null ) {

                int num = (int)((double)temp.getCnt() / temp.getTotal() * 100.0);
                pb.setProgress(num);
                Log.e("progress", String.valueOf(num));
                barNum.setText(temp.getCnt() + " / " + temp.getTotal());
            }
        }
        return v;
    }
}
