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
import com.app.smartpos.settings.unit.EditUnitActivity;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class UnitAdapter extends RecyclerView.Adapter<UnitAdapter.MyViewHolder> {


    private List<HashMap<String, String>> unitData;
    private Context context;


    public UnitAdapter(Context context, List<HashMap<String, String>> unitData) {
        this.context = context;
        this.unitData = unitData;

    }


    @NonNull
    @Override
    public UnitAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.unit_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final UnitAdapter.MyViewHolder holder, int position) {

        final String weightId = unitData.get(position).get("weight_id");
        String weightUnit = unitData.get(position).get("weight_unit");

        holder.txtUnitName.setText(weightUnit);

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
                                boolean deleteCustomer = databaseAccess.deleteUnit(weightId);

                                if (deleteCustomer) {
                                    Toasty.success(context, R.string.unit_deleted, Toast.LENGTH_SHORT).show();

                                    unitData.remove(holder.getAdapterPosition());

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
        return unitData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtUnitName;
        ImageView imgDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtUnitName = itemView.findViewById(R.id.txt_unit_name);

            imgDelete = itemView.findViewById(R.id.img_delete);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, EditUnitActivity.class);
            i.putExtra("weight_id", unitData.get(getAdapterPosition()).get("weight_id"));
            i.putExtra("weight_unit", unitData.get(getAdapterPosition()).get("weight_unit"));

            context.startActivity(i);
        }
    }


}
