package com.emiadda.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.emiadda.R;

public class MyAccountActivity extends AppCompatActivity {

    private EditText edtFirstName, edtLastName, edtEmail, edtMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        edtFirstName = (EditText) findViewById(R.id.edt_first_name);
        edtLastName = (EditText) findViewById(R.id.edt_last_name);
        edtMobile = (EditText) findViewById(R.id.edt_mobile);
        edtEmail = (EditText) findViewById(R.id.edt_email);

    }
}
