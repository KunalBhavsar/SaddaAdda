package com.emiadda.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emiadda.R;
import com.emiadda.core.EAAddress;

import java.util.List;

/**
 * Created by Rashmi on 02/07/16.
 */
public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    List<EAAddress> eaAddressList;

    public AddressAdapter(List<EAAddress> addresses) {
        eaAddressList = addresses;
    }

    public void addAddress(EAAddress eaAddress) {
        eaAddressList.add(eaAddress);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_address, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return eaAddressList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);

        }
    }
}
