package de.vkay.updateapps;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import de.vkay.updateapps.AlleApps.AlleApps;
import de.vkay.updateapps.AlleApps.AlleAppsDatatype;
import de.vkay.updateapps.AlleApps.RAdapterAA_Main;
import de.vkay.updateapps.Datenspeicher.DB_AlleApps;
import de.vkay.updateapps.Datenspeicher.SharedPrefs;
import de.vkay.updateapps.Sonstiges.Const;
import de.vkay.updateapps.Sonstiges.Snacks;
import de.vkay.updateapps.User.BenutzerPanel;
import de.vkay.updateapps.User.UserLogin;
import de.vkay.updateapps.Willkommen.WelcomeScreen;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class Main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    CoordinatorLayout coordinatorLayout_main;
    SharedPrefs shared;
    DB_AlleApps db;

    RecyclerView recyclerView;
    RAdapterAA_Main rvAAA;

    int counter = 0;

    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbarNavigationView();
        initialize();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_main);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(llm);

        ArrayList<AlleAppsDatatype> alleAppsArray = db.getDatabaseAppsRandom5();
        rvAAA = new RAdapterAA_Main(alleAppsArray, getApplicationContext());
        recyclerView.setAdapter(rvAAA);
        recyclerView.setHasFixedSize(true);


        Bundle bundNotify = getIntent().getExtras();
        if(bundNotify != null) {
            if (bundNotify.getBoolean("notification_recieved")) {
                getAppsInit();
            }
        }
    }

    public void initialize() {

        coordinatorLayout_main = (CoordinatorLayout) findViewById(R.id.coordinary_main);

        if(!shared.getWelcome()){
            TextView tvStreichen = (TextView) findViewById(R.id.tv_main_streichen);
            tvStreichen.setVisibility(View.VISIBLE);
            getAppsInit();
            shared.setWelcome();
            startActivityForResult(new Intent(Main.this, WelcomeScreen.class), 2);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        db = new DB_AlleApps(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (counter < 3) {
                    getAppsInit();
                    rvAAA.notifyDataSetChanged();
                    counter++;
                } else {
                    Snacks.toastInBackground(getApplicationContext(), "Oft genug aktualisiert", Toast.LENGTH_SHORT);
                }
            }
        });

        Button btnMore = (Button) findViewById(R.id.btn_main_more);
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AlleApps.class));
            }
        });
    }

    public void initToolbarNavigationView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        shared = new SharedPrefs(getApplicationContext());

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (shared.getLoggedInStatus()) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.navmenu_loggedin);
        } else {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.navigationview_menuitems);
        }

        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView headerVersion = (TextView) headerView.findViewById(R.id.HeaderVersion);

        String headerText = shared.getInstalledAppVersion(getResources().getString(R.string.app_package));
        headerVersion.setText(headerText);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_alleApps) {
            startActivity(new Intent(this, AlleApps.class));

        } else if (id == R.id.nav_login) {
            startActivityForResult(new Intent(this, UserLogin.class), 2);

        } else if (id == R.id.nav_cpanel) {
            startActivityForResult(new Intent(this, BenutzerPanel.class), 3);

        } else if (id == R.id.nav_web){
            Intent i = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(Const.WEBSITE));
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2 && shared.getLoggedInStatus()) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.navmenu_loggedin);
        }

        if(requestCode == 3 && !shared.getLoggedInStatus()) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.navigationview_menuitems);
        }
    }

    public void getAppsInit() {
        String jsonArrayLink = Const.BASE_PHP + Const.GETAPPS + "?init";

        OkHttpClient client = new OkHttpClient();
        Request request = new okhttp3.Request.Builder()
                .url(jsonArrayLink)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Snacks.toastInBackground(getApplicationContext(), "Fehler beim Laden", Toast.LENGTH_SHORT);
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                try {
                    String jsonData = response.body().string();
                    JSONArray Jarray = new JSONObject(jsonData).getJSONArray("updateapps");

                    ArrayList<String> appsOnServer = new ArrayList<>();

                    for (int i = 0; i < Jarray.length(); i++) {
                        JSONObject obj = Jarray.getJSONObject(i);

                        String paketname = obj.getString(Const.PAKETNAME);
                        String version = obj.getString(Const.VERSION);

                        appsOnServer.add(paketname);

                        if (!db.existsVersion(paketname, version)) {
                            getApps(paketname);
                        }
                    }

                    ArrayList<String> appsInDatabase = db.getDatabaseAppsPaketname();

                    for (int i = 0; i < appsInDatabase.size(); i++) {
                        if (!appsOnServer.contains(appsInDatabase.get(i))) {
                            db.removeApp(appsInDatabase.get(i));
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<AlleAppsDatatype> array = db.getDatabaseApps();
                            rvAAA.addAll(array);
                            rvAAA.notifyDataSetChanged();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new Snacks().Grey(coordinatorLayout_main, getApplicationContext(), getString(R.string.update_database), Snacks.SHORT);
            }
        });
    }

    public void getApps(final String paketname){
        String jsonArrayLink = Const.BASE_PHP + Const.GETAPPS + "?paketname=" + paketname;

        OkHttpClient client = new OkHttpClient();
        Request request = new okhttp3.Request.Builder()
                .url(jsonArrayLink)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Snacks.toastInBackground(getApplicationContext(), "Fehler in get", Toast.LENGTH_SHORT);
            }
            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                try {
                    String jsonData = response.body().string();
                    JSONObject obj = new JSONObject(jsonData).getJSONArray("updateapps").getJSONObject(0);

                    String name = obj.getString(Const.NAME);
                    String paketname = obj.getString(Const.PAKETNAME);
                    String version = obj.getString(Const.VERSION);
                    String date = obj.getString(Const.DATE);
                    String beschreibung = obj.getString(Const.BESCHREIBUNG);
                    String changelog = obj.getString(Const.CHANGELOG);

                    db.removeApp(paketname);
                    db.insertApp(name, paketname, version, date, beschreibung, changelog);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<AlleAppsDatatype> array = db.getDatabaseApps();
                            rvAAA.addAll(array);
                            rvAAA.notifyDataSetChanged();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
