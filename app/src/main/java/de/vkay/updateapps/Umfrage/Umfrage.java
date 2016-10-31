package de.vkay.updateapps.Umfrage;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Datentyp Umfrage
 */
public class Umfrage {

    public static Type arrayType = new TypeToken<ArrayList<Umfrage>>(){}.getType();

    public static final String TYPE_SINGLE = "single";          // RadioButtons
    public static final String TYPE_MULTIPLE = "multiple";      // CheckBoxes

    int id;
    String title;
    String description;
    String type;

    ArrayList<Optionen> optionen;

    public Umfrage() {
        // Konstruktor
    }

    /**
     * Konstruktor, um eine Umfrage hinzu zu fügen. Für evtl. Option der des Vorschlags
     * @param id
     * @param title
     * @param description
     */
    public Umfrage (int id, String title, String description) {
        optionen = new ArrayList<>();

        this.id = id;
        this.title = title;
        this.description = description;
    }

    /**
     * Fügt eine Option der Umfrage hinzu
     * @param option
     */
    public void addOption (Optionen option) {
        optionen.add(option);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Optionen getOption(int id) {
        Optionen option = new Optionen();

        for (Optionen o : optionen) {
            if (o.getId() == id) {
                option = o;
            }
        }

        return option;
    }

    public String getType() {return type;}

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Klasse der Option
     */
    public static class Optionen{
        String name;
        int amount;
        int id;

        public Optionen() {}

        public Optionen (int id, String name, int amount) {
            this.id = id;
            this.name = name;
            this.amount = amount;
        }

        public int getId() {
            return id;
        }

        public void addOne() {
            this.amount = amount + 1;
        }

        public int getAmount() {
            return amount;
        }

        public String getName() {
            return name;
        }
    }
}
