package com.emiadda.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.emiadda.R;
import com.emiadda.utils.AppUtils;

public class RegisterActivity extends AppCompatActivity {

    private WebView webView;
    private String regUrl = "http://www.mydevsystems.com/dev/emiaddanew/index.php?route=account/registercust";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        webView = (WebView) findViewById(R.id.webview);
        if(AppUtils.isNetworkAvailable(this)) {
            webView.loadUrl(regUrl);
        }

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.contains("route=account/account")) {
                    webView.stopLoading();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    return false;
                }

                view.loadUrl(url);
                return false;
            }


        });

    }
}
