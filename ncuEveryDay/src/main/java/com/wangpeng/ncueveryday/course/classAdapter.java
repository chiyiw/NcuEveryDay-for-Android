package com.wangpeng.ncueveryday.course;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wangpeng.ncueveryday.R;

public class classAdapter extends BaseAdapter{
	private Context context;
	private ArrayList<classindex> datas;

	public classAdapter(Context context, ArrayList<classindex> datas){
		this.context  = context;
		this.datas = datas;
	}
	public int getCount() {
		return datas.size();
	}

	public Object getItem(int position) {
		return datas.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ViewHolder")
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		View v = inflater.inflate(R.layout.cou_classitem, null);
		TextView tv = (TextView) v.findViewById(R.id.classitem);
		tv.setText(datas.get(position).class_name);
		return v;
	}

}
