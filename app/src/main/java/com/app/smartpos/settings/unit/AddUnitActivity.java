package com.app.smartpos.settings.unit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.smartpos.R;
import com.app.smartpos.database.DatabaseAccess;
import com.app.smartpos.settings.payment_method.PaymentMethodActivity;
import com.app.smartpos.utils.BaseActivity;

import es.dmoral.toasty.Toasty;

public class AddUnitActivity extends BaseActivity {


    EditText etxtUnit;
    TextView txtAddUnit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_unit);
        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.add_unit);

        etxtUnit = findViewById(R.id.etxt_unit);
        txtAddUnit = findViewById(R.id.txt_add_unit);


        txtAddUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String unitName = etxtUnit.getText().toString().trim();

                if (unitName.isEmpty()) {
                    etxtUnit.setError(getString(R.string.add_unit));
                    etxtUnit.requestFocus();
                } else {

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddUnitActivity.this);
                    databaseAccess.open();

                    boolean check = databaseAccess.addUnit(unitName);

                    if (check) {
                        Toasty.success(AddUnitActivity.this, R.string.successfully_added, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddUnitActivity.this, UnitActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {

                        Toasty.error(AddUnitActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

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
