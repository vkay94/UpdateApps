package de.vkay.updateapps.Sonstiges;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TextInputLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Sonst {

    public static String getTimeDifference(String timeString) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date date;

        long diffSeconds, diffMinutes, diffHours, diffDays;

        try {
            date = format.parse(timeString);

            long diff = new Date().getTime() - date.getTime();

            diffSeconds = diff / 1000 % 60;
            diffMinutes = diff / (60 * 1000) % 60;
            diffHours = diff / (60 * 60 * 1000) % 24;
            diffDays = diff / (24 * 60 * 60 * 1000);

            if (diffDays > 0){
                if (diffDays > 31) {
                    return "vor mehr als 1 Monat";
                } else if (diffDays == 1){
                    return "vor " + String.valueOf(diffDays) + " Tag";
                } else {
                    return "vor " + String.valueOf(diffDays) + " Tagen";
                }
            } else if (diffHours > 0) {
                if (diffHours == 1) {
                    return "vor " + String.valueOf(diffHours) + " Stunde"; }
                else
                return "vor " + String.valueOf(diffHours) + " Stunden";
            } else if (diffMinutes > 0) {
                if (diffMinutes == 1) {
                    return "vor " + String.valueOf(diffMinutes) + " Minute";
                }
                return "vor " + String.valueOf(diffMinutes) + " Minuten";
            } else {
                return "vor " + String.valueOf(diffSeconds) + " Sekunden";
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return String.valueOf(-1);
    }

    public static String getCurrentTimeStamp() {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date().getTime());
    }

    // User Apps

    public static ArrayList<String> getWords(String string) {
        String[] list = string.split("\\s+");
        ArrayList<String> array = new ArrayList<>();

        Collections.addAll(array, list);

        return array;
    }

    public static String appendNewApp(String old, String string) {
        return old + " " + string;
    }

    public static String removeApp(String apps, String zuEntfernen) {
        ArrayList<String> list = getWords(apps);
        if (list.contains(zuEntfernen)) {
            list.remove(list.indexOf(zuEntfernen));
        }
        String s = "";
        for (int i = 0; i < list.size(); i++) {
            s += list.get(i) + " ";
        }
        return s;
    }

    public static String splitGetFirst (String seperator, String string) {
        return string.split(seperator, 2)[0];
    }

    public static String splitGetSecond (String seperator, String string) {
        return string.split(seperator, 2)[1];
    }

    // Ändert die ErrorFarbe von TextInputLayout
    public static void setErrorTextColor(TextInputLayout textInputLayout, int color) {
        try {
            Field fErrorView = TextInputLayout.class.getDeclaredField("mErrorView");
            fErrorView.setAccessible(true);
            TextView mErrorView = (TextView) fErrorView.get(textInputLayout);
            Field fCurTextColor = TextView.class.getDeclaredField("mCurTextColor");
            fCurTextColor.setAccessible(true);
            fCurTextColor.set(mErrorView, color);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm =  (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
    }

}
