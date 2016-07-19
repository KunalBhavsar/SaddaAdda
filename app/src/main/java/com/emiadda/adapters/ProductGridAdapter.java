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
import com.emiadda.wsdl.ProductModel;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shraddha on 16/3/16.
 */
public class ProductGridAdapter extends RecyclerView.Adapter<ProductGridAdapter.ViewHolder> implements Filterable {

    private static final String TAG = ProductGridAdapter.class.getSimpleName();
    private Context context;
    private List<ProductModel> masterProductList;
    private List<ProductModel> filteredProductList;
    private OnItemClickListener mItemClickListener;
    private DecimalFormat formater = new DecimalFormat("#.##");
    private String filterString;
    private ProductGridFilter productGridFilter;

    public ProductGridAdapter(Activity context, OnItemClickListener listener) {
        this.context = context;
        masterProductList = new ArrayList<>();
        filteredProductList = new ArrayList<>();
        this.mItemClickListener = listener;
        productGridFilter = new ProductGridFilter();
    }

    public void resetProducts(List<ProductModel> productList) {
        if(productList != null) {
            masterProductList.clear();
            filteredProductList.clear();
            masterProductList.addAll(productList);
            if(filterString != null && !filterString.isEmpty()) {
                getFilter().filter(filterString);
            }
            else {
                filteredProductList.addAll(masterProductList);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_elememt_product, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ProductModel productModel = filteredProductList.get(position);
        holder.bind(productModel, mItemClickListener);
        holder.txtProductName.setText(productModel.getName().replaceAll("&amp;", "&"));
        holder.txtPrice.setText("Rs." + formater.format(Double.parseDouble(productModel.getPrice())));
        Picasso.with(context).load(productModel.getActualImage()).placeholder(R.drawable.placeholder_product).fit().into(holder.imgProduct);
    }

    @Override
    public int getItemCount() {
        return filteredProductList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtProductName, txtPrice, txtNew;
        public ImageView imgProduct, imgFav;

        public ViewHolder(View v) {
            super(v);
            imgProduct = (ImageView) v.findViewById(R.id.image);
            imgFav = (ImageView) v.findViewById(R.id.image_fav);
            txtProductName = (TextView) v.findViewById(R.id.txt_brand);
            txtPrice = (TextView) v.findViewById(R.id.txt_price);
            txtNew = (TextView) v.findViewById(R.id.txt_new);
        }

        public void bind(final ProductModel item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ProductModel item);
    }

    @Override
    public Filter getFilter() {
        return productGridFilter;
    }

    public class ProductGridFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filterString = constraint.toString().toLowerCase();

            final List<ProductModel> list = masterProductList;
            final ArrayList<ProductModel> nlist = new ArrayList<>();

            for (ProductModel productModel : list) {
                if(productModel.getName().toLowerCase().contains(filterString)) {
                    nlist.add(productModel);
                }
            }

            FilterResults results = new FilterResults();
            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredProductList = (ArrayList<ProductModel>) results.values;
            notifyDataSetChanged();
        }
    }

}
