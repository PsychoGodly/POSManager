package com.app.smartpos.orders;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.smartpos.R;
import com.app.smartpos.adapter.OrderDetailsAdapter;
import com.app.smartpos.database.DatabaseAccess;
import com.app.smartpos.pdf_report.BarCodeEncoder;
import com.app.smartpos.pdf_report.TemplatePDF;
import com.app.smartpos.utils.BaseActivity;
import com.app.smartpos.utils.IPrintToPrinter;
import com.app.smartpos.utils.PrefMng;
import com.app.smartpos.utils.Tools;
import com.app.smartpos.utils.WoosimPrnMng;
import com.app.smartpos.utils.printerFactory;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class OrderDetailsActivity extends BaseActivity {


    private RecyclerView recyclerView;
    private OrderDetailsAdapter orderDetailsAdapter;

    ImageView imgNoProduct;
    TextView txtNoProducts, txtTotalPrice, txtTax, txtDiscount, txtTotalCost;
    String order_id, order_date, order_time, customer_name, tax, discount;
    Button btnPdfReceipt, btnThermalPrinter;

    double total_price, calculated_total_price, getTax, getDiscount;
    //how many headers or column you need, add here by using ,
    //headers and get clients para meter must be equal
    private String[] header = {"Description", "Price"};

    Bitmap bm = null;
    String currency, shop_name, shop_contact, shop_email, shop_address, longText, shortText;

    private TemplatePDF templatePDF;

    private static final int REQUEST_CONNECT = 100;
    private WoosimPrnMng mPrnMng = null;
    DecimalFormat f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        recyclerView = findViewById(R.id.recycler);
        imgNoProduct = findViewById(R.id.image_no_product);
        txtTotalPrice = findViewById(R.id.txt_total_price);
        txtTax = findViewById(R.id.txt_tax);
        txtDiscount = findViewById(R.id.txt_discount);
        txtTotalCost = findViewById(R.id.txt_total_cost);
        btnPdfReceipt = findViewById(R.id.btn_pdf_receipt);
        btnThermalPrinter = findViewById(R.id.btn_thermal_printer);

        f = new DecimalFormat("#0.00");
        txtNoProducts = findViewById(R.id.txt_no_products);
        order_id = getIntent().getExtras().getString("order_id");
        order_date = getIntent().getExtras().getString("order_date");
        order_time = getIntent().getExtras().getString("order_time");
        customer_name = getIntent().getExtras().getString("customer_name");

        tax = getIntent().getExtras().getString("tax");
        discount = getIntent().getExtras().getString("discount");

        imgNoProduct.setVisibility(View.GONE);
        txtNoProducts.setVisibility(View.GONE);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.order_details);


        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrderDetailsActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
        recyclerView.setHasFixedSize(true);

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(OrderDetailsActivity.this);
        databaseAccess.open();


        //get data from local database
        List<HashMap<String, String>> orderDetailsList;
        orderDetailsList = databaseAccess.getOrderDetailsList(order_id);

        if (orderDetailsList.isEmpty()) {
            //if no data in local db, then load data from server
            Toasty.info(OrderDetailsActivity.this, R.string.no_data_found, Toast.LENGTH_SHORT).show();
        } else {
            orderDetailsAdapter = new OrderDetailsAdapter(OrderDetailsActivity.this, orderDetailsList);
            recyclerView.setAdapter(orderDetailsAdapter);
        }


        databaseAccess.open();
        //get data from local database
        List<HashMap<String, String>> shopData;
        shopData = databaseAccess.getShopInformation();

        shop_name = shopData.get(0).get("shop_name");
        shop_contact = shopData.get(0).get("shop_contact");
        shop_email = shopData.get(0).get("shop_email");
        shop_address = shopData.get(0).get("shop_address");
        currency = shopData.get(0).get("shop_currency");


        databaseAccess.open();
        total_price = databaseAccess.totalOrderPrice(order_id);
        getTax = Double.parseDouble(tax);
        getDiscount = Double.parseDouble(discount);


        txtTax.setText(getString(R.string.total_tax) + " : " + currency + f.format(getTax));
        txtDiscount.setText(getString(R.string.discount) + " : " + currency + f.format(getDiscount));

        calculated_total_price = total_price + getTax - getDiscount;
        txtTotalPrice.setText(getString(R.string.sub_total) + currency + f.format(total_price));
        txtTotalCost.setText(getString(R.string.total_price) + currency + f.format(calculated_total_price));


        //for pdf report
        shortText = "Customer Name: Mr/Mrs. " + customer_name;
        longText = "Thanks for purchase. Visit again";


        templatePDF = new TemplatePDF(getApplicationContext());
        templatePDF.openDocument();
        templatePDF.addMetaData("Order Receipt", "Order Receipt", "POS Manager");
        templatePDF.addTitle(shop_name, shop_address + "\n Email: " + shop_email + "\nContact: " + shop_contact + "\nInvoice ID:" + order_id, order_time + " " + order_date);
        templatePDF.addParagraph(shortText);


        BarCodeEncoder qrCodeEncoder = new BarCodeEncoder();
        try {
            bm = qrCodeEncoder.encodeAsBitmap(order_id, BarcodeFormat.CODE_128, 600, 300);
        } catch (WriterException e) {
            Log.d("Data", e.toString());
        }


        btnPdfReceipt.setOnClickListener(v -> {

            templatePDF.createTable(header, getOrdersData());
            templatePDF.addRightParagraph(longText);
            templatePDF.addImage(bm);
            templatePDF.closeDocument();
            templatePDF.viewPDF();

        });


        btnThermalPrinter.setOnClickListener(v -> {

            //Check if the Bluetooth is available and on.
            if (!Tools.isBlueToothOn(OrderDetailsActivity.this)) return;
            PrefMng.saveActivePrinter(OrderDetailsActivity.this, PrefMng.PRN_WOOSIM_SELECTED);
            //Pick a Bluetooth device
            Intent i = new Intent(OrderDetailsActivity.this, DeviceListActivity.class);
            startActivityForResult(i, REQUEST_CONNECT);
        });

    }


    //for pdf
    private ArrayList<String[]> getOrdersData() {
        ArrayList<String[]> rows = new ArrayList<>();

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(OrderDetailsActivity.this);
        databaseAccess.open();


        //get data from local database
        List<HashMap<String, String>> orderDetailsList;
        orderDetailsList = databaseAccess.getOrderDetailsList(order_id);
        String name, price, qty, weight;
        double cost_total;

        for (int i = 0; i < orderDetailsList.size(); i++) {
            name = orderDetailsList.get(i).get("product_name");
            price = orderDetailsList.get(i).get("product_price");
            qty = orderDetailsList.get(i).get("product_qty");
            weight = orderDetailsList.get(i).get("product_weight");

            cost_total = Integer.parseInt(qty) * Double.parseDouble(price);

            rows.add(new String[]{name + "\n" + weight + "\n" + "(" + qty + "x" + currency + price + ")", currency + f.format(cost_total)});


        }
        rows.add(new String[]{"..........................................", ".................................."});
        rows.add(new String[]{"Sub Total: ", currency + f.format(total_price)});
        rows.add(new String[]{"Total Tax: ", currency + f.format(getTax)});
        rows.add(new String[]{"Discount: ", currency + f.format(getDiscount)});
        rows.add(new String[]{"..........................................", ".................................."});
        rows.add(new String[]{"Total Price: ", currency + f.format(calculated_total_price)});
//        you can add more row above format
        return rows;
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CONNECT && resultCode == RESULT_OK) {
            try {
                //Get device address to print to.
                String blutoothAddr = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                //The interface to print text to thermal printers.
                IPrintToPrinter testPrinter = new TestPrinter(this, shop_name, shop_address, shop_email, shop_contact, order_id, order_date, order_time, shortText, longText, total_price, calculated_total_price, tax, discount, currency);
                //Connect to the printer and after successful connection issue the print command.
                mPrnMng = printerFactory.createPrnMng(this, blutoothAddr, testPrinter);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        if (mPrnMng != null) mPrnMng.releaseAllocatoins();
        super.onDestroy();
    }


}

