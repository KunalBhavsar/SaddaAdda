package com.emiadda.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.emiadda.EAApplication;
import com.emiadda.R;
import com.emiadda.adapters.SubCategoryAdapter;
import com.emiadda.asynctasks.ServerRequestProcessingThread;
import com.emiadda.core.EACategory;
import com.emiadda.core.EAServerRequest;
import com.emiadda.interafaces.ServerResponseSubscriber;
import com.emiadda.server.ServerResponse;
import com.emiadda.utils.AppUtils;
import com.emiadda.utils.KeyConstants;
import com.emiadda.wsdl.CategoryModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class SubCategoryActivity extends AppCompatActivity implements ServerResponseSubscriber {
    private static final String TAG = SubCategoryActivity.class.getSimpleName();
    private static final int GET_CATEGORIES_REQUEST_CODE = 21;

    Activity mActivityContext;
    Context mAppContext;

    private int getSubCategoriesStatus;

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView subcategoryRecyclerView;
    private SubCategoryAdapter subCategoryAdapter;
    private Toolbar toolbar;
    private EditText edtSearch;
    private List<EACategory> masterCategoryList;
    private boolean inForeground;
    private boolean dismissSubCategoryProgress;
    private String categorySelected;

    private RelativeLayout relRetrySC;
    private TextView txtRetrySC;
    private ImageView imgRetrySC;
    private RelativeLayout relProgressSC;

    private long selectedMainCategoryId;

    private Fragment cartFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);
        mActivityContext = this;
        mAppContext = getApplicationContext();
        setUpCartFragment();
        edtSearch = (EditText) findViewById(R.id.edt_search);
        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        subcategoryRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        subcategoryRecyclerView.setHasFixedSize(true);

        relRetrySC = (RelativeLayout) findViewById(R.id.rel_retry_sub_categories);
        txtRetrySC = (TextView) findViewById(R.id.txt_retry_sub_categories);
        imgRetrySC = (ImageView) findViewById(R.id.img_retry_sub_categories);

        relProgressSC = (RelativeLayout) findViewById(R.id.rlt_progress_sub_categories);

        masterCategoryList = new ArrayList<>();
        subCategoryAdapter = new SubCategoryAdapter(this, new SubCategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(EACategory item) {
                Intent intent = new Intent(mActivityContext, ProductListActivity.class);
                intent.putExtra(KeyConstants.INTENT_CONSTANT_SUB_CATEGORY_ID, item.getId());
                intent.putExtra(KeyConstants.INTENT_CONSTANT_SUB_CATEGORY_NAME, item.getCategoryName());
                startActivity(intent);
            }
        });

        layoutManager = new LinearLayoutManager(SubCategoryActivity.this);
        subcategoryRecyclerView.setLayoutManager(layoutManager);
        subcategoryRecyclerView.setAdapter(subCategoryAdapter);

        ImageView imgCart = (ImageView) findViewById(R.id.img_cart);
        assert imgCart != null;
        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CartFrgament) cartFragment).getSize() <= 0) {
                    Toast.makeText(SubCategoryActivity.this, "Add products into cart", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(mActivityContext, CartActivity.class);
                startActivity(intent);
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (masterCategoryList == null) {
                    return;
                }
                subCategoryAdapter.getFilter().filter(s.toString());
            }
        });

        subCategoryAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onChanged() {
                super.onChanged();
                if (getSubCategoriesStatus == KeyConstants.SERVER_CALL_STATUS_SUCCESS) {
                    if (subCategoryAdapter.getItemCount() == 0) {
                        relRetrySC.setVisibility(View.VISIBLE);
                        txtRetrySC.setVisibility(View.VISIBLE);
                        subcategoryRecyclerView.setVisibility(View.GONE);
                    } else {
                        subcategoryRecyclerView.setVisibility(View.VISIBLE);
                        relRetrySC.setVisibility(View.GONE);
                        txtRetrySC.setVisibility(View.GONE);
                    }
                }
            }
        });

        selectedMainCategoryId = getIntent().getLongExtra(KeyConstants.INTENT_CONSTANT_CATEGORY_ID, 0);
        categorySelected = getIntent().getStringExtra(KeyConstants.INTENT_CONSTANT_CATEGORY_NAME);
        TextView txtCat = (TextView) findViewById(R.id.txt_category);
        if (txtCat != null) {
            txtCat.setText(categorySelected.toUpperCase());
        }
        ((EAApplication) mAppContext).attach(this);

        if(AppUtils.isNetworkAvailable(mAppContext)) {
            relProgressSC.setVisibility(View.VISIBLE);
            getSubCategoriesStatus = KeyConstants.SERVER_CALL_STATUS_ONGOING;
            EAApplication.makeServerRequest(ServerRequestProcessingThread.REQUEST_CODE_GET_CATEGORY,
                    GET_CATEGORIES_REQUEST_CODE, EAServerRequest.PRIORITY_HIGH, TAG, String.valueOf(selectedMainCategoryId));
        }
        else {
            getSubCategoriesStatus = KeyConstants.SERVER_CALL_STATUS_ERROR_OCCURRED;

            refreshEmptyListUI();
            Toast.makeText(mAppContext, mAppContext.getString(R.string.no_network_toast), Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpCartFragment() {
        cartFragment = new CartFrgament();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.cart_fragment, cartFragment, KeyConstants.CART_FRAGMENT);
        fragmentTransaction.commit();
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
    public void responseReceived(ServerResponse response, int requestCode, int extraRequestCode, String activityTag) {
        if (!TAG.equals(activityTag)) {
            return;
        }

        if (response == null) {
            Log.e(TAG, "Received null response for request code " + requestCode);
            return;
        }

        if (requestCode == ServerRequestProcessingThread.REQUEST_CODE_GET_CATEGORY
                && extraRequestCode == GET_CATEGORIES_REQUEST_CODE) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (inForeground) {
                        relProgressSC.setVisibility(View.GONE);
                    } else {
                        dismissSubCategoryProgress = true;
                    }
                }
            });

            Log.i(TAG, "Response of get categories : " + response);
            if (response.getResponseStatus() == ServerResponse.SERVER_OK) {
                getSubCategoriesStatus = KeyConstants.SERVER_CALL_STATUS_SUCCESS;
                refreshEmptyListUI();
                if (response.getResponse() != null && !response.getResponse().isEmpty()) {
                    List<CategoryModel> categoryModelList = new Gson().fromJson(response.getResponse(),
                            new TypeToken<ArrayList<CategoryModel>>() {
                            }.getType());
                    processGetCategoriesResponse(categoryModelList);
                    refreshCatergoryUI();
                }
            } else if (response.getResponseStatus() == ServerResponse.SERVER_ERROR
                    || response.getResponseStatus() == ServerResponse.NETWORK_ERROR) {
                getSubCategoriesStatus = KeyConstants.SERVER_CALL_STATUS_ERROR_OCCURRED;
                refreshEmptyListUI();
            }
        }
    }

    private void refreshEmptyListUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (getSubCategoriesStatus == KeyConstants.SERVER_CALL_STATUS_ONGOING) {
                    imgRetrySC.setVisibility(View.GONE);
                    txtRetrySC.setVisibility(View.GONE);
                    relRetrySC.setVisibility(View.GONE);
                } else if (getSubCategoriesStatus == KeyConstants.SERVER_CALL_STATUS_ERROR_OCCURRED) {
                    relRetrySC.setVisibility(View.VISIBLE);
                    imgRetrySC.setVisibility(View.VISIBLE);
                    txtRetrySC.setVisibility(View.VISIBLE);
                    txtRetrySC.setText("Error in fetching sub categories, Retry..");

                    relRetrySC.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            relProgressSC.setVisibility(View.VISIBLE);
                            getSubCategoriesStatus = KeyConstants.SERVER_CALL_STATUS_ONGOING;
                            imgRetrySC.setVisibility(View.GONE);
                            txtRetrySC.setVisibility(View.GONE);
                            EAApplication.makeServerRequest(ServerRequestProcessingThread.REQUEST_CODE_GET_CATEGORY,
                                    GET_CATEGORIES_REQUEST_CODE, EAServerRequest.PRIORITY_HIGH, TAG, String.valueOf(selectedMainCategoryId));
                        }
                    });
                } else if (getSubCategoriesStatus == KeyConstants.SERVER_CALL_STATUS_SUCCESS) {
                    relRetrySC.setVisibility(View.GONE);
                    imgRetrySC.setVisibility(View.GONE);
                    txtRetrySC.setVisibility(View.GONE);
                    txtRetrySC.setText(mActivityContext.getString(R.string.no_result));
                }
            }
        });
    }

    private void processGetCategoriesResponse(List<CategoryModel> categoryModelList) {
        masterCategoryList.clear();
        for (CategoryModel categoryModel : categoryModelList) {
            EACategory eaCategory = new EACategory();
            eaCategory.setId(Long.parseLong(categoryModel.getCategory_id()));
            eaCategory.setCategoryName(categoryModel.getCategory_name());
            eaCategory.setCategoryImage(categoryModel.getCategory_image());
            masterCategoryList.add(eaCategory);
        }
        refreshCatergoryUI();
    }

    private void refreshCatergoryUI() {
        if (inForeground) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (subCategoryAdapter == null) {
                        subCategoryAdapter = new SubCategoryAdapter(mActivityContext, new SubCategoryAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(EACategory item) {
                                Intent intent = new Intent(mActivityContext, ProductListActivity.class);
                                intent.putExtra(KeyConstants.INTENT_CONSTANT_SUB_CATEGORY_ID, item.getId());
                                intent.putExtra(KeyConstants.INTENT_CONSTANT_SUB_CATEGORY_NAME, item.getCategoryName());
                                startActivity(intent);
                            }
                        });

                        if (subcategoryRecyclerView != null) {
                            subcategoryRecyclerView.setAdapter(subCategoryAdapter);
                        }
                    }
                    subCategoryAdapter.resetCategories(masterCategoryList);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((EAApplication) mAppContext).attach(this);
        inForeground = true;
        if (dismissSubCategoryProgress) {
            relProgressSC.setVisibility(View.GONE);
            dismissSubCategoryProgress = false;
        }
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(KeyConstants.CART_FRAGMENT);
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.detach(currentFragment);
        fragTransaction.attach(currentFragment);
        fragTransaction.commit();
        refreshCatergoryUI();
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
}
