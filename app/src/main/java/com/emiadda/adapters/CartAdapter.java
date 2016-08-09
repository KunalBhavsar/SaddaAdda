package com.emiadda.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.emiadda.EAApplication;
import com.emiadda.R;
import com.emiadda.asynctasks.ServerRequestProcessingThread;
import com.emiadda.interafaces.ServerResponseSubscriber;
import com.emiadda.server.ServerResponse;
import com.emiadda.ui.ProductDetailActivity;
import com.emiadda.utils.AppPreferences;
import com.emiadda.utils.KeyConstants;
import com.emiadda.wsdl.ProductImageModel;
import com.emiadda.wsdl.ProductModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shraddha on 16/3/16.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private static final String TAG = CartAdapter.class.getSimpleName();
    private Context context;
    public List<ProductModel> cartList;
    private DecimalFormat formater = new DecimalFormat("#.##");

    public CartAdapter(Activity context) {
        this.context = context;
        cartList = new ArrayList<>();
    }

    public void resetProductList(List<ProductModel> productModelList) {
        cartList.clear();
        if (productModelList != null) {
            cartList.addAll(productModelList);
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
        ProductModel productModel = cartList.get(position);
        holder.txtBrandName.setText(productModel.getMeta_title());
        holder.txtAmount.setText(productModel.getPrice());
        holder.txtQunt.setText(productModel.getQuantity());
        holder.txtAmount.setText(KeyConstants.rs + formater.format(Integer.valueOf(productModel.getQuantity()) * Double.parseDouble(productModel.getPrice())));

        Picasso.with(context).load(productModel.getActualImage()).fit().placeholder(R.drawable.placeholder_product).into(holder.imgCat);

        holder.btnRemove.setTag(productModel.getProduct_id());
        holder.btnEdit.setTag(productModel.getProduct_id());
    }

    @Override
    public int getItemCount() {
        if (cartList == null) {
            return 0;
        }
        return cartList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtBrandName, txtSize, txtQunt, txtAmount;
        public ImageView imgCat;
        public Button btnSave, btnRemove, btnEdit;

        public ViewHolder(View v) {
            super(v);
            txtBrandName = (TextView) v.findViewById(R.id.txt_brand_name_value);
            txtSize = (TextView) v.findViewById(R.id.txt_size_value);
            txtQunt = (TextView) v.findViewById(R.id.txt_qun_value);
            txtAmount = (TextView) v.findViewById(R.id.txt_amount_value);
            imgCat = (ImageView) v.findViewById(R.id.img_cart);
            btnSave = (Button) v.findViewById(R.id.btn_save);
            btnRemove = (Button) v.findViewById(R.id.btn_remove);
            btnEdit = (Button) v.findViewById(R.id.btn_edit);

            btnSave.setOnClickListener(this);
            btnRemove.setOnClickListener(this);
            btnEdit.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_save:

                    break;

                case R.id.btn_remove:
                    ProductModel productModel = new ProductModel();
                    productModel.setProduct_id(String.valueOf(v.getTag()));
                    cartList.remove(productModel);
                    AppPreferences.getInstance().removeProductFromCartList(productModel);
                    notifyDataSetChanged();
                    break;

                case R.id.btn_edit:
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    productModel = new ProductModel();
                    productModel.setProduct_id(String.valueOf(v.getTag()));
                    int itemIndex = cartList.indexOf(productModel);
                    if (itemIndex >= 0) {
                        ProductModel cartItemSelected = cartList.get(itemIndex);
                        intent.putExtra(KeyConstants.INTENT_CONSTANT_PRODUCT_ID, cartItemSelected.getProduct_id());
                        intent.putExtra(KeyConstants.INTENT_CONSTANT_PRODUCT_NAME, cartItemSelected.getName());
                        intent.putExtra(KeyConstants.INTENT_CONSTANT_PRODUCT_ITEM_SELECTED_COUNT, cartItemSelected.getQuantity());
                        context.startActivity(intent);
                    } else {
                        Log.i(TAG, "Wrong item index selected : " + itemIndex + " for product id : " + productModel.getProduct_id());
                    }
                    break;
            }
        }
    }
}
