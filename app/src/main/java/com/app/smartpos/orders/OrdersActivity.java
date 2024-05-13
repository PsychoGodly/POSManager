package com.app.smartpos.orders;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.smartpos.HomeActivity;
import com.app.smartpos.R;
import com.app.smartpos.adapter.OrderAdapter;
import com.app.smartpos.database.DatabaseAccess;
import com.app.smartpos.utils.BaseActivity;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class OrdersActivity extends BaseActivity {


    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;

    ImageView imgNoProduct;
    TextView txtNoProducts;
    EditText etxtSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        recyclerView = findViewById(R.id.recycler);
        imgNoProduct = findViewById(R.id.image_no_product);

        txtNoProducts=findViewById(R.id.txt_no_products);
        etxtSearch=findViewById(R.id.etxt_search_order);

        imgNoProduct.setVisibility(View.GONE);
        txtNoProducts.setVisibility(View.GONE);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.order_history);

        //for interstitial ads show
//        Utils utils=new Utils();
//        utils.interstitialAdsShow(OrdersActivity.this);


        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrdersActivity.this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView

        recyclerView.setHasFixedSize(true);


        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(OrdersActivity.this);
        databaseAccess.open();


        //get data from local database
        List<HashMap<String, String>> orderList;
        orderList = databaseAccess.getOrderList();

        if (orderList.size() <= 0) {
            //if no data in local db, then load data from server
            Toasty.info(OrdersActivity.this, R.string.no_order_found, Toast.LENGTH_SHORT).show();
            recyclerView.setVisibility(View.GONE);
            imgNoProduct.setVisibility(View.VISIBLE);
            imgNoProduct.setImageResource(R.drawable.not_found);
            txtNoProducts.setVisibility(View.VISIBLE);
        } else {
            orderAdapter = new OrderAdapter(OrdersActivity.this, orderList);

            recyclerView.setAdapter(orderAdapter);
        }



        etxtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {




                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(OrdersActivity.this);
                databaseAccess.open();
                //get data from local database
                List<HashMap<String, String>> searchOrder;

                searchOrder = databaseAccess.searchOrderList(s.toString());


                if (searchOrder.size() <= 0) {
                    recyclerView.setVisibility(View.GONE);
                    imgNoProduct.setVisibility(View.VISIBLE);
                    imgNoProduct.setImageResource(R.drawable.no_data);



                } else {


                    recyclerView.setVisibility(View.VISIBLE);
                    imgNoProduct.setVisibility(View.GONE);


                    OrderAdapter supplierAdapter = new OrderAdapter(OrdersActivity.this, searchOrder);

                    recyclerView.setAdapter(supplierAdapter);


                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }


        });

    }


    @Override
    public void onBackPressed() {

        Intent intent=new Intent(OrdersActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                Intent intent=new Intent(OrdersActivity.this, HomeActivity.class);
                startActivity(intent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
