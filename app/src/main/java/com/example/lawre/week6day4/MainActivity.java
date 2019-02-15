package com.example.lawre.week6day4;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity
{
    DatabaseHelper databaseHelper;
    EditText etName, etPassword;
    private CipherWrapper cipherWrapper;
    private KeystoreWrapper keystoreWrapper;
    private String alias = "Vinz_Clortho";
    private KeyPair keyPair;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            initWrappers();
        } catch (Exception e)
        {
            Log.e("TAG", "onCreate: ", e);
        }
        databaseHelper = new DatabaseHelper(this);
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
    }

    public void onClick(View view) throws BadPaddingException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, InvalidKeyException, IllegalBlockSizeException, NoSuchProviderException, InvalidAlgorithmParameterException {
        if(view.getId() == R.id.btLogin)
        {
            User retrievedUser = databaseHelper.getSingleUser(etName.getText().toString());
            if(retrievedUser != null) //&& retrievedUser.getPassword().equals(encryptPassword(etPassword.getText().toString())))
            {
                handleTransitionToNext(retrievedUser.getName());
            }
            else
            {
                Toast.makeText(this,"Invalid user/password combination",Toast.LENGTH_SHORT).show();
            }
        }
        else if(view.getId() == R.id.btSignUp)
        {
            String safePassword = encryptPassword(etPassword.getText().toString());
            User newUser = new User(etName.getText().toString(),safePassword);
            databaseHelper.insertNewUser(newUser);
            Log.d("TAG_", "onClick: " + databaseHelper.getSingleUser(newUser.getName()));
            handleTransitionToNext(newUser.getName());
        }
    }

    public void initWrappers() throws NoSuchPaddingException, NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
        cipherWrapper = new CipherWrapper("RSA/ECB/PKCS1Padding");
        keystoreWrapper = new KeystoreWrapper(getApplicationContext());
    }

    public String encryptPassword(String password) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, UnrecoverableKeyException, KeyStoreException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        keyPair = keystoreWrapper.createKeyPair(alias);
        String encryptedPassword = cipherWrapper.encrypt(password, keyPair.getPublic());
        return encryptedPassword;
    }

    private void handleTransitionToNext(String userName)
    {
        Intent intent = new Intent(this,PasswordChange.class);
        intent.putExtra("keyPair",keyPair);
        intent.putExtra("alias",alias);
        intent.putExtra("keyStore",keystoreWrapper);
        intent.putExtra("cipher",cipherWrapper);
        intent.putExtra("userName",userName);
        intent.putExtra("db",databaseHelper);
        startActivity(intent);
    }
}
