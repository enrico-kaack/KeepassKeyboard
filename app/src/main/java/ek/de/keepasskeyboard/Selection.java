package ek.de.keepasskeyboard;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.security.Security;
import java.util.List;

import de.slackspace.openkeepass.domain.Entry;


public class Selection extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        handlePermissions();

        final KeepassHandler kee = new KeepassHandler();


        //Create FileOpenDialog and register a callback
        FilePicker fileOpenDialog =  new FilePicker(
                Selection.this,
                "FileOpen..",
                new FilePicker.SimpleFileDialogListener()
                {
                    @Override
                    public void onChosenDir(String chosenDir)
                    {
                        // The code in this function will be executed when the dialog OK button is pushed
                       kee.unlockDatabase(chosenDir, "1234");
                        List<Entry> entries = kee.getAllEntries();
                        Log.v("KEEPASS", "test");
                    }
                }
        );
        //You can change the default filename using the public variable "Default_File_Name"
        //fileOpenDialog.default_file_name = editFile.getText().toString();
        //fileOpenDialog.chooseFile_or_Dir();
        kee.unlockDatabase("/storage/emulated/0/Security/test.kdbx", "1234");
        List<Entry> entries = kee.getAllEntries();
        Log.v("KEEPASS", "test");

    }

    private void handlePermissions() {
        int permissionCheck1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED || permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                            22);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 22: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("KEEPASS", "Permission Granted");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
