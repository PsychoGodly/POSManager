package com.app.smartpos.settings.categories;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.smartpos.R;
import com.app.smartpos.database.DatabaseAccess;
import com.app.smartpos.utils.BaseActivity;

import es.dmoral.toasty.Toasty;

public class EditCategoryActivity extends BaseActivity {


    EditText etxtCategoryName;
    TextView txtUpdateCategory, txtEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.edit_category);

        txtEdit = findViewById(R.id.txt_edit_category);
        txtUpdateCategory = findViewById(R.id.txt_update_category);
        etxtCategoryName = findViewById(R.id.etxt_category_name);

        String category_id = getIntent().getExtras().getString("category_id");
        String category_name = getIntent().getExtras().getString("category_name");


        etxtCategoryName.setText(category_name);
        etxtCategoryName.setEnabled(false);
        txtUpdateCategory.setVisibility(View.INVISIBLE);


        txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etxtCategoryName.setEnabled(true);
                txtUpdateCategory.setVisibility(View.VISIBLE);
                etxtCategoryName.setTextColor(Color.RED);

            }
        });


        txtUpdateCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category_name = etxtCategoryName.getText().toString().trim();

                if (category_name.isEmpty()) {
                    etxtCategoryName.setError(getString(R.string.enter_category_name));
                    etxtCategoryName.requestFocus();
                } else {

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(EditCategoryActivity.this);
                    databaseAccess.open();

                    boolean check = databaseAccess.updateCategory(category_id, category_name);

                    if (check) {
                        Toasty.success(EditCategoryActivity.this, R.string.category_updated, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditCategoryActivity.this, CategoriesActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {

                        Toasty.error(EditCategoryActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

                    }
                }

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
