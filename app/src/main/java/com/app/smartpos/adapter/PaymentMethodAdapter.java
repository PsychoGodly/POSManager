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
import com.app.smartpos.settings.payment_method.EditPaymentMethodActivity;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class PaymentMethodAdapter extends RecyclerView.Adapter<PaymentMethodAdapter.MyViewHolder> {


    private List<HashMap<String, String>> paymentMethodData;
    private Context context;


    public PaymentMethodAdapter(Context context, List<HashMap<String, String>> paymentMethodData) {
        this.context = context;
        this.paymentMethodData = paymentMethodData;

    }


    @NonNull
    @Override
    public PaymentMethodAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_method_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final PaymentMethodAdapter.MyViewHolder holder, int position) {

        final String payment_method_id = paymentMethodData.get(position).get("payment_method_id");
        String payment_method_name = paymentMethodData.get(position).get("payment_method_name");


        holder.txtPaymentMethodName.setText(payment_method_name);


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
                                boolean deleteCustomer = databaseAccess.deletePaymentMethod(payment_method_id);

                                if (deleteCustomer) {
                                    Toasty.success(context, R.string.payment_method_deleted, Toast.LENGTH_SHORT).show();

                                    paymentMethodData.remove(holder.getAdapterPosition());

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
        return paymentMethodData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtPaymentMethodName;
        ImageView imgDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtPaymentMethodName = itemView.findViewById(R.id.txt_payment_method_name);

            imgDelete = itemView.findViewById(R.id.img_delete);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, EditPaymentMethodActivity.class);
            i.putExtra("payment_method_id", paymentMethodData.get(getAdapterPosition()).get("payment_method_id"));
            i.putExtra("payment_method_name", paymentMethodData.get(getAdapterPosition()).get("payment_method_name"));

            context.startActivity(i);
        }
    }


}
