package com.app.smartpos.customers;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.smartpos.HomeActivity;
import com.app.smartpos.R;
import com.app.smartpos.database.DatabaseAccess;
import com.app.smartpos.utils.BaseActivity;

import es.dmoral.toasty.Toasty;

public class EditCustomersActivity extends BaseActivity {


    EditText etxtCustomerName, etxtAddress, etxtCustomerCell, etxtCustomerEmail;
    TextView txtEditCustomer, txtUpdateInformation;
    String get_customer_id, get_customer_name, get_customer_cell, get_customer_email, get_customer_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customers);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.edit_customer);


        etxtCustomerName = findViewById(R.id.etxt_customer_name);
        etxtCustomerCell = findViewById(R.id.etxt_customer_cell);
        etxtCustomerEmail = findViewById(R.id.etxt_email);
        etxtAddress = findViewById(R.id.etxt_address);

        txtEditCustomer = findViewById(R.id.txt_edit_customer);
        txtUpdateInformation = findViewById(R.id.txt_update_customer);

        get_customer_id = getIntent().getExtras().getString("customer_id");
        get_customer_name = getIntent().getExtras().getString("customer_name");
        get_customer_cell = getIntent().getExtras().getString("customer_cell");
        get_customer_email = getIntent().getExtras().getString("customer_email");
        get_customer_address = getIntent().getExtras().getString("customer_address");


        etxtCustomerName.setText(get_customer_name);
        etxtCustomerCell.setText(get_customer_cell);
        etxtCustomerEmail.setText(get_customer_email);
        etxtAddress.setText(get_customer_address);


        etxtCustomerName.setEnabled(false);
        etxtCustomerCell.setEnabled(false);
        etxtCustomerEmail.setEnabled(false);
        etxtAddress.setEnabled(false);

        txtUpdateInformation.setVisibility(View.INVISIBLE);


        txtEditCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etxtCustomerName.setEnabled(true);
                etxtCustomerCell.setEnabled(true);
                etxtCustomerEmail.setEnabled(true);
                etxtAddress.setEnabled(true);

                etxtCustomerName.setTextColor(Color.RED);
                etxtCustomerCell.setTextColor(Color.RED);
                etxtCustomerEmail.setTextColor(Color.RED);
                etxtAddress.setTextColor(Color.RED);
                txtUpdateInformation.setVisibility(View.VISIBLE);

            }
        });


        txtUpdateInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String customer_name = etxtCustomerName.getText().toString().trim();
                String customer_cell = etxtCustomerCell.getText().toString().trim();
                String customer_email = etxtCustomerEmail.getText().toString().trim();
                String customer_address = etxtAddress.getText().toString().trim();

                if (customer_name.isEmpty()) {
                    etxtCustomerName.setError(getString(R.string.enter_customer_name));
                    etxtCustomerName.requestFocus();
                } else if (customer_cell.isEmpty()) {
                    etxtCustomerCell.setError(getString(R.string.enter_customer_cell));
                    etxtCustomerCell.requestFocus();
                } else if (customer_email.isEmpty() || !customer_email.contains("@") || !customer_email.contains(".")) {
                    etxtCustomerEmail.setError(getString(R.string.enter_valid_email));
                    etxtCustomerEmail.requestFocus();
                } else if (customer_address.isEmpty()) {
                    etxtAddress.setError(getString(R.string.enter_customer_address));
                    etxtAddress.requestFocus();
                } else {

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(EditCustomersActivity.this);
                    databaseAccess.open();

                    boolean check = databaseAccess.updateCustomer(get_customer_id, customer_name, customer_cell, customer_email, customer_address);

                    if (check) {
                        Toasty.success(EditCustomersActivity.this, R.string.update_successfully, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditCustomersActivity.this, CustomersActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {

                        Toasty.error(EditCustomersActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

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
