package dbapp.com.example.dbapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lenovo on 2016-11-07.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Student.db";
    public static final String TABLE_STUDENT = "student_table";
    public static final String TABLE_HISTORY = "history";
    public static final String COL_ID = "ID";
    public static final String COL_NAME = "NAME";
    public static final String COL_SURNAME = "SURNAME";
    public static final String COL_AGE = "AGE";
    public static final String COL_PATH = "PHOTOPATH";
    public static final String COL_OPERATION = "OPERATION";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_STUDENT + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, SURNAME TEXT,AGE INTEGER, PHOTOPATH TEXT)");
        db.execSQL("create table " + TABLE_HISTORY + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, OPERATION TEXT, NAME TEXT, SURNAME TEXT, AGE TEXT)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
    }

    public boolean insertData(String name, String surname, Integer age, String photoPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, name);
        contentValues.put(COL_SURNAME, surname);
        contentValues.put(COL_AGE, age);
        contentValues.put(COL_PATH, photoPath);
        long result = db.insert(TABLE_STUDENT, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean addToHistory(String operation, String name, String age, String surname) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_OPERATION, operation);
        contentValues.put(COL_NAME, name);
        contentValues.put(COL_SURNAME, surname);
        contentValues.put(COL_AGE, age);
        long result = db.insert(TABLE_HISTORY, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_STUDENT, null);
        return res;
    }

    public Cursor getHistory() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_HISTORY, null);
        return res;
    }

    public boolean updateData(String id, String name, String surname, Integer age, String photoPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID, id);
        contentValues.put(COL_NAME, name);
        contentValues.put(COL_SURNAME, surname);
        contentValues.put(COL_AGE, age);
        contentValues.put(COL_PATH, photoPath);
        db.update(TABLE_STUDENT, contentValues, "ID = ?", new String[]{id});
        return true;
    }

    public Integer deleteData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_STUDENT, "ID = ?", new String[]{id});
    }


}
