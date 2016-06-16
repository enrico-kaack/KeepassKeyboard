package ek.de.keepasskeyboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import static ek.de.keepasskeyboard.AesCbcWithIntegrity.decryptString;
import static ek.de.keepasskeyboard.AesCbcWithIntegrity.encrypt;
import static ek.de.keepasskeyboard.AesCbcWithIntegrity.generateKeyFromPassword;
import static ek.de.keepasskeyboard.AesCbcWithIntegrity.generateSalt;
import static ek.de.keepasskeyboard.AesCbcWithIntegrity.saltString;

/**
 * Created by Enrico on 16.06.2016.
 */
public class EncryptionModul {
    Context context;


    public EncryptionModul(Context context) {
        this.context = context;
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
}
