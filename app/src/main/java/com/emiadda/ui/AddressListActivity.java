package com.emiadda.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.emiadda.R;
import com.emiadda.adapters.AddressAdapter;
import com.emiadda.core.EAAddress;

import java.util.List;

public class AddressListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Button btnAddAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
        mRecyclerView = (RecyclerView) findViewById(R.id.address_recycler_view);
        btnAddAddress = (Button)findViewById(R.id.btn_add_address);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new AddressAdapter(new List<EAAddress>);
        mRecyclerView.setAdapter(mAdapter);

    }
}
