package com.app.smartpos.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.smartpos.R;
import com.app.smartpos.customers.EditCustomersActivity;
import com.app.smartpos.database.DatabaseAccess;
import com.app.smartpos.suppliers.EditSuppliersActivity;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.MyViewHolder> {


    private List<HashMap<String, String>> supplierData;
    private Context context;


    public SupplierAdapter(Context context, List<HashMap<String, String>> supplierData) {
        this.context = context;
        this.supplierData = supplierData;

    }


    @NonNull
    @Override
    public SupplierAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.supplier_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final SupplierAdapter.MyViewHolder holder, int position) {

        final String suppliers_id = supplierData.get(position).get("suppliers_id");
        String name = supplierData.get(position).get("suppliers_name");
        String contact_perosn = supplierData.get(position).get("suppliers_contact_person");
        String cell = supplierData.get(position).get("suppliers_cell");
        String email = supplierData.get(position).get("suppliers_email");
        String address = supplierData.get(position).get("suppliers_address");

        holder.txtSuppliersName.setText(name);
        holder.txtSupplierContactPerson.setText(contact_perosn);
        holder.txtSupplierCell.setText(cell);
        holder.txtSupplierEmail.setText(email);
        holder.txtSupplierAddress.setText(address);


        holder.imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                String phone = "tel:" + cell;
                callIntent.setData(Uri.parse(phone));
                context.startActivity(callIntent);
            }
        });

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.want_to_delete_supplier)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                                databaseAccess.open();
                                boolean deleteSupplier = databaseAccess.deleteSupplier(suppliers_id);

                                if (deleteSupplier) {
                                    Toasty.error(context, R.string.supplier_deleted, Toast.LENGTH_SHORT).show();

                                    supplierData.remove(holder.getAdapterPosition());

                                    // Notify that item at position has been removed
                                    notifyItemRemoved(holder.getAdapterPosition());

                                } else {
                                    Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show();
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
        return supplierData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtSuppliersName, txtSupplierContactPerson, txtSupplierCell, txtSupplierEmail, txtSupplierAddress;
        ImageView imgDelete,imgCall;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtSuppliersName = itemView.findViewById(R.id.txt_supplier_name);
            txtSupplierContactPerson = itemView.findViewById(R.id.txt_contact_person);
            txtSupplierCell = itemView.findViewById(R.id.txt_supplier_cell);
            txtSupplierEmail = itemView.findViewById(R.id.txt_supplier_email);
            txtSupplierAddress = itemView.findViewById(R.id.txt_supplier_address);

            imgDelete = itemView.findViewById(R.id.img_delete);
            imgCall = itemView.findViewById(R.id.img_call);


            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            Intent i = new Intent(context, EditSuppliersActivity.class);
            i.putExtra("suppliers_id", supplierData.get(getAdapterPosition()).get("suppliers_id"));
            i.putExtra("suppliers_name", supplierData.get(getAdapterPosition()).get("suppliers_name"));
            i.putExtra("suppliers_contact_person", supplierData.get(getAdapterPosition()).get("suppliers_contact_person"));
            i.putExtra("suppliers_cell", supplierData.get(getAdapterPosition()).get("suppliers_cell"));
            i.putExtra("suppliers_email", supplierData.get(getAdapterPosition()).get("suppliers_email"));
            i.putExtra("suppliers_address", supplierData.get(getAdapterPosition()).get("suppliers_address"));
            context.startActivity(i);
        }
    }


}
