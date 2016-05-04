package com.minastelien.quentin.gestionnairedesporz.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.minastelien.quentin.gestionnairedesporz.Game.Character;
import com.minastelien.quentin.gestionnairedesporz.Game.Game;
import com.minastelien.quentin.gestionnairedesporz.Game.Gene;
import com.minastelien.quentin.gestionnairedesporz.Game.Role;

import java.util.HashMap;

/**
 * DAO for finished games data statistics saves.
 * Created by Quentin on 03/02/2016.
 */
public class DAO_GameHistory extends DAO_Base {

    public DAO_GameHistory(Context pContext) {
        super(pContext);
    }

    /**
     * Saves the game's consistent data in the end of a game, like dead or not, paralyzed or not...
     * Does not save action flags !
     */
    public void save_game_metadata() {
        open();

        // Get the DbSingleton from DatabaseHandler
        DatabaseHandler.DbSingleton dbSingleton = DatabaseHandler.getDbSingleton();

        // Match roles and genes with corresponding keys in the database
        Cursor c;
        HashMap<Gene, Integer> corres_gene = new HashMap<>();
        HashMap<Role, Integer> corres_role = new HashMap<>();

        c = mDb.rawQuery("SELECT " + dbSingleton.GENE_KEY + ", " + dbSingleton.GENE_NOM +
                " FROM " + dbSingleton.GENE_TABLE_NAME, null);
        while (c.moveToNext()) {
            int key = c.getInt(0);
            Gene gene = Game.getGameSingleton().NORMAL;
            for (Gene g : Game.getGameSingleton().GENES_LIST) {
                if (g.toString().equals(c.getString(1))) {
                    gene = g;
                    break;
                }
            }
            corres_gene.put(gene, key);
        }
        c.close();

        c = mDb.rawQuery("SELECT " + dbSingleton.ROLE_KEY + ", " + dbSingleton.ROLE_NOM +
                " FROM " + dbSingleton.ROLE_TABLE_NAME, null);
        while (c.moveToNext()) {
            int key = c.getInt(0);
            Role role = Game.getGameSingleton().SIMPLE_ASTRONAUTE;
            for (Role r : Game.getGameSingleton().ROLES_LIST) {
                if (r.toString().equals(c.getString(1))) {
                    role = r;
                    break;
                }
            }
            corres_role.put(role, key);
        }
        c.close();

        // Creating the game entry in Game
        long unixTime = System.currentTimeMillis() / 1000L;
        ContentValues value = new ContentValues();
        value.put(dbSingleton.GAME_TURN_NUMBER, Game.getGameSingleton().getCurrent_game().getTurn_count());
        value.put(dbSingleton.GAME_GAME_HIST, Game.getGameSingleton().getCurrent_game().getGame_hist());
        value.put(dbSingleton.GAME_TIMESTAMP, unixTime);
        long jeu_rowid = mDb.insert(dbSingleton.GAME_TABLE_NAME, null, value);

        // Creating character entries in Personnage
        for (Character p : Game.getGameSingleton().getCurrent_game().getCharacters()) {
            value = new ContentValues();
            value.put(dbSingleton.CHARACTER_JEU_FK, jeu_rowid);
            value.put(dbSingleton.CHARACTER_ROLE_FK, corres_role.get(p.getRole()));
            value.put(dbSingleton.CHARACTER_GENE_FK, corres_gene.get(p.getGene()));
            value.put(dbSingleton.CHARACTER_NOM, p.getNom());
            value.put(dbSingleton.CHARACTER_MORT, (p.isMort() ? 1 : 0));
            value.put(dbSingleton.CHARACTER_CONTAMINE, (p.isContamine() ? 1 : 0));
            value.put(dbSingleton.CHARACTER_PARALYSE, (p.isParalyse() ? 1 : 0));
            value.put(dbSingleton.CHARACTER_MORT_CONFIRME, (p.isMort_confirme() ? 1 : 0));
            mDb.insert(dbSingleton.CHARACTER_TABLE_NAME, null, value);
        }

        close();
    }

    /**
     * Gets a cursor with 2 attributes: the role's name and whether it is good or evil.
     *
     * @return Cursor with roles data.
     */
    public Cursor get_roles_list() {
        open();
        DatabaseHandler.DbSingleton dbSingleton = DatabaseHandler.getDbSingleton();
        Cursor c = mDb.rawQuery("SELECT " + dbSingleton.ROLE_KEY + ", " +
                dbSingleton.ROLE_NOM + ", " +
                "(CASE WHEN " + dbSingleton.ROLE_CAMP_DEPART_MUTANT + "= 1  THEN \'Mutants\' ELSE \'Astronautes\' END)" + ", " +
                dbSingleton.ROLE_DESCRIPTION +
                " FROM " + dbSingleton.ROLE_TABLE_NAME, null);
        return c;
    }

    /**
     * Gets a cursor with 3 attributes: the games' keys, their timestamp and character count.
     *
     * @return Cursor with games data.
     */
    public Cursor get_games_list() {
        open();
        DatabaseHandler.DbSingleton dbSingleton = DatabaseHandler.getDbSingleton();
        Cursor c = mDb.rawQuery("SELECT " +
                        dbSingleton.GAME_TABLE_NAME + "." + dbSingleton.GAME_KEY + ", " +
                        dbSingleton.GAME_TIMESTAMP + ", " +
                        " COUNT(*) " + " FROM " + dbSingleton.GAME_TABLE_NAME +
                        " JOIN " + dbSingleton.CHARACTER_TABLE_NAME + " ON " +
                        dbSingleton.CHARACTER_TABLE_NAME + "." + dbSingleton.CHARACTER_JEU_FK + "=" +
                        dbSingleton.GAME_TABLE_NAME + "." + dbSingleton.GAME_KEY +
                        " GROUP BY " + dbSingleton.GAME_TABLE_NAME + "." + dbSingleton.GAME_KEY
                , null);
        return c;
    }

    /**
     * Reads the characters in the game pointed by the key.
     *
     * @param key The game's key in table Game.
     * @return Cursor with the game's character list and data.
     */
    public Cursor get_characters_list(long key) {
        open();
        DatabaseHandler.DbSingleton dbSingleton = DatabaseHandler.getDbSingleton();
        Cursor c = mDb.rawQuery("SELECT " +
                dbSingleton.CHARACTER_TABLE_NAME + "." + dbSingleton.CHARACTER_KEY + ", " +
                dbSingleton.CHARACTER_NOM + ", " +
                dbSingleton.ROLE_NOM + ", " +
                dbSingleton.GENE_NOM + ", " +
                dbSingleton.CHARACTER_MORT + ", " +
                dbSingleton.CHARACTER_CONTAMINE + ", " +
                dbSingleton.CHARACTER_PARALYSE +
                " FROM " + dbSingleton.CHARACTER_TABLE_NAME +
                " JOIN " + dbSingleton.ROLE_TABLE_NAME + " ON " + dbSingleton.ROLE_TABLE_NAME + "." + dbSingleton.ROLE_KEY + "=" + dbSingleton.CHARACTER_ROLE_FK +
                " JOIN " + dbSingleton.GENE_TABLE_NAME + " ON " + dbSingleton.GENE_TABLE_NAME + "." + dbSingleton.GENE_KEY + "=" + dbSingleton.CHARACTER_GENE_FK +
                " WHERE " + dbSingleton.CHARACTER_JEU_FK + "=" + key, null);
        return c;
    }

    /**
     * Reads the hist of the game pointed by the key.
     *
     * @param key The game's key in table Game.
     * @return The game's hist as a String.
     */
    public String get_hist(long key) {
        open();
        DatabaseHandler.DbSingleton dbSingleton = DatabaseHandler.getDbSingleton();
        Cursor c = mDb.rawQuery("SELECT " +
                dbSingleton.GAME_GAME_HIST +
                " FROM " + dbSingleton.GAME_TABLE_NAME +
                " WHERE " + dbSingleton.GAME_TABLE_NAME + "." + dbSingleton.GAME_KEY + "=" + key, null);
        if (c.moveToFirst()) {
            return c.getString(0);
        }
        return "Pas d'historique";
    }

}
