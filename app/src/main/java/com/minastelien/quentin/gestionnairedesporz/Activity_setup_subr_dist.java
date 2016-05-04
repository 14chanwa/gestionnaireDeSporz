package com.minastelien.quentin.gestionnairedesporz;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.minastelien.quentin.gestionnairedesporz.Adapters.Adapter_role_dist;
import com.minastelien.quentin.gestionnairedesporz.Game.Character;
import com.minastelien.quentin.gestionnairedesporz.Game.Role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * This activity generates a role composition according to user settings.
 * Created by Quentin on 28/12/2015.
 */
public class Activity_setup_subr_dist extends Activity_main {

    private Adapter_role_dist dist_adapter;
    private ArrayList<Role> roles_a_choisir;

    private TextView tv_resistants;
    private TextView tv_hotes;

    @Override
    /**
     * Builds a menu for the game master to tweak settings. Generates a role distribution.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // For use in randomization.
        final long seed = System.nanoTime();

        roles_a_choisir = new ArrayList<>();
        for (Role r : gameSingleton.ROLES_LIST) {
            if (!r.equals(gameSingleton.SIMPLE_ASTRONAUTE)) {
                roles_a_choisir.add(r);
            }
        }

        RelativeLayout layout = (RelativeLayout) RelativeLayout.inflate(this, R.layout.activity_setup_subr_dist, null);

        TextView textView = (TextView) layout.findViewById(R.id.dialog_distrib_pers_tv);
        String phrase = "Nombre de joueurs : " + gameSingleton.getCurrent_game().getCharacters().size();
        textView.setText(phrase);

        // Initialisation ligne hotes
        tv_hotes = (TextView) layout.findViewById(R.id.dialog_distrib_pers_hotes_tv);
        Button button_left_hotes = (Button) layout.findViewById(R.id.dialog_distrib_pers_hotes_but_left);
        button_left_hotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int valeur;
                try {
                    valeur = Integer.parseInt(tv_hotes.getText().toString());
                } catch (NumberFormatException e) {
                    valeur = 0;
                }
                if (valeur > 0) {
                    tv_hotes.setText("" + (valeur - 1));
                }
            }
        });
        Button button_right_hotes = (Button) layout.findViewById(R.id.dialog_distrib_pers_hotes_but_right);
        button_right_hotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int valeur;
                try {
                    valeur = Integer.parseInt(tv_hotes.getText().toString());
                } catch (NumberFormatException e) {
                    valeur = 0;
                }
                tv_hotes.setText("" + (valeur + 1));
            }
        });

        // Initialisation ligne resistants
        tv_resistants = (TextView) layout.findViewById(R.id.dialog_distrib_pers_resistants_tv);
        Button button_left_resistants = (Button) layout.findViewById(R.id.dialog_distrib_pers_resistants_but_left);
        button_left_resistants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int valeur;
                try {
                    valeur = Integer.parseInt(tv_resistants.getText().toString());
                } catch (NumberFormatException e) {
                    valeur = 0;
                }
                if (valeur > 0) {
                    tv_resistants.setText("" + (valeur - 1));
                }
            }
        });
        Button button_right_resistants = (Button) layout.findViewById(R.id.dialog_distrib_pers_resistants_but_right);
        button_right_resistants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int valeur;
                try {
                    valeur = Integer.parseInt(tv_resistants.getText().toString());
                } catch (NumberFormatException e) {
                    valeur = 0;
                }
                tv_resistants.setText("" + (valeur + 1));
            }
        });

        // Initialisaton lv roles
        ListView dist_lv = (ListView) layout.findViewById(R.id.dialog_distrib_lv);
        dist_adapter = new Adapter_role_dist(this, R.layout.adapter_dist_roles, roles_a_choisir, gameSingleton);
        dist_lv.setAdapter(dist_adapter);

        // Initialisation boutons
        Button bouton_annuler = (Button) layout.findViewById(R.id.activity_setup_subr_dist_bout_annuler);
        bouton_annuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button bouton_suivant = (Button) layout.findViewById(R.id.activity_setup_subr_dist_bout_suivant);
        bouton_suivant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Role> roles_choisis = new ArrayList<Role>();

                for (int i = 0; i < dist_adapter.getChoix().size(); i++) {
                    roles_choisis.add(dist_adapter.getChoix().get(i));
                }

                if (roles_choisis.size() > gameSingleton.getCurrent_game().getCharacters().size()) {
                    Toast.makeText(getApplicationContext(), "ERREUR : il y a " + roles_choisis.size() + " roles et " + gameSingleton.getCurrent_game().getCharacters().size() + " joueurs  !", Toast.LENGTH_SHORT).show();
                } else {

                    // Distribution des rôles
                    int longueur = roles_choisis.size();
                    for (int i = 0; i < gameSingleton.getCurrent_game().getCharacters().size() - longueur; i++) {
                        roles_choisis.add(gameSingleton.SIMPLE_ASTRONAUTE);
                    }
                    Collections.shuffle(roles_choisis, new Random(seed));
                    for (int i = 0; i < gameSingleton.getCurrent_game().getCharacters().size(); i++) {
                        gameSingleton.getCurrent_game().getCharacters().get(i).setRole(roles_choisis.get(i));
                    }

                    // Distribution des génomes
                    for (Character p : gameSingleton.getCurrent_game().getCharacters()) {
                        if (!p.getRole().equals(gameSingleton.MUTANT_DE_BASE)) {
                            p.setGene(gameSingleton.NORMAL);
                        } else {
                            p.setGene(gameSingleton.HOTE);
                        }
                    }

                    ArrayList<Character> liste_pers_pour_genomes = (ArrayList) gameSingleton.getCurrent_game().getCharacters().clone();
                    Collections.shuffle(liste_pers_pour_genomes, new Random(seed));

                    int max_hotes = Integer.parseInt(tv_hotes.getText().toString());
                    int compteur_hotes = 0;

                    for (int j = 0; j < liste_pers_pour_genomes.size(); j++) {
                        if (!(compteur_hotes < max_hotes)) {
                            break;
                        } else {
                            if (!liste_pers_pour_genomes.get(j).getGene().equals(gameSingleton.HOTE) && !liste_pers_pour_genomes.get(j).getRole().equals(gameSingleton.MEDECIN)) {
                                liste_pers_pour_genomes.get(j).setGene(gameSingleton.HOTE);
                                compteur_hotes++;
                            }
                        }
                    }

                    int max_resistants = Integer.parseInt(tv_resistants.getText().toString());
                    int compteur_resistants = 0;

                    for (int j = 0; j < liste_pers_pour_genomes.size(); j++) {
                        if (!(compteur_resistants < max_resistants)) {
                            break;
                        } else {
                            if (!liste_pers_pour_genomes.get(j).getGene().equals(gameSingleton.HOTE) && !liste_pers_pour_genomes.get(j).getRole().equals(gameSingleton.MEDECIN)) {
                                liste_pers_pour_genomes.get(j).setGene(gameSingleton.RESISTANT);
                                compteur_resistants++;
                            }
                        }
                    }

                    Toast.makeText(getApplicationContext(), "Rôles distribués  !", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        setContentView(layout);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

    }
}
