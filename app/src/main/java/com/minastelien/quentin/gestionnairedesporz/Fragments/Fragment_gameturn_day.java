package com.minastelien.quentin.gestionnairedesporz.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.minastelien.quentin.gestionnairedesporz.Activity_gameturn;
import com.minastelien.quentin.gestionnairedesporz.Activity_gameturn_night;
import com.minastelien.quentin.gestionnairedesporz.Adapters.Adapter_character_vote;
import com.minastelien.quentin.gestionnairedesporz.Game.Character;
import com.minastelien.quentin.gestionnairedesporz.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Fragment providing day game turn functions.
 * Created by Quentin on 29/12/2015.
 */
public class Fragment_gameturn_day extends Fragment_gameturn {

    private final String KEY_VOTE_RESULT_TEXT = "vote_result_text";
    private final String KEY_CURRENT_FRAME_VOTE = "current_frame_vote";

    private ArrayList<Character> living_characters;

    private String vote_result_text;

    private RelativeLayout lay_day_vote;
    private RelativeLayout lay_day_end;

    private TextView tv_day_end;
    private Spinner sp_day_end;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout lay_global_fragment = (FrameLayout) inflater.inflate(R.layout.fragment_gameturn_day, container, false);

        super.init_layout(lay_global_fragment);
        layout_game = (RelativeLayout) lay_global_fragment.findViewById(R.id.fragment_gameturn_game);
        layout_endgame = (RelativeLayout) lay_global_fragment.findViewById(R.id.fragment_gameturn_endgame);

        lay_day_vote = (RelativeLayout) lay_global_fragment.findViewById(R.id.act_tour_jour_vote);
        lay_day_end = (RelativeLayout) lay_global_fragment.findViewById(R.id.act_tour_jour_fin);
        build_vote();
        build_end();

        if (savedInstanceState != null) {
            selectItem(savedInstanceState.getInt(KEY_CURRENT_FRAME_VOTE));
            if (savedInstanceState.getString(KEY_VOTE_RESULT_TEXT) != null) {
                vote_result_text = savedInstanceState.getString(KEY_VOTE_RESULT_TEXT);
                tv_day_end.setText(vote_result_text);
            }
        } else {
            selectItem(0);
        }

        getActivity().setTitle("Jour " + gameSingleton.getCurrent_game().getTurn_count());

        update();

        return lay_global_fragment;
    }

    private void selectItem(int item) {
        lay_day_vote.setVisibility(View.INVISIBLE);
        lay_day_end.setVisibility(View.INVISIBLE);
        if (item == 0) {
            lay_day_vote.setVisibility(View.VISIBLE);
        } else if (item == 1) {
            lay_day_end.setVisibility(View.VISIBLE);
        }
    }

    private void build_vote() {

        // Initializing living characters list
        living_characters = new ArrayList<>();
        for (Character p : gameSingleton.getCurrent_game().getCharacters()) {
            if (!p.isMort()) {
                living_characters.add(p);
            }
        }

        // Initializing living characters list view and adapter
        ListView lv_day_vote = (ListView) lay_day_vote.findViewById(R.id.act_tour_jour_vote_lv);
        Adapter_character_vote ad_day_vote = new Adapter_character_vote(getActivity(), living_characters, gameSingleton);
        lv_day_vote.setAdapter(ad_day_vote);

        // Initializing button listeners
        Button but_day_vote = (Button) lay_day_vote.findViewById(R.id.act_tour_jour_vote_bout);
        but_day_vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setCancelable(true)
                        .setMessage(R.string.warning_finir_votes)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                HashMap<Character, Character> results = gameSingleton.resultats_votes_jour;
                                HashMap<Character, Integer> counts = new HashMap<>();

                                vote_result_text = "Résultat des votes :\n\n";
                                for (Character p : living_characters) {
                                    // Abstention case. If player did not vote, set "abstension"
                                    if (results.get(p) == gameSingleton.ABSTENTION || results.get(p) == null) {
                                        vote_result_text += "  " + p.getNom() + " s'abstient\n";
                                        results.put(p, gameSingleton.ABSTENTION);
                                    } else {
                                        vote_result_text += "  " + p.getNom() + " vote " + results.get(p).getNom() + "\n";
                                    }
                                    // Also add player to final vote counts
                                    counts.put(p, 0);
                                }
                                counts.put(gameSingleton.BLANK, 0);

                                // Qui est le personnage le plus voté ?
                                int nb_abstentions = 0;
                                for (Character p : living_characters) {

                                    Character cible = results.get(p);
                                    // Count abstensions
                                    if (cible != gameSingleton.ABSTENTION)
                                        counts.put(cible, counts.get(cible) + 1);
                                    else
                                        nb_abstentions++;

                                }

                                int max = 0;
                                vote_result_text += "\nNombre de votes contre :\n\n";
                                for (Character p : living_characters) {
                                    max = Math.max(max, counts.get(p));
                                    vote_result_text += "  " + p.getNom() + " : " + counts.get(p) + "\n";
                                }

                                max = Math.max(max, counts.get(gameSingleton.BLANK));
                                vote_result_text += "  " + gameSingleton.BLANK.getNom() + " : " + counts.get(gameSingleton.BLANK) + "\n";

                                if (max > 0) {
                                    vote_result_text += "\nPersonnage(s) ayant le plus de votes :\n\n";
                                    for (Character p : counts.keySet()) {
                                        if (counts.get(p) == max) {
                                            vote_result_text += "  " + p.getNom() + " : " + counts.get(p) + "\n";
                                        }
                                    }
                                    vote_result_text += "\nNombre d'abstensions : " + nb_abstentions + "\n\n";
                                } else {
                                    vote_result_text += "\nTout le monde s'est abstenu.\n\n";
                                }

                                tv_day_end.setText(vote_result_text);

                                lay_day_vote.setVisibility(View.INVISIBLE);
                                lay_day_end.setVisibility(View.VISIBLE);

                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            }
        });
    }

    private void build_end() {

        tv_day_end = (TextView) lay_day_end.findViewById(R.id.act_tour_jour_fin_tv);
        sp_day_end = (Spinner) lay_day_end.findViewById(R.id.act_tour_fin_sp);
        Button but_day_end = (Button) lay_day_end.findViewById(R.id.act_tour_jour_fin_bout);

        ArrayList<Character> elimination_choices = new ArrayList<>(living_characters);
        elimination_choices.add(gameSingleton.BLANK);

        final ArrayAdapter<Character> adapter_victim = new ArrayAdapter<>(getActivity(), R.layout.spinner_theme, elimination_choices);
        adapter_victim.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_day_end.setAdapter(adapter_victim);

        sp_day_end.setSelection(elimination_choices.size() - 1);

        but_day_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Character victim = (Character) sp_day_end.getSelectedItem();
                if (!victim.equals(gameSingleton.BLANK)) {
                    victim.setMort(true);
                    victim.setMort_confirme(true);
                    gameSingleton.getCurrent_game().addHist_jeu(vote_result_text + "\n" + victim.getNom() + " est éliminé.\n\n");
                } else {
                    gameSingleton.getCurrent_game().addHist_jeu(vote_result_text + "\nPersonne n'est éliminé.\n\n");
                }

                ((Activity_gameturn) getActivity()).update_all();

                // Handle win event
                if (!((Activity_gameturn) getActivity()).check_victory_conditions()) {
                    // If no win, display sum up dialog box.
                    String res = "";
                    if (!victim.equals(gameSingleton.BLANK)) {
                        res += victim.getNom() + " est éliminé. Il était :\n\n" + victim.getRole();
                        if (victim.isContamine()) {
                            res += " , Mutant";
                        }
                    } else {
                        res += "Personne n'est éliminé.";
                    }
                    showDialog_fin_tour_jour(res);
                }
            }
        });

    }

    @Override
    public void update() {
        if (((Activity_gameturn) getActivity()).somebody_win()) {
            super.update();
            layout_game.setVisibility(View.INVISIBLE);
            layout_endgame.setVisibility(View.VISIBLE);
        } else {
            if (lay_day_end.getVisibility() == View.VISIBLE) {
                build_end();
                tv_day_end.setText(vote_result_text);
            } else if (lay_day_vote.getVisibility() == View.VISIBLE) {
                build_vote();
            }
            layout_game.setVisibility(View.VISIBLE);
            layout_endgame.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int j = 0;
        if (lay_day_vote != null && lay_day_vote.getVisibility() == View.VISIBLE) {
            j = 0;
        } else if (lay_day_end != null && lay_day_end.getVisibility() == View.VISIBLE) {
            j = 1;
        }
        outState.putInt(KEY_CURRENT_FRAME_VOTE, j);
        outState.putString(KEY_VOTE_RESULT_TEXT, vote_result_text);
    }

    protected void showDialog_fin_tour_jour(String text) {

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        Dialog_fin_tour_jour newFragment = Dialog_fin_tour_jour.newInstance(text);
        newFragment.setCancelable(false);
        newFragment.show(ft, "dialog");
    }

    public static class Dialog_fin_tour_jour extends Dialog_fin_tour {
        public static Dialog_fin_tour_jour newInstance(String text) {
            Dialog_fin_tour_jour f = new Dialog_fin_tour_jour();
            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putString("texte_dial", text);
            f.setArguments(args);
            return f;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            RelativeLayout lay_diag = (RelativeLayout) RelativeLayout.inflate(getActivity(), R.layout.dialog_endturn, null);
            TextView tv = (TextView) lay_diag.findViewById(R.id.dialog_fin_tour_tv);
            tv.setText(dialog_text);
            builder.setView(lay_diag);
            builder.setPositiveButton(R.string.bout_suivant, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Switch to next activity
                    Intent nextActivity = new Intent(getActivity(), Activity_gameturn_night.class);
                    startActivity(nextActivity);
                    dialog.dismiss();
                }
            });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }


}
