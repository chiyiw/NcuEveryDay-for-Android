package com.wangpeng.ncueveryday;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.wangpeng.ncueveryday.book.MainActivity_B;
import com.wangpeng.ncueveryday.course.MainActivity_C;
import com.wangpeng.ncueveryday.news.FragmentCollege;
import com.wangpeng.ncueveryday.news.FragmentHome;
import com.wangpeng.ncueveryday.news.FragmentSchoolWork;
import com.wangpeng.ncueveryday.score.MainActivity_S;
import com.wangpeng.ncueveryday.weather.MainActivity_W;

import java.util.ArrayList;
import java.util.List;

// 继承自FragmentActivity
public class MainActivity extends FragmentActivity {

	private ViewPager mPager;
	private FragmentPagerAdapter mAdapter;
	private List<Fragment> pages;
	private int device_width; // 设备屏幕宽度
	private ImageView tabline; // 顶部移动的标签线
	private TextView tab01;
	private TextView tab02;
	private TextView tab03;
	private SlidingMenu slidingMenu;
	private ImageButton btn_weather;
	private ImageButton btn_course;
	private ImageButton btn_book;
	private ImageButton btn_score;
	private ImageButton btn_about;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		// 声明ImageLoader单例
		ImageLoaderConfiguration imgloadConfig = ImageLoaderConfiguration
				.createDefault(this);
		ImageLoader.getInstance().init(imgloadConfig);

		mPager = (ViewPager) findViewById(R.id.viewpager);

		// 向Pager中添加页面
		SetPages();
		// 重载Adapter(需要FragmentManager())
		SetmAdapter();
		// 设置ViewPager的适配器
		mPager.setAdapter(mAdapter);

		// 设置顶部标签位置，监听页面切换
		setTabs();
		// 设置字体
		// setFontStyle();
		// 设置侧滑菜单
		setSlideMenu();

	}

	public void SetmAdapter() {
		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return pages.size();
			}

			@Override
			public Fragment getItem(int arg0) {
				return pages.get(arg0);
			}
		};
	}

	public void SetPages() {
		pages = new ArrayList<Fragment>();
		FragmentCollege pageCollege = new FragmentCollege();
		FragmentHome pageHome = new FragmentHome();
		FragmentSchoolWork pageSchoolWork = new FragmentSchoolWork();
		pages.add(pageCollege);
		pages.add(pageHome);
		pages.add(pageSchoolWork);
	}

	public void setSlideMenu() {
		slidingMenu = new SlidingMenu(this);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setBehindOffsetRes(R.dimen.sliding_menu_offset);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		slidingMenu.setMenu(R.layout.slidingmenu);

		btn_weather = (ImageButton) slidingMenu.findViewById(R.id.btn_weather);
		btn_weather.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("Click");
				Intent intent = new Intent(MainActivity.this,
						MainActivity_W.class);
				MainActivity.this.startActivity(intent);
			}
		});

		btn_course = (ImageButton) slidingMenu.findViewById(R.id.btn_course);
		btn_course.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("Click");
				Intent intent = new Intent(MainActivity.this,
						MainActivity_C.class);
				MainActivity.this.startActivity(intent);
			}
		});

		btn_book = (ImageButton) slidingMenu.findViewById(R.id.btn_book);
		btn_book.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("Click");
				Intent intent = new Intent(MainActivity.this,
						MainActivity_B.class);
				MainActivity.this.startActivity(intent);
			}
		});

		btn_score = (ImageButton) slidingMenu.findViewById(R.id.btn_score);
		btn_score.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("Click");
				Intent intent = new Intent(MainActivity.this,
						MainActivity_S.class);
				MainActivity.this.startActivity(intent);
			}
		});

		btn_about = (ImageButton) slidingMenu.findViewById(R.id.btn_about);
		btn_about.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("Click");

				LayoutInflater flater = MainActivity.this.getLayoutInflater();
				View mview = flater.inflate(R.layout.aboutdialog, null);

				final Dialog dialog = new AlertDialog.Builder(MainActivity.this)
						.setView(mview).create();

				dialog.show();

				// 检查更新按钮被点击时
				mview.findViewById(R.id.about_main_update).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								// 异步调用更新接口
								Log.d("未完成","未实现检查更新！");
								dialog.dismiss();
							}
						});

				mview.findViewById(R.id.about_main_qanda).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(MainActivity.this,
										QAActivity.class);
								MainActivity.this.startActivity(intent);
								dialog.dismiss();
							}
						});
			}
		});
	}

	/**
	 * 设置顶部标签宽度和位置
	 */
	public void setTabs() {
		// 获取设备窗口显示
		Display display = getWindow().getWindowManager().getDefaultDisplay();
		// 定义显示尺寸
		DisplayMetrics metrics = new DisplayMetrics();
		// 获取显示尺寸
		display.getMetrics(metrics);
		device_width = metrics.widthPixels;

		// 设置标签线的宽度
		tabline = (ImageView) findViewById(R.id.tabline);
		LayoutParams lp = tabline.getLayoutParams();
		lp.width = device_width / 3;
		tabline.setLayoutParams(lp);

		// 设置标签文字的宽度
		tab01 = (TextView) findViewById(R.id.tab1);
		tab02 = (TextView) findViewById(R.id.tab2);
		tab03 = (TextView) findViewById(R.id.tab3);
		LayoutParams tablp = tab01.getLayoutParams();
		tablp.width = device_width / 3;
		tab01.setLayoutParams(tablp);
		tab02.setLayoutParams(tablp);
		tab03.setLayoutParams(tablp);

		// 设置页面滑动监听事件
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// 设置标签文字颜色
				tab01.setTextColor(Color.BLACK);
				tab02.setTextColor(Color.BLACK);
				tab03.setTextColor(Color.BLACK);

				switch (arg0) {
				case 0:
					tab01.setTextColor(Color.parseColor("#33b5e5"));
					slidingMenu.setSlidingEnabled(true);
					break;
				case 1:
					tab02.setTextColor(Color.parseColor("#33b5e5"));
					slidingMenu.setSlidingEnabled(false);
					break;
				case 2:
					tab03.setTextColor(Color.parseColor("#33b5e5"));
					slidingMenu.setSlidingEnabled(false);
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// 设置标签线的位置
				LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) tabline
						.getLayoutParams();
				lp.leftMargin = arg0 * device_width / 3 + (arg2 / 3);
				tabline.setLayoutParams(lp);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		// 点击标签切换页面
		tab01.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPager.setCurrentItem(0, true);
				tab01.setTextColor(Color.parseColor("#33b5e5"));
				tab02.setTextColor(Color.BLACK);
				tab03.setTextColor(Color.BLACK);
			}
		});
		tab02.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPager.setCurrentItem(1, true);
				tab01.setTextColor(Color.BLACK);
				tab02.setTextColor(Color.parseColor("#33b5e5"));
				tab03.setTextColor(Color.BLACK);
			}
		});
		tab03.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPager.setCurrentItem(2, true);
				tab01.setTextColor(Color.BLACK);
				tab02.setTextColor(Color.BLACK);
				tab03.setTextColor(Color.parseColor("#33b5e5"));
			}
		});
	}

	public void setFontStyle() {
		// AssetManager amg = getAssets();
		// Typeface tf = Typeface.createFromAsset(amg, "weiruanyahei.ttf");
		// tab01.setTypeface(tf);
		// tab02.setTypeface(tf);
		// tab03.setTypeface(tf);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wea_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		return super.onOptionsItemSelected(item);
	}
}
