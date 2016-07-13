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
import com.emiadda.wsdl.ProductModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shraddha on 16/3/16.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private static final String TAG = CartAdapter.class.getSimpleName();
    private Context context;
    public List<ProductModel> cartList;

    public CartAdapter(Activity context) {
        this.context = context;
        cartList = new ArrayList<>();
    }

    public void addProduct(List<ProductModel> productModelList) {
        if(productModelList != null) {
            cartList = productModelList;
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
        ProductModel model = cartList.get(position);
        holder.txtBrandName.setText(model.getMeta_title());
        holder.txtAmount.setText(model.getPrice());
        //holder.txtQunt
    }

    @Override
    public int getItemCount() {
        if(cartList == null) {
            return 0;
        }
        return cartList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtBrandName, txtSize, txtQunt, txtAmount;
        public ImageView imgCat;

        public ViewHolder(View v) {
            super(v);
            txtBrandName = (TextView) v.findViewById(R.id.txt_brand_name_value);
            txtSize = (TextView) v.findViewById(R.id.txt_size_value);
            txtQunt = (TextView) v.findViewById(R.id.txt_qun_value);
            txtAmount = (TextView) v.findViewById(R.id.txt_amount_value);
            imgCat = (ImageView) v.findViewById(R.id.img_cart);
        }
    }
}
