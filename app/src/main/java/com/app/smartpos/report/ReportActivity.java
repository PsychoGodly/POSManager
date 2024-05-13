package com.app.smartpos.report;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.cardview.widget.CardView;

import com.app.smartpos.R;
import com.app.smartpos.utils.BaseActivity;

public class ReportActivity extends BaseActivity {


    CardView cardSalesReport, cardGraphReport, cardExpenseReport, cardExpenseGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.report);


        cardSalesReport = findViewById(R.id.card_sales_report);
        cardGraphReport = findViewById(R.id.card_graph_report);
        cardExpenseGraph = findViewById(R.id.card_expense_graph);
        cardExpenseReport = findViewById(R.id.card_expense_report);


        //for interstitial ads show,
//        Utils utils=new Utils();
//        utils.interstitialAdsShow(ReportActivity.this);


        cardSalesReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportActivity.this, SalesReportActivity.class);
                startActivity(intent);
            }
        });


        cardGraphReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportActivity.this, GraphReportActivity.class);
                startActivity(intent);
            }
        });


        cardExpenseReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportActivity.this, ExpenseReportActivity.class);
                startActivity(intent);
            }
        });


        cardExpenseGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportActivity.this, ExpenseGraphActivity.class);
                startActivity(intent);
            }
        });
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
