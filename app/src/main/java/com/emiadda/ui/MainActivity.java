package com.emiadda.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class MainActivity extends AppCompatActivity
        implements ServerResponseSubscriber {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GET_CATEGORIES_REQUEST_CODE = 12;
    private static final int GET_SPECIAL_PRODUCTS = 13;

    private ProgressDialog progressDialog;
    private Activity mActivityContext;
    private Context mAppContext;

    private NavigationView navigationView;
    private RecyclerView drawerRecyclerView;
    private DrawerAdapter drawerAdapter;
    private DrawerLayout drawer;
    private EditText edtSearch;

    private GridView gridCategories;
    private CategoryAdapter categoryAdapter;
    private List<EACategory> masterList;

    private LinearLayout linSpecialProducts;
    private HashMap<String, ProductModel> specialProductHashmap;
    private boolean inForeground = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivityContext = this;
        mAppContext = getApplicationContext();

        edtSearch = (EditText) findViewById(R.id.edt_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        linSpecialProducts = (LinearLayout) findViewById(R.id.lin_special_product_list);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        gridCategories = (GridView) findViewById(R.id.grd_categories);
        ImageView imgCart = (ImageView) findViewById(R.id.img_cart);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        if (navigationView != null) {
            View headerView = navigationView.getHeaderView(0);
            CustomerModel appOwner = AppPreferences.getInstance().getAppOwnerData();
            TextView txtName = (TextView) headerView.findViewById(R.id.txt_name);
            txtName.setText(appOwner.getFirstname() + " " + appOwner.getLastname());

            TextView txtEmail = (TextView) headerView.findViewById(R.id.txt_email);
            txtEmail.setText(appOwner.getEmail());

            TextView txtHome = (TextView) headerView.findViewById(R.id.txt_home);
            txtHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawer.closeDrawers();
                }
            });

            drawerRecyclerView = (RecyclerView) headerView.findViewById(R.id.recycler_view);
            headerView.findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppPreferences.getInstance().setUserLoggegIn(false);
                    AppPreferences.getInstance().remove(AppPreferences.APP_OWNER_DATA);
                    Intent intent = new Intent(mActivityContext, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    mActivityContext.startActivity(intent);
                    finish();
                }
            });
        }

        progressDialog = new ProgressDialog(this);
        masterList = new ArrayList<>();
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

        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);

        progressDialog.show();
        ((EAApplication) mAppContext).attach(this);
        EAApplication.makeServerRequest(ServerRequestProcessingThread.REQUEST_CODE_GET_CATEGORY, GET_CATEGORIES_REQUEST_CODE, EAServerRequest.PRIORITY_HIGH, String.valueOf(0));
        EAApplication.makeServerRequest(ServerRequestProcessingThread.REQUEST_CODE_GET_SPECIAL_PRODUCTS, GET_SPECIAL_PRODUCTS, EAServerRequest.PRIORITY_MEDIUM);
    }

    @Override
    protected void onStop() {
        ((EAApplication) mAppContext).dettach(this);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
    public void responseReceived(final String response, final int requestCode, final int responseCode, final int extraRequestCode) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (responseCode == ServerResponseSubscriber.RESPONSE_CODE_OK) {
            if (requestCode == ServerRequestProcessingThread.REQUEST_CODE_GET_CATEGORY
                    && extraRequestCode == GET_CATEGORIES_REQUEST_CODE) {
                Log.i(TAG, "Categories response : " + response);
                List<CategoryModel> categoryModelList = new Gson().fromJson(response,
                        new TypeToken<ArrayList<CategoryModel>>() {}.getType());
                processGetCategoriesResponse(categoryModelList);
                refreshCatergoryUI();
            } else if (requestCode == ServerRequestProcessingThread.REQUEST_CODE_GET_SPECIAL_PRODUCTS
                    && extraRequestCode == GET_SPECIAL_PRODUCTS) {
                Log.i(TAG, "Special products response : " + response);
                if (!response.isEmpty()) {
                    specialProductHashmap = new Gson().fromJson(response,
                            new TypeToken<HashMap<String, ProductModel>>() {}.getType());
                    refreshSpecialProductUI();
                }
            } else if (requestCode == ServerRequestProcessingThread.REQUEST_CODE_GET_PRODUCT_IMAGE) {
                Log.i(TAG, "Get image response response : " + response);
                ProductImageModel productImageModel = new Gson().fromJson(response,
                        new TypeToken<ProductImageModel>() {}.getType());
                final String imagePath = productImageModel.getImage().replaceAll("&amp;", "&").replaceAll(" ", "%20");
                specialProductHashmap.get(String.valueOf(extraRequestCode)).setActualImage(imagePath);
                updateImagesOfSpecialProductsUI();
            }
        }
        else if (responseCode == ServerResponseSubscriber.RESPONSE_CODE_EXCEPTION) {
            Toast.makeText(mActivityContext, "Error in fetching categories", Toast.LENGTH_SHORT).show();
        }
    }

    private void processGetCategoriesResponse(List<CategoryModel> categoryModelList) {
        masterList.clear();
        for (CategoryModel categoryModel : categoryModelList) {
            EACategory eaCategory = new EACategory();
            eaCategory.setId(Long.parseLong(categoryModel.getCategory_id()));
            eaCategory.setCategoryName(categoryModel.getCategory_name());
            eaCategory.setCategoryImage(categoryModel.getCategory_image());
            masterList.add(eaCategory);
        }
        refreshCatergoryUI();
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
                    categoryAdapter.resetCategories(masterList);
                    if (drawerAdapter == null) {
                        drawerAdapter = new DrawerAdapter(mActivityContext);
                        if (drawerRecyclerView != null) {
                            drawerRecyclerView.setLayoutManager(new LinearLayoutManager(mActivityContext));
                            drawerRecyclerView.setHasFixedSize(true);
                            drawerRecyclerView.setAdapter(drawerAdapter);
                        }
                    }
                    drawerAdapter.resetCategories(masterList);
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
                        if((productModel.getActualImage() == null || productModel.getActualImage().isEmpty())
                                && !productModel.isLoadingImage()) {
                            productModel.setLoadingImage(true);
                            EAApplication.makeServerRequest(ServerRequestProcessingThread.REQUEST_CODE_GET_PRODUCT_IMAGE,
                                    Integer.parseInt(specialProductModelEntry.getKey()), EAServerRequest.PRIORITY_LOW, productModel.getProduct_id());
                        }
                        ((TextView)view.findViewById(R.id.txt_product_name)).setText(productModel.getName().replaceAll("&amp;","&"));
                        Picasso.with(mAppContext).load(productModel.getActualImage())
                                .placeholder(R.drawable.placeholder_product).into((ImageView) view.findViewById(R.id.img_product));
                        linSpecialProducts.addView(view);
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
                    for (int i = 0; i < linSpecialProducts.getChildCount(); i++) {
                        View view = linSpecialProducts.getChildAt(i);
                        String specialProductId = (String) view.getTag();
                        ProductModel specialProduct = specialProductHashmap.get(specialProductId);
                        if((specialProduct.getActualImage() == null
                                || specialProduct.getActualImage().isEmpty())
                                && !specialProduct.isLoadingImage()) {
                            specialProduct.setLoadingImage(true);
                            EAApplication.makeServerRequest(ServerRequestProcessingThread.REQUEST_CODE_GET_PRODUCT_IMAGE,
                                    Integer.parseInt(specialProductId), EAServerRequest.PRIORITY_LOW, specialProduct.getProduct_id());
                        }
                        Picasso.with(mAppContext).load(specialProductHashmap.get(specialProductId).getActualImage())
                                .placeholder(R.drawable.placeholder_product).into((ImageView)view.findViewById(R.id.img_product));
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        inForeground = true;
        refreshCatergoryUI();
        refreshSpecialProductUI();
        updateImagesOfSpecialProductsUI();
    }

    @Override
    protected void onPause() {
        inForeground = false;
        drawer.closeDrawers();
        super.onPause();
    }
}

