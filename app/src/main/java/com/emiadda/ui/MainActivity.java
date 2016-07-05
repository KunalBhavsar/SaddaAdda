package com.emiadda.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.emiadda.R;
import com.emiadda.adapters.CategoryAdapter;
import com.emiadda.adapters.ProductAdapter;
import com.emiadda.core.EACategory;
import com.emiadda.asynctasks.GetCategoriesAsync;
import com.emiadda.interafaces.ServerResponseInterface;

import com.emiadda.utils.KeyConstants;
import com.emiadda.wsdl.CategoryModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity
        implements NavigationView.OnNavigationItemSelectedListener, ServerResponseInterface {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GET_CATEGORIES_REQUEST_CODE = 12;

    ProductAdapter productAdapter;
    ProgressDialog progressDialog;
    Activity mActivityContext;

    private GridView gridCategories;
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivityContext = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        gridCategories = (GridView) findViewById(R.id.grd_categories);
        categoryAdapter = new CategoryAdapter(this);
        gridCategories.setAdapter(categoryAdapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);

        progressDialog.show();

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
        new GetCategoriesAsync(this, GET_CATEGORIES_REQUEST_CODE).execute(String.valueOf(0));
    }

    public String loadProducts(int option) {

        InputStream inputStream = null;
        switch (option) {
            case 1:
                inputStream = getResources().openRawResource(R.raw.master);
                break;
            case 2:
                inputStream = getResources().openRawResource(R.raw.fashion);
                break;
            case 3:
                inputStream = getResources().openRawResource(R.raw.electronics);
                break;
            case 4:
                inputStream = getResources().openRawResource(R.raw.home);
                break;
            case 5:
                inputStream = getResources().openRawResource(R.raw.wellness);
                break;

        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int ctr;
        try {
            ctr = inputStream.read();
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toString();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

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
        Log.i(TAG, "Response of get categories : " + response);
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
                    }
                }
            } else if (responseCode == ServerResponseInterface.RESPONSE_CODE_EXCEPTION) {
                Toast.makeText(mActivityContext, "Error in fetching categories", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

