package com.minastelien.quentin.gestionnairedesporz;

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
