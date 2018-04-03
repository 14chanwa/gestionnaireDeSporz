package com.minastelien.quentin.gestionnairedesporz;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.minastelien.quentin.gestionnairedesporz.Fragments.Fragment_gameturn;
import com.minastelien.quentin.gestionnairedesporz.Fragments.Fragment_gameturn_day;
import com.minastelien.quentin.gestionnairedesporz.Game.Character;
import com.minastelien.quentin.gestionnairedesporz.Game.Game;
import com.minastelien.quentin.gestionnairedesporz.Utilities.Dates;

import java.util.HashMap;

/**
 * This activity provides the contents for a day gameturn. Initializes the turn providing info
 * about the last night. Triggers the day fragment creation and adds it.
 * Created by Quentin on 28/12/2015.
 */
public class Activity_gameturn_day extends Activity_gameturn {

    @Override
    protected String getTabName() {
        return "Jour";
    }

    @Override
    public Fragment_gameturn new_Fragment_game_instance() {
        return new Fragment_gameturn_day();
    }

    @Override
    protected void begin_gameturn() {
        // Write day turn start history.
        if (gameSingleton.getCurrent_game().getGame_hist() == null) {
            gameSingleton.getCurrent_game().setGame_hist("");
        } else {
            gameSingleton.getCurrent_game().addHist_jeu("*********************\n" + "Jour " + gameSingleton.getCurrent_game().getTurn_count() + "\n" + Dates.date() + "\n*********************\n\n");
        }

        // Reset votes.
        gameSingleton.resultats_votes_jour = new HashMap<>();

        // Show recap dialog.
        build_start_dialog_frame();
    }

    /**
     * Builds dialog according to the preceding night's story.
     */
    private void build_start_dialog_frame() {

        // Update dialog box according to past events.
        String story = "";

        for (Character p : gameSingleton.getCurrent_game().getCharacters()) {
            if (gameSingleton.actions_tour_nuit != null) {
                if (gameSingleton.actions_tour_nuit.get(p) != null) {
                    if (gameSingleton.actions_tour_nuit.get(p).contains(Game.GameSingleton.Night_action.TUE)) {
                        story += "    " + p.getNom() + " (" + p.getRole();
                        p.setMort_confirme(true);
                        if (p.isContamine()) {
                            story += ", Mutant)\n";
                        } else {
                            story += ")\n";
                        }
                    }
                }
            }
        }

        if (story.equals("")) {
            story += "Personne n'est mort !";
        } else {
            story = "On déplore les morts suivants :\n\n" + story;
        }
        story = "Le jour se lève...\n\n" + story;

        showDialog_resultats_nuit(story);
    }

    /**
     * Shows requested text in a dialog frame.
     *
     * @param text Requested text.
     */
    protected void showDialog_resultats_nuit(String text) {

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        Dialog_resultats_nuit newFragment = Dialog_resultats_nuit.newInstance(text);
        newFragment.setCancelable(false);
        newFragment.show(ft, "dialog");
    }

    /**
     * This Dialog shows information about past night on day turn start.
     */
    public static class Dialog_resultats_nuit extends Fragment_gameturn.Dialog_fin_tour {

        public static Dialog_resultats_nuit newInstance(String text) {
            Dialog_resultats_nuit f = new Dialog_resultats_nuit();
            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putString("texte_dial", text);
            f.setArguments(args);
            return f;
        }

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            RelativeLayout lay_diag = (RelativeLayout) RelativeLayout.inflate(getActivity(), R.layout.dialog_endturn, null);
            TextView tv = lay_diag.findViewById(R.id.dialog_fin_tour_tv);
            tv.setText(dialog_text);
            builder.setView(lay_diag);
            builder.setPositiveButton(R.string.bout_suivant, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Fermer
                    dialog.dismiss();
                }
            });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}
