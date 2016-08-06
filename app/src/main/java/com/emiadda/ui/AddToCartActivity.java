package com.emiadda.ui;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.emiadda.EAApplication;
import com.emiadda.R;
import com.emiadda.utils.AppPreferences;
import com.emiadda.utils.KeyConstants;
import com.emiadda.wsdl.ProductModel;

import java.util.ArrayList;
import java.util.List;

public class AddToCartActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String SAVED_INSTANCE_SELECTED_PAYMENT_TYPE = "payment_type";
    private static final String SAVED_INSTANCE_FROM_CART = "from_cart";

    private Toolbar toolbar;
    private TextView txtEmi, txtDirectPayment;
    private RelativeLayout rltEmi;
    private Button btnConfirmOrder;

    private Spinner spnrEmiCount;
    private TextView txtDownPaymentValue, txtEmiPriceValue, txtNetEmiValue, txtTotalValue;
    private TextView txtPaybleDPValue, txtPaybleDPTitle, txtTaxesValue, txtDeliveryChargesValue, txtTotalPayableValue;
    String currentCartType, selectedItemPaymentType;

    Context mAppContext;
    Activity mActivityContext;

    ProductModel productModel;
    boolean fromCart;
    private int numberOfEmiSelected;
    private boolean showEmi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityContext = this;
        mAppContext = getApplicationContext();

        productModel = EAApplication.getTransientSelectedProductModel();

        setContentView(R.layout.activity_make_payment);
        txtEmi = (TextView) findViewById(R.id.txt_emi_lable);
        txtDirectPayment = (TextView) findViewById(R.id.txt_direct_lable);
        rltEmi = (RelativeLayout) findViewById(R.id.rel_emi);
        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        btnConfirmOrder = (Button) findViewById(R.id.btn_confirm_order);

        spnrEmiCount = (Spinner) findViewById(R.id.spnr_emi_count);
        txtDownPaymentValue = (TextView) findViewById(R.id.txt_down_payment_value);
        txtEmiPriceValue = (TextView) findViewById(R.id.txt_emi_price_value);
        txtNetEmiValue = (TextView) findViewById(R.id.txt_net_emi_value);
        txtTotalValue = (TextView) findViewById(R.id.txt_total_value);

        txtPaybleDPTitle = (TextView) findViewById(R.id.txt_payable_down_payment);
        txtPaybleDPValue = (TextView) findViewById(R.id.txt_payable_down_payment_value);

        txtTaxesValue = (TextView) findViewById(R.id.txt_taxes_value);
        txtDeliveryChargesValue = (TextView) findViewById(R.id.txt_delivery_charges_value);
        txtTotalPayableValue = (TextView) findViewById(R.id.txt_total_payable_value);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (savedInstanceState != null) {
            fromCart = savedInstanceState.getBoolean(SAVED_INSTANCE_FROM_CART);
            selectedItemPaymentType = savedInstanceState.getString(SAVED_INSTANCE_SELECTED_PAYMENT_TYPE);
        }
        else {
            fromCart = getIntent().hasExtra(KeyConstants.INTENT_CONSTANT_PRODUCT_ITEM_SELECTED_COUNT);
        }

        currentCartType = AppPreferences.getInstance().getCartType();

        txtDirectPayment.setOnClickListener(this);
        txtEmi.setOnClickListener(this);
        btnConfirmOrder.setOnClickListener(this);

        btnConfirmOrder.setText("ADD TO CART");

        showEmi = Integer.parseInt(productModel.getShow_payment_option()) > 0;

        if(showEmi) {
            txtEmi.setVisibility(View.VISIBLE);
            double totalEMIAmount = Double.parseDouble(productModel.getEmi_last_price());
            double downPayment = Double.parseDouble(productModel.getDown_payment());
            if((totalEMIAmount - downPayment) > 200d) {
                txtPaybleDPValue.setText(String.valueOf(totalEMIAmount * Integer.parseInt(productModel.getQuantity())));
                int i = 1;
                List<String> spnrValues = new ArrayList<>();
                while ((totalEMIAmount / i) > 200d && i <= 20) {
                    spnrValues.add(String.valueOf(i));
                    i++;
                }

                // Spinner click listener
                spnrEmiCount.setOnItemSelectedListener(this);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spnrValues);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnrEmiCount.setAdapter(dataAdapter);

                if(numberOfEmiSelected == 0) {
                    numberOfEmiSelected = 1;
                }

                if(selectedItemPaymentType != null) {
                    if(selectedItemPaymentType.isEmpty() || selectedItemPaymentType.equals(AppPreferences.CART_TYPE_VALUE_EMI)) {
                        handleUIForPaymentType(true);
                    }
                    else if(selectedItemPaymentType.equals(AppPreferences.CART_TYPE_VALUE_DIRECT_PAYMENT)) {
                        handleUIForPaymentType(false);
                    }
                }
                else if(currentCartType.isEmpty() || currentCartType.equals(AppPreferences.CART_TYPE_VALUE_EMI)) {
                    handleUIForPaymentType(true);
                }
                else if(currentCartType.equals(AppPreferences.CART_TYPE_VALUE_DIRECT_PAYMENT)) {
                    handleUIForPaymentType(false);
                }
            }
            else {
                if((selectedItemPaymentType != null && (selectedItemPaymentType.isEmpty() || selectedItemPaymentType.equals(AppPreferences.CART_TYPE_VALUE_DIRECT_PAYMENT)))
                        || currentCartType.isEmpty() || currentCartType.equals(AppPreferences.CART_TYPE_VALUE_DIRECT_PAYMENT)) {
                    handleUIForPaymentType(false);
                }
                else {
                    Toast.makeText(mAppContext, "EMI option not available for selected product, clear your cart for direct payment", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
        else {
            txtEmi.setVisibility(View.GONE);
            if((selectedItemPaymentType != null && (selectedItemPaymentType.isEmpty() || selectedItemPaymentType.equals(AppPreferences.CART_TYPE_VALUE_DIRECT_PAYMENT)))
                    || currentCartType.isEmpty() || currentCartType.equals(AppPreferences.CART_TYPE_VALUE_DIRECT_PAYMENT)) {
                handleUIForPaymentType(false);
            }
            else {
                Toast.makeText(mAppContext, "EMI option not available for selected product, clear your cart for direct payment", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void handleUIForPaymentType(boolean emiUi) {
        int quantity = Integer.parseInt(String.valueOf(productModel.getQuantity()));
        if(!emiUi) {
            selectedItemPaymentType = AppPreferences.CART_TYPE_VALUE_DIRECT_PAYMENT;
            txtEmi.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            txtEmi.setTextColor(ContextCompat.getColor(this, R.color.dark_grey));
            txtDirectPayment.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray));
            txtDirectPayment.setTextColor(ContextCompat.getColor(this, R.color.black));

            rltEmi.setVisibility(View.GONE);
            txtPaybleDPTitle.setText("Amount");

            double totalOTPAmount = Double.parseDouble(productModel.getOtp_last_price()) * quantity;
            txtPaybleDPValue.setText(String.valueOf(totalOTPAmount));

            double taxAmount = Double.parseDouble(productModel.getTax_data() != null ? String.valueOf(productModel.getTax_data().getTax_amt()) : "0.00")
                    * quantity;
            txtTaxesValue.setText(String.valueOf(taxAmount));

            double deliveryCharges = Double.valueOf(productModel.getShipping_charge()) * quantity;
            txtDeliveryChargesValue.setText(String.valueOf(deliveryCharges));

            txtTotalPayableValue.setText(String.valueOf(totalOTPAmount + taxAmount + deliveryCharges));
        }
        else {
            selectedItemPaymentType = AppPreferences.CART_TYPE_VALUE_EMI;
            txtEmi.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray));
            txtEmi.setTextColor(ContextCompat.getColor(this, R.color.black));
            txtDirectPayment.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            txtDirectPayment.setTextColor(ContextCompat.getColor(this, R.color.dark_grey));

            rltEmi.setVisibility(View.VISIBLE);
            txtPaybleDPTitle.setText("Down Payment");

            double totalEMIAmount = Double.parseDouble(productModel.getEmi_last_price()) * quantity;
            double downPayment = Double.parseDouble(productModel.getDown_payment()) * quantity;
            if((totalEMIAmount - downPayment) > 200d && showEmi) {
                txtPaybleDPValue.setText(String.valueOf(totalEMIAmount));

                double taxAmount = Double.parseDouble(productModel.getTax_data() != null ? String.valueOf(productModel.getTax_data().getTax_amt()) : "0.00") * quantity;
                txtTaxesValue.setText(String.valueOf(taxAmount));

                double deliveryCharges = Double.valueOf(productModel.getShipping_charge()) * quantity;
                txtDeliveryChargesValue.setText(String.valueOf(deliveryCharges));

                txtTotalPayableValue.setText(String.valueOf(downPayment + taxAmount + deliveryCharges));

                txtPaybleDPValue.setText(String.valueOf(downPayment));
                txtDownPaymentValue.setText(String.valueOf(downPayment));

                double emiValue = (totalEMIAmount - downPayment) / numberOfEmiSelected;
                txtEmiPriceValue.setText(String.format("%.2f", emiValue));

                txtNetEmiValue.setText(String.valueOf(totalEMIAmount - downPayment));

                txtTotalValue.setText(String.valueOf(totalEMIAmount));
            }
            else {
                Toast.makeText(mAppContext, "EMI option not available for selected product", Toast.LENGTH_SHORT).show();
                handleUIForPaymentType(false);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(SAVED_INSTANCE_SELECTED_PAYMENT_TYPE, selectedItemPaymentType);
        outState.putBoolean(SAVED_INSTANCE_FROM_CART, fromCart);
        super.onSaveInstanceState(outState);
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
                if(currentCartType.equals(AppPreferences.CART_TYPE_VALUE_DIRECT_PAYMENT)) {
                    Toast.makeText(mAppContext, "Checkout all the Direct payment items from your cart for using EMI option", Toast.LENGTH_LONG).show();
                    return;
                }
                handleUIForPaymentType(true);
                break;

            case R.id.txt_direct_lable:
                if(currentCartType.equals(AppPreferences.CART_TYPE_VALUE_DIRECT_PAYMENT)) {
                    Toast.makeText(mAppContext, "Checkout all the EMI items from your cart for using Direct payment option", Toast.LENGTH_LONG).show();
                    return;
                }
                handleUIForPaymentType(false);
                break;
            case R.id.btn_confirm_order:
                Toast.makeText(mAppContext, "Added " + productModel.getQuantity() + " items of " + productModel.getName() + " to the cart", Toast.LENGTH_SHORT).show();
                AppPreferences.getInstance().addProductToCartList(productModel, selectedItemPaymentType);

                if(fromCart) {
                    mActivityContext.finish();
                    return;
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        numberOfEmiSelected = position + 1;
        handleUIForPaymentType(true);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        parent.setSelection(0);
    }
}
