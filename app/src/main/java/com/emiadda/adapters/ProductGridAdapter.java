package com.emiadda.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.emiadda.R;
import com.emiadda.core.EAProduct;
import com.emiadda.wsdl.ProductModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shraddha on 16/3/16.
 */
public class ProductGridAdapter extends RecyclerView.Adapter<ProductGridAdapter.ViewHolder> {

    private static final String TAG = ProductGridAdapter.class.getSimpleName();
    private Context context;
    private List<ProductModel> productList;

    public ProductGridAdapter(Activity context) {
        this.context = context;
        productList = new ArrayList<>();
    }

    public void setProducts(List<ProductModel> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_elememt_product, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ProductModel productModel = productList.get(position);
        holder.txtProductName.setText(productModel.getName());
        holder.txtPrice.setText("Rs." + productModel.getPrice());

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtProductName, txtPrice, txtNew;
        public ImageView imgProduct, imgFav;

        public ViewHolder(View v) {
            super(v);
            imgProduct = (ImageView) v.findViewById(R.id.image);
            imgFav = (ImageView) v.findViewById(R.id.image_fav);
            txtProductName = (TextView) v.findViewById(R.id.txt_brand);
            txtPrice = (TextView) v.findViewById(R.id.txt_price);
            txtNew = (TextView) v.findViewById(R.id.txt_new);
        }
    }

}
