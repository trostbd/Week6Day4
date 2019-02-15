package com.example.lawre.week6day4;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class PasswordChange extends AppCompatActivity
{
    String TAG = "TAG_PASS";
    private KeyPair keyPair;
    private String alias;
    TextView tvDisplayPassword;
    EditText etChangePassword;
    private CipherWrapper cipherWrapper;
    private KeystoreWrapper keystoreWrapper;
    private String userName;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);
        keyPair = (KeyPair) getIntent().getSerializableExtra("keyPair");
        alias = getIntent().getStringExtra("alias");
        cipherWrapper = (CipherWrapper)getIntent().getSerializableExtra("cipher");
        keystoreWrapper = (KeystoreWrapper)getIntent().getSerializableExtra("keyStore");
        userName = getIntent().getStringExtra("userName");
        etChangePassword = findViewById(R.id.etChangePassword);
        databaseHelper = (DatabaseHelper)getIntent().getSerializableExtra("db");
        User retrievedUser = databaseHelper.getSingleUser(userName);
        Log.d(TAG, "onCreate: user " + retrievedUser.getName());
        tvDisplayPassword = findViewById(R.id.tvDisplayPassword);
        String displayText = null;
        try {
            displayText = "Current password: " + decryptPassword(retrievedUser.getPassword());
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            Log.d(TAG, "onCreate: Invalid key");
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        tvDisplayPassword.setText(displayText);
    }

    public void onClick(View view) throws BadPaddingException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, InvalidKeyException, IllegalBlockSizeException, NoSuchProviderException, InvalidAlgorithmParameterException {
        String newPassword = encryptPassword(etChangePassword.getText().toString());
        User updatedUser = new User(userName,newPassword);
        databaseHelper.updateUser(updatedUser);
    }

    public String encryptPassword(String password) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, UnrecoverableKeyException, KeyStoreException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        String encryptedPassword = cipherWrapper.encrypt(password, keyPair.getPublic());
        return encryptedPassword;
    }

    public String decryptPassword(String password) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        String decryptedPassword = cipherWrapper.decrypt(password, keyPair.getPrivate());
        return decryptedPassword;
    }
}
