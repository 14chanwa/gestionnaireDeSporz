package com.minastelien.quentin.gestionnairedesporz.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.minastelien.quentin.gestionnairedesporz.Game.Game;

/**
 * Abstract Fragment class used as base by all other Fragments. Gets the game's singleton.
 * Created by Quentin on 03/02/2016.
 */
public abstract class Fragment_main extends Fragment {

    protected Game.GameSingleton gameSingleton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Game.isThereSingleton()) {
            gameSingleton = Game.getGameSingleton();
        } else {
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        }
    }

}
