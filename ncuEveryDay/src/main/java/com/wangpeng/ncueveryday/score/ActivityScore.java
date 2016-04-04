package com.wangpeng.ncueveryday.score;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.wangpeng.ncueveryday.R;

import java.util.prefs.Preferences;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ActivityScore extends Activity {

    @Bind(R.id.btn_score_search) Button btnScoreSearch;
    @Bind(R.id.et_score_name) EditText etScoreName;
    @Bind(R.id.et_score_password) EditText etScorePassword;
    @Bind(R.id.cb_score_remember) CheckBox cbScoreRemember;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.score_activity_main);
        ButterKnife.bind(this);

        preferences = getSharedPreferences("score", Context.MODE_PRIVATE);

        etScoreName.setText(preferences.getString("name",""));
        etScorePassword.setText(preferences.getString("password",""));

        btnScoreSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = etScoreName.getText().toString();
                String password = etScorePassword.getText().toString();
                boolean rememberPwd = cbScoreRemember.isChecked();

                if (name.equals("") || password.equals("") || name.trim().length() != 10){
                    Toast.makeText(ActivityScore.this, "账号格式不正确",Toast.LENGTH_SHORT).show();
                }else {
                    // 是否记住密码
                    if (rememberPwd) {

                        SharedPreferences.Editor editor = preferences.edit();

                        editor.putString("name", name);
                        editor.putString("password", password);
                        editor.apply();
                    }

                    Intent intent = new Intent(ActivityScore.this, ActivityScoreResult.class);
                    intent.putExtra("name",name);
                    intent.putExtra("password", password);
                    ActivityScore.this.startActivity(intent);
                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.score_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_return) {

            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
