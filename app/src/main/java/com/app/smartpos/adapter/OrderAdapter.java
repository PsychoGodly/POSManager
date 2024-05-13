package com.app.smartpos.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.app.smartpos.Constant;
import com.app.smartpos.R;
import com.app.smartpos.database.DatabaseAccess;
import com.app.smartpos.orders.OrderDetailsActivity;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.Slidetop;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {


    Context context;
    private List<HashMap<String, String>> orderData;


    public OrderAdapter(Context context, List<HashMap<String, String>> orderData) {
        this.context = context;
        this.orderData = orderData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        String customer_name=orderData.get(position).get("customer_name");
        String invoice_id=orderData.get(position).get("invoice_id");
        String order_date=orderData.get(position).get("order_date");
        String order_time=orderData.get(position).get("order_time");
        String payment_method=orderData.get(position).get("order_payment_method");
        String order_type=orderData.get(position).get("order_type");
        String orderStatus = orderData.get(position).get(Constant.ORDER_STATUS);



        holder.txt_customer_name.setText(customer_name);
        holder.txt_order_id.setText(context.getString(R.string.order_id)+invoice_id);
        holder.txt_payment_method.setText(context.getString(R.string.payment_method)+payment_method);
        holder.txt_order_type.setText(context.getString(R.string.order_type)+order_type);
        holder.txt_date.setText(order_time+" "+order_date);

        holder.txt_order_status.setText(orderStatus);

        if (orderStatus.equals(Constant.COMPLETED))
        {

            holder.txt_order_status.setBackgroundColor(Color.parseColor("#43a047"));
            holder.txt_order_status.setTextColor(Color.WHITE);
            holder.imgStatus.setVisibility(View.GONE);


        }

        else   if (orderStatus.equals(Constant.CANCEL))
        {

            holder.txt_order_status.setBackgroundColor(Color.parseColor("#e53935"));
            holder.txt_order_status.setTextColor(Color.WHITE);
            holder.imgStatus.setVisibility(View.GONE);


        }

        holder.imgStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
                dialogBuilder
                        .withTitle(context.getString(R.string.change_order_status))
                        .withMessage(context.getString(R.string.please_change_order_status_to_complete_or_cancel))
                        .withEffect(Slidetop)
                        .withDialogColor("#01baef") //use color code for dialog
                        .withButton1Text(context.getString(R.string.order_completed))
                        .withButton2Text(context.getString(R.string.cancel_order))
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                                databaseAccess.open();
                                boolean updateOrder = databaseAccess.updateOrder(invoice_id, Constant.COMPLETED);

                                if (updateOrder) {
                                    Toasty.success(context, R.string.order_updated, Toast.LENGTH_SHORT).show();

                                    holder.txt_order_status.setText(Constant.COMPLETED);
                                    holder.txt_order_status.setBackgroundColor(Color.parseColor("#43a047"));
                                    holder.txt_order_status.setTextColor(Color.WHITE);
                                    holder.imgStatus.setVisibility(View.GONE);

                                } else {
                                    Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show();
                                }


                                dialogBuilder.dismiss();
                            }
                        })
                        .setButton2Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                                databaseAccess.open();
                                boolean updateOrder = databaseAccess.updateOrder(invoice_id,Constant.CANCEL);

                                if (updateOrder) {
                                    Toasty.error(context, R.string.order_updated, Toast.LENGTH_SHORT).show();

                                    holder.txt_order_status.setText(Constant.CANCEL);
                                    holder.txt_order_status.setBackgroundColor(Color.parseColor("#e53935"));
                                    holder.txt_order_status.setTextColor(Color.WHITE);
                                    holder.imgStatus.setVisibility(View.GONE);

                                } else {
                                    Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show();
                                }


                                dialogBuilder.dismiss();
                            }
                        })
                        .show();



            }
        });





    }

    @Override
    public int getItemCount() {
        return orderData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txt_customer_name,txt_order_id,txt_order_status,txt_order_type, txt_payment_method,txt_date;
        ImageView imgStatus;

        public MyViewHolder(View itemView) {
            super(itemView);

            txt_customer_name = itemView.findViewById(R.id.txt_customer_name);
            txt_order_id = itemView.findViewById(R.id.txt_order_id);
            txt_order_type= itemView.findViewById(R.id.txt_order_type);
            txt_payment_method = itemView.findViewById(R.id.txt_payment_method);
            txt_date= itemView.findViewById(R.id.txt_date);
            txt_order_status= itemView.findViewById(R.id.txt_order_status);
            imgStatus =itemView.findViewById(R.id.img_status);

            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(context, OrderDetailsActivity.class);
            i.putExtra("order_id",orderData.get(getAdapterPosition()).get("invoice_id"));
            i.putExtra("customer_name",orderData.get(getAdapterPosition()).get("customer_name"));
            i.putExtra("order_date",orderData.get(getAdapterPosition()).get("order_date"));
            i.putExtra("order_time",orderData.get(getAdapterPosition()).get("order_time"));
            i.putExtra("tax", orderData.get(getAdapterPosition()).get("tax"));
            i.putExtra("discount", orderData.get(getAdapterPosition()).get("discount"));

            context.startActivity(i);
        }
    }




}