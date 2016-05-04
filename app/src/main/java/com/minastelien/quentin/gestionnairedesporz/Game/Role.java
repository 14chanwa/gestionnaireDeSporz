package com.minastelien.quentin.gestionnairedesporz.Game;

/**
 * This is a character's role in the game.
 * Created by Quentin on 28/12/2015.
 */
public class Role {

    private final String nom;
    private final String description;
    private final Side start_side;
    private final boolean contamine_depart;
    private final int layout_id;
    private final int texte_id;

    public Role(String n, String d, Side c, boolean cont, int id, int id_texte) {
        nom = n;
        description = d;
        start_side = c;
        contamine_depart = cont;
        layout_id = id;
        texte_id = id_texte;
    }

    public String toString() {
        return nom;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public Side getStart_side() {
        return start_side;
    }

    public boolean isContamine_depart() {
        return contamine_depart;
    }

    public int getLayout_id() {
        return layout_id;
    }

    public int getTexte_id() {
        return texte_id;
    }


    /**
     * Enumerates possible sides.
     * Created by Quentin on 02/02/2016.
     */
    public enum Side {
        HUMAIN, MUTANT
    }
}
