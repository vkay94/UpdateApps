package de.vkay.updateapps.User;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
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

import de.vkay.updateapps.AppUebersicht.Feedback.FeedbackDatatype;
import de.vkay.updateapps.Datenspeicher.SharedPrefs;
import de.vkay.updateapps.R;
import de.vkay.updateapps.Sonstiges.Const;
import de.vkay.updateapps.Sonstiges.Snacks;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class UserFeedback extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<FeedbackDatatype> array;
    LinearLayoutManager llm;
    RAdapterUserFeedback rvAUF;

    SharedPrefs shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.enter_activty_backwards, R.anim.exit_activity_backwards);
            }
        });

        shared = new SharedPrefs(this);

        recyclerView = (RecyclerView) findViewById(R.id.rec_user_feedback);
        recyclerView.setNestedScrollingEnabled(false);

        array = new ArrayList<>();
        rvAUF = new RAdapterUserFeedback(array, this);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(750);
        //itemAnimator.setRemoveDuration(1000);
        recyclerView.setItemAnimator(itemAnimator);

        llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(rvAUF);

        getRevsWithOkHttp2();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_activty_backwards, R.anim.exit_activity_backwards);
    }

    public void getRevsWithOkHttp2() {
        String jsonArrayLink = Const.BASE_PHP + Const.GETFEED + "?autor=" + shared.getUsername();

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
                                rvAUF.newInsert(new FeedbackDatatype(paketname, autor, message, art, date), 0);
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
