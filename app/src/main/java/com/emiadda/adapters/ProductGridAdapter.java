package com.emiadda.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.emiadda.EAApplication;
import com.emiadda.R;
import com.emiadda.asynctasks.GetProductImageAsync;
import com.emiadda.asynctasks.ServerRequestProcessingThread;
import com.emiadda.core.EAServerRequest;
import com.emiadda.interafaces.ServerResponseSubscriber;
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
public class ProductGridAdapter extends RecyclerView.Adapter<ProductGridAdapter.ViewHolder> implements ServerResponseSubscriber {

    private static final String TAG = ProductGridAdapter.class.getSimpleName();
    private Context context;
    private List<ProductModel> productList;
    private OnItemClickListener mItemClickListener;
    private DecimalFormat formater = new DecimalFormat("#.##");

    public ProductGridAdapter(Activity context, OnItemClickListener listener) {
        this.context = context;
        productList = new ArrayList<>();
        this.mItemClickListener = listener;
        ((EAApplication)context.getApplicationContext()).attach(this);
    }

    public void setProducts(List<ProductModel> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        ((EAApplication)context.getApplicationContext()).dettach(this);
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_elememt_product, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ProductModel productModel = productList.get(position);
        holder.bind(productModel, mItemClickListener);
        holder.txtProductName.setText(productModel.getName().replaceAll("&amp;", "&"));
        holder.txtPrice.setText("Rs." + formater.format(Double.parseDouble(productModel.getPrice())));
        if((productModel.getImage() == null || productModel.getImage().isEmpty()) && !productModel.isLoadingImage()) {
            productModel.setLoadingImage(true);
            EAApplication.makeServerRequest(ServerRequestProcessingThread.REQUEST_CODE_GET_PRODUCT_IMAGE,
                    Integer.parseInt(productModel.getProduct_id()), EAServerRequest.PRIORITY_LOW, productModel.getProduct_id());
        }
        else {
            Picasso.with(context).load(productModel.getImage()).fit().into(holder.imgProduct);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public void responseReceived(final String response, final int requestCode, final int responseCode, final int extraRequestCode) {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Received image download response "+response);
                if(requestCode == ServerRequestProcessingThread.REQUEST_CODE_GET_PRODUCT_IMAGE) {
                    if(responseCode == ServerResponseSubscriber.RESPONSE_CODE_OK) {
                        for (ProductModel product : productList) {
                            if (product.getProduct_id().equals(String.valueOf(extraRequestCode))) {
                                try {
                                    ProductImageModel productImageModel = new Gson().fromJson(response, new TypeToken<ProductImageModel>() {
                                    }.getType());
                                    if (productImageModel != null) {
                                        product.setImage(productImageModel.getImage().replaceAll("&amp;", "&").replaceAll(" ", "%20"));
                                        notifyDataSetChanged();
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, e.getMessage(), e);
                                }
                            }
                        }
                    }
                }
            }
        });
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
}
