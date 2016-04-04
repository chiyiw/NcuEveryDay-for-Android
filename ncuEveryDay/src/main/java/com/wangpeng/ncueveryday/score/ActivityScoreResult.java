package com.wangpeng.ncueveryday.score;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.wangpeng.ncueveryday.R;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wangpeng on 16/3/31.
 */
public class ActivityScoreResult extends Activity {
    @Bind(R.id.lv_score_result) ListView lvScoreResult;

    String username;
    String password;
    @Bind(R.id.tv_score_name) TextView tvScoreName;
    @Bind(R.id.tv_score_term) TextView tvScoreTerm;
    @Bind(R.id.score_linearlayout_title) LinearLayout scoreLinearlayoutTitle;
    @Bind(R.id.score_line1) View scoreLine1;
    @Bind(R.id.score_line2) View scoreLine2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_result);
        ButterKnife.bind(this);

        username = getIntent().getStringExtra("name");
        password = getIntent().getStringExtra("password");

        // 使用异步联网库
        AsyncHttpClient asyClient = new AsyncHttpClient();

        String url = "http://cloud.bmob.cn/f791b9071ca67fee/getScore";

        RequestParams params = new RequestParams();
        params.put("username", username); // 设置请求的参数名和参数值
        params.put("password", password);// 设置请求的参数名和参数

        asyClient.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String datas = new String(bytes);
                ScoreSearchResult results = Json2scoreResult(datas);

                tvScoreName.setText(results.name);
                tvScoreTerm.setText(results.term);

                if (results.name.equals("用户名或密码错误")) {
                    Toast.makeText(ActivityScoreResult.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                } else {

                    scoreLinearlayoutTitle.setVisibility(View.VISIBLE);
                    scoreLine1.setVisibility(View.VISIBLE);
                    scoreLine2.setVisibility(View.VISIBLE);
                    AdapterScore adapter = new AdapterScore(results.scoreResults, ActivityScoreResult.this);
                    lvScoreResult.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(ActivityScoreResult.this, "查询失败", Toast.LENGTH_SHORT).show();
            }
        });


    }

    // 将获取到的json数据转化成对象
    public ScoreSearchResult Json2scoreResult(String json) {

        ScoreSearchResult result = new ScoreSearchResult();

        result.scoreResults = new ArrayList<ScoreResult>();
        try {
            JSONObject root = new JSONObject(json);
            result.name = root.getString("name"); // 姓名
            result.term = root.getString("term"); // 学期

            if (result.name.equals("用户名或密码错误")) {
                return result;
            }
            // 获取到所有的成绩
            JSONArray arr = root.getJSONArray("score");

            for (int i = 0; i < arr.length(); i++) {
                ScoreResult score = new ScoreResult();
                JSONObject obj = arr.getJSONObject(i);
                score.setCourse(obj.get("course").toString());
                score.setScore(obj.get("score").toString());
                score.setCredit(obj.get("credit").toString());

                result.scoreResults.add(score);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    class ScoreSearchResult {

        String name; // 用户名（学号）
        String term; // 学期
        ArrayList<ScoreResult> scoreResults; // 各门成绩
    }
}
