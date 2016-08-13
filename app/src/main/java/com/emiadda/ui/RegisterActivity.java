package com.emiadda.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.emiadda.R;

public class RegisterActivity extends AppCompatActivity {
    EditText edtFirstName, edtLastaName, edtMobileNumber, edtEmail, edtDOB, edtGender;
    EditText edtAddress, edtLandMark, edtSubZone, edtArea, edtPasscode;
    EditText edtUsername, edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtFirstName = (EditText) findViewById(R.id.edt_first_name);
        edtLastaName = (EditText) findViewById(R.id.edt_last_name);
        edtMobileNumber = (EditText) findViewById(R.id.edt_mobile_number);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtDOB = (EditText) findViewById(R.id.edt_date_of_birth);
        edtGender = (EditText) findViewById(R.id.edt_gender);

        edtAddress = (EditText) findViewById(R.id.edt_address);
        edtLandMark = (EditText) findViewById(R.id.edt_landmark);
        edtSubZone = (EditText) findViewById(R.id.edt_subzone);
        edtArea = (EditText) findViewById(R.id.edt_area);
        edtPasscode = (EditText) findViewById(R.id.edt_postcode);

        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtPassword = (EditText) findViewById(R.id.edt_password);
    }
}
