package com.wangpeng.ncueveryday.course;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.wangpeng.ncueveryday.R;

public class ActivityCourse extends Activity {

	private TextView class_nameTv; // 班级名称
	private LinearLayout ll1; // 周一
	private LinearLayout ll2;
	private LinearLayout ll3;
	private LinearLayout ll4;
	private LinearLayout ll5;
	private LinearLayout ll6;
	private LinearLayout ll7;

	private int device_width;
	private int device_height;

	private SharedPreferences preference;
	private Editor pre_edit;

	// 配置SQLite数据库
	private static SQLiteDatabase database;
	public static final String DATABASE_FILENAME = "coursesdata.db"; // 这个是DB文件名字
	public static final String PACKAGE_NAME = "com.wangpeng.ncueveryday"; // 这个是自己项目包路径
	public static final String DATABASE_PATH = "/data"
			+ Environment.getDataDirectory().getAbsolutePath() + "/"
			+ PACKAGE_NAME + "/databases"; // 获取存储位置地址

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.cou_activity_main);

		// Bundle bundle = this.getIntent().getExtras();
		// int class_index = bundle.getInt("class_index");

		class_nameTv = (TextView) findViewById(R.id.class_name);
		ll1 = (LinearLayout) findViewById(R.id.ll1);
		ll2 = (LinearLayout) findViewById(R.id.ll2);
		ll3 = (LinearLayout) findViewById(R.id.ll3);
		ll4 = (LinearLayout) findViewById(R.id.ll4);
		ll5 = (LinearLayout) findViewById(R.id.ll5);
		ll6 = (LinearLayout) findViewById(R.id.ll6);
		ll7 = (LinearLayout) findViewById(R.id.ll7);

		preference = getPreferences(Activity.MODE_PRIVATE);
		pre_edit = preference.edit();

		int class_index = preference.getInt("class_index", 6913);
		String class_name = preference.getString("class_name", "计算机科学与技术134班");
		
		setWidth();

		setTable(class_index, class_name);
	}

	void setTable(int class_index, String class_name){
		
		ll1.removeAllViews();
		ll2.removeAllViews();
		ll3.removeAllViews();
		ll4.removeAllViews();
		ll5.removeAllViews();
		ll6.removeAllViews();
		ll7.removeAllViews();
		
		class_nameTv.setText(class_name);
		
		SQLiteDatabase db = openDatabase(this);
		Cursor c = db
				.rawQuery("select * from data where idx>=? and idx<?;",
						new String[] { (class_index + 3) + "",
								(class_index + 8) + "" });

		Log.i("数据库查询", class_index + "");
		if (!c.moveToFirst()) {
			System.out.println("未查询到结果");
		} else {
			String result = "";
			int i = 0;

			c.isAfterLast();
//			System.out.println("result:"
//					+ CutCourseString(c.getString(c.getColumnIndex("mon"))));
			String s = CutCourseString(c.getString(c.getColumnIndex("mon")));

			if (s.length() > 0) {
				int start = s.lastIndexOf(")周");
				int end = s.lastIndexOf("-");

//				System.out.println(s.substring(start + 2, end));
			}

			while (!c.isAfterLast()) {

				setCourses(ll1,
						CutCourseString(c.getString(c.getColumnIndex("mon"))));
				setCourses(ll2,
						CutCourseString(c.getString(c.getColumnIndex("tues"))));
				setCourses(ll3,
						CutCourseString(c.getString(c.getColumnIndex("wed"))));
				setCourses(ll4,
						CutCourseString(c.getString(c.getColumnIndex("thur"))));
				setCourses(ll5,
						CutCourseString(c.getString(c.getColumnIndex("fri"))));
				setCourses(ll6,
						CutCourseString(c.getString(c.getColumnIndex("sat"))));
				setCourses(ll7,
						CutCourseString(c.getString(c.getColumnIndex("sun"))));

				c.moveToNext();
			}
			// Toast.makeText(getApplicationContext(), result,
			// Toast.LENGTH_LONG).show();
		}
	}
	
	String CutCourseString(String os) {

		String temp = os.replaceAll("\n", "\n");
		// Pattern p = Pattern
		// .compile("(WP（\\d*-\\d*\\)\\S)|(WP\\d*-\\d*\\S)");
		// Matcher m = p.matcher(temp);
		// temp = m.replaceAll("");

		return temp + "";
	}

	void setCourses(LinearLayout v, String s) {
		int colors[] = { Color.rgb(0x31, 0xc4, 0x28),
				Color.rgb(0x20, 0x92, 0x59), Color.rgb(0x8c, 0xbf, 0x26),
				Color.rgb(0x00, 0xab, 0xa9), Color.rgb(0x99, 0x6c, 0x33),
				Color.rgb(0x3b, 0x92, 0xbc), Color.rgb(0xd5, 0x4d, 0x34),
				Color.rgb(0xe3, 0x2f, 0xd9), Color.rgb(0xfb, 0x74, 0xc1) };

		if (!s.equals("")) {
			int start = s.lastIndexOf(")周");
			int end = s.lastIndexOf("-");

			// System.out.println(s.substring(start+1, end));
			// lp.topMargin = 100;
		}

		View mv = LayoutInflater.from(this).inflate(R.layout.cou_mtextview,
				null);

		TextView tv = (TextView) mv.findViewById(R.id.course_block);

		tv.setText(s);
		tv.setBackgroundColor(colors[(int) (Math.random() * 100 % 9)]);
		tv.setHeight(device_height / 6);

		tv.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Dialog dialog = new AlertDialog.Builder(ActivityCourse.this)
						.create();
			}
		});

		v.addView(mv);
	}

	void setWidth() {
		// 获取设备窗口显示
		Display display = getWindow().getWindowManager().getDefaultDisplay();
		// 定义显示尺寸
		DisplayMetrics metrics = new DisplayMetrics();
		// 获取显示尺寸
		display.getMetrics(metrics);
		device_width = metrics.widthPixels;
		device_height = metrics.heightPixels;

		LayoutParams lp = ll1.getLayoutParams();
		lp.width = device_width / 5;

		ll1.setLayoutParams(lp);
		ll2.setLayoutParams(lp);
		ll3.setLayoutParams(lp);
		ll4.setLayoutParams(lp);
		ll5.setLayoutParams(lp);
		ll6.setLayoutParams(lp);
		ll7.setLayoutParams(lp);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.cou_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) { // 刷新项

			// 获取布局管理器
			LayoutInflater flater = this.getLayoutInflater();
			View mview = flater.inflate(R.layout.cou_dialog, null);

			// 搜索框
			EditText editv = (EditText) mview
					.findViewById(R.id.Cou_DialogSearchEditv);
			// “未查找到”提示文本
			final TextView textv = (TextView) mview
					.findViewById(R.id.Cou_DialogSearchTipTextv);
			// 查询城市结果列表
			final ListView lv = (ListView) mview
					.findViewById(R.id.Cou_DialogCitysLv);
			// 查询城市结果数据组
			final ArrayList<classindex> list_classes = new ArrayList<classindex>();
			// EditText内容改变监听
			editv.addTextChangedListener(new TextWatcher() {
				// 当EditText的文本正在改变时执行
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				// 当EditText的文本改变前执行
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				// 当EditText的文本改变后执行
				public void afterTextChanged(Editable s) {

					// 初始化提示框和结果列表
					textv.setText("未查询到结果");
					list_classes.clear();
					textv.setVisibility(View.GONE);

					String c_name = s.toString().trim();
					if (!c_name.equals("")) {
						// 数据库中模糊查找关键词
						SQLiteDatabase db = openDatabase(ActivityCourse.this);
						Cursor c = db.rawQuery("select * from indx where name like ?",
								new String[] { "%" + c_name + "%" });

						if (!c.moveToFirst()) {// 未查找到结果
							System.out.println("未查询到结果");
							// 显示未找到的提示
							textv.setVisibility(View.VISIBLE);

						} else {// 查询到结果
							textv.setVisibility(View.GONE);
							while (!c.isAfterLast()) {
								String na = c.getString(c.getColumnIndex("name"));
								
//								String result = "";
//								result += c.getString(c.getColumnIndex("idx")) + ": "
//										+ na.substring(4, na.length() - 4) + "\n";
//								Log.i("result", result);
								
								// textv.append(result);
								
								classindex cla = new classindex();
								cla.class_name = c.getString(c.getColumnIndex("name")).substring(4, na.length() - 4);
								cla.index = c.getInt(c.getColumnIndex("idx"));
								
								list_classes.add(cla);
								c.moveToNext();
							}
						}
					}
					// 将所有结果呈现到列表中
					classAdapter adapter = new classAdapter(ActivityCourse.this, list_classes);
					lv.setAdapter(adapter);
				}
			});

			// 自定义的对话框（自定义View）
			final Dialog dialog = new AlertDialog.Builder(this).setView(
					mview).create();
			// 显示对话框
			dialog.show();

			// 结果列表点击监听
			lv.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// Toast.makeText(context, list_citys.get(position).areaid,
					// Toast.LENGTH_SHORT).show();

					int ind = list_classes.get(position).index;
					String cla_name = list_classes.get(position).class_name;
					setTable(ind, cla_name);
					
					// 隐藏对话框
					dialog.dismiss();
					// 根据城市和ID更新UI数据
					// 将本次选择的城市存入本地，作为下次启动默认城市
					Editor editor = ActivityCourse.this.getPreferences(
							Activity.MODE_PRIVATE).edit();
					editor.putInt("class_index", ind);
					editor.putString("class_name", cla_name);
					editor.commit();

				}
			});

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// 打开数据库的静态函数
	public static SQLiteDatabase openDatabase(Context context) {
		try {
			String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
			File dir = new File(DATABASE_PATH);
			// 如果数据库文件目录不存在，则创建目录
			if (!dir.exists()) {
				dir.mkdir();
			}
			if (!(new File(databaseFilename)).exists()) {
				// 如果数据库文件在内部存储区不存在，则拷贝raw中的数据库文件到内部存储区
				InputStream is = context.getResources().openRawResource(
						R.raw.coursesdata);
				FileOutputStream fos = new FileOutputStream(databaseFilename);
				byte[] buffer = new byte[8192];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}

				fos.close();
				is.close();
			}

			// 通过文件创建数据库对象
			database = SQLiteDatabase.openOrCreateDatabase(databaseFilename,
					null);
			return database;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
