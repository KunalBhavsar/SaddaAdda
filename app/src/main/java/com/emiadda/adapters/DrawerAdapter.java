package com.emiadda.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.emiadda.R;
import com.emiadda.core.EACategory;
import com.emiadda.ui.MainActivity;
import com.emiadda.ui.SubCategoryActivity;
import com.emiadda.utils.KeyConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shraddha on 7/7/16.
 */
public class DrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_FOOTER = 0;
    private static final int TYPE_ITEM = 1;

    private List<EACategory> categoryList;
    private Context context;

    public DrawerAdapter(Context context) {
        categoryList = new ArrayList<>();
        this.context = context;
    }

    public void addCategory(EACategory eaCategory) {
        if(eaCategory != null) {
            categoryList.add(eaCategory);
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case TYPE_FOOTER:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_footer, parent, false);
                return new FooterHolder(v);

            case TYPE_ITEM:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drawer, parent, false);
                return new ListHolder(v);

        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == categoryList.size()) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //holder.txtCat.setText(categoryList.get(position).getCategoryName());
        if (holder instanceof ListHolder) {
            ListHolder viewHolder = (ListHolder) holder;
            viewHolder.txtCat.setText(categoryList.get(position).getCategoryName().replaceAll("&amp;","&").toUpperCase());

            String catName = categoryList.get(position).getCategoryName().toLowerCase();
            //Set category image logic
            if(catName.equalsIgnoreCase("women")) {
                Drawable drawable = context.getResources().getDrawable(context.getResources().getIdentifier("nav_women", "drawable", context.getPackageName()));
                viewHolder.imgCategory.setImageDrawable(drawable);
            }else if(catName.equalsIgnoreCase("men")) {
                Drawable drawable = context.getResources().getDrawable(context.getResources().getIdentifier("nav_men", "drawable", context.getPackageName()));
                viewHolder.imgCategory.setImageDrawable(drawable);
            }else if(catName.equalsIgnoreCase("fashion")) {
                Drawable drawable = context.getResources().getDrawable(context.getResources().getIdentifier("nav_fashion", "drawable", context.getPackageName()));
                viewHolder.imgCategory.setImageDrawable(drawable);
            }else if(catName.contains("jew")) {
                Drawable drawable = context.getResources().getDrawable(context.getResources().getIdentifier("nav_jew", "drawable", context.getPackageName()));
                viewHolder.imgCategory.setImageDrawable(drawable);
            }else if(catName.contains("health")) {
                Drawable drawable = context.getResources().getDrawable(context.getResources().getIdentifier("nav_health", "drawable", context.getPackageName()));
                viewHolder.imgCategory.setImageDrawable(drawable);
            }else if(catName.contains("home")) {
                Drawable drawable = context.getResources().getDrawable(context.getResources().getIdentifier("nav_home_living", "drawable", context.getPackageName()));
                viewHolder.imgCategory.setImageDrawable(drawable);
            }else if(catName.contains("hair")) {
                Drawable drawable = context.getResources().getDrawable(context.getResources().getIdentifier("nav_hair", "drawable", context.getPackageName()));
                viewHolder.imgCategory.setImageDrawable(drawable);
            }else if(catName.contains("food")) {
                Drawable drawable = context.getResources().getDrawable(context.getResources().getIdentifier("nav_food", "drawable", context.getPackageName()));
                viewHolder.imgCategory.setImageDrawable(drawable);
            }else if(catName.contains("hit")) {
                Drawable drawable = context.getResources().getDrawable(context.getResources().getIdentifier("nav_elec", "drawable", context.getPackageName()));
                viewHolder.imgCategory.setImageDrawable(drawable);
            }
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size() + 1;
    }

    class ListHolder extends RecyclerView.ViewHolder {

        private TextView txtCat;
        private ImageView imgCategory;

        public ListHolder(View itemView) {
            super(itemView);
            txtCat = (TextView) itemView.findViewById(R.id.txt_category);
            imgCategory = (ImageView) itemView.findViewById(R.id.nav_cat);
            txtCat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EACategory eaCategory = categoryList.get(getAdapterPosition());
                    Intent intent = new Intent(context, SubCategoryActivity.class);
                    intent.putExtra(KeyConstants.INTENT_CONSTANT_CATEGORY_ID, eaCategory.getId());
                    context.startActivity(intent);
                }
            });
        }
    }

    class FooterHolder extends RecyclerView.ViewHolder {

        private TextView txtMyWishList, txtMyAccount, txtTrackOrder, txtPolicies;

        public FooterHolder(View itemView) {
            super(itemView);
            txtMyWishList = (TextView) itemView.findViewById(R.id.txt_my_wish_list);
            txtMyAccount = (TextView) itemView.findViewById(R.id.txt_account);
            txtTrackOrder = (TextView) itemView.findViewById(R.id.txt_track_my_order);
            txtPolicies = (TextView) itemView.findViewById(R.id.txt_policies);
        }
    }


}
