package com.minastelien.quentin.gestionnairedesporz.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.minastelien.quentin.gestionnairedesporz.Activity_gameturn;
import com.minastelien.quentin.gestionnairedesporz.Activity_gameturn_day;
import com.minastelien.quentin.gestionnairedesporz.Activity_gameturn_night;
import com.minastelien.quentin.gestionnairedesporz.Game.Character;
import com.minastelien.quentin.gestionnairedesporz.Game.Game;
import com.minastelien.quentin.gestionnairedesporz.Game.Role;
import com.minastelien.quentin.gestionnairedesporz.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Fragment providing night game turn functions.
 * Created by Quentin on 29/12/2015.
 */
public class Fragment_gameturn_night extends Fragment_gameturn {

    private final String KEY_CURRENT_ROLE = "current_role";
    int current_role_index = 0;
    private RelativeLayout lay_role_night;
    private Button but_next;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FrameLayout frag_glob = (FrameLayout) inflater.inflate(R.layout.fragment_gameturn_night, container, false);

        super.init_layout(frag_glob);
        layout_game = (RelativeLayout) frag_glob.findViewById(R.id.fragment_gameturn_game);
        layout_endgame = (RelativeLayout) frag_glob.findViewById(R.id.fragment_gameturn_endgame);

        lay_role_night = (RelativeLayout) frag_glob.findViewById(R.id.act_tour_nuit_role_layout);
        but_next = (Button) frag_glob.findViewById(R.id.act_tour_nuit_bouton_suivant);

        if (savedInstanceState != null) {
            current_role_index = savedInstanceState.getInt(KEY_CURRENT_ROLE);
        }

        update();

        return frag_glob;
    }


    /**
     * Display the layout corresponding to the given role index.
     *
     * @param index Role index in the Role list.
     */
    private void display_role(int index) {

        int index_max = Game.getGameSingleton().ROLES_LIST_NIGHT.size() - 1;

        if (index <= index_max) {
            lay_role_night.removeAllViews();
            Role role = Game.getGameSingleton().ROLES_LIST_NIGHT.get(index);

            // TOUR DES MUTANTS
            if (role.equals(gameSingleton.MUTANT_DE_BASE)) {
                gameSingleton.role_a_joue_nuit.add(role);
                display_role_mutant_de_base();
                getActivity().setTitle("Nuit " + gameSingleton.getCurrent_game().getTurn_count() + " - " + "Mutants");
            }

            // TOUR DES MEDECINS
            else if (role.equals(gameSingleton.MEDECIN)) {
                boolean il_y_a_un_medecin_non_paralyse = false;
                for (Character p : gameSingleton.personnages_vivants_debut_tour) {
                    if (!p.isMort() && p.getRole().equals(gameSingleton.MEDECIN) && !p.isParalyse() && !p.isContamine()) {
                        gameSingleton.role_a_joue_nuit.add(role);
                        display_role_medecin();
                        il_y_a_un_medecin_non_paralyse = true;
                        break;
                    }
                }
                if (!il_y_a_un_medecin_non_paralyse) {
                    display_role_if_paralyse(gameSingleton.MEDECIN);
                }
                getActivity().setTitle("Nuit " + gameSingleton.getCurrent_game().getTurn_count() + " - " + "Médecins");
            }

            // TOUR AUTRE ROLE SPECIAL
            else if (Game.getGameSingleton().ROLES_LIST_NIGHT.contains(role)) {
                if (isRole(role)) {
                    if (!isRole_vivant(role)) {
                        display_role_if_mort(role);
                        getActivity().setTitle("Nuit " + gameSingleton.getCurrent_game().getTurn_count() + " - " + role.getNom());
                    } else if (!isRole_non_contamine(role)) {
                        display_role_if_contamine(role);
                        getActivity().setTitle("Nuit " + gameSingleton.getCurrent_game().getTurn_count() + " - " + role.getNom());
                    } else if (!isRole_non_paralyse(role)) {
                        display_role_if_paralyse(role);
                        getActivity().setTitle("Nuit " + gameSingleton.getCurrent_game().getTurn_count() + " - " + role.getNom());
                    } else {
                        gameSingleton.role_a_joue_nuit.add(role);
                        display_special_role(role);
                        getActivity().setTitle("Nuit " + gameSingleton.getCurrent_game().getTurn_count() + " - " + role.getNom());
                    }
                } else {
                    display_next_role();
                }
            }

            // SINON
            else {
                display_next_role();
            }
            if (index < index_max) {
                but_next.setText("Rôle suivant");
            } else {
                but_next.setText("Terminer tour");
            }
        } else {
            // Passer écran suivant
            Intent secondeActivite = new Intent(getActivity(), Activity_gameturn_day.class);
            startActivity(secondeActivite);
        }

    }

    private void display_next_role() {
        current_role_index++;

        if (!((Activity_gameturn) getActivity()).check_victory_conditions()) {
            display_role(current_role_index);
            ((Activity_gameturn_night) getActivity()).update_all();
        }
    }

    private void display_special_role(Role role) {
        if (role.equals(gameSingleton.INFORMATICIEN)) {
            display_role_informaticien();
        } else if (role.equals(gameSingleton.PSYCHOLOGUE)) {
            display_role_psychologue();
        } else if (role.equals(gameSingleton.GENETICIEN)) {
            display_role_geneticien();
        } else if (role.equals(gameSingleton.POLITICIEN)) {
            display_role_politicien();
        } else if (role.equals(gameSingleton.HACKER)) {
            display_role_hacker();
        } else if (role.equals(gameSingleton.APPRENTI_HACKER)) {
            display_role_apprenti_hacker();
        } else if (role.equals(gameSingleton.ESPION)) {
            display_role_espion();
        } else if (role.equals(gameSingleton.PEINTRE)) {
            display_role_peintre();
        }
    }

    private void display_role_mutant_de_base() {

        RelativeLayout mutant_layout = (RelativeLayout) RelativeLayout.inflate(getActivity(), gameSingleton.MUTANT_DE_BASE.getLayout_id(), null);
        lay_role_night.addView(mutant_layout);

        TextView mutant_tv = (TextView) mutant_layout.findViewById(R.id.role_mutant_textview);
        final Spinner mutant_sp = (Spinner) mutant_layout.findViewById(R.id.role_mutant_spinner);
        final Spinner mutant_sp_paralyse = (Spinner) mutant_layout.findViewById(R.id.role_mutant_spinner_paralyse);

        final RadioButton mutant_bouton_muter = (RadioButton) mutant_layout.findViewById(R.id.role_mutant_radiobutton_muter);
        mutant_bouton_muter.setChecked(true);

        // Les personnes pas encore contaminées et les contaminés
        String texte = "\n";
        final Set<Character> set_mutants_vivants = new HashSet<Character>();
        for (Character p : gameSingleton.getCurrent_game().getCharacters()) {
            if (p.isContamine() && !p.isMort()) {
                set_mutants_vivants.add(p);
                texte += "  " + p.getNom() + "\n";
            }
        }
        final ArrayList<Character> personnages_sains = new ArrayList<Character>() {{
            for (Character p : gameSingleton.getCurrent_game().getCharacters()) {
                if (!p.isContamine() && !p.isMort_confirme()) {
                    add(p);
                }
            }
        }};

        texte += "\n" + getString(R.string.texte_mutant);
        mutant_tv.setText(texte);

        final ArrayAdapter<Character> adapter_victime = new ArrayAdapter<Character>(getActivity(), R.layout.spinner_theme, personnages_sains);
        adapter_victime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mutant_sp.setAdapter(adapter_victime);

        final ArrayAdapter<Character> adapter_victime_paralyse = new ArrayAdapter<Character>(getActivity(), R.layout.spinner_theme, personnages_sains);
        adapter_victime_paralyse.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mutant_sp_paralyse.setAdapter(adapter_victime_paralyse);

        but_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Character perso_cible = (Character) mutant_sp.getSelectedItem();
                Character perso_cible_paralyse = (Character) mutant_sp_paralyse.getSelectedItem();

                if (perso_cible.equals(perso_cible_paralyse)) {
                    Toast.makeText(getActivity(), "ERREUR : on ne peut pas tuer/muter et paralyser la même personne !", Toast.LENGTH_SHORT).show();
                } else {

                    // Préparation des boites de dialogue...
                    String str_une_tape = "";
                    String str_deux_tapes = "";
                    String str_trois_tapes = "";
                    String str_paralyse = "";

                    gameSingleton.getCurrent_game().addHist_jeu("---\nTour des mutants\n\n");

                    if (mutant_bouton_muter.isChecked()) {
                        // Muter
                        gameSingleton.getCurrent_game().addHist_jeu("Les mutants choisissent de muter " + perso_cible.getNom() + "\n\n");
                        add_to_action_list(perso_cible, Game.GameSingleton.Night_action.MUTE);
                        if (action_contaminer(perso_cible)) {
                            str_deux_tapes += perso_cible.getNom();
                        } else {
                            str_trois_tapes += perso_cible.getNom();
                        }
                    } else {
                        // Tuer
                        gameSingleton.getCurrent_game().addHist_jeu("Les mutants choisissent de tuer " + perso_cible.getNom() + ".\n\n");
                        perso_cible.setMort(true);
                        add_to_action_list(perso_cible, Game.GameSingleton.Night_action.TUE);
                        str_une_tape += perso_cible.getNom();
                    }

                    // Paralyser
                    gameSingleton.getCurrent_game().addHist_jeu("Les mutants choisissent de paralyser " + perso_cible_paralyse.getNom() + ".\n\n");
                    perso_cible_paralyse.setParalyse(true);
                    add_to_action_list(perso_cible_paralyse, Game.GameSingleton.Night_action.PARALYSE);
                    str_paralyse += perso_cible_paralyse.getNom();

                    ((Activity_gameturn_night) getActivity()).update_all();

                    // Ajouter l'un des loups à la liste des personnes visiteurs
                    int size = set_mutants_vivants.size();
                    int item = new Random().nextInt(size);
                    int i = 0;
                    for (Character pers : set_mutants_vivants) {
                        if (i == item)
                            add_to_visit_list(perso_cible, pers);

                        i = i + 1;
                    }
                    item = new Random().nextInt(size);
                    i = 0;
                    for (Character pers : set_mutants_vivants) {
                        if (i == item)
                            add_to_visit_list(perso_cible_paralyse, pers);

                        i = i + 1;
                    }

                    if (!((Activity_gameturn) getActivity()).check_victory_conditions()) {
                        while (true) {
                            // On affiche une boite de dialogue avec les choses à dire
                            showDialog_end_night_gameturn("Il est temps de faire le tour... Taper :\n\n" +
                                    " - 1 fois si le personnage est mort\n" +
                                    " - 2 fois si le personnage a été muté\n" +
                                    " - 3 fois si le personnage a résisté à la mutation.\n\n" +
                                    " 1 fois : " + str_une_tape +
                                    "\n 2 fois : " + str_deux_tapes +
                                    "\n 3 fois : " + str_trois_tapes +
                                    "\n\n Puis le tour des paralysés... Taper :\n\n" +
                                    " 1 fois : " + str_paralyse);
                            break;
                        }
                    }
                }
            }
        });
    }

    private void display_role_medecin() {
        RelativeLayout medecin_layout = (RelativeLayout) RelativeLayout.inflate(getActivity(), gameSingleton.MEDECIN.getLayout_id(), null);
        lay_role_night.addView(medecin_layout);

        TextView medecin_tv = (TextView) medecin_layout.findViewById(R.id.role_medecin_textview);
        final RadioButton medecin_bouton_soigner = (RadioButton) medecin_layout.findViewById(R.id.role_medecin_radiobutton_soigner);
        RadioButton medecin_bouton_tuer = (RadioButton) medecin_layout.findViewById(R.id.role_medecin_radiobutton_tuer);
        final RelativeLayout layout_soigner = (RelativeLayout) medecin_layout.findViewById(R.id.role_medecin_frame_soigner);
        final RelativeLayout layout_tuer = (RelativeLayout) medecin_layout.findViewById(R.id.role_medecin_frame_tuer);

        final Spinner spinner_soigner_1 = (Spinner) medecin_layout.findViewById(R.id.role_medecin_soigner_spinner1);
        final Spinner spinner_soigner_2 = (Spinner) medecin_layout.findViewById(R.id.role_medecin_soigner_spinner2);
        final Spinner spinner_tuer = (Spinner) medecin_layout.findViewById(R.id.role_medecin_tuer_spinner);

        String texte = "\n";
        final Set<Character> set_medecins_vivants = new HashSet<Character>();
        final ArrayList<Character> personnages_vivants_non_medecins = new ArrayList<Character>();
        for (Character p : gameSingleton.personnages_vivants_debut_tour) {
            if (!p.isMort() && p.getRole().equals(gameSingleton.MEDECIN) && !p.isContamine() && !p.isParalyse()) {
                set_medecins_vivants.add(p);
                texte += "  " + p.getNom() + "\n";
            } else {
                personnages_vivants_non_medecins.add(p);
            }
        }

        if (set_medecins_vivants.size() < 2) {
            spinner_soigner_2.setVisibility(View.INVISIBLE);
        }

        texte += "\n" + getString(R.string.texte_medecin);
        medecin_tv.setText(texte);

        final ArrayAdapter<Character> adapter_soigner_1 = new ArrayAdapter<Character>(getActivity(), R.layout.spinner_theme, personnages_vivants_non_medecins);
        ArrayAdapter<Character> adapter_soigner_2 = new ArrayAdapter<Character>(getActivity(), R.layout.spinner_theme, personnages_vivants_non_medecins);
        ArrayAdapter<Character> adapter_tuer = new ArrayAdapter<Character>(getActivity(), R.layout.spinner_theme, personnages_vivants_non_medecins);
        adapter_soigner_1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_soigner_2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_tuer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_soigner_1.setAdapter(adapter_soigner_1);
        spinner_soigner_2.setAdapter(adapter_soigner_2);
        spinner_tuer.setAdapter(adapter_tuer);


        medecin_bouton_soigner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_soigner.setVisibility(View.VISIBLE);
                layout_tuer.setVisibility(View.INVISIBLE);
            }
        });

        medecin_bouton_tuer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_soigner.setVisibility(View.INVISIBLE);
                layout_tuer.setVisibility(View.VISIBLE);
            }
        });

        layout_tuer.setVisibility(View.INVISIBLE);
        medecin_bouton_soigner.setChecked(true);

        but_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_soigner.getVisibility() == View.VISIBLE) {
                    if (spinner_soigner_1.getVisibility() == View.VISIBLE && spinner_soigner_2.getVisibility() == View.VISIBLE) {
                        if (spinner_soigner_1.getSelectedItem().equals(spinner_soigner_2.getSelectedItem())) {
                            Toast.makeText(getActivity(), "ERREUR : choisissez deux personnes différentes à soigner !", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }


                // Préparation des boites de dialogue...
                String str_une_tape = "";
                String str_deux_tapes = "";
                String str_trois_tapes = "";

                gameSingleton.getCurrent_game().addHist_jeu("---\nTour des médecins\n\n");
                Character perso_cible_1;
                Character perso_cible_2;

                // Ajouter l'un des médecins à la liste des personnes visiteurs
                int size = set_medecins_vivants.size();
                int item = new Random().nextInt(size);
                int i = 0;
                Character medecin_tmp = gameSingleton.BLANK;
                for (Character pers : set_medecins_vivants) {
                    if (i == item)
                        medecin_tmp = pers;
                    i = i + 1;
                }
                final Character medecin_retenu = medecin_tmp;

                if (medecin_bouton_soigner.isChecked()) {
                    // Soigner
                    if (set_medecins_vivants.size() < 2) {
                        perso_cible_1 = (Character) spinner_soigner_1.getSelectedItem();
                        gameSingleton.getCurrent_game().addHist_jeu("Le médecin choisit de soigner " + perso_cible_1 + "\n");
                        add_to_action_list(perso_cible_1, Game.GameSingleton.Night_action.SOIGNE);
                        if (action_soigner(perso_cible_1)) {
                            str_deux_tapes += perso_cible_1.getNom();
                        } else {
                            str_trois_tapes += perso_cible_1.getNom();
                        }
                        add_to_visit_list(perso_cible_1, medecin_retenu);
                    } else {
                        perso_cible_1 = (Character) spinner_soigner_1.getSelectedItem();
                        perso_cible_2 = (Character) spinner_soigner_2.getSelectedItem();
                        gameSingleton.getCurrent_game().addHist_jeu("Les médecins choisissent de soigner " + perso_cible_1 + " et " + perso_cible_2 + "\n\n");
                        add_to_action_list(perso_cible_1, Game.GameSingleton.Night_action.SOIGNE);
                        add_to_action_list(perso_cible_2, Game.GameSingleton.Night_action.SOIGNE);
                        if (action_soigner(perso_cible_1)) {
                            str_deux_tapes += perso_cible_1.getNom() + "    ";
                        } else {
                            str_trois_tapes += perso_cible_1.getNom() + "   ";
                        }
                        if (action_soigner(perso_cible_2)) {
                            str_deux_tapes += perso_cible_2.getNom() + "    ";
                        } else {
                            str_trois_tapes += perso_cible_2.getNom();
                        }
                        add_to_visit_list(perso_cible_1, medecin_retenu);
                        add_to_visit_list(perso_cible_2, medecin_retenu);
                    }
                } else {
                    // Tuer
                    perso_cible_1 = (Character) spinner_tuer.getSelectedItem();
                    gameSingleton.getCurrent_game().addHist_jeu("Les médecins choisissent de tuer " + perso_cible_1.getNom() + ".\n\n");
                    perso_cible_1.setMort(true);
                    add_to_action_list(perso_cible_1, Game.GameSingleton.Night_action.TUE);
                    str_une_tape += perso_cible_1.getNom();
                    add_to_visit_list(perso_cible_1, medecin_retenu);
                }
                ((Activity_gameturn_night) getActivity()).update_all();

                // Cas où l'on élimine le dernier mutant
                if (!((Activity_gameturn) getActivity()).check_victory_conditions()) {

                    // On affiche une boite de dialogue avec les choses à dire
                    showDialog_end_night_gameturn("Il est temps de faire le tour... Taper :\n\n" +
                            " - 1 fois si le personnage est mort\n" +
                            " - 2 fois si le personnage a été soigné\n" +
                            " - 3 fois si le soin n'a pas marché.\n\n" +
                            " 1 fois : " + str_une_tape +
                            "\n 2 fois : " + str_deux_tapes +
                            "\n 3 fois : " + str_trois_tapes);

                }
            }
        });
    }

    private void display_role_informaticien() {
        RelativeLayout informaticien_layout = (RelativeLayout) RelativeLayout.inflate(getActivity(), gameSingleton.INFORMATICIEN.getLayout_id(), null);
        lay_role_night.addView(informaticien_layout);

        TextView informaticien_tv_desc = (TextView) informaticien_layout.findViewById(R.id.fragment_role_textonly_tv);

        // Qui est l'informaticien ?
        Character pers_tmp = gameSingleton.BLANK;
        final ArrayList<Character> tous_pers_sauf_informaticien = new ArrayList<Character>();
        for (Character p : gameSingleton.personnages_vivants_debut_tour) {
            if (p.getRole().equals(gameSingleton.INFORMATICIEN)) {
                pers_tmp = p;
            } else {
                tous_pers_sauf_informaticien.add(p);
            }
        }
        final Character pers_informaticien = pers_tmp;

        // Quel est le nombre de mutants à bord ?
        int compteur_tmp = 0;
        for (Character p : gameSingleton.getCurrent_game().getCharacters()) {
            if (!p.isMort() && p.isContamine()) {
                compteur_tmp++;
            }
        }
        final int compteur = compteur_tmp;

        informaticien_tv_desc.setText("\n  " + pers_informaticien.getNom() + "\n\n" + getString(R.string.texte_informaticien) + "\n\n" + "  " + compteur + " mutants\n\n");

        but_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Information pour le hacker
                // L'informaticien a joué
                gameSingleton.resultat_role_hacker.add(Game.GameSingleton.Night_action_result.INFORMATICIEN);

                gameSingleton.getCurrent_game().addHist_jeu("---\nTour de l'informaticien\n\nL'informaticien trouve " + compteur + " mutant(s).\n\n");
                display_next_role();
            }
        });
    }

    private void display_role_psychologue() {

        RelativeLayout psychologue_layout = (RelativeLayout) RelativeLayout.inflate(getActivity(), gameSingleton.PSYCHOLOGUE.getLayout_id(), null);
        lay_role_night.addView(psychologue_layout);

        TextView psychlogue_tv_desc = (TextView) psychologue_layout.findViewById(R.id.role_simple_tv_desc);
        final TextView psychologue_tv_resultat = (TextView) psychologue_layout.findViewById(R.id.role_simple_tv_resultat);

        final Spinner psychologue_spinner = (Spinner) psychologue_layout.findViewById(R.id.role_simple_spinner);

        // Qui est le psychologue ?
        Character pers_tmp = gameSingleton.BLANK;
        final ArrayList<Character> tous_pers_sauf_psychologue = new ArrayList<Character>();
        for (Character p : gameSingleton.personnages_vivants_debut_tour) {
            if (p.getRole().equals(gameSingleton.PSYCHOLOGUE)) {
                pers_tmp = p;
            } else {
                tous_pers_sauf_psychologue.add(p);
            }
        }
        final Character pers_psychologue = pers_tmp;

        psychlogue_tv_desc.setText("\n  " + pers_psychologue.getNom() + "\n\n" + getString(R.string.texte_psychologue));

        ArrayAdapter<Character> psychologue_adapter = new ArrayAdapter<Character>(getActivity(), R.layout.spinner_theme, tous_pers_sauf_psychologue);
        psychologue_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        psychologue_spinner.setAdapter(psychologue_adapter);

        psychologue_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Character selected_character = tous_pers_sauf_psychologue.get(position);
                if (selected_character.isContamine()) {
                    psychologue_tv_resultat.setText(selected_character.getNom() + " est mutant !");
                } else {
                    psychologue_tv_resultat.setText(selected_character.getNom() + " n'est pas mutant.");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Rien
            }
        });

        but_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Character targeted_character = (Character) psychologue_spinner.getSelectedItem();
                add_to_visit_list(targeted_character, pers_psychologue);
                add_to_action_list(targeted_character, Game.GameSingleton.Night_action.PSYCHOLOGUE);

                // Information pour le hacker
                if (targeted_character.isContamine()) {
                    gameSingleton.resultat_role_hacker.add(Game.GameSingleton.Night_action_result.PSY_MUTANT);
                } else {
                    gameSingleton.resultat_role_hacker.add(Game.GameSingleton.Night_action_result.PSY_SAIN);
                }
                gameSingleton.getCurrent_game().addHist_jeu("---\nTour du psychologue\n\nLe psychologue choisit d'inspecter " +
                        targeted_character.getNom() + ".\n");
                gameSingleton.getCurrent_game().addHist_jeu(psychologue_tv_resultat.getText().toString() + "\n\n");
                display_next_role();
            }
        });

    }

    private void display_role_geneticien() {

        RelativeLayout geneticien_layout = (RelativeLayout) RelativeLayout.inflate(getActivity(), gameSingleton.GENETICIEN.getLayout_id(), null);
        lay_role_night.addView(geneticien_layout);

        TextView geneticien_tv_desc = (TextView) geneticien_layout.findViewById(R.id.role_simple_tv_desc);
        final TextView geneticien_tv_resultat = (TextView) geneticien_layout.findViewById(R.id.role_simple_tv_resultat);

        final Spinner geneticien_spinner = (Spinner) geneticien_layout.findViewById(R.id.role_simple_spinner);

        // Qui est le généticien ?
        Character pers_tmp = gameSingleton.BLANK;
        for (Character p : gameSingleton.personnages_vivants_debut_tour) {
            if (p.getRole().equals(gameSingleton.GENETICIEN)) {
                pers_tmp = p;
            }
        }
        final Character pers_geneticien = pers_tmp;

        geneticien_tv_desc.setText("\n  " + pers_geneticien.getNom() +
                "\n\n" + getString(R.string.texte_geneticien));

        ArrayAdapter<Character> geneticien_adapter = new ArrayAdapter<Character>(getActivity(), R.layout.spinner_theme, gameSingleton.personnages_vivants_debut_tour);
        geneticien_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        geneticien_spinner.setAdapter(geneticien_adapter);

        geneticien_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Character selected_character = gameSingleton.personnages_vivants_debut_tour.get(position);
                geneticien_tv_resultat.setText("Le génome de " + selected_character.getNom() + " est " + selected_character.getGene());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Rien
            }
        });

        but_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Character targeted_character = (Character) geneticien_spinner.getSelectedItem();
                add_to_visit_list(targeted_character, pers_geneticien);
                add_to_action_list(targeted_character, Game.GameSingleton.Night_action.GENETICIEN);

                // Information pour le hacker
                if (targeted_character.getGene().equals(gameSingleton.HOTE)) {
                    gameSingleton.resultat_role_hacker.add(Game.GameSingleton.Night_action_result.GENE_HOTE);
                } else if (targeted_character.getGene().equals(gameSingleton.RESISTANT)) {
                    gameSingleton.resultat_role_hacker.add(Game.GameSingleton.Night_action_result.GENE_RESISTANT);
                } else {
                    gameSingleton.resultat_role_hacker.add(Game.GameSingleton.Night_action_result.GENE_NORMAL);
                }
                gameSingleton.getCurrent_game().addHist_jeu("---\nTour du généticien\n\nLe généticien choisit d'inspecter " +
                        targeted_character.getNom() + ".\n" + geneticien_tv_resultat.getText() + "\n\n");
                display_next_role();
            }
        });

    }

    private void display_role_politicien() {

        RelativeLayout politicien_layout = (RelativeLayout) RelativeLayout.inflate(getActivity(), gameSingleton.POLITICIEN.getLayout_id(), null);
        lay_role_night.addView(politicien_layout);

        final TextView politicien_tv_desc = (TextView) politicien_layout.findViewById(R.id.role_simple_tv_desc);
        final TextView politicien_tv_resultat = (TextView) politicien_layout.findViewById(R.id.role_simple_tv_resultat);
        final Spinner politicien_spinner = (Spinner) politicien_layout.findViewById(R.id.role_simple_spinner);

        // Qui est le politicien ?
        Character pers_tmp = gameSingleton.BLANK;
        final ArrayList<Character> tous_pers_sauf_politicien = new ArrayList<Character>();
        for (Character p : gameSingleton.personnages_vivants_debut_tour) {
            if (p.getRole().equals(gameSingleton.POLITICIEN)) {
                pers_tmp = p;
            } else {
                tous_pers_sauf_politicien.add(p);
            }
        }
        final Character pers_politicien = pers_tmp;

        if (gameSingleton.getCurrent_game().getTurn_count() > 1) {

            politicien_tv_desc.setText("\n  " + pers_politicien.getNom() +
                    "\n\n" + getString(R.string.texte_politicien));

            ArrayAdapter<Character> politicien_adapter = new ArrayAdapter<Character>(getActivity(), R.layout.spinner_theme, tous_pers_sauf_politicien);
            politicien_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            politicien_spinner.setAdapter(politicien_adapter);

            politicien_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Character selected_character = tous_pers_sauf_politicien.get(position);
                    if (gameSingleton.resultats_votes_jour.get(selected_character) != null) {
                        politicien_tv_resultat.setText(selected_character.getNom() + " a voté " +
                                (gameSingleton.resultats_votes_jour.get(selected_character)).getNom());
                    } else {
                        politicien_tv_resultat.setText(selected_character.getNom() + " a voté " +
                                gameSingleton.BLANK.getNom());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Rien
                }
            });

            but_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Character targeted_character = (Character) politicien_spinner.getSelectedItem();
                    add_to_visit_list(targeted_character, pers_politicien);
                    add_to_action_list(targeted_character, Game.GameSingleton.Night_action.POLITICIEN);

                    // Information pour le hacker
                    if (targeted_character.isContamine()) {
                        gameSingleton.resultat_role_hacker.add(Game.GameSingleton.Night_action_result.POLITICIEN);
                    } else {
                        gameSingleton.resultat_role_hacker.add(Game.GameSingleton.Night_action_result.POLITICIEN);
                    }
                    gameSingleton.getCurrent_game().addHist_jeu("---\nTour du politicien\n\nLe politicien choisit de regarder le vote de " +
                            targeted_character.getNom() + ".\n");
                    gameSingleton.getCurrent_game().addHist_jeu(politicien_tv_resultat.getText().toString() + "\n\n");
                    display_next_role();
                }
            });
        } else {

            politicien_spinner.setVisibility(View.INVISIBLE);
            politicien_tv_resultat.setVisibility(View.INVISIBLE);
            gameSingleton.role_a_joue_nuit.remove(gameSingleton.POLITICIEN);
            politicien_tv_desc.setText("Le politicien\n\n  " + pers_politicien.getNom() + "\n\nne joue pas car c'est le premier tour !");

            but_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    gameSingleton.getCurrent_game().addHist_jeu("---\nTour du politicien\n\n");
                    gameSingleton.getCurrent_game().addHist_jeu("Le politicien " + pers_politicien.getNom() + " ne joue pas car c'est le premier tour !\n\n");
                    display_next_role();
                }
            });

        }
    }

    private void display_role_hacker() {

        RelativeLayout hacker_layout = (RelativeLayout) RelativeLayout.inflate(getActivity(), gameSingleton.HACKER.getLayout_id(), null);
        lay_role_night.addView(hacker_layout);

        TextView hacker_tv_desc = (TextView) hacker_layout.findViewById(R.id.role_simple_tv_desc);
        final TextView hacker_tv_resultat = (TextView) hacker_layout.findViewById(R.id.role_simple_tv_resultat);

        final Spinner hacker_spinner = (Spinner) hacker_layout.findViewById(R.id.role_simple_spinner);

        // Qui est le hacker ?
        Character pers_tmp = gameSingleton.BLANK;
        for (Character p : gameSingleton.personnages_vivants_debut_tour) {
            if (p.getRole().equals(gameSingleton.HACKER)) {
                pers_tmp = p;
            }
        }
        final Character pers_hacker = pers_tmp;

        final ArrayList<Role> choix_hacker = new ArrayList<Role>();
        for (Role role : gameSingleton.ROLES_LIST_HACKER) {
            if (isRole(role)) {
                if (!return_character_given_role(role).isMort_confirme()) {
                    choix_hacker.add(role);
                }
            }
        }

        // Quel est le nombre de mutants à bord ?
        int compteur_tmp = 0;
        for (Character p : gameSingleton.getCurrent_game().getCharacters()) {
            if (!p.isMort() && p.isContamine()) {
                compteur_tmp++;
            }
        }
        final int compteur = compteur_tmp;

        if (!choix_hacker.isEmpty()) {

            hacker_tv_desc.setText("\n  " + pers_hacker.getNom() +
                    "\n\n" + getString(R.string.texte_hacker));

            ArrayAdapter<Role> hacker_adapter = new ArrayAdapter<Role>(getActivity(), R.layout.spinner_theme, choix_hacker);
            hacker_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            hacker_spinner.setAdapter(hacker_adapter);

            hacker_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Role role_selectionne = choix_hacker.get(position);
                    if (role_selectionne.equals(gameSingleton.INFORMATICIEN)) {
                        if (gameSingleton.role_a_joue_nuit.contains(gameSingleton.INFORMATICIEN)) {
                            hacker_tv_resultat.setText("L'informaticien a compté " + compteur + " mutant(s) à bord.");
                        } else {
                            hacker_tv_resultat.setText("L'informaticien n'a pas joué cette nuit.");
                        }
                    } else if (role_selectionne.equals(gameSingleton.PSYCHOLOGUE)) {
                        if (gameSingleton.role_a_joue_nuit.contains(gameSingleton.PSYCHOLOGUE)) {
                            if (gameSingleton.resultat_role_hacker.contains(Game.GameSingleton.Night_action_result.PSY_SAIN)) {
                                hacker_tv_resultat.setText("Le psychologue a inspecté un personnage non mutant.");
                            } else if (gameSingleton.resultat_role_hacker.contains(Game.GameSingleton.Night_action_result.PSY_MUTANT)) {
                                hacker_tv_resultat.setText("Le psychologue a inspecté un personnage mutant !");
                            }
                        } else {
                            hacker_tv_resultat.setText("Le psychologue n'a pas joué cette nuit.");
                        }
                    } else if (role_selectionne.equals(gameSingleton.GENETICIEN)) {
                        if (gameSingleton.role_a_joue_nuit.contains(gameSingleton.GENETICIEN)) {
                            if (gameSingleton.resultat_role_hacker.contains(Game.GameSingleton.Night_action_result.GENE_NORMAL)) {
                                hacker_tv_resultat.setText("Le généticien a inspecté un personnage de génome normal.");
                            } else if (gameSingleton.resultat_role_hacker.contains(Game.GameSingleton.Night_action_result.GENE_RESISTANT)) {
                                hacker_tv_resultat.setText("Le psychologue a inspecté un personnage de génome résistant.");
                            } else if (gameSingleton.resultat_role_hacker.contains(Game.GameSingleton.Night_action_result.GENE_HOTE)) {
                                hacker_tv_resultat.setText("Le psychologue a inspecté un personnage de génome hôte.");
                            }
                        } else {
                            hacker_tv_resultat.setText("Le généticien n'a pas joué cette nuit.");
                        }
                    } else if (role_selectionne.equals(gameSingleton.POLITICIEN)) {
                        if (gameSingleton.role_a_joue_nuit.contains(gameSingleton.POLITICIEN)) {
                            for (Character p : gameSingleton.personnages_vivants_debut_tour) {
                                if (gameSingleton.actions_tour_nuit.get(p) != null && gameSingleton.actions_tour_nuit.get(p).contains(Game.GameSingleton.Night_action.POLITICIEN)) {
                                    hacker_tv_resultat.setText("Le politicien a inspecté une personne qui a voté " +
                                            gameSingleton.resultats_votes_jour.get(p) + ".");
                                }
                            }
                        } else {
                            hacker_tv_resultat.setText("Le politicien n'a pas joué cette nuit.");
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Rien
                }
            });

            but_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Role role_cible = (Role) hacker_spinner.getSelectedItem();
                    Character targeted_character = return_character_given_role(role_cible);

                    gameSingleton.getCurrent_game().addHist_jeu("---\nTour du hacker\n\nLe hacker choisit de hacker " +
                            targeted_character.getNom() + " (" + targeted_character.getRole() + ")" + ".\n");
                    gameSingleton.getCurrent_game().addHist_jeu(hacker_tv_resultat.getText().toString() + "\n\n");
                    display_next_role();
                }
            });

        } else {
            hacker_tv_desc.setText("Le hacker\n\n   " + pers_hacker.getNom() +
                    "\n\nse réveille. Comme il n'a personne à hacker, il est informaticien.");

            hacker_spinner.setVisibility(View.INVISIBLE);
            hacker_tv_resultat.setText("Le hacker-informaticien compte " + compteur + " mutant(s) à bord.");

            but_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gameSingleton.getCurrent_game().addHist_jeu("---\nTour du hacker\n\nLe hacker n'a personne à hacker ; il est donc informaticien.\n");
                    gameSingleton.getCurrent_game().addHist_jeu(hacker_tv_resultat.getText().toString() + "\n\n");
                    display_next_role();
                }
            });
        }
    }

    private void display_role_apprenti_hacker() {

        RelativeLayout apprenti_hacker_layout = (RelativeLayout) RelativeLayout.inflate(getActivity(), gameSingleton.APPRENTI_HACKER.getLayout_id(), null);
        lay_role_night.addView(apprenti_hacker_layout);

        TextView apprenti_hacker_tv_desc = (TextView) apprenti_hacker_layout.findViewById(R.id.role_simple_tv_desc);

        final TextView apprenti_hacker_tv_resultat = (TextView) apprenti_hacker_layout.findViewById(R.id.role_simple_tv_resultat);

        final Spinner apprenti_hacker_spinner = (Spinner) apprenti_hacker_layout.findViewById(R.id.role_simple_spinner);

        // Qui est l'apprenti hacker ?
        Character pers_tmp = gameSingleton.BLANK;
        for (Character p : gameSingleton.personnages_vivants_debut_tour) {
            if (p.getRole().equals(gameSingleton.APPRENTI_HACKER)) {
                pers_tmp = p;
            }
        }

        final Character pers_apprenti_hacker = pers_tmp;

        final ArrayList<Role> choix_apprenti_hacker = new ArrayList<Role>();
        for (Role role : gameSingleton.ROLES_LIST_HACKER) {
            if (isRole(role)) {
                if (!return_character_given_role(role).isMort_confirme()) {
                    choix_apprenti_hacker.add(role);
                }
            }
        }

        // Quel est le nombre de mutants à bord ?
        int compteur_tmp = 0;
        for (Character p : gameSingleton.getCurrent_game().getCharacters()) {
            if (!p.isMort() && p.isContamine()) {
                compteur_tmp++;
            }
        }
        final int compteur = compteur_tmp;

        if (!choix_apprenti_hacker.isEmpty()) {

            apprenti_hacker_tv_desc.setText("\n  " + pers_apprenti_hacker.getNom() +
                    "\n\n" + getString(R.string.texte_apprenti_hacker));

            ArrayAdapter<Role> apprenti_hacker_adapter = new ArrayAdapter<Role>(getActivity(), R.layout.spinner_theme, choix_apprenti_hacker);
            apprenti_hacker_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            apprenti_hacker_spinner.setAdapter(apprenti_hacker_adapter);

            apprenti_hacker_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Role role_selectionne = choix_apprenti_hacker.get(position);
                    if (role_selectionne.equals(gameSingleton.INFORMATICIEN)) {
                        if (gameSingleton.role_a_joue_nuit.contains(gameSingleton.INFORMATICIEN)) {
                            apprenti_hacker_tv_resultat.setText("L'informaticien a compté " + compteur + " mutant(s) à bord.");
                        } else {
                            apprenti_hacker_tv_resultat.setText("L'informaticien n'a pas joué cette nuit.");
                        }
                    } else if (role_selectionne.equals(gameSingleton.PSYCHOLOGUE)) {
                        if (gameSingleton.role_a_joue_nuit.contains(gameSingleton.PSYCHOLOGUE)) {
                            for (Character p : gameSingleton.personnages_vivants_debut_tour) {
                                if (gameSingleton.actions_tour_nuit.get(p) != null && gameSingleton.actions_tour_nuit.get(p).contains(Game.GameSingleton.Night_action.PSYCHOLOGUE) ||
                                        gameSingleton.resultat_role_hacker.contains(Game.GameSingleton.Night_action_result.PSY_MUTANT)) {
                                    apprenti_hacker_tv_resultat.setText("Le psychologue a inspecté " + p.getNom() + ".");
                                }
                            }
                        } else {
                            apprenti_hacker_tv_resultat.setText("Le psychologue n'a pas joué cette nuit.");
                        }
                    } else if (role_selectionne.equals(gameSingleton.GENETICIEN)) {
                        if (gameSingleton.role_a_joue_nuit.contains(gameSingleton.GENETICIEN)) {
                            for (Character p : gameSingleton.personnages_vivants_debut_tour) {
                                if (gameSingleton.actions_tour_nuit.get(p) != null && gameSingleton.actions_tour_nuit.get(p).contains(Game.GameSingleton.Night_action.GENETICIEN)) {
                                    apprenti_hacker_tv_resultat.setText("Le généticien a inspecté " + p.getNom() + ".");
                                }
                            }
                        } else {
                            apprenti_hacker_tv_resultat.setText("Le généticien n'a pas joué cette nuit.");
                        }
                    } else if (role_selectionne.equals(gameSingleton.POLITICIEN)) {
                        if (gameSingleton.role_a_joue_nuit.contains(gameSingleton.POLITICIEN)) {
                            for (Character p : gameSingleton.personnages_vivants_debut_tour) {
                                if (gameSingleton.actions_tour_nuit.get(p) != null && gameSingleton.actions_tour_nuit.get(p).contains(Game.GameSingleton.Night_action.POLITICIEN)) {
                                    apprenti_hacker_tv_resultat.setText("Le politicien a inspecté " + p.getNom() + ".");
                                }
                            }
                        } else {
                            apprenti_hacker_tv_resultat.setText("Le politicien n'a pas joué cette nuit.");
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Rien
                }
            });

            but_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Role role_cible = (Role) apprenti_hacker_spinner.getSelectedItem();
                    Character targeted_character = return_character_given_role(role_cible);

                    // Information pour le hacker
                    gameSingleton.getCurrent_game().addHist_jeu("---\nTour de l'apprenti hacker\n\nL'apprenti hacker choisit de hacker " +
                            targeted_character.getNom() + " (" + targeted_character.getRole() + ")" + ".\n");
                    gameSingleton.getCurrent_game().addHist_jeu(apprenti_hacker_tv_resultat.getText().toString() + "\n\n");
                    display_next_role();
                }
            });
        } else {
            apprenti_hacker_tv_desc.setText("L'apprenti hacker\n\n   " + pers_apprenti_hacker.getNom() +
                    "\n\nse réveille. Comme il n'a personne à hacker, il est informaticien.");

            apprenti_hacker_spinner.setVisibility(View.INVISIBLE);
            apprenti_hacker_tv_resultat.setText("L'apprenti hacker-informaticien compte " + compteur + " mutant(s) à bord.");

            but_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gameSingleton.getCurrent_game().addHist_jeu("---\nTour de l'apprenti hacker\n\nL'apprenti hacker n'a personne à hacker ; il est donc informaticien.\n");
                    gameSingleton.getCurrent_game().addHist_jeu(apprenti_hacker_tv_resultat.getText().toString() + "\n\n");
                    display_next_role();
                }
            });
        }

    }

    private void display_role_espion() {

        RelativeLayout espion_layout = (RelativeLayout) RelativeLayout.inflate(getActivity(), gameSingleton.ESPION.getLayout_id(), null);
        lay_role_night.addView(espion_layout);

        TextView espion_tv_desc = (TextView) espion_layout.findViewById(R.id.role_simple_tv_desc);
        final TextView espion_tv_resultat = (TextView) espion_layout.findViewById(R.id.role_simple_tv_resultat);

        final Spinner espion_spinner = (Spinner) espion_layout.findViewById(R.id.role_simple_spinner);

        // Qui est l'espion ?
        Character pers_tmp = gameSingleton.BLANK;
        for (Character p : gameSingleton.personnages_vivants_debut_tour) {
            if (p.getRole().equals(gameSingleton.ESPION)) {
                pers_tmp = p;
            }
        }
        final Character pers_espion = pers_tmp;

        espion_tv_desc.setText("\n  " + pers_espion.getNom() +
                "\n\n" + getString(R.string.texte_espion));

        ArrayAdapter<Character> espion_adapter = new ArrayAdapter<Character>(getActivity(), R.layout.spinner_theme, gameSingleton.personnages_vivants_debut_tour);
        espion_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        espion_spinner.setAdapter(espion_adapter);

        espion_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Character selected_character = gameSingleton.personnages_vivants_debut_tour.get(position);
                String s = selected_character.getNom() + " a été :\n";
                s += "\n    Tué : ";
                if (gameSingleton.actions_tour_nuit.get(selected_character) != null && gameSingleton.actions_tour_nuit.get(selected_character).contains(Game.GameSingleton.Night_action.TUE)) {
                    s += "Oui";
                }

                s += "\n    Muté : ";
                if (gameSingleton.actions_tour_nuit.get(selected_character) != null && gameSingleton.actions_tour_nuit.get(selected_character).contains(Game.GameSingleton.Night_action.MUTE)) {
                    s += "Oui";
                }
                s += "\n    Paralysé : ";
                if (gameSingleton.actions_tour_nuit.get(selected_character) != null && gameSingleton.actions_tour_nuit.get(selected_character).contains(Game.GameSingleton.Night_action.PARALYSE)) {
                    s += "Oui";
                }
                s += "\n    Soigné : ";
                if (gameSingleton.actions_tour_nuit.get(selected_character) != null && gameSingleton.actions_tour_nuit.get(selected_character).contains(Game.GameSingleton.Night_action.SOIGNE)) {
                    s += "Oui";
                }
                if (isRole(gameSingleton.PSYCHOLOGUE)) {
                    s += "\n    Inspecté par le psychologue : ";
                    if (gameSingleton.actions_tour_nuit.get(selected_character) != null && gameSingleton.actions_tour_nuit.get(selected_character).contains(Game.GameSingleton.Night_action.PSYCHOLOGUE)) {
                        s += "Oui";
                    }
                }
                if (isRole(gameSingleton.GENETICIEN)) {
                    s += "\n    Inspecté par le généticien : ";
                    if (gameSingleton.actions_tour_nuit.get(selected_character) != null && gameSingleton.actions_tour_nuit.get(selected_character).contains(Game.GameSingleton.Night_action.GENETICIEN)) {
                        s += "Oui";
                    }
                }
                if (isRole(gameSingleton.POLITICIEN)) {
                    s += "\n    Inspecté par le politicien : ";
                    if (gameSingleton.actions_tour_nuit.get(selected_character) != null && gameSingleton.actions_tour_nuit.get(selected_character).contains(Game.GameSingleton.Night_action.POLITICIEN)) {
                        s += "Oui";
                    }
                }
                espion_tv_resultat.setText(s);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Rien
            }
        });

        but_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Character targeted_character = (Character) espion_spinner.getSelectedItem();
                add_to_visit_list(targeted_character, pers_espion);

                gameSingleton.getCurrent_game().addHist_jeu("---\nTour de l'espion\n\nL'espion choisit d'espionner " +
                        targeted_character.getNom() + ".\n" + espion_tv_resultat.getText() + "\n\n");
                display_next_role();
            }
        });
    }

    private void display_role_peintre() {

        RelativeLayout peintre_layout = (RelativeLayout) RelativeLayout.inflate(getActivity(), gameSingleton.PEINTRE.getLayout_id(), null);
        lay_role_night.addView(peintre_layout);

        TextView peintre_tv_desc = (TextView) peintre_layout.findViewById(R.id.role_simple_tv_desc);
        final TextView peintre_tv_resultat = (TextView) peintre_layout.findViewById(R.id.role_simple_tv_resultat);

        final Spinner peintre_spinner = (Spinner) peintre_layout.findViewById(R.id.role_simple_spinner);

        // Qui est le peintre ?
        // Le peintre est dans la liste de ses choix
        Character pers_tmp = gameSingleton.BLANK;
        for (Character p : gameSingleton.personnages_vivants_debut_tour) {
            if (p.getRole().equals(gameSingleton.PEINTRE)) {
                pers_tmp = p;
                break;
            }
        }
        final Character pers_peintre = pers_tmp;

        peintre_tv_desc.setText("\n  " + pers_peintre.getNom() +
                "\n\n" + getString(R.string.texte_peintre));

        ArrayAdapter<Character> peintre_adapter = new ArrayAdapter<Character>(getActivity(), R.layout.spinner_theme, gameSingleton.personnages_vivants_debut_tour);
        peintre_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        peintre_spinner.setAdapter(peintre_adapter);

        peintre_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Character selected_character = gameSingleton.personnages_vivants_debut_tour.get(position);
                String resultat = "";
                if (gameSingleton.visites_tour_nuit.get(selected_character) != null) {
                    resultat = "Voici la liste des personnages ayant visité " + selected_character.getNom() + " :\n\n";
                    for (Character p : gameSingleton.visites_tour_nuit.get(selected_character)) {
                        resultat += "   " + p.getNom() + ", " + p.getRole() + "\n";
                    }
                } else {
                    resultat = "Personne n'a visité le personnage marqué.\n";
                }
                peintre_tv_resultat.setText(resultat);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Rien
            }
        });

        but_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Character targeted_character = (Character) peintre_spinner.getSelectedItem();

                gameSingleton.getCurrent_game().addHist_jeu("---\nTour du peintre\n\nLe peintre choisit de marquer " +
                        targeted_character.getNom() + ".\n" +
                        peintre_tv_resultat.getText() + "\n");
                display_next_role();
            }
        });
    }

    private void display_role_if_mort(final Role role) {
        String s = role.getNom() + " - Le personnage est mort.\n\n" + getString(role.getTexte_id());
        TextView tv = new TextView(getActivity());
        tv.setText(s);
        lay_role_night.addView(tv);
        but_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameSingleton.getCurrent_game().addHist_jeu("---\nLe personnage " + role.getNom() + " est mort.\n\n");

                // Passage au suivant
                display_next_role();
            }
        });
    }

    private void display_role_if_paralyse(final Role role) {
        lay_role_night.removeAllViews();
        String s = role.getNom() + " - Le personnage est paralysé. \n\n" + getString(role.getTexte_id());
        TextView tv = new TextView(getActivity());
        tv.setText(s);
        lay_role_night.addView(tv);
        but_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameSingleton.getCurrent_game().addHist_jeu("---\nLe personnage " + role.getNom() + " est paralysé.\n\n");

                // Passage au suivant
                display_next_role();
            }
        });
    }

    private void display_role_if_contamine(final Role role) {
        lay_role_night.removeAllViews();
        String s = role.getNom() + " - Le personnage est mutant.\n\n" + getString(role.getTexte_id());
        TextView tv = new TextView(getActivity());
        tv.setText(s);
        lay_role_night.addView(tv);
        but_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameSingleton.getCurrent_game().addHist_jeu("---\nLe personnage " + role.getNom() + " est mutant.\n\n");

                // Passage au suivant
                display_next_role();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_ROLE, current_role_index);
    }

    /**
     * Attempts contaminating the given character. Writes the result in game history.
     *
     * @param p Target character.
     * @return True if success, else false.
     */
    private boolean action_contaminer(Character p) {
        if (!p.isMort() && !p.getGene().equals(gameSingleton.RESISTANT)) {
            p.setContamine(true);
            gameSingleton.getCurrent_game().addHist_jeu(p.getNom() + " est maintenant mutant !\n\n");
            return true;
        } else {
            gameSingleton.getCurrent_game().addHist_jeu(p.getNom() + " a résisté à la mutation !\n\n");
            return false;
        }
    }

    /**
     * Attempts healing the given character. Writes the result in game history.
     *
     * @param p Target character
     * @return True if success, else false.
     */
    private boolean action_soigner(Character p) {
        if (!p.isMort()) {
            if (p.getGene().equals(gameSingleton.HOTE) && p.isContamine()) {
                gameSingleton.getCurrent_game().addHist_jeu("Le soin sur " + p.getNom() + " a échoué !\n\n");
                return false;
            } else if (p.isContamine()) {
                gameSingleton.getCurrent_game().addHist_jeu(p.getNom() + " n'est plus mutant !\n\n");
                p.setContamine(false);
                return true;
            }
            gameSingleton.getCurrent_game().addHist_jeu(p.getNom() + " est soigné ! (sans effet)\n\n");
            return true;
        }
        gameSingleton.getCurrent_game().addHist_jeu(p.getNom() + " est déjà mort ! (sans effet)\n\n");
        return false;
    }

    private boolean isRole(Role role) {
        for (Character p : gameSingleton.getCurrent_game().getCharacters()) {
            // Si le rôle a été attribué et que le rôle n'est pas mort confirmé
            if (p.getRole().equals(role) && !p.isMort_confirme()) {
                return true;
            }
        }
        return false;
    }

    private boolean isRole_vivant(Role role) {
        for (Character p : gameSingleton.getCurrent_game().getCharacters()) {
            if (p.getRole().equals(role) && !p.isMort()) {
                return true;
            }
        }
        return false;
    }

    private boolean isRole_non_paralyse(Role role) {
        for (Character p : gameSingleton.getCurrent_game().getCharacters()) {
            if (p.getRole().equals(role) && !p.isMort() && !p.isParalyse()) {
                return true;
            }
        }
        return false;
    }

    private boolean isRole_non_contamine(Role role) {
        for (Character p : gameSingleton.getCurrent_game().getCharacters()) {
            if (p.getRole().equals(role) && !p.isMort() && !p.isContamine()) {
                return true;
            }
        }
        return false;
    }

    private Character return_character_given_role(Role role) {
        for (Character p : gameSingleton.getCurrent_game().getCharacters()) {
            if (p.getRole().equals(role)) {
                return p;
            }
        }
        return null;
    }

    private void add_to_visit_list(Character cible, Character visiteur) {
        try {
            Set<Character> set = gameSingleton.visites_tour_nuit.get(cible);
            set.add(visiteur);
            gameSingleton.visites_tour_nuit.put(cible, set);
        } catch (NullPointerException e) {
            Set<Character> set = new HashSet<Character>();
            set.add(visiteur);
            gameSingleton.visites_tour_nuit.put(cible, set);
        }
    }

    private void add_to_action_list(Character cible, Game.GameSingleton.Night_action action) {
        try {
            Set<Game.GameSingleton.Night_action> set = gameSingleton.actions_tour_nuit.get(cible);
            set.add(action);
            gameSingleton.actions_tour_nuit.put(cible, set);
        } catch (NullPointerException e) {
            Set<Game.GameSingleton.Night_action> set = new HashSet<Game.GameSingleton.Night_action>();
            set.add(action);
            gameSingleton.actions_tour_nuit.put(cible, set);
        }
    }

    @Override
    public void update() {
        if (((Activity_gameturn) getActivity()).somebody_win()) {
            super.update();
            layout_game.setVisibility(View.INVISIBLE);
            layout_endgame.setVisibility(View.VISIBLE);
        } else {
            display_role(current_role_index);
            layout_game.setVisibility(View.VISIBLE);
            layout_endgame.setVisibility(View.INVISIBLE);
        }
    }

    protected void showDialog_end_night_gameturn(String text) {

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
        Dialog_end_night_gameturn newFragment = Dialog_end_night_gameturn.newInstance(text);
        newFragment.setCancelable(false);
        newFragment.show(ft, "dialog");
    }

    public static class Dialog_end_night_gameturn extends Dialog_fin_tour {


        public static Dialog_end_night_gameturn newInstance(String text) {
            Dialog_end_night_gameturn f = new Dialog_end_night_gameturn();
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
                    // Go to next role
                    ((Fragment_gameturn_night)((Activity_gameturn) getActivity()).getAndReleaseFragment_game()).display_next_role();
                    dialog.dismiss();
                }
            });

            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

}
