package com.app.smartpos.product;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ajts.androidmads.library.ExcelToSQLite;
import com.app.smartpos.HomeActivity;
import com.app.smartpos.R;
import com.app.smartpos.database.DatabaseAccess;
import com.app.smartpos.database.DatabaseOpenHelper;
import com.app.smartpos.pos.ProductCart;
import com.app.smartpos.utils.BaseActivity;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

public class AddProductActivity extends BaseActivity {


    ProgressDialog loading;

    public static EditText etxtProductCode;
    EditText etxtProductName, etxtProductCategory, etxtProductDescription, etxtProductBuyPrice, etxtProductSellPrice, etxtProductStock, etxtProductSupplier, etxtProdcutWeightUnit, etxtProductWeight;
    TextView txtAddProdcut, txtChooseImage;
    ImageView imgProduct, imgScanCode;
    String mediaPath, encodedImage = "N/A";
    ArrayAdapter<String> categoryAdapter, supplierAdapter, weightUnitAdapter;
    List<String> categoryNames, supplierNames, weightUnitNames;

    String selectedCategoryID, selectedSupplierID, selectedWeightUnitID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);


        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.add_product);

        etxtProductName = findViewById(R.id.etxt_product_name);
        etxtProductCode = findViewById(R.id.etxt_product_code);
        etxtProductCategory = findViewById(R.id.etxt_product_category);
        etxtProductDescription = findViewById(R.id.etxt_product_description);
        etxtProductBuyPrice = findViewById(R.id.etxt_buy_price);
        etxtProductSellPrice = findViewById(R.id.etxt_product_sell_price);
        etxtProductStock = findViewById(R.id.etxt_product_stock);
        etxtProductSupplier = findViewById(R.id.etxt_supplier);
        etxtProdcutWeightUnit = findViewById(R.id.etxt_product_weight_unit);
        etxtProductWeight = findViewById(R.id.etxt_product_weight);

        txtAddProdcut = findViewById(R.id.txt_add_product);
        imgProduct = findViewById(R.id.image_product);
        imgScanCode = findViewById(R.id.img_scan_code);
        txtChooseImage = findViewById(R.id.txt_choose_image);

        imgScanCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddProductActivity.this, ScannerViewActivity.class);
                startActivity(intent);
            }
        });


        txtChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AddProductActivity.this, ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                startActivityForResult(intent, 1213);
            }
        });

        imgProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AddProductActivity.this, ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                startActivityForResult(intent, 1213);
            }
        });


        categoryNames = new ArrayList<>();
        supplierNames = new ArrayList<>();
        weightUnitNames = new ArrayList<>();


        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddProductActivity.this);
        databaseAccess.open();

        //get data from local database
        final List<HashMap<String, String>> productCategory, productSupplier, weightUnit;
        productCategory = databaseAccess.getProductCategory();

        //need to open database in every query to get data from local db
        databaseAccess.open();
        productSupplier = databaseAccess.getProductSupplier();


        //need to open database in every query to get data from local db
        databaseAccess.open();
        weightUnit = databaseAccess.getWeightUnit();

        for (int i = 0; i < productCategory.size(); i++) {

            // Get the ID of selected Country
            categoryNames.add(productCategory.get(i).get("category_name"));

        }

        for (int i = 0; i < productSupplier.size(); i++) {

            // Get the ID of selected supplier
            supplierNames.add(productSupplier.get(i).get("suppliers_name"));

        }

        for (int i = 0; i < weightUnit.size(); i++) {

            // Get the ID of selected weight unit
            weightUnitNames.add(weightUnit.get(i).get("weight_unit"));

        }


        etxtProductCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryAdapter = new ArrayAdapter<String>(AddProductActivity.this, android.R.layout.simple_list_item_1);
                categoryAdapter.addAll(categoryNames);

                AlertDialog.Builder dialog = new AlertDialog.Builder(AddProductActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialog_button = dialogView.findViewById(R.id.dialog_button);
                EditText dialog_input = dialogView.findViewById(R.id.dialog_input);
                TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
                ListView dialog_list = dialogView.findViewById(R.id.dialog_list);


                dialog_title.setText(R.string.product_category);
                dialog_list.setVerticalScrollBarEnabled(true);
                dialog_list.setAdapter(categoryAdapter);

                dialog_input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        categoryAdapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });


                final AlertDialog alertDialog = dialog.create();

                dialog_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();


                dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        alertDialog.dismiss();
                        final String selectedItem = categoryAdapter.getItem(position);

                        String category_id = "0";
                        etxtProductCategory.setText(selectedItem);


                        for (int i = 0; i < categoryNames.size(); i++) {
                            if (categoryNames.get(i).equalsIgnoreCase(selectedItem)) {
                                // Get the ID of selected Country
                                category_id = productCategory.get(i).get("category_id");
                            }
                        }


                        selectedCategoryID = category_id;
                        Log.d("category_id", category_id);
                    }
                });
            }
        });


        etxtProductSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supplierAdapter = new ArrayAdapter<String>(AddProductActivity.this, android.R.layout.simple_list_item_1);
                supplierAdapter.addAll(supplierNames);

                AlertDialog.Builder dialog = new AlertDialog.Builder(AddProductActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialog_button = (Button) dialogView.findViewById(R.id.dialog_button);
                EditText dialog_input = (EditText) dialogView.findViewById(R.id.dialog_input);
                TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                ListView dialog_list = (ListView) dialogView.findViewById(R.id.dialog_list);

//                dialog_title.setText(getString(R.string.zone));
                dialog_title.setText(R.string.suppliers);
                dialog_list.setVerticalScrollBarEnabled(true);
                dialog_list.setAdapter(supplierAdapter);

                dialog_input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        supplierAdapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });


                final AlertDialog alertDialog = dialog.create();

                dialog_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();


                dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        alertDialog.dismiss();
                        final String selectedItem = supplierAdapter.getItem(position);

                        String supplier_id = "0";
                        etxtProductSupplier.setText(selectedItem);


                        for (int i = 0; i < supplierNames.size(); i++) {
                            if (supplierNames.get(i).equalsIgnoreCase(selectedItem)) {
                                // Get the ID of selected Country
                                supplier_id = productSupplier.get(i).get("suppliers_id");
                            }
                        }


                        selectedSupplierID = supplier_id;

                    }
                });
            }
        });


        etxtProdcutWeightUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weightUnitAdapter = new ArrayAdapter<String>(AddProductActivity.this, android.R.layout.simple_list_item_1);
                weightUnitAdapter.addAll(weightUnitNames);

                AlertDialog.Builder dialog = new AlertDialog.Builder(AddProductActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialog_button = (Button) dialogView.findViewById(R.id.dialog_button);
                EditText dialog_input = (EditText) dialogView.findViewById(R.id.dialog_input);
                TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                ListView dialog_list = (ListView) dialogView.findViewById(R.id.dialog_list);


                dialog_title.setText(R.string.product_weight_unit);
                dialog_list.setVerticalScrollBarEnabled(true);
                dialog_list.setAdapter(weightUnitAdapter);

                dialog_input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        weightUnitAdapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });


                final AlertDialog alertDialog = dialog.create();

                dialog_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();


                dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        alertDialog.dismiss();
                        final String selectedItem = weightUnitAdapter.getItem(position);

                        String weight_unit_id = "0";
                        etxtProdcutWeightUnit.setText(selectedItem);


                        for (int i = 0; i < weightUnitNames.size(); i++) {
                            if (weightUnitNames.get(i).equalsIgnoreCase(selectedItem)) {
                                // Get the ID of selected Country
                                weight_unit_id = weightUnit.get(i).get("weight_id");
                            }
                        }


                        selectedWeightUnitID = weight_unit_id;

                        Log.d("weight_unit", selectedWeightUnitID);
                    }
                });
            }
        });


        txtAddProdcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String product_name = etxtProductName.getText().toString();
                String product_code = etxtProductCode.getText().toString();
                String product_category_name = etxtProductCategory.getText().toString();
                String product_category_id = selectedCategoryID;
                String product_description = etxtProductDescription.getText().toString();
                String product_buy_price = etxtProductBuyPrice.getText().toString();
                String product_sell_price = etxtProductSellPrice.getText().toString();
                String product_stock = etxtProductStock.getText().toString();
                String product_supplier_name = etxtProductSupplier.getText().toString();
                String product_supplier = selectedSupplierID;
                String product_Weight_unit_name = etxtProdcutWeightUnit.getText().toString();
                String product_weight_unit_id = selectedWeightUnitID;
                String product_weight = etxtProductWeight.getText().toString();


                if (product_name.isEmpty()) {
                    etxtProductName.setError(getString(R.string.product_name_cannot_be_empty));
                    etxtProductName.requestFocus();
                }
                else if (product_category_name.isEmpty() || product_category_id.isEmpty()) {
                    etxtProductCategory.setError(getString(R.string.product_category_cannot_be_empty));
                    etxtProductCategory.requestFocus();
                }
               else if (product_sell_price.isEmpty()) {
                    etxtProductSellPrice.setError(getString(R.string.product_sell_price_cannot_be_empty));
                    etxtProductSellPrice.requestFocus();
                } else if (product_Weight_unit_name.isEmpty() || product_weight.isEmpty()) {
                    etxtProductWeight.setError(getString(R.string.product_weight_cannot_be_empty));
                    etxtProductWeight.requestFocus();
                } else if (product_stock.isEmpty()) {
                    etxtProductStock.setError(getString(R.string.product_stock_cannot_be_empty));
                    etxtProductStock.requestFocus();
                } else if (product_supplier_name.isEmpty() || product_supplier.isEmpty()) {
                    etxtProductSupplier.setError(getString(R.string.product_supplier_cannot_be_empty));
                    etxtProductSupplier.requestFocus();
                } else {
                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddProductActivity.this);
                    databaseAccess.open();

                    boolean check = databaseAccess.addProduct(product_name, product_code, product_category_id, product_description, product_buy_price, product_sell_price, product_stock, product_supplier, encodedImage, product_weight_unit_id, product_weight);

                    if (check) {
                        Toasty.success(AddProductActivity.this, R.string.product_successfully_added, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddProductActivity.this, ProductActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {

                        Toasty.error(AddProductActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

                    }


                }

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            // When an Image is picked
            if (requestCode == 1213 && resultCode == RESULT_OK && null != data) {


                mediaPath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
                Bitmap selectedImage = BitmapFactory.decodeFile(mediaPath);
                imgProduct.setImageBitmap(selectedImage);

                encodedImage = encodeImage(selectedImage);


            }


        } catch (Exception e) {
            Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
        }

    }


    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
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

                //onImport();

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //import data from Excel xls file
    public void onImport(String path) {

        String directory_path = path;
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddProductActivity.this);
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

                loading = new ProgressDialog(AddProductActivity.this);
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
                        Toasty.success(AddProductActivity.this, R.string.data_successfully_imported, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddProductActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();


                    }
                }, 5000);


            }

            @Override
            public void onError(Exception e) {

                loading.dismiss();
                Log.d("Error : ", "" + e.getMessage());
                Toasty.error(AddProductActivity.this, R.string.data_import_fail, Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void fileChooser() {
        new ChooserDialog(AddProductActivity.this)


                .displayPath(true)
                .withFilter(false, false, "xls") //filter file type

                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String path, File pathFile) {
//                        Toast.makeText(AddProductActivity.this, "FILE: " + path, Toast.LENGTH_SHORT).show();
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



