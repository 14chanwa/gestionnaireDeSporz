package com.minastelien.quentin.gestionnairedesporz.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.minastelien.quentin.gestionnairedesporz.Game.Game;
import com.minastelien.quentin.gestionnairedesporz.Game.Gene;
import com.minastelien.quentin.gestionnairedesporz.Game.Role;

/**
 * Provides access to the database. Defines the tables.
 * Created by Quentin on 03/02/2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static DbSingleton dbSingleton;

    /**
     * Basic constructor for DatabaseHandler
     *
     * @param context Application context
     * @param name    Database file name
     * @param factory CursorFactory
     * @param version Current database version number
     */
    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * Call this method to get the gameSingleton instance of DbSingleton
     *
     * @return Instance of DbSingleton.
     */
    public static DbSingleton getDbSingleton() {
        if (dbSingleton == null) {
            dbSingleton = new DbSingleton();
        }
        return dbSingleton;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String JEU_TABLE_CREATE =
                "CREATE TABLE " + getDbSingleton().GAME_TABLE_NAME + " (" +
                        getDbSingleton().GAME_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        getDbSingleton().GAME_TURN_NUMBER + " INTEGER, " +
                        getDbSingleton().GAME_GAME_HIST + " TEXT, " +
                        getDbSingleton().GAME_TIMESTAMP + " INTEGER, " +
                        "UNIQUE(" + getDbSingleton().GAME_KEY + ", " + getDbSingleton().GAME_TIMESTAMP + ") ON CONFLICT REPLACE" +
                        ");";
        final String ROLE_TABLE_CREATE =
                "CREATE TABLE " + getDbSingleton().ROLE_TABLE_NAME + " (" +
                        getDbSingleton().ROLE_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        getDbSingleton().ROLE_NOM + " TEXT, " +
                        getDbSingleton().ROLE_CONTAMINE_DEPART + " INTEGER, " +
                        getDbSingleton().ROLE_CAMP_DEPART_MUTANT + " INTEGER, " +
                        getDbSingleton().ROLE_DESCRIPTION + " TEXT, " +
                        "UNIQUE(" + getDbSingleton().ROLE_KEY + ", " + getDbSingleton().ROLE_NOM + ") ON CONFLICT REPLACE" +
                        ");";
        final String GENE_TABLE_CREATE =
                "CREATE TABLE " + getDbSingleton().GENE_TABLE_NAME + " (" +
                        getDbSingleton().GENE_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        getDbSingleton().GENE_NOM + " TEXT, " +
                        "UNIQUE(" + getDbSingleton().GENE_KEY + ", " + getDbSingleton().GENE_NOM + ") ON CONFLICT REPLACE" +
                        ");";
        final String PERSONNAGE_TABLE_CREATE =
                "CREATE TABLE " + getDbSingleton().CHARACTER_TABLE_NAME + " (" +
                        getDbSingleton().CHARACTER_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        getDbSingleton().CHARACTER_JEU_FK + " INTEGER, " +
                        getDbSingleton().CHARACTER_ROLE_FK + " INTEGER, " +
                        getDbSingleton().CHARACTER_GENE_FK + " INTEGER, " +
                        getDbSingleton().CHARACTER_NOM + " TEXT, " +
                        getDbSingleton().CHARACTER_MORT + " INTEGER, " +
                        getDbSingleton().CHARACTER_CONTAMINE + " INTEGER, " +
                        getDbSingleton().CHARACTER_PARALYSE + " INTEGER, " +
                        getDbSingleton().CHARACTER_MORT_CONFIRME + " INTEGER," +
                        "FOREIGN KEY(" + getDbSingleton().CHARACTER_JEU_FK + ") REFERENCES " + getDbSingleton().GAME_TABLE_NAME + '(' + getDbSingleton().GAME_KEY + ')' +
                        "FOREIGN KEY(" + getDbSingleton().CHARACTER_ROLE_FK + ") REFERENCES " + getDbSingleton().ROLE_TABLE_NAME + '(' + getDbSingleton().ROLE_KEY + ')' +
                        "FOREIGN KEY(" + getDbSingleton().CHARACTER_GENE_FK + ") REFERENCES " + getDbSingleton().GENE_TABLE_NAME + '(' + getDbSingleton().GENE_KEY + ')' +
                        ");";
        final String PLAYER_LISTS_TABLE_CREATE =
                "CREATE TABLE " + getDbSingleton().PLAYER_LISTS_TABLE_NAME + " (" +
                        getDbSingleton().PLAYER_LISTS_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        getDbSingleton().PLAYER_LISTS_NAME + " TEXT " +
                        ");";
        final String PLAYER_NAMES_TABLE_CREATE =
                "CREATE TABLE " + getDbSingleton().PLAYER_NAMES_TABLE_NAME + " (" +
                        getDbSingleton().PLAYER_NAMES_NAME + " TEXT, " +
                        getDbSingleton().PLAYER_NAMES_LIST_FK + " INTEGER, " +
                        "FOREIGN KEY(" + getDbSingleton().PLAYER_NAMES_LIST_FK + ") REFERENCES " + getDbSingleton().PLAYER_LISTS_TABLE_NAME + '(' + getDbSingleton().PLAYER_LISTS_KEY + ')' +
                        ");";

        db.execSQL(JEU_TABLE_CREATE);
        db.execSQL(ROLE_TABLE_CREATE);
        db.execSQL(GENE_TABLE_CREATE);
        db.execSQL(PERSONNAGE_TABLE_CREATE);
        db.execSQL(PLAYER_LISTS_TABLE_CREATE);
        db.execSQL(PLAYER_NAMES_TABLE_CREATE);

        // Initial fill
        fill_gene(db);
        fill_role(db);

        // [versionCode 16 versionName 2.06] Checkpoints
        final String CHECKPOINT_TABLE_CREATE =
                "CREATE TABLE " + getDbSingleton().CHECKPOINT_TN + " (" +
                        getDbSingleton().CHECKPOINT_PK + " INTEGER PRIMARY KEY);";
        final String CHECKPOINT_CHARACTERS_TABLE_CREATE =
                "CREATE TABLE " + getDbSingleton().CHECKPOINT_CHARACTERS_TN + " (" +
                        getDbSingleton().CHECKPOINT_CHARACTERS_FK_CHECKPOINT + " INTEGER, " +
                        getDbSingleton().CHECKPOINT_CHARACTERS_NOM + " TEXT, " +
                        getDbSingleton().CHECKPOINT_CHARACTERS_MORT + " INTEGER, " +
                        getDbSingleton().CHECKPOINT_CHARACTERS_CONTAMINE + " INTEGER, " +
                        getDbSingleton().CHECKPOINT_CHARACTERS_PARALYSE + " INTEGER, " +
                        getDbSingleton().CHECKPOINT_CHARACTERS_MORT_CONFIRME + " INTEGER, " +
                        getDbSingleton().CHECKPOINT_CHARACTERS_VIVANT_DEB_TOUR + " INTEGER, " +
                        "FOREIGN KEY(" + getDbSingleton().CHECKPOINT_CHARACTERS_FK_CHECKPOINT + ") REFERENCES " + getDbSingleton().CHECKPOINT_TN + '(' + getDbSingleton().CHECKPOINT_PK + ')' +
                        "UNIQUE(" + getDbSingleton().CHECKPOINT_CHARACTERS_FK_CHECKPOINT + ", " + getDbSingleton().CHECKPOINT_CHARACTERS_NOM + ") ON CONFLICT REPLACE" +
                        ");";

        final String CHECKPOINT_ROLE_A_JOUE_NUIT_TABLE_CREATE =
                "CREATE TABLE " + getDbSingleton().CHECKPOINT_ROLE_A_JOUE_NUIT_TN + " (" +
                        getDbSingleton().CHECKPOINT_ROLE_A_JOUE_NUIT_FK_CHECKPOINT + " INTEGER, " +
                        getDbSingleton().CHECKPOINT_ROLE_A_JOUE_NUIT_FK_ROLE + " INTEGER, " +
                        "FOREIGN KEY(" + getDbSingleton().CHECKPOINT_ROLE_A_JOUE_NUIT_FK_CHECKPOINT + ") REFERENCES " + getDbSingleton().CHECKPOINT_TN + '(' + getDbSingleton().CHECKPOINT_PK + ')' +
                        "FOREIGN KEY(" + getDbSingleton().CHECKPOINT_ROLE_A_JOUE_NUIT_FK_ROLE + ") REFERENCES " + getDbSingleton().ROLE_TABLE_NAME + '(' + getDbSingleton().ROLE_KEY + ')' +
                        "UNIQUE(" + getDbSingleton().CHECKPOINT_ROLE_A_JOUE_NUIT_FK_CHECKPOINT + ", " + getDbSingleton().CHECKPOINT_ROLE_A_JOUE_NUIT_FK_ROLE + ") ON CONFLICT REPLACE" +
                        ");";

        final String CHECKPOINT_VISITES_NUIT_TABLE_CREATE =
                "CREATE TABLE " + getDbSingleton().CHECKPOINT_VISITES_NUIT_TN + " (" +
                        getDbSingleton().CHECKPOINT_VISITES_NUIT_FK_CHECKPOINT + " INTEGER, " +
                        getDbSingleton().CHECKPOINT_VISITES_NUIT_FK_CHARACTER + " TEXT, " +
                        getDbSingleton().CHECKPOINT_VISITES_NUIT_FK_VISITED + " TEXT, " +
                        "FOREIGN KEY(" + getDbSingleton().CHECKPOINT_VISITES_NUIT_FK_CHECKPOINT + ") REFERENCES " + getDbSingleton().CHECKPOINT_TN + '(' + getDbSingleton().CHECKPOINT_PK + ')' +
                        "FOREIGN KEY(" + getDbSingleton().CHECKPOINT_VISITES_NUIT_FK_CHARACTER + ") REFERENCES " + getDbSingleton().CHECKPOINT_CHARACTERS_TN + '(' + getDbSingleton().CHECKPOINT_CHARACTERS_NOM + ')' +
                        "UNIQUE(" + getDbSingleton().CHECKPOINT_VISITES_NUIT_FK_CHECKPOINT + ", " + getDbSingleton().CHECKPOINT_VISITES_NUIT_FK_CHARACTER + ", " + getDbSingleton().CHECKPOINT_VISITES_NUIT_FK_VISITED + ") ON CONFLICT REPLACE" +
                        ");";

        final String CHECKPOINT_ACTIONS_TOUR_NUIT_TABLE_CREATE =
                "CREATE TABLE " + getDbSingleton().CHECKPOINT_ACTIONS_TOUR_NUIT_TN + " (" +
                        getDbSingleton().CHECKPOINT_ACTIONS_TOUR_NUIT_FK_CHECKPOINT + " INTEGER, " +
                        getDbSingleton().CHECKPOINT_ACTIONS_TOUR_NUIT_FK_CHARACTER + " TEXT, " +
                        getDbSingleton().CHECKPOINT_ACTIONS_TOUR_NUIT_ACTION + " INTEGER, " +
                        "FOREIGN KEY(" + getDbSingleton().CHECKPOINT_ACTIONS_TOUR_NUIT_FK_CHECKPOINT + ") REFERENCES " + getDbSingleton().CHECKPOINT_TN + '(' + getDbSingleton().CHECKPOINT_PK + ')' +
                        "FOREIGN KEY(" + getDbSingleton().CHECKPOINT_ACTIONS_TOUR_NUIT_FK_CHARACTER + ") REFERENCES " + getDbSingleton().CHECKPOINT_CHARACTERS_TN + '(' + getDbSingleton().CHECKPOINT_CHARACTERS_NOM + ')' +
                        "UNIQUE(" + getDbSingleton().CHECKPOINT_ACTIONS_TOUR_NUIT_FK_CHECKPOINT + ", " + getDbSingleton().CHECKPOINT_ACTIONS_TOUR_NUIT_FK_CHARACTER + ", " + getDbSingleton().CHECKPOINT_ACTIONS_TOUR_NUIT_ACTION + ") ON CONFLICT REPLACE" +
                        ");";

        final String CHECKPOINT_RESULTATS_ROLE_HACKER_TABLE_CREATE =
                "CREATE TABLE " + getDbSingleton().CHECKPOINT_RESULTATS_ROLE_HACKER_TN + " (" +
                        getDbSingleton().CHECKPOINT_RESULTATS_ROLE_HACKER_FK_CHECKPOINT + " INTEGER, " +
                        getDbSingleton().CHECKPOINT_RESULTATS_ROLE_HACKER_ACTION_RESULT + " INTEGER, " +
                        "FOREIGN KEY(" + getDbSingleton().CHECKPOINT_RESULTATS_ROLE_HACKER_FK_CHECKPOINT + ") REFERENCES " + getDbSingleton().CHECKPOINT_TN + '(' + getDbSingleton().CHECKPOINT_PK + ')' +
                        "UNIQUE(" + getDbSingleton().CHECKPOINT_RESULTATS_ROLE_HACKER_FK_CHECKPOINT + ", " + getDbSingleton().CHECKPOINT_RESULTATS_ROLE_HACKER_ACTION_RESULT + ") ON CONFLICT REPLACE" +
                        ");";

        db.execSQL(CHECKPOINT_TABLE_CREATE);
        db.execSQL(CHECKPOINT_CHARACTERS_TABLE_CREATE);
        db.execSQL(CHECKPOINT_ROLE_A_JOUE_NUIT_TABLE_CREATE);
        db.execSQL(CHECKPOINT_VISITES_NUIT_TABLE_CREATE);
        db.execSQL(CHECKPOINT_ACTIONS_TOUR_NUIT_TABLE_CREATE);
        db.execSQL(CHECKPOINT_RESULTATS_ROLE_HACKER_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 2 && newVersion == 3) {
            // Adding role description column.
            final String ROLE_TABLE_2_TO_3 = "ALTER TABLE " + getDbSingleton().ROLE_TABLE_NAME + " ADD COLUMN " + getDbSingleton().ROLE_DESCRIPTION + " TEXT";
            db.execSQL(ROLE_TABLE_2_TO_3);
            for (Role r : Game.getGameSingleton().ROLES_LIST) {
                ContentValues args = new ContentValues();
                args.put(getDbSingleton().ROLE_DESCRIPTION, r.getDescription());
                db.update(getDbSingleton().ROLE_TABLE_NAME,
                        args,
                        getDbSingleton().ROLE_NOM + "= ?",
                        new String[]{r.getNom()});
            }
        } else {
            final String JEU_TABLE_DROP = "DROP TABLE IF EXISTS " + getDbSingleton().GAME_TABLE_NAME + ";";
            final String ROLE_TABLE_DROP = "DROP TABLE IF EXISTS " + getDbSingleton().ROLE_TABLE_NAME + ";";
            final String GENE_TABLE_DROP = "DROP TABLE IF EXISTS " + getDbSingleton().GENE_TABLE_NAME + ";";
            final String PERSONNAGE_TABLE_DROP = "DROP TABLE IF EXISTS " + getDbSingleton().CHARACTER_TABLE_NAME + ";";

            final String PLAYER_LISTS_TABLE_DROP = "DROP TABLE IF EXISTS " + getDbSingleton().PLAYER_LISTS_TABLE_NAME + ";";
            final String PLAYER_NAMES_TABLE_DROP = "DROP TABLE IF EXISTS " + getDbSingleton().PLAYER_NAMES_TABLE_NAME + ";";

            final String CHECKPOINT_TABLE_DROP = "DROP TABLE IF EXISTS " + getDbSingleton().CHECKPOINT_TN + ";";
            final String CHECKPOINT_CHARACTERS_TABLE_DROP = "DROP TABLE IF EXISTS " + getDbSingleton().CHECKPOINT_CHARACTERS_TN + ";";
            final String CHECKPOINT_ROLE_A_JOUE_NUIT_TABLE_DROP = "DROP TABLE IF EXISTS " + getDbSingleton().CHECKPOINT_ROLE_A_JOUE_NUIT_TN + ";";
            final String CHECKPOINT_VISITES_NUIT_TABLE_DROP = "DROP TABLE IF EXISTS " + getDbSingleton().CHECKPOINT_VISITES_NUIT_TN + ";";
            final String CHECKPOINT_ACTIONS_TOUR_NUIT_TABLE_DROP = "DROP TABLE IF EXISTS " + getDbSingleton().CHECKPOINT_ACTIONS_TOUR_NUIT_TN + ";";
            final String CHECKPOINT_RESULTATS_ROLE_HACKER_TABLE_DROP = "DROP TABLE IF EXISTS " + getDbSingleton().CHECKPOINT_RESULTATS_ROLE_HACKER_TN + ";";

            db.execSQL(PLAYER_LISTS_TABLE_DROP);
            db.execSQL(PLAYER_NAMES_TABLE_DROP);

            db.execSQL(CHECKPOINT_RESULTATS_ROLE_HACKER_TABLE_DROP);
            db.execSQL(CHECKPOINT_ACTIONS_TOUR_NUIT_TABLE_DROP);
            db.execSQL(CHECKPOINT_VISITES_NUIT_TABLE_DROP);
            db.execSQL(CHECKPOINT_ROLE_A_JOUE_NUIT_TABLE_DROP);
            db.execSQL(CHECKPOINT_CHARACTERS_TABLE_DROP);
            db.execSQL(CHECKPOINT_TABLE_DROP);

            db.execSQL(PERSONNAGE_TABLE_DROP);
            db.execSQL(GENE_TABLE_DROP);
            db.execSQL(ROLE_TABLE_DROP);
            db.execSQL(JEU_TABLE_DROP);

            onCreate(db);
        }
    }

    /**
     * Fills the Gene table with data from the GameSingleton instance in the input database.
     *
     * @param db Input database.
     */
    private void fill_gene(SQLiteDatabase db) {
        for (Gene g : Game.getGameSingleton().GENES_LIST) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(getDbSingleton().GENE_NOM, g.toString());
            db.insert(getDbSingleton().GENE_TABLE_NAME, null, contentValues);
        }
    }

    /**
     * Fills the Role table with data from the GameSingleton instance in the input database.
     *
     * @param db Input database.
     */
    private void fill_role(SQLiteDatabase db) {
        for (Role r : Game.getGameSingleton().ROLES_LIST) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(getDbSingleton().ROLE_NOM, r.getNom());
            contentValues.put(getDbSingleton().ROLE_CONTAMINE_DEPART, r.isContamine_depart() ? 1 : 0);
            contentValues.put(getDbSingleton().ROLE_CAMP_DEPART_MUTANT, r.getStart_side() == Role.Side.MUTANT ? 1 : 0);
            contentValues.put(getDbSingleton().ROLE_DESCRIPTION, r.getDescription());
            db.insert(getDbSingleton().ROLE_TABLE_NAME, null, contentValues);
        }
    }

    /**
     * This class is gameSingleton storage for table and attribute names.
     */
    public static class DbSingleton {
        public final String GAME_TABLE_NAME = "Jeu";
        public final String GAME_KEY = "_id";
        public final String GAME_TURN_NUMBER = "j_num_tour";
        public final String GAME_GAME_HIST = "j_hist_jeu";
        public final String GAME_TIMESTAMP = "j_timestamp";

        public final String ROLE_TABLE_NAME = "Role";
        public final String ROLE_KEY = "_id";
        public final String ROLE_NOM = "r_nom";
        public final String ROLE_CONTAMINE_DEPART = "r_cont_depart";
        public final String ROLE_CAMP_DEPART_MUTANT = "r_camp_depart_mutant";
        public final String ROLE_DESCRIPTION = "r_description";

        public final String GENE_TABLE_NAME = "Gene";
        public final String GENE_KEY = "_id";
        public final String GENE_NOM = "g_nom";

        public final String CHARACTER_TABLE_NAME = "Personnage";
        public final String CHARACTER_KEY = "_id";
        public final String CHARACTER_JEU_FK = "_jeu";
        public final String CHARACTER_ROLE_FK = "_role";
        public final String CHARACTER_GENE_FK = "_gene";
        public final String CHARACTER_NOM = "p_nom";
        public final String CHARACTER_MORT = "p_mort";
        public final String CHARACTER_CONTAMINE = "p_contamine";
        public final String CHARACTER_PARALYSE = "p_paralyse";
        public final String CHARACTER_MORT_CONFIRME = "p_mort_confirme";

        public final String PLAYER_LISTS_TABLE_NAME = "PlayerLists";
        public final String PLAYER_LISTS_KEY = "_id";
        public final String PLAYER_LISTS_NAME = "l_n";

        public final String PLAYER_NAMES_TABLE_NAME = "PlayerNames";
        public final String PLAYER_NAMES_LIST_FK = "_list";
        public final String PLAYER_NAMES_NAME = "p_n";

        public final String CHECKPOINT_TN = "CheckpointList";
        public final String CHECKPOINT_PK = "_id";

        public final String CHECKPOINT_CHARACTERS_TN = "CheckpointCharacters";
        public final String CHECKPOINT_CHARACTERS_FK_CHECKPOINT = "_checkpoint";
//        public final String CHECKPOINT_CHARACTERS_FK_ROLE = CHARACTER_ROLE_FK;
//        public final String CHECKPOINT_CHARACTERS_FK_GENE = CHARACTER_GENE_FK;
        public final String CHECKPOINT_CHARACTERS_NOM = CHARACTER_NOM;
        public final String CHECKPOINT_CHARACTERS_MORT = CHARACTER_MORT;
        public final String CHECKPOINT_CHARACTERS_CONTAMINE = CHARACTER_CONTAMINE;
        public final String CHECKPOINT_CHARACTERS_PARALYSE = CHARACTER_PARALYSE;
        public final String CHECKPOINT_CHARACTERS_MORT_CONFIRME = CHARACTER_MORT_CONFIRME;
        // Useless but implemented
        public final String CHECKPOINT_CHARACTERS_VIVANT_DEB_TOUR = "p_vivant_deb_tour";

        public final String CHECKPOINT_ROLE_A_JOUE_NUIT_TN = "CheckpointRoleAJoueNuit";
        public final String CHECKPOINT_ROLE_A_JOUE_NUIT_FK_CHECKPOINT = CHECKPOINT_CHARACTERS_FK_CHECKPOINT;
        public final String CHECKPOINT_ROLE_A_JOUE_NUIT_FK_ROLE = CHARACTER_ROLE_FK;

        public final String CHECKPOINT_VISITES_NUIT_TN = "CheckpointVisitesNuit";
        public final String CHECKPOINT_VISITES_NUIT_FK_CHECKPOINT = CHECKPOINT_CHARACTERS_FK_CHECKPOINT;
        public final String CHECKPOINT_VISITES_NUIT_FK_CHARACTER = "_p_name";
        public final String CHECKPOINT_VISITES_NUIT_FK_VISITED = "_p_name_visited";

        public final String CHECKPOINT_ACTIONS_TOUR_NUIT_TN = "CheckpointActionsTourNuit";
        public final String CHECKPOINT_ACTIONS_TOUR_NUIT_FK_CHECKPOINT = CHECKPOINT_CHARACTERS_FK_CHECKPOINT;
        public final String CHECKPOINT_ACTIONS_TOUR_NUIT_FK_CHARACTER = CHECKPOINT_VISITES_NUIT_FK_CHARACTER;
        public final String CHECKPOINT_ACTIONS_TOUR_NUIT_ACTION = "action";

        public final String CHECKPOINT_RESULTATS_ROLE_HACKER_TN = "CheckpointResultatRoleHacker";
        public final String CHECKPOINT_RESULTATS_ROLE_HACKER_FK_CHECKPOINT = CHECKPOINT_CHARACTERS_FK_CHECKPOINT;
        public final String CHECKPOINT_RESULTATS_ROLE_HACKER_ACTION_RESULT = "hackerActionResult";

        // Could be useful if loading everything after reboot for instance
        // but if only to rollback role, this does not change
        //public final String CHECKPOINT_RESULTATS_VOTES_JOUR_TN = "CheckpointResultatsVotesJour";

        private DbSingleton() {
        }
    }
}