package com.emiadda.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.emiadda.EAApplication;
import com.emiadda.R;
import com.emiadda.adapters.CartAdapter;
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
import com.emiadda.wsdl.ProductImageModel;
import com.emiadda.wsdl.ProductModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.ksoap2.serialization.SoapObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

public class CartActivity extends AppCompatActivity implements ServerResponseSubscriber, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = CartActivity.class.getSimpleName();
    private static final int REQUEST_CODE_LOGIN = 1;

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private CartAdapter cartAdapter;
    private Button btnPlaceOrder;
    private double subTotal;
    private double deliveryCharges;
    private double taxes;
    private List<ProductModel> masterProductModelList;
    private boolean inForeground;
    private Context mAppContext;
    private Activity mActivityContext;
    private RelativeLayout rltProgress;
    private DecimalFormat formater = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        mActivityContext = this;
        mAppContext = getApplicationContext();

        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        btnPlaceOrder = (Button) findViewById(R.id.btn_place_order);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        cartAdapter = new CartAdapter(this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cartAdapter);

        rltProgress = (RelativeLayout)findViewById(R.id.rlt_progress);

        ((EAApplication)mAppContext).attach(this);

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!AppPreferences.getInstance().isUserLoggedIn()) {
                    Toast.makeText(mAppContext, "Please login first to place an order", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mActivityContext, LoginActivity.class);
                    mActivityContext.startActivityForResult(intent, REQUEST_CODE_LOGIN);
                    return;
                }
                Intent intent = new Intent(mActivityContext, PlaceOrderActivity.class);
                intent.putExtra(KeyConstants.INTENT_IS_FROM_CART, true);
                startActivity(intent);
            }
        });

        AppPreferences.getInstance().registerSharedPreferenceListener(this);
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

    @Override
    protected void onStart() {
        super.onStart();
        ((EAApplication)mAppContext).attach(this);
    }

    @Override
    protected void onDestroy() {
        ((EAApplication)mAppContext).attach(this);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshTotalAmount();
        inForeground = true;
        //Reset is downloading status
        if(masterProductModelList.isEmpty()) {
            Toast.makeText(mAppContext, "Your cart is Empty", Toast.LENGTH_SHORT).show();
            finish();
        }
        for (ProductModel productModel : masterProductModelList) {
            if((productModel.getActualImage() == null || productModel.getActualImage().isEmpty()) && !productModel.isLoadingImage()) {
                productModel.setLoadingImage(true);
                EAApplication.makeServerRequest(ServerRequestProcessingThread.REQUEST_CODE_GET_PRODUCT_IMAGE,
                        Integer.parseInt(productModel.getProduct_id()), EAServerRequest.PRIORITY_LOW, TAG, productModel.getProduct_id());
            }
        }
    }

    private void refreshTotalAmount() {
        masterProductModelList = AppPreferences.getInstance().getCartList();
        cartAdapter.resetProductList(masterProductModelList);
        subTotal = 0;
        taxes = 0;
        deliveryCharges = 0;
        for (ProductModel productModel : masterProductModelList) {
            double quantity = Double.parseDouble(productModel.getQuantity());
            double totalPrice = quantity * Double.parseDouble(productModel.getPrice());
            subTotal += totalPrice;
            if(productModel.getTax_data() != null) {
                taxes += productModel.getTax_data().getTax_amt() * quantity;
            }
            if(productModel.getShipping_charge() != null) {
                deliveryCharges += Double.parseDouble(productModel.getShipping_charge()) * quantity;
            }
        }

        ((TextView)findViewById(R.id.txt_sub_total)).setText(KeyConstants.rs + formater.format(subTotal));
        ((TextView)findViewById(R.id.txt_delivery_charges)).setText(KeyConstants.rs + formater.format(deliveryCharges));
        ((TextView)findViewById(R.id.txt_taxes)).setText(KeyConstants.rs + formater.format(taxes));
        ((TextView)findViewById(R.id.txt_total)).setText(KeyConstants.rs + formater.format(subTotal + deliveryCharges + taxes));
    }

    @Override
    protected void onPause() {
        inForeground = false;
        super.onPause();
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
    public void responseReceived(String response, int requestCode, int responseCode, int extraRequestCode, String activityTag) {
        if(!TAG.equals(activityTag)) {
            return;
        }

        Log.i(TAG, "Received image download response "+response);
        if(requestCode == ServerRequestProcessingThread.REQUEST_CODE_GET_PRODUCT_IMAGE) {
            if(response != null && !response.isEmpty()) {
                ProductImageModel productImageModel = new Gson().fromJson(response, new TypeToken<ProductImageModel>() {}.getType());
                for (ProductModel product : masterProductModelList) {
                    if (product.getProduct_id().equals(String.valueOf(extraRequestCode))) {
                        try {
                            if (productImageModel != null) {
                                product.setActualImage(productImageModel.getImage().replaceAll("&amp;", "&").replaceAll(" ", "%20"));
                                String cartType = AppPreferences.getInstance().getCartType();
                                AppPreferences.getInstance().removeProductFromCartList(product);
                                AppPreferences.getInstance().addProductToCartList(product, cartType);
                                if(inForeground) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            cartAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage(), e);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_LOGIN) {
            if(resultCode == RESULT_OK) {
                Intent intent = new Intent(mActivityContext, PlaceOrderActivity.class);
                intent.putExtra(KeyConstants.INTENT_IS_FROM_CART, true);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(AppPreferences.CART_LIST)) {
            refreshTotalAmount();
        }
    }
}
