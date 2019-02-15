package com.example.lawre.week6day4;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class CipherWrapper implements Parcelable
{
    String transformation;
    private Cipher cipher;

    public CipherWrapper(String transformation) throws NoSuchAlgorithmException, NoSuchPaddingException
    {
        this.transformation = transformation;
        initWrapper();
    }

    protected CipherWrapper(Parcel in) {
        transformation = in.readString();
    }

    public static final Creator<CipherWrapper> CREATOR = new Creator<CipherWrapper>() {
        @Override
        public CipherWrapper createFromParcel(Parcel in) {
            return new CipherWrapper(in);
        }

        @Override
        public CipherWrapper[] newArray(int size) {
            return new CipherWrapper[size];
        }
    };

    private void initWrapper() throws NoSuchPaddingException, NoSuchAlgorithmException
    {
        cipher = Cipher.getInstance(transformation);
    }

    public String encrypt(String data, Key key) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException
    {
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] bytes = cipher.doFinal(data.getBytes());
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public String decrypt(String encryptedData, Key key) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException
    {
        cipher.init(Cipher.DECRYPT_MODE,key);
        Log.d("TAG_CIPHER", "decrypt: " + key);
        byte[] encryptedBytes = Base64.decode(encryptedData,Base64.DEFAULT);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(transformation);
    }
}
