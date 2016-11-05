package de.vkay.updateapps;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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
import de.vkay.updateapps.AppUebersicht.AppUebersicht;
import de.vkay.updateapps.Datenspeicher.DB_AlleApps;
import de.vkay.updateapps.Datenspeicher.SharedPrefs;
import de.vkay.updateapps.Sonstiges.Const;
import de.vkay.updateapps.Sonstiges.Snacks;
import de.vkay.updateapps.Sonstiges.Utils;
import de.vkay.updateapps.User.BenutzerPanel;
import de.vkay.updateapps.User.UserLogin;
import de.vkay.updateapps.Willkommen.WelcomeScreen;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    CoordinatorLayout coordinatorLayout_main;
    SharedPrefs shared;
    DB_AlleApps db;

    RecyclerView recyclerView;
    RAdapterAA_Main rvAAA;

    int counter = 0;

    NavigationView navTop, navBottom;
    View viewMoreAppCard;
    boolean isCardShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbarNavigationView();
        initialize();

        viewMoreAppCard = findViewById(R.id.main_more_apps_card);
        viewMoreAppCard.setVisibility(View.GONE);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_main);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(llm);

        ArrayList<AlleAppsDatatype> alleAppsArray = db.getDatabaseAppsRandom5();

        if (!alleAppsArray.isEmpty()) {
            viewMoreAppCard.setVisibility(View.VISIBLE);
            isCardShown = true;
        }

        rvAAA = new RAdapterAA_Main(alleAppsArray, getApplicationContext(), this);
        recyclerView.setAdapter(rvAAA);
        recyclerView.setHasFixedSize(true);


        Bundle bundNotify = getIntent().getExtras();
        if(bundNotify != null) {
            if (bundNotify.getBoolean("notification_recieved")) {
                getAppsInit();
            }
        }

        getAppsInit();
        checkImageSetVersion();
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
        db = new DB_AlleApps(this);

        navTop = (NavigationView) findViewById(R.id.main_nav_view_top);
        navBottom = (NavigationView) findViewById(R.id.main_nav_view_bottom);

        if (shared.getLoggedInStatus()) {
            navTop.getMenu().clear();
            navTop.inflateMenu(R.menu.navmenu_loggedin);
        } else {
            navTop.getMenu().clear();
            navTop.inflateMenu(R.menu.navigationview_menuitems);
        }

        navTop.setNavigationItemSelectedListener(this);
        navBottom.setNavigationItemSelectedListener(this);

        checkNavBottomUpdateText();

        View headerView = navTop.getHeaderView(0);
        TextView headerVersion = (TextView) headerView.findViewById(R.id.HeaderVersion);

        String headerText = shared.getInstalledAppVersion(getResources().getString(R.string.app_package));
        headerVersion.setText(headerText);

    }

    public void checkNavBottomUpdateText() {
        if (navBottom != null
                && !db.getSpecificApp(getString(R.string.app_package))
                .getVersion().equals(shared.getInstalledAppVersion(getApplicationContext().getString(R.string.app_package)))) {

            navBottom.getMenu().findItem(R.id.nav_update).setTitle(R.string.nav_update_when_available);
            navBottom.setItemTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.expVersion)));
        }
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

        switch (id) {

            case R.id.nav_alleApps:
                startActivity(new Intent(this, AlleApps.class));
                break;

            case R.id.nav_login:
                startActivityForResult(new Intent(this, UserLogin.class), 2);
                break;

            case R.id.nav_cpanel:
                startActivityForResult(new Intent(this, BenutzerPanel.class), 3);
                break;

            case R.id.nav_web:
                Utils.openInChromeCustomTab(this, Const.WEBSITE);
                break;

            case R.id.nav_update:
                AlleAppsDatatype data = db.getSpecificApp(getString(R.string.app_package));
                Intent intent = new Intent(this, AppUebersicht.class);

                intent.putExtra(Const.PAKETNAME, data.getPaketname());
                intent.putExtra(Const.NAME, data.getName());
                intent.putExtra(Const.VERSION, data.getVersion());
                intent.putExtra(Const.DATE, data.getDate());
                intent.putExtra(Const.BESCHREIBUNG, data.getBeschreibung());
                intent.putExtra(Const.CHANGELOG, data.getChangelog());

                startActivity(intent);

                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2 && shared.getLoggedInStatus()) {
            navTop.getMenu().clear();
            navTop.inflateMenu(R.menu.navmenu_loggedin);
        }

        if(requestCode == 3 && !shared.getLoggedInStatus()) {
            navTop.getMenu().clear();
            navTop.inflateMenu(R.menu.navigationview_menuitems);
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

                            if (!array.isEmpty() && !isCardShown) {
                                viewMoreAppCard.setVisibility(View.VISIBLE);
                                isCardShown = true;
                            }

                            rvAAA.addAll(array);
                            rvAAA.notifyDataSetChanged();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Snacks.ShowSnack(getApplicationContext(), coordinatorLayout_main,
                        getString(R.string.update_database), Snackbar.LENGTH_SHORT,
                        R.color.greyStatus, 0)
                        .show();
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
                    final String paketname = obj.getString(Const.PAKETNAME);
                    String version = obj.getString(Const.VERSION);
                    String date = obj.getString(Const.DATE);
                    String beschreibung = obj.getString(Const.BESCHREIBUNG);
                    String changelog = obj.getString(Const.CHANGELOG);

                    db.removeApp(paketname);
                    db.insertApp(name, paketname, version, date, beschreibung, changelog);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (paketname.equals(getString(R.string.app_package))) {
                                checkNavBottomUpdateText();
                            }

                            ArrayList<AlleAppsDatatype> array = db.getDatabaseApps();

                            if (!array.isEmpty() && !isCardShown) {
                                viewMoreAppCard.setVisibility(View.VISIBLE);
                                isCardShown = true;
                            }

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

    public void checkImageSetVersion() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Const.GETIMAGESETVERSION)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String imageSetName = response.body().string();
                if (!shared.getImageSetVersion().equals(imageSetName)){
                    new SharedPrefs(getApplicationContext()).setImageSetVersion(imageSetName);
                }
            }
        });
    }
}
