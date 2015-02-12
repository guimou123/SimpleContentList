package com.guil.moura.simplecontentlist.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.guil.moura.simplecontentlist.R;
import com.guil.moura.simplecontentlist.network.VolleyHelper;
import com.guil.moura.simplecontentlist.model.Row;

import java.util.ArrayList;

public class FactsAdapter extends ArrayAdapter<Row> {

    private Context mContext;

    public FactsAdapter(Context context, ArrayList<Row> rows) {
        super(context, 0, rows);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Row row = getItem(position);

        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.desc = (TextView) convertView.findViewById(R.id.content);
            holder.image = (NetworkImageView) convertView.findViewById(R.id.network_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(row.getTitle());
        holder.desc.setText(row.getDescription());
        holder.image.setImageUrl(row.getImageHref(), VolleyHelper.getInstance(mContext).getImageLoader());
        return convertView;
    }


    private static class ViewHolder {
        TextView title;
        TextView desc;
        NetworkImageView image;
    }

}
