package com.emiadda.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.emiadda.R;
import com.emiadda.utils.AppUtils;

public class RegisterActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressDialog progressDialog;
    private String regUrl = "http://www.mydevsystems.com/dev/emiaddanew/index.php?route=account/registercust";
    private String errorUrl = "file:///android_asset/error.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        webView = (WebView) findViewById(R.id.webview);
        if(AppUtils.isNetworkAvailable(this)) {
            progressDialog.show();
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

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressDialog.dismiss();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                view.loadUrl(errorUrl);
            }
        });

    }
}
