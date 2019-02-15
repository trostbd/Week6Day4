package com.example.lawre.week6day4;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.security.KeyPairGeneratorSpec;
import android.util.Log;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Calendar;

import javax.security.auth.x500.X500Principal;

public class KeystoreWrapper implements Parcelable
{
    private KeyStore keystore;
    private Context context;

    public KeystoreWrapper(Context context) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException
    {
        this.context = context;
        initWrapper();
    }

    protected KeystoreWrapper(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<KeystoreWrapper> CREATOR = new Creator<KeystoreWrapper>() {
        @Override
        public KeystoreWrapper createFromParcel(Parcel in) {
            return new KeystoreWrapper(in);
        }

        @Override
        public KeystoreWrapper[] newArray(int size) {
            return new KeystoreWrapper[size];
        }
    };

    public void initWrapper() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException
    {
        keystore = KeyStore.getInstance("AndroidKeyStore");
        keystore.load(null);
    }

    public KeyPair createKeyPair(String alias) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException
    {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA","AndroidKeyStore");
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.YEAR, 10);
        KeyPairGeneratorSpec keyPairGeneratorSpec = new KeyPairGeneratorSpec.Builder(context)
                .setAlias(alias)
                .setSerialNumber(BigInteger.ONE)
                .setSubject(new X500Principal("CN =${alias} CA Certificate"))
                .setStartDate(startDate.getTime())
                .setEndDate(endDate.getTime())
                .build();
        keyPairGenerator.initialize(keyPairGeneratorSpec);
        return keyPairGenerator.generateKeyPair();
    }

    public KeyPair getAsymKey(String alias) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException
    {
        Log.d("TAG_", "getAsymKey: keystore " + keystore);
        PrivateKey privateKey = (PrivateKey)keystore.getKey(alias,null);
        Log.d("TAG_", "getAsymKey: privateKey " + privateKey);

        PublicKey publicKey = keystore.getCertificate(alias).getPublicKey();
        if(privateKey != null && publicKey != null)
        {
            Log.d("TAG_", "getAsymKey: ");
            return new KeyPair(publicKey,privateKey);
        }
        else
        {
            Log.d("TAG_", "getAsymKey: ");
            return null;
        }
    }

    public void removeKey(String alias) throws KeyStoreException {
        keystore.deleteEntry(alias);
    }
}
