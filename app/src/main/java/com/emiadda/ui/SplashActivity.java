package com.emiadda.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.emiadda.R;

public class SplashActivity extends AppCompatActivity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_product_recycler_view);
        final ImageView imageView = (ImageView) findViewById(R.id.image);
        Button button = (Button) findViewById(R.id.button);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.translate);
        imageView.startAnimation(animation);
        button.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(SplashActivity.this, imageView,"Product");
                startActivity(intent, activityOptionsCompat.toBundle());
            }
        });

    }
}
