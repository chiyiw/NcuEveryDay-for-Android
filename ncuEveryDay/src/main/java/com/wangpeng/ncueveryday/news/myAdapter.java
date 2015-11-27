package com.wangpeng.ncueveryday.news;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wangpeng.ncueveryday.R;
import com.wangpeng.ncueveryday.R.id;
import com.wangpeng.ncueveryday.R.layout;

public class myAdapter extends ArrayAdapter<Map<String, String>> {

	public myAdapter(Context context, List<Map<String, String>> objects) {
		super(context, 0, objects);
	}

	@SuppressLint({ "InflateParams", "ViewHolder" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 获取当前的Activity
		Activity activity = (Activity) getContext();
		// 获取Activity的布局添加器
		LayoutInflater inflater = activity.getLayoutInflater();
		// 获取布局文件
		View rootView = inflater.inflate(R.layout.news_newsitem, null);

		// 获取xml文件中的UI控件
		ImageView imageview = (ImageView) rootView.findViewById(R.id.pic);
		TextView title = (TextView) rootView.findViewById(R.id.title);
		TextView content = (TextView) rootView.findViewById(R.id.content);
		TextView createtime = (TextView) rootView.findViewById(R.id.createtime);

		// 接收数据
		Map<String, String> item = getItem(position);

		// 获取网络图片并呈现
		//GetWebImage getimage = new GetWebImage(item.get("PicUrl"), imageview);
		//getimage.SetImage();
		
		final DisplayImageOptions option = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisk(true) // 缓存目录(mnt/sdcard/Android/data/com.xxx.xxx/cache/)
		.build();
		// 使用ImageLoader加载网络图片
		ImageLoader.getInstance().displayImage(item.get("PicUrl"), imageview, option);

		// 将数据呈现到UI控件
		title.setText(item.get("newstitle"));
		content.setText(item.get("Content"));
		createtime.setText(item.get("CreateTime"));

		return rootView;
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

			// 当列表中不足20项时加入原来的项目
//			int size = combine.size();
//			if (size < 20) {
//				for (int k = 0; k < 20 - size; k++) {
//					if (k < origin.size()) {
//						combine.add(origin.get(k));
//					}
//				}
//			}
		}

		return combine;
	}
}
