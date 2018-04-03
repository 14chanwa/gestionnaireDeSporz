package com.minastelien.quentin.gestionnairedesporz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.minastelien.quentin.gestionnairedesporz.Game.Character;
import com.minastelien.quentin.gestionnairedesporz.Game.Game;
import com.minastelien.quentin.gestionnairedesporz.Utilities.Dates;

import java.util.ArrayList;

/**
 * A welcome activity with some settings and game presentation.
 * Created by Quentin on 28/12/2015.
 */
public class Activity_welcome extends Activity_main {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        setTitle(R.string.app_name);

        Button button = findViewById(R.id.act_accueil_bn_commencer);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Game.resetGame();
                Intent nextActivity = new Intent(Activity_welcome.this, Activity_setup_subj.class);
                startActivity(nextActivity);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_accueil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

//        if (id == R.id.menu_accueil_options) {
//            Toast.makeText(getApplicationContext(), R.string.menu_about_text, Toast.LENGTH_SHORT).show();
//            return true;
//        }
        if (id == R.id.menu_accueil_demo) {
            new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setMessage(R.string.menu_accueil_jeu_test)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            start_sample_game();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
            return true;
        }
        if (id == R.id.menu_accueil_stats) {
            Intent secondeActivite = new Intent(Activity_welcome.this, Activity_stats.class);
            startActivity(secondeActivite);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void start_sample_game() {

        Game.resetGame();

        ArrayList<Character> perso = new ArrayList<>();
        perso.add(new Character("Amar", gameSingleton.MEDECIN, gameSingleton.NORMAL, false, false));
        perso.add(new Character("Amaury", gameSingleton.MUTANT_DE_BASE, gameSingleton.HOTE, false, true));
        perso.add(new Character("David", gameSingleton.FANATIQUE, gameSingleton.RESISTANT, false, false));
        perso.add(new Character("Elina", gameSingleton.INFORMATICIEN, gameSingleton.NORMAL, false, false));
        perso.add(new Character("Grégoire", gameSingleton.APPRENTI_HACKER, gameSingleton.NORMAL, false, false));
        perso.add(new Character("Jean", gameSingleton.POLITICIEN, gameSingleton.RESISTANT, false, false));
        perso.add(new Character("Adrien", gameSingleton.HACKER, gameSingleton.NORMAL, false, false));
        perso.add(new Character("Aimée", gameSingleton.PSYCHOLOGUE, gameSingleton.HOTE, false, false));
        perso.add(new Character("Matthieu", gameSingleton.MEDECIN, gameSingleton.NORMAL, false, false));
        perso.add(new Character("Maxime", gameSingleton.PEINTRE, gameSingleton.RESISTANT, false, false));
        perso.add(new Character("Rémi", gameSingleton.GENETICIEN, gameSingleton.NORMAL, false, false));
        perso.add(new Character("Rodolphe", gameSingleton.ESPION, gameSingleton.HOTE, false, false));
        gameSingleton.getCurrent_game().setCharacters(perso);
        gameSingleton.getCurrent_game().setGame_hist("Jeu de démo\n\n");

        String s = "*********************\nDébut du jeu\n" + Dates.date() + "\n";
        s += "*********************\n\nLes rôles sont :\n";
        for (Character p : gameSingleton.getCurrent_game().getCharacters()) {
            s += p.getNom() + ", " + p.getRole();
            if (!p.getGene().equals(gameSingleton.NORMAL)) {
                s += ", " + p.getGene();
            }
            s += "\n";
        }
        s += "\n";
        gameSingleton.getCurrent_game().addHist_jeu(s);


        Intent nextActivity = new Intent(Activity_welcome.this, Activity_sumup.class);
        startActivity(nextActivity);

    }

    /**
     * Overrides the inherited return method: this is already the welcome activity.
     */
    @Override
    protected void retour_menu() {
        gameSingleton = Game.getGameSingleton();
    }

}
