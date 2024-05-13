package com.app.smartpos.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.smartpos.R;
import com.app.smartpos.database.DatabaseAccess;
import com.app.smartpos.settings.order_type.EditOrderTypeActivity;
import com.app.smartpos.settings.payment_method.EditPaymentMethodActivity;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class OrderTypeAdapter extends RecyclerView.Adapter<OrderTypeAdapter.MyViewHolder> {


    private List<HashMap<String, String>> orderTypeData;
    private Context context;


    public OrderTypeAdapter(Context context, List<HashMap<String, String>> orderTypeData) {
        this.context = context;
        this.orderTypeData = orderTypeData;

    }


    @NonNull
    @Override
    public OrderTypeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_type_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final OrderTypeAdapter.MyViewHolder holder, int position) {

        final String typeId = orderTypeData.get(position).get("order_type_id");
        String orderTypeName = orderTypeData.get(position).get("order_type_name");


        holder.txtTypeName.setText(orderTypeName);


        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.want_to_delete)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                                databaseAccess.open();
                                boolean deleteCustomer = databaseAccess.deleteOrderType(typeId);

                                if (deleteCustomer) {
                                    Toasty.success(context, R.string.order_type_deleted, Toast.LENGTH_SHORT).show();

                                    orderTypeData.remove(holder.getAdapterPosition());

                                    // Notify that item at position has been removed
                                    notifyItemRemoved(holder.getAdapterPosition());

                                } else {
                                    Toasty.error(context, R.string.failed, Toast.LENGTH_SHORT).show();
                                }
                                dialog.cancel();

                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Perform Your Task Here--When No is pressed
                                dialog.cancel();
                            }
                        }).show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return orderTypeData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtTypeName;
        ImageView imgDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTypeName = itemView.findViewById(R.id.txt_type_name);

            imgDelete = itemView.findViewById(R.id.img_delete);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, EditOrderTypeActivity.class);
            i.putExtra("order_type_id", orderTypeData.get(getAdapterPosition()).get("order_type_id"));
            i.putExtra("order_type_name", orderTypeData.get(getAdapterPosition()).get("order_type_name"));

            context.startActivity(i);
        }
    }


}
