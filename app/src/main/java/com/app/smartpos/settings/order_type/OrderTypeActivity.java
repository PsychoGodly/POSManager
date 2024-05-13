package com.app.smartpos.settings.order_type;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.smartpos.R;
import com.app.smartpos.adapter.OrderTypeAdapter;
import com.app.smartpos.adapter.PaymentMethodAdapter;
import com.app.smartpos.database.DatabaseAccess;
import com.app.smartpos.settings.payment_method.AddPaymentMethodActivity;
import com.app.smartpos.settings.payment_method.PaymentMethodActivity;
import com.app.smartpos.utils.BaseActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class OrderTypeActivity extends BaseActivity {


    private RecyclerView recyclerView;

    ImageView imgNoProduct;
    EditText etxtSearch;

    FloatingActionButton fabAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_type);


        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.order_type);

        recyclerView = findViewById(R.id.recycler_view);
        imgNoProduct = findViewById(R.id.image_no_product);
        etxtSearch = findViewById(R.id.etxt_search);
        fabAdd = findViewById(R.id.fab_add);


        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView


        recyclerView.setHasFixedSize(true);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(OrderTypeActivity.this);
        databaseAccess.open();

        //get data from local database
        List<HashMap<String, String>> orderTypeData;
        orderTypeData = databaseAccess.getOrderType();

        Log.d("data", "" + orderTypeData.size());

        if (orderTypeData.size() <= 0) {
            Toasty.info(this, R.string.no_data_found, Toast.LENGTH_SHORT).show();
            imgNoProduct.setImageResource(R.drawable.no_data);
        } else {


            imgNoProduct.setVisibility(View.GONE);
            OrderTypeAdapter orderTypeAdapter = new OrderTypeAdapter(OrderTypeActivity.this, orderTypeData);

            recyclerView.setAdapter(orderTypeAdapter);


        }

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderTypeActivity.this, AddOrderTypeActivity.class);
                startActivity(intent);
            }
        });


        etxtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                //  searchData(s.toString());

                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(OrderTypeActivity.this);
                databaseAccess.open();
                //get data from local database
                List<HashMap<String, String>> searchOrderTypeList;

                searchOrderTypeList = databaseAccess.searchOrderType(s.toString());


                if (searchOrderTypeList.size() <= 0) {
                    //  Toasty.info(ProductActivity.this, "No Product Found!", Toast.LENGTH_SHORT).show();

                    recyclerView.setVisibility(View.GONE);
                    imgNoProduct.setVisibility(View.VISIBLE);
                    imgNoProduct.setImageResource(R.drawable.no_data);


                } else {


                    recyclerView.setVisibility(View.VISIBLE);
                    imgNoProduct.setVisibility(View.GONE);


                    OrderTypeAdapter orderTypeAdapter = new OrderTypeAdapter(OrderTypeActivity.this, searchOrderTypeList);

                    recyclerView.setAdapter(orderTypeAdapter);


                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }


        });


    }


    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
