package com.emiadda.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.emiadda.asynctasks.ServerRequestProcessingThread;
import com.emiadda.core.EAServerRequest;
import com.emiadda.interafaces.ServerResponseSubscriber;
import com.emiadda.server.OrderParams;
import com.emiadda.server.ProductsParams;
import com.emiadda.server.ServerResponse;
import com.emiadda.server.TotalParams;
import com.emiadda.server.VectorProductsParams;
import com.emiadda.server.VectorTotalParams;
import com.emiadda.utils.AppPreferences;
import com.emiadda.utils.KeyConstants;
import com.emiadda.wsdl.CustomerModel;
import com.emiadda.wsdl.PlaceOrderResponse;
import com.emiadda.wsdl.ProductModel;
import com.google.gson.Gson;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kunal on 06/08/16.
 */

public class PlaceOrderActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, ServerResponseSubscriber {
    private static final String TAG = PlaceOrderActivity.class.getSimpleName();

    private static final String SAVED_INSTANCE_SELECTED_PAYMENT_TYPE = "payment_type";
    private static final String SAVED_INSTANCE_FROM_CART = "from_cart";
    private static final int PLACE_ORDER_REQUEST_CODE = 23;

    private Toolbar toolbar;
    private TextView txtEmi, txtDirectPayment;
    private RelativeLayout rltEmi;
    private Button btnConfirmOrder;

    private Spinner spnrEmiCount;
    private TextView txtDownPaymentValue, txtEmiPriceValue, txtNetEmiValue, txtTotalValue;
    private TextView txtPaybleDPValue, txtPaybleDPTitle, txtTaxesValue, txtDeliveryChargesValue, txtTotalPayableValue;
    private RelativeLayout rltProgress;

    private String currentCartType, selectedItemPaymentType;
    private DecimalFormat formater = new DecimalFormat("#.##");

    private Context mAppContext;
    private Activity mActivityContext;

    private ProductModel productModel;
    private List<ProductModel> productCart;
    private boolean fromCart;

    private int numberOfEmiSelected = 1;
    private boolean showEmi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityContext = this;
        mAppContext = getApplicationContext();

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

        rltProgress = (RelativeLayout)findViewById(R.id.rlt_progress);

        txtDirectPayment.setOnClickListener(this);
        txtEmi.setOnClickListener(this);
        btnConfirmOrder.setOnClickListener(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (savedInstanceState != null) {
            fromCart = savedInstanceState.getBoolean(SAVED_INSTANCE_FROM_CART);
            selectedItemPaymentType = savedInstanceState.getString(SAVED_INSTANCE_SELECTED_PAYMENT_TYPE);
        } else {
            fromCart = getIntent().getBooleanExtra(KeyConstants.INTENT_IS_FROM_CART, false);
        }

        if (fromCart) {
            productCart = AppPreferences.getInstance().getCartList();
            currentCartType = AppPreferences.getInstance().getCartType();
            if (currentCartType.equals(AppPreferences.CART_TYPE_VALUE_EMI)) {
                txtEmi.setVisibility(View.VISIBLE);
                txtDirectPayment.setVisibility(View.GONE);
                handleUIForPaymentType(true);

                double totalEmiAmount = 0;
                double totalDownPayemnt = 0;

                for (ProductModel productModel : productCart) {
                    int quantity = Integer.parseInt(String.valueOf(productModel.getQuantity()));
                    totalEmiAmount += Double.parseDouble(productModel.getEmi_last_price()) * quantity;
                    totalDownPayemnt += (Double.parseDouble(productModel.getDown_payment()) * Double.parseDouble(productModel.getPrice()) * quantity) /  100;
                }

                int i = 1;
                List<String> spnrValues = new ArrayList<>();
                while (((totalEmiAmount - totalDownPayemnt) / i) > 200d && i <= 20) {
                    spnrValues.add(String.valueOf(i));
                    i++;
                }

                // Spinner click listener
                spnrEmiCount.setOnItemSelectedListener(this);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.view_spinner_item, spnrValues);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnrEmiCount.setAdapter(dataAdapter);

            } else {
                txtEmi.setVisibility(View.GONE);
                txtDirectPayment.setVisibility(View.VISIBLE);
                handleUIForPaymentType(false);
            }
        } else {
            productModel = EAApplication.getTransientSelectedProductModel();
            showEmi = Integer.parseInt(productModel.getShow_payment_option()) > 0;
            if (showEmi) {
                txtEmi.setVisibility(View.VISIBLE);
                double totalEMIAmount = Double.parseDouble(productModel.getEmi_last_price());
                double downPayment = (Double.parseDouble(productModel.getDown_payment()) * Integer.parseInt(productModel.getQuantity())
                        * Double.parseDouble(productModel.getPrice()))/100;
                if ((totalEMIAmount - downPayment) > 200d) {
                    txtPaybleDPValue.setText(KeyConstants.rs + formater.format(totalEMIAmount * Integer.parseInt(productModel.getQuantity())));
                    int i = 1;
                    List<String> spnrValues = new ArrayList<>();
                    while (((totalEMIAmount - downPayment) / i) > 200d && i <= 20) {
                        spnrValues.add(String.valueOf(i));
                        i++;
                    }

                    // Spinner click listener
                    spnrEmiCount.setOnItemSelectedListener(this);
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.view_spinner_item, spnrValues);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnrEmiCount.setAdapter(dataAdapter);

                    if (selectedItemPaymentType != null) {
                        if (selectedItemPaymentType.isEmpty() || selectedItemPaymentType.equals(AppPreferences.CART_TYPE_VALUE_EMI)) {
                            handleUIForPaymentType(true);
                        } else if (selectedItemPaymentType.equals(AppPreferences.CART_TYPE_VALUE_DIRECT_PAYMENT)) {
                            handleUIForPaymentType(false);
                        }
                    } else {
                        handleUIForPaymentType(true);
                    }
                } else {
                    if ((selectedItemPaymentType != null && (selectedItemPaymentType.isEmpty() || selectedItemPaymentType.equals(AppPreferences.CART_TYPE_VALUE_DIRECT_PAYMENT)))) {
                        handleUIForPaymentType(false);
                    } else {
                        Toast.makeText(mAppContext, "EMI option not available for selected product, clear your cart for direct payment", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            } else {
                txtEmi.setVisibility(View.GONE);
                handleUIForPaymentType(false);
            }
        }
    }

    private void showProgress(final boolean visibile) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(visibile) {
                    rltProgress.setVisibility(View.VISIBLE);
                }
                else {
                    rltProgress.setVisibility(View.INVISIBLE);
                }
            }
        });
    }


    private void handleUIForPaymentType(boolean emiUi) {
        if (!emiUi) {
            selectedItemPaymentType = AppPreferences.CART_TYPE_VALUE_DIRECT_PAYMENT;
            txtEmi.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            txtEmi.setTextColor(ContextCompat.getColor(this, R.color.dark_grey));
            txtDirectPayment.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray));
            txtDirectPayment.setTextColor(ContextCompat.getColor(this, R.color.black));

            rltEmi.setVisibility(View.GONE);
            txtPaybleDPTitle.setText("Amount");
        } else {
            selectedItemPaymentType = AppPreferences.CART_TYPE_VALUE_EMI;
            txtEmi.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray));
            txtEmi.setTextColor(ContextCompat.getColor(this, R.color.black));
            txtDirectPayment.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            txtDirectPayment.setTextColor(ContextCompat.getColor(this, R.color.dark_grey));

            rltEmi.setVisibility(View.VISIBLE);
            txtPaybleDPTitle.setText("Down Payment");
        }

        if (!fromCart) {
            int quantity = Integer.parseInt(String.valueOf(productModel.getQuantity()));
            if (!emiUi) {
                double totalOTPAmount = Double.parseDouble(productModel.getOtp_last_price()) * quantity;
                txtPaybleDPValue.setText(KeyConstants.rs + formater.format(totalOTPAmount));

                double taxAmount = Double.parseDouble(productModel.getTax_data() != null ? String.valueOf(productModel.getTax_data().getTax_amt()) : "0.00")
                        * quantity;
                txtTaxesValue.setText(KeyConstants.rs + formater.format(taxAmount));

                double deliveryCharges = 0;
                if(Integer.parseInt(productModel.getShipping_value_type()) > 0) {
                    //Percentage
                    deliveryCharges += (Double.valueOf(productModel.getShipping_charge())
                            * Double.parseDouble(productModel.getPrice()) * quantity) /  100;
                }
                else {
                    deliveryCharges += Double.valueOf(productModel.getShipping_charge()) * quantity;
                }
                txtDeliveryChargesValue.setText(KeyConstants.rs + formater.format(deliveryCharges));

                txtTotalPayableValue.setText(KeyConstants.rs + formater.format(totalOTPAmount + taxAmount + deliveryCharges));
            } else {
                double totalEMIAmount = Double.parseDouble(productModel.getEmi_last_price()) * quantity;
                double downPayment = (Double.parseDouble(productModel.getDown_payment()) * Double.parseDouble(productModel.getPrice()) * quantity) /  100;
                if ((totalEMIAmount - downPayment) > 200d && showEmi) {

                    txtPaybleDPValue.setText(KeyConstants.rs + formater.format(totalEMIAmount));

                    double taxAmount = Double.parseDouble(productModel.getTax_data() != null ? String.valueOf(productModel.getTax_data().getTax_amt()) : "0.00") * quantity;
                    txtTaxesValue.setText(KeyConstants.rs + formater.format(taxAmount));

                    double deliveryCharges = 0;
                    if(Integer.parseInt(productModel.getShipping_value_type()) > 0) {
                        //Percentage
                        deliveryCharges += (Double.valueOf(productModel.getShipping_charge())
                                * Double.parseDouble(productModel.getPrice()) * quantity) /  100;
                    }
                    else {
                        deliveryCharges += Double.valueOf(productModel.getShipping_charge()) * quantity;
                    }
                    txtDeliveryChargesValue.setText(KeyConstants.rs + formater.format(deliveryCharges));

                    txtTotalPayableValue.setText(KeyConstants.rs + formater.format(downPayment + taxAmount + deliveryCharges));

                    txtPaybleDPValue.setText(KeyConstants.rs + formater.format(downPayment));
                    txtDownPaymentValue.setText(KeyConstants.rs + formater.format(downPayment));

                    double emiValue = (totalEMIAmount - downPayment) / numberOfEmiSelected;
                    txtEmiPriceValue.setText(KeyConstants.rs + formater.format(emiValue));

                    txtNetEmiValue.setText(KeyConstants.rs + formater.format(totalEMIAmount - downPayment));

                    txtTotalValue.setText(KeyConstants.rs + formater.format(totalEMIAmount));
                } else {
                    Toast.makeText(mAppContext, "EMI option not available for selected product", Toast.LENGTH_SHORT).show();
                    handleUIForPaymentType(false);
                }
            }
        } else {
            double totalOTPAmount = 0;
            double totolTaxAmount = 0;
            double totalDeliveryCharges = 0;

            double totalEmiAmount = 0;
            double totalDownPayemnt = 0;
            double totalEmiValue = 0;

            for (ProductModel productModel : productCart) {
                int quantity = Integer.parseInt(String.valueOf(productModel.getQuantity()));
                totolTaxAmount += productModel.getTax_data() != null ? productModel.getTax_data().getTax_amt() * quantity : 0;
                if(Integer.parseInt(productModel.getShipping_value_type()) > 0) {
                    //Percentage
                    totalDeliveryCharges += (Double.valueOf(productModel.getShipping_charge())
                            * Double.parseDouble(productModel.getPrice()) * quantity) /  100;
                }
                else {
                    totalDeliveryCharges += Double.valueOf(productModel.getShipping_charge()) * quantity;
                }
                if (!emiUi) {
                    totalOTPAmount += Double.parseDouble(productModel.getOtp_last_price()) * quantity;
                } else {
                    totalEmiAmount += Double.parseDouble(productModel.getEmi_last_price()) * quantity;
                    totalDownPayemnt += (Double.parseDouble(productModel.getDown_payment()) * Double.parseDouble(productModel.getPrice()) * quantity) /  100;
                }
            }

            if (emiUi) {
                if ((totalEmiAmount - totalDownPayemnt) > 200d) {
                    totalEmiValue = (totalEmiAmount - totalDownPayemnt) / numberOfEmiSelected;
                } else {
                    Toast.makeText(mAppContext, "EMI option not available for selected product", Toast.LENGTH_SHORT).show();
                    handleUIForPaymentType(false);
                }
                txtPaybleDPValue.setText(KeyConstants.rs + formater.format(totalDownPayemnt));
                txtTaxesValue.setText(KeyConstants.rs + formater.format(totolTaxAmount));
                txtDeliveryChargesValue.setText(KeyConstants.rs + formater.format(totalDeliveryCharges));
                txtTotalPayableValue.setText(KeyConstants.rs + formater.format(totalDownPayemnt + totolTaxAmount + totalDeliveryCharges));

                txtDownPaymentValue.setText(KeyConstants.rs + formater.format(totalDownPayemnt));
                txtEmiPriceValue.setText(KeyConstants.rs + formater.format(totalEmiValue));
                txtNetEmiValue.setText(KeyConstants.rs + formater.format(totalEmiValue * numberOfEmiSelected));
                txtTotalValue.setText(KeyConstants.rs + formater.format(totalEmiAmount));
            } else {
                txtPaybleDPValue.setText(KeyConstants.rs + formater.format(totalOTPAmount));
                txtTaxesValue.setText(KeyConstants.rs + formater.format(totolTaxAmount));
                txtDeliveryChargesValue.setText(KeyConstants.rs + formater.format(totalDeliveryCharges));
                txtTotalPayableValue.setText(KeyConstants.rs + formater.format(totalOTPAmount + totolTaxAmount + totalDeliveryCharges));
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
                handleUIForPaymentType(true);
                break;

            case R.id.txt_direct_lable:
                handleUIForPaymentType(false);
                break;

            case R.id.btn_confirm_order:
                proceedForPayment(fromCart);
                break;
        }
    }

    private void proceedForPayment(boolean fromCart) {
        showProgress(true);
        double totalMrp = 0;
        double totalAmount = 0;
        double totalShippingCharge = 0;
        double totalDownPayment = 0;
        double totalEmiValue = 0;
        double totalVat = 0;
        SoapObject soapObject;

        VectorProductsParams vectorProductParams = new VectorProductsParams();

        if (fromCart) {
            for (ProductModel productModel : productCart) {
                soapObject = new SoapObject();

                int productQuantity = Integer.parseInt(productModel.getQuantity());
                int shippingValueType = Integer.parseInt(productModel.getShipping_value_type());

                double shippingCharge = Double.parseDouble(productModel.getShipping_charge()) * productQuantity;

                double productMRP = Double.parseDouble(productModel.getPrice()) * productQuantity;
                double productOTPLastPrice = Double.parseDouble(productModel.getOtp_last_price()) * productQuantity;
                double productEMILastPrice = Double.parseDouble(productModel.getEmi_last_price()) * productQuantity;
                double productDownPayment = (Double.parseDouble(productModel.getDown_payment()) * Double.parseDouble(productModel.getPrice()) * productQuantity) /  100;

                double productShippingCharge = shippingValueType == 1 ? (shippingCharge * productMRP) / 100 : shippingCharge;
                double productPrice = (AppPreferences.getInstance().getCartType().equals(AppPreferences.CART_TYPE_VALUE_EMI) ?
                        productEMILastPrice : productOTPLastPrice);
                double productTax = productModel.getTax_data() != null ? productModel.getTax_data().getTax_amt() * productQuantity : 0;

                totalMrp += productMRP;
                totalAmount += productPrice;
                totalShippingCharge += productShippingCharge;
                totalVat += productTax;
                totalDownPayment += productDownPayment;
                totalEmiValue += (productPrice - productDownPayment);

                PropertyInfo propertyInfo = new PropertyInfo();
                propertyInfo.setType(PropertyInfo.STRING_CLASS);
                propertyInfo.setValue(productModel.getProduct_id());

                soapObject.addProperty(getPropertyInfo("product_id", productModel.getProduct_id(), PropertyInfo.STRING_CLASS));
                soapObject.addProperty(getPropertyInfo("thumb", productModel.getActualImage(), PropertyInfo.STRING_CLASS));
                soapObject.addProperty(getPropertyInfo("name", productModel.getName(), PropertyInfo.STRING_CLASS));
                soapObject.addProperty(getPropertyInfo("model", productModel.getModel(), PropertyInfo.STRING_CLASS));
                soapObject.addProperty(getPropertyInfo("quantity", String.valueOf(productQuantity), PropertyInfo.STRING_CLASS));
                soapObject.addProperty(getPropertyInfo("subtract", String.valueOf(1), PropertyInfo.STRING_CLASS));
                soapObject.addProperty(getPropertyInfo("commission", String.valueOf(0), PropertyInfo.STRING_CLASS));
                soapObject.addProperty(getPropertyInfo("vendor_id", String.valueOf(0), PropertyInfo.STRING_CLASS));
                soapObject.addProperty(getPropertyInfo("store_tax", String.valueOf(0), PropertyInfo.STRING_CLASS));
                soapObject.addProperty(getPropertyInfo("vendor_total", String.valueOf(0), PropertyInfo.STRING_CLASS));
                soapObject.addProperty(getPropertyInfo("vendor_tax", String.valueOf(0), PropertyInfo.STRING_CLASS));
                soapObject.addProperty(getPropertyInfo("shipping_value_type", String.valueOf(shippingValueType), PropertyInfo.STRING_CLASS));
                soapObject.addProperty(getPropertyInfo("shipping_charge", String.valueOf(shippingCharge), PropertyInfo.STRING_CLASS));
                soapObject.addProperty(getPropertyInfo("mrp", String.valueOf(productMRP), PropertyInfo.STRING_CLASS));
                soapObject.addProperty(getPropertyInfo("title", "", PropertyInfo.STRING_CLASS));
                soapObject.addProperty(getPropertyInfo("price", String.valueOf(productPrice), PropertyInfo.STRING_CLASS));
                soapObject.addProperty(getPropertyInfo("down_payment", String.valueOf(productDownPayment), PropertyInfo.STRING_CLASS));
                soapObject.addProperty(getPropertyInfo("product_shipping", String.valueOf(productShippingCharge), PropertyInfo.STRING_CLASS));
                soapObject.addProperty(getPropertyInfo("tax", String.valueOf(productTax), PropertyInfo.STRING_CLASS));
                soapObject.addProperty(getPropertyInfo("total", String.valueOf(productPrice + productTax + productShippingCharge), PropertyInfo.STRING_CLASS));
                soapObject.addProperty(getPropertyInfo("reward", String.valueOf(productModel.getReward()), PropertyInfo.STRING_CLASS));

                vectorProductParams.add(new ProductsParams(soapObject));
            }
        } else {
            soapObject = new SoapObject();

            int productQuantity = Integer.parseInt(productModel.getQuantity());
            int shippingValueType = Integer.parseInt(productModel.getShipping_value_type());
            double shippingCharge = Double.parseDouble(productModel.getShipping_charge()) * productQuantity;
            double productMRP = Double.parseDouble(productModel.getPrice()) * productQuantity;
            double productOTPLastPrice = Double.parseDouble(productModel.getOtp_last_price()) * productQuantity;
            double productEMILastPrice = Double.parseDouble(productModel.getEmi_last_price()) * productQuantity;
            double productDownPayment = (Double.parseDouble(productModel.getDown_payment()) * productQuantity * productMRP)/100;
            double productShippingCharge = shippingValueType == 1 ? (shippingCharge * productMRP) / 100 : shippingCharge;
            double productPrice = (AppPreferences.getInstance().getCartType().equals(AppPreferences.CART_TYPE_VALUE_EMI) ?
                    productEMILastPrice : productOTPLastPrice);
            double productTax = productModel.getTax_data() != null ? productModel.getTax_data().getTax_amt() * productQuantity : 0;

            totalMrp = productMRP;
            totalAmount = productPrice;
            totalShippingCharge = productShippingCharge;
            totalVat = productTax;
            totalDownPayment = productDownPayment;
            totalEmiValue = (productPrice - productDownPayment);

            soapObject.addProperty(getPropertyInfo("product_id", productModel.getProduct_id(), PropertyInfo.STRING_CLASS));
            soapObject.addProperty(getPropertyInfo("thumb", productModel.getActualImage(), PropertyInfo.STRING_CLASS));
            soapObject.addProperty(getPropertyInfo("name", productModel.getName(), PropertyInfo.STRING_CLASS));
            soapObject.addProperty(getPropertyInfo("model", productModel.getModel(), PropertyInfo.STRING_CLASS));
            soapObject.addProperty(getPropertyInfo("quantity", String.valueOf(productQuantity), PropertyInfo.STRING_CLASS));
            soapObject.addProperty(getPropertyInfo("subtract", String.valueOf(1), PropertyInfo.STRING_CLASS));
            soapObject.addProperty(getPropertyInfo("commission", String.valueOf(0), PropertyInfo.STRING_CLASS));
            soapObject.addProperty(getPropertyInfo("vendor_id", String.valueOf(0), PropertyInfo.STRING_CLASS));
            soapObject.addProperty(getPropertyInfo("store_tax", String.valueOf(0), PropertyInfo.STRING_CLASS));
            soapObject.addProperty(getPropertyInfo("vendor_total", String.valueOf(0), PropertyInfo.STRING_CLASS));
            soapObject.addProperty(getPropertyInfo("vendor_tax", String.valueOf(0), PropertyInfo.STRING_CLASS));
            soapObject.addProperty(getPropertyInfo("shipping_value_type", String.valueOf(shippingValueType), PropertyInfo.STRING_CLASS));
            soapObject.addProperty(getPropertyInfo("shipping_charge", String.valueOf(shippingCharge), PropertyInfo.STRING_CLASS));
            soapObject.addProperty(getPropertyInfo("mrp", String.valueOf(productMRP), PropertyInfo.STRING_CLASS));
            soapObject.addProperty(getPropertyInfo("title", "", PropertyInfo.STRING_CLASS));
            soapObject.addProperty(getPropertyInfo("price", String.valueOf(productPrice), PropertyInfo.STRING_CLASS));
            soapObject.addProperty(getPropertyInfo("down_payment", String.valueOf(productDownPayment), PropertyInfo.STRING_CLASS));
            soapObject.addProperty(getPropertyInfo("product_shipping", String.valueOf(productShippingCharge), PropertyInfo.STRING_CLASS));
            soapObject.addProperty(getPropertyInfo("tax", String.valueOf(productTax), PropertyInfo.STRING_CLASS));
            soapObject.addProperty(getPropertyInfo("total", String.valueOf(productPrice + productTax + productShippingCharge), PropertyInfo.STRING_CLASS));
            soapObject.addProperty(getPropertyInfo("reward", productModel.getReward(), PropertyInfo.STRING_CLASS));

            vectorProductParams.add(new ProductsParams(soapObject));
        }

        VectorTotalParams vectorTotalParams = new VectorTotalParams();

        soapObject = new SoapObject();
        soapObject.addProperty(getPropertyInfo("code", "sub_total", PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("title", "Sub-Total", PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("value", String.valueOf(totalAmount), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("sort_order", "1", PropertyInfo.STRING_CLASS));
        vectorTotalParams.add(new TotalParams(soapObject));

        soapObject = new SoapObject();
        soapObject.addProperty(getPropertyInfo("code", "shipping", PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("title", "Shipping Charge", PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("value", String.valueOf(totalShippingCharge), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("sort_order", "3", PropertyInfo.STRING_CLASS));
        vectorTotalParams.add(new TotalParams(soapObject));

        soapObject = new SoapObject();
        soapObject.addProperty(getPropertyInfo("code", "tax", PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("title", "VAT (15%)", PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("value", String.valueOf(totalVat), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("sort_order", "5", PropertyInfo.STRING_CLASS));
        vectorTotalParams.add(new TotalParams(soapObject));

        soapObject = new SoapObject();
        soapObject.addProperty(getPropertyInfo("code", "total", PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("title", "Total", PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("value", String.valueOf(totalAmount + totalShippingCharge + totalVat), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("sort_order", "9", PropertyInfo.STRING_CLASS));
        vectorTotalParams.add(new TotalParams(soapObject));

        soapObject = new SoapObject();

        CustomerModel appOwnerData = AppPreferences.getInstance().getAppOwnerData();
        soapObject.addProperty(getPropertyInfo("store_id", String.valueOf(0), PropertyInfo.INTEGER_CLASS));
        soapObject.addProperty(getPropertyInfo("store_name", "EMIADDA", PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("invoice_prefix", "INV-2013-00", PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("store_url", "http://www.mydevsystems.com/dev/emiaddanew/", PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("customer_id", appOwnerData.getCustomer_id(), PropertyInfo.INTEGER_CLASS));
        soapObject.addProperty(getPropertyInfo("customer_group_id", appOwnerData.getCustomer_group_id(), PropertyInfo.INTEGER_CLASS));
        soapObject.addProperty(getPropertyInfo("firstname", appOwnerData.getFirstname(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("lastname", appOwnerData.getLastname(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("email", appOwnerData.getEmail(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("telephone", appOwnerData.getTelephone(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("fax", appOwnerData.getFax(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("custom_field", "", PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("payment_firstname", appOwnerData.getAddress().getFirstname(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("payment_lastname", appOwnerData.getAddress().getLastname(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("payment_company", appOwnerData.getAddress().getCompany(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("payment_address_1", appOwnerData.getAddress().getAddress_1(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("payment_address_2", appOwnerData.getAddress().getAddress_2(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("payment_city", appOwnerData.getAddress().getCity(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("payment_postcode", appOwnerData.getAddress().getPostcode(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("payment_country", appOwnerData.getAddress().getCountry(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("payment_country_id", appOwnerData.getAddress().getCountry_id(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("payment_zone", appOwnerData.getAddress().getZone(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("payment_zone_id", appOwnerData.getAddress().getZone_id(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("payment_district_id", appOwnerData.getAddress().getDistrict(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("payment_subzone_id", appOwnerData.getAddress().getSubzone(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("payment_address_format", appOwnerData.getAddress().getAddress_format(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("payment_custom_field", "", PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("payment_method", "Cash On Delivery", PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("payment_code", "cod", PropertyInfo.STRING_CLASS));

        soapObject.addProperty(getPropertyInfo("shipping_firstname", appOwnerData.getAddress().getFirstname(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("shipping_lastname", appOwnerData.getAddress().getLastname(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("shipping_company", appOwnerData.getAddress().getCompany(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("shipping_address_1", appOwnerData.getAddress().getAddress_1(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("shipping_address_2", appOwnerData.getAddress().getAddress_2(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("shipping_city", appOwnerData.getAddress().getCity(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("shipping_postcode", appOwnerData.getAddress().getPostcode(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("shipping_country", appOwnerData.getAddress().getCountry(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("shipping_country_id", appOwnerData.getAddress().getCountry_id(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("shipping_zone", appOwnerData.getAddress().getZone(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("shipping_zone_id", appOwnerData.getAddress().getZone_id(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("shipping_district_id", appOwnerData.getAddress().getDistrict(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("shipping_subzone_id", appOwnerData.getAddress().getSubzone(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("shipping_address_format", appOwnerData.getAddress().getAddress_format(), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("shipping_custom_field", "", PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("shipping_method", "Shipping Charge", PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("shipping_code", "flat.flat", PropertyInfo.STRING_CLASS));

        soapObject.addProperty(getPropertyInfo("comment", "", PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("total", String.valueOf(totalMrp), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("affiliate_id", String.valueOf(0), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("commission", String.valueOf(0), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("marketing_id", String.valueOf(0), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("tracking", "", PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("language_id", "1", PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("currency_id", "4", PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("currency_code", "INR", PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("currency_value", "1", PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("ip", getIPAddress(true), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("forwarded_ip", "", PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("user_agent", "Android", PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("accept_language", "en-US,en;q=0.8", PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("method", "cod", PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("emi", AppPreferences.getInstance().getCartType().equals(AppPreferences.CART_TYPE_VALUE_EMI) ? "emi" : "otd", PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("totalamt", String.valueOf(totalAmount), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("shipping_charge", String.valueOf(totalShippingCharge), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("down_payment", String.valueOf(totalDownPayment), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("each_emi", String.valueOf(totalEmiValue), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("customer_type", String.valueOf(appOwnerData.getCustomer_type()), PropertyInfo.INTEGER_CLASS));
        soapObject.addProperty(getPropertyInfo("no_of_emi", AppPreferences.getInstance().getCartType().equals(AppPreferences.CART_TYPE_VALUE_EMI) ? String.valueOf(numberOfEmiSelected) : String.valueOf(0), PropertyInfo.INTEGER_CLASS));
        soapObject.addProperty(getPropertyInfo("payable_mrp", String.valueOf(totalMrp), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("vat", String.valueOf(totalVat), PropertyInfo.STRING_CLASS));
        soapObject.addProperty(getPropertyInfo("cod_type", AppPreferences.getInstance().getCartType().equals(AppPreferences.CART_TYPE_VALUE_EMI) ? "emi" : "otd", PropertyInfo.STRING_CLASS));
        if (AppPreferences.getInstance().getCartType().equals(AppPreferences.CART_TYPE_VALUE_EMI)) {
            soapObject.addProperty(getPropertyInfo("selected_num_emi", String.valueOf(numberOfEmiSelected), PropertyInfo.STRING_CLASS));
            soapObject.addProperty(getPropertyInfo("per_emi", String.valueOf(totalEmiValue / numberOfEmiSelected), PropertyInfo.STRING_CLASS));
        }
        OrderParams orderParams = new OrderParams(soapObject);

        EAApplication.makePlaceOrderServerRequest(ServerRequestProcessingThread.REQUEST_CODE_PLACE_ORDER, PLACE_ORDER_REQUEST_CODE, EAServerRequest.PRIORITY_HIGH, TAG,
                orderParams, vectorProductParams, vectorTotalParams);
    }

    private PropertyInfo getPropertyInfo(String name, String value, Class type) {
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setType(type);
        propertyInfo.setValue(value);
        propertyInfo.setName(name);
        return propertyInfo;
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

    /**
     * Get IP address from first non-localhost interface
     *
     * @param useIPv4 true=return ipv4, false=return ipv6
     * @return address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
    }

    @Override
    public void responseReceived(final ServerResponse response, int requestCode, int extraRequestCode, String activityTag) {
        if (!TAG.equals(activityTag)) {
            return;
        }
        Log.i(TAG, "Received place order response " + response);

        if(response == null) {
            Log.e(TAG, "Received null response for request code "+requestCode);
            return;
        }

        if (requestCode == ServerRequestProcessingThread.REQUEST_CODE_PLACE_ORDER) {
            showProgress(false);
            if (response.getResponseStatus() == ServerResponse.SERVER_OK) {
                //Order placed successfully
                if (fromCart) {
                    AppPreferences.getInstance().removeAllProductFromCartList();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        PlaceOrderResponse placeOrderResponse = new Gson().fromJson(response.getResponse(), PlaceOrderResponse.class);

                        Toast.makeText(mAppContext, "Successfully placed your Order", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PlaceOrderActivity.this, ThankYouActivity.class);

                        intent.putExtra(KeyConstants.INTENT_CONSTANT_ORDER_ID, placeOrderResponse.getOrder_id());
                        intent.putExtra(KeyConstants.INTENT_CONSTANT_ORDER_PAYMENT_ID, placeOrderResponse.getOrder_payment_id());

                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
            }
            else if(response.getResponseStatus() == ServerResponse.SERVER_ERROR){
                Toast.makeText(mAppContext, "Error in placing your Order", Toast.LENGTH_SHORT).show();
                finish();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mAppContext, "Successfully placed your Order", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
            else if(response.getResponseStatus() == ServerResponse.NETWORK_ERROR) {
                //TODO: handle retry option
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        ((EAApplication) mAppContext).attach(this);
    }

    @Override
    protected void onDestroy() {
        ((EAApplication) mAppContext).dettach(this);
        super.onDestroy();
    }

}
