package com.app.smartpos.report;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.smartpos.R;
import com.app.smartpos.database.DatabaseAccess;
import com.app.smartpos.utils.BaseActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GraphReportActivity extends BaseActivity {



    int mYear;
    BarChart barChart;
    TextView txtTotalSales, txtSelectYear,txtTotalTax,txtTotalDiscount,txtNetSales;;
    DecimalFormat f;
    LinearLayout layoutYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_report);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.monthly_sales_graph);
        layoutYear=findViewById(R.id.layout_year);

        f = new DecimalFormat("#0.00");

        barChart = findViewById(R.id.barchart);
        txtTotalSales = findViewById(R.id.txt_total_sales);
        txtSelectYear = findViewById(R.id.txt_select_year);

        txtTotalTax=findViewById(R.id.txt_total_tax);
        txtTotalDiscount=findViewById(R.id.txt_discount);
        txtNetSales=findViewById(R.id.txt_net_sales);


        barChart.setDrawBarShadow(false);

        barChart.setDrawValueAboveBar(true);

        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(true);



        String currentYear = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date());
        txtSelectYear.setText(getString(R.string.year)+ " " +currentYear);


        mYear=Integer.parseInt(currentYear);


        getGraphData(mYear);

        layoutYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseYearOnly();

            }
        });

    }


    public void getGraphData(int mYear) {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(GraphReportActivity.this);

        String[] monthNumber = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

        ArrayList<BarEntry> barEntries = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            databaseAccess.open();
            barEntries.add(new BarEntry(i, databaseAccess.getMonthlySalesAmount(monthNumber[i], "" + mYear)));
        }


        String[] monthList = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(monthList));
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelCount(12);

        BarDataSet barDataSet = new BarDataSet(barEntries, getString(R.string.monthly_sales_report));
        barDataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f);

        barChart.setData(barData);

        barChart.setScaleEnabled(false);  //for fixed bar chart,no zoom

        //for refresh chart
        barChart.notifyDataSetChanged();
        barChart.invalidate();


        databaseAccess.open();
        String currency=databaseAccess.getCurrency();
        databaseAccess.open();
        double sub_total=databaseAccess.getTotalOrderPriceForGraph("yearly",mYear);
        txtTotalSales.setText(getString(R.string.total_sales)+ currency + f.format(sub_total));

        databaseAccess.open();
        double get_tax=databaseAccess.getTotalTaxForGraph("yearly",mYear);
        txtTotalTax.setText(getString(R.string.total_tax)+"(+) : "+currency+f.format(get_tax));


        databaseAccess.open();
        double get_discount=databaseAccess.getTotalDiscountForGraph("yearly",mYear);
        txtTotalDiscount.setText(getString(R.string.total_discount)+"(-) : "+currency+f.format(get_discount));

        double net_sales=sub_total+get_tax-get_discount;
        txtNetSales.setText(getString(R.string.net_sales)+": "+currency+f.format(net_sales));

    }



    private void chooseYearOnly() {



        findViewById(R.id.txt_select_year).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(GraphReportActivity.this, new MonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) {
                        txtSelectYear.setText(getString(R.string.year)+" "+selectedYear);
                        mYear = selectedYear;

                        getGraphData(mYear);
                    }
                }, mYear, 0);

                builder.showYearOnly()
                        .setTitle(getString(R.string.select_year))
                        .build()
                        .show();


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
