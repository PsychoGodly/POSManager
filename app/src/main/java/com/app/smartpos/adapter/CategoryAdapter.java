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
import com.app.smartpos.settings.categories.EditCategoryActivity;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {


    private List<HashMap<String, String>> categoryData;
    private Context context;


    public CategoryAdapter(Context context, List<HashMap<String, String>> categoryData) {
        this.context = context;
        this.categoryData = categoryData;

    }


    @NonNull
    @Override
    public CategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final CategoryAdapter.MyViewHolder holder, int position) {

        final String category_id = categoryData.get(position).get("category_id");
        String category_name = categoryData.get(position).get("category_name");


        holder.txtCategoryName.setText(category_name);


        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.want_to_delete_category)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                                databaseAccess.open();
                                boolean deleteCustomer = databaseAccess.deleteCategory(category_id);

                                if (deleteCustomer) {
                                    Toasty.success(context, R.string.category_deleted, Toast.LENGTH_SHORT).show();

                                    categoryData.remove(holder.getAdapterPosition());

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
        return categoryData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtCategoryName;
        ImageView imgDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCategoryName = itemView.findViewById(R.id.txt_category_name);

            imgDelete = itemView.findViewById(R.id.img_delete);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, EditCategoryActivity.class);
            i.putExtra("category_id", categoryData.get(getAdapterPosition()).get("category_id"));
            i.putExtra("category_name", categoryData.get(getAdapterPosition()).get("category_name"));

            context.startActivity(i);
        }
    }


}
