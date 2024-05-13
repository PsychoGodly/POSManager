package com.app.smartpos.suppliers;

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

public class EditSuppliersActivity extends BaseActivity {

    EditText etxtSuppliersName, etxtSuppliersContactPerson, etxtSuppliersAddress, etxtSuppliersCell, etxtSuppliersEmail;
    TextView txtEditSuppliers, txtUpdateSuppliers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_suppliers);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.edit_suppliers);


        etxtSuppliersName = findViewById(R.id.etxt_supplier_name);
        etxtSuppliersContactPerson = findViewById(R.id.etxt_supplier_contact_name);
        etxtSuppliersCell = findViewById(R.id.etxt_supplier_cell);
        etxtSuppliersEmail = findViewById(R.id.etxt_supplier_email);
        etxtSuppliersAddress = findViewById(R.id.etxt_supplier_address);

        txtUpdateSuppliers = findViewById(R.id.txt_update_suppliers);
        txtEditSuppliers = findViewById(R.id.txt_edit_suppliers);

        String get_suppliers_id = getIntent().getExtras().getString("suppliers_id");
        String get_suppliers_name = getIntent().getExtras().getString("suppliers_name");
        String get_suppliers_contact_person = getIntent().getExtras().getString("suppliers_contact_person");
        String get_suppliers_cell = getIntent().getExtras().getString("suppliers_cell");
        String get_suppliers_email = getIntent().getExtras().getString("suppliers_email");
        String get_suppliers_address = getIntent().getExtras().getString("suppliers_address");


        etxtSuppliersName.setText(get_suppliers_name);
        etxtSuppliersContactPerson.setText(get_suppliers_contact_person);
        etxtSuppliersCell.setText(get_suppliers_cell);
        etxtSuppliersEmail.setText(get_suppliers_email);
        etxtSuppliersAddress.setText(get_suppliers_address);

        etxtSuppliersName.setEnabled(false);
        etxtSuppliersContactPerson.setEnabled(false);
        etxtSuppliersCell.setEnabled(false);
        etxtSuppliersEmail.setEnabled(false);
        etxtSuppliersAddress.setEnabled(false);


        txtUpdateSuppliers.setVisibility(View.INVISIBLE);

        txtEditSuppliers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                etxtSuppliersName.setEnabled(true);
                etxtSuppliersContactPerson.setEnabled(true);
                etxtSuppliersCell.setEnabled(true);
                etxtSuppliersEmail.setEnabled(true);
                etxtSuppliersAddress.setEnabled(true);


                etxtSuppliersName.setTextColor(Color.RED);
                etxtSuppliersContactPerson.setTextColor(Color.RED);
                etxtSuppliersCell.setTextColor(Color.RED);
                etxtSuppliersEmail.setTextColor(Color.RED);
                etxtSuppliersAddress.setTextColor(Color.RED);


                txtUpdateSuppliers.setVisibility(View.VISIBLE);
                txtEditSuppliers.setVisibility(View.GONE);

            }
        });


        txtUpdateSuppliers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String suppliers_name = etxtSuppliersName.getText().toString().trim();
                String suppliers_contact_person = etxtSuppliersContactPerson.getText().toString().trim();
                String suppliers_cell = etxtSuppliersCell.getText().toString().trim();
                String suppliers_email = etxtSuppliersEmail.getText().toString().trim();
                String suppliers_address = etxtSuppliersAddress.getText().toString().trim();


                if (suppliers_name.isEmpty()) {
                    etxtSuppliersName.setError(getString(R.string.enter_suppliers_name));
                    etxtSuppliersName.requestFocus();
                } else if (suppliers_contact_person.isEmpty()) {
                    etxtSuppliersContactPerson.setError(getString(R.string.enter_suppliers_contact_person_name));
                    etxtSuppliersContactPerson.requestFocus();
                } else if (suppliers_cell.isEmpty()) {
                    etxtSuppliersCell.setError(getString(R.string.enter_suppliers_cell));
                    etxtSuppliersCell.requestFocus();
                } else if (suppliers_email.isEmpty() || !suppliers_email.contains("@") || !suppliers_email.contains(".")) {
                    etxtSuppliersEmail.setError(getString(R.string.enter_valid_email));
                    etxtSuppliersEmail.requestFocus();
                } else if (suppliers_address.isEmpty()) {
                    etxtSuppliersAddress.setError(getString(R.string.enter_suppliers_address));
                    etxtSuppliersAddress.requestFocus();
                } else {

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(EditSuppliersActivity.this);
                    databaseAccess.open();

                    boolean check = databaseAccess.updateSuppliers(get_suppliers_id, suppliers_name, suppliers_contact_person, suppliers_cell, suppliers_email, suppliers_address);

                    if (check) {
                        Toasty.success(EditSuppliersActivity.this, R.string.update_successfully, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditSuppliersActivity.this, SuppliersActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {

                        Toasty.error(EditSuppliersActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

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
