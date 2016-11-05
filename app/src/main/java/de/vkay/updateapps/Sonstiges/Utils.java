package de.vkay.updateapps.Sonstiges;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.vkay.updateapps.R;

public class Utils {

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

    public static String splitGetFirst (String seperator, String string) {
        return string.split(seperator, 2)[0];
    }

    public static String splitGetSecond (String seperator, String string) {
        return string.split(seperator, 2)[1];
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm =  (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static void openInChromeCustomTab(Context context, String url) {
        Uri link;
        if (!url.contains("http://") && !url.contains("https://")) {
            link = Uri.parse("http://" + url);
        } else {
            link = Uri.parse(url);
        }

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder()
                .setToolbarColor(ContextCompat.getColor(context, R.color.userRegisterBg))
                .setShowTitle(true)
                .setStartAnimations(context, R.anim.slide_in_right, R.anim.slide_out_left)
                .setExitAnimations(context, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .setCloseButtonIcon(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.ic_arrow_left));

        if (chromeInstalled(context)) {
            builder.build().intent.setPackage("com.android.chrome");
        }

        builder.build().launchUrl((Activity) context, link);
    }

    public static boolean chromeInstalled(Context context) {
        try {
            context.getPackageManager().getPackageInfo("com.android.chrome", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static long dirSize(File dir) {

        if (dir.exists()) {
            long result = 0;
            File[] fileList = dir.listFiles();
            for(int i = 0; i < fileList.length; i++) {
                // Recursive call if it's a directory
                if(fileList[i].isDirectory()) {
                    result += dirSize(fileList [i]);
                } else {
                    // Sum the file size in bytes
                    result += fileList[i].length();
                }
            }
            return result;
        }
        return 0;
    }
}
