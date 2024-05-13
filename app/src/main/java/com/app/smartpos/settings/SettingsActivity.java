package com.app.smartpos.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.cardview.widget.CardView;

import com.app.smartpos.R;
import com.app.smartpos.settings.backup.BackupActivity;
import com.app.smartpos.settings.categories.CategoriesActivity;
import com.app.smartpos.settings.order_type.OrderTypeActivity;
import com.app.smartpos.settings.payment_method.PaymentMethodActivity;
import com.app.smartpos.settings.shop.ShopInformationActivity;
import com.app.smartpos.settings.unit.UnitActivity;
import com.app.smartpos.utils.BaseActivity;
import com.app.smartpos.utils.Utils;

public class SettingsActivity extends BaseActivity {


    CardView cardShopInfo, cardBackup,cardCategory,cardPaymentMethod,cardOrderType,cardUnit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.action_settings);


        cardShopInfo = findViewById(R.id.card_shop_info);
        cardBackup = findViewById(R.id.card_backup);
        cardCategory=findViewById(R.id.card_category);
        cardPaymentMethod=findViewById(R.id.card_payment_method);
        cardOrderType=findViewById(R.id.card_order_type);
        cardUnit=findViewById(R.id.card_unit);


        //for interstitial ads show
//        Utils utils=new Utils();
//        utils.interstitialAdsShow(SettingsActivity.this);

        cardShopInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SettingsActivity.this, ShopInformationActivity.class);
                startActivity(intent);
            }
        });



        cardCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SettingsActivity.this, CategoriesActivity.class);
                startActivity(intent);
            }
        });


        cardOrderType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SettingsActivity.this, OrderTypeActivity.class);
                startActivity(intent);
            }
        });

        cardUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SettingsActivity.this, UnitActivity.class);
                startActivity(intent);
            }
        });


        cardPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SettingsActivity.this, PaymentMethodActivity.class);
                startActivity(intent);
            }
        });


        cardBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SettingsActivity.this, BackupActivity.class);
                startActivity(intent);
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
