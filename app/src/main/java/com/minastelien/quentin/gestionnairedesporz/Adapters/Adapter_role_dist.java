package com.minastelien.quentin.gestionnairedesporz.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.minastelien.quentin.gestionnairedesporz.Game.Game;
import com.minastelien.quentin.gestionnairedesporz.Game.Role;
import com.minastelien.quentin.gestionnairedesporz.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Adapter used in game setup for role distribution.
 * Created by Quentin on 16/01/2016.
 */
public class Adapter_role_dist extends ArrayAdapter<Role> {

    protected ArrayList<Role> roles;
    protected HashMap<Role, Integer> roles_count;
    protected Context context;

    protected Game.GameSingleton gameSingleton;

    public Adapter_role_dist(Context context, int resource, ArrayList<Role> objects, Game.GameSingleton sing) {
        super(context, resource, objects);
        this.context = context;
        this.roles = objects;
        roles_count = new HashMap<>();
        gameSingleton = sing;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        RelativeLayout view = null;
        // If this is a recycled view, it already contains the right layout.
        if (convertView != null) {
            // Get the view.
            view = (RelativeLayout) convertView;
        } else {
            // Else, inflate the right layout from resources.
            view = (RelativeLayout) RelativeLayout.inflate(context, R.layout.adapter_dist_roles, null);
        }

        final RelativeLayout layout_sub = (RelativeLayout) view.findViewById(R.id.adapter_dist_roles_relatlayout);
        final CheckBox checkbox = (CheckBox) view.findViewById(R.id.adapter_dist_roles_checkbox);
        final TextView layout_sub_edittext = (TextView) layout_sub.findViewById(R.id.adapter_dist_roles_textview);
        final Button button_left = (Button) layout_sub.findViewById(R.id.adapter_dist_roles_butleft);
        final Button button_right = (Button) layout_sub.findViewById(R.id.adapter_dist_roles_butright);

        if (roles.get(position).equals(gameSingleton.MUTANT_DE_BASE) || roles.get(position).equals(gameSingleton.MEDECIN)) {
            layout_sub.setVisibility(View.VISIBLE);

            try {
                int value = roles_count.get(roles.get(position));
                layout_sub_edittext.setText("" + value);
            } catch (NullPointerException e) {
                layout_sub_edittext.setText("" + 0);
            }

            button_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int value;
                    try {
                        value = Integer.parseInt(layout_sub_edittext.getText().toString());
                    } catch (NumberFormatException e) {
                        value = 0;
                    }
                    if (value > 0) {
                        layout_sub_edittext.setText("" + (value - 1));
                        roles_count.put(roles.get(position), Integer.parseInt(layout_sub_edittext.getText().toString()));
                        if (value - 1 <= 0) {
                            checkbox.setChecked(false);
                        }
                    }
                }
            });

            button_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int valeur;
                    try {
                        valeur = Integer.parseInt(layout_sub_edittext.getText().toString());
                    } catch (NumberFormatException e) {
                        valeur = 0;
                    }
                    layout_sub_edittext.setText("" + (valeur + 1));
                    roles_count.put(roles.get(position), Integer.parseInt(layout_sub_edittext.getText().toString()));
                    checkbox.setChecked(true);
                }
            });
        } else {
            layout_sub.setVisibility(View.INVISIBLE);
        }

        checkbox.setText(roles.get(position).getNom());
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (roles.get(position).equals(gameSingleton.MEDECIN) || roles.get(position).equals(gameSingleton.MUTANT_DE_BASE)) {
                        try {
                            roles_count.put(roles.get(position), Integer.parseInt(layout_sub_edittext.getText().toString()));
                        } catch (NumberFormatException e) {
                            roles_count.put(roles.get(position), 0);
                        }
                    } else {
                        roles_count.put(roles.get(position), 1);
                    }
                } else {
                    roles_count.put(roles.get(position), 0);
                }
            }
        });

        return view;
    }

    public ArrayList<Role> getChoix() {
        ArrayList<Role> choix = new ArrayList<>();
        for (int i = 0; i < roles.size(); i++) {
            if (roles_count.get(roles.get(i)) != null) {
                for (int j = 0; j < roles_count.get(roles.get(i)); j++) {
                    choix.add(roles.get(i));
                }
            }
        }
        return choix;
    }
}
