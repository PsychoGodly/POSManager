package com.app.smartpos.settings.shop;

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
import com.app.smartpos.settings.SettingsActivity;
import com.app.smartpos.utils.BaseActivity;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class ShopInformationActivity extends BaseActivity {


    TextView txtUpdate, txtShopEdit;
    EditText etxtShopName, etxtTax,etxtShopContact, etxtShopEmail, etxtShopAddress, etxtShopCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_information);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.shop_information);


        etxtShopName = findViewById(R.id.etxt_shop_name);
        etxtShopContact = findViewById(R.id.etxt_shop_contact);
        etxtShopEmail = findViewById(R.id.etxt_shop_email);
        etxtShopAddress = findViewById(R.id.etxt_shop_address);
        etxtShopCurrency = findViewById(R.id.etxt_shop_currency);
        txtUpdate = findViewById(R.id.txt_update);
        txtShopEdit = findViewById(R.id.txt_shop_edit);
        txtShopEdit = findViewById(R.id.txt_shop_edit);
        etxtTax=findViewById(R.id.etxt_tax);


        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(ShopInformationActivity.this);
        databaseAccess.open();

        //get data from local database
        List<HashMap<String, String>> shopData;
        shopData = databaseAccess.getShopInformation();

        String shop_name = shopData.get(0).get("shop_name");
        String shop_contact = shopData.get(0).get("shop_contact");
        String shop_email = shopData.get(0).get("shop_email");
        String shop_address = shopData.get(0).get("shop_address");
        String shop_currency = shopData.get(0).get("shop_currency");
        String tax = shopData.get(0).get("tax");

        etxtShopName.setText(shop_name);
        etxtShopContact.setText(shop_contact);
        etxtShopEmail.setText(shop_email);
        etxtShopAddress.setText(shop_address);
        etxtShopCurrency.setText(shop_currency);
        etxtTax.setText(tax);

        etxtShopName.setEnabled(false);
        etxtShopContact.setEnabled(false);
        etxtShopEmail.setEnabled(false);
        etxtShopAddress.setEnabled(false);
        etxtShopCurrency.setEnabled(false);
        etxtTax.setEnabled(false);

        txtUpdate.setVisibility(View.GONE);


        txtShopEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etxtShopName.setEnabled(true);
                etxtShopContact.setEnabled(true);
                etxtShopEmail.setEnabled(true);
                etxtShopAddress.setEnabled(true);
                etxtShopCurrency.setEnabled(true);
                etxtTax.setEnabled(true);

                etxtShopName.setTextColor(Color.RED);
                etxtShopContact.setTextColor(Color.RED);
                etxtShopEmail.setTextColor(Color.RED);
                etxtShopAddress.setTextColor(Color.RED);
                etxtShopCurrency.setTextColor(Color.RED);
                etxtTax.setTextColor(Color.RED);

                txtUpdate.setVisibility(View.VISIBLE);
                txtShopEdit.setVisibility(View.GONE);

            }
        });


        txtUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                Toasty.warning(ShopInformationActivity.this, "Shop Information can't be change. Please purchase from codecanyon. Thank you", Toast.LENGTH_LONG).show();

                String shop_name = etxtShopName.getText().toString().trim();
                String shop_contact = etxtShopContact.getText().toString().trim();
                String shop_email = etxtShopEmail.getText().toString().trim();
                String shop_address = etxtShopAddress.getText().toString().trim();
                String shop_currency = etxtShopCurrency.getText().toString().trim();
                String tax = etxtTax.getText().toString().trim();

                if (shop_name.isEmpty()) {
                    etxtShopName.setError(getString(R.string.shop_name_cannot_be_empty));
                    etxtShopName.requestFocus();
                } else if (shop_contact.isEmpty()) {
                    etxtShopContact.setError(getString(R.string.shop_contact_cannot_be_empty));
                    etxtShopContact.requestFocus();
                } else if (shop_email.isEmpty() || !shop_email.contains("@") || !shop_email.contains(".")) {
                    etxtShopEmail.setError(getString(R.string.enter_valid_email));
                    etxtShopEmail.requestFocus();
                } else if (shop_address.isEmpty()) {
                    etxtShopAddress.setError(getString(R.string.shop_address_cannot_be_empty));
                    etxtShopAddress.requestFocus();
                } else if (shop_currency.isEmpty()) {
                    etxtShopCurrency.setError(getString(R.string.shop_currency_cannot_be_empty));
                    etxtShopCurrency.requestFocus();
                }
                else if (tax.isEmpty()) {
                    etxtTax.setError(getString(R.string.tax_in_percentage));
                    etxtTax.requestFocus();
                }

                else {

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(ShopInformationActivity.this);
                    databaseAccess.open();

                    boolean check = databaseAccess.updateShopInformation(shop_name, shop_contact, shop_email, shop_address, shop_currency,tax);

                    if (check) {
                        Toasty.success(ShopInformationActivity.this, R.string.shop_information_updated_successfully, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ShopInformationActivity.this, SettingsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {

                        Toasty.error(ShopInformationActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

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
