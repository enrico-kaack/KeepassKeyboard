package ek.de.keepasskeyboard.encryption;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import ek.de.keepasskeyboard.OnPasswordInputed;
import ek.de.keepasskeyboard.R;
import ek.de.keepasskeyboard.wizard.Wizard;

import static ek.de.keepasskeyboard.encryption.AesCbcWithIntegrity.decryptString;
import static ek.de.keepasskeyboard.encryption.AesCbcWithIntegrity.encrypt;
import static ek.de.keepasskeyboard.encryption.AesCbcWithIntegrity.generateKeyFromPassword;
import static ek.de.keepasskeyboard.encryption.AesCbcWithIntegrity.generateSalt;
import static ek.de.keepasskeyboard.encryption.AesCbcWithIntegrity.saltString;

/**
 * Created by Enrico on 16.06.2016.
 */
public class EncryptionModul implements OnPasswordInputed {
    Context context;
    OnPasswordInputed onPasswordHandler;
    OnPasswordInputed onPasswordHandlerLokal;


    public EncryptionModul(Context context) {
        this.context = context;
        onPasswordHandlerLokal = this;
        try {
            onPasswordHandler = (OnPasswordInputed) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


    public void getMasterPW(Activity activity){

        SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        switch (prefs.getInt("ENCRYPTION_MODE", -1)){
            case Wizard.FRAG_MASTER_PW:
                openDialogToGetPW(activity, "Master Password");
                break;
            case Wizard.FRAG_PIN:

                break;
            case Wizard.FRAG_PW:
                openDialogToGetPW(activity, "Encryption Password");
                break;
            case -1:
                break;
        }


    }

    private void openDialogToGetPW(final Activity activity, String title) {
        // Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        //Set Custom Layout
        // Get the layout inflater
       final LayoutInflater inflater = activity.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View dialogView = inflater.inflate(R.layout.dialog_single_input, null);
        builder.setView(dialogView);

        final EditText in_pw = (EditText)dialogView.findViewById(R.id.in_password);

        // Add action buttons
        builder.setPositiveButton("Open DB", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                if (in_pw.getText().toString().length() > 0){
                    onPasswordHandlerLokal.onPasswordAvailible(in_pw.getText().toString());
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public String decryptMPByPassword(String pw){
        try{
            SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            AesCbcWithIntegrity.SecretKeys key;

            String salt = prefs.getString("PW_SALT", null);
            String civ = prefs.getString("ENCRYPTED_MP", null);
            if (salt != null && civ != null){
                AesCbcWithIntegrity.CipherTextIvMac encypted = new AesCbcWithIntegrity.CipherTextIvMac(civ);
                key = generateKeyFromPassword(pw, salt);
                String decryptedText = decryptString(encypted, key);
                return decryptedText;
            }else{
                return null;
            }

        }catch (GeneralSecurityException e){
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }



    public boolean encryptMPByPassword(String pw, String masterPW){
        try {
            AesCbcWithIntegrity.SecretKeys key;

            //Generate Salt value
            String salt = saltString(generateSalt());

            key = generateKeyFromPassword(pw, salt);

            //Decrypt MAsterPW with AES
            AesCbcWithIntegrity.CipherTextIvMac civ = encrypt(masterPW, key);
            SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            SharedPreferences.Editor editor = prefs.edit();


            //Save key and salt in sharedPreference
            editor.putString("PW_SALT", salt);
            editor.putString("ENCRYPTED_MP", civ.toString());

            editor.commit();

            return true;

        }catch(GeneralSecurityException e) {
            Log.d("KEEPASS", e.getMessage());
            return false;
        }catch (UnsupportedEncodingException e2){
            Log.d("KEEPASS", e2.getMessage());
            return false;
        }
    }

    @Override
    public void onMasterPWAvailible(String mpw) {

    }

    @Override
    public void onPasswordAvailible(String pw) {
        SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String masterPW = null;
        switch (prefs.getInt("ENCRYPTION_MODE", -1)){
            case Wizard.FRAG_MASTER_PW:
                onPasswordHandler.onMasterPWAvailible(pw);
                break;
            case Wizard.FRAG_PIN:

                break;
            case Wizard.FRAG_PW:
                onPasswordHandler.onMasterPWAvailible(decryptMPByPassword(pw));
                break;
        }
    }
}
