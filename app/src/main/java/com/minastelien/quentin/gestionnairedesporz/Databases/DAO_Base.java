package com.minastelien.quentin.gestionnairedesporz.Databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Basic class for DAO. Provides methods to open, close and reset the database.
 * Created by Quentin on 03/02/2016.
 */
public abstract class DAO_Base {

    // Database version number
    private final int VERSION = 5;

    // Database filename
    private final String FILENAME = "database.db";

    SQLiteDatabase mDb = null;
    private DatabaseHandler mHandler;

    DAO_Base(Context pContext) {
        this.mHandler = new DatabaseHandler(pContext, FILENAME, null, VERSION);
    }

    /**
     * Opens a data stream to the database.
     *
     * @return Database object.
     */
    SQLiteDatabase open() {
        // getWritableDatabase closes the old data stream if open
        mDb = mHandler.getWritableDatabase();
        return mDb;
    }

    /**
     * Closes the data stream.
     */
    void close() {
        mDb.close();
    }

    public SQLiteDatabase getDb() {
        return mDb;
    }

    public void resetDb(String table_name) {
        open();
        mDb.delete(table_name, "", null);
        close();
    }

    public void reset() {
        open();
        mHandler.onUpgrade(mDb, VERSION, VERSION);
        close();
    }
}
