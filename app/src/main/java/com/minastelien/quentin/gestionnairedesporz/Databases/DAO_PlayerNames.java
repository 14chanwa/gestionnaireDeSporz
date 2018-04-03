package com.minastelien.quentin.gestionnairedesporz.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.minastelien.quentin.gestionnairedesporz.Game.Character;
import com.minastelien.quentin.gestionnairedesporz.Game.Game;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

/**
 * [versionCode 14 versionName 2.05] DAO for saving and loading player name lists.
 * Created by 14chanwa on 03/04/18.
 */

public class DAO_PlayerNames extends DAO_Base {

    public DAO_PlayerNames(Context pContext) {
        super(pContext);
    }

    public void save_player_list(String list_name) {
        open();

        // Get the DbSingleton from DatabaseHandler
        DatabaseHandler.DbSingleton dbSingleton = DatabaseHandler.getDbSingleton();

        // Create a new entry in lisy
        ContentValues value = new ContentValues();
        value.put(dbSingleton.PLAYER_LISTS_NAME, list_name);
        long list_id = mDb.insert(dbSingleton.PLAYER_LISTS_TABLE_NAME, null, value);

        // Creating character names for the corresponding list
        for (Character p : Game.getGameSingleton().getCurrent_game().getCharacters()) {
            value = new ContentValues();
            value.put(dbSingleton.PLAYER_NAMES_LIST_FK, list_id);
            value.put(dbSingleton.PLAYER_NAMES_NAME, p.getNom());
            mDb.insert(dbSingleton.PLAYER_NAMES_TABLE_NAME, null, value);
        }

        close();
    }

    public Cursor get_player_lists() {
        open();

        // Get the DbSingleton from DatabaseHandler
        DatabaseHandler.DbSingleton dbSingleton = DatabaseHandler.getDbSingleton();

        Cursor c;
        ArrayList<Map.Entry<Integer, String>> list_player_lists = new ArrayList<>();

        c = mDb.rawQuery("SELECT " + dbSingleton.PLAYER_LISTS_KEY + ", " +
                dbSingleton.PLAYER_LISTS_NAME + ", " +
                " COUNT(*) " + " FROM " + dbSingleton.PLAYER_LISTS_TABLE_NAME +
                " JOIN " + dbSingleton.PLAYER_NAMES_TABLE_NAME + " ON " +
                dbSingleton.PLAYER_NAMES_TABLE_NAME + "." + dbSingleton.PLAYER_NAMES_LIST_FK + "=" +
                dbSingleton.PLAYER_LISTS_TABLE_NAME + "." + dbSingleton.PLAYER_LISTS_KEY +
                " GROUP BY " + dbSingleton.PLAYER_LISTS_TABLE_NAME + "." + dbSingleton.PLAYER_LISTS_KEY,
                null);
        return c;
    }

    public Cursor get_player_names_from_list_key(int list_pkey) {
        open();

        // Get the DbSingleton from DatabaseHandler
        DatabaseHandler.DbSingleton dbSingleton = DatabaseHandler.getDbSingleton();

        Cursor c;
        ArrayList<Character> list_characters = new ArrayList<>();

        c = mDb.rawQuery("SELECT " + dbSingleton.PLAYER_NAMES_NAME +
                " FROM " + dbSingleton.PLAYER_NAMES_TABLE_NAME +
                " WHERE " + dbSingleton.PLAYER_NAMES_LIST_FK + "=" + list_pkey, null);
        return c;
    }

    public void remove_player_list_from_key(int list_pkey) {
        open();
        // Get the DbSingleton from DatabaseHandler
        DatabaseHandler.DbSingleton dbSingleton = DatabaseHandler.getDbSingleton();

        // Remove entries in player names table
        mDb.delete(
                dbSingleton.PLAYER_NAMES_TABLE_NAME,
                dbSingleton.PLAYER_NAMES_LIST_FK + "=?",
                new String[]{"" + list_pkey});

        mDb.delete(
                dbSingleton.PLAYER_LISTS_TABLE_NAME,
                dbSingleton.PLAYER_LISTS_KEY + "=?",
                new String[]{"" + list_pkey});
        close();
    }
}
