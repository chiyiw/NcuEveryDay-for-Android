package com.wangpeng.ncueveryday.news;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.os.AsyncTask;
import android.support.v4.app.Fragment;

public class mFragmentBase extends Fragment{
	/**
	 * 构造Adapter的List数据
	 * @param newslist 新闻list数据
	 * @return
	 */
	public ArrayList<Map<String,String>> getlist(ArrayList<NewsItem> newslist){
		
		ArrayList<Map<String,String>> arraylist = new ArrayList<Map<String,String>>();
		
		for (int i = 0; i < newslist.size(); i++) {
			
			HashMap<String, String> newsitem = new HashMap<String, String>();
			
			newsitem.put("ID", newslist.get(i).id);
			newsitem.put("newstitle", newslist.get(i).title);
			newsitem.put("PicUrl", newslist.get(i).PicUrl);
			newsitem.put("Content", newslist.get(i).content);
			newsitem.put("CreateTime", newslist.get(i).CreateTime);
			newsitem.put("rowcount", newslist.get(i).rowcount);
			newsitem.put("pagecount", newslist.get(i).pagecount);
			
			arraylist.add(newsitem);
		}
		return arraylist;
	}
	/**
	 * 获取Assets中的文件内容，返回字符串
	 * @param filename 文件路径
	 * @return String 文件中的字符串内容
	 */
	public String getAssetsData(String filename) {

		String data = "";
		try {
			InputStream is = getResources().getAssets().open(filename);
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader bfr = new BufferedReader(isr);

			String line = "";
			while ((line = bfr.readLine()) != null) {
				data += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	/**
	 * 获取url的网络字符流
	 * @param url 数据地址
	 * @return String 型的字符串
	 */
	public String getHttpJson(String url){
		String result = "";
	    try {
	        result = new AsyncTask<String, Void, String>() {

	            @Override
	            protected String doInBackground(String... params) {
	                String webstring = "";
	                try {
	                    URL fileurl = new URL(params[0]);
	                    HttpURLConnection con;
	                    InputStream is;
	                    try {
	                        // 开始请求网络数据
	                        con = (HttpURLConnection) fileurl.openConnection();
	                        // 设置请求超时
	                        con.setConnectTimeout(6000);
	                        con.setDoInput(true);
	                        // 设置是否使用缓存
	                        con.setUseCaches(true);

	                        is = con.getInputStream();
	                        InputStreamReader isr = new InputStreamReader(is,
									"utf-8");
							BufferedReader br = new BufferedReader(isr);

							String line;

							while ((line = br.readLine()) != null) {
								System.out.println(line);
								webstring += line;
							}
	                        // 解析出图片
	                        is.close();

	                    } catch (IOException e1) {
	                        e1.printStackTrace();
	                    }
	                } catch (MalformedURLException e) {
	                    e.printStackTrace();
	                }
	                return webstring;
	            }

	        // 通过AsyncTask的get()方法获得异步任务的返回值
	        // get()的返回值对应于AsyncTask的第三个参数
	        }.execute(url).get();
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    } catch (ExecutionException e) {
	        e.printStackTrace();
	    }
	    return result;
	}

	/**
	 * json数据转化到ArrayList项
	 * @param json 需要转化的json字符串
	 * @return list 存储数据的List列表项
	 */
	public ArrayList<NewsItem> Json2list(String json) {
		
		ArrayList<NewsItem> list = new ArrayList<NewsItem>();
		try {
			JSONArray arr = new JSONArray(json);

			for (int i = 0; i < arr.length(); i++) {
				NewsItem news = new NewsItem();
				JSONObject obj = arr.getJSONObject(i);

				news.id = obj.getString("ID");
				news.content = obj.getString("Content");
				news.title = obj.getString("newstitle");
				news.PicUrl = obj.getString("PicUrl");
				news.CreateTime = obj.getString("CreateTime");
				news.rowcount = obj.getString("rowcount");
				news.pagecount = obj.getString("pagecount");

				list.add(news);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * ArrayList数据转化到json项
	 * @param list 需要转化的List列表项
	 * @return json 数据生成的json字符串
	 */
	public String list2json(ArrayList<NewsItem> list){
		String json = "";
		JSONArray arr = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			try {
				JSONObject obj = new JSONObject();
				obj.put("ID", list.get(i).id);
				obj.put("Content", list.get(i).content);
				obj.put("newstitle", list.get(i).title);
				obj.put("PicUrl", list.get(i).PicUrl);
				obj.put("CreateTime", list.get(i).CreateTime);
				obj.put("rowcount", list.get(i).rowcount);
				obj.put("pagecount", list.get(i).pagecount);
				
				arr.put(obj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		json = arr.toString();
		return json;
	}

}
