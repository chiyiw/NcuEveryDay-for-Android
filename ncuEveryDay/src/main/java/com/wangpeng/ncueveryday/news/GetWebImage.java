/*******************************************************************************
 * 作者: 汪鹏  2015.
 *
 * 以下类为异步获取网络图片所用
 * 可以自由复制、修改和传播
 *******************************************************************************/
package com.wangpeng.ncueveryday.news;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

/**
 * 获取网络图片，并呈现到指定ImageView中
 * @author WangPeng
 */
@SuppressLint("HandlerLeak")
public class GetWebImage {
	private Bitmap bmp = null;
	private ImageView viewid;
	private String imgurl;
	
	private ArrayList<ImageView> viewids;
	private ArrayList<String> imgurls;

	Handler handler = new Handler() {

		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			// 接收传送过来的消息
			bmp = (Bitmap) msg.obj;
			viewid.setImageBitmap(bmp);
		}
	};

	// 不传值的构造函数
	protected GetWebImage() {
	}
	/**
	 * 带值的构造函数
	 * @param url 图片URL
	 * @param v 需要设置图片的ImageView控件
	 */
	protected GetWebImage(String url, ImageView v) {

		this.viewid = v;
		this.imgurl = url;
	}
	
	/**
	 * 设置需要呈现单个图片的URL和ImageView
	 * @param url 图片的URL
	 * @param v 需要设置图片ImageView
	 */
	public void SetProperty(String url,ImageView v){
		this.viewid = v;
		this.imgurl = url;
	}
	/**
	 * 设置需要呈现多个图片的URL和ImageView
	 * @param urls 多个图片URL数组
	 * @param vs 多个需要设置的ImageView数组
	 */
	public void SetProperties(ArrayList<String> urls, ArrayList<ImageView> vs){
		this.viewids = vs;
		this.imgurls = urls;
	}
	
	/**
	 * 开始异步请求单个图片并呈现到UI
	 */
	public void SetImage(){
		MyThread thread = new MyThread(handler, imgurl);
		thread.start();
	}
	/**
	 * 开始异步请求多个图片并呈现到UI
	 */
	public void SetImages(){
		for (int i = 0; i < imgurls.size(); i++) {
			final ImageView iv = viewids.get(i);
			MyThread thread = new MyThread(new Handler(){
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);

					Bitmap bmp = (Bitmap) msg.obj;
					iv.setImageBitmap(bmp);
				}
			}, imgurls.get(i));
			thread.start();
		}
	}
	
	/**
	 * 自定义线程用于异步获取网络图片
	 * @author WangPeng
	 */
	public class MyThread extends Thread {
		Handler handler = null;
		String url = "";

		protected MyThread(Handler handler, String url) {
			this.handler = handler;
			this.url = url;
		}

		// 当外部调用Thread.start()时调用
		@Override
		public void run() {
			super.run();

			URL fileurl;
			Bitmap bmp = null;
			InputStream is;

			try {
				fileurl = new URL(url);
				HttpURLConnection con;
				try {
					// 开始请求网络数据
					con = (HttpURLConnection) fileurl.openConnection();
					// 设置请求超时
					con.setConnectTimeout(6000);
					con.setDoInput(true);
					// 设置是否使用缓存
					con.setUseCaches(true);

					is = con.getInputStream();
					// 解析出图片
					bmp = BitmapFactory.decodeStream(is);
					is.close();

				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

			// 将图片作为消息传送出去
			Message msg = handler.obtainMessage();
			msg.obj = bmp;
			handler.sendMessage(msg);
		}
	}
}
