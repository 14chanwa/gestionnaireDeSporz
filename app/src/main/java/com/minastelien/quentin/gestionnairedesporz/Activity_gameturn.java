package com.minastelien.quentin.gestionnairedesporz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.minastelien.quentin.gestionnairedesporz.Fragments.Fragment_game_hist;
import com.minastelien.quentin.gestionnairedesporz.Fragments.Fragment_game_sumup;
import com.minastelien.quentin.gestionnairedesporz.Fragments.Fragment_gameturn;
import com.minastelien.quentin.gestionnairedesporz.Game.Character;

/**
 * This activity provides the basis for all gameturn activities. It creates the drawer and the views
 * common to day and night gameturns, that are sumup and hist.
 * Created by Quentin on 28/12/2015.
 */
public abstract class Activity_gameturn extends Activity_main {

    private final String KEY_FIN_JEU_INSCRIT = "endgame_written";
    private final String KEY_CURRENT_FRAME = "current_frame";
    private final String KEY_DIALOG_SHOWN = "dialog_shown";

    private final String TAG_FRAGMENT_GAME = "fragment_game";
    private final String TAG_FRAGMENT_SUMUP = "fragment_sumup";
    private final String TAG_FRAGMENT_HIST = "fragment_game_hist";

    private final String[] tabNames = {getTabName(), "Rôles", "Histoire"};
    /**
     * To be assigned by children classes.
     */
    protected Fragment_gameturn fragment_game;
    private ViewPager layout_viewpager;
    private TabLayout tabLayout;
    private Fragment_game_sumup fragment_sumup;
    private Fragment_game_hist fragment_game_hist;
    private boolean has_begun = false;
    private boolean is_ViewPager = true;
    private boolean endgame_written = false;

    /**
     * Builds the ViewPager layout OR the landscape layout depending on the configuration.
     *
     * @param savedInstanceState Saved parameter bundle.
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            endgame_written = savedInstanceState.getBoolean(KEY_FIN_JEU_INSCRIT);
            has_begun = savedInstanceState.getBoolean(KEY_DIALOG_SHOWN);
        }

        if (!has_begun) {
            begin_gameturn();
            has_begun = true;
        }

        /*
         * IMPORTANT
         * First tries to build the ViewPager layout. If fails (IllegalCastException or
         * NullPointerException), assume the landscape layout has to be built.
         */
        try {

            final View layout_principal = LinearLayout.inflate(this, R.layout.activity_gameturn, null);
            setContentView(layout_principal);

            // The following returns NullPointerException if no ViewPager is found.
            layout_viewpager = (ViewPager) layout_principal.findViewById(R.id.activity_tour_vp);

            layout_viewpager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

                /**
                 * Returns the ONLY instance of the fragment corresponding to the requested
                 * position. Makes sure to comply with the usage of the same Fragments outside the
                 * PageViewer (in landscape mode).
                 * @param position Requested position.
                 * @return The ONLY instance of the requested fragment.
                 */
                @Override
                public Fragment getItem(int position) {
                    switch (position) {
                        case 0:
                            return getAndReleaseFragment_game();
                        case 1:
                            return getAndReleaseFragment_sumup();
                        case 2:
                            return getAndReleaseFragment_hist();
                    }
                    return null;
                }

                @Override
                public int getCount() {
                    return 3;
                }

                /**
                 * Overrides the original method: modifies the fragment tagging in order to match
                 * the one used in other methods in {@link Activity_gameturn}.
                 */
                @Override
                public Object instantiateItem(ViewGroup container, int position) {

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                    String tag = "";
                    switch (position) {
                        case 0:
                            tag = TAG_FRAGMENT_GAME;
                            break;
                        case 1:
                            tag = TAG_FRAGMENT_SUMUP;
                            break;
                        case 2:
                            tag = TAG_FRAGMENT_HIST;
                            break;
                    }

                    Fragment fragment = getItem(position);
                    transaction.add(container.getId(), fragment, tag).commit();

                    return fragment;
                }

            });

            layout_viewpager.setOffscreenPageLimit(2);

            // Tabs setup
            tabLayout = (TabLayout) layout_principal.findViewById(R.id.activity_gameturn_sliding_tabs);

            // Adding tabs and tabs names.
            tabLayout.post(new Runnable() {
                @Override
                public void run() {
                    tabLayout.addTab(tabLayout.newTab().setText(tabNames[0]));
                    tabLayout.addTab(tabLayout.newTab().setText(tabNames[1]));
                    tabLayout.addTab(tabLayout.newTab().setText(tabNames[2]));
                    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                }
            });

            // Select right tab before adding listeners
            tabLayout.post(new Runnable() {
                @Override
                public void run() {
                    if (savedInstanceState != null) {
                        int current_frame_id_restored = savedInstanceState.getInt(KEY_CURRENT_FRAME);
                        layout_viewpager.setCurrentItem(current_frame_id_restored);
                        tabLayout.getTabAt(current_frame_id_restored).select();
                    }
                }
            });

            // Setting up ViewPager listener
            tabLayout.post(new Runnable() {
                @Override
                public void run() {
                    layout_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        }

                        /**
                         * Makes the tabs switch to requested position.
                         * @param position Requested position.
                         */
                        @Override
                        public void onPageSelected(int position) {
                            tabLayout.getTabAt(position).select();

                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {
                        }
                    });
                }
            });

            // Setting up tab listener
            tabLayout.post(new Runnable() {
                @Override
                public void run() {
                    tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

                        /**
                         * Makes the ViewPager switch to the tab's position.
                         * @param tab Requested tab.
                         */
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            selectItem(tab.getPosition());
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {
                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {
                        }
                    });
                }
            });

            is_ViewPager = true;


        } catch (ClassCastException | NullPointerException e) {

            // If exception caught, build landscape layout.

            final View layout_principal = RelativeLayout.inflate(this, R.layout.activity_gameturn, null);
            setContentView(layout_principal);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.activity_tour_land_frag_sumup, getAndReleaseFragment_sumup(), TAG_FRAGMENT_SUMUP);
            transaction.replace(R.id.activity_tour_land_frag_hist, getAndReleaseFragment_hist(), TAG_FRAGMENT_HIST);
            transaction.replace(R.id.activity_tour_fragment, getAndReleaseFragment_game(), TAG_FRAGMENT_GAME);

            transaction.commit();

            // The landscape layout implements a toolbar (contrary to the ViewPager one).
            // Toolbar
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);


            is_ViewPager = false;
        }

        update_all();

    }

    /**
     * Gets the game tab's name.
     *
     * @return Game tab's name.
     */
    protected abstract String getTabName();


    private Fragment_gameturn getAndReleaseFragment_game() {
        if (fragment_game != null) {
            return fragment_game;
        } else {
            if (getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_GAME) != null) {
                fragment_game = (Fragment_gameturn) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_GAME);
                getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_GAME)).commit();
                getSupportFragmentManager().executePendingTransactions();
            } else {
                fragment_game = new_Fragment_game_instance();
            }
        }
        return fragment_game;
    }

    private Fragment_game_sumup getAndReleaseFragment_sumup() {
        if (fragment_sumup != null) {
            return fragment_sumup;
        } else {
            if (getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_SUMUP) != null) {
                fragment_sumup = (Fragment_game_sumup) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_SUMUP);
                getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_SUMUP)).commit();
                getSupportFragmentManager().executePendingTransactions();
            } else {
                fragment_sumup = new Fragment_game_sumup();
            }
        }
        return fragment_sumup;
    }

    private Fragment_game_hist getAndReleaseFragment_hist() {
        if (fragment_game_hist != null) {
            return fragment_game_hist;
        } else {
            if (getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_HIST) != null) {
                fragment_game_hist = (Fragment_game_hist) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_HIST);
                getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_HIST)).commit();
                getSupportFragmentManager().executePendingTransactions();
            } else {
                fragment_game_hist = new Fragment_game_hist();
            }
        }
        return fragment_game_hist;
    }

    /**
     * Given the current Activity's class, returns corresponding game fragment.
     *
     * @return Game fragment.
     */
    protected abstract Fragment_gameturn new_Fragment_game_instance();

    /**
     * Initializes game settings on turn start (eg. turn count).
     */
    protected abstract void begin_gameturn();

    /**
     * If ViewPager, selects requested frame and notify change.
     *
     * @param position Requested frame index.
     */
    protected void selectItem(int position) {
        if (position == 0) {
            layout_viewpager.setCurrentItem(0);
        } else if (position == 1) {
            layout_viewpager.setCurrentItem(1);
        } else if (position == 2) {
            layout_viewpager.setCurrentItem(2);
        }
    }

    /**
     * Writes endgame history
     */
    protected void write_endgame_history() {

        if (!endgame_written && somebody_win()) {
            String resume_fin_tour = "";
            if (mutants_win()) {
                resume_fin_tour += "Les mutants gagnent !";
            } else {
                resume_fin_tour += "Les humains gagnent !";
            }
            gameSingleton.getCurrent_game().addHist_jeu("*********************\n" + resume_fin_tour + "\n\n");
            update_all();
            endgame_written = true;
        }
    }

    /**
     * Updates fragment data.
     * Called in case an event in the activity modifies the fragments' data.
     */
    public void update_all() {
        if (fragment_game != null && fragment_game.isAdded()) {
            fragment_game.update();
        }
        if (fragment_sumup != null && fragment_sumup.isAdded()) {
            fragment_sumup.update_sumup();
        }
        if (fragment_game_hist != null && fragment_game_hist.isAdded()) {
            fragment_game_hist.update_hist();
        }
    }

    /**
     * If ViewPager, selects game frame. Else, request confirmation then exits.
     */
    @Override
    public void onBackPressed() {
        if (is_ViewPager && !fragment_game.isAdded()) {
            selectItem(0);
        } else {
            new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setMessage(R.string.warning_quitter)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent nextActivity = new Intent(Activity_gameturn.this, Activity_welcome.class);
                            startActivity(nextActivity);
                            Activity_gameturn.this.finish();
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (is_ViewPager) {
            outState.putInt(KEY_CURRENT_FRAME, layout_viewpager.getCurrentItem());
        }
        outState.putBoolean(KEY_DIALOG_SHOWN, has_begun);
        outState.putBoolean(KEY_FIN_JEU_INSCRIT, endgame_written);
    }

    /**
     * This method checks if humans or mutants have won, and switches to the endgame fragment if
     * it is the case.
     *
     * @return True if someone wins, else false.
     */
    public boolean check_victory_conditions() {
        boolean result = somebody_win();
        if (result) {
            write_endgame_history();
        } else {
            endgame_written = false;
        }
        update_all();
        return result;
    }

    /**
     * Checks if somebody win.
     *
     * @return True if win, else false.
     */
    public boolean somebody_win() {
        return mutants_win() || astronauts_win();
    }

    /**
     * Checks if the astronauts win.
     *
     * @return True if the astronauts win, else false.
     */
    public boolean astronauts_win() {
        for (Character p : gameSingleton.getCurrent_game().getCharacters()) {
            if (p.isContamine() && !p.isMort()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the mutants win.
     *
     * @return True if the mutants win, else false.
     */
    public boolean mutants_win() {
        // Handle the case in which there is only one non mutant character: mutants win
        int compteurNonContamine = 0;
        for (Character p : gameSingleton.getCurrent_game().getCharacters()) {
            if (!p.isContamine()) {
                compteurNonContamine++;
            }
        }
        if (compteurNonContamine < 2) {
            return true;
        }

        // Handle the case in which all doctors are dead or mutants
        for (Character p : gameSingleton.getCurrent_game().getCharacters()) {
            if (p.getRole() == gameSingleton.MEDECIN && !(p.isMort() || p.isContamine())) {
                return false;
            }
        }
        return true;
    }

}
