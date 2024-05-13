package com.app.smartpos.settings.order_type;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.smartpos.R;
import com.app.smartpos.database.DatabaseAccess;
import com.app.smartpos.utils.BaseActivity;

import es.dmoral.toasty.Toasty;

public class EditOrderTypeActivity extends BaseActivity {

    EditText etxtOrderTypeName;
    TextView txtUpdate, txtEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order_type);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.update_order_type);

        txtEdit = findViewById(R.id.txt_edit);
        txtUpdate = findViewById(R.id.txt_update);
        etxtOrderTypeName = findViewById(R.id.etxt_order_type);

        String order_type_id = getIntent().getExtras().getString("order_type_id");
        String order_type_name = getIntent().getExtras().getString("order_type_name");


        etxtOrderTypeName.setText(order_type_name);
        etxtOrderTypeName.setEnabled(false);
        txtUpdate.setVisibility(View.INVISIBLE);


        txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etxtOrderTypeName.setEnabled(true);
                txtUpdate.setVisibility(View.VISIBLE);
                etxtOrderTypeName.setTextColor(Color.RED);

            }
        });


        txtUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String orderTypeName = etxtOrderTypeName.getText().toString().trim();

                if (orderTypeName.isEmpty()) {
                    etxtOrderTypeName.setError(getString(R.string.order_type_name));
                    etxtOrderTypeName.requestFocus();
                } else {

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(EditOrderTypeActivity.this);
                    databaseAccess.open();

                    boolean check = databaseAccess.updateOrderType(order_type_id, orderTypeName);

                    if (check) {
                        Toasty.success(EditOrderTypeActivity.this, R.string.successfully_updated, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditOrderTypeActivity.this, OrderTypeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {

                        Toasty.error(EditOrderTypeActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

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
