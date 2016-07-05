package com.emiadda.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.emiadda.R;
import com.emiadda.core.EAProduct;

import java.util.List;

/**
 * Created by Shraddha on 16/3/16.
 */
public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.ViewHolder> {

    private static final String TAG = SubCategoryAdapter.class.getSimpleName();
    private Context context;

    public SubCategoryAdapter(Activity context) {
        this.context = context;
    }

    public void setProducts(List<EAProduct> eaProducts) {
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sub_category, parent, false);
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
        public TextView txtName;
        public ImageView imgCat;

        public ViewHolder(View v) {
            super(v);
            txtName = (TextView) v.findViewById(R.id.txt_category);
            imgCat = (ImageView) v.findViewById(R.id.img_category);
        }
    }

}
