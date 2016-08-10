package com.emiadda.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.emiadda.R;

import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SplashActivity extends AppCompatActivity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_product_recycler_view);
        final ImageView imageView = (ImageView) findViewById(R.id.image);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.translate);
        imageView.startAnimation(animation);



        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);

    }
}
