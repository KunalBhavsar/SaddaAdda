package com.emiadda.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.emiadda.EAApplication;
import com.emiadda.R;
import com.emiadda.adapters.ProductGridAdapter;
import com.emiadda.asynctasks.ServerRequestProcessingThread;
import com.emiadda.core.EAServerRequest;
import com.emiadda.interafaces.ServerResponseSubscriber;
import com.emiadda.server.ServerResponse;
import com.emiadda.utils.AppUtils;
import com.emiadda.utils.KeyConstants;
import com.emiadda.wsdl.ProductImageModel;
import com.emiadda.wsdl.ProductModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ProductListActivity extends AppCompatActivity implements ServerResponseSubscriber {

    private static final int GET_PRODUCT_REQUEST_CODE = 17;
    private static final String TAG = ProductListActivity.class.getSimpleName();

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerViewProductList;
    private ProductGridAdapter productGridAdapter;
    private Toolbar toolbar;
    private Activity mActivityContext;
    private Context mAppContext;
    private EditText edtSearch;
    private List<ProductModel> masterProductList;
    private LinearLayout lnrSort;

    private int getProductListState;
    private boolean inForeground = true;
    private boolean dismissProductsLoading;

    private Fragment cartFragment;
    private String selectedSubcategoryName;
    private long selectedSubcategoryId;

    private RelativeLayout relRetryProducts;
    private TextView txtRetryProducts;
    private ImageView imgRetryProducts;
    private RelativeLayout relProgressProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        setUpCartFragment();
        lnrSort = (LinearLayout) findViewById(R.id.lnr_sort);
        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mActivityContext = this;
        mAppContext = getApplicationContext();

        relRetryProducts = (RelativeLayout) findViewById(R.id.rel_retry_products);
        txtRetryProducts = (TextView) findViewById(R.id.txt_retry_products);
        imgRetryProducts = (ImageView) findViewById(R.id.img_retry_products);

        relProgressProducts = (RelativeLayout) findViewById(R.id.rlt_progress_products);

        masterProductList = new ArrayList<>();

        recyclerViewProductList = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerViewProductList.setHasFixedSize(true);
        recyclerViewProductList.addItemDecoration(new GridSpaceItemDecoration(10));
        productGridAdapter = new ProductGridAdapter(mActivityContext, new ProductGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ProductModel item) {
                Intent intent = new Intent(mActivityContext, ProductDetailActivity.class);
                intent.putExtra(KeyConstants.INTENT_CONSTANT_PRODUCT_ID, item.getProduct_id());
                intent.putExtra(KeyConstants.INTENT_CONSTANT_PRODUCT_NAME, item.getName());
                mActivityContext.startActivity(intent);
            }
        });

        ImageView imgCart = (ImageView) findViewById(R.id.img_cart);
        assert imgCart != null;
        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CartFrgament)cartFragment).getSize() <= 0) {
                    Toast.makeText(ProductListActivity.this, "Add products into cart", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(mActivityContext, CartActivity.class);
                startActivity(intent);
            }
        });

        layoutManager = new GridLayoutManager(ProductListActivity.this, 2);
        recyclerViewProductList.setLayoutManager(layoutManager);
        recyclerViewProductList.setAdapter(productGridAdapter);

        productGridAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if(getProductListState == KeyConstants.SERVER_CALL_STATUS_SUCCESS) {
                    if (productGridAdapter.getItemCount() == 0) {
                        relRetryProducts.setVisibility(View.VISIBLE);
                        txtRetryProducts.setVisibility(View.VISIBLE);
                        recyclerViewProductList.setVisibility(View.GONE);
                    } else {
                        relRetryProducts.setVisibility(View.GONE);
                        txtRetryProducts.setVisibility(View.GONE);
                        recyclerViewProductList.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        selectedSubcategoryName = getIntent().getStringExtra(KeyConstants.INTENT_CONSTANT_SUB_CATEGORY_NAME).replaceAll("&amp;", "&");
        TextView  subcategoryTextView = (TextView) findViewById(R.id.txt_category_name);
        if(subcategoryTextView != null)
        subcategoryTextView.setText(selectedSubcategoryName);

        edtSearch = (EditText) findViewById(R.id.edt_search);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (productGridAdapter == null) {
                    return;
                }
                productGridAdapter.getFilter().filter(s.toString());
            }
        });

        lnrSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] languages = {"Name", "Price"};
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivityContext);
                dialogBuilder.setTitle("Select crop type");
                dialogBuilder.setItems(languages, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (masterProductList != null && !masterProductList.isEmpty()) {
                            if (i == 0) {
                                Collections.sort(masterProductList, new ProductNameComparator());
                            } else {
                                Collections.sort(masterProductList, new ProductPriceComparator());
                            }
                            productGridAdapter.notifyDataSetChanged();
                        }
                    }
                });
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
            }
        });

        ((EAApplication) mAppContext).attach(this);

        if(AppUtils.isNetworkAvailable(mAppContext)) {
            getProductListState = KeyConstants.SERVER_CALL_STATUS_ONGOING;
            relProgressProducts.setVisibility(View.VISIBLE);
            selectedSubcategoryId = getIntent().getLongExtra(KeyConstants.INTENT_CONSTANT_SUB_CATEGORY_ID, 0);
            EAApplication.makeServerRequest(ServerRequestProcessingThread.REQUEST_CODE_GET_PRODUCTS_BY_CATEGORY, GET_PRODUCT_REQUEST_CODE, EAServerRequest.PRIORITY_HIGH, TAG, String.valueOf(selectedSubcategoryId));
        }
        else {
            getProductListState = KeyConstants.SERVER_CALL_STATUS_ERROR_OCCURED;

            refreshEmptyListUI();
            Toast.makeText(mAppContext, mAppContext.getString(R.string.no_network_toast), Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpCartFragment() {
        cartFragment = new CartFrgament();
        FragmentManager fragmentManager =getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.cart_fragment, cartFragment, KeyConstants.CART_FRAGMENT);
        fragmentTransaction.commit();
    }

    private void refreshEmptyListUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (getProductListState == KeyConstants.SERVER_CALL_STATUS_ONGOING) {
                    imgRetryProducts.setVisibility(View.GONE);
                    txtRetryProducts.setVisibility(View.GONE);
                    relRetryProducts.setVisibility(View.GONE);
                    recyclerViewProductList.setVisibility(View.GONE);
                } else if (getProductListState == KeyConstants.SERVER_CALL_STATUS_ERROR_OCCURED) {
                    relRetryProducts.setVisibility(View.VISIBLE);
                    imgRetryProducts.setVisibility(View.VISIBLE);
                    txtRetryProducts.setVisibility(View.VISIBLE);
                    txtRetryProducts.setText("Error in fetching sub categories, Retry..");
                    recyclerViewProductList.setVisibility(View.GONE);

                    relRetryProducts.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            relProgressProducts.setVisibility(View.VISIBLE);
                            getProductListState = KeyConstants.SERVER_CALL_STATUS_ONGOING;
                            imgRetryProducts.setVisibility(View.GONE);
                            txtRetryProducts.setVisibility(View.GONE);
                            EAApplication.makeServerRequest(ServerRequestProcessingThread.REQUEST_CODE_GET_PRODUCTS_BY_CATEGORY, GET_PRODUCT_REQUEST_CODE, EAServerRequest.PRIORITY_HIGH, TAG, String.valueOf(selectedSubcategoryId));
                        }
                    });
                } else if (getProductListState == KeyConstants.SERVER_CALL_STATUS_SUCCESS) {
                    relRetryProducts.setVisibility(View.GONE);
                    imgRetryProducts.setVisibility(View.GONE);
                    txtRetryProducts.setVisibility(View.GONE);
                    recyclerViewProductList.setVisibility(View.VISIBLE);
                    txtRetryProducts.setText(mActivityContext.getString(R.string.no_result));
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

    @Override
    protected void onResume() {
        super.onResume();
        inForeground = true;
        if (dismissProductsLoading) {
            relProgressProducts.setVisibility(View.GONE);
            dismissProductsLoading = false;
        }
        //Reset is downloading status
        for (ProductModel productModel : masterProductList) {
            productModel.setLoadingImage(false);
        }
        updateProductListUI();
        Fragment currentFragment  = getSupportFragmentManager().findFragmentByTag(KeyConstants.CART_FRAGMENT);
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.detach(currentFragment);
        fragTransaction.attach(currentFragment);
        fragTransaction.commit();
    }

    @Override
    protected void onPause() {
        inForeground = false;
        super.onPause();
    }

    class ProductNameComparator implements Comparator<ProductModel> {

        @Override
        public int compare(ProductModel lhs, ProductModel rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    }

    class ProductPriceComparator implements Comparator<ProductModel> {

        @Override
        public int compare(ProductModel lhs, ProductModel rhs) {

            double lhsPrice = Double.parseDouble(lhs.getPrice());
            double rhsPrice = Double.parseDouble(rhs.getPrice());

            if (lhsPrice < rhsPrice) {
                return -1;
            } else if (lhsPrice > rhsPrice) {
                return 1;
            }
            return 0;
        }
    }

    @Override
    public void responseReceived(ServerResponse response, int requestCode, int extraRequestCode, String activityTag) {
        if (!TAG.equals(activityTag)) {
            return;
        }

        if(response == null) {
            Log.e(TAG, "Received null response for request code "+requestCode);
            return;
        }

        if (requestCode == ServerRequestProcessingThread.REQUEST_CODE_GET_PRODUCTS_BY_CATEGORY &&
                extraRequestCode == GET_PRODUCT_REQUEST_CODE) {
            Log.i(TAG, "Get products by category response : " + response);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (inForeground) {
                        relProgressProducts.setVisibility(View.GONE);
                    } else {
                        dismissProductsLoading = true;
                    }

                }
            });
            if (response.getResponseStatus() == ServerResponse.SERVER_OK) {
                getProductListState = KeyConstants.SERVER_CALL_STATUS_SUCCESS;
                refreshEmptyListUI();

                if (response.getResponse() != null && !response.getResponse().isEmpty()) {
                    try {
                        HashMap<String, ProductModel> map = new Gson().fromJson(response.getResponse(),
                                new TypeToken<HashMap<String, ProductModel>>() {}.getType());
                        if (map != null) {
                            List<ProductModel> productModelList = new ArrayList<>();
                            Set<String> set = map.keySet();
                            for (String key : set) {
                                productModelList.add(map.get(key));
                            }
                            masterProductList = productModelList;
                            updateProductListUI();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            } else if(response.getResponseStatus() == ServerResponse.SERVER_ERROR ||
                    response.getResponseStatus() == ServerResponse.NETWORK_ERROR) {
                getProductListState = KeyConstants.SERVER_CALL_STATUS_ERROR_OCCURED;
                refreshEmptyListUI();
            }
        }
        else if (requestCode == ServerRequestProcessingThread.REQUEST_CODE_GET_PRODUCT_IMAGE) {
            Log.i(TAG, "Get product image response : " + response);
            if (response.getResponseStatus() == ServerResponse.SERVER_OK) {
                if (response.getResponse() != null && !response.getResponse().isEmpty()) {
                    ProductImageModel productImageModel = new Gson().fromJson(response.getResponse(),
                            new TypeToken<ProductImageModel>() {
                            }.getType());
                    final String imagePath = productImageModel.getImage().replaceAll("&amp;", "&").replaceAll(" ", "%20");
                    for (ProductModel productModel : masterProductList) {
                        if (extraRequestCode == Integer.parseInt(productModel.getProduct_id())) {
                            productModel.setActualImage(imagePath);
                            break;
                        }
                    }
                    updateProductListUI();
                }
            }
            else {
                Log.e(TAG, "Error in getting product images : "+response.getError());
            }
        }
    }

    private void updateProductListUI() {
        if (inForeground) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    productGridAdapter.resetProducts(masterProductList);
                    for (ProductModel productModel : masterProductList) {
                        if ((productModel.getActualImage() == null || productModel.getActualImage().isEmpty())
                                && !productModel.isLoadingImage()) {
                            productModel.setLoadingImage(true);
                            EAApplication.makeServerRequest(ServerRequestProcessingThread.REQUEST_CODE_GET_PRODUCT_IMAGE,
                                    Integer.parseInt(productModel.getProduct_id()), EAServerRequest.PRIORITY_LOW, TAG, productModel.getProduct_id());
                        }
                    }
                }
            });
        }
    }

    public class GridSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int mVerticalSpaceHeight;

        public GridSpaceItemDecoration(int mVerticalSpaceHeight) {
            this.mVerticalSpaceHeight = mVerticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = mVerticalSpaceHeight;

            int i = parent.getChildAdapterPosition(view);
            if ((i % 2) == 0) {
                outRect.right = mVerticalSpaceHeight;
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
