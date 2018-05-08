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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This activity generates a role composition according to user settings.
 * Created by Quentin on 28/12/2015.
 */
public class Activity_setup_subr_dist extends Activity_main {

    private Adapter_role_dist dist_adapter;
    private ArrayList<Role> roles_a_choisir;

    private TextView tv_resistants;
    private TextView tv_hotes;

    /**
     * Builds a menu for the game master to tweak settings. Generates a role distribution.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        roles_a_choisir = new ArrayList<>();
        for (Role r : gameSingleton.ROLES_LIST) {
            if (!r.equals(gameSingleton.SIMPLE_ASTRONAUTE)) {
                roles_a_choisir.add(r);
            }
        }

        RelativeLayout layout = (RelativeLayout) RelativeLayout.inflate(this, R.layout.activity_setup_subr_dist, null);

        TextView textView = layout.findViewById(R.id.dialog_distrib_pers_tv);
        String phrase = "Nombre de joueurs : " + gameSingleton.getCurrent_game().getCharacters().size();
        textView.setText(phrase);

        // Initialisation ligne hotes
        tv_hotes = layout.findViewById(R.id.dialog_distrib_pers_hotes_tv);
        Button button_left_hotes = layout.findViewById(R.id.dialog_distrib_pers_hotes_but_left);
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
        Button button_right_hotes = layout.findViewById(R.id.dialog_distrib_pers_hotes_but_right);
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
        tv_resistants = layout.findViewById(R.id.dialog_distrib_pers_resistants_tv);
        Button button_left_resistants = layout.findViewById(R.id.dialog_distrib_pers_resistants_but_left);
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
        Button button_right_resistants = layout.findViewById(R.id.dialog_distrib_pers_resistants_but_right);
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
        ListView dist_lv = layout.findViewById(R.id.dialog_distrib_lv);
        dist_adapter = new Adapter_role_dist(this, R.layout.adapter_dist_roles, roles_a_choisir, gameSingleton);
        dist_lv.setAdapter(dist_adapter);

        // Initialisation boutons
        Button bouton_annuler = layout.findViewById(R.id.activity_setup_subr_dist_bout_annuler);
        bouton_annuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button bouton_suivant = layout.findViewById(R.id.activity_setup_subr_dist_bout_suivant);
        bouton_suivant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Role> roles_choisis = new ArrayList<Role>(dist_adapter.getChoix());

                if (roles_choisis.size() > gameSingleton.getCurrent_game().getCharacters().size()) {
                    Toast.makeText(getApplicationContext(), "ERREUR : il y a " + roles_choisis.size() + " roles et " + gameSingleton.getCurrent_game().getCharacters().size() + " joueurs  !", Toast.LENGTH_SHORT).show();
                } else {

                    // [2.04] Fixed issue that made the distribution biased (roles were not "that" shuffled)

                    /*
                     * Distribution des rôles
                     */

                    // Ajoute autant de simples astronautes que nécessaire
                    // [versionCode 15 versionName 2.05] Fixed fatal bug ("+1" below)
//                    for (int i = 0; i < gameSingleton.getCurrent_game().getCharacters().size() + 1 - roles_choisis.size(); i++) {
                    // [versionCode 17 versionName 2.07] Hopefully fix the IndexOutOfBoundsException below
                    while (roles_choisis.size() < gameSingleton.getCurrent_game().getCharacters().size()) {
                        roles_choisis.add(gameSingleton.SIMPLE_ASTRONAUTE);
                    }

                    // On mélange les rôles et on les distribue dans cet ordre aux personnages
                    Collections.shuffle(roles_choisis);
                    for (int i = 0; i < gameSingleton.getCurrent_game().getCharacters().size(); i++) {
                        gameSingleton.getCurrent_game().getCharacters().get(i).setRole(roles_choisis.get(i));
                    }

                    /*
                     * Distribution des génomes
                     */

                    // Les mutants de base sont forcément hôtes
                    for (Character p : gameSingleton.getCurrent_game().getCharacters()) {
                        if (!p.getRole().equals(gameSingleton.MUTANT_DE_BASE)) {
                            p.setGene(gameSingleton.NORMAL);
                        } else {
                            p.setGene(gameSingleton.HOTE);
                        }
                    }

                    ArrayList<Character> liste_pers_pour_genomes = new ArrayList<>(gameSingleton.getCurrent_game().getCharacters());
                    Collections.shuffle(liste_pers_pour_genomes);

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

        // [2.04] Add restore state to keep config across screen rotation
        if (savedInstanceState != null) {
            tv_hotes.setText(String.valueOf(savedInstanceState.getInt("Hotes")));
            tv_resistants.setText(String.valueOf(savedInstanceState.getInt("Resistants")));
            for (Role role : dist_adapter.roles_count.keySet()) {
                dist_adapter.roles_count.put(role, savedInstanceState.getInt(role.getNom()));
            }
        }

        setContentView(layout);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // [2.04] Add save state to keep config across screen rotation
        int nb_hotes = 0;
        try {
            nb_hotes = Integer.parseInt(tv_hotes.getText().toString());
        } catch (NumberFormatException e) {
            // Nothing
        }
        savedInstanceState.putInt("Hotes", nb_hotes);
        int nb_resistants = 0;
        try {
            nb_resistants = Integer.parseInt(tv_resistants.getText().toString());
        } catch (NumberFormatException e) {
            // Nothing
        }
        savedInstanceState.putInt("Resistants", nb_resistants);
        for (Role role : dist_adapter.roles_count.keySet()) {
            savedInstanceState.putInt(role.getNom(), dist_adapter.roles_count.get(role));
        }
    }
}
