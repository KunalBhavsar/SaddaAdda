package com.emiadda.adapters;

import android.content.Context;
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

        //Set category image logic


        return convertView;
    }

    class ViewHolder {
        CircularImageView imgCategory;
        TextView txtCategory;
    }
}
