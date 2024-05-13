package com.app.smartpos.suppliers;

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
import com.app.smartpos.customers.AddCustomersActivity;
import com.app.smartpos.database.DatabaseAccess;
import com.app.smartpos.database.DatabaseOpenHelper;
import com.app.smartpos.utils.BaseActivity;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;

import es.dmoral.toasty.Toasty;

public class AddSuppliersActivity extends BaseActivity {


    ProgressDialog loading;
    EditText etxtSuppliersName,etxtSuppliersContactPerson,etxtSuppliersAddress,etxtSuppliersCell,etxtSuppliersEmail;
    TextView txtAddSuppliers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_suppliers);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.add_suppliers);

        etxtSuppliersName=findViewById(R.id.etxt_supplier_name);
        etxtSuppliersContactPerson=findViewById(R.id.etxt_supplier_contact_name);
        etxtSuppliersCell=findViewById(R.id.etxt_supplier_cell);
        etxtSuppliersEmail=findViewById(R.id.etxt_supplier_email);
        etxtSuppliersAddress=findViewById(R.id.etxt_supplier_address);


        txtAddSuppliers=findViewById(R.id.txt_add_supplier);


        txtAddSuppliers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String suppliers_name=etxtSuppliersName.getText().toString().trim();
                String suppliers_contact_person=etxtSuppliersContactPerson.getText().toString().trim();
                String suppliers_cell=etxtSuppliersCell.getText().toString().trim();
                String suppliers_email=etxtSuppliersEmail.getText().toString().trim();
                String suppliers_address=etxtSuppliersAddress.getText().toString().trim();


                if (suppliers_name.isEmpty())
                {
                    etxtSuppliersName.setError(getString(R.string.enter_suppliers_name));
                    etxtSuppliersName.requestFocus();
                }

               else if (suppliers_contact_person.isEmpty())
                {
                    etxtSuppliersContactPerson.setError(getString(R.string.enter_suppliers_contact_person_name));
                    etxtSuppliersContactPerson.requestFocus();
                }

                else if (suppliers_cell.isEmpty())
                {
                    etxtSuppliersCell.setError(getString(R.string.enter_suppliers_cell));
                    etxtSuppliersCell.requestFocus();
                }


                else if (suppliers_email.isEmpty() || !suppliers_email.contains("@") || !suppliers_email.contains("."))
                {
                    etxtSuppliersEmail.setError(getString(R.string.enter_valid_email));
                    etxtSuppliersEmail.requestFocus();
                }

                else if (suppliers_address.isEmpty())
                {
                    etxtSuppliersAddress.setError(getString(R.string.enter_suppliers_address));
                    etxtSuppliersAddress.requestFocus();
                }

                else
                {

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddSuppliersActivity.this);
                    databaseAccess.open();

                    boolean check=databaseAccess.addSuppliers(suppliers_name,suppliers_contact_person,suppliers_cell,suppliers_email,suppliers_address);

                    if (check)
                    {
                        Toasty.success(AddSuppliersActivity.this, R.string.suppliers_successfully_added, Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(AddSuppliersActivity.this, SuppliersActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                    else {

                        Toasty.error(AddSuppliersActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

                    }
                }




            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_supplier_menu, menu);
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
            case R.id.menu_import_supplier:

                fileChooser();

            default:
                return super.onOptionsItemSelected(item);
        }
    }








    //import data from Excel xls file
    public void onImport(String path) {

        String directory_path = path;
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddSuppliersActivity.this);
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

                loading = new ProgressDialog(AddSuppliersActivity.this);
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
                        Toasty.success(AddSuppliersActivity.this, R.string.data_successfully_imported, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddSuppliersActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();


                    }
                }, 5000);


            }

            @Override
            public void onError(Exception e) {

                loading.dismiss();
                Log.d("Error : ", "" + e.getMessage());
                Toasty.error(AddSuppliersActivity.this, R.string.data_import_fail, Toast.LENGTH_SHORT).show();
            }
        });

    }



    public void fileChooser() {
        new ChooserDialog(AddSuppliersActivity.this)


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
