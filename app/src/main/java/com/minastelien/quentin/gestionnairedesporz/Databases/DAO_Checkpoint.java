package com.minastelien.quentin.gestionnairedesporz.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.minastelien.quentin.gestionnairedesporz.Game.Character;
import com.minastelien.quentin.gestionnairedesporz.Game.Game;
import com.minastelien.quentin.gestionnairedesporz.Game.Role;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.minastelien.quentin.gestionnairedesporz.Game.Game.getGameSingleton;

/**
 * [versionCode 16 versionName 2.06] DAO for saving and loading game checkpoints.
 * Created by 14chanwa on 03/04/18.
 */

public class DAO_Checkpoint extends DAO_Base {

    public DAO_Checkpoint(Context pContext) {
        super(pContext);
    }

    public void remove_checkpoint_items(int role_index) {
        open();

        // Get the DbSingleton from DatabaseHandler
        DatabaseHandler.DbSingleton dbSingleton = DatabaseHandler.getDbSingleton();

        mDb.delete(
                dbSingleton.CHECKPOINT_RESULTATS_ROLE_HACKER_TN,
                dbSingleton.CHECKPOINT_RESULTATS_ROLE_HACKER_FK_CHECKPOINT + "=?",
                new String[]{"" + role_index});
        mDb.delete(
                dbSingleton.CHECKPOINT_ACTIONS_TOUR_NUIT_TN,
                dbSingleton.CHECKPOINT_ACTIONS_TOUR_NUIT_FK_CHECKPOINT + "=?",
                new String[]{"" + role_index});
        mDb.delete(
                dbSingleton.CHECKPOINT_VISITES_NUIT_TN,
                dbSingleton.CHECKPOINT_VISITES_NUIT_FK_CHECKPOINT + "=?",
                new String[]{"" + role_index});
        mDb.delete(
                dbSingleton.CHECKPOINT_ROLE_A_JOUE_NUIT_TN,
                dbSingleton.CHECKPOINT_ROLE_A_JOUE_NUIT_FK_CHECKPOINT + "=?",
                new String[]{"" + role_index});
        mDb.delete(
                dbSingleton.CHECKPOINT_CHARACTERS_TN,
                dbSingleton.CHECKPOINT_CHARACTERS_FK_CHECKPOINT + "=?",
                new String[]{"" + role_index});
        mDb.delete(
                dbSingleton.CHECKPOINT_TN,
                dbSingleton.CHECKPOINT_PK + "=?",
                new String[]{"" + role_index});

        close();
    }

    public void remove_all_checkpoints() {

        open();

        // Get the DbSingleton from DatabaseHandler
        DatabaseHandler.DbSingleton dbSingleton = DatabaseHandler.getDbSingleton();

        mDb.execSQL("DELETE FROM "+ dbSingleton.CHECKPOINT_RESULTATS_ROLE_HACKER_TN);
        mDb.execSQL("DELETE FROM "+ dbSingleton.CHECKPOINT_ACTIONS_TOUR_NUIT_TN);
        mDb.execSQL("DELETE FROM "+ dbSingleton.CHECKPOINT_VISITES_NUIT_TN);
        mDb.execSQL("DELETE FROM "+ dbSingleton.CHECKPOINT_ROLE_A_JOUE_NUIT_TN);
        mDb.execSQL("DELETE FROM "+ dbSingleton.CHECKPOINT_CHARACTERS_TN);
        mDb.execSQL("DELETE FROM "+ dbSingleton.CHECKPOINT_TN);

        close();
    }


    public int load_last_checkpoint() {

        // Get game singleton
        Game.GameSingleton gameSingleton = Game.getGameSingleton();

        open();

        // Get the DbSingleton from DatabaseHandler
        DatabaseHandler.DbSingleton dbSingleton = DatabaseHandler.getDbSingleton();

        // Get last checkpoint id
        int last_checkpoint_id = -1;
        {
            Cursor c = mDb.rawQuery(
                    "SELECT " + dbSingleton.CHECKPOINT_PK +
                    " FROM " + dbSingleton.CHECKPOINT_TN +
                    " ORDER BY " + dbSingleton.CHECKPOINT_PK + " DESC",
                    null);
            if (c.moveToNext()) {
                last_checkpoint_id = c.getInt(0);
            }
            c.close();
        }
        if (last_checkpoint_id == -1)
            return last_checkpoint_id;

        // Modify the characters
        {
            Cursor c = mDb.rawQuery(
                    "SELECT " +
                            dbSingleton.CHECKPOINT_CHARACTERS_NOM + ", " +
                            dbSingleton.CHECKPOINT_CHARACTERS_MORT + ", " +
                            dbSingleton.CHECKPOINT_CHARACTERS_CONTAMINE + ", " +
                            dbSingleton.CHECKPOINT_CHARACTERS_PARALYSE + ", " +
                            dbSingleton.CHECKPOINT_CHARACTERS_MORT_CONFIRME + ", " +
                            dbSingleton.CHECKPOINT_CHARACTERS_VIVANT_DEB_TOUR +
                            " FROM " + dbSingleton.CHECKPOINT_CHARACTERS_TN +
                            " WHERE " + dbSingleton.CHECKPOINT_CHARACTERS_FK_CHECKPOINT + "=" + last_checkpoint_id,
                    null);

            // Clear "vivant deb tour" list
            gameSingleton.personnages_vivants_debut_tour.clear();

            while (c.moveToNext()) {
                // Get character and modify it
                try {
                    Character character = Game.getGameSingleton().getCurrent_game().getCharacterByName(c.getString(0));
                    character.setMort(c.getInt(1) == 1);
                    character.setContamine(c.getInt(2) == 1);
                    character.setParalyse(c.getInt(3) == 1);
                    character.setMort_confirme(c.getInt(4) == 1);

                    // Add the character to the "vivant deb tour" list
                    if (c.getInt(5) == 1) {
                        gameSingleton.personnages_vivants_debut_tour.add(character);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            c.close();
        }

        // Modify the "roles a joue nuit" list
        {
            Cursor c = mDb.rawQuery(
                    "SELECT " +
                            dbSingleton.ROLE_NOM +
                            " FROM " + dbSingleton.CHECKPOINT_ROLE_A_JOUE_NUIT_TN +
                            " JOIN " + dbSingleton.ROLE_TABLE_NAME +
                            " ON " + dbSingleton.ROLE_TABLE_NAME + "." + dbSingleton.ROLE_KEY + "=" + dbSingleton.CHECKPOINT_ROLE_A_JOUE_NUIT_FK_ROLE +
                            " WHERE " + dbSingleton.CHECKPOINT_ROLE_A_JOUE_NUIT_FK_CHECKPOINT + "=" + last_checkpoint_id,
                    null);

            gameSingleton.role_a_joue_nuit.clear();

            while (c.moveToNext()) {
                // Get role and add it
                try {
                    Role role = gameSingleton.getRoleByName(c.getString(0));
                    gameSingleton.role_a_joue_nuit.add(role);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            c.close();
        }

        // Modify the "visites nuit" hash
        {
            Cursor c = mDb.rawQuery(
                    "SELECT " +
                            dbSingleton.CHECKPOINT_VISITES_NUIT_FK_CHARACTER + ", " +
                            dbSingleton.CHECKPOINT_VISITES_NUIT_FK_VISITED +
                            " FROM " + dbSingleton.CHECKPOINT_VISITES_NUIT_TN +
                            " WHERE " + dbSingleton.CHECKPOINT_VISITES_NUIT_FK_CHECKPOINT + "=" + last_checkpoint_id,
                    null);

            // Clear hashmap
            gameSingleton.visites_tour_nuit.clear();

            while (c.moveToNext()) {
                // Get characters
                try {
                    Character char_1 = gameSingleton.getCurrent_game().getCharacterByName(c.getString(0));
                    Character char_2 = gameSingleton.getCurrent_game().getCharacterByName(c.getString(1));
                    if (!gameSingleton.visites_tour_nuit.containsKey(char_1)) {
                        gameSingleton.visites_tour_nuit.put(char_1, new HashSet<Character>());
                    }
                    gameSingleton.visites_tour_nuit.get(char_1).add(char_2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            c.close();
        }

        // Modify the "actions tour nuit" hash
        {
            Cursor c = mDb.rawQuery(
                    "SELECT " +
                            dbSingleton.CHECKPOINT_ACTIONS_TOUR_NUIT_FK_CHARACTER + ", " +
                            dbSingleton.CHECKPOINT_ACTIONS_TOUR_NUIT_ACTION +
                            " FROM " + dbSingleton.CHECKPOINT_ACTIONS_TOUR_NUIT_TN +
                            " WHERE " + dbSingleton.CHECKPOINT_ACTIONS_TOUR_NUIT_FK_CHECKPOINT + "=" + last_checkpoint_id,
                    null);

            // Clear
            gameSingleton.actions_tour_nuit.clear();

            while (c.moveToNext()) {
                // Get character and action
                try {
                    Character character = gameSingleton.getCurrent_game().getCharacterByName(c.getString(0));
                    Game.GameSingleton.Night_action night_action = Game.GameSingleton.Night_action.getInstance(c.getInt(1));

                    if (!gameSingleton.actions_tour_nuit.containsKey(character)) {
                        gameSingleton.actions_tour_nuit.put(character, new HashSet<Game.GameSingleton.Night_action>());
                    }
                    gameSingleton.actions_tour_nuit.get(character).add(night_action);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            c.close();
        }

        // Modify the "resultats role hacker" hash
        {
            Cursor c = mDb.rawQuery(
                    "SELECT " +
                            dbSingleton.CHECKPOINT_RESULTATS_ROLE_HACKER_ACTION_RESULT +
                            " FROM " + dbSingleton.CHECKPOINT_RESULTATS_ROLE_HACKER_TN +
                            " WHERE " + dbSingleton.CHECKPOINT_RESULTATS_ROLE_HACKER_FK_CHECKPOINT + "=" + last_checkpoint_id,
                    null);

            // Clear
            gameSingleton.resultat_role_hacker.clear();

            while (c.moveToNext()) {
                gameSingleton.resultat_role_hacker.add(Game.GameSingleton.Night_action_result.getInstance(c.getInt(0)));
            }
            c.close();
        }

        close();

        // Remove the checkpoint
        remove_checkpoint_items(last_checkpoint_id);

        return last_checkpoint_id;
    }

    /**
     * Gets the role PK by name.
     * IMPORTANT: assumes the db is open and does not close it (thus private access).
     * @param name The name we seek
     * @return Role PK
     * @throws Exception Name not found
     */
    private int get_role_pk_by_name(String name) throws Exception {

        // Get the DbSingleton from DatabaseHandler
        DatabaseHandler.DbSingleton dbSingleton = DatabaseHandler.getDbSingleton();

        Cursor c = mDb.rawQuery(
                "SELECT " +
                        dbSingleton.ROLE_KEY +
                        " FROM " + dbSingleton.ROLE_TABLE_NAME +
                        " WHERE " + dbSingleton.ROLE_NOM + "=?",
                new String[] {name});

        if (c.moveToNext()) {

            int pk = c.getInt(0);

            c.close();

            return pk;
        }

        c.close();

        throw new Exception("Unknown role name (db)");
    }

//    public int get_gene_pk_by_name(String name) throws Exception {
//
//        open();
//
//        // Get the DbSingleton from DatabaseHandler
//        DatabaseHandler.DbSingleton dbSingleton = DatabaseHandler.getDbSingleton();
//
//        Cursor c = mDb.rawQuery(
//                "SELECT " +
//                        dbSingleton.GENE_KEY +
//                        " FROM " + dbSingleton.GENE_TABLE_NAME +
//                        " WHERE " + dbSingleton.GENE_NOM + "=" + name,
//                null);
//
//        if (c.moveToNext()) {
//
//            int pk = c.getInt(0);
//
//            c.close();
//            close();
//
//            return pk;
//        }
//
//        c.close();
//        close();
//
//        throw new Exception("Unknown gene name (db)");
//    }

    public void save_checkpoint() {

        // Get game singleton
        Game.GameSingleton gameSingleton = Game.getGameSingleton();

        // Remove checkpoint if exists
        int checkpoint_id = gameSingleton.current_role_index;
        remove_checkpoint_items(checkpoint_id);

        open();

        // Get the DbSingleton from DatabaseHandler
        DatabaseHandler.DbSingleton dbSingleton = DatabaseHandler.getDbSingleton();

        // Create entry in checkpoint table
        {
            ContentValues values = new ContentValues();
            values.put(dbSingleton.CHECKPOINT_PK, checkpoint_id);
            mDb.insert(dbSingleton.CHECKPOINT_TN, null, values);
        }

        // Create entries in checkpoint characters
        for (Character character : gameSingleton.getCurrent_game().getCharacters()) {
            ContentValues values = new ContentValues();
            values.put(dbSingleton.CHECKPOINT_CHARACTERS_FK_CHECKPOINT, checkpoint_id);
            values.put(dbSingleton.CHECKPOINT_CHARACTERS_NOM, character.getNom());
            values.put(dbSingleton.CHECKPOINT_CHARACTERS_MORT, character.isMort() ? 1 : 0);
            values.put(dbSingleton.CHECKPOINT_CHARACTERS_CONTAMINE, character.isContamine() ? 1 : 0);
            values.put(dbSingleton.CHECKPOINT_CHARACTERS_PARALYSE, character.isParalyse() ? 1 : 0);
            values.put(dbSingleton.CHECKPOINT_CHARACTERS_MORT_CONFIRME, character.isMort_confirme() ? 1 : 0);
            values.put(dbSingleton.CHECKPOINT_CHARACTERS_VIVANT_DEB_TOUR, gameSingleton.personnages_vivants_debut_tour.contains(character) ? 1 : 0);
            mDb.insert(dbSingleton.CHECKPOINT_CHARACTERS_TN, null, values);
        }

        // Create entries for roles a joue nuit
        for (Role role : gameSingleton.role_a_joue_nuit) {
            try {
                int role_pk = get_role_pk_by_name(role.getNom());
                ContentValues values = new ContentValues();
                values.put(dbSingleton.CHECKPOINT_ROLE_A_JOUE_NUIT_FK_CHECKPOINT, checkpoint_id);
                values.put(dbSingleton.CHECKPOINT_ROLE_A_JOUE_NUIT_FK_ROLE, role_pk);
                mDb.insert(dbSingleton.CHECKPOINT_ROLE_A_JOUE_NUIT_TN, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Create entries for visites nuit
        for (Character char_1 : gameSingleton.visites_tour_nuit.keySet()) {
             for (Character char_2 : gameSingleton.visites_tour_nuit.get(char_1)) {
                 ContentValues values = new ContentValues();
                 values.put(dbSingleton.CHECKPOINT_VISITES_NUIT_FK_CHECKPOINT, checkpoint_id);
                 values.put(dbSingleton.CHECKPOINT_VISITES_NUIT_FK_CHARACTER, char_1.getNom());
                 values.put(dbSingleton.CHECKPOINT_VISITES_NUIT_FK_VISITED, char_2.getNom());
                 mDb.insert(dbSingleton.CHECKPOINT_VISITES_NUIT_TN, null, values);
             }
        }

        // Create entries for actions tour nuit
        for (Character character : gameSingleton.actions_tour_nuit.keySet()) {
            for (Game.GameSingleton.Night_action night_action : gameSingleton.actions_tour_nuit.get(character)) {
                ContentValues values = new ContentValues();
                values.put(dbSingleton.CHECKPOINT_ACTIONS_TOUR_NUIT_FK_CHECKPOINT, checkpoint_id);
                values.put(dbSingleton.CHECKPOINT_ACTIONS_TOUR_NUIT_FK_CHARACTER, character.getNom());
                values.put(dbSingleton.CHECKPOINT_ACTIONS_TOUR_NUIT_ACTION, night_action.getValue());
                mDb.insert(dbSingleton.CHECKPOINT_ACTIONS_TOUR_NUIT_TN, null, values);
            }
        }

        // Create entries for resultats role hacker
        for (Game.GameSingleton.Night_action_result result : gameSingleton.resultat_role_hacker) {
            ContentValues values = new ContentValues();
            values.put(dbSingleton.CHECKPOINT_RESULTATS_ROLE_HACKER_FK_CHECKPOINT, checkpoint_id);
            values.put(dbSingleton.CHECKPOINT_RESULTATS_ROLE_HACKER_ACTION_RESULT, result.getValue());
            mDb.insert(dbSingleton.CHECKPOINT_RESULTATS_ROLE_HACKER_TN, null, values);
        }

        close();
    }
}
