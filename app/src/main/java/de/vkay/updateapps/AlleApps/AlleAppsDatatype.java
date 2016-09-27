package de.vkay.updateapps.AlleApps;

public class AlleAppsDatatype {

    String name, paketname, version, date, beschreibung, changelog;

    public AlleAppsDatatype(String name, String paketname, String version, String date, String beschreibung, String changelog){
        this.name = name;
        this.version = version;
        this.date = date;
        this.beschreibung = beschreibung;
        this.paketname = paketname;
        this.changelog = changelog;
    }
}
