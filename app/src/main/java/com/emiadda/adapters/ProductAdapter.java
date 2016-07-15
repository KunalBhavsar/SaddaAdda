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

import com.emiadda.core.EAProduct;

import java.io.File;
import java.util.List;

import com.emiadda.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Kunal Bhavsar on 16/3/16.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private static final String TAG = ProductAdapter.class.getSimpleName();
    private List<EAProduct> eaProducts;
    private Context context;

    public ProductAdapter(List<EAProduct> eaProducts, Activity context) {
        this.eaProducts = eaProducts;
        this.context = context;
    }

    public void setProducts(List<EAProduct> eaProducts) {
        this.eaProducts = eaProducts;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_recycler_view, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EAProduct eaProduct = eaProducts.get(position);
        holder.txtProductName.setText(eaProduct.getProductName());
        Picasso.with(context)
                .load(!eaProduct.getImageUrls().isEmpty() ? eaProduct.getImageUrls().get(0) : "")
                .placeholder(R.drawable.placeholder_product)
                .into(holder.imgProduct);
    }

    @Override
    public int getItemCount() {
        return eaProducts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtProductName;
        public ImageView imgProduct;

        public ViewHolder(View v) {
            super(v);
            imgProduct = (ImageView) v.findViewById(R.id.img_product_img);
            txtProductName = (TextView) v.findViewById(R.id.txt_product_name);
        }
    }

}
