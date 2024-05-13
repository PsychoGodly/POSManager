package com.app.smartpos.pos;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.smartpos.R;
import com.app.smartpos.adapter.CartAdapter;
import com.app.smartpos.database.DatabaseAccess;
import com.app.smartpos.orders.OrdersActivity;
import com.app.smartpos.utils.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class ProductCart extends BaseActivity {



    private RecyclerView recyclerView;
    CartAdapter productCartAdapter;
    ImageView imgNoProduct;
    Button btnSubmitOrder;
    TextView txt_no_product,txt_total_price;
    LinearLayout linearLayout;
    DecimalFormat f;
    List<String> customerNames,orderTypeNames,paymentMethodNames;
    ArrayAdapter<String>  customerAdapter, orderTypeAdapter,paymentMethodAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_cart);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.product_cart);
        f = new DecimalFormat("#0.00");
        recyclerView = findViewById(R.id.cart_recyclerview);
        imgNoProduct = findViewById(R.id.image_no_product);
        btnSubmitOrder=findViewById(R.id.btn_submit_order);
        txt_no_product=findViewById(R.id.txt_no_product);
        linearLayout=findViewById(R.id.linear_layout);
        txt_total_price=findViewById(R.id.txt_total_price);

        txt_no_product.setVisibility(View.GONE);


        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView


        recyclerView.setHasFixedSize(true);


        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(ProductCart.this);
        databaseAccess.open();


        //get data from local database
        List<HashMap<String, String>> cartProductList;
        cartProductList = databaseAccess.getCartProduct();



        if (cartProductList.isEmpty()) {

            imgNoProduct.setImageResource(R.drawable.empty_cart);
            imgNoProduct.setVisibility(View.VISIBLE);
            txt_no_product.setVisibility(View.VISIBLE);
            btnSubmitOrder.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
            txt_total_price.setVisibility(View.GONE);
        } else {


            imgNoProduct.setVisibility(View.GONE);
            productCartAdapter = new CartAdapter(ProductCart.this, cartProductList,txt_total_price,btnSubmitOrder,imgNoProduct,txt_no_product);

            recyclerView.setAdapter(productCartAdapter);


        }




        btnSubmitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog();


            }
        });

    }




    public void proceedOrder(String type,String payment_method,String customer_name,double calculated_tax,String discount)
    {

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(ProductCart.this);
        databaseAccess.open();

        int itemCount = databaseAccess.getCartItemCount();

        if (itemCount>0) {



            databaseAccess.open();
            //get data from local database
            final List<HashMap<String, String>> lines;
            lines = databaseAccess.getCartProduct();

            if (lines.isEmpty()) {
                Toasty.error(ProductCart.this, R.string.no_product_found, Toast.LENGTH_SHORT).show();
            } else {



                //get current timestamp

                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
                //H denote 24 hours and h denote 12 hour hour format
                String currentTime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date()); //HH:mm:ss a

                //timestamp use for invoice id for unique
                Long tsLong = System.currentTimeMillis() / 1000;
                String timeStamp = tsLong.toString();
                Log.d("Time", timeStamp);

                final JSONObject obj = new JSONObject();
                try {


                    obj.put("order_date", currentDate);
                    obj.put("order_time", currentTime);
                    obj.put("order_type", type);
                    obj.put("order_payment_method", payment_method);
                    obj.put("customer_name", customer_name);

                    obj.put("tax", calculated_tax);
                    obj.put("discount", discount);



                    JSONArray array = new JSONArray();


                    for (int i = 0; i < lines.size(); i++) {

                        databaseAccess.open();
                        String product_id=lines.get(i).get("product_id");
                        String product_name=databaseAccess.getProductName(product_id);

                        databaseAccess.open();
                        String weight_unit_id=lines.get(i).get("product_weight_unit");
                        String weight_unit=databaseAccess.getWeightUnitName(weight_unit_id);


                        databaseAccess.open();
                        String product_image=databaseAccess.getProductImage(product_id);

                        JSONObject objp = new JSONObject();
                        objp.put("product_id", product_id);
                        objp.put("product_name", product_name);
                        objp.put("product_weight", lines.get(i).get("product_weight")+" "+weight_unit);
                        objp.put("product_qty", lines.get(i).get("product_qty"));
                        objp.put("stock", lines.get(i).get("stock"));
                        objp.put("product_price", lines.get(i).get("product_price"));
                        objp.put("product_image", product_image);
                        objp.put("product_order_date", currentDate);

                        array.put(objp);

                    }
                    obj.put("lines", array);




                } catch (JSONException e) {
                    e.printStackTrace();
                }

                saveOrderInOfflineDb(obj);




            }

        }
        else {
            Toasty.error(ProductCart.this, R.string.no_product_in_cart, Toast.LENGTH_SHORT).show();
        }
    }




    //for save data in offline
    private void saveOrderInOfflineDb(final JSONObject obj)
    {

        //get current timestamp
        Long tsLong = System.currentTimeMillis() / 1000;
        String timeStamp = tsLong.toString();

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(ProductCart.this);

        databaseAccess.open();
        /*
        timestamp used for un sync order and make it unique id
         */
        databaseAccess.insertOrder(timeStamp,obj);

        Toasty.success(this, R.string.order_done_successful, Toast.LENGTH_SHORT).show();

        Intent intent=new Intent(ProductCart.this, OrdersActivity.class);

        startActivity(intent);
        finish();


    }




    //dialog for taking otp code
    public void dialog() {


        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(ProductCart.this);
        databaseAccess.open();
        //get data from local database
        List<HashMap<String, String>> shopData;
        shopData = databaseAccess.getShopInformation();
        String shop_currency = shopData.get(0).get("shop_currency");
        String tax = shopData.get(0).get("tax");

        double getTax=Double.parseDouble(tax);

        AlertDialog.Builder dialog = new AlertDialog.Builder(ProductCart.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_payment, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        final Button dialog_btn_submit = dialogView.findViewById(R.id.btn_submit);
        final ImageButton dialog_btn_close = dialogView.findViewById(R.id.btn_close);
        final TextView dialog_order_payment_method =  dialogView.findViewById(R.id.dialog_order_status);
        final TextView dialog_order_type=  dialogView.findViewById(R.id.dialog_order_type);
        final TextView dialog_customer=  dialogView.findViewById(R.id.dialog_customer);

        final TextView dialog_txt_total=  dialogView.findViewById(R.id.dialog_txt_total);
        final TextView dialog_txt_total_tax=  dialogView.findViewById(R.id.dialog_txt_total_tax);
        final TextView dialog_txt_level_tax=  dialogView.findViewById(R.id.dialog_level_tax);
        final TextView dialog_txt_total_cost=  dialogView.findViewById(R.id.dialog_txt_total_cost);
        final EditText dialog_etxt_discount=  dialogView.findViewById(R.id.etxt_dialog_discount);


        final ImageButton dialog_img_customer = dialogView.findViewById(R.id.img_select_customer);
        final ImageButton dialog_img_order_payment_method = dialogView.findViewById(R.id.img_order_payment_method);
        final ImageButton dialog_img_order_type = dialogView.findViewById(R.id.img_order_type);


        dialog_txt_level_tax.setText(getString(R.string.total_tax)+"( "+tax+"%) : ");
        double total_cost=CartAdapter.total_price;
        dialog_txt_total.setText(shop_currency+f.format(total_cost));

        double calculated_tax=(total_cost*getTax)/100.0;
        dialog_txt_total_tax.setText(shop_currency+f.format(calculated_tax));


        double discount=0;
        double calculated_total_cost=total_cost+calculated_tax-discount;
        dialog_txt_total_cost.setText(shop_currency+f.format(calculated_total_cost));



        dialog_etxt_discount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                double discount=0;
                String get_discount=s.toString();
                if (!get_discount.isEmpty() && !get_discount.equals("."))
                {
                    double calculated_total_cost=total_cost+calculated_tax;
                    discount=Double.parseDouble(get_discount);
                    if(discount>calculated_total_cost)
                    {
                        dialog_etxt_discount.setError(getString(R.string.discount_cant_be_greater_than_total_price));
                        dialog_etxt_discount.requestFocus();

                        dialog_btn_submit.setVisibility(View.INVISIBLE);
                        }
                    else {

                        dialog_btn_submit.setVisibility(View.VISIBLE);
                        calculated_total_cost = total_cost + calculated_tax - discount;
                        dialog_txt_total_cost.setText(shop_currency + f.format(calculated_total_cost));
                    }
                }
                else
                {

                    double calculated_total_cost=total_cost+calculated_tax-discount;
                    dialog_txt_total_cost.setText(shop_currency+f.format(calculated_total_cost));
                }



            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




        customerNames = new ArrayList<>();


        databaseAccess.open();

        //get data from local database
        final List<HashMap<String, String>> customer;
        customer = databaseAccess.getCustomers();

        for (int i=0;  i<customer.size();  i++) {

            // Get the ID of selected Country
            customerNames.add(customer.get(i).get("customer_name"));

        }


        orderTypeNames = new ArrayList<>();
        databaseAccess.open();

        //get data from local database
        final List<HashMap<String, String>> order_type;
        order_type = databaseAccess.getOrderType();

        for (int i=0;  i<order_type.size();  i++) {

            // Get the ID of selected Country
            orderTypeNames.add(order_type.get(i).get("order_type_name"));

        }




        //payment methods
        paymentMethodNames = new ArrayList<>();
        databaseAccess.open();

        //get data from local database
        final List<HashMap<String, String>> payment_method;
        payment_method = databaseAccess.getPaymentMethod();

        for (int i=0;  i<payment_method.size();  i++) {

            // Get the ID of selected Country
            paymentMethodNames.add(payment_method.get(i).get("payment_method_name"));

        }





        dialog_img_order_payment_method.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               paymentMethodAdapter = new ArrayAdapter<String>(ProductCart.this, android.R.layout.simple_list_item_1);
               paymentMethodAdapter.addAll(paymentMethodNames);

                AlertDialog.Builder dialog = new AlertDialog.Builder(ProductCart.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialog_button  = (Button) dialogView.findViewById(R.id.dialog_button);
                EditText dialog_input = (EditText) dialogView.findViewById(R.id.dialog_input);
                TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                ListView dialog_list = (ListView) dialogView.findViewById(R.id.dialog_list);

//                dialog_title.setText(getString(R.string.zone));
                dialog_title.setText(R.string.select_payment_method);
                dialog_list.setVerticalScrollBarEnabled(true);
                dialog_list.setAdapter(paymentMethodAdapter);

                dialog_input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        paymentMethodAdapter.getFilter().filter(charSequence);
                    }
                    @Override
                    public void afterTextChanged(Editable s) {}
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
                        String selectedItem = paymentMethodAdapter.getItem(position);


                        dialog_order_payment_method.setText(selectedItem);


                    }
                });
            }


        });


        dialog_img_order_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                orderTypeAdapter = new ArrayAdapter<String>(ProductCart.this, android.R.layout.simple_list_item_1);
                orderTypeAdapter.addAll(orderTypeNames);

                AlertDialog.Builder dialog = new AlertDialog.Builder(ProductCart.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialog_button  = (Button) dialogView.findViewById(R.id.dialog_button);
                EditText dialog_input = (EditText) dialogView.findViewById(R.id.dialog_input);
                TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                ListView dialog_list = (ListView) dialogView.findViewById(R.id.dialog_list);

//                dialog_title.setText(getString(R.string.zone));
                dialog_title.setText(R.string.select_order_type);
                dialog_list.setVerticalScrollBarEnabled(true);
                dialog_list.setAdapter(orderTypeAdapter);

                dialog_input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        orderTypeAdapter.getFilter().filter(charSequence);
                    }
                    @Override
                    public void afterTextChanged(Editable s) {}
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
                        String selectedItem = orderTypeAdapter.getItem(position);


                        dialog_order_type.setText(selectedItem);


                    }
                });
            }


        });



        dialog_img_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customerAdapter = new ArrayAdapter<String>(ProductCart.this, android.R.layout.simple_list_item_1);
                customerAdapter.addAll(customerNames);

                AlertDialog.Builder dialog = new AlertDialog.Builder(ProductCart.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialog_button  = (Button) dialogView.findViewById(R.id.dialog_button);
                EditText dialog_input = (EditText) dialogView.findViewById(R.id.dialog_input);
                TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                ListView dialog_list = (ListView) dialogView.findViewById(R.id.dialog_list);

//                dialog_title.setText(getString(R.string.zone));
                dialog_title.setText(R.string.select_customer);
                dialog_list.setVerticalScrollBarEnabled(true);
                dialog_list.setAdapter(customerAdapter);

                dialog_input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        customerAdapter.getFilter().filter(charSequence);
                    }
                    @Override
                    public void afterTextChanged(Editable s) {}
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
                        String selectedItem = customerAdapter.getItem(position);


                        dialog_customer.setText(selectedItem);


                    }
                });
            }
        });


        final AlertDialog alertDialog = dialog.create();
        alertDialog.show();



        dialog_btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String order_type = dialog_order_type.getText().toString().trim();
                String order_payment_method = dialog_order_payment_method.getText().toString().trim();
                String customer_name = dialog_customer.getText().toString().trim();

                String discount = dialog_etxt_discount.getText().toString().trim();
                if (discount.isEmpty())
                {
                    discount="0.00";
                }


                proceedOrder(order_type,order_payment_method,customer_name,calculated_tax,discount);


                alertDialog.dismiss();
            }



        });


        dialog_btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });



    }


    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                Intent intent=new Intent(ProductCart.this,PosActivity.class);
                startActivity(intent);
                this.finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

