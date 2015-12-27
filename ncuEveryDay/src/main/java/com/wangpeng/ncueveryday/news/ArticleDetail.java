package com.wangpeng.ncueveryday.news;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wangpeng.ncueveryday.R;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文章详情页面
 * 
 * @author wangpeng
 */
@SuppressLint("InflateParams")
public class ArticleDetail extends Activity {

	private TextView vi_title; // 标题
	private ListView listview; // 正文段落列表
	private ArrayList<Map<String, String>> listdatas; // 数据
	private ProgressBar progressbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.news_detail_main);

		vi_title = (TextView) findViewById(R.id.vi_title);
		listview = (ListView) findViewById(R.id.vi_detail_list);

		progressbar = (ProgressBar) findViewById(R.id.news_progress);
		
		// 获取前一页面传递来的数据
		Bundle bundle = this.getIntent().getExtras();
		// 获取文章标题
		vi_title.setText(bundle.getString("title"));
		// 获取文章id
		String articleid = bundle.getString("articleid");
		// 获取文章发布时间
		String createtime = bundle.getString("createtime");

		// 文章详情地址
        // 通过获取html本地解析
//		String url = "http://www.ncuhome.cn/NewIndex2013/Article_detail.aspx?SubjectId=" + articleid;
        // 使用服务器解析返回json
		String url = "http://ncueveryday.sinaapp.com/index.php?article_id=" + articleid;

        System.out.println(articleid);

		// 使用开源Android-async-http类异步获取网络数据
		AsyncHttpClient asyClient = new AsyncHttpClient();
		asyClient.get(url, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				progressbar.setVisibility(View.GONE);
			}

			@Override
			public void onRetry(int retryNo) {
				super.onRetry(retryNo);
				// called when request is retried
			}

			@Override
			public void onStart() {
				super.onStart();
				// called before request is started
				progressbar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// System.out.println(new String(arg2));
				listdatas = getListDatas(new String(arg2));
//				listdatas = getListDatas_local(new String(arg2));
				mAdapter adapter = new mAdapter(ArticleDetail.this, listdatas);
				listview.setAdapter(adapter);
				
				progressbar.setVisibility(View.GONE);
			}
		});
	}


    /**
     * 获取文章内容（后台服务器解析）
     * @param str
     * @return Json数据
     */
    public ArrayList<Map<String, String>> getListDatas(String str) {

        //str = "[{\"imgUri\":\"\",\"content\":\"化学学院消息：12月13日下午2点，化学学院理研会于18栋1楼东会议室召开会议，集中学习十八届四中全会精神，各班级代表参与了本次会议。\"},{\"imgUri\":\"\",\"content\":\"“天下之事，不难于立法，而难于法之必行”，理研会负责人唐湘兰借助这一句话，与参加会议的同学们学习了“法”的实施与立法相比存在明显的不足，而法治是建设中国特色社会主义的必经之路，厉行法治才能提升国家治理体系，使治理能力现代化，有利于党执政兴国，是国家长治久安的坚强后盾，会议中，理研会负责人就应该成为一种新常态的依法治国进行多方面阐述，并延伸到自身，说道:“依法治国，其实是与我们自身息息相关的，我们要时刻对自己的行为举止内省，对法律严格恪守。”\"}]";

        //System.out.println(str);

        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

        try {
            JSONArray arr = new JSONArray(str);

            for(int i = 0; i<arr.length(); i++){
                JSONObject obj = arr.getJSONObject(i);
                Map<String, String> map = new HashMap<String, String>();

                map.put("image",obj.getString("imgUri"));
                map.put("text",obj.getString("content"));

                list.add(map);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return list;
    }

	/**
	 * 获取文章正文内容(通过html代码本地解析)
	 * 
	 * @param str
	 *            文章网页源代码
	 * @return 文章段落列表
	 */
	public ArrayList<Map<String, String>> getListDatas_local(String str) {

		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

		// String result = getWebData(str);

		// 取消了之前自己写的获取网络数据函数，本函数的功能只是解析网络数据中的可用数据项
		String content = str;

		// 定位到文章正文位置
		int start = content.indexOf("<div class=\"eassy_main\">");
		int end = content.indexOf("</div>", start);
		// 截取文章正文部分
		content = content.substring(start, end);

		// 去除<p style=……>中的属性
		Pattern p = Pattern
				.compile("( alt=\".*?\")|( style=\".*?\")|( oldsrc=\".*?\")|( title=\".*?\")|( width=\".*?\")|( height=\".*?\")");
		Matcher m = p.matcher(content);
		content = m.replaceAll("");

		Pattern p1 = Pattern.compile("<(/|)span>");
		Matcher m1 = p1.matcher(content);
		content = m1.replaceAll("");

		// System.out.println(content);

		// 去除<strong> </strong> <p><br></p>等等
		Pattern p2 = Pattern
				.compile("(<(|/)strong>|<p>\\s+</p>|<p>(|<br(| /|/)>)</p>|&nbsp;|quot;)|<br/>|<(|/)em>");
		content = p2.matcher(content).replaceAll("");

		Pattern p3 = Pattern.compile("<p></p>");
		Matcher m3 = p3.matcher(content);
		content = m3.replaceAll("");

		// System.out.println(content);

		// 找到段落并循环解析段落，将获取的内容保存到Map中
		int s = content.indexOf("<p>");
		s = content.indexOf("<p>", s + 1);
		int e = content.indexOf("</p>", s);

		while (s > 0 && e > 0) {
			Map<String, String> map = new HashMap<String, String>();
			String item = content.substring(s + 3, e);

			// 图片段落
			int imgstart = item.indexOf("img src=\"");

			if (imgstart > 0) {
				int imgend = item.indexOf("\"", imgstart + 9);
				item = item.substring(imgstart + 9, imgend);
				map.put("image", item);
				map.put("text", "");
				// 文字段落
			} else {
				map.put("image", "");
				map.put("text", item);
			}

			// 将该段内容加入到列表中
			list.add(map);

			// System.out.println(item);

			// 将光标位置向后移动
			s = content.indexOf("<p>", e + 1);
			e = content.indexOf("</p>", s + 1);
		}
		return list;
	}

	/**
	 * 联网获取文章数据
	 * 
	 * @param url
	 *            请求地址
	 * @return
	 */
	public String getWebData(String url) {
		String result = "";
		try {
			result = new AsyncTask<String, Void, String>() {

				@Override
				protected String doInBackground(String... params) {
					String content = "";

					try {
						URL u = new URL(params[0]);
						URLConnection con;

						con = u.openConnection();
						InputStream is = con.getInputStream();
						InputStreamReader isr = new InputStreamReader(is,
								"utf-8");
						BufferedReader bf = new BufferedReader(isr);

						String line = "";

						while ((line = bf.readLine()) != null) {
							content += line;
						}

					} catch (IOException e) {
						e.printStackTrace();
					}

					return content;
				}
			}.execute(url).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 自定义的Adapter
	 * 
	 * @author wangpeng
	 */
	@SuppressLint("ViewHolder")
	public class mAdapter extends ArrayAdapter<Map<String, String>> {

		// 构造函数
		public mAdapter(Context context, List<Map<String, String>> objects) {
			super(context, 0, objects);
		}

		// 为Adapter的每一项设置View
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			Activity activity = (Activity) getContext();
			// 获取布局
			View v = activity.getLayoutInflater().inflate(R.layout.news_articleitem,
					null);

			TextView vi_text = (TextView) v.findViewById(R.id.vi_detail_text);
			ImageView vi_image = (ImageView) v
					.findViewById(R.id.vi_detail_image);

			Map<String, String> datas = getItem(position);

			// 向布局设置数据
			vi_text.setText(datas.get("text"));
			// GetWebImage getimage = new GetWebImage(datas.get("image"),
			// vi_image);
			// getimage.SetImage();

			final DisplayImageOptions option = new DisplayImageOptions.Builder()
					.cacheInMemory(true).cacheOnDisk(true) // 缓存目录(mnt/sdcard/Android/data/com.xxx.xxx/cache/)
					.build();
			ImageLoader.getInstance().displayImage(datas.get("image"),
					vi_image, option);

			// vi_image.setScaleType(ImageView.ScaleType.FIT_XY);

			// 获取设备窗口显示
			Display display = getWindow().getWindowManager()
					.getDefaultDisplay();
			// 定义显示尺寸
			DisplayMetrics metrics = new DisplayMetrics();
			// 获取显示尺寸
			display.getMetrics(metrics);
			float device_width = metrics.widthPixels;
			float device_height = metrics.heightPixels;

			System.out.println("width = " + device_width + " height = "
					+ device_height);
			vi_image.setScaleX(device_width / 530.0f);
			vi_image.setScaleY(device_width / 530.0f);
			// 返回布局
			return v;
		}
	}
}
