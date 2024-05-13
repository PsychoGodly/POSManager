package com.app.smartpos.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.smartpos.R;
import com.app.smartpos.database.DatabaseAccess;

import java.util.HashMap;
import java.util.List;

public class SalesReportAdapter extends RecyclerView.Adapter<SalesReportAdapter.MyViewHolder> {


    Context context;
    private List<HashMap<String, String>> orderData;


    public SalesReportAdapter(Context context, List<HashMap<String, String>> orderData) {
        this.context = context;
        this.orderData = orderData;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sales_report_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        String currency = databaseAccess.getCurrency();


        holder.txt_product_name.setText(orderData.get(position).get("product_name"));

        holder.txt_product_qty.setText(context.getString(R.string.quantity) + orderData.get(position).get("product_qty"));
        holder.txt_product_Weight.setText(context.getString(R.string.weight) + orderData.get(position).get("product_weight"));

        holder.txt_date.setText(context.getString(R.string.date) + orderData.get(position).get("product_order_date"));

        String unit_price = orderData.get(position).get("product_price");
        String qty = orderData.get(position).get("product_qty");
        double price = Double.parseDouble(unit_price);
        int quantity = Integer.parseInt(qty);
        double cost = quantity * price;

        holder.txt_total_cost.setText(currency + unit_price + " x " + qty + " = " + currency + cost);

        String base64Image = orderData.get(position).get("product_image");


        if (base64Image != null) {
            if (base64Image.isEmpty() || base64Image.length() < 6) {
                holder.imgProduct.setImageResource(R.drawable.expense);
            } else {


                byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
                holder.imgProduct.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

            }
        }


    }

    @Override
    public int getItemCount() {
        return orderData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_product_name, txt_date, txt_total_cost, txt_product_price, txt_product_qty, txt_product_Weight;
        ImageView imgProduct;


        public MyViewHolder(View itemView) {
            super(itemView);

            txt_product_name = itemView.findViewById(R.id.txt_product_name);
            txt_product_price = itemView.findViewById(R.id.txt_price);
            txt_product_qty = itemView.findViewById(R.id.txt_qty);
            txt_product_Weight = itemView.findViewById(R.id.txt_weight);
            imgProduct = itemView.findViewById(R.id.img_product);
            txt_total_cost = itemView.findViewById(R.id.txt_total_cost);
            txt_date = itemView.findViewById(R.id.txt_date);


        }

    }


}