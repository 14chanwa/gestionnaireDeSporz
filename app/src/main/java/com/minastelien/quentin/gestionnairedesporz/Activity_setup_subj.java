package com.minastelien.quentin.gestionnairedesporz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.minastelien.quentin.gestionnairedesporz.Game.Character;

import java.util.Collections;
import java.util.Comparator;

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
}
