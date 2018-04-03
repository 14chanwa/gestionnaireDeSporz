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
import com.minastelien.quentin.gestionnairedesporz.R;

/**
 * Displays a simple role list from the database.
 */
public class Fragment_stats_roles extends Fragment {

    private RelativeLayout lay_stats;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        lay_stats = (RelativeLayout) inflater.inflate(R.layout.fragment_stats_simple, container, false);
        build_role_list();
        return lay_stats;
    }

    /**
     * Fills the layout with role data from the database.
     */
    private void build_role_list() {
        DAO_GameHistory dao_gameHistory = new DAO_GameHistory(getActivity());

        ListView lv_stats = lay_stats.findViewById(R.id.activity_stats_lv);
        CursorAdapter cursorAdapter = new CursorAdapter(getActivity(), dao_gameHistory.get_roles_list(), 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                return LayoutInflater.from(context).inflate(R.layout.adapter_stats_roles, viewGroup, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                TextView tv_ad_stats = view.findViewById(R.id.adapter_stats_roles_tv_left);
                tv_ad_stats.setText(cursor.getString(1));
                TextView tv_ad_stats_camp = view.findViewById(R.id.adapter_stats_roles_tv_right);
                tv_ad_stats_camp.setText(cursor.getString(2));
                TextView tv_ad_stats_desc = view.findViewById(R.id.adapter_stats_roles_tv_bottom);
                tv_ad_stats_desc.setText(cursor.getString(3).replace("\n", " "));
            }
        };
        lv_stats.setAdapter(cursorAdapter);
    }

}
