package com.minastelien.quentin.gestionnairedesporz.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.minastelien.quentin.gestionnairedesporz.Activity_gameturn;
import com.minastelien.quentin.gestionnairedesporz.Activity_welcome;
import com.minastelien.quentin.gestionnairedesporz.Databases.DAO_GameHistory;
import com.minastelien.quentin.gestionnairedesporz.Game.Game;
import com.minastelien.quentin.gestionnairedesporz.R;

/**
 * Abstract Fragment class to be used as base for game turn fragments.
 * Created by Quentin on 29/12/2015.
 */
public abstract class Fragment_gameturn extends Fragment_main {

    protected RelativeLayout layout_game;
    protected RelativeLayout layout_endgame;
    private TextView tv_endgame_content;

    /**
     * Method updating fragment data. To be redefined in children classes.
     */
    public void update() {
        String resume_fin_tour = "";
        if (((Activity_gameturn) getActivity()).mutants_win()) {
            resume_fin_tour += "Les mutants gagnent !";
        } else {
            resume_fin_tour += "Les humains gagnent !";
        }
        tv_endgame_content.setText(resume_fin_tour);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    public void init_layout(View lay_jeu) {
        tv_endgame_content = (TextView) lay_jeu.findViewById(R.id.act_fin_jeu_tv_contenu);
        Button but_hist = (Button) lay_jeu.findViewById(R.id.act_fin_jeu_bouton_afficher_hist);
        Button but_end = (Button) lay_jeu.findViewById(R.id.act_fin_jeu_bouton);

        but_hist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog_save_hist();
            }
        });

        but_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DAO_GameHistory dao_gameHistory = new DAO_GameHistory(getActivity());
                dao_gameHistory.save_game_metadata();
                Intent nextActivity = new Intent(getActivity(), Activity_welcome.class);
                startActivity(nextActivity);
            }
        });
        but_hist.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Builds and shows the save history dialog.
     */
    private void showDialog_save_hist() {

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog_hist");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        Dialog_save_hist newFragment = new Dialog_save_hist();
        newFragment.setCancelable(false);
        newFragment.show(ft, "dialog_hist");
    }

    public abstract static class Dialog_fin_tour extends DialogFragment {

        protected String dialog_text;

        /**
         * Creates a dialog displaying plain text. To be particularized by children classes.
         *
         * @param savedInstanceState Bundled saved parameters.
         * @return Dialog instance.
         */
        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            RelativeLayout lay_diag = (RelativeLayout) RelativeLayout.inflate(getActivity(), R.layout.dialog_endturn, null);
            TextView tv = (TextView) lay_diag.findViewById(R.id.dialog_fin_tour_tv);
            tv.setText(dialog_text);
            builder.setView(lay_diag);
            // Create the AlertDialog object and return it
            return builder.create();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            getDialog().setCanceledOnTouchOutside(false);
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.dialog_text = getArguments().getString("texte_dial");
        }
    }

    /**
     * This is a dialog for the user to copy the game history.
     */
    public static class Dialog_save_hist extends DialogFragment {

        private Game.GameSingleton gameSingleton;

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            gameSingleton = Game.getGameSingleton();

            // Use the Builder class for convenient dialog construction
            RelativeLayout lay_diag = (RelativeLayout) RelativeLayout.inflate(getActivity(), R.layout.dialog_savehist, null);
            EditText edittext = (EditText) lay_diag.findViewById(R.id.enreg_hist_tv);
            edittext.setText(gameSingleton.getCurrent_game().getGame_hist());
            edittext.setFocusableInTouchMode(true);
            edittext.requestFocus();
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
            builder.setCancelable(false);
            builder.setView(lay_diag);
            builder.setPositiveButton(R.string.finish_game, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Fin
                    dialog.dismiss();
                }
            });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}
