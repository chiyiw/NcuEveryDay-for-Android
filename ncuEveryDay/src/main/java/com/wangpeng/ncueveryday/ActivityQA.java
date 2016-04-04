package com.wangpeng.ncueveryday;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityQA extends Activity {

	private ListView lv;
	private ArrayList<feedback> feedbackdata;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.q_and_a);
		
		lv = (ListView) findViewById(R.id.qa_feedbackLv);
		feedbackdata = new ArrayList<feedback>();
		
		setFeedBack();
		
		feedBackAdapter adapter = new feedBackAdapter();
		lv.setAdapter(adapter);
		
	}
	
	void setFeedBack(){
		try {
			InputStream is = getResources().getAssets().open("feedback.txt");
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader bfr = new BufferedReader(isr);

			String line = "";
			while ((line = bfr.readLine()) != null) {
				feedback fe = new feedback();
				fe.title = line;
				if ((line = bfr.readLine()) != null){
					fe.content = line;
				}
				
				feedbackdata.add(fe);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private class feedBackAdapter extends BaseAdapter{

		int colors[] = { Color.rgb(0x31, 0xc4, 0x28),
				Color.rgb(0x20, 0x92, 0x59), Color.rgb(0x8c, 0xbf, 0x26),
				Color.rgb(0x00, 0xab, 0xa9), Color.rgb(0x99, 0x6c, 0x33),
				Color.rgb(0x3b, 0x92, 0xbc), Color.rgb(0xd5, 0x4d, 0x34),
				Color.rgb(0xe3, 0x2f, 0xd9), Color.rgb(0xfb, 0x74, 0xc1) };
		
		@Override
		public int getCount() {
			return feedbackdata.size();
		}

		@Override
		public Object getItem(int position) {
			return feedbackdata.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			LinearLayout lo = new LinearLayout(ActivityQA.this);
			lo.setOrientation(LinearLayout.VERTICAL);

			TextView tv_title = new TextView(ActivityQA.this);
			tv_title.setText(""+feedbackdata.get(position).title);
			tv_title.setTextSize(18);
			tv_title.setTextColor(colors[(int) (Math.random() * 100 % 9)]);
			
			TextView tv_content = new TextView(ActivityQA.this);
			tv_content.setText(ToDBC("\n\u3000\u3000"+feedbackdata.get(position).content+"\n"));
			tv_content.setTextSize(15);
			tv_content.setAutoLinkMask(Linkify.ALL);
			//tv_content.setTextColor(colors[(int) (Math.random() * 100 % 9)]);
			
			lo.addView(tv_title);
			lo.addView(tv_content);
			
			return lo;
		}
		
	}
	public class feedback{
		String title;
		String content;
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

	public String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i< c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}if (c[i]> 65280&& c[i]< 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}
}
