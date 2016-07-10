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
import com.emiadda.core.EACategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shraddha on 16/3/16.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private static final String TAG = CartAdapter.class.getSimpleName();
    private Context context;
    public List<EACategory> categories;

    public CartAdapter(Activity context) {
        this.context = context;
        categories = new ArrayList<>();
    }

    public void addCategory(EACategory eaCategory) {
        if(eaCategory != null) {
            categories.add(eaCategory);
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
    }

    @Override
    public int getItemCount() {
        return 7;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName;
        public ImageView imgCat;

        public ViewHolder(View v) {
            super(v);
            txtName = (TextView) v.findViewById(R.id.txt_brand_name);
            imgCat = (ImageView) v.findViewById(R.id.img_cart);
        }

    }

}
