package ek.de.keepasskeyboard;

import android.Manifest;
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
import java.util.List;

import de.slackspace.openkeepass.domain.Entry;
import ek.de.keepasskeyboard.connection.BluetoothModul;
import ek.de.keepasskeyboard.database.FilePicker;
import ek.de.keepasskeyboard.database.KeepassHandler;
import ek.de.keepasskeyboard.encryption.EncryptionModul;
import ek.de.keepasskeyboard.wizard.Wizard;


public class Selection extends AppCompatActivity implements OnPasswordInputed{
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



    @Override
    protected void onResume() {
        super.onResume();
        handleBluetooth();
        path_to_db = sharedPref.getString(Constants.DB_PATH, null);
        encyrptionMethod = sharedPref.getInt(Constants.ENCRYPTION_MODE, -1);

        if (path_to_db != null && !path_to_db.equals("null") && encyrptionMethod != -1) {
           initilizeEncryption();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        blue.disable();

    }

    private void checkForEncryptionMethodAndAskForIfNeccessay() {
        encyrptionMethod = sharedPref.getInt(Constants.ENCRYPTION_MODE, -1);
        //if (encyrptionMethod == -1){
        Intent wizard = new Intent(this, Wizard.class);
        startActivity(wizard);
        //}
    }

    private void initilizeEncryption() {
        EncryptionModul encryptionModul = new EncryptionModul(this);
        encryptionModul.getMasterPW(this);
    }

    private void handleBluetooth() {
         blue = new BluetoothModul();

        blue.enable();
        if (sharedPref.getString(Constants.DEVICE_MAC, null) == null){
            blue.connect(this);
        }else{
            blue.connect(sharedPref.getString(Constants.DEVICE_MAC, null));
        }
    }

    private void loadDatabaseAndEntry(String pw) {


        if (pw != null) {
            kee.unlockDatabase(path_to_db, pw);
            List<Entry> entries = kee.getAllEntries();

            ListView lv_entries = (ListView) findViewById(R.id.lv_entries);
            final EntryListAdapter adapter = new EntryListAdapter(this, entries);
            lv_entries.setAdapter(adapter);

            lv_entries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    blue.write(adapter.getItem(position).getPassword());
                }
            });
        }else {
            Log.d("KEEPASS", "Unable to load/encrypt MPW");
        }
    }

    private void checkForInputPathAndAskForIfNeccessary() {


        //read Value for DB-Path
        path_to_db = sharedPref.getString(Constants.DB_PATH, "null");

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
                            editor.putString(Constants.DB_PATH, chosenDir);
                            editor.commit();
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

    @Override
    public void onMasterPWAvailible(String mpw) {
        loadDatabaseAndEntry(mpw);
    }

    @Override
    public void onPasswordAvailible(String pw) {
        Log.d(Constants.TAG, pw);
    }
}
