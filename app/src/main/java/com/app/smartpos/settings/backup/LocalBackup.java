
package com.app.smartpos.settings.backup;

import android.content.DialogInterface;
import android.os.Environment;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.app.smartpos.R;
import com.app.smartpos.database.DatabaseOpenHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class LocalBackup {

    private BackupActivity activity;

    public LocalBackup(BackupActivity activity) {
        this.activity = activity;
    }

    //ask to the user a name for the backup and perform it. The backup will be saved to a custom folder.
    public void performBackup(final DatabaseOpenHelper db, final String outFileName) {


        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "SmartPos/");

        boolean success = true;
        if (!folder.exists())
            success = folder.mkdirs();
        if (success) {

            //get current timestamp
            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(new Date());


            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            final EditText input = new EditText(activity);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setText("backup_" + currentDate + ".db");
            builder.setView(input);
            builder.setMessage(R.string.enter_local_database_backup_name)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {


                            String m_Text = input.getText().toString();
                            String out = outFileName + m_Text;
                            db.backup(out);
                            dialog.cancel();

                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Perform Your Task Here--When No is pressed
                            dialog.cancel();
                        }
                    }).show();

        } else
            Toast.makeText(activity, R.string.unable_to_create_directory_retry, Toast.LENGTH_SHORT).show();
    }

    //ask to the user what backup to restore
    public void performRestore(final DatabaseOpenHelper db) {


        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "SmartPos/");
        if (folder.exists()) {

            final File[] files = folder.listFiles();

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(activity, android.R.layout.select_dialog_item);
            for (File file : files)
                arrayAdapter.add(file.getName());

            AlertDialog.Builder builderSingle = new AlertDialog.Builder(activity);
            builderSingle.setTitle(R.string.database_restore);
            builderSingle.setNegativeButton(
                    R.string.cancel,
                    (dialog, which) -> dialog.dismiss());
            builderSingle.setAdapter(
                    arrayAdapter,
                    (dialog, which) -> {
                        try {
                            db.importDB(files[which].getPath());
                        } catch (Exception e) {
                            Toast.makeText(activity, R.string.unable_to_restore_retry, Toast.LENGTH_SHORT).show();
                        }
                    });
            builderSingle.show();
        } else
            Toast.makeText(activity, R.string.backup_folder_not_present, Toast.LENGTH_SHORT).show();
    }

}
