package com.app.smartpos.pos;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.smartpos.HomeActivity;
import com.app.smartpos.R;
import com.app.smartpos.adapter.PosProductAdapter;
import com.app.smartpos.adapter.ProductCategoryAdapter;
import com.app.smartpos.database.DatabaseAccess;
import com.app.smartpos.utils.BaseActivity;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class PosActivity extends BaseActivity {


    private RecyclerView recyclerView;
    PosProductAdapter productAdapter;
    TextView txtNoProducts,txtReset;
    ProductCategoryAdapter categoryAdapter;
    public static TextView txtCount;

    ImageView imgNoProduct,imgScanner,imgCart,imgBack;
    public static EditText etxtSearch;
    int spanCount=2;
    DatabaseAccess databaseAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos);


        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.all_product);
        getSupportActionBar().hide();

        etxtSearch = findViewById(R.id.etxt_search);
        recyclerView = findViewById(R.id.recycler);
        imgNoProduct = findViewById(R.id.image_no_product);
        txtNoProducts = findViewById(R.id.txt_no_products);
        imgScanner=findViewById(R.id.img_scanner);

        txtReset=findViewById(R.id.txt_reset);
        txtCount=findViewById(R.id.txt_count);
        imgBack=findViewById(R.id.img_back);
        imgCart=findViewById(R.id.img_cart);
        RecyclerView categoryRecyclerView = findViewById(R.id.category_recyclerview);

        databaseAccess = DatabaseAccess.getInstance(PosActivity.this);



        imgScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PosActivity.this,ScannerActivity.class);
                startActivity(intent);
            }
        });

        imgNoProduct.setVisibility(View.GONE);
        txtNoProducts.setVisibility(View.GONE);


        //Determine screen size
        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {

            spanCount=4;
        }
        else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {

            spanCount=2;
        }
        else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {

            spanCount=2;
        }
        else {

            spanCount=4;
        }


        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView


        recyclerView.setHasFixedSize(true);

        txtReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                databaseAccess.open();

                //get data from local database
                List<HashMap<String, String>> productList;
                productList = databaseAccess.getProducts();

                if (productList.isEmpty()) {

                    recyclerView.setVisibility(View.GONE);
                    imgNoProduct.setVisibility(View.VISIBLE);
                    imgNoProduct.setImageResource(R.drawable.not_found);
                    txtNoProducts.setVisibility(View.VISIBLE);


                } else {


                    recyclerView.setVisibility(View.VISIBLE);
                    imgNoProduct.setVisibility(View.GONE);
                    txtNoProducts.setVisibility(View.GONE);

                    productAdapter = new PosProductAdapter(PosActivity.this, productList);

                    recyclerView.setAdapter(productAdapter);


                }




            }
        });



        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        LinearLayoutManager linerLayoutManager = new LinearLayoutManager(PosActivity.this,LinearLayoutManager.HORIZONTAL,false);
        categoryRecyclerView.setLayoutManager(linerLayoutManager); // set LayoutManager to RecyclerView


        categoryRecyclerView.setHasFixedSize(true);


        databaseAccess.open();

        //get data from local database
        List<HashMap<String, String>> categoryData;
        categoryData = databaseAccess.getProductCategory();

        Log.d("data", "" + categoryData.size());

        if (categoryData.isEmpty()) {
            Toasty.info(this, R.string.no_data_found, Toast.LENGTH_SHORT).show();

        } else {


            categoryAdapter = new ProductCategoryAdapter(PosActivity.this, categoryData,recyclerView,imgNoProduct,txtNoProducts);

            categoryRecyclerView.setAdapter(categoryAdapter);


        }



        databaseAccess.open();
        int count=databaseAccess.getCartItemCount();
        if (count==0)
        {
            txtCount.setVisibility(View.INVISIBLE);
        }
        else
        {
            txtCount.setVisibility(View.VISIBLE);
            txtCount.setText(String.valueOf(count));
        }

        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PosActivity.this, ProductCart.class);
                startActivity(intent);
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PosActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });



        databaseAccess.open();

        //get data from local database
        List<HashMap<String, String>> productList;
        productList = databaseAccess.getProducts();

        if (productList.isEmpty()) {

            recyclerView.setVisibility(View.GONE);
            imgNoProduct.setVisibility(View.VISIBLE);
            imgNoProduct.setImageResource(R.drawable.not_found);
            txtNoProducts.setVisibility(View.VISIBLE);


        } else {


            recyclerView.setVisibility(View.VISIBLE);
            imgNoProduct.setVisibility(View.GONE);
            txtNoProducts.setVisibility(View.GONE);

            productAdapter = new PosProductAdapter(PosActivity.this, productList);

            recyclerView.setAdapter(productAdapter);


        }


        etxtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                databaseAccess.open();
                //get data from local database
                List<HashMap<String, String>> searchProductList;

                searchProductList = databaseAccess.getSearchProducts(s.toString());


                if (searchProductList.size() <= 0) {

                    recyclerView.setVisibility(View.GONE);
                    imgNoProduct.setVisibility(View.VISIBLE);
                    imgNoProduct.setImageResource(R.drawable.not_found);
                    txtNoProducts.setVisibility(View.VISIBLE);


                } else {


                    recyclerView.setVisibility(View.VISIBLE);
                    imgNoProduct.setVisibility(View.GONE);
                    txtNoProducts.setVisibility(View.GONE);

                    productAdapter = new PosProductAdapter(PosActivity.this, searchProductList);

                    recyclerView.setAdapter(productAdapter);


                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }


        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        return true;
    }

    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.menu_cart_button:
                Intent intent = new Intent(PosActivity.this, ProductCart.class);
                startActivity(intent);
                return true;


            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    //to recheck item count when back this activity
    @Override
    protected void onResume() {
        super.onResume();


        databaseAccess.open();
        int count = databaseAccess.getCartItemCount();
        if (count == 0) {
            txtCount.setVisibility(View.INVISIBLE);
        } else {
            txtCount.setVisibility(View.VISIBLE);
            txtCount.setText(String.valueOf(count));
        }
    }


}
