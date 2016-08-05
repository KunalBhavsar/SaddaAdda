package com.emiadda.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.emiadda.EAApplication;
import com.emiadda.R;
import com.emiadda.adapters.CategoryAdapter;
import com.emiadda.adapters.DrawerAdapter;
import com.emiadda.asynctasks.ServerRequestProcessingThread;
import com.emiadda.core.EACategory;
import com.emiadda.core.EAServerRequest;
import com.emiadda.interafaces.ServerResponseSubscriber;
import com.emiadda.utils.AppPreferences;
import com.emiadda.utils.KeyConstants;
import com.emiadda.views.WrapHeightGridView;
import com.emiadda.wsdl.CategoryModel;
import com.emiadda.wsdl.CustomerModel;
import com.emiadda.wsdl.ProductImageModel;
import com.emiadda.wsdl.ProductModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ServerResponseSubscriber {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GET_CATEGORIES_REQUEST_CODE = 12;
    private static final int GET_SPECIAL_PRODUCTS = 13;

    private Activity mActivityContext;
    private Context mAppContext;

    private NavigationView navigationView;
    private RecyclerView drawerRecyclerView;
    private DrawerAdapter drawerAdapter;
    private DrawerLayout drawer;
    private EditText edtSearch;

    private WrapHeightGridView gridCategories;
    private CategoryAdapter categoryAdapter;
    private List<EACategory> masterCategoryList;

    private LinearLayout linSpecialProducts;
    private HashMap<String, ProductModel> specialProductHashmap;
    private boolean inForeground = true;
    private boolean dismissLoading;
    private RelativeLayout rltProgress;

    private Fragment cartFragment;
    private RelativeLayout btnNext;
    private RelativeLayout btnPrev;
    private Button btnViewAll;
    private HorizontalScrollView horizontalScrollView;

    private TextView txtNoCategories;
    private TextView txtNoSpecialProducts;

    private View navDrawerHeaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivityContext = this;
        mAppContext = getApplicationContext();

        setUpCartFragment();

        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        edtSearch = (EditText) findViewById(R.id.edt_search);
        btnNext = (RelativeLayout) findViewById(R.id.btnNext);
        btnPrev = (RelativeLayout) findViewById(R.id.btnPrevoius);
        btnViewAll = (Button) findViewById(R.id.btn_view_all);
        txtNoCategories = (TextView)findViewById(R.id.txt_no_categories);
        txtNoSpecialProducts = (TextView)findViewById(R.id.txt_no_special_products);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        linSpecialProducts = (LinearLayout) findViewById(R.id.lin_special_product_list);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        gridCategories = (WrapHeightGridView) findViewById(R.id.grd_categories);
        ImageView imgCart = (ImageView) findViewById(R.id.img_cart);
        rltProgress = (RelativeLayout)findViewById(R.id.rlt_progress);
        navDrawerHeaderView = navigationView.getHeaderView(0);
        drawerRecyclerView = (RecyclerView) navDrawerHeaderView.findViewById(R.id.recycler_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        masterCategoryList = new ArrayList<>();
        specialProductHashmap = new HashMap<>();
        categoryAdapter = new CategoryAdapter(this);
        drawerAdapter = new DrawerAdapter(this);

        gridCategories.setAdapter(categoryAdapter);
        drawerRecyclerView.setLayoutManager(new LinearLayoutManager(mActivityContext));
        drawerRecyclerView.setHasFixedSize(true);
        drawerRecyclerView.setAdapter(drawerAdapter);

        assert imgCart != null;
        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivityContext, CartActivity.class);
                startActivity(intent);
            }
        });

        gridCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (categoryAdapter != null) {
                    EACategory eaCategory = (EACategory) categoryAdapter.getItem(position);
                    Intent intent = new Intent(MainActivity.this, SubCategoryActivity.class);
                    intent.putExtra(KeyConstants.INTENT_CONSTANT_CATEGORY_ID, eaCategory.getId());
                    intent.putExtra(KeyConstants.INTENT_CONSTANT_CATEGORY_NAME, eaCategory.getCategoryName().replaceAll("&amp;", "&"));
                    startActivity(intent);
                }
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
                if (categoryAdapter == null) {
                    return;
                }
                categoryAdapter.getFilter().filter(s.toString());
            }
        });

        categoryAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if(categoryAdapter.isEmpty()) {
                    txtNoCategories.setVisibility(View.VISIBLE);
                    gridCategories.setVisibility(View.GONE);
                }
                else {
                    txtNoCategories.setVisibility(View.GONE);
                    gridCategories.setVisibility(View.VISIBLE);
                }
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int widthOfOneElement = linSpecialProducts.getChildAt(0).getMeasuredWidth();
                horizontalScrollView.scrollTo((int)horizontalScrollView.getScrollX() - widthOfOneElement, (int)horizontalScrollView.getScrollY());
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int widthOfOneElement = linSpecialProducts.getChildAt(0).getMeasuredWidth();
                horizontalScrollView.scrollTo((int)horizontalScrollView.getScrollX() + widthOfOneElement, (int)horizontalScrollView.getScrollY());
            }
        });

        btnViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivityContext, ProductListActivity.class);
                intent.putExtra(KeyConstants.INTENT_CONSTANT_SUB_CATEGORY_ID, 0L);
                intent.putExtra(KeyConstants.INTENT_CONSTANT_SUB_CATEGORY_NAME, "New Arrivals");
                startActivity(intent);
            }
        });

        refreshUserLoginStatusOnNavDrawer();

        showProgress(true);
        ((EAApplication) mAppContext).attach(this);
        EAApplication.makeServerRequest(ServerRequestProcessingThread.REQUEST_CODE_GET_CATEGORY, GET_CATEGORIES_REQUEST_CODE, EAServerRequest.PRIORITY_HIGH, TAG, String.valueOf(0));
        EAApplication.makeServerRequest(ServerRequestProcessingThread.REQUEST_CODE_GET_SPECIAL_PRODUCTS, GET_SPECIAL_PRODUCTS, EAServerRequest.PRIORITY_MEDIUM, TAG);
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
        ((EAApplication) mAppContext).attach(this);
    }

    @Override
    protected void onDestroy() {
        ((EAApplication) mAppContext).dettach(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        Fragment currentFragment  = getSupportFragmentManager().findFragmentByTag(KeyConstants.CART_FRAGMENT);
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.detach(currentFragment);
        fragTransaction.attach(currentFragment);
        fragTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void responseReceived(String response, int requestCode, int responseCode, int extraRequestCode, String activityTag) {
        if(!TAG.equals(activityTag)) {
            return;
        }
        if(inForeground) {
            showProgress(false);
        }
        else {
            dismissLoading = true;
        }
        if (requestCode == ServerRequestProcessingThread.REQUEST_CODE_GET_CATEGORY
                && extraRequestCode == GET_CATEGORIES_REQUEST_CODE) {
            Log.i(TAG, "Categories response : " + response);
            if (responseCode == ServerResponseSubscriber.RESPONSE_CODE_OK) {
                List<CategoryModel> categoryModelList = new Gson().fromJson(response,
                        new TypeToken<ArrayList<CategoryModel>>() {
                        }.getType());
                processGetCategoriesResponse(categoryModelList);
                refreshCatergoryUI();
            }
            else {
                showToast("Error in fetching Categories", Toast.LENGTH_SHORT);
            }
        }
        else if (requestCode == ServerRequestProcessingThread.REQUEST_CODE_GET_SPECIAL_PRODUCTS
                && extraRequestCode == GET_SPECIAL_PRODUCTS) {
            Log.i(TAG, "Special products response : " + response);
            if (responseCode == ServerResponseSubscriber.RESPONSE_CODE_OK) {
                if (!response.isEmpty()) {
                    specialProductHashmap = new Gson().fromJson(response,
                            new TypeToken<HashMap<String, ProductModel>>() {}.getType());
                    refreshSpecialProductUI();
                    updateImagesOfSpecialProductsUI();
                }
            }
            else {
                showToast("Error in fetching Special products", Toast.LENGTH_SHORT);
            }
        }
        else if (requestCode == ServerRequestProcessingThread.REQUEST_CODE_GET_PRODUCT_IMAGE) {
            Log.i(TAG, "Get image response response : " + response);
            if(response != null && !response.isEmpty()) {
                ProductImageModel productImageModel = new Gson().fromJson(response,
                        new TypeToken<ProductImageModel>() {
                        }.getType());
                final String imagePath = productImageModel.getImage().replaceAll("&amp;", "&").replaceAll(" ", "%20");
                specialProductHashmap.get(String.valueOf(extraRequestCode)).setActualImage(imagePath);
                updateImagesOfSpecialProductsUI();
            }
        }
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
    }

    private void refreshUserLoginStatusOnNavDrawer() {
        if (navigationView != null) {
            RelativeLayout relUserProfile = (RelativeLayout) navDrawerHeaderView.findViewById(R.id.rlt_user_profile);
            Button buttonLogin = (Button) navDrawerHeaderView.findViewById(R.id.btn_login);
            if(AppPreferences.getInstance().isUserLoggedIn()) {
                relUserProfile.setVisibility(View.VISIBLE);
                buttonLogin.setVisibility(View.GONE);

                CustomerModel appOwner = AppPreferences.getInstance().getAppOwnerData();
                TextView txtName = (TextView) navDrawerHeaderView.findViewById(R.id.txt_name);
                txtName.setText(appOwner.getFirstname() + " " + appOwner.getLastname());

                TextView txtEmail = (TextView) navDrawerHeaderView.findViewById(R.id.txt_email);
                txtEmail.setText(appOwner.getEmail());

                TextView txtHome = (TextView) navDrawerHeaderView.findViewById(R.id.txt_home);
                txtHome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawer.closeDrawers();
                    }
                });

                navDrawerHeaderView.findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppPreferences.getInstance().setUserLoggegIn(false);
                        AppPreferences.getInstance().clearAppOwnerData();
                        Intent intent = new Intent(mActivityContext, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        mActivityContext.startActivity(intent);
                        finish();
                    }
                });
            }
            else {
                buttonLogin.setVisibility(View.VISIBLE);
                relUserProfile.setVisibility(View.GONE);

                buttonLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mActivityContext, LoginActivity.class);
                        mActivityContext.startActivity(intent);
                    }
                });
            }
        }
    }

    private void refreshCatergoryUI() {
        if (inForeground) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (categoryAdapter == null) {
                        categoryAdapter = new CategoryAdapter(mActivityContext);
                        if (gridCategories != null) {
                            gridCategories.setAdapter(categoryAdapter);
                        }
                    }
                    categoryAdapter.resetCategories(masterCategoryList);
                    if (drawerAdapter == null) {
                        drawerAdapter = new DrawerAdapter(mActivityContext);
                        if (drawerRecyclerView != null) {
                            drawerRecyclerView.setLayoutManager(new LinearLayoutManager(mActivityContext));
                            drawerRecyclerView.setHasFixedSize(true);
                            drawerRecyclerView.setAdapter(drawerAdapter);
                        }
                    }
                    drawerAdapter.resetCategories(masterCategoryList);
                    if(masterCategoryList.isEmpty()) {
                        txtNoCategories.setVisibility(View.VISIBLE);
                        gridCategories.setVisibility(View.GONE);
                    }
                    else {
                        txtNoCategories.setVisibility(View.GONE);
                        gridCategories.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    private void refreshSpecialProductUI() {
        if (inForeground) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    linSpecialProducts.removeAllViews();
                    for (Map.Entry<String, ProductModel> specialProductModelEntry : specialProductHashmap.entrySet()) {
                        View view = LayoutInflater.from(mActivityContext)
                                .inflate(R.layout.item_special_product, null, false);
                        String productKey = specialProductModelEntry.getKey();
                        ProductModel productModel = specialProductModelEntry.getValue();

                        view.setTag(productKey);
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mActivityContext, ProductDetailActivity.class);
                                intent.putExtra(KeyConstants.INTENT_CONSTANT_PRODUCT_ID,
                                        specialProductHashmap.get((String)v.getTag()).getProduct_id());
                                mActivityContext.startActivity(intent);
                            }
                        });
                        ((TextView)view.findViewById(R.id.txt_product_name)).setText(productModel.getName().replaceAll("&amp;","&"));
                        Picasso.with(mAppContext).load(productModel.getActualImage())
                                .placeholder(R.drawable.placeholder_product).into((ImageView) view.findViewById(R.id.img_product));
                        linSpecialProducts.addView(view);
                    }

                    if(specialProductHashmap.isEmpty()) {
                        txtNoSpecialProducts.setVisibility(View.VISIBLE);
                        btnNext.setVisibility(View.GONE);
                        btnPrev.setVisibility(View.GONE);
                        linSpecialProducts.setVisibility(View.GONE);
                        btnViewAll.setVisibility(View.GONE);
                    }
                    else {
                        txtNoSpecialProducts.setVisibility(View.GONE);
                        btnNext.setVisibility(View.VISIBLE);
                        btnPrev.setVisibility(View.VISIBLE);
                        linSpecialProducts.setVisibility(View.VISIBLE);
                        btnViewAll.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    private void updateImagesOfSpecialProductsUI() {
        if (inForeground) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    List<EAServerRequest> serverRequests = new ArrayList<>();

                    for (int i = 0; i < linSpecialProducts.getChildCount(); i++) {
                        View view = linSpecialProducts.getChildAt(i);
                        String specialProductKey = (String) view.getTag();
                        ProductModel specialProduct = specialProductHashmap.get(specialProductKey);
                        if((specialProduct.getActualImage() == null
                                || specialProduct.getActualImage().isEmpty())
                                && !specialProduct.isLoadingImage()) {
                            specialProduct.setLoadingImage(true);
/*                            EAServerRequest eaServerRequest = new EAServerRequest(ServerRequestProcessingThread.REQUEST_CODE_GET_PRODUCT_IMAGE,
                                    Integer.parseInt(specialProductKey), EAServerRequest.PRIORITY_LOW);
                            eaServerRequest.setActivityTag(TAG);
                            eaServerRequest.setParams(specialProduct.getProduct_id());
                            serverRequests.add(eaServerRequest);*/
                            Log.i(TAG, "Special product id : "+specialProduct.getProduct_id());
                            EAApplication.makeServerRequest(ServerRequestProcessingThread.REQUEST_CODE_GET_PRODUCT_IMAGE,
                                    Integer.parseInt(specialProductKey), EAServerRequest.PRIORITY_LOW, TAG, specialProduct.getProduct_id());
                        }
                        Picasso.with(mAppContext).load(specialProductHashmap.get(specialProductKey).getActualImage())
                                .placeholder(R.drawable.placeholder_product).into((ImageView)view.findViewById(R.id.img_product));
                    }
/*                    for (EAServerRequest serverRequest : serverRequests) {
                        Log.i(TAG, "Special product id : "+serverRequest.getParams());
                    }
                    EAApplication.addServerRequests(serverRequests);*/
                }
            });
        }
    }

    private void showToast(final String textToBeShown, final int toastDuration) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mAppContext, textToBeShown, toastDuration).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        inForeground = true;
        if(dismissLoading) {
            showProgress(false);
            dismissLoading = false;
        }
        //Reset is downloading status
        for (Map.Entry<String, ProductModel> productModelEntry : specialProductHashmap.entrySet()) {
            productModelEntry.getValue().setLoadingImage(false);
        }
        refreshCatergoryUI();
        refreshSpecialProductUI();
        updateImagesOfSpecialProductsUI();
        refreshUserLoginStatusOnNavDrawer();

        Fragment currentFragment  = getSupportFragmentManager().findFragmentByTag(KeyConstants.CART_FRAGMENT);
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.detach(currentFragment);
        fragTransaction.attach(currentFragment);
        fragTransaction.commit();
    }

    @Override
    protected void onPause() {
        inForeground = false;
        drawer.closeDrawers();
        super.onPause();
    }
}

