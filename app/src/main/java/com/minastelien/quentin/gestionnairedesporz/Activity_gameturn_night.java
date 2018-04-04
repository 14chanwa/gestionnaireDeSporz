package com.minastelien.quentin.gestionnairedesporz;

import com.minastelien.quentin.gestionnairedesporz.Databases.DAO_Checkpoint;
import com.minastelien.quentin.gestionnairedesporz.Fragments.Fragment_gameturn;
import com.minastelien.quentin.gestionnairedesporz.Fragments.Fragment_gameturn_night;
import com.minastelien.quentin.gestionnairedesporz.Game.Character;
import com.minastelien.quentin.gestionnairedesporz.Utilities.Dates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This activity provides the contents for a night game turn. Initializes the turn resetting night
 * parameters. Triggers the day fragment creation and adds it.
 * Created by Quentin on 28/12/2015.
 */
public class Activity_gameturn_night extends Activity_gameturn {

    @Override
    protected String getTabName() {
        return "Nuit";
    }

    @Override
    public Fragment_gameturn new_Fragment_game_instance() {
        return new Fragment_gameturn_night();
    }

    @Override
    protected void begin_gameturn() {
        // Ecrire le début de la nuit
        gameSingleton.getCurrent_game().setTurn_count(gameSingleton.getCurrent_game().getTurn_count() + 1);
        if (gameSingleton.getCurrent_game().getGame_hist() == null) {
            gameSingleton.getCurrent_game().setGame_hist("");
        } else {
            gameSingleton.getCurrent_game().addHist_jeu("*********************\n" + "Nuit " + gameSingleton.getCurrent_game().getTurn_count() + "\n" + Dates.date() + "\n*********************\n\n");
        }

        {
            String s = "Les personnages en jeu sont :\n";
            for (Character p : gameSingleton.getCurrent_game().getCharacters()) {
                if (!p.isMort()) {
                    s += p.getNom() + ", " + p.getRole();
                    if (!p.getGene().equals(gameSingleton.NORMAL)) {
                        s += ", " + p.getGene();
                    }
                    if (p.isContamine()) {
                        s += ", Contaminé";
                    }
                    s += "\n";
                }
            }
            s += "\n";
            gameSingleton.getCurrent_game().addHist_jeu(s);
        }

        {
            String s = "";
            boolean first_write = true;
            for (Character p : gameSingleton.getCurrent_game().getCharacters()) {
                if (p.isMort()) {
                    if (first_write) {
                        s += "Les personnages morts sont :";
                        first_write = false;
                    }
                    s += p.getNom() + ", " + p.getRole();
                    if (!p.getGene().equals(gameSingleton.NORMAL)) {
                        s += ", " + p.getGene();
                    }
                    s += "\n";
                }
            }
            if (!first_write) {
                s += "\n";
            }
            gameSingleton.getCurrent_game().addHist_jeu(s);
        }

        // [versionCode 16 versionName 2.06] reset night counter
        gameSingleton.current_role_index = 0;

        // [versionCode 16 versionName 2.06] clear all checkpoints
        DAO_Checkpoint dao_checkpoint = new DAO_Checkpoint(this);
        dao_checkpoint.remove_all_checkpoints();

        // Remise à zéro de tous les paramètres
        gameSingleton.visites_tour_nuit = new HashMap<>();
        gameSingleton.actions_tour_nuit = new HashMap<>();
        gameSingleton.role_a_joue_nuit = new HashSet<>();
        gameSingleton.resultat_role_hacker = new HashSet<>();
        for (Character p : gameSingleton.getCurrent_game().getCharacters()) {
            p.setParalyse(false);
        }
        initialiser_liste_persos_vivants();
    }

    private void initialiser_liste_persos_vivants() {
        gameSingleton.personnages_vivants_debut_tour = new ArrayList<>();
        for (Character p : gameSingleton.getCurrent_game().getCharacters()) {
            if (!p.isMort()) {
                gameSingleton.personnages_vivants_debut_tour.add(p);
            }
        }
    }
}
