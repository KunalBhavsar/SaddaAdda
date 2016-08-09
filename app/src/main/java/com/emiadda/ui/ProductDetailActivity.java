package com.emiadda.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.emiadda.server.ServerResponse;
import com.emiadda.utils.AppPreferences;
import com.emiadda.utils.AppUtils;
import com.emiadda.utils.KeyConstants;
import com.emiadda.wsdl.ProductModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Created by Kunal on 12/07/16.
 */
public class ProductDetailActivity extends AppCompatActivity
        implements ServerResponseSubscriber, BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private static final String TAG = ProductDetailActivity.class.getSimpleName();
    private static final int PRODUCT_DETIALS_REQUEST_CODE = 1;
    private static final String SAVED_INSTANCE_CURRENT_QUANTITY = "current_quantity";
    private static final String SAVED_INSTANCE_FROM_CART = "from_cart";
    private static final int REQUEST_CODE_LOGIN = 1;

    String mimeType = "text/html";
    String encoding = "utf-8";

    private Context mAppContext;
    private Activity mActivityContext;

    private String productId;
    private SliderLayout mDemoSlider;
    private ProductModel productModel;

    private TextView txtAmount;
    private Button btnIncrQuatity;
    private Button btnDecrQuatity;
    private TextView txtCurrentQuatity;
    private Button btnBuyNow;
    private Button btnAddToCart;
    private WebView txtDescription;
    private LinearLayout lnrManufacturer, lnrProductCode, lnrRewardPoints, lnrStockStatus, lnrMrp, lnrOurPrice, lnrQuantityAvailable;
    private TextView txtManufacturer, txtProductCode, txtRewardPoints, txtStockStatus, txtMrp, txtOurPrice, txtQuantityAvailable, txtInSale;

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

        txtAmount = (TextView) findViewById(R.id.txt_amount);
        btnIncrQuatity = (Button) findViewById(R.id.btn_incr_quantity);
        btnDecrQuatity = (Button) findViewById(R.id.btn_decr_quantity);
        txtCurrentQuatity = (TextView) findViewById(R.id.txt_quantity);
        btnBuyNow = (Button) findViewById(R.id.btn_buy_now);
        btnAddToCart = (Button) findViewById(R.id.btn_add_to_cart);
        txtDescription = (WebView) findViewById(R.id.txt_description);
        txtManufacturer = (TextView) findViewById(R.id.txt_manufacturer);
        txtStockStatus = (TextView) findViewById(R.id.txt_stock_status);
        txtQuantityAvailable = (TextView) findViewById(R.id.txt_quantity_available);
        rltProgress = (RelativeLayout) findViewById(R.id.rlt_progress);
        mDemoSlider = (SliderLayout) findViewById(R.id.slider);

        lnrManufacturer = (LinearLayout)findViewById(R.id.lnr_manufacturer);
        lnrProductCode = (LinearLayout)findViewById(R.id.lnr_product_code);
        lnrRewardPoints = (LinearLayout)findViewById(R.id.lnr_reward_points);
        lnrStockStatus = (LinearLayout)findViewById(R.id.lnr_stock_status);
        lnrMrp = (LinearLayout)findViewById(R.id.lnr_mrp);
        lnrOurPrice = (LinearLayout)findViewById(R.id.lnr_our_price);
        lnrQuantityAvailable = (LinearLayout)findViewById(R.id.lnr_quantity_available);

        txtManufacturer = (TextView)findViewById(R.id.txt_manufacturer);
        txtProductCode = (TextView)findViewById(R.id.txt_product_code);
        txtRewardPoints = (TextView)findViewById(R.id.txt_reward_points);
        txtStockStatus = (TextView)findViewById(R.id.txt_stock_status);
        txtMrp = (TextView)findViewById(R.id.txt_mrp);
        txtOurPrice = (TextView)findViewById(R.id.txt_our_price);
        txtQuantityAvailable = (TextView)findViewById(R.id.txt_quantity_available);
        txtInSale = (TextView)findViewById(R.id.txt_sale);

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

        if(AppUtils.isNetworkAvailable(mAppContext)) {
            showProgress(true);
            ((EAApplication) mAppContext).attach(this);

            EAApplication.makeServerRequest(ServerRequestProcessingThread.REQUEST_CODE_GET_PRODUCT_BY_PRODUCT_ID, PRODUCT_DETIALS_REQUEST_CODE,
                    EAServerRequest.PRIORITY_HIGH, TAG, productId);
        }
        else {
            Toast.makeText(mAppContext, mAppContext.getString(R.string.no_network_toast), Toast.LENGTH_SHORT).show();
            finish();
        }
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

                    String firstConversion = Html.fromHtml(productModel.getDescription()).toString();
                    String structure = "<html>\n" +
                            "    <head>\n" +
                            "        <meta http-equiv=\"content-type\" content=\"text/html;\" charset=\"UTF-8\">\n" +
                            "            <style>\n" +
                            "                /** Specify a font named \"MyFont\",\n" +
                            "                and specify the URL where it can be found: */\n" +
                            "                @font-face {\n" +
                            "                    font-family: \"MyFont\";\n" +
                            "                    src: url('file:///android_asset/fonts/Montserrat-Regular.otf');\n" +
                            "                }\n" +
                            "                h3 { font-family:\"MyFont\";" +
                            "               color: \"#545151\";}\n" +
                            "            </style>\n" +
                            "    </head>\n" +
                            "    <body>\n" +
                            firstConversion +
                            "    </body>\n" +
                            "</html>";
                    txtDescription.loadData(structure, "text/html", "UTF-8");

                    if(productModel.getName() != null || !productModel.getName().isEmpty()) {
                        lnrManufacturer.setVisibility(View.VISIBLE);
                        txtManufacturer.setText(productModel.getName());
                    }
                    else {
                        lnrManufacturer.setVisibility(View.GONE);
                    }

                    txtStockStatus.setText(productModel.getStock_status());
                    txtQuantityAvailable.setText(productModel.getQuantity());

                    availableQuantity = Integer.parseInt(productModel.getQuantity());
                    if (availableQuantity < currentQuantity) {
                        txtCurrentQuatity.setText(String.valueOf(availableQuantity));
                    } else {
                        txtCurrentQuatity.setText(String.valueOf(currentQuantity));
                    }

                    if(productModel.getModel() == null && !productModel.getModel().isEmpty()) {
                        lnrProductCode.setVisibility(View.GONE);
                    }
                    else {
                        txtProductCode.setText(productModel.getModel());
                    }

                    if(productModel.getReward() == null) {
                        lnrRewardPoints.setVisibility(View.GONE);
                    }
                    else {
                        txtRewardPoints.setText(productModel.getReward());
                    }

                    if(productModel.getSpecial() != null && Double.parseDouble(productModel.getPrice()) > Double.parseDouble(productModel.getSpecial())) {
                        txtInSale.setVisibility(View.VISIBLE);
                        lnrMrp.setVisibility(View.VISIBLE);
                        txtMrp.setText("Rs." + formater.format(Double.parseDouble(productModel.getPrice())));
                        txtMrp.setPaintFlags(txtMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                        double price = Double.parseDouble(productModel.getSpecial());
                        txtOurPrice.setText("Rs." + formater.format(price));
                        txtAmount.setText(KeyConstants.rs + formater.format(price));
                    }
                    else {
                        txtInSale.setVisibility(View.GONE);
                        lnrMrp.setVisibility(View.GONE);
                        double price = Double.parseDouble(productModel.getPrice());
                        txtOurPrice.setText("Rs." + formater.format(price));
                        txtAmount.setText(KeyConstants.rs + formater.format(price));
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
                            if(!AppPreferences.getInstance().isUserLoggedIn()) {
                                Toast.makeText(mAppContext, "Please login first to place an order", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(mActivityContext, LoginActivity.class);
                                mActivityContext.startActivityForResult(intent, REQUEST_CODE_LOGIN);
                                return;
                            }

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

    private String getTableTagFromString(String myString) {
        int start = myString.indexOf("<table");
        if( start < 0 )
            Log.d(this.toString(), "Table start tag not found");
        else {
            int end = myString.indexOf("</table>", start) + 8;
            if( end < 0 )
                Log.d(this.toString(), "Table end tag not found");
            else
                myString = "<html><body>" + myString.substring(start, end) + "</body></html>";
        }
        return myString;
    }

    @Override
    public void responseReceived(ServerResponse response, int requestCode, int extraRequestCode, String activityTag) {
        if (!TAG.equals(activityTag)) {
            return;
        }

        if (inForeground) {
            showProgress(false);
        } else {
            dismissProgress = true;
        }

        if(response == null) {
            Log.e(TAG, "Received null response for request code "+requestCode);
            return;
        }

        if (requestCode == ServerRequestProcessingThread.REQUEST_CODE_GET_PRODUCT_BY_PRODUCT_ID &&
                extraRequestCode == PRODUCT_DETIALS_REQUEST_CODE) {

            if (response.getResponseStatus() == ServerResponse.SERVER_OK) {
                    Log.i(TAG, "Product details received : " + response);
                if(response.getResponse() != null && !response.getResponse().isEmpty()) {
                        try {
                            productModel = new Gson().fromJson(response.getResponse(), new TypeToken<ProductModel>() {}.getType());
                            if (productModel != null) {
                                setProductDetails();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage(), e);
                        }
                    }
            }
            else if(response.getResponseStatus() == ServerResponse.SERVER_ERROR ||
                    response.getResponseStatus() == ServerResponse.NETWORK_ERROR){
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
