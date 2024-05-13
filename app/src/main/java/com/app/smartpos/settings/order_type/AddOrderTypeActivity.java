package com.app.smartpos.settings.order_type;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.smartpos.R;
import com.app.smartpos.database.DatabaseAccess;
import com.app.smartpos.utils.BaseActivity;

import es.dmoral.toasty.Toasty;

public class AddOrderTypeActivity extends BaseActivity {


    EditText etxtOrderType;
    TextView txtAddOrderType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order_type);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.add_order_type);

        etxtOrderType = findViewById(R.id.etxt_order_type);
        txtAddOrderType = findViewById(R.id.txt_add_order_type);


        txtAddOrderType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String orderTypeName = etxtOrderType.getText().toString().trim();

                if (orderTypeName.isEmpty()) {
                    etxtOrderType.setError(getString(R.string.order_type_name));
                    etxtOrderType.requestFocus();
                } else {

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddOrderTypeActivity.this);
                    databaseAccess.open();

                    boolean check = databaseAccess.addOrderType(orderTypeName);

                    if (check) {
                        Toasty.success(AddOrderTypeActivity.this, R.string.successfully_added, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddOrderTypeActivity.this, OrderTypeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {

                        Toasty.error(AddOrderTypeActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

                    }
                }


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
