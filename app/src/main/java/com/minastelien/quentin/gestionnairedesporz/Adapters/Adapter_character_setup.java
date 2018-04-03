package com.minastelien.quentin.gestionnairedesporz.Adapters;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.minastelien.quentin.gestionnairedesporz.Game.Character;
import com.minastelien.quentin.gestionnairedesporz.Game.Game;
import com.minastelien.quentin.gestionnairedesporz.Game.Gene;
import com.minastelien.quentin.gestionnairedesporz.Game.Role;
import com.minastelien.quentin.gestionnairedesporz.R;

import java.util.ArrayList;

/**
 * Adapter used in player name setup.
 * Created by Quentin on 28/12/2015.
 */
public class Adapter_character_setup extends Adapter_character {

    public Adapter_character_setup(Context context, ArrayList<Character> objects, Game.GameSingleton sing) {
        super(context, R.layout.adapter_setup_subr_item_row_list, objects, sing);
    }

    @Override
    protected void build_view(View view, int position) {

        final Character current_character = characters.get(position);

        TextView tv = view.findViewById(R.id.lay_row_list_tv);
        final Spinner sp_role = view.findViewById(R.id.lay_row_list_spinner_role);
        final Spinner sp_gene = view.findViewById(R.id.lay_row_list_spinner_gene);

        tv.setText(current_character.getNom());

        ArrayAdapter<Role> adapter_role = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, gameSingleton.ROLES_LIST);
        ArrayAdapter<Gene> adapter_gene = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, gameSingleton.GENES_LIST);
        adapter_role.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_gene.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_role.setAdapter(adapter_role);
        sp_gene.setAdapter(adapter_gene);

        sp_role.setSelection(seek_object(current_character.getRole(), gameSingleton.ROLES_LIST));
        sp_gene.setSelection(seek_object(current_character.getGene(), gameSingleton.GENES_LIST));

        sp_role.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View currentView, int position, long id) {
                current_character.setRole(gameSingleton.ROLES_LIST.get(position));
                if (gameSingleton.ROLES_LIST.get(position).equals(gameSingleton.MUTANT_DE_BASE)) {
                    current_character.setContamine(true);
                    current_character.setGene(gameSingleton.HOTE);
                    sp_gene.setSelection(seek_object(current_character.getGene(), gameSingleton.GENES_LIST));
                } else if (gameSingleton.ROLES_LIST.get(position).equals(gameSingleton.MEDECIN)) {
                    current_character.setContamine(false);
                    current_character.setGene(gameSingleton.NORMAL);
                    sp_gene.setSelection(seek_object(current_character.getGene(), gameSingleton.GENES_LIST));
                } else {
                    current_character.setContamine(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nothing
            }
        });

        sp_gene.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View currentView, int position, long id) {
                current_character.setGene(gameSingleton.GENES_LIST.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nothing
            }
        });

    }
}
