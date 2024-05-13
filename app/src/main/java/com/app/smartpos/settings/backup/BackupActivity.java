package com.app.smartpos.settings.backup;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import com.ajts.androidmads.library.SQLiteToExcel;
import com.app.smartpos.R;
import com.app.smartpos.database.DatabaseOpenHelper;
import com.app.smartpos.utils.BaseActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class BackupActivity extends BaseActivity {

    ProgressDialog loading;
    private LocalBackup localBackup;
    CardView cardLocalBackUp, cardLocalImport, cardExportToExcel, cardBackupToDrive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);


        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.data_backup);

        final DatabaseOpenHelper db = new DatabaseOpenHelper(getApplicationContext());
        cardLocalBackUp = findViewById(R.id.card_local_backup);
        cardLocalImport = findViewById(R.id.card_local_db_import);
        cardExportToExcel = findViewById(R.id.card_export_to_excel);
        cardBackupToDrive = findViewById(R.id.card_backup_to_drive);



        localBackup = new LocalBackup(BackupActivity.this);


        if (Build.VERSION.SDK_INT >= 23) //Android MarshMellow Version or above
        {
            requestPermission();

        }

        cardLocalImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseOpenHelper db = new DatabaseOpenHelper(getApplicationContext());

                localBackup.performRestore(db);
            }
        });


        cardLocalBackUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String outFileName = Environment.getExternalStorageDirectory() + File.separator + "SmartPos/";
                localBackup.performBackup(db, outFileName);
            }
        });


        cardBackupToDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fileChooser();


            }
        });


        cardExportToExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                folderChooser();

            }
        });


    }


    private void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            //  Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings

                        }
                    }


                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
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


    public void folderChooser() {
        new ChooserDialog(BackupActivity.this)

                .displayPath(true)
                .withFilter(true, false)

                // to handle the result(s)
                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String path, File pathFile) {
                        onExport(path);
                        Log.d("path", path);

                    }
                })
                .build()
                .show();
    }


    public void onExport(String path) {


        //get current timestamp
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(new Date());


        String fileName = "SmartPOS" + "_Backup_" + currentDate + ".xls";


        String directory_path = path;
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        // Export SQLite DB as EXCEL FILE
        SQLiteToExcel sqliteToExcel = new SQLiteToExcel(getApplicationContext(), DatabaseOpenHelper.DATABASE_NAME, directory_path);
        sqliteToExcel.exportAllTables(fileName, new SQLiteToExcel.ExportListener() {
            @Override
            public void onStart() {

                loading = new ProgressDialog(BackupActivity.this);
                loading.setMessage(getString(R.string.data_exporting_please_wait));
                loading.setCancelable(false);
                loading.show();
            }

            @Override
            public void onCompleted(String filePath) {

                Handler mHand = new Handler();
                mHand.postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        loading.dismiss();
                        Toasty.success(BackupActivity.this, R.string.data_successfully_exported, Toast.LENGTH_SHORT).show();


                    }
                }, 5000);

            }

            @Override
            public void onError(Exception e) {

                loading.dismiss();
                Toasty.error(BackupActivity.this, R.string.data_export_fail, Toast.LENGTH_SHORT).show();
            }
        });
    }



    public void fileChooser() {
        new ChooserDialog(BackupActivity.this)


                .displayPath(true)
                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String path, File pathFile) {
                        shareFile(path);
                    }
                })
                // to handle the back key pressed or clicked outside the dialog:
                .withOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        dialog.cancel(); // MUST have
                    }
                })
                .build()
                .show();
    }


    private void shareFile(String filePath) {

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        File fileWithinMyDir = new File(filePath);

        if (fileWithinMyDir.exists()) {

            Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", fileWithinMyDir);

            intentShareFile.setType("application/*");
            intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intentShareFile, "Share File"));
        }
    }

}
