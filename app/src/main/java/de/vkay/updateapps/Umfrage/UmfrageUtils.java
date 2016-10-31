package de.vkay.updateapps.Umfrage;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.vkay.updateapps.Sonstiges.Const;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Klasse, um Umfragen zu ermöglichen
 */
public class UmfrageUtils {

    public List<Umfrage> list;
    Gson gson;

    /**
     * Konstruktor
     */
    public UmfrageUtils() {
        list = new ArrayList<>();
        gson = new Gson();
    }

    /**
     * Fügt eine Umfrage der Liste hinzu
     * @param umfrage Umfrage, die hinzugefügt werden soll
     */
    public void addUmfrage(Umfrage umfrage) {
        list.add(umfrage);
    }

    /**
     * Lädt die Umfragen vom Server und speichert diese in einer Liste
     */
    public void laden() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Const.BASE_PHP + Const.GETUMFRAGEN)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();

                try {
                    JSONArray Jarray = new JSONArray(data).getJSONArray(0);

                    for (int i = 0; i < Jarray.length(); i++){
                        JSONObject obj = Jarray.getJSONObject(i);
                        Umfrage listUm = gson.fromJson(obj.getString("umfrage"), Umfrage.class);

                        addUmfrage(listUm);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Aktualliert eine Umfrage an der Stelle id
     * @param id id der Umfrage
     */
    public void aktualisieren(int id) {
        Umfrage umfrage = getUmfrage(id);
        String jsonUmfrage = gson.toJson(umfrage);

        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .build();

        Request request = new Request.Builder()
                .url(Const.BASE_PHP + Const.GETUMFRAGEN + "?send=" + jsonUmfrage + "&id=" + id)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
            }
        });
    }

    /**
     * Sucht eine passende Umfrage anhand der id
     * @param id id der zu suchenden Umfrage
     * @return Passende Umfrage
     */
    public Umfrage getUmfrage(int id) {
        Umfrage tmp = new Umfrage();

        for (Umfrage um : list) {
            if (um.getId() == id) {
                tmp = um;
            }
        }

        return tmp;
    }
}
