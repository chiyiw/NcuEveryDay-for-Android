package com.wangpeng.ncueveryday.news;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.wangpeng.ncueveryday.R;
import com.wangpeng.ncueveryday.R.id;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewsAdapter extends ArrayAdapter<Map<String, String>> {

    public NewsAdapter(Context context, List<Map<String, String>> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            // 获取当前的Activity
            Activity activity = (Activity) getContext();
            // 获取Activity的布局添加器
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.news_newsitem, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        // 接收数据
        Map<String, String> item = getItem(position);
        // 将数据呈现到UI控件
        holder.title.setText(item.get("newstitle"));
        //holder.content.setText(item.get("Content").substring(0,25).concat("..."));
        holder.createtime.setText(item.get("CreateTime"));

        String url = item.get("PicUrl");
        Glide.with(getContext())
                .load(url) // 地址
                .crossFade()
                .centerCrop() // 居中显示
                .override(200, 156) // 调整大小
                .into(holder.pic);

        return convertView;
    }

    static class ViewHolder {
        @Bind(id.pic) ImageView pic;
        @Bind(id.title) TextView title;
        //@Bind(id.content) TextView content;
        @Bind(id.createtime) TextView createtime;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public void add(Map<String, String> object) {
        super.add(object);
    }

    @Override
    public void addAll(Collection<? extends Map<String, String>> collection) {
        super.addAll(collection);
    }

    @Override
    public void insert(Map<String, String> object, int index) {
        super.insert(object, index);
    }

    /**
     * 从列表前部插入选项
     *
     * @param object
     */
    public void AddFromFront(ArrayList<Map<String, String>> object) {
        // 倒序插入
        for (int i = object.size() - 1; i >= 0; i--) {
            insert(object.get(i), 0);
        }
    }
    // 添加到列表后部
    public void AddToBack(ArrayList<Map<String, String>> object){
        for (int i = 0; i < object.size(); i++) {
            add(object.get(i));
        }
    }

    /**
     * 比较筛选出新添加的项目
     *
     * @param origin
     * @param addlist
     * @return
     */
    public ArrayList<Map<String, String>> DelSame(
            ArrayList<Map<String, String>> origin,
            ArrayList<Map<String, String>> addlist) {

        ArrayList<Map<String, String>> combine = new ArrayList<Map<String, String>>();
        int flag = 1; // 记录id在原列表中是否存在
        for (int i = 0; i < addlist.size(); i++) {
            flag = 1;
            for (int j = 0; j < origin.size(); j++) {
                if (origin.get(j).get("ID").equals(addlist.get(i).get("ID"))) {
                    flag = 0; // 说明列表中已经存在了，不需要新添加到列表
                    break;
                }
            }
            if (flag == 1) {
                combine.add(addlist.get(i));
            }
        }

        return combine;
    }

}
