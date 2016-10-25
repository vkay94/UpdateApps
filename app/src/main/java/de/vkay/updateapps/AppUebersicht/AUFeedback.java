package de.vkay.updateapps.AppUebersicht;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import de.vkay.updateapps.Datenspeicher.SharedPrefs;
import de.vkay.updateapps.R;
import de.vkay.updateapps.Sonstiges.Const;
import de.vkay.updateapps.Sonstiges.Snacks;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class AUFeedback extends AppCompatActivity {

    Bundle bund;
    FloatingActionButton fabSend;
    SharedPrefs shared;

    RecyclerView recyclerView;
    RAdapterFeedback rAF;
    ArrayList<FeedbackDatatype> array;

    public static boolean fabVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aufeedback);

        bund = getIntent().getExtras();
        shared = new SharedPrefs(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        actionBar.setTitle(bund.getString(Const.NAME));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        fabSend = (FloatingActionButton) findViewById(R.id.au_fab_sendFeedback);
        fabSend.hide();

        if (shared.getLoggedInStatus()) {
            fabSend.show();
            fabSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), FeedbackActivity.class).putExtras(bund));
                }
            });
            fabVisible = true;
        }

        recyclerView = (RecyclerView) findViewById(R.id.au_recycler_feedback);
        array = new ArrayList<>();
        rAF = new RAdapterFeedback(array, this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(rAF);

        loadFeedback();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stay, R.anim.slide_from_top);
    }

    public void loadFeedback() {
        final String jsonArrayLink = Const.BASE_PHP + Const.GETFEED + "?paketname=" + bund.getString(Const.PAKETNAME);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(jsonArrayLink)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    Snacks.toastInBackground(getApplicationContext(), "Fehler beim Laden von Feedback", Toast.LENGTH_SHORT);
                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    try {
                        String jsonData = response.body().string();
                        JSONArray Jarray = new JSONObject(jsonData).getJSONArray("updateapps");

                        for (int i = 0; i < Jarray.length(); i++){
                            JSONObject obj = Jarray.getJSONObject(i);

                            final String paketname = obj.getString(Const.FEED_PAKETNAME);
                            final String date = obj.getString(Const.FEED_TIME);
                            final String message = obj.getString(Const.FEED_TEXT);
                            final String art = obj.getString(Const.FEED_BETREFF);
                            final String autor = obj.getString(Const.FEED_AUTOR);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rAF.newInsert(new FeedbackDatatype(paketname, autor, message, art, date), 0);
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
    }
}
