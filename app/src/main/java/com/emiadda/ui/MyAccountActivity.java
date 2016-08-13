package com.emiadda.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.emiadda.R;
import com.emiadda.server.IWsdl2CodeEvents;
import com.emiadda.server.Server;
import com.emiadda.utils.AppPreferences;
import com.emiadda.utils.AppUtils;
import com.emiadda.utils.KeyConstants;
import com.emiadda.wsdl.CustomerModel;
import com.google.gson.Gson;

import org.json.JSONObject;

public class MyAccountActivity extends AppCompatActivity implements IWsdl2CodeEvents, View.OnClickListener {

    private static final int GET_PROFILE_REQUEST_CODE = 51;
    private static final String TAG = MyAccountActivity.class.getSimpleName();

    private EditText edtFirstName, edtLastName, edtEmail, edtMobile;
    private Button btnEdit;
    private RelativeLayout rltProgress;
    private Context mAppContext;
    private int serverReqStatus;
    private IWsdl2CodeEvents eventHandler;
    private Toolbar toolbar;
    private boolean editMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        edtFirstName = (EditText) findViewById(R.id.edt_first_name);
        edtLastName = (EditText) findViewById(R.id.edt_last_name);
        edtMobile = (EditText) findViewById(R.id.edt_mobile);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        rltProgress = (RelativeLayout) findViewById(R.id.rlt_progress);
        btnEdit = (Button) findViewById(R.id.btn_edit);
        btnEdit.setOnClickListener(this);

        eventHandler = this;
        mAppContext = this;

        if(AppUtils.isNetworkAvailable(mAppContext)) {

            if(!AppPreferences.getInstance().isUserLoggedIn()) {
                return;
            }

            rltProgress.setVisibility(View.VISIBLE);
            serverReqStatus = KeyConstants.SERVER_CALL_STATUS_ONGOING;
            String custID = AppPreferences.getInstance().getAppOwnerData().getCustomer_id();
            try {
                new Server(eventHandler).fetchCustomerInfoAsync(custID);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        else {
            Toast.makeText(mAppContext, mAppContext.getString(R.string.no_network_toast), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void Wsdl2CodeStartedRequest() {}

    @Override
    public void Wsdl2CodeFinished(String methodName, final Object data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rltProgress.setVisibility(View.GONE);
                if(data != null) {
                    if(!data.toString().equals("")) {
                        try {
                            JSONObject jsonObject = new JSONObject(data.toString());
                            JSONObject custJson =  jsonObject.getJSONObject("customer_info");
                            CustomerModel customerModel = new Gson().fromJson(custJson.toString(), CustomerModel.class);
                            setProfileUI(customerModel);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });
        Log.i(TAG, data.toString());

    }

    private void setProfileUI(CustomerModel customerModel) {
        edtFirstName.setText(customerModel.getFirstname());
        edtLastName.setText(customerModel.getLastname());
        edtEmail.setText(customerModel.getEmail());
        edtMobile.setText(customerModel.getTelephone());

    }

    @Override
    public void Wsdl2CodeFinishedWithException(Exception ex) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rltProgress.setVisibility(View.GONE);
            }
        });
        Log.i(TAG, ex.getMessage(), ex);
    }

    @Override
    public void Wsdl2CodeEndedRequest() {}

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
            case R.id.btn_edit:
                if(isEditMode()) {
                    setEditMode(false);
                    setEditableFields(false);
                    btnEdit.setText("Edit");
                    //TODO: server call to update, change shared pref values

                } else {
                    setEditMode(true);
                    setEditableFields(true);
                    btnEdit.setText("Save");

                }
                break;
        }
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    private void setEditableFields(boolean editMode) {
        edtEmail.setEnabled(editMode);
        edtFirstName.setEnabled(editMode);
        edtLastName.setEnabled(editMode);
        edtMobile.setEnabled(editMode);

    }
}
