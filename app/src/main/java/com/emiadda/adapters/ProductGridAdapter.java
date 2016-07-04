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
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Shraddha on 16/3/16.
 */
public class ProductGridAdapter extends RecyclerView.Adapter<ProductGridAdapter.ViewHolder> {

    private static final String TAG = ProductGridAdapter.class.getSimpleName();
    private Context context;

    public ProductGridAdapter(Activity context) {
        this.context = context;
    }

    public void setProducts(List<EAProduct> eaProducts) {
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

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtProductName;
        public ImageView imgProduct;

        public ViewHolder(View v) {
            super(v);
            imgProduct = (ImageView) v.findViewById(R.id.image);
            txtProductName = (TextView) v.findViewById(R.id.txt_brand);
        }
    }

}
