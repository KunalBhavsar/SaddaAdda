package com.emiadda.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emiadda.R;
import com.emiadda.utils.AppPreferences;

/**
 * Created by Shraddha on 19/7/16.
 */
public class CartFrgament extends Fragment {

    private int size;
    private TextView txtCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.toolbar_layout, container, false);
        txtCount = (TextView) view.findViewById(R.id.txt_count);
        size = AppPreferences.getInstance().getCartList().size();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(size > 99) {
            txtCount.setVisibility(View.VISIBLE);
            txtCount.setText("<99");
        } else if(size > 0) {
            txtCount.setVisibility(View.VISIBLE);
            txtCount.setText(""+size);
        }
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
