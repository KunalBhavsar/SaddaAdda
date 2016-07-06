package com.emiadda.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.emiadda.R;
import com.emiadda.interafaces.ServerResponseInterface;

public class ProductListActivity extends AppCompatActivity implements ServerResponseInterface {

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
//    private ProductGridAdapter productGridAdapter;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;
    private Activity mActivityContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GridSpaceItemDecoration(10));
  //      productGridAdapter = new ProductGridAdapter(this);
        layoutManager = new GridLayoutManager(ProductListActivity.this, 2);
        recyclerView.setLayoutManager(layoutManager);
  //      recyclerView.setAdapter(productGridAdapter);
    }

    @Override
    public void responseReceived(String response, int requestCode, int responseCode) {
        progressDialog.dismiss();
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
}
