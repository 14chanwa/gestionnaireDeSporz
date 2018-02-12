package com.minastelien.quentin.gestionnairedesporz.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.minastelien.quentin.gestionnairedesporz.Activity_gameturn;
import com.minastelien.quentin.gestionnairedesporz.Activity_main;
import com.minastelien.quentin.gestionnairedesporz.Adapters.Adapter_character_sumup;
import com.minastelien.quentin.gestionnairedesporz.Game.Character;
import com.minastelien.quentin.gestionnairedesporz.Game.Game;
import com.minastelien.quentin.gestionnairedesporz.R;

/**
 * Fragment displaying current game characters state.
 * Created by Quentin on 08/01/2016.
 */
public class Fragment_game_sumup extends Fragment_main {

    private CoordinatorLayout lay_sumup;
    private ArrayAdapter<Character> ad_sumup;

    private TextView tv_recap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        build_sumup(inflater);
        return lay_sumup;
    }

    /**
     * Builds the sumup layout.
     *
     * @param inflater Context inflater.
     */
    @SuppressLint("InflateParams")
    protected void build_sumup(LayoutInflater inflater) {
        lay_sumup = (CoordinatorLayout) inflater.inflate(R.layout.fragment_sumup_with_recap, null);

        // Initialize character list
        ListView lv_sumup = (ListView) lay_sumup.findViewById(R.id.sumup_lv);
        ad_sumup = new Adapter_character_sumup(getActivity(), gameSingleton.getCurrent_game().getCharacters(), gameSingleton);
        lv_sumup.setAdapter(ad_sumup);
        lv_sumup.setLongClickable(true);

        // Modify character if long pressed
        lv_sumup.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDialog_modif_perso(position);
                return true;
            }
        });

        // Display role help dialog if clicked on the bottom right red button
        lay_sumup.findViewById(R.id.fragment_sumup_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity_main) getActivity()).showDialog_help_roles();
            }
        });

        tv_recap = (TextView) lay_sumup.findViewById(R.id.fragment_sumup_tv);
        update_recap();

    }

    private void update_recap() {
        int nb_char_alive = 0;
        int nb_mut = 0;
        int nb_med = 0;
        for (Character c : Game.getGameSingleton().getCurrent_game().getCharacters()) {
            if (!c.isMort()) {
                nb_char_alive++;
                if (c.isContamine()) {
                    nb_mut++;
                } else if (!c.isContamine() && c.getRole() == Game.getGameSingleton().MEDECIN) {
                    nb_med++;
                }
            }
        }
        tv_recap.setText("Personnages vivants : " + nb_char_alive +
                " (Total : " + Game.getGameSingleton().getCurrent_game().getCharacters().size() + ")\n" +
                "Médecins : " + nb_med + "\n" +
                "Mutants : " + nb_mut);
    }

    public void update_sumup() {
        ad_sumup.notifyDataSetChanged();
        update_recap();
    }

    protected void showDialog_modif_perso(int indice_personnage) {

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
        Dialog_modif_perso newFragment = Dialog_modif_perso.newInstance(indice_personnage);
        newFragment.setCancelable(true);
        newFragment.show(ft, "dialog");
    }

    public static class Dialog_modif_perso extends DialogFragment {

        private Game.GameSingleton gameSingleton;

        private int indice_personnage;

        public static Dialog_modif_perso newInstance(int indice_personnage) {
            Dialog_modif_perso f = new Dialog_modif_perso();
            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("indice_personnage", indice_personnage);
            f.setArguments(args);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.indice_personnage = getArguments().getInt("indice_personnage");
            this.gameSingleton = Game.getGameSingleton();
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(true);
            builder.setTitle("Modifier personnage");
            RelativeLayout lay_dialog = (RelativeLayout) RelativeLayout.inflate(getActivity(), R.layout.dialog_modif_perso, null);
            final CheckBox check_paralyse = (CheckBox) lay_dialog.findViewById(R.id.dialog_check_paralyse);
            final CheckBox check_contamine = (CheckBox) lay_dialog.findViewById(R.id.dialog_check_contamine);
            final CheckBox check_mort = (CheckBox) lay_dialog.findViewById(R.id.dialog_check_mort);

            if (gameSingleton.getCurrent_game().getCharacters().get(indice_personnage).isParalyse()) {
                check_paralyse.setChecked(true);
            } else {
                check_paralyse.setChecked(false);
            }

            if (gameSingleton.getCurrent_game().getCharacters().get(indice_personnage).isContamine()) {
                check_contamine.setChecked(true);
            } else {
                check_contamine.setChecked(false);
            }

            if (gameSingleton.getCurrent_game().getCharacters().get(indice_personnage).isMort()) {
                check_mort.setChecked(true);
            } else {
                check_mort.setChecked(false);
            }

            builder.setView(lay_dialog);
            builder.setPositiveButton(R.string.bout_suivant, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                    if (gameSingleton.getCurrent_game().getCharacters().get(indice_personnage).getRole().equals(gameSingleton.MUTANT_DE_BASE) && !check_contamine.isChecked()) {
                        Toast.makeText(getActivity().getApplicationContext(), "ERREUR : un mutant de base doit être contaminé !", Toast.LENGTH_LONG).show();
                        check_contamine.setChecked(true);
                    }

                    if (check_contamine.isChecked() && check_paralyse.isChecked()) {
                        Toast.makeText(getActivity().getApplicationContext(), "MODIFICATION : un personnage ne peut pas être contaminé et paralysé ! " + gameSingleton.getCurrent_game().getCharacters().get(indice_personnage).getNom() + " n'est plus paralysé.", Toast.LENGTH_LONG).show();
                        check_paralyse.setChecked(false);
                    }

                    if (check_paralyse.isChecked() != gameSingleton.getCurrent_game().getCharacters().get(indice_personnage).isParalyse()) {
                        gameSingleton.getCurrent_game().getCharacters().get(indice_personnage).setParalyse(check_paralyse.isChecked());
                        gameSingleton.getCurrent_game().addHist_jeu("$$$ La main du destin :\n" + gameSingleton.getCurrent_game().getCharacters().get(indice_personnage).getNom() + " est paralysé : " + check_paralyse.isChecked() + "\n\n");
                    }
                    if (check_contamine.isChecked() != gameSingleton.getCurrent_game().getCharacters().get(indice_personnage).isContamine()) {
                        gameSingleton.getCurrent_game().getCharacters().get(indice_personnage).setContamine(check_contamine.isChecked());
                        gameSingleton.getCurrent_game().addHist_jeu("$$$ La main du destin :\n" + gameSingleton.getCurrent_game().getCharacters().get(indice_personnage).getNom() + " est contaminé : " + check_contamine.isChecked() + "\n\n");
                    }
                    if (check_mort.isChecked() != gameSingleton.getCurrent_game().getCharacters().get(indice_personnage).isMort()) {
                        gameSingleton.getCurrent_game().getCharacters().get(indice_personnage).setMort(check_mort.isChecked());
                        gameSingleton.getCurrent_game().addHist_jeu("$$$ La main du destin :\n" + gameSingleton.getCurrent_game().getCharacters().get(indice_personnage).getNom() + " est mort : " + check_mort.isChecked() + "\n\n");
                    }

                    if (((Activity_gameturn) getActivity()).check_victory_conditions()) {
                        Toast.makeText(getActivity().getApplicationContext(), "ATTENTION : dans la configuration actuelle, le jeu est fini !", Toast.LENGTH_LONG).show();
                    }

                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}
