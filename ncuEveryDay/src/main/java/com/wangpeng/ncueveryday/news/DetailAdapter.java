package com.wangpeng.ncueveryday.news;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wangpeng.ncueveryday.R;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wangpeng on 2016/1/19.
 */
public class DetailAdapter extends ArrayAdapter<Map<String, String>> {

    // 构造函数
    public DetailAdapter(Context context, List<Map<String, String>> objects) {
        super(context, 0, objects);
    }

    // 为Adapter的每一项设置View
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Activity activity = (Activity) getContext();

        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = activity.getLayoutInflater().inflate(R.layout.news_articleitem,
                    null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        Map<String, String> datas = getItem(position);


        if (datas.get("text").equals("")) {
            holder.viDetailText.setVisibility(View.GONE);
        } else {
            // 向布局设置数据
            if (!datas.get("text").startsWith("\u3000\u3000")) {
                holder.viDetailText.setText("\u3000\u3000" + datas.get("text"));
            }else {
                holder.viDetailText.setText(datas.get("text"));
            }
        }

        // 获取设备窗口显示
        Display display = activity.getWindow().getWindowManager()
                .getDefaultDisplay();
        // 定义显示尺寸
        DisplayMetrics metrics = new DisplayMetrics();
        // 获取显示尺寸
        display.getMetrics(metrics);
        float device_width = metrics.widthPixels;

        Glide.with(activity)
                .load(datas.get("image"))
                .crossFade() // 渐现动画
                .override((int) device_width, (int) (device_width * 0.618))
                .fitCenter()
                .centerCrop()
                .into(holder.viDetailImage);

        // 返回布局
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.vi_detail_text) TextView viDetailText;
        @Bind(R.id.vi_detail_image) ImageView viDetailImage;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
