package com.minastelien.quentin.gestionnairedesporz.Adapters;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.minastelien.quentin.gestionnairedesporz.Game.Character;
import com.minastelien.quentin.gestionnairedesporz.Game.Game;
import com.minastelien.quentin.gestionnairedesporz.R;

import java.util.ArrayList;

/**
 * Adapter used for votes in day game turns.
 * Created by Quentin on 29/12/2015.
 */
public class Adapter_character_vote extends Adapter_character {

    private ArrayList<Character> vote_choices;

    public Adapter_character_vote(Context context, ArrayList<Character> objects, Game.GameSingleton sing) {
        super(context, R.layout.adapter_vote_item_row_list, objects, sing);
        vote_choices = new ArrayList<Character>();
        characters = objects;

        vote_choices.add(gameSingleton.BLANK);

        for (Character p : characters) {
            vote_choices.add(p);
        }

    }

    @Override
    protected void build_view(View vue, final int position) {
        final Character current_character = characters.get(position);
        final ArrayList<Character> liste_choix_sub = new ArrayList<Character>();

        for (Character p : vote_choices) {
            if (!p.equals(current_character)) {
                liste_choix_sub.add(p);
            }
        }

        TextView tv = (TextView) vue.findViewById(R.id.jour_vote_lay_row_list_tv);
        tv.setText(current_character.getNom());

        final Spinner sp_vote = (Spinner) vue.findViewById(R.id.jour_vote_lay_row_list_spinner);
        ArrayAdapter<Character> adapter_choix = new ArrayAdapter<Character>(context, android.R.layout.simple_spinner_item, liste_choix_sub);
        adapter_choix.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp_vote.setAdapter(adapter_choix);
        if (gameSingleton.resultats_votes_jour.get(current_character) != null) {
            sp_vote.setSelection(liste_choix_sub.indexOf(gameSingleton.resultats_votes_jour.get(current_character)));
        } else {
            sp_vote.setSelection(liste_choix_sub.indexOf(gameSingleton.BLANK));
            gameSingleton.resultats_votes_jour.put(current_character, gameSingleton.BLANK);
        }

        sp_vote.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                gameSingleton.resultats_votes_jour.put(current_character, liste_choix_sub.get(pos));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
