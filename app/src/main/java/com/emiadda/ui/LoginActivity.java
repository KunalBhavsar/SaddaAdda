package com.emiadda.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.emiadda.Async.LoginAsync;
import com.emiadda.R;

import org.ksoap2.serialization.SoapSerializationEnvelope;

/**
 * Created by Shraddha on 30/6/16.
 */
public class LoginActivity extends ActionBarActivity implements View.OnClickListener{

    private Button btnSubmit;
    private EditText edtEmail, edtPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_login);

        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if(validationCheck(v)) {

                    new LoginAsync(edtEmail.getText().toString(), edtPassword.getText().toString()).execute();

                    /*Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);*/
                }
                break;

        }
    }

    private boolean validationCheck(View v) {
        if(edtEmail.getText().toString().trim().equals("")) {

            Snackbar.make(v, "Please enter email", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(edtPassword.getText().toString().trim().equals("")) {
            Snackbar.make(v, "Please enter password", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
