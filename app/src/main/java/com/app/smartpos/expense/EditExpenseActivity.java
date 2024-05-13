package com.app.smartpos.expense;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.app.smartpos.HomeActivity;
import com.app.smartpos.R;
import com.app.smartpos.database.DatabaseAccess;
import com.app.smartpos.utils.BaseActivity;

import java.util.Calendar;

import es.dmoral.toasty.Toasty;

public class EditExpenseActivity extends BaseActivity {


    String date_time = "";
    int mYear,mMonth,mDay,mHour,mMinute;



    EditText etxtExpenseName, etxtExpenseNote, etxtExpenseAmount, etxtDate, etxtTime;
    TextView txtEditExpense,txtUpdateExpense;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);


        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.edit_expense);

        etxtExpenseName = findViewById(R.id.etxt_expense_name);
        etxtExpenseNote = findViewById(R.id.etxt_expense_note);
        etxtExpenseAmount = findViewById(R.id.etxt_expense_amount);
        etxtDate = findViewById(R.id.etxt_date);
        etxtTime = findViewById(R.id.etxt_time);

        txtEditExpense = findViewById(R.id.txt_edit_expense);
        txtUpdateExpense=findViewById(R.id.txt_update_expense);


        String get_expense_id=getIntent().getExtras().getString("expense_id");
        String get_expense_name=getIntent().getExtras().getString("expense_name");
        String get_expense_note=getIntent().getExtras().getString("expense_note");
        String get_expense_amount=getIntent().getExtras().getString("expense_amount");
        String get_expense_date=getIntent().getExtras().getString("expense_date");
        String get_expense_time=getIntent().getExtras().getString("expense_time");


        etxtExpenseName.setText(get_expense_name);
        etxtExpenseNote.setText(get_expense_note);
        etxtExpenseAmount.setText(get_expense_amount);
        etxtDate.setText(get_expense_date);
        etxtTime.setText(get_expense_time);


        etxtExpenseName.setEnabled(false);
        etxtExpenseNote.setEnabled(false);
        etxtExpenseAmount.setEnabled(false);
        etxtDate.setEnabled(false);
        etxtTime.setEnabled(false);
        txtUpdateExpense.setVisibility(View.INVISIBLE);

        etxtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePicker();
            }
        });


        etxtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             timePicker();
            }
        });


        txtEditExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etxtExpenseName.setEnabled(true);
                etxtExpenseNote.setEnabled(true);
                etxtExpenseAmount.setEnabled(true);
                etxtDate.setEnabled(true);
                etxtTime.setEnabled(true);

                etxtExpenseName.setTextColor(Color.RED);
                etxtExpenseNote.setTextColor(Color.RED);
                etxtExpenseAmount.setTextColor(Color.RED);
                etxtDate.setTextColor(Color.RED);
                etxtTime.setTextColor(Color.RED);

                txtUpdateExpense.setVisibility(View.VISIBLE);
                txtEditExpense.setVisibility(View.GONE);


            }
        });


        txtUpdateExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String expense_name = etxtExpenseName.getText().toString();
                String expense_note = etxtExpenseNote.getText().toString();
                String expense_amount = etxtExpenseAmount.getText().toString();
                String expense_date = etxtDate.getText().toString();
                String expense_time = etxtTime.getText().toString();


                if (expense_name.isEmpty()) {
                    etxtExpenseName.setError(getString(R.string.expense_name_cannot_be_empty));
                    etxtExpenseName.requestFocus();
                } else if (expense_amount.isEmpty()) {
                    etxtExpenseAmount.setError(getString(R.string.expense_amount_cannot_be_empty));
                    etxtExpenseAmount.requestFocus();
                } else {

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(EditExpenseActivity.this);
                    databaseAccess.open();

                    boolean check = databaseAccess.updateExpense(get_expense_id,expense_name, expense_amount, expense_note, expense_date, expense_time);

                    if (check) {
                        Toasty.success(EditExpenseActivity.this, R.string.update_successfully, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditExpenseActivity.this,ExpenseActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {

                        Toasty.error(EditExpenseActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

                    }


                }


            }


        });

    }



    private void datePicker() {

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(EditExpenseActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        int month= monthOfYear+1;
                        String fm=""+month;
                        String fd=""+dayOfMonth;

                        if (monthOfYear<10)
                        {
                            fm ="0"+month;
                        }
                        if (dayOfMonth<10)
                        {
                            fd="0"+dayOfMonth;
                        }
                        date_time = year + "-" + (fm) + "-" + fd;


                        etxtDate.setText(date_time);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    private void timePicker() {
        // Get Current Time


        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(EditExpenseActivity.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String am_pm;
                        mHour = hourOfDay;
                        mMinute = minute;

                        if (mHour < 12) {
                            am_pm = "AM";
                            mHour = hourOfDay;
                        } else {
                            am_pm = "PM";
                            mHour = hourOfDay - 12;
                        }

                        etxtTime.setText(mHour + ":" + minute + " " + am_pm);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
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

}
