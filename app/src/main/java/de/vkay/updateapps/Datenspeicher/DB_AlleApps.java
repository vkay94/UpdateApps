package de.vkay.updateapps.Datenspeicher;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import de.vkay.updateapps.AlleApps.AlleAppsDatatype;
import de.vkay.updateapps.Sonstiges.Const;


public class DB_AlleApps extends SQLiteOpenHelper {

    public DB_AlleApps(Context context) {
        super(context, "AlleApps.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ALLEAPPS(" +
                Const.NAME + " TEXT, " +
                Const.PAKETNAME + " TEXT, " +
                Const.VERSION + " TEXT, " +
                Const.DATE + " TEXT, " +
                Const.BESCHREIBUNG + " TEXT, " +
                Const.CHANGELOG + "  TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS ALLEAPPS;");
        onCreate(db);
    }

    // ********** CRUD ***********************************************************

    public void insertApp(String name, String paketname, String version, String date, String beschreibung, String changelog){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(Const.NAME, name);
        cValues.put(Const.PAKETNAME, paketname);
        cValues.put(Const.VERSION, version);
        cValues.put(Const.DATE, date);
        cValues.put(Const.BESCHREIBUNG, beschreibung);
        cValues.put(Const.CHANGELOG, changelog);

        db.insert("ALLEAPPS", null, cValues);
    }

    public void removeApp(String paketname){
        this.getWritableDatabase().delete("ALLEAPPS", Const.PAKETNAME + " = '" + paketname + "'", null);
    }

    public boolean existsVersion(String paketname, String version){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c1 = db.rawQuery(
                "SELECT * FROM ALLEAPPS WHERE " + Const.PAKETNAME + " = '" + paketname + "'", null);

        try {
            while (c1.moveToNext()) {
                return c1.getString(1).equals(version);
            }

        } finally {
            if(c1 != null){
                c1.close();
            }
        }

        return false;
    }

    public ArrayList<AlleAppsDatatype> getDatabaseApps(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<AlleAppsDatatype> array = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT * FROM ALLEAPPS", null);

        try {

            while (c.moveToNext()){
                array.add(new AlleAppsDatatype(
                        c.getString(0), // Name
                        c.getString(1), // Paketname
                        c.getString(2), // Version
                        c.getString(3), // Datum
                        c.getString(4), // Beschreibung
                        c.getString(5)  // Version
                ));
            }
            return array;

        } finally {
            if(c != null){
                c.close();
            }
        }
    }

    public ArrayList<AlleAppsDatatype> getDatabaseAppsRandom5(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<AlleAppsDatatype> array = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM ALLEAPPS ORDER BY RANDOM() LIMIT 5", null);

        try {

            while (c.moveToNext()){
                array.add(new AlleAppsDatatype(
                        c.getString(0),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3),
                        c.getString(4),
                        c.getString(5)
                ));
            }
            return array;

        } finally {
            if(c != null){
                c.close();
            }
        }
    }

    public ArrayList<String> getDatabaseAppsPaketname(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> array = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT " + Const.PAKETNAME + " FROM ALLEAPPS", null);

        try {

            while (c.moveToNext()){
                array.add(c.getString(0));
            }
            return array;

        } finally {
            if(c != null){
                c.close();
            }
        }
    }

    public String getAppName(String paketname) {
        SQLiteDatabase db = this.getWritableDatabase();
        String s = "";
        Cursor c = db.rawQuery(
                "SELECT " + Const.NAME + " FROM ALLEAPPS WHERE " + Const.PAKETNAME + " = '" + paketname + "'", null);

        try {

            while (c.moveToNext()){
                s = c.getString(0);
            }

            return s;

        } finally {
            if(c != null){
                c.close();
            }
        }
    }

}
