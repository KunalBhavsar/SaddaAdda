package com.emiadda.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.emiadda.R;
import com.emiadda.asynctasks.GetProductByProductId;
import com.emiadda.interafaces.ServerResponseSubscriber;
import com.emiadda.utils.AppPreferences;
import com.emiadda.utils.KeyConstants;
import com.emiadda.wsdl.ProductModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by Kunal on 12/07/16.
 */
public class ProductDetailActivity extends AppCompatActivity implements ServerResponseSubscriber, BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private static final String TAG = ProductDetailActivity.class.getSimpleName();
    private static final int PRODUCT_DETIALS_REQUEST_CODE = 1;
    private static final String SAVED_INSTANCE_CURRENT_QUANTITY = "current_quantity";
    private Context mAppContext;
    private Activity mActivityContext;

    private String productId;
    private SliderLayout mDemoSlider;
    private ProductModel productModel;

    private TextView txtBrandName;
    private TextView txtAmount;
    private Button btnIncrQuatity;
    private Button btnDecrQuatity;
    private TextView txtCurrentQuatity;
    private Button btnBuyNow;
    private Button btnAddToCart;
    private TextView txtDescription;
    private TextView txtManufacturer;
    private TextView txtStockStatus;
    private TextView txtQuantityAvailable;

    private ProgressDialog progressDialog;

    private int currentQuantity = 1;
    private int availableQuantity;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_detail);

        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageView imgCart = (ImageView) findViewById(R.id.img_cart);
        assert imgCart != null;
        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivityContext, CartActivity.class);
                startActivity(intent);
            }
        });

        mActivityContext = this;
        mAppContext = getApplicationContext();

        txtBrandName = (TextView)findViewById(R.id.txt_brand_name);
        txtAmount = (TextView)findViewById(R.id.txt_amount);
        btnIncrQuatity = (Button)findViewById(R.id.btn_incr_quantity);
        btnDecrQuatity = (Button)findViewById(R.id.btn_decr_quantity);
        txtCurrentQuatity = (TextView)findViewById(R.id.txt_quantity);
        btnBuyNow = (Button)findViewById(R.id.btn_buy_now);
        btnAddToCart = (Button)findViewById(R.id.btn_add_to_cart);
        txtDescription = (TextView)findViewById(R.id.txt_description);
        txtManufacturer = (TextView) findViewById(R.id.txt_manufacturer);
        txtStockStatus = (TextView) findViewById(R.id.txt_stock_status);
        txtQuantityAvailable = (TextView) findViewById(R.id.txt_quantity_available);

        mDemoSlider = (SliderLayout)findViewById(R.id.slider);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);

        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.addOnPageChangeListener(this);

        if(savedInstanceState != null) {
            currentQuantity = savedInstanceState.getInt(SAVED_INSTANCE_CURRENT_QUANTITY);
        }

        progressDialog.show();
        productId = getIntent().getStringExtra(KeyConstants.INTENT_CONSTANT_PRODUCT_ID);
        new GetProductByProductId(this, PRODUCT_DETIALS_REQUEST_CODE).execute(productId);
    }

    private void setProductDetails() {

        for(String imagePath : productModel.getAll_images()) {
            TextSliderView textSliderView = new TextSliderView(this);

            // initialize a SliderLayout
            textSliderView
                    .image(imagePath)
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", imagePath);

            mDemoSlider.addSlider(textSliderView);
        }

        txtBrandName.setText(productModel.getName());
        double price = Double.parseDouble(productModel.getPrice());
        txtAmount.setText("Rs."+price);
        txtDescription.setText(productModel.getDescription());
        txtManufacturer.setText(productModel.getManufacturer());
        txtStockStatus.setText(productModel.getStock_status());
        txtQuantityAvailable.setText(productModel.getQuantity());

        availableQuantity = Integer.parseInt(productModel.getQuantity());
        if(availableQuantity < currentQuantity) {
            txtCurrentQuatity.setText(String.valueOf(availableQuantity));
        }
        else {
            txtCurrentQuatity.setText(String.valueOf(currentQuantity));
        }

        btnIncrQuatity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentQuantity < availableQuantity) {
                    currentQuantity++;
                    txtCurrentQuatity.setText(String.valueOf(currentQuantity));
                }
                else {
                    Toast.makeText(mAppContext, "Available quantity is "+availableQuantity, Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnDecrQuatity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentQuantity > 1) {
                    currentQuantity--;
                    txtCurrentQuatity.setText(String.valueOf(currentQuantity));
                }
                else {
                    Toast.makeText(mAppContext, "Available quantity is "+availableQuantity, Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mAppContext, "Buy "+ currentQuantity + " items of " +productModel.getName() , Toast.LENGTH_SHORT).show();
                //TODO: buy product option
            }
        });

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mAppContext, "Added "+ currentQuantity + " items of " +productModel.getName() + " to the cart" , Toast.LENGTH_SHORT).show();
                productModel.setNumberOfSeletedItems(currentQuantity);
                AppPreferences.getInstance().addProductToCartList(productModel);
            }
        });
    }

    @Override
    public void responseReceived(String response, int requestCode, int responseCode, int extraRequestCode) {
        progressDialog.dismiss();
        if(responseCode == ServerResponseSubscriber.RESPONSE_CODE_OK) {
            if(requestCode == PRODUCT_DETIALS_REQUEST_CODE) {
                Log.i(TAG, "received response : " + response);
                if(!response.isEmpty()) {
                    try {
                        productModel = new Gson().fromJson(response, new TypeToken<ProductModel>() {}.getType());
                        if(productModel != null) {
                            setProductDetails();
                        }
                    }catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            }
        }
        else {
            Toast.makeText(mActivityContext, "Error in fetching product details", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Error in getting product details for id " + productId + " error is " + response);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SAVED_INSTANCE_CURRENT_QUANTITY, currentQuantity);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
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
}
