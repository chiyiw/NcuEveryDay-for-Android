package com.wangpeng.ncueveryday.weather;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;
import com.thinkland.sdk.android.SDKInitializer;
import com.wangpeng.ncueveryday.R;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity_W extends Activity {

	private GridView gridView;
	private ArrayList<WeatherHourInfo> listHourData;
	private ArrayList<WeatherDayInfo> listDayData;
	private ListView listView;

	public static int[] resIds; // 天气图标
	public static ArrayList<String> daysName; // 未来几天的名称

	private TextView weatherCityTextv; // 上部今日天气的控件
	private ImageView weatherImagev;
	private TextView weatherTepTextv;
	private Button weatherCitySettingBtn;
	private TextView publishtimeTextv;
	private ProgressBar progressbar;

	private static String IsOpenAd = "true"; // 是否开启广告
	// 获得键值存储区
	private SharedPreferences preferences;
	private Editor pref_editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.wea_activity_main);

		// 初始化聚合数据接口
		SDKInitializer.initialize(getApplicationContext());

		SetLayout(); // 初始化View控件布局

		preferences = getPreferences(Activity.MODE_PRIVATE);
		pref_editor = preferences.edit();

		daysName = new ArrayList<String>();
		SetResources();

		String areaid = preferences.getString("areaid", "101240101");
		String cityname = preferences.getString("cityname", "南昌");
		ShowUI(areaid, cityname);
		//Test();

	}


	// 本地测试模拟联网数据
	public void Test() {
		String result = getAssetsData("daysdata.json");
		listDayData = Json2list_day_juhe(result);
		listAdapter d_adapter = new listAdapter(MainActivity_W.this,
				listDayData);
		listView.setAdapter(d_adapter);

		listHourData = Json2list_hour(getAssetsData("101240101.html"));
		// 设置GridView的项数和每项宽度属性属性
		SetGridParams(listHourData.size(), 80);
		gridAdapter h_adapter = new gridAdapter(MainActivity_W.this,
				listHourData);
		gridView.setAdapter(h_adapter);
	}

	public void ShowUI(String AreaId, String CityName) {
		GetHoursData("http://m.weather.com.cn//mpub/hours/" + AreaId + ".html");
		GetDaysData_juhe(CityName);
	}

	public void SetLayout() {
		gridView = (GridView) findViewById(R.id.gridView);
		listView = (ListView) findViewById(R.id.weatherdaysListv);
		weatherCityTextv = (TextView) findViewById(R.id.weatherCityTextv);
		weatherTepTextv = (TextView) findViewById(R.id.weatherTepTextv);
		weatherImagev = (ImageView) findViewById(R.id.weatherImageV);
		weatherCitySettingBtn = (Button) findViewById(R.id.weatherCitySettingBtn);
		publishtimeTextv = (TextView) findViewById(R.id.publishtimeTextv);
		progressbar = (ProgressBar) findViewById(R.id.progressbar);

		weatherCitySettingBtn
				.setOnClickListener(new CitySettingOnClickListener(this));
	}

	// 联网获取未来几天天气
	public void GetDaysData_juhe(String cityname) {
		Parameters params = new Parameters();
		params.add("cityname", cityname);
		params.add("dtype", "json");
		params.add("format", 1);

		// 显示进度条
		progressbar.setVisibility(View.VISIBLE);
		// 移除上一次的数据
		// listDayData.clear();
		listView.setAdapter(null);

		// 读取本地缓存的数据，当网络请求失败时使用
		final String temp = readFromFile("daysdata.json");
		if (!temp.equals("")) {
			listDayData = Json2list_day_juhe(readFromFile("daysdata.json"));
		}

		JuheData.executeWithAPI(39, "http://v.juhe.cn/weather/index",
				JuheData.GET, params, new DataCallBack() {
					public void resultLoaded(int err, String reason,
							String result) {
						// 当数据加载成功，隐藏进度条
						progressbar.setVisibility(View.GONE);

						if (err == 0) {
							// 将获取的数据存入到文件中，离线显示
							writeToFile("daysdata.json", result);

							System.out.println("未来几天数据获取成功:" + result);

							// 解析数据并呈现在列表中
							listDayData = Json2list_day_juhe(result);

							listAdapter d_adapter = new listAdapter(
									MainActivity_W.this, listDayData);
							listView.setAdapter(d_adapter);
						} else {
							// 获取失败，提示失败原因
							Toast.makeText(getApplicationContext(),
									"更新失败：" + reason, Toast.LENGTH_SHORT)
									.show();
							if (!temp.equals("")) {
								// 如果加载失败，则载入本地的数据
								listAdapter d_adapter = new listAdapter(
										MainActivity_W.this, listDayData);
								listView.setAdapter(d_adapter);
							}
						}
					}
				});
	}

	// 联网获取未来几小时天气
	public void GetHoursData(String url) {

		// 使用异步联网库
		AsyncHttpClient asyClient = new AsyncHttpClient();
		asyClient.get(url, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				System.out.println("failure");

				String str = readFromFile("hoursdata.json");
				if (!str.equals("")) {
					listHourData = Json2list_hour(str);

					// 设置GridView的项数和每项宽度属性属性
					SetGridParams(listHourData.size(), 80);
					gridAdapter h_adapter = new gridAdapter(
							MainActivity_W.this, listHourData);
					gridView.setAdapter(h_adapter);
				}
			}

			@Override
			public void onRetry(int retryNo) {
				super.onRetry(retryNo);
				// called when request is retried
				System.out.println("retry");
			}

			@Override
			public void onStart() {
				super.onStart();
				// called before request is started
				System.out.println("start");
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				System.out.println("success");

				// 解析获取到的数据并呈现在横向列表GridView中
				String str = new String(arg2);

				writeToFile("hoursdata.json", str);

				listHourData = Json2list_hour(str);

				// 设置GridView的项数和每项宽度属性属性
				SetGridParams(listHourData.size(), 80);
				gridAdapter h_adapter = new gridAdapter(MainActivity_W.this,
						listHourData);
				gridView.setAdapter(h_adapter);
			}
		});
	}

	// 写文件操作
	public void writeToFile(String filename, String data) {
		try {
			// 打开应用内部文件，当文件不存在时会创建，当存在会将之前内容覆盖
			FileOutputStream fos = this.openFileOutput(filename,
					Context.MODE_PRIVATE);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			// 写入信息到文件中
			osw.write(data);
			// 清理输入缓冲区
			osw.flush();
			osw.close();
			fos.close();
			// Toast.makeText(context.getApplicationContext(), "写入文件成功",
			// Toast.LENGTH_SHORT).show();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 读文件操作
	public String readFromFile(String filename) {
		String result = "";
		try {
			// 打开应用内部文件
			FileInputStream fis = this.openFileInput(filename);
			// 设置读取编码
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			// 创建字节流
			char input[] = new char[fis.available()];
			// 读取数据到字节流中
			isr.read(input);
			isr.close();
			fis.close();
			// 使用字节流创建字符串
			result = new String(input);
			// Toast.makeText(context.getApplicationContext(), result,
			// Toast.LENGTH_SHORT).show();
		} catch (FileNotFoundException e) {
			// Toast.makeText(this.getApplicationContext(), "文件不存在",
			// Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	// 设置天气图片资源
	public void SetResources() {
		// 获取资源组
		TypedArray ar = getResources().obtainTypedArray(R.array.weatherimages);
		int len = ar.length();
		resIds = new int[len];
		for (int i = 0; i < len; i++) {
			// 添加资源的ID到数组中
			resIds[i] = ar.getResourceId(i, 0);
		}
		ar.recycle();
	}

	// 设置GridView 的宽度和项数，参数：总项数，每一项的宽度
	public void SetGridParams(int size, int length) {

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		float density = dm.density;
		int gridviewWidth = (int) (size * (length + 4) * density);
		int itemWidth = (int) (length * density);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				gridviewWidth, LinearLayout.LayoutParams.FILL_PARENT);
		gridView.setLayoutParams(params); // 重点
		gridView.setColumnWidth(itemWidth); // 重点
		gridView.setHorizontalSpacing(5); // 间距
		gridView.setNumColumns(size); // 重点
	}

	/**
	 * json数据转化到ArrayList<WeatherHourInfo>
	 * 
	 * @param json
	 *            需要转化的json字符串
	 * @return list
	 */
	public ArrayList<WeatherHourInfo> Json2list_hour(String json) {

		ArrayList<WeatherHourInfo> list = new ArrayList<WeatherHourInfo>();
		try {
			JSONObject root = new JSONObject(json);
			JSONArray arr = root.getJSONArray("jh");

			for (int i = 0; i < arr.length(); i++) {
				WeatherHourInfo news = new WeatherHourInfo();
				JSONObject obj = arr.getJSONObject(i);

				news.ja = obj.getString("ja");
				news.jb = obj.getString("jb");
				news.jc = obj.getString("jc");
				news.jd = obj.getString("jd");
				news.je = obj.getString("je");
				news.jf = obj.getString("jf");

				list.add(news);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * json数据转化到ArrayList<WeatherDayInfo>
	 * 
	 * @param json
	 * @return list
	 */
	public ArrayList<WeatherDayInfo> Json2list_day_juhe(String json) {

		ArrayList<WeatherDayInfo> list = new ArrayList<WeatherDayInfo>();
		try {
			JSONObject root = new JSONObject(json);
			JSONObject weatherobj = root.getJSONObject("result");

			JSONObject sk = weatherobj.getJSONObject("sk");
			publishtimeTextv.setText("发布:" + sk.getString("time"));

			// 今日天气
			JSONObject today = weatherobj.getJSONObject("today");
			{
				int weatherId = Integer.parseInt(today.getJSONObject(
						"weather_id").getString("fa"));
				int hour = Integer.parseInt(sk.getString("time")
						.substring(0, 2));

				// 如果是霾，图片编号为32
				if (weatherId == 53) {
					weatherId = 32;
				} // 如果是夜晚，换为夜晚的图标
				if (hour > 19 || hour < 6) {
					weatherId += 33;
				}

				weatherImagev.setImageResource(resIds[weatherId + 66]);

				weatherCityTextv.setText(today.getString("city"));
				weatherTepTextv.setText(today.getString("temperature").replace(
						"℃~", "~"));
			}
			// 未来几天天气
			JSONObject future = weatherobj.getJSONObject("future");
			{
				// 获取future下的所有日期结点
				JSONArray days = future.names();
				ArrayList<String> days_l = new ArrayList<String>();
				for (int i = 0; i < days.length(); i++) {
					days_l.add(days.get(i).toString());
				}
				// 对获取到的所有节点按照名称排序
				Collections.sort(days_l);

				for (int i = 1; i < 7; i++) {
					JSONObject wobj = future.getJSONObject(days_l.get(i));
					WeatherDayInfo w = new WeatherDayInfo();

					w.img1 = wobj.getJSONObject("weather_id").getString("fa");
					w.img2 = wobj.getJSONObject("weather_id").getString("fb");
					w.weather = wobj.getString("weather");
					w.tep = wobj.getString("temperature");

					list.add(w);
				}
			}

			int week = 0;
			// Integer.parseInt(weatherobj.getString("week").substring(3));
			String weeks[] = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
			for (int i = 0; i < weeks.length; i++) {
				if (today.getString("week").equals(weeks[i])) {
					week = i;
				}
			}
			// 未来天气只有六天，用于适配未来天气中的星期
			for (int i = 1; i < 7; i++) {
				daysName.add(weeks[(week + i) % 7]);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获取Assets中的文件内容，返回字符串
	 * 
	 * @param filename
	 *            文件路径
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

		if (id == R.id.refresh) { // 刷新项
			String areaid = preferences.getString("areaid", "101010100");
			String cityname = preferences.getString("cityname", "北京");
			ShowUI(areaid, cityname);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
