package com.karlgrauers.favorecipe.utils;

import android.text.TextUtils;
import androidx.room.ProvidedTypeConverter;
import androidx.room.TypeConverter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/*
 * Abstrakt klass med nyttometoder för konvertering av datatyper
 * Används i flera olika klasser. Är också typ-konverterare
 * av Room-databasens interface som ej klarar att hantera lagring
 * av komplexa typer som listor och datumobjekt.
 */


@ProvidedTypeConverter
public abstract class Converters {

    /**
     * Gör första bokstav i sträng versal.
     * @param str den sträng som ska ha versal.
     */
    public static String firstToUpperCase(String str) {
        return str.substring(0,1).toUpperCase() + str.substring(1).toLowerCase();
    }

    /**
     * Overloaded metod som konverterar lista till sträng.
     * @param list innehåller listan som ska konverteras.
     * @param delimiter det tecken som ska avskilja orden
     *                  i strängen, exempelvis ','
     * @param charsToDelete antal tecken att radera från slutet av strängen.
     */
    public static String listToString(List<String> list, String delimiter, int charsToDelete) {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s).append(delimiter);
        }
        return charsToDelete <= 0 ? sb.toString() : sb.deleteCharAt(sb.length() - charsToDelete).toString();
    }

    /**
     * Overloaded metod som konverterar lista till sträng.
     * Används som typ-konverterare av Room-databasen.
     * @param list innehåller den lista som ska konverteras.
     */
    @TypeConverter
    public static String listToString(List<String> list) {
        return list == null ? null : TextUtils.join(",", list);
    }

    /**
     * Konverterar sträng till lista.
     * Används som typ-konverterare av Room-databasen.
     * @param str innehåller den sträng som ska konverteras.
     */
    @TypeConverter
    public static List<String> stringToList(String str) {
        return str == null ? null : Arrays.asList(str.split(",(?! )"));
    }

    /**
     * Konverterar typ long till datumonbjekt.
     * Används som typ-konverterare av Room-databasen.
     * @param dateLong innehåller de siffror som ska konverteras till datum.
     */
    @TypeConverter
    public static Date longToDate(Long dateLong) {
        return dateLong == null ? null: new Date(dateLong);
    }

    /**
     * Konverterar datumonbjekt till long.
     * Används som typ-konverterare av Room-databasen.
     * @param date innehåller det datum som ska konverteras till siffror.
     */
    @TypeConverter
    public static Long dateToLong(Date date){
        return date == null ? null : date.getTime();
    }

}
