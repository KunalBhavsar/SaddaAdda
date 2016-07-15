package com.emiadda.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.emiadda.R;
import com.emiadda.adapters.CartAdapter;
import com.emiadda.adapters.ProductGridAdapter;
import com.emiadda.utils.AppPreferences;
import com.emiadda.wsdl.ProductModel;

import java.util.List;

public class CartActivity extends AppCompatActivity {


    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private CartAdapter cartAdapter;
    private Button btnPlaceOrder;
    private double subTotal;
    private double deliveryCharges;
    private double taxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        btnPlaceOrder = (Button) findViewById(R.id.btn_place_order);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        cartAdapter = new CartAdapter(this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cartAdapter);

        List<ProductModel> productModelList = AppPreferences.getInstance().getCartList();
        cartAdapter.addProduct(productModelList);

        for (ProductModel productModel : productModelList) {
            subTotal = productModel.getNumberOfSeletedItems() * Double.parseDouble(productModel.getPrice());
        }

        deliveryCharges = 100.00;
        taxes = subTotal / 10;

        ((TextView)findViewById(R.id.txt_sub_total)).setText(String.valueOf(subTotal));
        ((TextView)findViewById(R.id.txt_delivery_charges)).setText(String.valueOf(deliveryCharges));
        ((TextView)findViewById(R.id.txt_taxes)).setText(String.valueOf(taxes));
        ((TextView)findViewById(R.id.txt_total)).setText(String.valueOf(subTotal + deliveryCharges + taxes));

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, MakePaymentActivity.class);
                startActivity(intent);
            }
        });
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
