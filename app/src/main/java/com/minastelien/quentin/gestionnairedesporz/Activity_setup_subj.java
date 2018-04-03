package com.minastelien.quentin.gestionnairedesporz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.minastelien.quentin.gestionnairedesporz.Databases.DAO_PlayerNames;
import com.minastelien.quentin.gestionnairedesporz.Game.Character;
import com.minastelien.quentin.gestionnairedesporz.Game.Game;
import com.minastelien.quentin.gestionnairedesporz.Game.Role;
import com.minastelien.quentin.gestionnairedesporz.Utilities.Dates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * In this activity, the user can enter the names of the players.
 * Created by Quentin on 28/12/2015.
 */
public class Activity_setup_subj extends Activity_main {

    private RelativeLayout subj_lay;
    private TextView subj_tv;
    private EditText subj_et;
    private Button subj_bout_ok;
    private Button subj_bout_suiv;
    private ListView subj_lv;
    private ArrayAdapter<Character> subj_adapter;
    /**
     * Compares a character with an other using their names.
     */
    private Comparator<Character> comparateur_personnage = new Comparator<Character>() {
        @Override
        public int compare(Character lhs, Character rhs) {
            return lhs.getNom().compareTo(rhs.getNom());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        subj_lay = (RelativeLayout) RelativeLayout.inflate(this, R.layout.activity_setup_subj, null);
        setContentView(subj_lay);
        build_subj();
    }

    /**
     * Initializes the layout and the listeners.
     */
    private void build_subj() {
        // Initialisation du haut du layout
        subj_tv = (TextView) subj_lay.findViewById(R.id.act_setup_subj_tv);
        subj_et = (EditText) subj_lay.findViewById(R.id.act_setup_subj_et);
        subj_bout_ok = (Button) subj_lay.findViewById(R.id.act_setup_subj_bout_ok);
        subj_bout_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nom = (subj_et.getText()).toString().trim();

                boolean nom_est_doublon = false;
                for (Character p : gameSingleton.getCurrent_game().getCharacters()) {
                    if (p.getNom().equals(nom)) {
                        nom_est_doublon = true;
                        break;
                    }
                }

                if (gameSingleton.FORBIDDEN_PLAYER_NAMES.contains(nom)) {
                    Toast.makeText(getApplicationContext(), "Ce nom est interdit !", Toast.LENGTH_SHORT).show();
                } else if (nom_est_doublon) {
                    Toast.makeText(getApplicationContext(), "Ce personnage existe déjà.", Toast.LENGTH_SHORT).show();
                } else {
                    gameSingleton.getCurrent_game().getCharacters().add(new Character(nom.toString()));
                    refresh();
                    subj_adapter.notifyDataSetChanged();
                }
            }
        });

        // Initialisation de la liste des personnages
        subj_lv = (ListView) subj_lay.findViewById(R.id.act_setup_subj_lv);
        Collections.sort(gameSingleton.getCurrent_game().getCharacters(), comparateur_personnage);
        subj_adapter = new ArrayAdapter<Character>(this, android.R.layout.simple_list_item_1, gameSingleton.getCurrent_game().getCharacters());
        subj_lv.setAdapter(subj_adapter);
        registerForContextMenu(subj_lv);

        // Initialisation du bouton suivant
        subj_bout_suiv = (Button) subj_lay.findViewById(R.id.act_setup_subj_bout_suiv);
        subj_bout_suiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (subj_adapter.getCount() > 2) {
                    // Passage à l'écran suivant
                    Intent deuxiemeActivite = new Intent(Activity_setup_subj.this, Activity_setup_subr.class);
                    startActivity(deuxiemeActivite);
                } else {
                    Toast.makeText(getApplicationContext(), "ERREUR : il faut au moins 3 joueurs !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Refreshes the text view and the clears the edit text, and sorts the characters.
     */
    private void refresh() {
        subj_et.setText("");
        subj_tv.setText(getString(R.string.act_setup_nombre_noms) + subj_adapter.getCount());
        Collections.sort(gameSingleton.getCurrent_game().getCharacters(), comparateur_personnage);
    }

    @Override
    /**
     * If long pressed on the adapter view, shows a delete dialog.
     */
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v == subj_lv) {
            menu.add(Menu.NONE, 0, 0, getString(R.string.delete));
        }
    }

    @Override
    /**
     * If delete is pressed in the context menu, deletes the character and refresh.
     */
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        subj_adapter.remove(subj_adapter.getItem(info.position));
        refresh();
        subj_adapter.notifyDataSetChanged();
        return true;
    }

    @Override
    /**
     * If back is pressed, shows a dialog asking confirmation, then quits if yes is pressed.
     */
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setMessage(R.string.warning_quitter)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Activity_setup_subj.this.finish();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    /**
     * [versionCode 14 versionName 2.05] Builds the options menu.
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_players, menu);
        return true;
    }

    @Override
    /**
     * [versionCode 14 versionName 2.05] Builds the options menu with two options; builds the listeners.
     * Enables the player to save or load a list of player names
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Handle save button
        if (id == R.id.menu_players_save) {

            if (Game.getGameSingleton().getCurrent_game().getCharacters().size() < 1) {
                Toast.makeText(getApplicationContext(), "Aucun joueur à sauvegarder !", Toast.LENGTH_SHORT).show();
                return true;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // Get the layout inflater
            LayoutInflater inflater = getLayoutInflater();

            // Get the text edit
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_save_player_list, null);
            final EditText m_edit_text = (EditText) layout.findViewById(R.id.save_player_list);

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(layout)
                    // Add action buttons
                    .setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // On click postponed
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Just close
                        }
                    })
                    .setTitle("Enregistrer la liste")
                    .setCancelable(false);

            final AlertDialog alertDialog = builder.create();
            alertDialog.show();

            Button posButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            posButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DAO_PlayerNames dao_playerNames = new DAO_PlayerNames(getApplicationContext());
                    if (m_edit_text.getText().toString().length() > 0) {
                        dao_playerNames.save_player_list(m_edit_text.getText().toString());
                        alertDialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "ERREUR : nom invalide !", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return true;
        }

        if (id == R.id.menu_players_load) {

            // Create a new dialog
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // Get the layout inflater
            LayoutInflater inflater = getLayoutInflater();

            // Get the text edit
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_load_player_list, null);
            final ListView list_view = (ListView) layout.findViewById(R.id.load_player_list);

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(layout)
                    .setTitle("Charger une liste");

            final AlertDialog alertDialog =
                     builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // Just close
                                }
                            })
                            .create();
            alertDialog.show();

            final DAO_PlayerNames dao_playerNames = new DAO_PlayerNames(getApplicationContext());
            final CursorAdapter cursorAdapter = new CursorAdapter(this, dao_playerNames.get_player_lists(), 0) {
                @Override
                public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                    return LayoutInflater.from(context).inflate(R.layout.adapter_load_player_list, viewGroup, false);
                }

                @Override
                public void bindView(View view, final Context context, final Cursor cursor) {

                    final String list_name = cursor.getString(1);
                    final int list_id = cursor.getInt(0);
                    final int list_player_count = cursor.getInt(2);

                    TextView tv_name = (TextView) view.findViewById(R.id.load_player_list_tv);
                    tv_name.setText(list_name);
                    TextView tv_count = (TextView) view.findViewById(R.id.load_player_list_tv_count);
                    tv_count.setText("" + list_player_count);

                    // If click on element, load list
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Cursor c = dao_playerNames.get_player_names_from_list_key(list_id);

                            List<Character> char_list = Game.getGameSingleton().getCurrent_game().getCharacters();
                            char_list.clear();

                            while (c.moveToNext()) {
                                String pname = c.getString(0);
                                char_list.add(new Character(pname));
                            }
                            c.close();

                            alertDialog.dismiss();
                            recreate();
                        }
                    });

                    // If long click on element, query delete
                    view.setOnLongClickListener(new View.OnLongClickListener() {


                        @Override
                        public boolean onLongClick(View view) {

                            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                            dialog.setMessage("Supprimer la liste ?")
                                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            // Nothing
                                        }
                                    })
                                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialoginterface, int i) {
                                            // Delete the line
                                            dao_playerNames.remove_player_list_from_key(list_id);
                                            alertDialog.dismiss();
                                            // TODO: how to refresh the adapter? for now, just dismiss the dialog and pop it again
                                        }
                                    })
                                    .show();
                            return true;

                        }
                    });
                }
            };
            list_view.setAdapter(cursorAdapter);
        }

        return super.onOptionsItemSelected(item);
    }
}
