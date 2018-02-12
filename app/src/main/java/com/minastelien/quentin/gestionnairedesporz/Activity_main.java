package com.minastelien.quentin.gestionnairedesporz;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.minastelien.quentin.gestionnairedesporz.Fragments.Fragment_stats_roles;
import com.minastelien.quentin.gestionnairedesporz.Game.Game;

/**
 * This class provides a base for all activities. It includes mainly GameSingleton check.
 * Created by Quentin on 02/02/2016.
 */
public abstract class Activity_main extends AppCompatActivity {

    protected Game.GameSingleton gameSingleton;

    /**
     * This method creates the Activity ; it checks if the GameSingleton has already been created. If
     * not, the user is brought to the welcome activity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Game.isThereSingleton()) {
            gameSingleton = Game.getGameSingleton();
        } else {
            retour_menu();
        }
    }

    /**
     * This method brings the user back to the welcome activity, then kills the current activity.
     */
    protected void retour_menu() {
        Intent mStartActivity = new Intent(this, Activity_welcome.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId,
                mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    /**
     * Builds and shows the save history dialog.
     */
    public void showDialog_help_roles() {

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog_help_roles");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        Dialog_help_roles newFragment = new Dialog_help_roles();
        newFragment.setCancelable(true);
        newFragment.show(ft, "dialog_help_roles");
    }

    public static class Dialog_help_roles extends DialogFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(STYLE_NO_FRAME, android.R.style.Theme_Light);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if (getDialog() != null) {
                getDialog().setCanceledOnTouchOutside(true);
            }
            RelativeLayout lay = (RelativeLayout) inflater.inflate(R.layout.dialog_help_roles, container, false);

            // [2.03] The Fragment_stats_roles was added directly in the layout file.

            lay.findViewById(R.id.dialog_help_roles_but).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });

            return lay;
        }
    }
}
