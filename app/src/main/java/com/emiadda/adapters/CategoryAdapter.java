package com.emiadda.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.emiadda.R;
import com.emiadda.core.EACategory;
import com.emiadda.views.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kunal on 05/07/16.
 */
public class CategoryAdapter extends BaseAdapter implements Filterable {
    private List<EACategory> masterCategories;
    private List<EACategory> filteredCategories;
    private Context context;
    private CategoryFilter categoryFilter;
    private String filterString;

    public CategoryAdapter(Context context) {
        this.context = context;
        masterCategories = new ArrayList<>();
        filteredCategories = new ArrayList<>();
        categoryFilter = new CategoryFilter();
    }

    public void addCategory(EACategory eaCategory) {
        if(eaCategory != null) {
            masterCategories.add(eaCategory);
            if(filterString != null && !filterString.isEmpty()) {
                getFilter().filter(filterString);
            }
            else {
                filteredCategories.add(eaCategory);
                notifyDataSetChanged();
            }
        }
    }

    public void resetCategories(List<EACategory> eaCategories) {
        if(eaCategories != null) {
            masterCategories.clear();
            filteredCategories.clear();
            masterCategories.addAll(eaCategories);
            if(filterString != null && !filterString.isEmpty()) {
                getFilter().filter(filterString);
            }
            else {
                filteredCategories.addAll(masterCategories);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public int getCount() {
        return filteredCategories.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredCategories.get(position);
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
            Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Regular.otf");
            viewHolder.txtCategory.setTypeface(tf);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        EACategory eaCategory = (EACategory)getItem(position);
        viewHolder.txtCategory.setText(eaCategory.getCategoryName().replaceAll("&amp;","&").toUpperCase());

        String catName = eaCategory.getCategoryName().toLowerCase();
        //Set category image logic
        int identifier;
        if(catName.equalsIgnoreCase("women")) {
            identifier = context.getResources().getIdentifier("women", "drawable", context.getPackageName());
        }
        else if(catName.equalsIgnoreCase("men")) {
            identifier = context.getResources().getIdentifier("men", "drawable", context.getPackageName());
        }
        else if(catName.equalsIgnoreCase("fashion")) {
            identifier = context.getResources().getIdentifier("fashion", "drawable", context.getPackageName());
        }
        else if(catName.contains("jew")) {
            identifier = context.getResources().getIdentifier("jwellery", "drawable", context.getPackageName());
        }
        else if(catName.contains("health")) {
            identifier = context.getResources().getIdentifier("health", "drawable", context.getPackageName());
        }
        else if(catName.contains("home")) {
            identifier = context.getResources().getIdentifier("home_living", "drawable", context.getPackageName());
        }
        else if(catName.contains("hair")) {
            identifier = context.getResources().getIdentifier("hair", "drawable", context.getPackageName());
        }
        else if(catName.contains("food")) {
            identifier = context.getResources().getIdentifier("food", "drawable", context.getPackageName());
        }
        else if(catName.contains("hit")) {
            identifier = context.getResources().getIdentifier("elec", "drawable", context.getPackageName());
        }
        else if(catName.contains("western")) {
            identifier = context.getResources().getIdentifier("western", "drawable", context.getPackageName());
        }
        else {
            identifier = R.drawable.placeholder_product;
        }
        Picasso.with(context).load(identifier).fit().into(viewHolder.imgCategory);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return categoryFilter;
    }

    class ViewHolder {
        CircularImageView imgCategory;
        TextView txtCategory;
    }

    class CategoryFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filterString = constraint.toString().toLowerCase();

            final List<EACategory> list = masterCategories;
            final ArrayList<EACategory> nlist = new ArrayList<>();

            for (EACategory eaCategory : list) {
                if(eaCategory.getCategoryName().toLowerCase().contains(filterString)) {
                    nlist.add(eaCategory);
                }
            }

            FilterResults results = new FilterResults();
            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredCategories = (ArrayList<EACategory>) results.values;
            notifyDataSetChanged();
        }
    }
}
