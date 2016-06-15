package ek.de.keepasskeyboard;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.security.Security;
import java.util.List;

import de.slackspace.openkeepass.domain.Entry;


public class Selection extends Activity {
    SharedPreferences sharedPref;
    BluetoothModul blue;
    String path_to_db;
    int encyrptionMethod = -1;
    KeepassHandler kee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        //Get a SharedPreference Object
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        kee = new KeepassHandler();
        handlePermissions();

        checkForEncryptionMethodAndAskForIfNeccessay();
        checkForInputPathAndAskForIfNeccessary();

    }

    private void checkForEncryptionMethodAndAskForIfNeccessay() {
        encyrptionMethod = sharedPref.getInt("ENCRYPTION_MODE", -1);
        //if (encyrptionMethod == -1){
            Intent wizard = new Intent(this, Wizard.class);
            startActivity(wizard);
        //}
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleBluetooth();


        if (path_to_db != null && !path_to_db.equals("null") && encyrptionMethod != -1) {
            loadDatabaseAndEntry();

        }
    }

    private void handleBluetooth() {
         blue = new BluetoothModul();

        blue.enable();
        if (sharedPref.getString("DEVICE_MAC", null) == null){
            blue.connect(this);
        }else{
            blue.connect(sharedPref.getString("DEVICE_MAC", null));
        }
    }

    private void loadDatabaseAndEntry() {
        kee.unlockDatabase(path_to_db, "1234");
        List<Entry> entries = kee.getAllEntries();

        ListView lv_entries = (ListView)findViewById(R.id.lv_entries);
        final EntryListAdapter adapter = new EntryListAdapter(this, entries);
        lv_entries.setAdapter(adapter);

        lv_entries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                blue.write(adapter.getItem(position).getPassword());
            }
        });
    }

    private void checkForInputPathAndAskForIfNeccessary() {


        //read Value for DB-Path
        path_to_db = sharedPref.getString("DB_PATH", "null");

        if (path_to_db.equals("null")){
            //Path is not set, open file choose dialog
            openFileChooser();
        }
    }

    private void openFileChooser() {
        //Create FileOpenDialog and register a callback
        FilePicker fileOpenDialog =  new FilePicker(
                Selection.this,
                "FileOpen..",
                new FilePicker.SimpleFileDialogListener()
                {
                    @Override
                    public void onChosenDir(String chosenDir)
                    {
                        //Check if the chosen file is a allowed file typ with suffix .kdbx
                       File f = new File(chosenDir);

                        if (f.isFile() && f.getName().endsWith(".kdbx") && f.canRead()){
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("DB_PATH", chosenDir);
                            editor.commit();
                            loadDatabaseAndEntry();
                        }else {
                            openFileChooser();
                        }

                    }
                }
        );
        fileOpenDialog.chooseFile_or_Dir();
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
