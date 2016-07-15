package com.emiadda.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.emiadda.R;
import com.emiadda.core.EACategory;
import com.emiadda.views.CircularImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kunal on 05/07/16.
 */
public class CategoryAdapter extends BaseAdapter {
    private List<EACategory> categories;
    private Context context;

    public CategoryAdapter(Context context) {
        this.context = context;
        categories = new ArrayList<>();
    }

    public void addCategory(EACategory eaCategory) {
        if(eaCategory != null) {
            categories.add(eaCategory);
            notifyDataSetChanged();
        }
    }

    public void addCategories(List<EACategory> eaCategories) {
        if(eaCategories != null) {
            this.categories.addAll(eaCategories);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_grid_category, null);
            viewHolder = new ViewHolder();
            viewHolder.imgCategory = (CircularImageView)convertView.findViewById(R.id.img_category);
            viewHolder.txtCategory = (TextView) convertView.findViewById(R.id.txt_category);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        EACategory eaCategory = (EACategory)getItem(position);
        viewHolder.txtCategory.setText(eaCategory.getCategoryName().replaceAll("&amp;","&"));

        String catName = eaCategory.getCategoryName().toLowerCase();
        //Set category image logic
        if(catName.equalsIgnoreCase("women")) {
            Drawable drawable = context.getResources().getDrawable(context.getResources().getIdentifier("women", "drawable", context.getPackageName()));
            viewHolder.imgCategory.setImageDrawable(drawable);
        }else if(catName.equalsIgnoreCase("men")) {
            Drawable drawable = context.getResources().getDrawable(context.getResources().getIdentifier("men", "drawable", context.getPackageName()));
            viewHolder.imgCategory.setImageDrawable(drawable);
        }else if(catName.equalsIgnoreCase("fashion")) {
            Drawable drawable = context.getResources().getDrawable(context.getResources().getIdentifier("fashion", "drawable", context.getPackageName()));
            viewHolder.imgCategory.setImageDrawable(drawable);
        }else if(catName.contains("jew")) {
            Drawable drawable = context.getResources().getDrawable(context.getResources().getIdentifier("jwellery", "drawable", context.getPackageName()));
            viewHolder.imgCategory.setImageDrawable(drawable);
        }else if(catName.contains("health")) {
            Drawable drawable = context.getResources().getDrawable(context.getResources().getIdentifier("health", "drawable", context.getPackageName()));
            viewHolder.imgCategory.setImageDrawable(drawable);
        }else if(catName.contains("home")) {
            Drawable drawable = context.getResources().getDrawable(context.getResources().getIdentifier("home_living", "drawable", context.getPackageName()));
            viewHolder.imgCategory.setImageDrawable(drawable);
        }else if(catName.contains("hair")) {
            Drawable drawable = context.getResources().getDrawable(context.getResources().getIdentifier("hair", "drawable", context.getPackageName()));
            viewHolder.imgCategory.setImageDrawable(drawable);
        }else if(catName.contains("food")) {
            Drawable drawable = context.getResources().getDrawable(context.getResources().getIdentifier("food", "drawable", context.getPackageName()));
            viewHolder.imgCategory.setImageDrawable(drawable);
        }else if(catName.contains("hit")) {
            Drawable drawable = context.getResources().getDrawable(context.getResources().getIdentifier("elec", "drawable", context.getPackageName()));
            viewHolder.imgCategory.setImageDrawable(drawable);
        }

        return convertView;
    }

    class ViewHolder {
        CircularImageView imgCategory;
        TextView txtCategory;
    }
}
