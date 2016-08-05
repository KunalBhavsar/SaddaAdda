package com.emiadda.ui;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.emiadda.R;

public class MakePaymentActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView txtEmi, txtDirectPayment;
    private RelativeLayout rltEmi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_payment);
        txtEmi = (TextView) findViewById(R.id.txt_emi_lable);
        txtDirectPayment = (TextView) findViewById(R.id.txt_direct_lable);
        rltEmi = (RelativeLayout) findViewById(R.id.rel_emi);
        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        handleUIForPaymentType(false);
        txtDirectPayment.setOnClickListener(this);
        txtEmi.setOnClickListener(this);
    }

    private void handleUIForPaymentType(boolean emiUi) {
        if(!emiUi) {
            txtEmi.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            txtEmi.setTextColor(ContextCompat.getColor(this, R.color.dark_grey));
            txtDirectPayment.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray));
            txtDirectPayment.setTextColor(ContextCompat.getColor(this, R.color.black));
            rltEmi.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_emi_lable:

                break;

            case R.id.txt_direct_lable:

                break;
        }
    }
}
