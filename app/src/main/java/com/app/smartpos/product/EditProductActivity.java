package com.app.smartpos.product;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
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

import com.app.smartpos.R;
import com.app.smartpos.database.DatabaseAccess;
import com.app.smartpos.utils.BaseActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

public class EditProductActivity extends BaseActivity {

    public static EditText etxtProductCode;
    EditText etxtProductName, etxtProductCategory, etxtProductDescription, etxtProductBuyPrice, etxtProductSellPrice, etxtProductStock, etxtProductSupplier, etxtProdcutWeightUnit, etxtProductWeight;
    TextView txtUpdate, txtChooseImage, txtEditProduct;
    ImageView imgProduct, imgScanCode;
    String mediaPath, encodedImage = "N/A";
    ArrayAdapter<String> categoryAdapter, supplierAdapter, weightUnitAdapter;
    List<String> categoryNames, supplierNames, weightUnitNames;

    String selectedCategoryID, selectedSupplierID, selectedWeightUnitID, productID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.product_details);

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
        txtEditProduct = findViewById(R.id.txt_edit_product);

        txtUpdate = findViewById(R.id.txt_update);
        txtChooseImage = findViewById(R.id.txt_choose_image);
        imgProduct = findViewById(R.id.image_product);
        imgScanCode = findViewById(R.id.img_scan_code);

        txtEditProduct = findViewById(R.id.txt_edit_product);

        productID = getIntent().getExtras().getString("product_id");


        etxtProductName.setEnabled(false);
        etxtProductCode.setEnabled(false);
        etxtProductCategory.setEnabled(false);
        etxtProductDescription.setEnabled(false);
        etxtProductBuyPrice.setEnabled(false);
        etxtProductSellPrice.setEnabled(false);
        etxtProductSupplier.setEnabled(false);
        etxtProdcutWeightUnit.setEnabled(false);
        etxtProductWeight.setEnabled(false);
        txtChooseImage.setEnabled(false);
        etxtProductStock.setEnabled(false);
        imgProduct.setEnabled(false);
        imgScanCode.setEnabled(false);

        txtUpdate.setVisibility(View.GONE);


        imgProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(EditProductActivity.this, ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                startActivityForResult(intent, 1213);
            }
        });


        txtChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(EditProductActivity.this, ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                startActivityForResult(intent, 1213);
            }
        });


        imgScanCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imgScanCode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(EditProductActivity.this, EditProductScannerViewActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });

        categoryNames = new ArrayList<>();
        supplierNames = new ArrayList<>();
        weightUnitNames = new ArrayList<>();


        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(EditProductActivity.this);
        databaseAccess.open();

        //get data from local database
        List<HashMap<String, String>> productData;
        productData = databaseAccess.getProductsInfo(productID);
        String product_name = productData.get(0).get("product_name");
        String product_code = productData.get(0).get("product_code");
        String product_category_id = productData.get(0).get("product_category");
        String product_description = productData.get(0).get("product_description");
        String product_buy_price = productData.get(0).get("product_buy_price");
        String product_sell_price = productData.get(0).get("product_sell_price");
        String product_supplier_id = productData.get(0).get("product_supplier");
        String product_image = productData.get(0).get("product_image");

        String product_stock = productData.get(0).get("product_stock");
        String product_weight_unit_id = productData.get(0).get("product_weight_unit_id");
        String product_weight = productData.get(0).get("product_weight");

        etxtProductName.setText(product_name);
        etxtProductCode.setText(product_code);


        databaseAccess.open();
        String category_name = databaseAccess.getCategoryName(product_category_id);
        etxtProductCategory.setText(category_name);

        etxtProductDescription.setText(product_description);
        etxtProductBuyPrice.setText(product_buy_price);
        etxtProductSellPrice.setText(product_sell_price);


        databaseAccess.open();
        String supplier_name = databaseAccess.getSupplierName(product_supplier_id);
        etxtProductSupplier.setText(supplier_name);

        etxtProductStock.setText(product_stock);


        databaseAccess.open();
        String weight_unit_name = databaseAccess.getWeightUnitName(product_weight_unit_id);
        etxtProdcutWeightUnit.setText(weight_unit_name);

        etxtProductWeight.setText(product_weight);

        if (product_image != null) {
            if (product_image.length() < 6) {

                imgProduct.setImageResource(R.drawable.image_placeholder);
            } else {


                byte[] bytes = Base64.decode(product_image, Base64.DEFAULT);
                imgProduct.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

            }
        }


        selectedCategoryID = product_category_id;
        selectedSupplierID = product_supplier_id;
        selectedWeightUnitID = product_weight_unit_id;
        encodedImage = product_image;


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
                categoryAdapter = new ArrayAdapter<String>(EditProductActivity.this, android.R.layout.simple_list_item_1);
                categoryAdapter.addAll(categoryNames);

                AlertDialog.Builder dialog = new AlertDialog.Builder(EditProductActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialog_button = (Button) dialogView.findViewById(R.id.dialog_button);
                EditText dialog_input = (EditText) dialogView.findViewById(R.id.dialog_input);
                TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                ListView dialog_list = (ListView) dialogView.findViewById(R.id.dialog_list);


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
                supplierAdapter = new ArrayAdapter<String>(EditProductActivity.this, android.R.layout.simple_list_item_1);
                supplierAdapter.addAll(supplierNames);

                AlertDialog.Builder dialog = new AlertDialog.Builder(EditProductActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialog_button = (Button) dialogView.findViewById(R.id.dialog_button);
                EditText dialog_input = (EditText) dialogView.findViewById(R.id.dialog_input);
                TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                ListView dialog_list = (ListView) dialogView.findViewById(R.id.dialog_list);


                dialog_title.setText("Suppliers");
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
                weightUnitAdapter = new ArrayAdapter<String>(EditProductActivity.this, android.R.layout.simple_list_item_1);
                weightUnitAdapter.addAll(weightUnitNames);

                AlertDialog.Builder dialog = new AlertDialog.Builder(EditProductActivity.this);
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


        txtEditProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                etxtProductName.setEnabled(true);
                etxtProductCode.setEnabled(true);
                etxtProductCategory.setEnabled(true);
                etxtProductDescription.setEnabled(true);
                etxtProductSellPrice.setEnabled(true);
                etxtProductBuyPrice.setEnabled(true);

                etxtProductSupplier.setEnabled(true);
                etxtProdcutWeightUnit.setEnabled(true);
                etxtProductWeight.setEnabled(true);
                etxtProductStock.setEnabled(true);
                txtChooseImage.setEnabled(true);
                imgProduct.setEnabled(true);
                imgScanCode.setEnabled(true);


                etxtProductName.setTextColor(Color.RED);
                etxtProductCode.setTextColor(Color.RED);
                etxtProductCategory.setTextColor(Color.RED);
                etxtProductDescription.setTextColor(Color.RED);
                etxtProductSellPrice.setTextColor(Color.RED);
                etxtProductBuyPrice.setTextColor(Color.RED);

                etxtProductSupplier.setTextColor(Color.RED);
                etxtProdcutWeightUnit.setTextColor(Color.RED);
                etxtProductWeight.setTextColor(Color.RED);
                etxtProductStock.setTextColor(Color.RED);
                imgProduct.setEnabled(true);
                imgScanCode.setEnabled(true);


                txtUpdate.setVisibility(View.VISIBLE);
                txtEditProduct.setVisibility(View.GONE);


            }
        });


        txtUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String product_name = etxtProductName.getText().toString();
                String product_code = etxtProductCode.getText().toString();
                String product_category = selectedCategoryID;
                String product_description = etxtProductDescription.getText().toString();
                String product_buy_price = etxtProductBuyPrice.getText().toString();
                String product_sell_price = etxtProductSellPrice.getText().toString();
                String product_stock = etxtProductStock.getText().toString();
                String product_supplier = selectedSupplierID;
                String product_weight_unit_id = selectedWeightUnitID;
                String product_weight = etxtProductWeight.getText().toString();


                if (product_name.isEmpty()) {
                    etxtProductName.setError(getString(R.string.product_name_cannot_be_empty));
                    etxtProductName.requestFocus();
                } else if (product_category.isEmpty()) {
                    etxtProductCategory.setError(getString(R.string.product_category_cannot_be_empty));
                    etxtProductCategory.requestFocus();
                } else if (product_sell_price.isEmpty()) {
                    etxtProductSellPrice.setError(getString(R.string.product_sell_price_cannot_be_empty));
                    etxtProductSellPrice.requestFocus();
                } else if (product_stock.isEmpty()) {
                    etxtProductStock.setError(getString(R.string.product_stock_cannot_be_empty));
                    etxtProductStock.requestFocus();
                } else if (product_supplier.isEmpty()) {
                    etxtProductSupplier.setError(getString(R.string.product_supplier_cannot_be_empty));
                    etxtProductSupplier.requestFocus();
                } else if (product_weight.isEmpty()) {
                    etxtProductWeight.setError(getString(R.string.product_weight_cannot_be_empty));
                    etxtProductWeight.requestFocus();
                } else {
                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(EditProductActivity.this);
                    databaseAccess.open();

                    boolean check = databaseAccess.updateProduct(product_name, product_code, product_category, product_description, product_buy_price, product_sell_price, product_stock, product_supplier, encodedImage, product_weight_unit_id, product_weight, productID);

                    if (check) {
                        Toasty.success(EditProductActivity.this, R.string.update_successfully, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditProductActivity.this, ProductActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {

                        Toasty.error(EditProductActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

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
