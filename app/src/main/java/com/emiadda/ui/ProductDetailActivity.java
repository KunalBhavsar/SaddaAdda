package com.emiadda.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.emiadda.EAApplication;
import com.emiadda.R;
import com.emiadda.asynctasks.ServerRequestProcessingThread;
import com.emiadda.core.EAServerRequest;
import com.emiadda.interafaces.ServerResponseSubscriber;
import com.emiadda.utils.AppPreferences;
import com.emiadda.utils.KeyConstants;
import com.emiadda.wsdl.ProductModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.DecimalFormat;

/**
 * Created by Kunal on 12/07/16.
 */
public class ProductDetailActivity extends AppCompatActivity implements ServerResponseSubscriber, BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private static final String TAG = ProductDetailActivity.class.getSimpleName();
    private static final int PRODUCT_DETIALS_REQUEST_CODE = 1;
    private static final String SAVED_INSTANCE_CURRENT_QUANTITY = "current_quantity";
    private static final String SAVED_INSTANCE_FROM_CART = "from_cart";

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

    private int currentQuantity = 1;
    private int availableQuantity;
    private Toolbar toolbar;
    private boolean inForeground;
    private boolean dismissProgress;
    private RelativeLayout rltProgress;
    private String selectedProductName;

    private Fragment cartFragment;
    private boolean fromCart;
    private DecimalFormat formater = new DecimalFormat("#.##");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_detail);
        setUpCartFragment();
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
                if(fromCart) {
                    mActivityContext.finish();
                    return;
                }
                if(((CartFrgament)cartFragment).getSize() <= 0) {
                    Toast.makeText(ProductDetailActivity.this, "Add products into cart", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(mActivityContext, CartActivity.class);
                startActivity(intent);
            }
        });

        mActivityContext = this;
        mAppContext = getApplicationContext();

        txtBrandName = (TextView) findViewById(R.id.txt_brand_name);
        txtAmount = (TextView) findViewById(R.id.txt_amount);
        btnIncrQuatity = (Button) findViewById(R.id.btn_incr_quantity);
        btnDecrQuatity = (Button) findViewById(R.id.btn_decr_quantity);
        txtCurrentQuatity = (TextView) findViewById(R.id.txt_quantity);
        btnBuyNow = (Button) findViewById(R.id.btn_buy_now);
        btnAddToCart = (Button) findViewById(R.id.btn_add_to_cart);
        txtDescription = (TextView) findViewById(R.id.txt_description);
        txtManufacturer = (TextView) findViewById(R.id.txt_manufacturer);
        txtStockStatus = (TextView) findViewById(R.id.txt_stock_status);
        txtQuantityAvailable = (TextView) findViewById(R.id.txt_quantity_available);
        rltProgress = (RelativeLayout) findViewById(R.id.rlt_progress);
        mDemoSlider = (SliderLayout) findViewById(R.id.slider);

        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.addOnPageChangeListener(this);

        if (savedInstanceState != null) {
            fromCart = savedInstanceState.getBoolean(SAVED_INSTANCE_FROM_CART);
            currentQuantity = savedInstanceState.getInt(SAVED_INSTANCE_CURRENT_QUANTITY);
        }
        else {
            fromCart = getIntent().hasExtra(KeyConstants.INTENT_CONSTANT_PRODUCT_ITEM_SELECTED_COUNT);
            currentQuantity = getIntent().getIntExtra(KeyConstants.INTENT_CONSTANT_PRODUCT_ITEM_SELECTED_COUNT, 1);
        }
        productId = getIntent().getStringExtra(KeyConstants.INTENT_CONSTANT_PRODUCT_ID);
        selectedProductName = getIntent().getStringExtra(KeyConstants.INTENT_CONSTANT_PRODUCT_NAME);

        showProgress(true);
        ((EAApplication) mAppContext).attach(this);

        EAApplication.makeServerRequest(ServerRequestProcessingThread.REQUEST_CODE_GET_PRODUCT_BY_PRODUCT_ID, PRODUCT_DETIALS_REQUEST_CODE,
                EAServerRequest.PRIORITY_HIGH, TAG, productId);
    }

    private void setUpCartFragment() {
        cartFragment = new CartFrgament();
        FragmentManager fragmentManager =getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.cart_fragment, cartFragment, KeyConstants.CART_FRAGMENT);
        fragmentTransaction.commit();
    }

    private void showProgress(final boolean visibile) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (visibile) {
                    rltProgress.setVisibility(View.VISIBLE);
                } else {
                    rltProgress.setVisibility(View.GONE);
                }
            }
        });
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

    private void setProductDetails() {
        if (inForeground && productModel != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (String imagePath : productModel.getAll_images()) {
                        TextSliderView textSliderView = new TextSliderView(mActivityContext);

                        // initialize a SliderLayout
                        Log.i(TAG, "Loading product image : " + imagePath);
                        textSliderView
                                .image(imagePath)
                                .setScaleType(BaseSliderView.ScaleType.Fit)
                                .setOnSliderClickListener(ProductDetailActivity.this);

                        //add your extra information
                        textSliderView.bundle(new Bundle());
                        textSliderView.getBundle()
                                .putString("extra", imagePath);

                        mDemoSlider.addSlider(textSliderView);
                    }

                    txtBrandName.setText(productModel.getName());
                    double price = Double.parseDouble(productModel.getPrice());
                    txtAmount.setText(KeyConstants.rs + formater.format(price));
                    txtDescription.setText(productModel.getDescription());
                    txtManufacturer.setText(productModel.getManufacturer());
                    txtStockStatus.setText(productModel.getStock_status());
                    txtQuantityAvailable.setText(productModel.getQuantity());

                    availableQuantity = Integer.parseInt(productModel.getQuantity());
                    if (availableQuantity < currentQuantity) {
                        txtCurrentQuatity.setText(String.valueOf(availableQuantity));
                    } else {
                        txtCurrentQuatity.setText(String.valueOf(currentQuantity));
                    }

                    btnIncrQuatity.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (currentQuantity < availableQuantity) {
                                currentQuantity++;
                                txtCurrentQuatity.setText(String.valueOf(currentQuantity));
                            } else {
                                Toast.makeText(mAppContext, "Available quantity is " + availableQuantity, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    btnDecrQuatity.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (currentQuantity > 1) {
                                currentQuantity--;
                                txtCurrentQuatity.setText(String.valueOf(currentQuantity));
                            } else {
                                Toast.makeText(mAppContext, "Available quantity is " + availableQuantity, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    btnBuyNow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            productModel.setQuantity(String.valueOf(currentQuantity));
                            EAApplication.setTransientSelectedProductModel(productModel);

                            Intent intent = new Intent(mActivityContext, PlaceOrderActivity.class);
                            intent.putExtra(KeyConstants.INTENT_IS_FROM_CART, false);
                            startActivity(intent);
                        }
                    });

                    btnAddToCart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            productModel.setQuantity(String.valueOf(currentQuantity));

                            if(AppPreferences.getInstance().getCartType().isEmpty()) {
                                if(Integer.parseInt(productModel.getShow_payment_option()) == 0) {
                                    Toast.makeText(mAppContext, "Added " + productModel.getQuantity() + " items of " + productModel.getName() + " to the cart", Toast.LENGTH_SHORT).show();
                                    AppPreferences.getInstance().addProductToCartList(productModel, AppPreferences.CART_TYPE_VALUE_DIRECT_PAYMENT);
                                }
                                else {
                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivityContext)
                                            .setTitle("Choose method of Payment")
                                            .setNegativeButton("EMI", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Toast.makeText(mAppContext, "Added " + productModel.getQuantity() + " items of " + productModel.getName() + " to the cart", Toast.LENGTH_SHORT).show();
                                                    AppPreferences.getInstance().addProductToCartList(productModel, AppPreferences.CART_TYPE_VALUE_EMI);
                                                    Fragment currentFragment  = getSupportFragmentManager().findFragmentByTag(KeyConstants.CART_FRAGMENT);
                                                    FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
                                                    fragTransaction.detach(currentFragment);
                                                    fragTransaction.attach(currentFragment);
                                                    fragTransaction.commit();
                                                }
                                            })
                                            .setPositiveButton("Direct Payment", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Toast.makeText(mAppContext, "Added " + productModel.getQuantity() + " items of " + productModel.getName() + " to the cart", Toast.LENGTH_SHORT).show();
                                                    AppPreferences.getInstance().addProductToCartList(productModel, AppPreferences.CART_TYPE_VALUE_DIRECT_PAYMENT);
                                                    Fragment currentFragment  = getSupportFragmentManager().findFragmentByTag(KeyConstants.CART_FRAGMENT);
                                                    FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
                                                    fragTransaction.detach(currentFragment);
                                                    fragTransaction.attach(currentFragment);
                                                    fragTransaction.commit();
                                                }
                                            });
                                    alertDialog.show();
                                }
                            }
                            else {
                                if(AppPreferences.getInstance().getCartType().equals(AppPreferences.CART_TYPE_VALUE_EMI)) {
                                    if(Integer.parseInt(productModel.getShow_payment_option()) > 0) {
                                        Toast.makeText(mAppContext, "Added " + productModel.getQuantity() + " items of " + productModel.getName() + " to the cart", Toast.LENGTH_SHORT).show();
                                        AppPreferences.getInstance().addProductToCartList(productModel, AppPreferences.getInstance().getCartType());
                                    }
                                    else {
                                        Toast.makeText(mAppContext, "Checkout all the EMI items from your cart for using Direct Payment option", Toast.LENGTH_LONG).show();
                                    }
                                }
                                else if(AppPreferences.getInstance().getCartType().equals(AppPreferences.CART_TYPE_VALUE_DIRECT_PAYMENT)){
                                    Toast.makeText(mAppContext, "Added " + productModel.getQuantity() + " items of " + productModel.getName() + " to the cart", Toast.LENGTH_SHORT).show();
                                    AppPreferences.getInstance().addProductToCartList(productModel, AppPreferences.getInstance().getCartType());
                                }
                            }
                            if(fromCart) {
                                mActivityContext.finish();
                                return;
                            }

                            Fragment currentFragment  = getSupportFragmentManager().findFragmentByTag(KeyConstants.CART_FRAGMENT);
                            FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
                            fragTransaction.detach(currentFragment);
                            fragTransaction.attach(currentFragment);
                            fragTransaction.commit();
                        }
                    });
                }
            });
        }
    }

    @Override
    public void responseReceived(String response, int requestCode, int responseCode, int extraRequestCode, String activityTag) {
        if (!TAG.equals(activityTag)) {
            return;
        }

        if (inForeground) {
            showProgress(false);
        } else {
            dismissProgress = true;
        }

        if (responseCode == ServerResponseSubscriber.RESPONSE_CODE_OK) {
            if (requestCode == ServerRequestProcessingThread.REQUEST_CODE_GET_PRODUCT_BY_PRODUCT_ID &&
                    extraRequestCode == PRODUCT_DETIALS_REQUEST_CODE) {
                Log.i(TAG, "Product details received : " + response);
                if (!response.isEmpty()) {
                    try {
                        productModel = new Gson().fromJson(response, new TypeToken<ProductModel>() {
                        }.getType());
                        if (productModel != null) {
                            setProductDetails();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            }
        } else {
            if (inForeground) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivityContext, selectedProductName + " data not available",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
            Log.i(TAG, "Error in getting product details for id " + productId + " error is " + response);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SAVED_INSTANCE_CURRENT_QUANTITY, currentQuantity);
        outState.putBoolean(SAVED_INSTANCE_FROM_CART, fromCart);
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
    protected void onPause() {
        inForeground = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        inForeground = true;
        if (dismissProgress) {
            showProgress(false);
            dismissProgress = false;
        }
        Fragment currentFragment  = getSupportFragmentManager().findFragmentByTag(KeyConstants.CART_FRAGMENT);
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.detach(currentFragment);
        fragTransaction.attach(currentFragment);
        fragTransaction.commit();
        setProductDetails();
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
