package com.minastelien.quentin.gestionnairedesporz.Utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Provides all date support.
 */
public class Dates {

    // Define date format
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss");

    /**
     * Returns formatted current date.
     *
     * @return Formatted current date as String.
     */
    public static String date() {
        String date = dateFormat.format(new Date());
        return date;
    }

    /**
     * Returns formatted date from provided Unix time.
     *
     * @param unixSeconds Unix time to be converted.
     * @return Formatted date as String.
     */
    public static String date_from_unix_time(long unixSeconds) {
        String date = dateFormat.format(new Date(unixSeconds * 1000L));
        return date;
    }
}