package com.minastelien.quentin.gestionnairedesporz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.minastelien.quentin.gestionnairedesporz.Adapters.Adapter_character_sumup;
import com.minastelien.quentin.gestionnairedesporz.Game.Character;

/**
 * This activity provides an overview of the game settings for player information before the game
 * starts.
 * Created by Quentin on 28/12/2015.
 */
public class Activity_sumup extends Activity_main {

    private RelativeLayout lay_glob;
    private ListView sumup_lv;
    private ArrayAdapter<Character> sumup_adapter;
    private Button sumup_bouton_suiv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lay_glob = (RelativeLayout) RelativeLayout.inflate(this, R.layout.activity_sumup, null);
        setContentView(lay_glob);
        build_sumup();
    }

    /**
     * Fills the layout with data and sets button listener.
     */
    private void build_sumup() {
        // Initialisation de la liste des personnages
        sumup_lv = (ListView) lay_glob.findViewById(R.id.sumup_lv);
        sumup_adapter = new Adapter_character_sumup(this, gameSingleton.getCurrent_game().getCharacters(), gameSingleton);
        sumup_lv.setAdapter(sumup_adapter);

        // Initialisation du bouton
        sumup_bouton_suiv = (Button) lay_glob.findViewById(R.id.act_sumup_bout_comm);
        sumup_bouton_suiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent deuxiemeActivite = new Intent(Activity_sumup.this, Activity_gameturn_night.class);
                startActivity(deuxiemeActivite);
            }
        });
    }
}
