package com.emiadda.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.emiadda.R;
import com.emiadda.adapters.SubCategoryAdapter;
import com.emiadda.asynctasks.GetCategoriesAsync;
import com.emiadda.asynctasks.GetProductByProductId;
import com.emiadda.asynctasks.GetProductImageAsync;
import com.emiadda.asynctasks.GetProductsByCategory;
import com.emiadda.core.EACategory;
import com.emiadda.interafaces.ServerResponseInterface;
import com.emiadda.utils.KeyConstants;
import com.emiadda.wsdl.CategoryModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class SubCategoryActivity extends AppCompatActivity implements ServerResponseInterface {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GET_CATEGORIES_REQUEST_CODE = 12;

    ProgressDialog progressDialog;
    Activity mActivityContext;

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private SubCategoryAdapter subCategoryAdapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);
        mActivityContext = this;

        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
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
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(subCategoryAdapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ImageView imgCart = (ImageView) findViewById(R.id.img_cart);
        assert imgCart != null;
        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivityContext, CartActivity.class);
                startActivity(intent);
            }
        });

        long categoryId = getIntent().getLongExtra(KeyConstants.INTENT_CONSTANT_CATEGORY_ID, 0);
        new GetCategoriesAsync(this, GET_CATEGORIES_REQUEST_CODE).execute(String.valueOf(categoryId));
    }

    @Override
    public void responseReceived(String response, int requestCode, int responseCode) {
        Log.i(TAG, "Response of get categories : " + response);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (requestCode == GET_CATEGORIES_REQUEST_CODE) {
            if (responseCode == ServerResponseInterface.RESPONSE_CODE_OK) {
                Log.i(TAG, "received response : " + response);
                if(!response.isEmpty()) {
                    List<CategoryModel> categoryModelList = new Gson().fromJson(response, new TypeToken<ArrayList<CategoryModel>>() {
                    }.getType());
                    for (CategoryModel categoryModel : categoryModelList) {
                        if (subCategoryAdapter != null) {
                            EACategory eaCategory = new EACategory();
                            eaCategory.setId(Long.parseLong(categoryModel.getCategory_id()));
                            eaCategory.setCategoryName(categoryModel.getCategory_name());
                            eaCategory.setCategoryImage(categoryModel.getCategory_image());
                            subCategoryAdapter.addCategory(eaCategory);
                        }
                    }
                }
            } else if (responseCode == ServerResponseInterface.RESPONSE_CODE_EXCEPTION) {
                Toast.makeText(mActivityContext, "Error in fetching categories", Toast.LENGTH_SHORT).show();
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
