package ek.de.keepasskeyboard.wizard;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ek.de.keepasskeyboard.Constants;
import ek.de.keepasskeyboard.encryption.EncryptedPassword;
import ek.de.keepasskeyboard.encryption.EncryptionModul;

/**
 * Created by Enrico on 20.09.2016.
 */

final class WizardData {
    public static int encryptionMode = -1;
    public static String masterPw;
    public static String userPw;
    public static String deviceMac;
    public static String deviceEncryptionPw;

    public static Context context;

    public static void commit(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        EncryptionModul encryptionModul = new EncryptionModul(context);
        SharedPreferences.Editor ed = sharedPref.edit();

        //TODO: Input validation
        EncryptedPassword pw = encryptionModul.encryptMPByPassword(userPw, masterPw);
        EncryptedPassword devicePw = encryptionModul.encryptMPByPassword(userPw, deviceEncryptionPw);
        ed.putString(Constants.ENCRYPTED_MP, pw.getEncPassword());
        ed.putString(Constants.PW_SALT, pw.getSalt());
        ed.putInt(Constants.ENCRYPTION_MODE, encryptionMode);
        ed.putString(Constants.DEVICE_MAC, deviceMac);
        ed.putString(Constants.DEVICE_ENCRYPTION_KEY + "_" + deviceMac, devicePw.getEncPassword());
        ed.putString(Constants.DEVICE_SALT + "_" + deviceMac, devicePw.getSalt());

        ed.commit();
    }
}
