package com.carlosgarguz.routeme.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbAssistant extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "routeme.db";
    public static final String TABLE_DESTINATIONS = "t_destinations";
    public static final String TABLE_ROUTES = "t_routes";

    private static final String CREATE_TABLE_ROUTES = "CREATE TABLE " + TABLE_ROUTES + "(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT NOT NULL)";

    private static final String CREATE_TABLE_DESTINATIONS = "CREATE TABLE " + TABLE_DESTINATIONS + "(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "routeName TEXT NOT NULL," +
            "name TEXT NOT NULL," +
            "stopTime INTEGER," +
            "latitude DOUBLE," +
            "longitude DOUBLE)";




    public DbAssistant(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_DESTINATIONS);
        db.execSQL(CREATE_TABLE_ROUTES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DESTINATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTES);
        onCreate(db);
    }
}
