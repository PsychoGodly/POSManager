package com.app.smartpos;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.app.smartpos.customers.CustomersActivity;
import com.app.smartpos.expense.ExpenseActivity;
import com.app.smartpos.orders.OrdersActivity;
import com.app.smartpos.pos.PosActivity;
import com.app.smartpos.product.ProductActivity;
import com.app.smartpos.report.ReportActivity;
import com.app.smartpos.settings.SettingsActivity;
import com.app.smartpos.suppliers.SuppliersActivity;
import com.app.smartpos.utils.BaseActivity;
import com.app.smartpos.utils.LocaleManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class HomeActivity extends BaseActivity {


    CardView cardCustomers, cardProducts, cardSupplier, cardPos, cardOrderList, cardReport, cardSettings, cardExpense;
    //for double back press to exit
    private static final int TIME_DELAY = 2000;
    private static long back_pressed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_home);

        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_gradient));
        getSupportActionBar().setElevation(0);

        cardCustomers = findViewById(R.id.card_customers);
        cardSupplier = findViewById(R.id.card_suppliers);
        cardProducts = findViewById(R.id.card_products);
        cardPos = findViewById(R.id.card_pos);
        cardOrderList = findViewById(R.id.card_order_list);
        cardReport = findViewById(R.id.card_report);
        cardSettings = findViewById(R.id.card_settings);
        cardExpense = findViewById(R.id.card_expense);


        if (Build.VERSION.SDK_INT >= 23) //Android MarshMellow Version or above
        {
            requestPermission();

        }

//        Admob initialization
        MobileAds.initialize(this, initializationStatus -> {
        });
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        cardCustomers.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CustomersActivity.class);
            startActivity(intent);


        });

        cardSupplier.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SuppliersActivity.class);
            startActivity(intent);


        });


        cardProducts.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProductActivity.class);
            startActivity(intent);


        });


        cardPos.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, PosActivity.class);
            startActivity(intent);


        });

        cardOrderList.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, OrdersActivity.class);
            startActivity(intent);


        });


        cardReport.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ReportActivity.class);
            startActivity(intent);
        });


        cardExpense.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ExpenseActivity.class);
            startActivity(intent);
        });


        cardSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.language_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {




            case R.id.local_french:
                setNewLocale(this, LocaleManager.FRENCH);
                return true;


            case R.id.local_english:
                setNewLocale(this, LocaleManager.ENGLISH);
                return true;



        }

        return super.onOptionsItemSelected(item);
    }


    private void setNewLocale(AppCompatActivity mContext, @LocaleManager.LocaleDef String language) {
        LocaleManager.setNewLocale(this, language);
        Intent intent = mContext.getIntent();
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    

    //double backpress to exit
    @Override
    public void onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {

            finishAffinity();

        } else {
            Toasty.info(this, R.string.press_once_again_to_exit,
                    Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }






    private void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                          //do your action
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings

                        }
                    }


                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(error -> Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
    }
}
