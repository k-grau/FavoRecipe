package com.karlgrauers.favorecipe.utils;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public abstract class Utils {

    /*
     * Abstrakt klass med nyttometoder som används
     * frekvent och av flera klasser.
     */


    /**
     * Formaterar datum enligt format "yyyy-MM-dd HH:mm".
     * @param date innehåller det datumobjekt som ska formateras.
     */
    public static String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm", Locale.GERMAN);
        return formatter.format(date);
    }

    /**
     * Skapar sträng .
     * @param yearOffset innehåller integer som anger vilket år datumet
     *                   ska gälla för (ex. -1 för förra året, +1 för ett år fram i tiden).
     */
    public static String getUTCDateString(int yearOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, yearOffset);
        Date date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.GERMAN);
        return String.format("%s%s", formatter.format(date).replace(" ", "T"), "Z");
    }


    /**
     * Kontrollerar om app är installerad och aktiv på användares
     * telefon.
     * @param packageName innehåller namna på paket(app) som ska kontrolleras
     * @param context innehåller applikationskontext.
     * @return boolean 'isActive' - sätts till 'true' om app är installerad
     * och aktiv. Sätts till 'false' om app ej är installerad eller inaktiv.
     */
    public static boolean isAppActive(Context context, String packageName) {
        boolean isActive;

        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(packageName, 0);
            isActive = ai.enabled;
        }
        catch (PackageManager.NameNotFoundException e) {
            isActive = false;
        }
        return isActive;
    }
}
