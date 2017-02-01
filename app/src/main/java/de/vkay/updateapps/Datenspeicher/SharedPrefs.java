package de.vkay.updateapps.Datenspeicher;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

public class SharedPrefs {

    private Context context;
    private android.content.SharedPreferences shared;
    private android.content.SharedPreferences.Editor editor;
    private PackageInfo pInfo;

    public SharedPrefs(Context context){
        this.context = context;
        shared = PreferenceManager.getDefaultSharedPreferences(context);
        editor = shared.edit();
        pInfo = null;
    }

    /**
     * Methoden für Installation
     */

    public void setWelcome() {
        editor.putBoolean("welcome", true);
        editor.apply();
    }

    public boolean getWelcome() {
        return shared.getBoolean("welcome", false);
    }

    public String getInstalledAppVersion(String paketname) {
        try {
            pInfo = context.getPackageManager().getPackageInfo(paketname, 0);
            return pInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isTestBuild(String paketname) {
        if (getInstalledAppVersion(paketname) != null) {
            if (getInstalledAppVersion(paketname).contains("test-build")) {
                return true;
            }
        }
        return false;
    }

    public boolean appExistsInPreferences(String paketname) {
        return shared.contains(paketname);
    }

    public void saveApp(String paketname) {
        editor.putBoolean(paketname, true);
        editor.apply();
    }

    public void removeApp(String paketname) {
        editor.putBoolean(paketname, false);
        editor.apply();
    }

    public boolean getAppStatus(String paketname) {
        return shared.getBoolean(paketname, false);
    }

    public void setUsername(String s){
        editor.putString("username", s);
        editor.apply();
    }

    public String getUsername(){
        return shared.getString("username", "-");
    }

    public void setLoggedInStatus(boolean b) {
        editor.putBoolean("logged", b);
        editor.apply();
    }

    public boolean getLoggedInStatus() {
        return shared.getBoolean("logged", false);
    }

    public boolean getWifiDownloadStatus() {
        return shared.getBoolean("wifi_download", true);
    }

    public boolean getAutoInstallStatus() {
        return shared.getBoolean("autostart_install", true);
    }

    public void setImageSetVersion(String version) {
        editor.putString("imageSet", version);
        editor.apply();
    }

    public String getImageSetVersion() {
        return shared.getString("imageSet", "");
    }
}
