package com.emiadda.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.emiadda.R;
import com.emiadda.core.EACategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shraddha on 16/3/16.
 */
public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.ViewHolder> implements Filterable {

    private static final String TAG = SubCategoryAdapter.class.getSimpleName();
    private Context context;
    private List<EACategory> masterCategories;
    private List<EACategory> filteredCategories;
    private SubCategoryFilter categoryFilter;
    private String filterString;
    private OnItemClickListener mItemClickListener;

    public SubCategoryAdapter(Activity context, OnItemClickListener listener) {
        this.context = context;
        masterCategories = new ArrayList<>();
        filteredCategories = new ArrayList<>();
        categoryFilter = new SubCategoryFilter();
        mItemClickListener = listener;
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
            masterCategories.addAll(eaCategories);
            filteredCategories.clear();
            if(filterString != null && !filterString.trim().isEmpty()) {
                getFilter().filter(filterString);
            }
            else {
                filteredCategories.addAll(masterCategories);
                notifyDataSetChanged();
            }
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
        EACategory eaCategory = filteredCategories.get(position);
        holder.bind(eaCategory, mItemClickListener);
        holder.txtName.setText(eaCategory.getCategoryName().replaceAll("&amp;","&"));
        //set image logic
    }

    @Override
    public int getItemCount() {
        return filteredCategories.size();
    }

    @Override
    public Filter getFilter() {
        return categoryFilter;
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

    class SubCategoryFilter extends Filter {

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
