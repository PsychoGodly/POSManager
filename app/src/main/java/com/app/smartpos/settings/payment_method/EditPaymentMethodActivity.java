package com.app.smartpos.settings.payment_method;

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
import com.app.smartpos.utils.BaseActivity;

import es.dmoral.toasty.Toasty;

public class EditPaymentMethodActivity extends BaseActivity {


    EditText etxtPaymentMethodName;
    TextView txtUpdatePaymentMethod, txtEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_payment_method);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.update_payment_method);

        txtEdit = findViewById(R.id.txt_edit);
        txtUpdatePaymentMethod = findViewById(R.id.txt_update_payment_method);
        etxtPaymentMethodName = findViewById(R.id.etxt_payment_method_name);

        String payment_method_id = getIntent().getExtras().getString("payment_method_id");
        String payment_method_name = getIntent().getExtras().getString("payment_method_name");


        etxtPaymentMethodName.setText(payment_method_name);
        etxtPaymentMethodName.setEnabled(false);
        txtUpdatePaymentMethod.setVisibility(View.INVISIBLE);


        txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etxtPaymentMethodName.setEnabled(true);
                txtUpdatePaymentMethod.setVisibility(View.VISIBLE);
                etxtPaymentMethodName.setTextColor(Color.RED);

            }
        });


        txtUpdatePaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String payment_method_name = etxtPaymentMethodName.getText().toString().trim();

                if (payment_method_name.isEmpty()) {
                    etxtPaymentMethodName.setError(getString(R.string.payment_method_name));
                    etxtPaymentMethodName.requestFocus();
                } else {

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(EditPaymentMethodActivity.this);
                    databaseAccess.open();

                    boolean check = databaseAccess.updatePaymentMethod(payment_method_id, payment_method_name);

                    if (check) {
                        Toasty.success(EditPaymentMethodActivity.this, R.string.successfully_updated, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditPaymentMethodActivity.this, PaymentMethodActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {

                        Toasty.error(EditPaymentMethodActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

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
