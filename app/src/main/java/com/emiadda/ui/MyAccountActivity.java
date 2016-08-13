package com.emiadda.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.emiadda.R;
import com.emiadda.server.IWsdl2CodeEvents;
import com.emiadda.server.Server;
import com.emiadda.utils.AppPreferences;
import com.emiadda.utils.AppUtils;
import com.emiadda.wsdl.CustomerModel;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Calendar;

public class MyAccountActivity extends AppCompatActivity implements IWsdl2CodeEvents, View.OnClickListener {

    private static final String TAG = MyAccountActivity.class.getSimpleName();

    private EditText edtFirstName, edtLastName, edtEmail, edtMobile, edtGender, edtDob;
    private Button btnEdit;
    private RelativeLayout rltProgress;
    private Context mAppContext;
    private IWsdl2CodeEvents eventHandler;
    private Toolbar toolbar;
    private boolean editMode;
    private int year, month, day;
    private Calendar calendar;
    private CustomerModel customerModel;
    private String dob;

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
        edtGender = (EditText) findViewById(R.id.edt_gender);
        edtDob = (EditText) findViewById(R.id.edt_dob);
        rltProgress = (RelativeLayout) findViewById(R.id.rlt_progress);
        btnEdit = (Button) findViewById(R.id.btn_edit);
        btnEdit.setOnClickListener(this);
        edtGender.setOnClickListener(this);
        edtDob.setOnClickListener(this);

        eventHandler = this;
        mAppContext = this;
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        if(AppUtils.isNetworkAvailable(mAppContext)) {

            if(!AppPreferences.getInstance().isUserLoggedIn()) {
                return;
            }

            rltProgress.setVisibility(View.VISIBLE);
            customerModel = AppPreferences.getInstance().getAppOwnerData();
            String custID = customerModel.getCustomer_id();
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
                if(data != null && !data.toString().equals("")) {
                        try {
                            JSONObject jsonObject = new JSONObject(data.toString());
                            JSONObject custJson =  jsonObject.getJSONObject("customer_info");
                            customerModel = new Gson().fromJson(custJson.toString(), CustomerModel.class);
                            setProfileUI(customerModel);
                        } catch (Exception e) {
                            e.printStackTrace();
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
        edtGender.setText(customerModel.getGender());
        dob = customerModel.getDob();
        edtDob.setText(dob);
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

            case R.id.edt_gender:
                final CharSequence[] languages = {"Male", "Female" };
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mAppContext);
                dialogBuilder.setTitle("Select gender");
                dialogBuilder.setItems(languages,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       edtGender.setText(languages[i]);
                    }
                });
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                break;

            case R.id.edt_dob:
                showDialog(999);
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            //showDate(arg1, arg2+1, arg3);
            edtDob.setText(arg3 + "/" + (arg2+1) + "/" + arg1);
        }
    };


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
        edtGender.setEnabled(editMode);
        edtDob.setEnabled(editMode);
    }

    private void setCustomerModel() {
        customerModel.setFirstname(edtFirstName.getText().toString().trim());
        customerModel.setLastname(edtLastName.getText().toString().trim());
        customerModel.setGender(edtGender.getText().toString().trim());
        customerModel.setEmail(edtEmail.getText().toString().trim());
        customerModel.setDob(edtDob.getText().toString().trim());
        customerModel.setTelephone(edtMobile.getText().toString().trim());
    }
}
