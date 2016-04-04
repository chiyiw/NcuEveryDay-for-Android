package com.wangpeng.ncueveryday;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import com.wangpeng.ncueveryday.score.ActivityScore;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wangpeng on 16/4/1.
 */
public class ActivitySplash extends Activity {
    @Bind(R.id.splash_logo) ImageView splashLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        ButterKnife.bind(this);

        ObjectAnimator animator = ObjectAnimator.ofFloat(splashLogo, "translationY", 0, 160);
        animator.setDuration(1000);
        animator.setStartDelay(900);
        animator.setInterpolator(new OvershootInterpolator(1.5f));
        animator.start();

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(splashLogo, "scaleX", 0.1f, 1.0f);
        animator1.setDuration(1000);
        animator1.setStartDelay(100);
        animator1.start();
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(splashLogo, "scaleY", 0.1f, 1.0f);
        animator2.setDuration(1000);
        animator2.setStartDelay(100);
        animator2.start();

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(ActivitySplash.this, MainActivity.class);
                ActivitySplash.this.startActivity(intent);
                ActivitySplash.this.finish();
            }
        };

        timer.schedule(task,2000);
    }
}
