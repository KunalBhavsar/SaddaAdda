package com.emiadda.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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
import com.emiadda.server.ServerResponse;
import com.emiadda.utils.AppPreferences;
import com.emiadda.utils.AppUtils;
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
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements ServerResponseSubscriber {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GET_CATEGORIES_REQUEST_CODE = 12;
    private static final int GET_SPECIAL_PRODUCTS = 13;

    private int getCategoriesStatus;
    private int getSPStatus;

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
    private boolean dismissLoadingCategories;
    private boolean dismissLoadingSP;
    private RelativeLayout rltProgressCategories;
    private RelativeLayout rltProgressSP;

    private Fragment cartFragment;
    private RelativeLayout btnNext;
    private RelativeLayout btnPrev;
    private Button btnViewAll;
    private HorizontalScrollView horizontalScrollView;

    private ImageView imgMainCategoryRetry;
    private TextView txtMainCategoryRetry;
    private ImageView imgSpecialProductRetry;
    private TextView txtSpecialProductRetry;
    private RelativeLayout relRetryCategories;
    private RelativeLayout relRetrySp;

    private View navDrawerHeaderView;
    Timer repeatTask = new Timer();
    int repeatInterval = 2000;
    int count;
    int i = 0;

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
        txtMainCategoryRetry = (TextView)findViewById(R.id.txt_retry_main_categories);
        imgMainCategoryRetry = (ImageView) findViewById(R.id.img_retry_main_categories);
        txtSpecialProductRetry = (TextView)findViewById(R.id.txt_retry_sp);
        imgSpecialProductRetry = (ImageView)findViewById(R.id.img_retry_sp);
        relRetryCategories = (RelativeLayout)findViewById(R.id.rel_retry_categories);
        relRetrySp = (RelativeLayout)findViewById(R.id.rel_retry_special_product);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        linSpecialProducts = (LinearLayout) findViewById(R.id.lin_special_product_list);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        gridCategories = (WrapHeightGridView) findViewById(R.id.grd_categories);
        ImageView imgCart = (ImageView) findViewById(R.id.img_cart);
        rltProgressCategories = (RelativeLayout)findViewById(R.id.rlt_progress_categories);
        rltProgressSP = (RelativeLayout)findViewById(R.id.rlt_progress_special_product);

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
                if(((CartFrgament)cartFragment).getSize() <= 0) {
                    Toast.makeText(MainActivity.this, "Add products into cart", Toast.LENGTH_SHORT).show();
                    return;
                }
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
                if(getCategoriesStatus == KeyConstants.SERVER_CALL_STATUS_SUCCESS) {
                    if (categoryAdapter.isEmpty()) {
                        relRetryCategories.setVisibility(View.VISIBLE);
                        txtMainCategoryRetry.setVisibility(View.VISIBLE);
                        gridCategories.setVisibility(View.GONE);
                    } else {
                        relRetryCategories.setVisibility(View.GONE);
                        txtMainCategoryRetry.setVisibility(View.GONE);
                        gridCategories.setVisibility(View.VISIBLE);
                    }
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

        ((EAApplication) mAppContext).attach(this);

        if(AppUtils.isNetworkAvailable(mAppContext)) {
            rltProgressCategories.setVisibility(View.VISIBLE);
            getCategoriesStatus = KeyConstants.SERVER_CALL_STATUS_ONGOING;
            EAApplication.makeServerRequest(ServerRequestProcessingThread.REQUEST_CODE_GET_CATEGORY, GET_CATEGORIES_REQUEST_CODE, EAServerRequest.PRIORITY_HIGH, TAG, String.valueOf(0));

            rltProgressSP.setVisibility(View.VISIBLE);
            getSPStatus = KeyConstants.SERVER_CALL_STATUS_ONGOING;
            EAApplication.makeServerRequest(ServerRequestProcessingThread.REQUEST_CODE_GET_SPECIAL_PRODUCTS, GET_SPECIAL_PRODUCTS, EAServerRequest.PRIORITY_MEDIUM, TAG);
        }
        else {
            getCategoriesStatus = KeyConstants.SERVER_CALL_STATUS_ERROR_OCCURED;
            getSPStatus = KeyConstants.SERVER_CALL_STATUS_ERROR_OCCURED;

            refreshEmptyListStatus();
            Toast.makeText(mAppContext, mAppContext.getString(R.string.no_network_toast), Toast.LENGTH_SHORT).show();
        }

    }

/*
    public void timerDelayRunForScroll(long time) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                try {
                    int widthOfOneElement = linSpecialProducts.getChildAt(0).getMeasuredWidth();
                    horizontalScrollView.scrollTo((int)horizontalScrollView.getScrollX() + widthOfOneElement, (int)horizontalScrollView.getScrollY());
                } catch (Exception e) {}
            }
        }, time);
    }*/

    private void setUpCartFragment() {
        cartFragment = new CartFrgament();
        FragmentManager fragmentManager =getSupportFragmentManager();
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
    public void responseReceived(ServerResponse response, int requestCode, int extraRequestCode, String activityTag) {
        if(!TAG.equals(activityTag)) {
            return;
        }
        if(response == null) {
            Log.e(TAG, "Received null response for request code "+requestCode);
            return;
        }

        if (requestCode == ServerRequestProcessingThread.REQUEST_CODE_GET_CATEGORY
                && extraRequestCode == GET_CATEGORIES_REQUEST_CODE) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(inForeground) {
                        rltProgressCategories.setVisibility(View.GONE);
                    }
                    else {
                        dismissLoadingCategories = true;
                    }
                }
            });

            Log.i(TAG, "Categories response : " + response);
            if (response.getResponseStatus() == ServerResponse.SERVER_OK) {
                getCategoriesStatus = KeyConstants.SERVER_CALL_STATUS_SUCCESS;
                List<CategoryModel> categoryModelList = new Gson().fromJson(response.getResponse(),
                        new TypeToken<ArrayList<CategoryModel>>() {
                        }.getType());
                refreshEmptyListStatus();
                processGetCategoriesResponse(categoryModelList);
                refreshCatergoryUI();
            }
            else if(response.getResponseStatus() == ServerResponse.SERVER_ERROR ||
            response.getResponseStatus() == ServerResponse.NETWORK_ERROR) {
                getCategoriesStatus = KeyConstants.SERVER_CALL_STATUS_ERROR_OCCURED;
                Log.e(TAG, "Error in fetching categories : " + response.getError());
                refreshEmptyListStatus();
            }
        }
        else if (requestCode == ServerRequestProcessingThread.REQUEST_CODE_GET_SPECIAL_PRODUCTS
                && extraRequestCode == GET_SPECIAL_PRODUCTS) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(inForeground) {
                        rltProgressSP.setVisibility(View.GONE);
                    }
                    else {
                        dismissLoadingSP = true;
                    }
                }
            });

            Log.i(TAG, "Special products response : " + response);
            if (response.getResponseStatus() == ServerResponse.SERVER_OK) {
                getSPStatus = KeyConstants.SERVER_CALL_STATUS_SUCCESS;
                refreshEmptyListStatus();
                if (response.getResponse() != null && !response.getResponse().isEmpty()) {
                    specialProductHashmap = new Gson().fromJson(response.getResponse(),
                            new TypeToken<HashMap<String, ProductModel>>() {}.getType());
                    refreshSpecialProductUI();
                    updateImagesOfSpecialProductsUI();
                }
            }
            else if(response.getResponseStatus() == ServerResponse.SERVER_ERROR ||
                    response.getResponseStatus() == ServerResponse.NETWORK_ERROR) {
                getSPStatus = KeyConstants.SERVER_CALL_STATUS_ERROR_OCCURED;
                Log.e(TAG, "Error in fetching special products : " + response.getError());
                refreshEmptyListStatus();
            }
        }
        else if (requestCode == ServerRequestProcessingThread.REQUEST_CODE_GET_PRODUCT_IMAGE) {
            Log.i(TAG, "Get image response response : " + response);
            if(response.getResponseStatus() == ServerResponse.SERVER_OK) {
                if (response.getResponse() != null && !response.getResponse().isEmpty()) {
                    ProductImageModel productImageModel = new Gson().fromJson(response.getResponse(),
                            new TypeToken<ProductImageModel>() {
                            }.getType());
                    final String imagePath = productImageModel.getImage().replaceAll("&amp;", "&").replaceAll(" ", "%20");
                    ProductModel productModel = specialProductHashmap.get(String.valueOf(extraRequestCode));
                    if(productModel == null) {
                        return;
                    }
                    productModel.setActualImage(imagePath);
                    updateImagesOfSpecialProductsUI();
                }
            }
            else if(response.getResponseStatus() == ServerResponse.SERVER_ERROR) {
                //TODO: retry or something
            }
            else if(response.getResponseStatus() == ServerResponse.NETWORK_ERROR) {
                //TODO: give retry option
            }
        }
    }

    private void refreshEmptyListStatus() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(getCategoriesStatus == KeyConstants.SERVER_CALL_STATUS_ONGOING) {
                    imgMainCategoryRetry.setVisibility(View.GONE);
                    txtMainCategoryRetry.setVisibility(View.GONE);
                    relRetryCategories.setVisibility(View.GONE);
                }
                else if(getCategoriesStatus == KeyConstants.SERVER_CALL_STATUS_ERROR_OCCURED) {
                    relRetryCategories.setVisibility(View.VISIBLE);
                    imgMainCategoryRetry.setVisibility(View.VISIBLE);
                    txtMainCategoryRetry.setVisibility(View.VISIBLE);
                    txtMainCategoryRetry.setText("Error in fetching categories, Retry..");

                    relRetryCategories.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(AppUtils.isNetworkAvailable(mAppContext)) {
                                rltProgressCategories.setVisibility(View.VISIBLE);
                                getCategoriesStatus = KeyConstants.SERVER_CALL_STATUS_ONGOING;
                                imgMainCategoryRetry.setVisibility(View.GONE);
                                txtMainCategoryRetry.setVisibility(View.GONE);

                                rltProgressCategories.setVisibility(View.VISIBLE);
                                EAApplication.makeServerRequest(ServerRequestProcessingThread.REQUEST_CODE_GET_CATEGORY, GET_CATEGORIES_REQUEST_CODE, EAServerRequest.PRIORITY_HIGH, TAG, String.valueOf(0));
                            }
                            else {
                                getCategoriesStatus = KeyConstants.SERVER_CALL_STATUS_ERROR_OCCURED;

                                refreshEmptyListStatus();
                                Toast.makeText(mAppContext, mAppContext.getString(R.string.no_network_toast), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
                else if(getCategoriesStatus == KeyConstants.SERVER_CALL_STATUS_SUCCESS) {
                    imgMainCategoryRetry.setVisibility(View.GONE);
                    txtMainCategoryRetry.setVisibility(View.GONE);
                    relRetryCategories.setVisibility(View.GONE);
                    txtMainCategoryRetry.setText(mActivityContext.getString(R.string.no_result));
                }

                if(getSPStatus == KeyConstants.SERVER_CALL_STATUS_ONGOING) {
                    imgSpecialProductRetry.setVisibility(View.GONE);
                    txtSpecialProductRetry.setVisibility(View.GONE);
                    relRetrySp.setVisibility(View.GONE);
                }
                else if(getSPStatus == KeyConstants.SERVER_CALL_STATUS_ERROR_OCCURED) {
                    relRetrySp.setVisibility(View.VISIBLE);
                    imgSpecialProductRetry.setVisibility(View.VISIBLE);
                    txtSpecialProductRetry.setVisibility(View.VISIBLE);
                    txtSpecialProductRetry.setText("Error in fetching new arrivals, Retry..");

                    relRetrySp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(AppUtils.isNetworkAvailable(mAppContext)) {
                                rltProgressSP.setVisibility(View.VISIBLE);
                                getSPStatus = KeyConstants.SERVER_CALL_STATUS_ONGOING;
                                imgSpecialProductRetry.setVisibility(View.GONE);
                                txtSpecialProductRetry.setVisibility(View.GONE);
                                relRetrySp.setVisibility(View.GONE);
                                EAApplication.makeServerRequest(ServerRequestProcessingThread.REQUEST_CODE_GET_SPECIAL_PRODUCTS, GET_SPECIAL_PRODUCTS, EAServerRequest.PRIORITY_MEDIUM, TAG);
                            }
                            else {
                                getSPStatus = KeyConstants.SERVER_CALL_STATUS_ERROR_OCCURED;

                                refreshEmptyListStatus();
                                Toast.makeText(mAppContext, mAppContext.getString(R.string.no_network_toast), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
                else if(getSPStatus == KeyConstants.SERVER_CALL_STATUS_SUCCESS) {
                    relRetrySp.setVisibility(View.GONE);
                    imgSpecialProductRetry.setVisibility(View.GONE);
                    txtSpecialProductRetry.setVisibility(View.GONE);
                    txtSpecialProductRetry.setText(mActivityContext.getString(R.string.no_result));
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
                    if(getCategoriesStatus == KeyConstants.SERVER_CALL_STATUS_SUCCESS) {
                        if (masterCategoryList.isEmpty()) {
                            relRetryCategories.setVisibility(View.VISIBLE);
                            txtMainCategoryRetry.setVisibility(View.VISIBLE);
                            gridCategories.setVisibility(View.GONE);
                        } else {
                            relRetryCategories.setVisibility(View.GONE);
                            txtMainCategoryRetry.setVisibility(View.GONE);
                            gridCategories.setVisibility(View.VISIBLE);
                        }
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
                        TextView txtSplProd = (TextView)view.findViewById(R.id.txt_product_name);
                        txtSplProd.setText(productModel.getName().replaceAll("&amp;","&"));
                        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Regular.otf");
                        txtSplProd.setTypeface(tf);
                        Picasso.with(mAppContext).load(productModel.getActualImage())
                                .placeholder(R.drawable.placeholder_product).into((ImageView) view.findViewById(R.id.img_product));
                        linSpecialProducts.addView(view);
                    }

                    if(getSPStatus == KeyConstants.SERVER_CALL_STATUS_SUCCESS) {
                        if (specialProductHashmap.isEmpty()) {
                            relRetrySp.setVisibility(View.VISIBLE);
                            txtSpecialProductRetry.setVisibility(View.VISIBLE);
                            btnNext.setVisibility(View.GONE);
                            btnPrev.setVisibility(View.GONE);
                            linSpecialProducts.setVisibility(View.GONE);
                            btnViewAll.setVisibility(View.GONE);
                        } else {
                            relRetrySp.setVisibility(View.GONE);
                            txtSpecialProductRetry.setVisibility(View.GONE);
                            btnNext.setVisibility(View.GONE);
                            btnPrev.setVisibility(View.GONE);
                            linSpecialProducts.setVisibility(View.VISIBLE);
                            btnViewAll.setVisibility(View.VISIBLE);
                            //timerDelayRunForScroll(500);
                            count = linSpecialProducts.getChildCount();
                            repeatTask.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    if (!canScroll(horizontalScrollView)) {
                                        horizontalScrollView.fullScroll(View.FOCUS_LEFT);
                                    } else {
                                        int widthOfOneElement = linSpecialProducts.getChildAt(0).getMeasuredWidth();
                                        horizontalScrollView.scrollTo((int) horizontalScrollView.getScrollX() + widthOfOneElement, (int) horizontalScrollView.getScrollY());
                                    }

                                }
                            }, 0, repeatInterval);
                        }
                    }
                }
            });
        }
    }

    private boolean canScroll(HorizontalScrollView horizontalScrollView) {
        View child = (View) horizontalScrollView.getChildAt(0);
        if (child != null) {
            int childWidth = (child).getWidth();
            return horizontalScrollView.getWidth() < childWidth + horizontalScrollView.getPaddingLeft() + horizontalScrollView.getPaddingRight();
        }
        return false;

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
        if(dismissLoadingCategories) {
            rltProgressCategories.setVisibility(View.GONE);
            dismissLoadingCategories = false;
        }
        if(dismissLoadingSP) {
            rltProgressSP.setVisibility(View.GONE);
            dismissLoadingSP = false;
        }
        //Reset is downloading status
        for (Map.Entry<String, ProductModel> productModelEntry : specialProductHashmap.entrySet()) {
            productModelEntry.getValue().setLoadingImage(false);
        }
        refreshEmptyListStatus();
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

