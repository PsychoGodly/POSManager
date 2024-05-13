package com.app.smartpos.settings.unit;

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
import com.app.smartpos.adapter.UnitAdapter;
import com.app.smartpos.database.DatabaseAccess;
import com.app.smartpos.settings.order_type.AddOrderTypeActivity;
import com.app.smartpos.settings.order_type.OrderTypeActivity;
import com.app.smartpos.utils.BaseActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class UnitActivity extends BaseActivity {


    private RecyclerView recyclerView;

    ImageView imgNoProduct;
    EditText etxtSearch;

    FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.unit);

        recyclerView = findViewById(R.id.recycler_view);
        imgNoProduct = findViewById(R.id.image_no_product);
        etxtSearch = findViewById(R.id.etxt_search);
        fabAdd = findViewById(R.id.fab_add);


        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView


        recyclerView.setHasFixedSize(true);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(UnitActivity.this);
        databaseAccess.open();

        //get data from local database
        List<HashMap<String, String>> unitData;
        unitData = databaseAccess.getWeightUnit();

        Log.d("data", "" + unitData.size());

        if (unitData.size() <= 0) {
            Toasty.info(this, R.string.no_data_found, Toast.LENGTH_SHORT).show();
            imgNoProduct.setImageResource(R.drawable.no_data);
        } else {


            imgNoProduct.setVisibility(View.GONE);
            UnitAdapter unitAdapter = new UnitAdapter(UnitActivity.this, unitData);

            recyclerView.setAdapter(unitAdapter);


        }

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UnitActivity.this, AddUnitActivity.class);
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

                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(UnitActivity.this);
                databaseAccess.open();
                //get data from local database
                List<HashMap<String, String>> unitList;

                unitList = databaseAccess.searchUnit(s.toString());


                if (unitList.size() <= 0) {
                    //  Toasty.info(ProductActivity.this, "No Product Found!", Toast.LENGTH_SHORT).show();

                    recyclerView.setVisibility(View.GONE);
                    imgNoProduct.setVisibility(View.VISIBLE);
                    imgNoProduct.setImageResource(R.drawable.no_data);


                } else {


                    recyclerView.setVisibility(View.VISIBLE);
                    imgNoProduct.setVisibility(View.GONE);
                    UnitAdapter unitAdapter = new UnitAdapter(UnitActivity.this, unitList);

                    recyclerView.setAdapter(unitAdapter);


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
