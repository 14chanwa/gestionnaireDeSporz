package com.minastelien.quentin.gestionnairedesporz.Fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.minastelien.quentin.gestionnairedesporz.R;

/**
 * Fragment displaying game history.
 * Created by Quentin on 08/01/2016.
 */
public class Fragment_game_hist extends Fragment_main {

    protected CoordinatorLayout lay_hist;

    protected TextView tv_hist;
    protected ScrollView scrollview_hist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        build_hist(inflater);

        // Copy floating button: sets up copy history function.
        FloatingActionButton floatingActionButton = lay_hist.findViewById(R.id.fragment_hist_fab);
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
     * Builds the game history layout.
     *
     * @param inflater Context inflater.
     */
    protected void build_hist(LayoutInflater inflater) {
        lay_hist = (CoordinatorLayout) inflater.inflate(R.layout.fragment_hist, null);
        scrollview_hist = lay_hist.findViewById(R.id.act_tour_sub_hist_scrollview);
        tv_hist = lay_hist.findViewById(R.id.act_tour_sub_hist_tv);
        update_hist();
    }

    /**
     * Updates the displayed text history given new data.
     */
    public void update_hist() {
        tv_hist.setText(gameSingleton.getCurrent_game().getGame_hist());
        scrollview_hist.post(new Runnable() {
            @Override
            public void run() {
                scrollview_hist.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

}
