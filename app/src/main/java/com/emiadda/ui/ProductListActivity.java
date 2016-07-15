package com.emiadda.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
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
import android.widget.TextView;
import android.widget.Toast;

import com.emiadda.R;
import com.emiadda.adapters.ProductGridAdapter;
import com.emiadda.asynctasks.GetCategoriesAsync;
import com.emiadda.asynctasks.GetProductsByCategory;
import com.emiadda.core.EACategory;
import com.emiadda.interafaces.ServerResponseInterface;
import com.emiadda.utils.AppPreferences;
import com.emiadda.utils.KeyConstants;
import com.emiadda.wsdl.CategoryModel;
import com.emiadda.wsdl.ProductModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ProductListActivity extends AppCompatActivity implements ServerResponseInterface {

    private static final int GET_PRODUCT_REQUEST_CODE = 17;
    private static final String TAG = ProductListActivity.class.getSimpleName();

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private ProductGridAdapter productGridAdapter;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;
    private Activity mActivityContext;
    private EditText edtSearch;
    private List<ProductModel> masterList;
    private LinearLayout lnrSort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        lnrSort = (LinearLayout) findViewById(R.id.lnr_sort);
        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mActivityContext = this;

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GridSpaceItemDecoration(10));
        productGridAdapter = new ProductGridAdapter(mActivityContext, new ProductGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ProductModel item) {
                Intent intent = new Intent(mActivityContext, ProductDetailActivity.class);
                intent.putExtra(KeyConstants.INTENT_CONSTANT_PRODUCT_ID, item.getProduct_id());
                mActivityContext.startActivity(intent);
            }
        });

        ImageView imgCart = (ImageView) findViewById(R.id.img_cart);
        assert imgCart != null;
        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivityContext, CartActivity.class);
                startActivity(intent);
            }
        });

        layoutManager = new GridLayoutManager(ProductListActivity.this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(productGridAdapter);

        ((TextView)findViewById(R.id.txt_category_name)).setText(getIntent().getStringExtra(KeyConstants.INTENT_CONSTANT_SUB_CATEGORY_NAME));
        progressDialog.show();
        long categoryId = getIntent().getLongExtra(KeyConstants.INTENT_CONSTANT_SUB_CATEGORY_ID, 0);
        new GetProductsByCategory(this, GET_PRODUCT_REQUEST_CODE).execute(String.valueOf(categoryId));

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
                if(masterList == null) {
                    return;
                }
                String query = s.toString().trim().toLowerCase();
                if (!query.isEmpty()) {
                    List<ProductModel> categoryModels = new ArrayList<>();
                    for (ProductModel categoryModel : masterList) {
                        if(categoryModel.getName().toLowerCase().contains(query)) {
                            categoryModels.add(categoryModel);
                        }
                    }
                    productGridAdapter.setProducts(categoryModels);
                } else {
                    productGridAdapter.setProducts(masterList);
                }
            }
        });

        lnrSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] languages = {"Name","Price"};
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivityContext);
                dialogBuilder.setTitle("Select crop type");
                dialogBuilder.setItems(languages,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(masterList != null && !masterList.isEmpty()) {
                            if(i == 0) {
                                Collections.sort(masterList, new ProductNameComparator());
                            } else {
                                Collections.sort(masterList, new ProductPriceComparator());
                            }
                            productGridAdapter.notifyDataSetChanged();
                        }
                    }
                });
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
            }
        });

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

            if(lhsPrice < rhsPrice) {
                return -1;
            } else if(lhsPrice > rhsPrice) {
                return 1;
            }
            return 0;
        }
    }

    @Override
    public void responseReceived(String response, int requestCode, int responseCode) {
        if(progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        if (responseCode == ServerResponseInterface.RESPONSE_CODE_OK) {
            Log.i(TAG, "received response : " + response);
            if(response != null && !response.isEmpty()) {
                try {
                    HashMap<String, ProductModel> map = new Gson().fromJson(response, new TypeToken<HashMap<String, ProductModel>>() {}.getType());
                    if(map != null) {
                        List<ProductModel> productModelList = new ArrayList<>();
                        Set<String> set = map.keySet();
                        for (String key : set) {
                            productModelList.add(map.get(key));
                            masterList = productModelList;
                        }
                        productGridAdapter.setProducts(productModelList);
                    }
                }catch (Exception e) {
                 Log.e(TAG, e.getMessage(), e);
                }
            }
        } else if (responseCode == ServerResponseInterface.RESPONSE_CODE_EXCEPTION) {
            Toast.makeText(mActivityContext, "Error in fetching categories", Toast.LENGTH_SHORT).show();
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
