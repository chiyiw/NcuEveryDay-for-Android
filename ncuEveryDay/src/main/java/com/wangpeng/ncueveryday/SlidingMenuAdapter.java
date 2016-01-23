package com.wangpeng.ncueveryday;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wangpeng on 2016/1/20.
 */
public class SlidingMenuAdapter extends ArrayAdapter<Map<String, Object>> {
    public SlidingMenuAdapter(Context context, List<Map<String, Object>> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Activity activity = (Activity) getContext();
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.slidingmenuitem, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        Map<String, Object> map = getItem(position);
        holder.slidingText.setText(map.get("text").toString());
        holder.slidingImage.setImageResource(Integer.parseInt(map.get("image").toString()));

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.sliding_image) ImageView slidingImage;
        @Bind(R.id.sliding_text) TextView slidingText;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
