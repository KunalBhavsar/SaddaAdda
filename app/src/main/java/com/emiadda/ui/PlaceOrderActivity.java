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
import com.emiadda.server.TotalParams;
import com.emiadda.server.VectorProductsParams;
import com.emiadda.server.VectorTotalParams;
import com.emiadda.utils.AppPreferences;
import com.emiadda.utils.KeyConstants;
import com.emiadda.wsdl.CustomerModel;
import com.emiadda.wsdl.ProductModel;

import org.ksoap2.serialization.SoapObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
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

    String currentCartType, selectedItemPaymentType;

    Context mAppContext;
    Activity mActivityContext;

    ProductModel productModel;
    List<ProductModel> productCart;
    boolean fromCart;

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
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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
                    totalDownPayemnt += Double.parseDouble(productModel.getDown_payment()) * quantity;
                }

                int i = 1;
                List<String> spnrValues = new ArrayList<>();
                while (((totalEmiAmount - totalDownPayemnt) / i) > 200d && i <= 20) {
                    spnrValues.add(String.valueOf(i));
                    i++;
                }

                // Spinner click listener
                spnrEmiCount.setOnItemSelectedListener(this);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spnrValues);
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
                double downPayment = Double.parseDouble(productModel.getDown_payment());
                if ((totalEMIAmount - downPayment) > 200d) {
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
                txtPaybleDPValue.setText(String.valueOf(totalOTPAmount));

                double taxAmount = Double.parseDouble(productModel.getTax_data() != null ? String.valueOf(productModel.getTax_data().getTax_amt()) : "0.00")
                        * quantity;
                txtTaxesValue.setText(String.valueOf(taxAmount));

                double deliveryCharges = Double.valueOf(productModel.getShipping_charge()) * quantity;
                txtDeliveryChargesValue.setText(String.valueOf(deliveryCharges));

                txtTotalPayableValue.setText(String.valueOf(totalOTPAmount + taxAmount + deliveryCharges));
            } else {
                double totalEMIAmount = Double.parseDouble(productModel.getEmi_last_price()) * quantity;
                double downPayment = Double.parseDouble(productModel.getDown_payment()) * quantity;
                if ((totalEMIAmount - downPayment) > 200d && showEmi) {

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
                totalDeliveryCharges += Double.valueOf(productModel.getShipping_charge()) * quantity;
                if (!emiUi) {
                    totalOTPAmount += Double.parseDouble(productModel.getOtp_last_price()) * quantity;
                } else {
                    totalEmiAmount += Double.parseDouble(productModel.getEmi_last_price()) * quantity;
                    totalDownPayemnt += Double.parseDouble(productModel.getDown_payment()) * quantity;
                }
            }

            if (emiUi) {
                if ((totalEmiAmount - totalDownPayemnt) > 200d) {
                    totalEmiValue = (totalEmiAmount - totalDownPayemnt) / numberOfEmiSelected;
                } else {
                    Toast.makeText(mAppContext, "EMI option not available for selected product", Toast.LENGTH_SHORT).show();
                    handleUIForPaymentType(false);
                }
                txtPaybleDPValue.setText(String.valueOf(totalDownPayemnt));
                txtTaxesValue.setText(String.valueOf(totolTaxAmount));
                txtDeliveryChargesValue.setText(String.valueOf(totalDeliveryCharges));
                txtTotalPayableValue.setText(String.valueOf(totalDownPayemnt + totolTaxAmount + totalDeliveryCharges));

                txtDownPaymentValue.setText(String.valueOf(totalDownPayemnt));
                txtEmiPriceValue.setText(String.format("%.2f", totalEmiValue));
                txtNetEmiValue.setText(String.format("%.2f", (totalEmiValue * numberOfEmiSelected)));
                txtTotalValue.setText(String.valueOf(totalEmiAmount));
            } else {
                txtPaybleDPValue.setText(String.valueOf(totalOTPAmount));
                txtTaxesValue.setText(String.valueOf(totolTaxAmount));
                txtDeliveryChargesValue.setText(String.valueOf(totalDeliveryCharges));
                txtTotalPayableValue.setText(String.valueOf(totalOTPAmount + totolTaxAmount + totalDeliveryCharges));
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
                double productDownPayment = Double.parseDouble(productModel.getDown_payment()) * productQuantity;
                double productShippingCharge = shippingValueType == 1 ? (shippingCharge * productMRP) / 100 : shippingCharge;
                double productPrice = productMRP - (AppPreferences.getInstance().getCartType().equals(AppPreferences.CART_TYPE_VALUE_EMI) ?
                        productEMILastPrice : productOTPLastPrice);
                double productTax = productModel.getTax_data() != null ? productModel.getTax_data().getTax_amt() * productQuantity : 0;

                totalMrp += productMRP;
                totalAmount += productPrice;
                totalShippingCharge += productShippingCharge;
                totalVat += productTax;
                totalDownPayment += productDownPayment;
                totalEmiValue += (productPrice - productDownPayment);

                soapObject.addProperty("product_id", productModel.getProduct_id());
                soapObject.addProperty("thumb", productModel.getActualImage());
                soapObject.addProperty("name", productModel.getName());
                soapObject.addProperty("model", productModel.getModel());
                soapObject.addProperty("quantity", String.valueOf(productQuantity));
                soapObject.addProperty("subtract", String.valueOf(1));
                soapObject.addProperty("commission", String.valueOf(0));
                soapObject.addProperty("vendor_id", String.valueOf(0));
                soapObject.addProperty("store_tax", String.valueOf(0));
                soapObject.addProperty("vendor_total", String.valueOf(0));
                soapObject.addProperty("vendor_tax", String.valueOf(0));
                soapObject.addProperty("shipping_value_type", String.valueOf(shippingValueType));
                soapObject.addProperty("shipping_charge", String.valueOf(shippingCharge));
                soapObject.addProperty("mrp", String.valueOf(productMRP));
                soapObject.addProperty("title", "");
                soapObject.addProperty("price", String.valueOf(productPrice));
                soapObject.addProperty("down_payment", String.valueOf(productDownPayment));
                soapObject.addProperty("product_shipping", String.valueOf(productShippingCharge));
                soapObject.addProperty("tax", String.valueOf(productTax));
                soapObject.addProperty("total", String.valueOf(productPrice + productTax + productShippingCharge));
                soapObject.addProperty("reward", String.valueOf(productModel.getReward()));

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
            double productDownPayment = Double.parseDouble(productModel.getDown_payment()) * productQuantity;
            double productShippingCharge = shippingValueType == 1 ? (shippingCharge * productMRP) / 100 : shippingCharge;
            double productPrice = productMRP - (AppPreferences.getInstance().getCartType().equals(AppPreferences.CART_TYPE_VALUE_EMI) ?
                    productEMILastPrice : productOTPLastPrice);
            double productTax = productModel.getTax_data() != null ? productModel.getTax_data().getTax_amt() * productQuantity : 0;

            totalMrp = productMRP;
            totalAmount = productPrice;
            totalShippingCharge = productShippingCharge;
            totalVat = productTax;
            totalDownPayment = productDownPayment;
            totalEmiValue = (productPrice - productDownPayment);

            soapObject.addProperty("product_id", productModel.getProduct_id());
            soapObject.addProperty("thumb", productModel.getActualImage());
            soapObject.addProperty("name", productModel.getName());
            soapObject.addProperty("model", productModel.getModel());
            soapObject.addProperty("quantity", String.valueOf(productQuantity));
            soapObject.addProperty("subtract", String.valueOf(1));
            soapObject.addProperty("commission", String.valueOf(0));
            soapObject.addProperty("vendor_id", String.valueOf(0));
            soapObject.addProperty("store_tax", String.valueOf(0));
            soapObject.addProperty("vendor_total", String.valueOf(0));
            soapObject.addProperty("vendor_tax", String.valueOf(0));
            soapObject.addProperty("shipping_value_type", String.valueOf(shippingValueType));
            soapObject.addProperty("shipping_charge", String.valueOf(shippingCharge));
            soapObject.addProperty("mrp", String.valueOf(productMRP));
            soapObject.addProperty("title", "");
            soapObject.addProperty("price", String.valueOf(productPrice));
            soapObject.addProperty("down_payment", String.valueOf(productDownPayment));
            soapObject.addProperty("product_shipping", String.valueOf(productShippingCharge));
            soapObject.addProperty("tax", String.valueOf(productTax));
            soapObject.addProperty("total", String.valueOf(productPrice + productTax + productShippingCharge));
            soapObject.addProperty("reward", productModel.getReward());

            vectorProductParams.add(new ProductsParams(soapObject));
        }

        VectorTotalParams vectorTotalParams = new VectorTotalParams();

        soapObject = new SoapObject();
        soapObject.addProperty("code", "sub_total");
        soapObject.addProperty("title", "Sub-Total");
        soapObject.addProperty("value", String.valueOf(totalAmount));
        soapObject.addProperty("sort_order", "1");
        vectorTotalParams.add(new TotalParams(soapObject));

        soapObject = new SoapObject();
        soapObject.addProperty("code", "shipping");
        soapObject.addProperty("title", "Shipping Charge");
        soapObject.addProperty("value", String.valueOf(totalShippingCharge));
        soapObject.addProperty("sort_order", "3");
        vectorTotalParams.add(new TotalParams(soapObject));

        soapObject = new SoapObject();
        soapObject.addProperty("code", "tax");
        soapObject.addProperty("title", "VAT (15%)");
        soapObject.addProperty("value", String.valueOf(totalVat));
        soapObject.addProperty("sort_order", "5");
        vectorTotalParams.add(new TotalParams(soapObject));

        soapObject = new SoapObject();
        soapObject.addProperty("code", "total");
        soapObject.addProperty("title", "Total");
        soapObject.addProperty("value", String.valueOf(totalAmount + totalShippingCharge + totalVat));
        soapObject.addProperty("sort_order", "9");
        vectorTotalParams.add(new TotalParams(soapObject));

        soapObject = new SoapObject();

        CustomerModel appOwnerData = AppPreferences.getInstance().getAppOwnerData();
        soapObject.addProperty("store_id", String.valueOf(0));
        soapObject.addProperty("store_name", "EMIADDA");
        soapObject.addProperty("invoice_prefix", "INV-2013-00");
        soapObject.addProperty("store_url", "http://www.mydevsystems.com/dev/emiaddanew/");
        soapObject.addProperty("customer_id", appOwnerData.getCustomer_id());
        soapObject.addProperty("firstname", appOwnerData.getFirstname());
        soapObject.addProperty("lastname", appOwnerData.getLastname());
        soapObject.addProperty("email", appOwnerData.getEmail());
        soapObject.addProperty("telephone", appOwnerData.getTelephone());
        soapObject.addProperty("fax", appOwnerData.getFax());
        soapObject.addProperty("custom_field", "");
        soapObject.addProperty("payment_firstname", appOwnerData.getAddress().getFirstname());
        soapObject.addProperty("payment_lastname", appOwnerData.getAddress().getLastname());
        soapObject.addProperty("payment_company", appOwnerData.getAddress().getCompany());
        soapObject.addProperty("payment_address_1", appOwnerData.getAddress().getAddress_1());
        soapObject.addProperty("payment_address_2", appOwnerData.getAddress().getAddress_2());
        soapObject.addProperty("payment_city", appOwnerData.getAddress().getCity());
        soapObject.addProperty("payment_postcode", appOwnerData.getAddress().getPostcode());
        soapObject.addProperty("payment_country", appOwnerData.getAddress().getCountry());
        soapObject.addProperty("payment_country_id", appOwnerData.getAddress().getCountry_id());
        soapObject.addProperty("payment_zone", appOwnerData.getAddress().getZone());
        soapObject.addProperty("payment_zone_id", appOwnerData.getAddress().getZone_id());
        soapObject.addProperty("payment_district_id", appOwnerData.getAddress().getDistrict());
        soapObject.addProperty("payment_subzone_id", appOwnerData.getAddress().getSubzone());
        soapObject.addProperty("payment_address_format", appOwnerData.getAddress().getAddress_format());
        soapObject.addProperty("payment_custom_field", "");
        soapObject.addProperty("payment_method", "Cash On Delivery");
        soapObject.addProperty("payment_code", "cod");

        soapObject.addProperty("shipping_firstname", appOwnerData.getAddress().getFirstname());
        soapObject.addProperty("shipping_lastname", appOwnerData.getAddress().getLastname());
        soapObject.addProperty("shipping_company", appOwnerData.getAddress().getCompany());
        soapObject.addProperty("shipping_address_1", appOwnerData.getAddress().getAddress_1());
        soapObject.addProperty("shipping_address_2", appOwnerData.getAddress().getAddress_2());
        soapObject.addProperty("shipping_city", appOwnerData.getAddress().getCity());
        soapObject.addProperty("shipping_postcode", appOwnerData.getAddress().getPostcode());
        soapObject.addProperty("shipping_country", appOwnerData.getAddress().getCountry());
        soapObject.addProperty("shipping_country_id", appOwnerData.getAddress().getCountry_id());
        soapObject.addProperty("shipping_zone", appOwnerData.getAddress().getZone());
        soapObject.addProperty("shipping_zone_id", appOwnerData.getAddress().getZone_id());
        soapObject.addProperty("shipping_district_id", appOwnerData.getAddress().getDistrict());
        soapObject.addProperty("shipping_subzone_id", appOwnerData.getAddress().getSubzone());
        soapObject.addProperty("shipping_address_format", appOwnerData.getAddress().getAddress_format());
        soapObject.addProperty("shipping_custom_field", "");
        soapObject.addProperty("shipping_method", "Shipping Charge");
        soapObject.addProperty("shipping_code", "flat.flat");

        soapObject.addProperty("comment", "");
        soapObject.addProperty("total", String.valueOf(totalMrp));
        soapObject.addProperty("affiliate_id", String.valueOf(0));
        soapObject.addProperty("commission", String.valueOf(0));
        soapObject.addProperty("marketing_id", String.valueOf(0));
        soapObject.addProperty("tracking", "");
        soapObject.addProperty("language_id", "1");
        soapObject.addProperty("currency_id", "4");
        soapObject.addProperty("currency_code", "INR");
        soapObject.addProperty("currency_value", "1");
        soapObject.addProperty("ip", getIPAddress(true));
        soapObject.addProperty("forwarded_ip", "");
        soapObject.addProperty("user_agent", "Android");
        soapObject.addProperty("accept_language", "en-US,en;q=0.8");
        soapObject.addProperty("method", "cod");
        soapObject.addProperty("emi", AppPreferences.getInstance().getCartType().equals(AppPreferences.CART_TYPE_VALUE_EMI) ? "emi" : "otd");
        soapObject.addProperty("totalamt", String.valueOf(totalAmount));
        soapObject.addProperty("shipping_charge", String.valueOf(totalShippingCharge));
        soapObject.addProperty("down_payment", String.valueOf(totalDownPayment));
        soapObject.addProperty("each_emi", String.valueOf(totalEmiValue));
        soapObject.addProperty("customer_type", String.valueOf(appOwnerData.getCustomer_type()));
        soapObject.addProperty("no_of_emi", AppPreferences.getInstance().getCartType().equals(AppPreferences.CART_TYPE_VALUE_EMI) ? numberOfEmiSelected : 0);
        soapObject.addProperty("payable_mrp", String.valueOf(totalMrp));
        soapObject.addProperty("vat", String.valueOf(totalVat));
        soapObject.addProperty("cod_type", AppPreferences.getInstance().getCartType().equals(AppPreferences.CART_TYPE_VALUE_EMI) ? "emi" : "otd");
        if (AppPreferences.getInstance().getCartType().equals(AppPreferences.CART_TYPE_VALUE_EMI)) {
            soapObject.addProperty("selected_num_emi", String.valueOf(numberOfEmiSelected));
            soapObject.addProperty("per_emi", String.valueOf(totalEmiValue / numberOfEmiSelected));
        }
        OrderParams orderParams = new OrderParams(soapObject);

        EAApplication.makePlaceOrderServerRequest(ServerRequestProcessingThread.REQUEST_CODE_PLACE_ORDER, PLACE_ORDER_REQUEST_CODE, EAServerRequest.PRIORITY_HIGH, TAG,
                orderParams, vectorProductParams, vectorTotalParams);
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
    public void responseReceived(String response, int requestCode, int responseCode, int extraRequestCode, String activityTag) {
        if (!TAG.equals(activityTag)) {
            return;
        }
        Log.i(TAG, "Received place order response " + response);

        if (requestCode == ServerRequestProcessingThread.REQUEST_CODE_PLACE_ORDER) {
            showProgress(false);
            if (responseCode == ServerResponseSubscriber.RESPONSE_CODE_OK) {
                //Order placed successfully
                if (fromCart) {
                    AppPreferences.getInstance().removeAllProductFromCartList();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mAppContext, "Successfully placed your Order", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PlaceOrderActivity.this, ThankYouActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
            }
            else {
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
