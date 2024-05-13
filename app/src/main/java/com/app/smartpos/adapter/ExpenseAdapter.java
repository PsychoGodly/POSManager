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
import com.app.smartpos.customers.EditCustomersActivity;
import com.app.smartpos.database.DatabaseAccess;
import com.app.smartpos.expense.EditExpenseActivity;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.MyViewHolder> {


    private List<HashMap<String, String>> expenseData;
    private Context context;


    public ExpenseAdapter(Context context, List<HashMap<String, String>> expenseData) {
        this.context = context;
        this.expenseData = expenseData;

    }


    @NonNull
    @Override
    public ExpenseAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ExpenseAdapter.MyViewHolder holder, int position) {

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);

        final String expense_id= expenseData.get(position).get("expense_id");
        String expense_name= expenseData.get(position).get("expense_name");
        String expense_note= expenseData.get(position).get("expense_note");
        String expense_amount= expenseData.get(position).get("expense_amount");
        String date= expenseData.get(position).get("expense_date");
        String time= expenseData.get(position).get("expense_time");

        databaseAccess.open();
        String currency=databaseAccess.getCurrency();

        holder.txtExpenseName.setText(expense_name);
        holder.txtExpenseAmount.setText(currency+expense_amount);
        holder.txtExpenseDateTime.setText(date+" "+time);
        holder.txtExpenseNote.setText(context.getString(R.string.note)+expense_note);







        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.want_to_delete_expense)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                databaseAccess.open();
                                boolean deleteProduct=databaseAccess.deleteExpense(expense_id);

                                if (deleteProduct)
                                {
                                    Toasty.error(context, R.string.expense_deleted, Toast.LENGTH_SHORT).show();

                                    expenseData.remove(holder.getAdapterPosition());

                                    // Notify that item at position has been removed
                                    notifyItemRemoved(holder.getAdapterPosition());

                                }

                                else
                                {
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
        return expenseData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txtExpenseName, txtExpenseAmount, txtExpenseNote, txtExpenseDateTime;
        ImageView imgDelete,product_image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtExpenseName = itemView.findViewById(R.id.txt_expense_name);
            txtExpenseAmount = itemView.findViewById(R.id.txt_expense_amount);
            txtExpenseNote= itemView.findViewById(R.id.txt_expense_note);
            txtExpenseDateTime = itemView.findViewById(R.id.txt_date_time);

            imgDelete = itemView.findViewById(R.id.img_delete);
            product_image = itemView.findViewById(R.id.product_image);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, EditExpenseActivity.class);
            i.putExtra("expense_id", expenseData.get(getAdapterPosition()).get("expense_id"));
            i.putExtra("expense_name", expenseData.get(getAdapterPosition()).get("expense_name"));
            i.putExtra("expense_note", expenseData.get(getAdapterPosition()).get("expense_note"));
            i.putExtra("expense_amount", expenseData.get(getAdapterPosition()).get("expense_amount"));
            i.putExtra("expense_date", expenseData.get(getAdapterPosition()).get("expense_date"));
            i.putExtra("expense_time", expenseData.get(getAdapterPosition()).get("expense_time"));
            context.startActivity(i);
        }
    }



}
