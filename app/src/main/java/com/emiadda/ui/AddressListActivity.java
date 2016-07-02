package com.emiadda.ui;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.emiadda.R;
import com.emiadda.adapters.AddressAdapter;
import com.emiadda.core.EAAddress;

import java.util.ArrayList;
import java.util.List;

public class AddressListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton btnAddAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
        mRecyclerView = (RecyclerView) findViewById(R.id.address_recycler_view);
        btnAddAddress = (FloatingActionButton)findViewById(R.id.btn_add_address);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new AddressAdapter(getAddressList());
        mRecyclerView.setAdapter(mAdapter);

        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: show dialog for add new address
            }
        });
    }

    private List<EAAddress> getAddressList() {
        List<EAAddress> eaAddressList = new ArrayList<>();
        EAAddress eaAddress = new EAAddress();
        eaAddress.setAddress("17 Viya nagar, near Rail duniya, Bhusawal 425201.");
        eaAddress.setUserName("Abhishek Bhavsar");
        eaAddressList.add(eaAddress);

        eaAddress = new EAAddress();
        eaAddress.setAddress("11 ABBK, Mahajan wadi, Opp. Central railway workshop, Parel 400012");
        eaAddress.setDefaultAddress(true);
        eaAddress.setUserName("Kunal Bhavsar");
        eaAddressList.add(eaAddress);

        eaAddress = new EAAddress();
        eaAddress.setAddress("Somewhere in Devipada, in Boriwali :P");
        eaAddress.setUserName("Shraddha Pednekar");
        eaAddressList.add(eaAddress);

        eaAddress = new EAAddress();
        eaAddress.setAddress("Somewhere in Dahanu, near newly opened model bank, Dahanu");
        eaAddress.setUserName("Hitesh Baid");
        eaAddressList.add(eaAddress);
        return eaAddressList;
    }
}
