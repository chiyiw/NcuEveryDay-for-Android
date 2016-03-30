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


import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;
import com.wangpeng.ncueveryday.mFileOperate;

public class mFragmentBase extends Fragment{

	private PullToRefreshListView newslistview; // 新闻ListView
	private ArrayList<NewsItem> newslist; // 新闻列表数据
	private NewsAdapter adapter = null;
	private String FILENAME = ""; // 离线保存新闻列表的文件名
	private String newsPartId = ""; // 该模块新闻id
	int pageindex = 1; // 加载的页数，加载更多累加

	boolean isFirstOpen = false; // 应用程序是否是第一次打开

	public void initView(PullToRefreshListView listview, String partId, final String filename){

		this.newslistview = listview;
		this.FILENAME = filename;
		this.newsPartId = partId;

		// 获取json数据
		// String json = getAssetsData("list.json");
		// getHttpJson("http://www.ncuhome.cn/NewIndex2013/AjaxGetList.ashx?partID=394&pageindex=1&pagesize=5");
		mFileOperate fileOprt = new mFileOperate(getActivity(), FILENAME);
		String json = fileOprt.readFromFile();

		// 如果是初次打开应用，则加载预装的数据
		if (json.equals("[]") || json.equals("")) {
			json = getAssetsData("list.json");
			isFirstOpen = true;
		}

		//System.out.println("json:----"+json);

		// json转化为list
		newslist = Json2list(json);
		// 呈现数据到UI布局
		adapter = new NewsAdapter(this.getActivity(), getlist(newslist));
		newslistview.setAdapter(adapter);

		// 文章列表项点击事件
		newslistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {

				String title = newslist.get(position - 1).title;
				String createtime = newslist.get(position - 1).CreateTime;
				String articleid = newslist.get(position - 1).id;

				Intent intent = new Intent(getActivity(), ActivityArticleDetail.class);
				intent.putExtra("articleid", articleid); // 文章id
				intent.putExtra("title", title); // 文章标题
				intent.putExtra("createtime", createtime); // 文章发布时间
				getActivity().startActivity(intent);
			}
		});

		newslistview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				new AsyncTask<String, Void, String>() {

					// 开始异步任务时调用
					@Override
					protected String doInBackground(String... params) {
						String webstring = "";
						try {
							URL fileurl = new URL(params[0]);
							HttpURLConnection con;
							InputStream is;
							try {
								// 开始请求网络数据
								con = (HttpURLConnection) fileurl
										.openConnection();
								// 设置请求超时
								con.setConnectTimeout(6000);
								con.setDoInput(true);
								// 设置是否使用缓存
								con.setUseCaches(true);

								is = con.getInputStream();
								InputStreamReader isr = new InputStreamReader(
										is, "utf-8");
								BufferedReader br = new BufferedReader(isr);

								String line;

								while ((line = br.readLine()) != null) {
									//System.out.println(line);
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

					// 异步任务执行完毕时更新UI时调用
					@Override
					protected void onPostExecute(String result) {

// 						Logger.json(result);

						ArrayList<Map<String, String>> addList = getlist(Json2list(result));
						// 获得缓存区的原始数据
						mFileOperate fileOprt = new mFileOperate(getActivity(),
								FILENAME);
						String json = fileOprt.readFromFile();
						// json转化为list
						newslist = Json2list(json);

						if (isFirstOpen){
							adapter.clear();
							isFirstOpen = false;
						}

						// 剔除了相同ID的文章
						addList = adapter.DelSame(getlist(newslist), addList);
						// 向UI中列表前部逐渐加入项目
						adapter.AddFromFront(addList);

						Toast.makeText(getActivity(), "刷新成功",
								Toast.LENGTH_SHORT).show();

						// 将新添加的项加入到保存项,注意是倒叙加入
						for (int i = addList.size() - 1; i >= 0; i--) {
							NewsItem item = new NewsItem();
							item.id = addList.get(i).get("ID");
							item.content = addList.get(i).get("Content");
							item.PicUrl = addList.get(i).get("PicUrl");
							item.CreateTime = addList.get(i).get("CreateTime");
							item.title = addList.get(i).get("newstitle");
							item.rowcount = addList.get(i).get("rowcount");
							item.pagecount = addList.get(i).get("pagecount");
							newslist.add(0, item);
						}

						// 更新缓存文件
						fileOprt.writeToFile(list2json(newslist));
						adapter.notifyDataSetChanged();

						// 执行刷新结束，下拉弹回
						newslistview.onRefreshComplete();
						super.onPostExecute(result);
					}
					// 异步任务开始执行
				}.execute("http://www.ncuhome.cn/NewIndex2013/AjaxGetList.ashx?pageindex=1&pagesize=10&partID="+newsPartId);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

				new AsyncTask<String, Void, String>() {

					// 开始异步任务时调用
					@Override
					protected String doInBackground(String... params) {
						String webstring = "";
						try {
							URL fileurl = new URL(params[0]);
							HttpURLConnection con;
							InputStream is;
							try {
								// 开始请求网络数据
								con = (HttpURLConnection) fileurl
										.openConnection();
								// 设置请求超时
								con.setConnectTimeout(6000);
								con.setDoInput(true);
								// 设置是否使用缓存
								con.setUseCaches(true);

								is = con.getInputStream();
								InputStreamReader isr = new InputStreamReader(
										is, "utf-8");
								BufferedReader br = new BufferedReader(isr);

								String line;

								while ((line = br.readLine()) != null) {
									//System.out.println(line);
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

					// 异步任务执行完毕时更新UI时调用
					@Override
					protected void onPostExecute(String result) {

						ArrayList<Map<String, String>> addList = getlist(Json2list(result));
						// 下次上拉加载更多时使用pageindex
						if (addList.size() == 10) {
							pageindex++;
						}
						// 获得缓存区的原始数据
						mFileOperate fileOprt = new mFileOperate(getActivity(),
								FILENAME);
						String json = fileOprt.readFromFile();
						// json转化为list
						//newslist = Json2list(json);

						// 剔除了相同ID的文章
						addList = adapter.DelSame(getlist(newslist), addList);
						// 向UI中列表前部逐渐加入项目
						adapter.AddToBack(addList);

						// 将新添加的项加入到保存项
						for (int i = 0; i < addList.size(); i++) {
							NewsItem item = new NewsItem();
							item.id = addList.get(i).get("ID");
							item.content = addList.get(i).get("Content");
							item.PicUrl = addList.get(i).get("PicUrl");
							item.CreateTime = addList.get(i).get("CreateTime");
							item.title = addList.get(i).get("newstitle");
							item.rowcount = addList.get(i).get("rowcount");
							item.pagecount = addList.get(i).get("pagecount");
							newslist.add(item);
						}

						adapter.notifyDataSetChanged();

						Toast.makeText(getActivity(), "加载成功",
								Toast.LENGTH_SHORT).show();

						// 执行刷新结束，弹回
						newslistview.onRefreshComplete();
						super.onPostExecute(result);
					}
					// 异步任务开始执行
				}.execute("http://www.ncuhome.cn/NewIndex2013/AjaxGetList.ashx?pagesize=10&partID="+newsPartId+"&&pageindex=" + (pageindex + 1));
			}
		});
	}

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
