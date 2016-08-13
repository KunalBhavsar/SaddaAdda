package com.emiadda.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.emiadda.R;
import com.emiadda.asynctasks.LoginAsync;
import com.emiadda.interafaces.ServerResponseSubscriber;
import com.emiadda.server.ServerResponse;
import com.emiadda.utils.AppPreferences;
import com.emiadda.utils.AppUtils;
import com.emiadda.wsdl.CustomerModel;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Shraddha on 30/6/16.
 */
public class LoginActivity extends ActionBarActivity implements View.OnClickListener, ServerResponseSubscriber {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_CODE_LOGIN = 11;

    private Button btnSubmit;
    private EditText edtEmail, edtPassword;
    private ProgressDialog progressDialog;
    private Activity mActivityContext;
    private TextView btnRegister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(AppPreferences.getInstance().isUserLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.actvity_login);
        mActivityContext = this;

        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnRegister = (TextView) findViewById(R.id.btn_register);
        btnSubmit.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if (validationCheck(v)) {
                    if(progressDialog != null && !progressDialog.isShowing()) {
                       progressDialog.show();
                    }
                    new LoginAsync(this, REQUEST_CODE_LOGIN).execute(edtEmail.getText().toString(), edtPassword.getText().toString());
                }
                break;
            case R.id.btn_register:
                if(AppUtils.isNetworkAvailable(this)) {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
/*                    String regUrl = "http://www.mydevsystems.com/dev/emiaddanew/index.php?route=account/registercust";
                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(regUrl));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setPackage("com.android.chrome");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException ex) {
                        // Chrome browser presumably not installed so allow user to choose instead
                        intent.setPackage(null);
                        startActivity(intent);
                    }*/
                } else {
                    Toast.makeText(mActivityContext, "Please check internet connection", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private boolean validationCheck(View v) {
        if (edtEmail.getText().toString().trim().equals("")) {
            Snackbar.make(v, "Please enter email", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (edtPassword.getText().toString().trim().equals("")) {
            Snackbar.make(v, "Please enter password", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void responseReceived(ServerResponse response, int requestCode, int extraRequestCode, String activityTag) {
        if(requestCode == REQUEST_CODE_LOGIN) {
            if(progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if(response == null) {
                Log.e(TAG, "Error in processing login call");
                return;
            }

            if(response.getResponseStatus() == ServerResponse.SERVER_OK) {
                Log.i(TAG, "received response : "+response);
                try {
                    final CustomerModel customerModel = new Gson().fromJson(new JSONObject(response.getResponse()).toString(), CustomerModel.class);

                    if(customerModel.getCustomer_id() == null) {
                        Snackbar.make(btnSubmit, "Wrong username and password combination.", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    AppPreferences.getInstance().setUserLoggegIn(true);
                    AppPreferences.getInstance().setAppOwnerData(customerModel);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mActivityContext, "Welcome "+customerModel.getFirstname(), Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }
                    });

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    boolean falseResponse = Boolean.getBoolean(response.getResponse());
                    if(!falseResponse) {
                        Toast.makeText(mActivityContext, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            else if(response.getResponseStatus() == ServerResponse.SERVER_ERROR){
                Toast.makeText(mActivityContext, "Error in logging in", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
