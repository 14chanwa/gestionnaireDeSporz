package com.minastelien.quentin.gestionnairedesporz.Adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.minastelien.quentin.gestionnairedesporz.Game.Character;
import com.minastelien.quentin.gestionnairedesporz.Game.Game;
import com.minastelien.quentin.gestionnairedesporz.R;

import java.util.ArrayList;

/**
 * Adapter used in player sumups.
 * Created by Quentin on 28/12/2015.
 */
public class Adapter_character_sumup extends Adapter_character {

    public Adapter_character_sumup(Context context, ArrayList<Character> objects, Game.GameSingleton sing) {
        super(context, R.layout.adapter_sumup_item_row_list, objects, sing);
    }

    @Override
    protected void build_view(View vue, int position) {

        final Character current_character = characters.get(position);

        TextView tv_nom = (TextView) vue.findViewById(R.id.sumup_item_row_list_nom);
        TextView tv_role = (TextView) vue.findViewById(R.id.sumup_item_row_list_role);
        TextView tv_gene = (TextView) vue.findViewById(R.id.sumup_item_row_list_gene);
        TextView tv_mutant = (TextView) vue.findViewById(R.id.sumup_item_row_list_mutant);

        if (current_character.isMort()) {
            tv_nom.setTextColor(context.getResources().getColor(R.color.colorGray));
            tv_role.setTextColor(context.getResources().getColor(R.color.colorGray));
            tv_gene.setTextColor(context.getResources().getColor(R.color.colorGray));
            tv_mutant.setTextColor(context.getResources().getColor(R.color.colorGray));
        } else {
            tv_nom.setTextColor(context.getResources().getColor(R.color.colorBlackDefault));
            if (current_character.getRole().equals(gameSingleton.MEDECIN)) {
                tv_role.setTextColor(context.getResources().getColor(R.color.colorGreen));
            } else if (current_character.getRole().equals(gameSingleton.MUTANT_DE_BASE)) {
                tv_role.setTextColor(context.getResources().getColor(R.color.colorRed));
            } else {
                tv_role.setTextColor(context.getResources().getColor(R.color.colorBlackDefault));
            }
            tv_gene.setTextColor(context.getResources().getColor(R.color.colorBlackDefault));
        }


        tv_nom.setText(current_character.getNom());
        tv_role.setText(current_character.getRole().toString());
        if (!current_character.getGene().equals(gameSingleton.NORMAL)) {
            tv_gene.setText(current_character.getGene().toString());
        } else {
            tv_gene.setText("");
        }
        if (current_character.isContamine()) {
            tv_mutant.setText(R.string.mutant);
            tv_mutant.setTextColor(context.getResources().getColor(R.color.colorRed));
        } else if (current_character.isParalyse()) {
            tv_mutant.setText(R.string.paralyse);
            tv_mutant.setTextColor(context.getResources().getColor(R.color.colorOrange));
        } else {
            tv_mutant.setText("");
        }
    }

}
