package com.emiadda.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
        EAAddress eaAddress = eaAddressList.get(position);
        if(eaAddress.isDefaultAddress()) {
            holder.txtDefaultAddress.setVisibility(View.VISIBLE);
        }
        else {
            holder.txtDefaultAddress.setVisibility(View.GONE);
        }

        holder.txtUserName.setText(eaAddress.getUserName());
        holder.txtAddress.setText(eaAddress.getAddress());
        holder.btnDeliverToAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: show toast or do something
            }
        });
    }

    @Override
    public int getItemCount() {
        return eaAddressList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUserName;
        TextView txtDefaultAddress;
        TextView txtAddress;
        Button btnDeliverToAddress;
        public ViewHolder(View itemView) {
            super(itemView);
            txtUserName = (TextView) itemView.findViewById(R.id.txt_user_name);
            txtDefaultAddress = (TextView) itemView.findViewById(R.id.txt_default_address);
            txtAddress = (TextView) itemView.findViewById(R.id.txt_address);
            btnDeliverToAddress = (Button) itemView.findViewById(R.id.btn_deliver_to_address);
        }
    }
}
