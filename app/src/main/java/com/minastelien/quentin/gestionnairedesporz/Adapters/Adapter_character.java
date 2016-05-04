package com.minastelien.quentin.gestionnairedesporz.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import com.minastelien.quentin.gestionnairedesporz.Game.Character;
import com.minastelien.quentin.gestionnairedesporz.Game.Game;

import java.util.ArrayList;

/**
 * Abstract Adapter for characters. Initializes GameSingleton access.
 * Created by Quentin on 28/12/2015.
 */
public abstract class Adapter_character extends ArrayAdapter<Character> {

    protected Game.GameSingleton gameSingleton;

    protected ArrayList<Character> characters;
    protected Context context;
    protected int resource;

    public Adapter_character(Context context, int resource, ArrayList<Character> objects, Game.GameSingleton sing) {
        super(context, resource, objects);
        this.context = context;
        this.characters = objects;
        this.resource = resource;

        gameSingleton = sing;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout view = null;
        // If this is a recycled view, it already contains the right layout.
        if (convertView != null) {
            // Get the view.
            view = (RelativeLayout) convertView;
        } else {
            // Else, inflate the right layout from resources.
            view = (RelativeLayout) RelativeLayout.inflate(context, resource, null);
        }

        build_view(view, position);

        return view;
    }

    /**
     * Fills the view with data and sets listeners up. To be redefined in children classes.
     *
     * @param vue      The view to be filled.
     * @param position The view's position in the adapter.
     */
    protected abstract void build_view(View vue, int position);

    /**
     * Given an object and an ArrayList, finds the position of the object in the ArrayList
     *
     * @param target The object sought for.
     * @param a      The ArrayList to be looked over.
     * @return The position of the object in the ArrayList if found, else -1.
     */
    protected int seek_object(Object target, ArrayList a) {
        for (int i = 0; i < a.size(); i++) {
            if (target.equals(a.get(i))) {
                return i;
            }
        }
        return -1;
    }

}