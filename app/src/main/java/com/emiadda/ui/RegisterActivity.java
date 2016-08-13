package com.emiadda.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.emiadda.R;
import com.emiadda.server.IWsdl2CodeEvents;
import com.emiadda.utils.AppUtils;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity implements IWsdl2CodeEvents, View.OnClickListener{

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private static final String REG_CUST = "registerCustomer";
    private static final String FETCH_ZONE = "fetchZoneDO";
    private static final String FETCH_DISTRICT = "fetchDistrictAndVolunteer";

    private EditText edtFirstName, edtLastName, edtEmail, edtMobile, edtGender, edtDob, edtAddress;
    private Button btnContinue;
    private RelativeLayout rltProgress;
    private Context mAppContext;
    private IWsdl2CodeEvents eventHandler;
    private Toolbar toolbar;
    private int year, month, day;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
        edtAddress = (EditText) findViewById(R.id.edt_address);
        rltProgress = (RelativeLayout) findViewById(R.id.rlt_progress);
        btnContinue = (Button) findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(this);
        edtGender.setOnClickListener(this);
        edtDob.setOnClickListener(this);

        eventHandler = this;
        mAppContext = this;
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        if (AppUtils.isNetworkAvailable(mAppContext)) {
            rltProgress.setVisibility(View.VISIBLE);

        } else {
            Toast.makeText(mAppContext, mAppContext.getString(R.string.no_network_toast), Toast.LENGTH_SHORT).show();
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
    public void Wsdl2CodeStartedRequest() {

    }

    @Override
    public void Wsdl2CodeFinished(final String methodName, Object Data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rltProgress.setVisibility(View.GONE);
                switch (methodName) {
                    case REG_CUST:

                        break;
                    case FETCH_ZONE:

                        break;
                    case FETCH_DISTRICT:

                        break;

                }
            }
        });
    }

    @Override
    public void Wsdl2CodeFinishedWithException(Exception ex) {
        ex.printStackTrace();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rltProgress.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public void Wsdl2CodeEndedRequest() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edt_gender:
                final CharSequence[] languages = {"Male", "Female"};
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mAppContext);
                dialogBuilder.setTitle("Select gender");
                dialogBuilder.setItems(languages, new DialogInterface.OnClickListener() {
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

            case R.id.btn_continue:
                if(isValidationSuccessful()){
                    createRegRequest();
                }
                break;
        }
    }

    private void createRegRequest() {
        SoapObject soapObject = new SoapObject();
        soapObject.addProperty(getPropertyInfo("firstname", edtFirstName.getText().toString().trim(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("lastname", edtLastName.getText().toString().trim(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("email", edtFirstName.getText().toString().trim(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("telephone", edtFirstName.getText().toString().trim(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("input-gender", edtFirstName.getText().toString().trim(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("dob", edtFirstName.getText().toString().trim(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("franchise", edtFirstName.getText().toString().trim(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("volunteer", edtFirstName.getText().toString().trim(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("volunteer_code", edtFirstName.getText().toString().trim(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("vol", edtFirstName.getText().toString().trim(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("customer_group_id", String.valueOf(1), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("address_1", edtFirstName.getText().toString().trim(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("address_2", edtFirstName.getText().toString().trim(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("zone_do_id", edtFirstName.getText().toString().trim(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("city_id", edtFirstName.getText().toString().trim(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("postcode", edtFirstName.getText().toString().trim(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("password", edtFirstName.getText().toString().trim(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("confirm", edtFirstName.getText().toString().trim(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("newsletter", edtFirstName.getText().toString().trim(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("agree", edtFirstName.getText().toString().trim(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("service", edtFirstName.getText().toString().trim(), PropertyInfo.STRING_CLASS));
    }

    private PropertyInfo getPropertyInfo(String name, String value, Class type) {
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setType(type);
        propertyInfo.setValue(value);
        propertyInfo.setName(name);
        return propertyInfo;
    }

    public boolean isValidationSuccessful() {
        boolean validationSuccessful = true;

        return validationSuccessful;
    }
}
