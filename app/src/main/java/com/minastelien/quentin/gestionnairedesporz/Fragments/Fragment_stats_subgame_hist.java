package com.minastelien.quentin.gestionnairedesporz.Fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.minastelien.quentin.gestionnairedesporz.Databases.DAO_GameHistory;
import com.minastelien.quentin.gestionnairedesporz.R;

/**
 * Displays hist for a selected game.
 * Created by Quentin on 02/05/16.
 */
public class Fragment_stats_subgame_hist extends Fragment {

    private final String KEY_GAME_KEY = "GAME_KEY";
    private long gameKey;

    private CoordinatorLayout lay_hist;
    private TextView tv_hist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            gameKey = savedInstanceState.getLong(KEY_GAME_KEY);
        } else {
            gameKey = getArguments().getLong(KEY_GAME_KEY);
        }
        lay_hist = (CoordinatorLayout) inflater.inflate(R.layout.fragment_hist, container, false);
        build_characters_list();

        // Copy floating button: sets up copy history function.
        FloatingActionButton floatingActionButton = (FloatingActionButton) lay_hist.findViewById(R.id.fragment_hist_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", tv_hist.getText());
                clipboard.setPrimaryClip(clip);
                Toast toast = Toast.makeText(getActivity(), R.string.act_stats_copied_to_clipboard, Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        return lay_hist;
    }

    /**
     * Fills the layout with player list from the keyed game from the database.
     */
    private void build_characters_list() {
        DAO_GameHistory dao_gameHistory = new DAO_GameHistory(getActivity());

        tv_hist = (TextView) lay_hist.findViewById(R.id.act_tour_sub_hist_tv);
        tv_hist.setText(dao_gameHistory.get_hist(gameKey));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_GAME_KEY, gameKey);
    }

}
