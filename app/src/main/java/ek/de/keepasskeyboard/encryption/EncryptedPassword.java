package ek.de.keepasskeyboard.encryption;

/**
 * Created by Enrico on 20.09.2016.
 */

public class EncryptedPassword {
    private String encPassword;
    private String salt;

    public EncryptedPassword(String encPassword, String salt) {
        this.encPassword = encPassword;
        this.salt = salt;
    }

    public String getEncPassword() {
        return encPassword;
    }

    public void setEncPassword(String encPassword) {
        this.encPassword = encPassword;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
