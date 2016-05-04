package com.minastelien.quentin.gestionnairedesporz.Game;

/**
 * This is a character in the game.
 * Created by Quentin on 28/12/2015.
 */
public class Character {

    String nom;
    Role role;
    Gene gene;
    boolean mort;
    boolean contamine;
    boolean paralyse = false;
    boolean mort_confirme = false;

    public Character(String n, Role ro, Gene ge, boolean m, boolean c) {
        super();
        nom = n;
        role = ro;
        gene = ge;
        mort = m;
        contamine = c;
    }

    public Character(String nom) {
        this(nom, Game.getGameSingleton().SIMPLE_ASTRONAUTE,
                Game.getGameSingleton().NORMAL, false, false);
    }

    public String toString() {
        return nom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Gene getGene() {
        return gene;
    }

    public void setGene(Gene gene) {
        this.gene = gene;
    }

    public boolean isMort() {
        return mort;
    }

    public void setMort(boolean mort) {
        this.mort = mort;
    }

    public boolean isMort_confirme() {
        return mort_confirme;
    }

    public void setMort_confirme(boolean mort_confirme) {
        this.mort_confirme = mort_confirme;
    }

    public boolean isContamine() {
        return contamine;
    }

    public void setContamine(boolean contamine) {
        this.contamine = contamine;
    }

    public boolean isParalyse() {
        return paralyse;
    }

    public void setParalyse(boolean paralyse) {
        this.paralyse = paralyse;
    }
}
