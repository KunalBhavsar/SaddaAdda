package com.emiadda.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.emiadda.R;
import com.emiadda.asynctasks.GetProductImageAsync;
import com.emiadda.core.EACategory;
import com.emiadda.interafaces.ServerResponseInterface;
import com.emiadda.wsdl.ProductImageModel;
import com.emiadda.wsdl.ProductModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shraddha on 16/3/16.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> implements ServerResponseInterface {

    private static final String TAG = CartAdapter.class.getSimpleName();
    private Context context;
    public List<ProductModel> cartList;

    public CartAdapter(Activity context) {
        this.context = context;
        cartList = new ArrayList<>();
    }

    public void addProduct(List<ProductModel> productModelList) {
        if(productModelList != null) {
            cartList = productModelList;
            notifyDataSetChanged();
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //set image logic
        ProductModel productModel = cartList.get(position);
        holder.txtBrandName.setText(productModel.getMeta_title());
        holder.txtAmount.setText(productModel.getPrice());
       if((productModel.getImage() == null || productModel.getImage().isEmpty()) && !productModel.isLoadingImage()) {
            productModel.setLoadingImage(true);
            new GetProductImageAsync(null, Integer.parseInt(productModel.getProduct_id())).execute(productModel.getProduct_id());
        }
        else {
            Picasso.with(context).load(productModel.getImage()).fit().into(holder.imgCat);
        }
    }

    @Override
    public int getItemCount() {
        if(cartList == null) {
            return 0;
        }
        return cartList.size();
    }

    @Override
    public void responseReceived(String response, int requestCode, int responseCode) {
        if(responseCode == ServerResponseInterface.RESPONSE_CODE_OK) {
            for (ProductModel product : cartList) {
                if(product.getProduct_id().equals(String.valueOf(requestCode))) {
                    try {
                        ProductImageModel productImageModel = new Gson().fromJson(response, new TypeToken<ProductImageModel>() {
                        }.getType());
                        if(productImageModel != null) {
                            product.setImage(productImageModel.getImage().replaceAll("&amp;", "&").replaceAll(" ","%20"));
                            notifyDataSetChanged();
                        }
                    }catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtBrandName, txtSize, txtQunt, txtAmount;
        public ImageView imgCat;
        public Button btnSave, btnRemove, btnEdit;

        public ViewHolder(View v) {
            super(v);
            txtBrandName = (TextView) v.findViewById(R.id.txt_brand_name_value);
            txtSize = (TextView) v.findViewById(R.id.txt_size_value);
            txtQunt = (TextView) v.findViewById(R.id.txt_qun_value);
            txtAmount = (TextView) v.findViewById(R.id.txt_amount_value);
            imgCat = (ImageView) v.findViewById(R.id.img_cart);
            btnSave = (Button) v.findViewById(R.id.btn_save);
            btnRemove = (Button) v.findViewById(R.id.btn_remove);
            btnEdit = (Button) v.findViewById(R.id.btn_edit);

            btnSave.setOnClickListener(this);
            btnRemove.setOnClickListener(this);
            btnEdit.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_save:

                    break;

                case R.id.btn_remove:

                    break;

                case R.id.btn_edit:

                    break;
            }
        }
    }
}
