package com.example.lawre.week6day4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper implements Serializable
{
    public static final String DATABASE_NAME = "userdb";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "users";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_PASSWORD = "password";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createQuery = "CREATE TABLE " + TABLE_NAME + "("
                + FIELD_NAME + " TEXT PRIMARY KEY, "
                + FIELD_PASSWORD + " TEXT)";
        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public ArrayList<User> getAllUsers()
    {
        SQLiteDatabase myDB = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor myCur = myDB.rawQuery(query, null);
        if(myCur.moveToFirst())
        {
            ArrayList<User> userList = new ArrayList<>();
            do {
                String name = myCur.getString(myCur.getColumnIndex(FIELD_NAME));
                String password = myCur.getString(myCur.getColumnIndex(FIELD_PASSWORD));
                userList.add(new User(name,password));
            } while (myCur.moveToNext());
            myCur.close();
            return userList;
        }
        else
        {
            myCur.close();
            return null;
        }
    }

    public User getSingleUser(String name)
    {
        User userSingle = null;
        if(name != null && !name.isEmpty())
        {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + FIELD_NAME + " = \"" + name + "\"";
            Cursor cur = db.rawQuery(query, null);
            if(cur.moveToFirst())
            {
                String password = cur.getString(cur.getColumnIndex(FIELD_PASSWORD));
                userSingle = new User(name,password);
            }
        }
        return userSingle;
    }

    public void insertNewUser(User user)
    {
        if(user != null)
        {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(FIELD_NAME,user.getName());
            contentValues.put(FIELD_PASSWORD,user.getPassword());
            db.insert(TABLE_NAME,null,contentValues);
        }
    }

    int deleteUser(User user)
    {
        String name = user.getName();
        String whereClause = FIELD_NAME + " = \"" + name + "\"";
        SQLiteDatabase myDB = this.getWritableDatabase();
        return myDB.delete(TABLE_NAME,whereClause,null);
    }

    void updateUser(User user)
    {
        if(user != null)
        {
            SQLiteDatabase db = getWritableDatabase();
            String whereClause = FIELD_NAME + " = \"" + user.getName() + "\"";
            ContentValues contentValues = new ContentValues();
            contentValues.put(FIELD_NAME,user.getName());
            contentValues.put(FIELD_PASSWORD,user.getPassword());
            db.update(TABLE_NAME,contentValues,whereClause,null);
        }
    }
}
