package de.vkay.updateapps.AlleApps;

public class AlleAppsDatatype {

    String name, paketname, version, date, beschreibung, changelog;

    public AlleAppsDatatype() {
        // Konstruktor
    }

    public AlleAppsDatatype(String name, String paketname, String version, String date, String beschreibung, String changelog){
        this.name = name;
        this.version = version;
        this.date = date;
        this.beschreibung = beschreibung;
        this.paketname = paketname;
        this.changelog = changelog;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPaketname() {
        return paketname;
    }

    public void setPaketname(String paketname) {
        this.paketname = paketname;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }
}
