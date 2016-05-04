package com.minastelien.quentin.gestionnairedesporz;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.minastelien.quentin.gestionnairedesporz.Adapters.Adapter_character_setup;
import com.minastelien.quentin.gestionnairedesporz.Game.Character;
import com.minastelien.quentin.gestionnairedesporz.Game.Role;
import com.minastelien.quentin.gestionnairedesporz.Utilities.Dates;

import java.util.ArrayList;

/**
 * In this activity, the user can give roles to the players.
 * Created by Quentin on 28/12/2015.
 */
public class Activity_setup_subr extends Activity_main {

    private RelativeLayout subr_lay;
    private Button subr_bouton_suiv;
    private ListView subr_lv;
    private ArrayAdapter<Character> subr_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subr_lay = (RelativeLayout) RelativeLayout.inflate(this, R.layout.activity_setup_subr, null);
        setContentView(subr_lay);

        build_subr();
    }

    /**
     * Initializes the layout and the listeners.
     */
    private void build_subr() {
        // Initialisation de la liste des personnages
        subr_lv = (ListView) subr_lay.findViewById(R.id.act_setup_subr_lv);
        subr_adapter = new Adapter_character_setup(this, gameSingleton.getCurrent_game().getCharacters(), gameSingleton);
        subr_lv.setAdapter(subr_adapter);

        // Initialisation du bouton
        subr_bouton_suiv = (Button) subr_lay.findViewById(R.id.act_setup_subr_bout_suiv);
        subr_bouton_suiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_game_coherence()) {
                    Intent deuxiemeActivite = new Intent(Activity_setup_subr.this, Activity_sumup.class);
                    startActivity(deuxiemeActivite);
                    init_hist();
                }
            }
        });
    }

    /**
     * Initializes the game history with basics information about the current gameSingleton.getCurrent_game().
     */
    private void init_hist() {
        String s = "*********************\nDébut du jeu\n" + Dates.date() + "\n";
        s += "*********************\n\nLes rôles sont :\n";
        for (Character p : gameSingleton.getCurrent_game().getCharacters()) {
            s += p.getNom() + ", " + p.getRole();
            if (!p.getGene().equals(gameSingleton.NORMAL)) {
                s += ", " + p.getGene();
            }
            s += "\n";
        }
        s += "\n";
        gameSingleton.getCurrent_game().setGame_hist(s);
    }

    /**
     * Returns true if the game is playable with the current characters, else false.
     *
     * @return True if playable, else false.
     */
    private boolean check_game_coherence() {
        boolean is_there_mutant = false;
        boolean is_there_medecin = false;
        for (Character p : gameSingleton.getCurrent_game().getCharacters()) {
            if (p.getRole().equals(gameSingleton.MUTANT_DE_BASE) && !p.getGene().equals(gameSingleton.HOTE)) {
                Toast.makeText(getApplicationContext(), "MODIFIÉ : un mutant de base doit être de génome hôte !", Toast.LENGTH_SHORT).show();
                p.setGene(gameSingleton.HOTE);
                build_subr();
                return false;
            }
            if (p.getRole().equals(gameSingleton.MEDECIN) && !p.getGene().equals(gameSingleton.NORMAL)) {
                Toast.makeText(getApplicationContext(), "MODIFIÉ : un médecin doit être de génome normal !", Toast.LENGTH_SHORT).show();
                p.setGene(gameSingleton.NORMAL);
                build_subr();
                return false;
            }
            if (p.getRole().equals(gameSingleton.MUTANT_DE_BASE)) {
                is_there_mutant = true;
            }
            if (p.getRole().equals(gameSingleton.MEDECIN)) {
                is_there_medecin = true;
            }
        }
        if (!is_there_medecin) {
            Toast.makeText(getApplicationContext(), "ERREUR : il n'y a pas de médecin !", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!is_there_mutant) {
            Toast.makeText(getApplicationContext(), "ERREUR : il n'y a pas de mutant !", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    /**
     * Builds the options menu.
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_roles, menu);
        return true;
    }

    @Override
    /**
     * Builds the options menu with two options; builds the listeners.
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_roles_distribuer) {
            Toast.makeText(getApplicationContext(), "Chargement...", Toast.LENGTH_SHORT).show();
            Intent deuxiemeActivite = new Intent(Activity_setup_subr.this, Activity_setup_subr_dist.class);
            startActivity(deuxiemeActivite);
            return true;
        }
        if (id == R.id.menu_roles_verifier) {
            String resultat = "";

            // Rôles en double
            resultat += "Rôles en double :\n\n";
            ArrayList<Role> roles_existants = new ArrayList<>();
            for (Character p : gameSingleton.getCurrent_game().getCharacters()) {
                if (!p.getRole().equals(gameSingleton.SIMPLE_ASTRONAUTE)) {
                    if (!roles_existants.contains(p.getRole())) {
                        roles_existants.add(p.getRole());
                    } else {
                        resultat += "  " + p.getRole() + "\n";
                    }
                }
            }
            resultat += "\n";

            // Rôles manquants
            resultat += "Rôles non attribués :\n\n";
            roles_existants = new ArrayList<>();
            for (Character p : gameSingleton.getCurrent_game().getCharacters()) {
                if (!p.getRole().equals(gameSingleton.SIMPLE_ASTRONAUTE)) {
                    if (!roles_existants.contains(p.getRole())) {
                        roles_existants.add(p.getRole());
                    }
                }
            }
            for (Role r : gameSingleton.ROLES_LIST) {
                if (!r.equals(gameSingleton.SIMPLE_ASTRONAUTE) && !roles_existants.contains(r)) {
                    resultat += "  " + r + "\n";
                }
            }

            Toast.makeText(getApplicationContext(), resultat, Toast.LENGTH_LONG).show();
        }

        if (id == R.id.menu_roles_aide) {
            showDialog_help_roles();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    /**
     * Checks if the data has changed (typically after roles being distributed).
     */
    public void onResume() {
        super.onResume();
        subr_adapter.notifyDataSetChanged();
    }

//    public static class Dialog_dist_roles extends DialogFragment {
//
//        Adapter_role_dist dist_adapter;
//        ArrayList<Role> roles_a_choisir;
//
//        TextView tv_resistants;
//        TextView tv_hotes;
//
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            roles_a_choisir = new ArrayList<>();
//            for (Role r : Role.ROLES_LIST) {
//                if (!r.equals(Role.SIMPLE_ASTRONAUTE)) {
//                    roles_a_choisir.add(r);
//                }
//            }
//        }
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            // Use the Builder class for convenient dialog construction
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setCancelable(true);
//
//            RelativeLayout layout = (RelativeLayout) RelativeLayout.inflate(getActivity(), R.layout.dialog_dist_char, null);
//
//            TextView textView = (TextView) layout.findViewById(R.id.dialog_distrib_pers_tv);
//            String phrase = "Nombre de joueurs : " + Game.gameSingleton.getCurrent_game().getPersonnages().size();
//            textView.setText(phrase);
//
//            // Initialisation ligne hotes
//            tv_hotes = (TextView) layout.findViewById(R.id.dialog_distrib_pers_hotes_tv);
//            Button button_left_hotes = (Button) layout.findViewById(R.id.dialog_distrib_pers_hotes_but_left);
//            button_left_hotes.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int valeur;
//                    try {
//                        valeur = Integer.parseInt(tv_hotes.getText().toString());
//                    } catch (NumberFormatException e) {
//                        valeur = 0;
//                    }
//                    if (valeur > 0) {
//                        tv_hotes.setText("" + (valeur - 1));
//                    }
//                }
//            });
//            Button button_right_hotes = (Button) layout.findViewById(R.id.dialog_distrib_pers_hotes_but_right);
//            button_right_hotes.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int valeur;
//                    try {
//                        valeur = Integer.parseInt(tv_hotes.getText().toString());
//                    } catch (NumberFormatException e) {
//                        valeur = 0;
//                    }
//                    tv_hotes.setText("" + (valeur + 1));
//                }
//            });
//
//            // Initialisation ligne resistants
//            tv_resistants = (TextView) layout.findViewById(R.id.dialog_distrib_pers_resistants_tv);
//            Button button_left_resistants = (Button) layout.findViewById(R.id.dialog_distrib_pers_resistants_but_left);
//            button_left_resistants.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int valeur;
//                    try {
//                        valeur = Integer.parseInt(tv_resistants.getText().toString());
//                    } catch (NumberFormatException e) {
//                        valeur = 0;
//                    }
//                    if (valeur > 0) {
//                        tv_resistants.setText("" + (valeur - 1));
//                    }
//                }
//            });
//            Button button_right_resistants = (Button) layout.findViewById(R.id.dialog_distrib_pers_resistants_but_right);
//            button_right_resistants.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int valeur;
//                    try {
//                        valeur = Integer.parseInt(tv_resistants.getText().toString());
//                    } catch (NumberFormatException e) {
//                        valeur = 0;
//                    }
//                    tv_resistants.setText("" + (valeur + 1));
//                }
//            });
//
//            // Initialisaton lv roles
//            ListView dist_lv = (ListView) layout.findViewById(R.id.dialog_distrib_lv);
//            dist_adapter = new Adapter_role_dist(getActivity(), R.layout.adapter_dist_roles, roles_a_choisir);
//            dist_lv.setAdapter(dist_adapter);
//
//            builder.setView(layout);
//
//            builder.setPositiveButton(R.string.bout_suivant, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    // Défini plus tard
//                }
//            });
//
//            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//            // Create the AlertDialog object and return it
//            return builder.create();
//        }
//
//        @Override
//        public void onStart() {
//            super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
//            AlertDialog d = (AlertDialog) getDialog();
//            if (d != null) {
//                Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
//                positiveButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        ArrayList<Role> roles_choisis = new ArrayList<Role>();
//
//                        for (int i=0; i<dist_adapter.getChoix().size(); i++) {
//                            roles_choisis.add(dist_adapter.getChoix().get(i));
//                        }
//
//                        if (roles_choisis.size() > Game.gameSingleton.getCurrent_game().getPersonnages().size()) {
//                            Toast.makeText(getActivity(), "ERREUR : il y a " + roles_choisis.size() + " roles et " + Game.gameSingleton.getCurrent_game().getPersonnages().size() + " joueurs  !", Toast.LENGTH_SHORT).show();
//                        } else {
//
//                            // Distribution des rôles
//                            int longueur = roles_choisis.size();
//                            for (int i = 0; i < Game.gameSingleton.getCurrent_game().getPersonnages().size() - longueur; i++) {
//                                roles_choisis.add(Role.SIMPLE_ASTRONAUTE);
//                            }
//                            Collections.shuffle(roles_choisis);
//                            for (int i = 0; i < Game.gameSingleton.getCurrent_game().getPersonnages().size(); i++) {
//                                Game.gameSingleton.getCurrent_game().getPersonnages().get(i).setRole(roles_choisis.get(i));
//                            }
//
//                            // Distribution des génomes
//                            for (Personnage p: Game.gameSingleton.getCurrent_game().getPersonnages()) {
//                                if (!p.getRoleFromIndex().equals(Role.MUTANT_DE_BASE)) {
//                                    p.setGene(Gene.NORMAL);
//                                } else {
//                                    p.setGene(Gene.HOTE);
//                                }
//                            }
//
//                            ArrayList<Personnage> liste_pers_pour_genomes = (ArrayList) Game.gameSingleton.getCurrent_game().getPersonnages().clone();
//                            Collections.shuffle(liste_pers_pour_genomes);
//
//                            int max_hotes = Integer.parseInt(tv_hotes.getText().toString());
//                            int compteur_hotes = 0;
//
//                            for (int j = 0; j < liste_pers_pour_genomes.size(); j++) {
//                                if (!(compteur_hotes < max_hotes)) {
//                                    break;
//                                } else {
//                                    if(!liste_pers_pour_genomes.get(j).getGeneFromId().equals(Gene.HOTE) && !liste_pers_pour_genomes.get(j).getRoleFromIndex().equals(Role.MEDECIN)) {
//                                        liste_pers_pour_genomes.get(j).setGene(Gene.HOTE);
//                                        compteur_hotes++;
//                                    }
//                                }
//                            }
//
//                            int max_resistants = Integer.parseInt(tv_resistants.getText().toString());
//                            int compteur_resistants = 0;
//
//                            for (int j = 0; j < liste_pers_pour_genomes.size(); j++) {
//                                if (!(compteur_resistants < max_resistants)) {
//                                    break;
//                                } else {
//                                    if(!liste_pers_pour_genomes.get(j).getGeneFromId().equals(Gene.HOTE) && !liste_pers_pour_genomes.get(j).getRoleFromIndex().equals(Role.MEDECIN)) {
//                                        liste_pers_pour_genomes.get(j).setGene(Gene.RESISTANT);
//                                        compteur_resistants++;
//                                    }
//                                }
//                            }
//
//                            Toast.makeText(getActivity(), "Rôles distribués  !", Toast.LENGTH_SHORT).show();
//                            ((Activity_setup_subr) getActivity()).subr_adapter.notifyDataSetChanged();
//                            dismiss();
//                        }
//                    }
//                });
//            }
//        }
//    }
//
//    protected void showDialog_dist_roles() {
//
//        // DialogFragment.show() will take care of adding the fragment
//        // in a transaction.  We also want to remove any currently showing
//        // dialog, so make our own transaction and take care of that here.
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
//        if (prev != null) {
//            ft.remove(prev);
//        }
//        ft.addToBackStack(null);
//
//        // Create and show the dialog.
//        Dialog_dist_roles newFragment = new Dialog_dist_roles();
//        newFragment.show(ft, "dialog");
//    }

}
