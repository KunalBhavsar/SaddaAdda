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
import com.emiadda.server.UpdateCustomerParams;
import com.emiadda.utils.AppUtils;
import com.emiadda.wsdl.DistrictModel;
import com.emiadda.wsdl.RegionModel;
import com.emiadda.wsdl.VolunteerModel;
import com.google.gson.Gson;
import com.google.gson.internal.Streams;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class RegisterActivity extends AppCompatActivity implements IWsdl2CodeEvents, View.OnClickListener {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private static final String REG_CUST = "registerCustomer";
    private static final String FETCH_ZONE = "fetchZoneDO";
    private static final String FETCH_DISTRICT = "fetchDistrictAndVolunteer";

    private EditText edtFirstName, edtLastName, edtMobileNumber, edtEmail, edtDOB, edtGender;
    private EditText edtAddress, edtLandMark, edtSubZone, edtArea, edtAgency, edtPasscode;
    private EditText edtPassword, edtConfirmPassword;

    private String firstName, lastName, mobileNumber, email, dateOfBirth, gender;
    private String address, landMark, passcode;
    private String password, confirmPassword;

    private Button btnContinue;
    private RelativeLayout rltProgress;
    private Context mAppContext;
    private IWsdl2CodeEvents eventHandler;
    private Toolbar toolbar;
    private int year, month, day;
    private String dob;
    private Calendar calendar;

    private List<RegionModel> zoneModels;
    private RegionModel selectedZone;

    private List<RegionModel> districtModels;
    private RegionModel selectedDistrict;

    private List<VolunteerModel> agencyList;
    private VolunteerModel selectedAgency;

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
        edtMobileNumber = (EditText) findViewById(R.id.edt_mobile_number);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtDOB = (EditText) findViewById(R.id.edt_date_of_birth);
        edtGender = (EditText) findViewById(R.id.edt_gender);

        edtAddress = (EditText) findViewById(R.id.edt_address);
        edtLandMark = (EditText) findViewById(R.id.edt_landmark);
        edtSubZone = (EditText) findViewById(R.id.edt_subzone);
        edtArea = (EditText) findViewById(R.id.edt_area);
        edtAgency = (EditText) findViewById(R.id.edt_agency);
        edtPasscode = (EditText) findViewById(R.id.edt_postcode);

        edtPassword = (EditText) findViewById(R.id.edt_password);
        edtConfirmPassword = (EditText) findViewById(R.id.edt_confirm_password);
        btnContinue = (Button) findViewById(R.id.btn_edit);
        rltProgress = (RelativeLayout) findViewById(R.id.rlt_progress);
        btnContinue.setOnClickListener(this);
        edtGender.setOnClickListener(this);
        edtDOB.setOnClickListener(this);
        edtSubZone.setOnClickListener(this);
        edtArea.setOnClickListener(this);
        edtAgency.setOnClickListener(this);
        landMark = "";

        eventHandler = this;
        mAppContext = this;
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        if (AppUtils.isNetworkAvailable(mAppContext)) {
            rltProgress.setVisibility(View.VISIBLE);
            try {
                new Server(eventHandler).fetchZoneDOAsync("");
            } catch (Exception e) {
                e.printStackTrace();
            }
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
    public void Wsdl2CodeFinished(final String methodName, final Object Data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rltProgress.setVisibility(View.GONE);

                if (Data != null && !Data.equals("")) {
                    switch (methodName) {
                        case REG_CUST:
                            try {
                                JSONObject jsonObject = new JSONObject(Data.toString());
                                String error = (String) jsonObject.get("error_warning");
                                showToast(error);

                            } catch (Exception e) {
                                /*try {
                                    JSONObject jsonObject = new JSONObject(Data.toString());
                                    String result = (String) jsonObject.get("result");

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }*/

                                showToast("Registration successful...");
                                finish();
                            }

                            break;
                        case FETCH_ZONE:
                            String jsonResponse = new Gson().toJson(Data);
                            jsonResponse = jsonResponse.replaceAll("\\\\", "");
                            jsonResponse = jsonResponse.substring(2, jsonResponse.length() - 2);
                            Log.i(TAG, jsonResponse);
                            zoneModels = Arrays.asList(new Gson().fromJson(jsonResponse, RegionModel[].class));
                            break;
                        case FETCH_DISTRICT:
                            jsonResponse = new Gson().toJson(Data);
                            Log.i(TAG, jsonResponse);
                            jsonResponse = jsonResponse.replaceAll("\\\\", "").replace("\"[", "[").replace("]\"", "]");
                            jsonResponse = jsonResponse.substring(1, jsonResponse.length() - 1);
                            Log.i(TAG, jsonResponse);
                            DistrictModel districtModel = new Gson().fromJson(jsonResponse, DistrictModel.class);
                            districtModels = districtModel.getDistrictData();
                            agencyList = districtModel.getVolunteerData();
                            break;

                    }
                } else {
                    Toast.makeText(mAppContext, "Some error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void Wsdl2CodeFinishedWithException(final Exception ex) {
        ex.printStackTrace();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rltProgress.setVisibility(View.GONE);
                Log.e(TAG, ex.getMessage());
            }
        });
    }

    @Override
    public void Wsdl2CodeEndedRequest() {

    }

    @Override
    public void onClick(View v) {
        AppUtils.hideKeyboard(this);
        switch (v.getId()) {
            case R.id.edt_gender:
                final CharSequence[] genderStringList = {"Male", "Female"};
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mAppContext);
                dialogBuilder.setTitle("Select gender");
                dialogBuilder.setItems(genderStringList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        edtGender.setText(genderStringList[i]);
                    }
                });
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                break;

            case R.id.edt_date_of_birth:
                showDialog(999);
                break;

            case R.id.btn_edit:
                if (isValidationSuccessful()) {
                    createRegRequest();
                }
                break;
            case R.id.edt_subzone:
                if (zoneModels != null) {
                    final String[] zoneStringList = new String[zoneModels.size()];
                    int i = 0;
                    for (RegionModel regionModel : zoneModels) {
                        zoneStringList[i++] = regionModel.getRegion_name();
                    }
                    dialogBuilder = new AlertDialog.Builder(mAppContext);
                    dialogBuilder.setTitle("Select Zone");
                    dialogBuilder.setItems(zoneStringList, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            selectedZone = zoneModels.get(i);
                            districtModels = null;
                            selectedDistrict = null;
                            agencyList = null;
                            selectedAgency = null;
                            edtSubZone.setText(selectedZone.getRegion_name());
                            edtArea.setText("");
                            edtAgency.setText("");

                            if (AppUtils.isNetworkAvailable(mAppContext)) {
                                rltProgress.setVisibility(View.VISIBLE);
                                try {
                                    new Server(eventHandler).fetchDistrictAndVolunteerAsync(selectedZone.getRegion_id());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(mAppContext, mAppContext.getString(R.string.no_network_toast), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    alertDialog = dialogBuilder.create();
                    alertDialog.show();
                }
                break;
            case R.id.edt_area:
                if (districtModels != null) {
                    final String[] districtStringList = new String[districtModels.size()];
                    int i = 0;
                    for (RegionModel regionModel : districtModels) {
                        districtStringList[i++] = regionModel.getRegion_name();
                    }
                    dialogBuilder = new AlertDialog.Builder(mAppContext);
                    dialogBuilder.setTitle("Select Area");
                    dialogBuilder.setItems(districtStringList, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            selectedDistrict = districtModels.get(i);
                            edtArea.setText(selectedDistrict.getRegion_name());
                        }
                    });
                    alertDialog = dialogBuilder.create();
                    alertDialog.show();
                }
                break;

            case R.id.edt_agency:
                if (agencyList != null) {
                    final String[] agencyStringList = new String[agencyList.size()];
                    int i = 0;
                    for (VolunteerModel volunteerModel : agencyList) {
                        agencyStringList[i++] = volunteerModel.getVol();
                    }
                    dialogBuilder = new AlertDialog.Builder(mAppContext);
                    dialogBuilder.setTitle("Select Agency");
                    dialogBuilder.setItems(agencyStringList, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            selectedAgency = agencyList.get(i);
                            edtAgency.setText(selectedAgency.getVol());
                        }
                    });
                    alertDialog = dialogBuilder.create();
                    alertDialog.show();
                }
                break;
        }
    }

    private void createRegRequest() {
        SoapObject soapObject = new SoapObject();
        soapObject.addProperty(getPropertyInfo("firstname", firstName, PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("lastname", lastName, PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("email", email, PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("telephone", mobileNumber, PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("input-gender", gender, PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("dob", dob, PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("franchise", "", PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("volunteer", selectedAgency.getVol(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("volunteer_code", selectedAgency.getVolid(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("vol", selectedAgency.getVolid(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("customer_group_id", "1", PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("address_1", address, PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("address_2", landMark, PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("zone_do_id", selectedZone.getRegion_id(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("city_id", selectedDistrict.getRegion_id(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("postcode", passcode, PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("password", password, PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("confirm", confirmPassword, PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("newsletter", String.valueOf(1), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("agree", String.valueOf(1), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("service", String.valueOf(1), PropertyInfo.STRING_CLASS));

        UpdateCustomerParams updateCustomerParams = new UpdateCustomerParams(soapObject);
        try {
            rltProgress.setVisibility(View.VISIBLE);
            new Server(eventHandler).registerCustomerAsync(updateCustomerParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PropertyInfo getPropertyInfo(String name, String value, Class type) {
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setType(type);
        propertyInfo.setValue(value);
        propertyInfo.setName(name);
        return propertyInfo;
    }

    public boolean isValidationSuccessful() {
        firstName = edtFirstName.getText().toString().trim();
        lastName = edtLastName.getText().toString().trim();
        gender = edtGender.getText().toString().trim();
        email = edtEmail.getText().toString().trim();
        mobileNumber = edtMobileNumber.getText().toString().trim();
        dateOfBirth = edtDOB.getText().toString().trim();

        address = edtAddress.getText().toString().trim();
        landMark = edtLandMark.getText().toString().trim();
        passcode = edtPasscode.getText().toString().trim();

        password = edtPassword.getText().toString().trim();
        confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (firstName.equals("")) {
            showToast("Please enter first name");
            return false;
        } else if (lastName.equals("")) {
            showToast("Please enter last name");
            return false;
        } else if (mobileNumber.equals("") || mobileNumber.length() < 10) {
            showToast("Please enter valid mobile number");
            return false;
        } else if (email.equals("") || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter valid email");
            return false;
        } else if (dateOfBirth.equals("")) {
            showToast("Please enter date of birth");
            return false;
        } else if (gender.equals("")) {
            showToast("Please select gender");
            return false;
        } else if (address.equals("")) {
            showToast("Please enter address");
            return false;
        } else if (selectedZone == null) {
            showToast("Please select subzone");
            return false;
        } else if (selectedDistrict == null) {
            showToast("Please select area");
            return false;
        } else if (selectedAgency == null) {
            showToast("Please select agency");
            return false;
        } else if (passcode.equals("")) {
            showToast("Please enter postcode");
            return false;
        } else if (passcode.equals("")) {
            showToast("Please enter password");
            return false;
        } else if (confirmPassword.isEmpty() || !confirmPassword.equals(password)) {
            showToast("Please re enter proper password");
            edtPassword.setText("");
            edtConfirmPassword.setText("");
            return false;
        } else if (passcode.length() < 6) {
            showToast("Your password must be at least 6 characters long. Please try another.");
            return false;
        } else if (address.length() < 3) {
            showToast("Address must be at least 3 characters long.");
            return false;
        } else if (!landMark.equals("") && landMark.length() < 3) {
            showToast("Landmark must be at least 3 characters long.");
            return false;
        }

        return true;
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
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
            dob = arg1 + "-" + (arg2 + 1) + "-" + arg3;
            edtDOB.setText(dob);
        }
    };
}


/**
 * > For Registration Service -
 * > 1. franchise (should be empty)
 * > 2. customer_group_id (should be 1)
 * > 3. vol and volunteer_code (will contain the volunteer id of the selected volunteer)(List of volunteers can be get by fetchDistrictAndVolunteer Method by passing the selected zone do id)
 * > 4. volunteer (will contain the volunteer name of the selected volunteer)(List of volunteers can be get by fetchDistrictAndVolunteer Method by passing the selected zone do id)
 * > 5. city_id (will contain the selected district id)(List of district can be get by fetchDistrictAndVolunteer Method by passing the selected zone do id)
 * > 6. zone_do_id ( will contain the list of zone do that can be get by fetchZoneDO method)
 * > 7. newsletter (1- yes,0 - no)
 * > 8. agree (1 - yes)(to agree with terms)
 * >
 * > **To be Noted :
 * > Branch Office - Zone do
 * > Area - city / district
 * > Agency - volunteer
 */