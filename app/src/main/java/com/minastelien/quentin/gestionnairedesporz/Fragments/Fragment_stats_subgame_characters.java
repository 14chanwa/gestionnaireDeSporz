package com.minastelien.quentin.gestionnairedesporz.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.minastelien.quentin.gestionnairedesporz.Databases.DAO_GameHistory;
import com.minastelien.quentin.gestionnairedesporz.Game.Game;
import com.minastelien.quentin.gestionnairedesporz.R;

/**
 * Displays all played characters for selected game.
 * Created by Quentin on 02/05/16.
 */
public class Fragment_stats_subgame_characters extends Fragment {

    private final String KEY_GAME_KEY = "GAME_KEY";
    private long gameKey;
    private RelativeLayout lay_stats;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            gameKey = savedInstanceState.getLong(KEY_GAME_KEY);
        } else {
            gameKey = getArguments().getLong(KEY_GAME_KEY);
        }
        lay_stats = (RelativeLayout) inflater.inflate(R.layout.fragment_stats_simple, container, false);
        build_characters_list();
        return lay_stats;
    }

    /**
     * Fills the layout with player list from the keyed game from the database.
     */
    private void build_characters_list() {
        DAO_GameHistory dao_gameHistory = new DAO_GameHistory(getActivity());

        ListView lv_stats = (ListView) lay_stats.findViewById(R.id.activity_stats_lv);
        CursorAdapter cursorAdapter = new CursorAdapter(getActivity(), dao_gameHistory.get_characters_list(gameKey), 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                return LayoutInflater.from(context).inflate(R.layout.adapter_sumup_item_row_list, viewGroup, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                TextView tv_ad_stats_nom = (TextView) view.findViewById(R.id.sumup_item_row_list_nom);
                tv_ad_stats_nom.setText(cursor.getString(1));
                TextView tv_ad_stats_role = (TextView) view.findViewById(R.id.sumup_item_row_list_role);
                tv_ad_stats_role.setText(cursor.getString(2));
                TextView tv_ad_stats_gene = (TextView) view.findViewById(R.id.sumup_item_row_list_gene);
                tv_ad_stats_gene.setText(cursor.getString(3));
                TextView tv_ad_stats_mutant = (TextView) view.findViewById(R.id.sumup_item_row_list_mutant);
                tv_ad_stats_mutant.setText(cursor.getInt(5) == 0 ? "" : "Mutant");
                if (cursor.getInt(4) == 1) {
                    tv_ad_stats_nom.setTextColor(context.getResources().getColor(R.color.colorGray));
                    tv_ad_stats_role.setTextColor(context.getResources().getColor(R.color.colorGray));
                    tv_ad_stats_gene.setTextColor(context.getResources().getColor(R.color.colorGray));
                    tv_ad_stats_mutant.setTextColor(context.getResources().getColor(R.color.colorGray));
                } else {
                    tv_ad_stats_nom.setTextColor(context.getResources().getColor(R.color.colorBlackDefault));
                    if (tv_ad_stats_role.getText().equals(Game.getGameSingleton().MEDECIN.getNom())) {
                        tv_ad_stats_role.setTextColor(context.getResources().getColor(R.color.colorGreen));
                    } else if (tv_ad_stats_role.getText().equals(Game.getGameSingleton().MUTANT_DE_BASE.getNom())) {
                        tv_ad_stats_role.setTextColor(context.getResources().getColor(R.color.colorRed));
                    } else {
                        tv_ad_stats_role.setTextColor(context.getResources().getColor(R.color.colorBlackDefault));
                    }
                    tv_ad_stats_gene.setTextColor(context.getResources().getColor(R.color.colorBlackDefault));
                }
            }
        };


        lv_stats.setAdapter(cursorAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_GAME_KEY, gameKey);
    }

}
