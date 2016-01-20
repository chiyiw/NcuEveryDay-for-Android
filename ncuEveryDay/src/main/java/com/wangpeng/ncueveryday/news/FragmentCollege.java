package com.wangpeng.ncueveryday.news;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wangpeng.ncueveryday.R;
import com.wangpeng.ncueveryday.mFileOperate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class FragmentCollege extends mFragmentBase {

	private PullToRefreshListView newslistview; // 新闻列表
	private ArrayList<NewsItem> newslist; // 新闻列表数据
	private NewsAdapter adapter = null;
	private String FILENAME = "list1.json";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.news_college, container, false);

		newslistview = (PullToRefreshListView) v
				.findViewById(R.id.newslistview);
		newslistview.setMode(Mode.BOTH);

		// 获取json数据
		// String json = getAssetsData("list.json");
		// getHttpJson("http://www.ncuhome.cn/NewIndex2013/AjaxGetList.ashx?partID=394&pageindex=1&pagesize=5");
		mFileOperate fileOprt = new mFileOperate(getActivity(), FILENAME);
		String json = fileOprt.readFromFile();
		
		// 如果是初次打开应用，则加载预装的数据
		if (json.equals("[]") || json.equals("")) {
			json = getAssetsData("list.json");
		}
		
		//System.out.println("json:----"+json);

		// json转化为list
		newslist = Json2list(json);
		// 呈现数据到UI布局
		adapter = new NewsAdapter(this.getActivity(), getlist(newslist));
		newslistview.setAdapter(adapter);

		// 文章列表项点击事件
		newslistview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				String title = newslist.get(position - 1).title;
				String createtime = newslist.get(position - 1).CreateTime;
				String articleid = newslist.get(position - 1).id;

				Intent intent = new Intent(getActivity(), ArticleDetail.class);
				intent.putExtra("articleid", articleid); // 文章id
				intent.putExtra("title", title); // 文章标题
				intent.putExtra("createtime", createtime); // 文章发布时间
				getActivity().startActivity(intent);
			}
		});

		// newslistview.setOnRefreshListener(new OnRefreshListener2<ListView>()
		// {
		// });

		// 设置下拉刷新监听
		newslistview.setOnRefreshListener(new OnRefreshListener<ListView>() {

			// 开始下拉时执行
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
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
						// 获得缓存区的原始数据
						mFileOperate fileOprt = new mFileOperate(getActivity(),
								FILENAME);
						String json = fileOprt.readFromFile();
						// json转化为list
						newslist = Json2list(json);

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

						// 只保存最新的20项
						while (newslist.size() >= 20) {
							newslist.remove(newslist.size() - 1);
						}

						// 更新缓存文件
						fileOprt.writeToFile(list2json(newslist));

						// 执行刷新结束，下拉弹回
						newslistview.onRefreshComplete();
						super.onPostExecute(result);
					}
					// 异步任务开始执行
				}.execute("http://www.ncuhome.cn/NewIndex2013/AjaxGetList.ashx?partID=394&pageindex=1&pagesize=10");
			}
		});

		return v;
	}
}
