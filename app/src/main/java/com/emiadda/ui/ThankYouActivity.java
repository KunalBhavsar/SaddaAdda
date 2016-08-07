package com.emiadda.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.emiadda.R;
import com.emiadda.utils.AppPreferences;
import com.emiadda.wsdl.AddressModel;
import com.emiadda.wsdl.CustomerModel;

public class ThankYouActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private Button btnContinue;
    private TextView txtAddress;
    private TextView txtReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);

        txtAddress = (TextView) findViewById(R.id.txt_address);
        txtReview = (TextView) findViewById(R.id.txt_review);
        btnContinue = (Button) findViewById(R.id.btn_continue);
        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        btnContinue.setOnClickListener(this);
        txtReview.setOnClickListener(this);

        CustomerModel customerModel = AppPreferences.getInstance().getAppOwnerData();
        if(null != customerModel.getAddress()) {
            AddressModel addressModel = customerModel.getAddress();
            String address = addressModel.getAddress_1() + ", " +
                                addressModel.getCity_name() +", " +
                                addressModel.getDistrict_name() +", " +
                                addressModel.getZone() +", " +
                                addressModel.getCountry() +", " + "Postcode: " +
                                addressModel.getPostcode();
            txtAddress.setText(address);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_continue:
                Intent intent = new Intent(ThankYouActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

            case R.id.txt_review:
                Toast.makeText(ThankYouActivity.this, "Coming soon...", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
