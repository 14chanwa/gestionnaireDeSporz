package com.minastelien.quentin.gestionnairedesporz.Game;

import com.minastelien.quentin.gestionnairedesporz.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Main singleton game entity. Handle all game data.
 * Created by Quentin on 28/12/2015.
 */
public class Game {

    private ArrayList<Character> characters;
    private int turn_count;
    private String game_hist;

    /**
     * Private constructor according to Singleton pattern.
     */
    private Game() {
        characters = new ArrayList<>();
        turn_count = 0;
        game_hist = "";
    }

    /**
     * Resets the game creating a new Game instance.
     */
    public static void resetGame() {
        getGameSingleton().current_game.characters = new ArrayList<>();
        getGameSingleton().current_game.turn_count = 0;
        getGameSingleton().current_game.game_hist = "";
    }

    /**
     * Checks whether GameSingleton has already been initialized.
     *
     * @return True if already initialized, else false.
     */
    public static boolean isThereSingleton() {
        return GameSingleton.gameSingleton != null;
    }

    /**
     * Returns the only GameSingleton instance.
     *
     * @return The only GameSingleton instance.
     */
    public static GameSingleton getGameSingleton() {
        if (!isThereSingleton()) {
            GameSingleton.gameSingleton = new GameSingleton();
        }
        return GameSingleton.gameSingleton;
    }

    public ArrayList<Character> getCharacters() {
        return characters;
    }

    public void setCharacters(ArrayList<Character> characters) {
        this.characters = characters;
    }

    public int getTurn_count() {
        return turn_count;
    }

    public void setTurn_count(int turn_count) {
        this.turn_count = turn_count;
    }

    public String getGame_hist() {
        return game_hist;
    }

    public void setGame_hist(String game_hist) {
        this.game_hist = game_hist;
    }

    public void addHist_jeu(String hist_sequel) {
        setGame_hist(getGame_hist() + hist_sequel);
    }


    /**
     * This Singleton class contains all static game attributes.
     */
    public static class GameSingleton {

        /**
         * The singleton GameSingleton instance.
         */
        private static GameSingleton gameSingleton;
        /*
         * Parameters
         */
        public final HashSet<String> FORBIDDEN_PLAYER_NAMES = new HashSet<String>() {{
            add("");
            add("Blanc");
            add("Abstention");
        }};
        /*
         * Gene attributes
         */
        public final Gene NORMAL = new Gene("Normal");
        public final Gene HOTE = new Gene("Hôte");
        public final Gene RESISTANT = new Gene("Résistant");
        public final ArrayList<Gene> GENES_LIST = new ArrayList<Gene>() {{
            add(NORMAL);
            add(HOTE);
            add(RESISTANT);
        }};
        /*
         * Role attributes
         */
        public final Role MUTANT_DE_BASE = new Role("Mutant de base", "Chaque nuit les mutants vont désigner un joueur à muter. Ce joueur est transformé en mutant.\nIls désignent aussi une personne à paralyser, c'est à dire une personne qui n'aura pas la possibilité de jouer son action cette nuit, mais agira normalement par la suite.", Role.Side.MUTANT, true, R.layout.fragment_role_mutant_de_base, R.string.texte_mutant);
        public final Role MEDECIN = new Role("Médecin", "Chaque nuit les médecins agissent s'ils n'ont pas été mutés ou paralysés.\nChacun peut soigner un autre joueur.\nSi on est mutant et qu'on se fait soigner, alors on redevient sain. Sinon rien ne se passe.\nLe mutant originel ne peut jamais être soigné.", Role.Side.HUMAIN, false, R.layout.fragment_role_medecin, R.string.texte_medecin);
        public final Role INFORMATICIEN = new Role("Informaticien", "Chaque nuit l'ordinateur de bord indique à l'Informaticien le nombre exact de mutants à bord.", Role.Side.HUMAIN, false, R.layout.fragment_role_textonly, R.string.texte_informaticien);
        public final Role PSYCHOLOGUE = new Role("Psychologue", "Chaque nuit le Psychologue désigne un joueur. Il apprend si sa cible est mutante ou non (mais ne connait pas son rôle).", Role.Side.HUMAIN, false, R.layout.fragment_role_default, R.string.texte_psychologue);
        public final Role GENETICIEN = new Role("Généticien", "Chaque nuit le Généticien désigne un joueur. Il apprend si sa cible est de génôme Résistant, Hôte, ou Standard.\nLe Résistant ne peut jamais être muté.\nLes Hôtes ne peuvent jamais êtes soignés.\nLes joueurs de génômes Standard peuvent être mutés ou soigner normalement.", Role.Side.HUMAIN, false, R.layout.fragment_role_default, R.string.texte_geneticien);
        public final Role POLITICIEN = new Role("Politicien", "Chaque nuit, le Politicien désigne un joueur et apprend contre qui ce joueur a voté pendant la journée précédente. Il ne se réveille pas la première nuit.", Role.Side.HUMAIN, false, R.layout.fragment_role_default, R.string.texte_politicien);
        public final Role HACKER = new Role("Hacker", "Chaque nuit le Hacker peut \"pirater\" les fichiers d'un rôle donné.\nIl a le choix entre Informaticien, Psychologue, Généticien, Espion.\nIl reçoit une partie de l'information de la nuit correspondante.\nPar exemple s'il choisit Informaticien, il sait combien l'Informaticien a vu de mutants cette nuit.\nS'il choisit Psychologue (respectivement Généticien ou Espion) il sait QUI a été inspecté par le Psychologue (respectivement le Généticen ou l'Espion).", Role.Side.HUMAIN, false, R.layout.fragment_role_default, R.string.texte_hacker);
        public final Role APPRENTI_HACKER = new Role("Apprenti hacker", "Chaque nuit l'Apprenti Hacker peut \"pirater\" les fichiers d'un rôle donné.\nIl a le choix entre Informaticien, Psychologue, Généticien, Espion.\nIl reçoit une partie de l'information de la nuit correspondante.\nPar exemple s'il choisit Informaticien, il sait combien l'Informaticien a vu de mutants cette nuit.\nS'il choisit Psychologue (respectivement Généticien ou Espion) il sait le résultat qui a été découvert par le Psychologue (respectivement le Généticen ou l'Espion).", Role.Side.HUMAIN, false, R.layout.fragment_role_default, R.string.texte_apprenti_hacker);
        public final Role ESPION = new Role("Espion", "Chaque nuit l'Espion désigne un joueur. Il apprend si sa cible a oui ou non été mutée, paralysée, soignée, ou inspectée par le psychologue cette nuit (le meneur fait oui ou non de la tête à chaque fois pour que seul l'Espion sache exactement ce qu'il est arrivé à sa cible cette nuit).", Role.Side.HUMAIN, false, R.layout.fragment_role_default, R.string.texte_espion);
        public final Role PEINTRE = new Role("Peintre", "Chaque nuit, le Peintre désigne un joueur et dépose de la peinture sur sa porte. Il peut retracer toutes les personnes ayant été en contact d'une manière ou d'une autre avec ce joueur.", Role.Side.HUMAIN, false, R.layout.fragment_role_default, R.string.texte_peintre);
        public final Role FANATIQUE = new Role("Fanatique", "Le Fanatique n'a pas de pouvoir spécial, mais il gagne la partie si les mutants gagnent.", Role.Side.MUTANT, false, R.layout.fragment_role_simple_astronaute, R.string.texte_default);
        public final Role SIMPLE_ASTRONAUTE = new Role("Simple astronaute", "L'Astronaute garde les yeux fermés toute la nuit.\nN'étant pas indispensable, il peut donc prendre plus de risques en journée lorsqu'il bluffe.\nDe plus, lorsqu'il se fait paralyser il a la garantie que TOUS les autres joueurs ont pu agir : en servant de paratonnerre il fait avancer la cause de son camp.", Role.Side.HUMAIN, false, R.layout.fragment_role_simple_astronaute, R.string.texte_default);
        public final ArrayList<Role> ROLES_LIST = new ArrayList<Role>() {{
            add(MUTANT_DE_BASE);
            add(MEDECIN);
            add(INFORMATICIEN);
            add(PSYCHOLOGUE);
            add(GENETICIEN);
            add(POLITICIEN);
            add(HACKER);
            add(APPRENTI_HACKER);
            add(ESPION);
            add(PEINTRE);
            add(FANATIQUE);
            add(SIMPLE_ASTRONAUTE);
        }};
        public final ArrayList<Role> ROLES_LIST_NIGHT = new ArrayList<Role>() {{
            add(MUTANT_DE_BASE);
            add(MEDECIN);
            add(INFORMATICIEN);
            add(PSYCHOLOGUE);
            add(GENETICIEN);
            add(POLITICIEN);
            add(HACKER);
            add(APPRENTI_HACKER);
            add(ESPION);
            add(PEINTRE);
        }};
        public final ArrayList<Role> ROLES_LIST_HACKER = new ArrayList<Role>() {{
            add(INFORMATICIEN);
            add(PSYCHOLOGUE);
            add(GENETICIEN);
            add(POLITICIEN);
        }};
        /*
         * Character attributes
         */
        public final Character BLANK = new Character("Blanc", SIMPLE_ASTRONAUTE, NORMAL, false, false);

        /*
         * Abstention item (for vote purposes)
         */
        public final Character ABSTENTION = new Character("Abstention", SIMPLE_ASTRONAUTE, NORMAL, false, false);

        /*
         * Game related attributes
         */
        public ArrayList<Character> personnages_vivants_debut_tour = new ArrayList<>();
        public HashSet<Role> role_a_joue_nuit = new HashSet<>();
        public HashMap<Character, Set<Character>> visites_tour_nuit = new HashMap<>();
        public HashMap<Character, Set<Night_action>> actions_tour_nuit = new HashMap<>();
        public HashSet<Night_action_result> resultat_role_hacker = new HashSet<>();
        public HashMap<Character, Character> resultats_votes_jour = new HashMap<>();
        /**
         * The singleton Game instance.
         */
        private Game current_game;

        /**
         * Private constructor according to Singleton pattern.
         */
        private GameSingleton() {
            current_game = new Game();
        }

        /**
         * Returns the current SingletonGame's game instance.
         *
         * @return Current game instance.
         */
        public Game getCurrent_game() {
            return this.current_game;
        }


        /**
         * Enumerate possible night actions.
         * Created by Quentin on 02/02/2016.
         */
        public enum Night_action {
            TUE,
            MUTE,
            PARALYSE,
            SOIGNE,
            PSYCHOLOGUE,
            GENETICIEN,
            POLITICIEN
        }

        /**
         * Enumerate possible results of night actions.
         * Created by Quentin on 02/02/2016.
         */
        public enum Night_action_result {
            INFORMATICIEN,
            PSY_SAIN,
            PSY_MUTANT,
            GENE_HOTE,
            GENE_NORMAL,
            GENE_RESISTANT,
            POLITICIEN
        }
    }
}
