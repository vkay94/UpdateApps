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

public class AlleApps extends AppCompatActivity {

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

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview_alleapps);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);

        DB_AlleApps db_alleApps = new DB_AlleApps(this);
        ArrayList<AlleAppsDatatype> allAppsArray = db_alleApps.getDatabaseApps();

        RAdapterAlleApps rvAAA = new RAdapterAlleApps(allAppsArray, getApplicationContext(), this);
        recyclerView.setAdapter(rvAAA);
        recyclerView.setHasFixedSize(true);
    }
}
