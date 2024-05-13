package com.app.smartpos.customers;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ajts.androidmads.library.ExcelToSQLite;
import com.app.smartpos.HomeActivity;
import com.app.smartpos.R;
import com.app.smartpos.database.DatabaseAccess;
import com.app.smartpos.database.DatabaseOpenHelper;
import com.app.smartpos.utils.BaseActivity;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;

import es.dmoral.toasty.Toasty;

public class AddCustomersActivity extends BaseActivity {


    ProgressDialog loading;
    EditText etxtCustomerName, etxtAddress, etxtCustomerCell, etxtCustomerEmail;
    TextView txtAddCustomer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customers);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.add_customer);


        etxtCustomerName = findViewById(R.id.etxt_customer_name);
        etxtCustomerCell = findViewById(R.id.etxt_customer_cell);
        etxtCustomerEmail = findViewById(R.id.etxt_email);
        etxtAddress = findViewById(R.id.etxt_address);

        txtAddCustomer = findViewById(R.id.txt_add_customer);


        txtAddCustomer.setOnClickListener(new View.OnClickListener() {
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
                }

                else if (customer_email.isEmpty() || !customer_email.contains("@") || !customer_email.contains(".")) {
                    etxtCustomerEmail.setError(getString(R.string.enter_valid_email));
                    etxtCustomerEmail.requestFocus();
                }

                else if (customer_address.isEmpty()) {
                    etxtAddress.setError(getString(R.string.enter_customer_address));
                    etxtAddress.requestFocus();
                } else {

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddCustomersActivity.this);
                    databaseAccess.open();

                    boolean check = databaseAccess.addCustomer(customer_name, customer_cell, customer_email, customer_address);

                    if (check) {
                        Toasty.success(AddCustomersActivity.this, R.string.customer_successfully_added, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddCustomersActivity.this, CustomersActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {

                        Toasty.error(AddCustomersActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

                    }
                }


            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_product_menu, menu);
        return true;
    }


    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.menu_import:


                fileChooser();
                return true;




            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //import data from Excel xls file
    public void onImport(String path) {

        String directory_path = path;
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddCustomersActivity.this);
        databaseAccess.open();


        File file = new File(directory_path);
        if (!file.exists()) {
            Toast.makeText(this, R.string.no_file_found, Toast.LENGTH_SHORT).show();
            return;
        }

        // Is used to import data from excel without dropping table
//         ExcelToSQLite excelToSQLite = new ExcelToSQLite(getApplicationContext(),DatabaseOpenHelper.DATABASE_NAME);

        // if you want to add column in excel and import into DB, you must drop the table
        ExcelToSQLite excelToSQLite = new ExcelToSQLite(getApplicationContext(), DatabaseOpenHelper.DATABASE_NAME, false);
        // Import EXCEL FILE to SQLite
        excelToSQLite.importFromFile(directory_path, new ExcelToSQLite.ImportListener() {
            @Override
            public void onStart() {

                loading = new ProgressDialog(AddCustomersActivity.this);
                loading.setMessage(getString(R.string.data_importing_please_wait));
                loading.setCancelable(false);
                loading.show();

            }

            @Override
            public void onCompleted(String dbName) {


                Handler mHand = new Handler();
                mHand.postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        loading.dismiss();
                        Toasty.success(AddCustomersActivity.this, R.string.data_successfully_imported, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddCustomersActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();


                    }
                }, 5000);


            }

            @Override
            public void onError(Exception e) {

                loading.dismiss();
                Log.d("Error : ", "" + e.getMessage());
                Toasty.error(AddCustomersActivity.this, R.string.data_import_fail, Toast.LENGTH_SHORT).show();
            }
        });

    }



    public void fileChooser() {
        new ChooserDialog(AddCustomersActivity.this)


                .displayPath(true)
                .withFilter(false, false, "xls") //filter file type

                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String path, File pathFile) {
//                        Toast.makeText(AddCustomersActivity.this, "FILE: " + path, Toast.LENGTH_SHORT).show();
                        onImport(path);
                    }
                })
                // to handle the back key pressed or clicked outside the dialog:
                .withOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        Log.d("CANCEL", "CANCEL");
                        dialog.cancel(); // MUST have
                    }
                })
                .build()
                .show();
    }


}
