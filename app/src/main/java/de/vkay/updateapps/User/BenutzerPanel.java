package de.vkay.updateapps.User;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import de.vkay.updateapps.Datenspeicher.SharedPrefs;
import de.vkay.updateapps.R;
import de.vkay.updateapps.Sonstiges.Const;
import de.vkay.updateapps.Sonstiges.Snacks;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BenutzerPanel extends AppCompatActivity implements View.OnClickListener{

    SharedPrefs shared;
    View viewAnpassen, viewUsercard;
    TextInputLayout textInputNewName;
    TextInputEditText editNewName;

    TextView username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benutzer_panel);

        initToolbar();

        shared = new SharedPrefs(this);

        username = (TextView) findViewById(R.id.benutzer_panel_username);
        username.setText(shared.getUsername());

        textInputNewName = (TextInputLayout) findViewById(R.id.benutzer_panel_textinput_newname);
        Button btnCheckName = (Button) findViewById(R.id.benutzer_panel_btn_pruefname);
        btnCheckName.setOnClickListener(this);

        editNewName = (TextInputEditText) findViewById(R.id.benutzer_panel_textedit_newname);
        editNewName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                textInputNewName.setErrorEnabled(false);
            }
        });
        editNewName.setText(shared.getUsername());

        Button btnCloseEdit = (Button) findViewById(R.id.benutzer_panel_btn_closeedit);
        btnCloseEdit.setOnClickListener(this);

        TextView tvChange = (TextView) findViewById(R.id.benutzer_panel_changetext);
        tvChange.setOnClickListener(this);

        viewAnpassen = findViewById(R.id.benutzer_panel_view_anpassen);
        viewUsercard = findViewById(R.id.benutzer_panel_view_usercard);
        viewAnpassen.setVisibility(View.GONE);

        Button btnLogout = (Button) findViewById(R.id.benutzer_panel_btn_logout);
        btnLogout.setOnClickListener(this);

        LinearLayout llMeineBeitraege = (LinearLayout) findViewById(R.id.benutzer_panel_ll_meine_beitraege);
        llMeineBeitraege.setOnClickListener(this);
        LinearLayout llEinstellungen = (LinearLayout) findViewById(R.id.benutzer_panel_ll_einstellungen);
        llEinstellungen.setOnClickListener(this);
    }

    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(R.string.title_benutzer);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.benutzer_panel_btn_logout:
                shared.setLoggedInStatus(false);
                setResult(3);
                finish();
                break;

            case R.id.benutzer_panel_ll_meine_beitraege:
                startActivity(new Intent(this, UserFeedback.class));
                overridePendingTransition(R.anim.enter_activity, R.anim.exit_activity);
                break;

            case R.id.benutzer_panel_ll_einstellungen:
                startActivity(new Intent(this, Einstellungen.class));
                overridePendingTransition(R.anim.enter_activity, R.anim.exit_activity);
                break;

            case R.id.benutzer_panel_btn_closeedit:
                viewUsercard.setVisibility(View.VISIBLE);
                viewAnpassen.setVisibility(View.GONE);
                break;

            case R.id.benutzer_panel_changetext:
                viewAnpassen.setVisibility(View.VISIBLE);
                viewUsercard.setVisibility(View.GONE);
                break;

            case R.id.benutzer_panel_btn_pruefname:
                okhhtpNewName(editNewName);
        }

    }

    public void okhhtpNewName(TextInputEditText editNewName) {
        final String stringNewName = editNewName.getText().toString();
        String url = Const.BASE_PHP + Const.CHECKNEWNAME
                + "?currentName=" + shared.getUsername()
                + "&newName=" + stringNewName;

        System.out.println(url);

        OkHttpClient client = new OkHttpClient();

        Request request = new okhttp3.Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Snacks.toastInBackground(getApplicationContext(), getString(R.string.error), Toast.LENGTH_SHORT);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString = response.body().string();

                if (responseString.contains("Erfolg")) {
                    shared.setUsername(stringNewName);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            username.setText(stringNewName);
                        }
                    });
                    Snacks.toastInBackground(getApplicationContext(), getString(R.string.success), Toast.LENGTH_SHORT);
                } else if (responseString.contains("Fehler")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textInputNewName.setError(getString(R.string.user_panel_name_vergeben));
                        }
                    });
                }
            }
        });
    }
}
