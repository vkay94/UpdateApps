package de.vkay.updateapps.AlleApps;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

import de.vkay.updateapps.Datenspeicher.DB_AlleApps;
import de.vkay.updateapps.R;
import de.vkay.updateapps.Sonstiges.Snacks;

public class AlleApps extends AppCompatActivity {

    public ArrayList<AlleAppsDatatype> allAppsArray;
    public RAdapterAlleApps rvAAA;
    RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;

    DB_AlleApps db_alleApps;
    Snacks snacks;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alle_apps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){ getSupportActionBar().setDisplayHomeAsUpEnabled(true); }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_alleapps);

        gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);

        snacks = new Snacks();

        allAppsArray = new ArrayList<>();

        db_alleApps = new DB_AlleApps(this, null, 1);
        allAppsArray = db_alleApps.getDatabaseApps();

        rvAAA = new RAdapterAlleApps(allAppsArray, getApplicationContext());
        recyclerView.setAdapter(rvAAA);
        recyclerView.setHasFixedSize(true);
    }
}
