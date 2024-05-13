package com.app.smartpos.settings.unit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.smartpos.R;
import com.app.smartpos.database.DatabaseAccess;
import com.app.smartpos.settings.order_type.OrderTypeActivity;
import com.app.smartpos.utils.BaseActivity;

import es.dmoral.toasty.Toasty;

public class EditUnitActivity extends BaseActivity {


    EditText etxtWeightUnit;
    TextView txtUpdate, txtEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_unit);


        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.update_unit);

        txtEdit = findViewById(R.id.txt_edit);
        txtUpdate = findViewById(R.id.txt_update);
        etxtWeightUnit = findViewById(R.id.etxt_weight_unit);

        String weightId = getIntent().getExtras().getString("weight_id");
        String weightUnit = getIntent().getExtras().getString("weight_unit");


        etxtWeightUnit.setText(weightUnit);
        etxtWeightUnit.setEnabled(false);
        txtUpdate.setVisibility(View.INVISIBLE);


        txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etxtWeightUnit.setEnabled(true);
                txtUpdate.setVisibility(View.VISIBLE);
                etxtWeightUnit.setTextColor(Color.RED);

            }
        });


        txtUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String weightUnit = etxtWeightUnit.getText().toString().trim();

                if (weightUnit.isEmpty()) {
                    etxtWeightUnit.setError(getString(R.string.weight_unit_name));
                    etxtWeightUnit.requestFocus();
                } else {

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(EditUnitActivity.this);
                    databaseAccess.open();

                    boolean check = databaseAccess.updateWeightUnit(weightId, weightUnit);

                    if (check) {
                        Toasty.success(EditUnitActivity.this, R.string.successfully_updated, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditUnitActivity.this, UnitActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {

                        Toasty.error(EditUnitActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

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
