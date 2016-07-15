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
public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.ViewHolder> {

    private static final String TAG = SubCategoryAdapter.class.getSimpleName();
    private Context context;
    public List<EACategory> categories;
    private OnItemClickListener mItemClickListener;

    public SubCategoryAdapter(Activity context, OnItemClickListener listener) {
        this.context = context;
        categories = new ArrayList<>();
        mItemClickListener = listener;
    }

    public void addCategory(EACategory eaCategory) {
        if(eaCategory != null) {
            categories.add(eaCategory);
            notifyDataSetChanged();
        }
    }

    public void addCategories(List<EACategory> eaCategories) {
        if(eaCategories != null) {
            categories.clear();
            this.categories.addAll(eaCategories);
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sub_category, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EACategory eaCategory = categories.get(position);
        holder.bind(eaCategory, mItemClickListener);
        holder.txtName.setText(eaCategory.getCategoryName().replaceAll("&amp;","&"));
        //set image logic
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName;
        public ImageView imgCat;

        public ViewHolder(View v) {
            super(v);
            txtName = (TextView) v.findViewById(R.id.txt_category);
            imgCat = (ImageView) v.findViewById(R.id.img_category);
        }

        public void bind(final EACategory item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(EACategory item);
    }

}
