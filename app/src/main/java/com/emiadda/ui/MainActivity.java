package com.emiadda.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.emiadda.R;
import com.emiadda.adapters.CategoryAdapter;
import com.emiadda.adapters.DrawerAdapter;
import com.emiadda.asynctasks.GetProductImageAsync;
import com.emiadda.asynctasks.GetSpecialsAsync;
import com.emiadda.core.EACategory;
import com.emiadda.asynctasks.GetCategoriesAsync;
import com.emiadda.interafaces.ServerResponseInterface;

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
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ServerResponseInterface {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GET_CATEGORIES_REQUEST_CODE = 12;
    private static final int GET_SPECIAL_PRODUCTS = 13;

    ProgressDialog progressDialog;
    Activity mActivityContext;
    private NavigationView navigationView;
    private RecyclerView recyclerView;
    private DrawerAdapter drawerAdapter;
    private  DrawerLayout drawer;

    private GridView gridCategories;
    private CategoryAdapter categoryAdapter;

    private LinearLayout linSpecialProducts;
    private HashMap<String, ProductModel> specialProductHashmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivityContext = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();

        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.menu_layout, null);
        //actionBar.setCustomView(mCustomView);
        //actionBar.setDisplayShowCustomEnabled(true);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        drawerAdapter = new DrawerAdapter(this);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if(navigationView != null) {
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

            recyclerView = (RecyclerView) headerView.findViewById(R.id.recycler_view);
            headerView.findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppPreferences.getInstance().setUserLoggegIn(false);
                    AppPreferences.getInstance().remove(AppPreferences.APP_OWNER_DATA);
                    Intent intent = new Intent(mActivityContext, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                    mActivityContext.startActivity(intent);
                    finish();
                }
            });
            navigationView.setNavigationItemSelectedListener(this);
        }
        linSpecialProducts = (LinearLayout)findViewById(R.id.lin_special_product_list);
        gridCategories = (GridView) findViewById(R.id.grd_categories);
        categoryAdapter = new CategoryAdapter(this);
        gridCategories.setAdapter(categoryAdapter);

        ImageView imgCart = (ImageView) findViewById(R.id.img_cart);
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
                if(categoryAdapter != null) {
                    EACategory eaCategory = (EACategory) categoryAdapter.getItem(position);
                    Intent intent = new Intent(MainActivity.this, SubCategoryActivity.class);
                    intent.putExtra(KeyConstants.INTENT_CONSTANT_CATEGORY_ID, eaCategory.getId());
                    startActivity(intent);
                }
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);

        progressDialog.show();

        new GetCategoriesAsync(this, GET_CATEGORIES_REQUEST_CODE).execute(String.valueOf(0));
        new GetSpecialsAsync(this, GET_SPECIAL_PRODUCTS).execute();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_men) {
            Intent intent = new Intent(MainActivity.this, ProductListActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_kids) {
            Intent intent = new Intent(MainActivity.this, SubCategoryActivity.class);
            startActivity(intent);


        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void responseReceived(String response, int requestCode, int responseCode) {
        if (requestCode == GET_CATEGORIES_REQUEST_CODE) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (responseCode == ServerResponseInterface.RESPONSE_CODE_OK) {
                Log.i(TAG, "received response : " + response);
                List<CategoryModel> categoryModelList = new Gson().fromJson(response, new TypeToken<ArrayList<CategoryModel>>() {
                }.getType());
                for (CategoryModel categoryModel : categoryModelList) {
                    if (categoryAdapter != null) {
                        EACategory eaCategory = new EACategory();
                        eaCategory.setId(Long.parseLong(categoryModel.getCategory_id()));
                        eaCategory.setCategoryName(categoryModel.getCategory_name());
                        eaCategory.setCategoryImage(categoryModel.getCategory_image());
                        categoryAdapter.addCategory(eaCategory);
                        drawerAdapter.addCategory(eaCategory);
                    }
                }

                recyclerView.setLayoutManager(new LinearLayoutManager(mActivityContext));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(drawerAdapter);

            } else if (responseCode == ServerResponseInterface.RESPONSE_CODE_EXCEPTION) {
                Toast.makeText(mActivityContext, "Error in fetching categories", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == GET_SPECIAL_PRODUCTS) {
            Log.i(TAG, "Special products response : "+response);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (responseCode == ServerResponseInterface.RESPONSE_CODE_OK) {
                Log.i(TAG, "received response : " + response);
                if(!response.isEmpty()) {
                    try {
                        specialProductHashmap = new Gson().fromJson(response, new TypeToken<HashMap<String, ProductModel>>() {}.getType());

                        for (Map.Entry<String, ProductModel> specialProductModelEntry : specialProductHashmap.entrySet()) {
                            View v = LayoutInflater.from(mActivityContext)
                                    .inflate(R.layout.item_special_product, null, false);
                            ((ImageView)v.findViewById(R.id.img_product)).setImageBitmap(null);
                            ((TextView)v.findViewById(R.id.txt_product_name)).setText(specialProductModelEntry.getValue().getName());
                            v.setTag(specialProductModelEntry.getKey());

                            new GetProductImageAsync(this, Integer.parseInt(specialProductModelEntry.getKey())).execute(specialProductModelEntry.getValue().getProduct_id());

                            linSpecialProducts.addView(v);
                        }
                    }catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            } else if (responseCode == ServerResponseInterface.RESPONSE_CODE_EXCEPTION) {
                Toast.makeText(mActivityContext, "Error in fetching categories", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            if (responseCode == ServerResponseInterface.RESPONSE_CODE_OK) {
                Log.i(TAG, "received response : " + response);
                for (int i = 0; i < linSpecialProducts.getChildCount(); i++) {
                    if(((String)linSpecialProducts.getChildAt(i).getTag()).equals(String.valueOf(requestCode))) {
                        try {
                            ProductImageModel productImageModel = new Gson().fromJson(response, new TypeToken<ProductImageModel>() {
                            }.getType());
                            if(productImageModel != null) {
                                Picasso.with(mActivityContext).load(productImageModel.getImage().replaceAll("&amp;", "&")
                                        .replaceAll(" ","%20")).fit().into((ImageView)linSpecialProducts.getChildAt(i).findViewById(R.id.img_product));
                            }
                        }catch (Exception e) {
                            Log.e(TAG, e.getMessage(), e);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onPause() {
        drawer.closeDrawers();
        super.onPause();
    }
}

