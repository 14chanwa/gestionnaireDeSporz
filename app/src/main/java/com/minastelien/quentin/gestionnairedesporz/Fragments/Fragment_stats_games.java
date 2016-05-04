package com.minastelien.quentin.gestionnairedesporz.Fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.minastelien.quentin.gestionnairedesporz.Activity_stats_subgame;
import com.minastelien.quentin.gestionnairedesporz.Databases.DAO_GameHistory;
import com.minastelien.quentin.gestionnairedesporz.R;
import com.minastelien.quentin.gestionnairedesporz.Utilities.Dates;

/**
 * Displays played games.
 */
public class Fragment_stats_games extends Fragment {

    private final String KEY_TIMESTAMP = "TIMESTAMP_KEY";
    private final String KEY_GAME_KEY = "GAME_KEY";
    private RelativeLayout lay_stats;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        lay_stats = (RelativeLayout) inflater.inflate(R.layout.fragment_stats_legends, container, false);
        build_games_list();
        return lay_stats;
    }

    /**
     * Fills the layout with game list from the database.
     */
    private void build_games_list() {
        final DAO_GameHistory dao_gameHistory = new DAO_GameHistory(getActivity());

        ListView lv_stats = (ListView) lay_stats.findViewById(R.id.activity_stats_lv);
        CursorAdapter cursorAdapter = new CursorAdapter(getActivity(), dao_gameHistory.get_games_list(), 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                return LayoutInflater.from(context).inflate(R.layout.adapter_stats_2items_crossable, viewGroup, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                TextView tv_ad_stats = (TextView) view.findViewById(R.id.adapter_stats_roles_tv_left);
                tv_ad_stats.setText(Dates.date_from_unix_time(cursor.getLong(1)));
                TextView tv_ad_stats_count = (TextView) view.findViewById(R.id.adapter_stats_roles_tv_right);
                tv_ad_stats_count.setText(cursor.getInt(2) + "");
            }
        };
        lv_stats.setAdapter(cursorAdapter);

        lv_stats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Get the key from the database given the position read from the cursor.
                Cursor c = dao_gameHistory.get_games_list();
                c.moveToPosition(i);
                // Begin intent, transfer game key and timestamp.
                Intent nextActivity = new Intent(getActivity(), Activity_stats_subgame.class);
                Bundle bundle = new Bundle();
                bundle.putLong(KEY_GAME_KEY, c.getLong(0));
                bundle.putString(KEY_TIMESTAMP, Dates.date_from_unix_time(c.getLong(1)));
                nextActivity.putExtras(bundle);
                startActivity(nextActivity);
            }
        });


        // Legends
        TextView tv_leg_left = (TextView) lay_stats.findViewById(R.id.activity_stats_tv_leg_left);
        TextView tv_leg_right = (TextView) lay_stats.findViewById(R.id.activity_stats_tv_leg_right);
        tv_leg_left.setText(R.string.listview_legends_time_date);
        tv_leg_right.setText(R.string.listview_legends_character_count);
    }

}
