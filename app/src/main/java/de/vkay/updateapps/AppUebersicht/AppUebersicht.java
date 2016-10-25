package de.vkay.updateapps.AppUebersicht;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.vkay.updateapps.BuildConfig;
import de.vkay.updateapps.Datenspeicher.SharedPrefs;
import de.vkay.updateapps.R;
import de.vkay.updateapps.Sonstiges.Const;
import de.vkay.updateapps.Sonstiges.Snacks;
import de.vkay.updateapps.Sonstiges.Utils;
import de.vkay.updateapps.User.Einstellungen;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AppUebersicht extends AppCompatActivity implements View.OnClickListener {

    Bundle bund;

    TextView tvDate, tvVersion, tvBeschreibung;
    TextView tvChangelog, tvChangelogExpanded, tvExpVersion;

    LinearLayout llScreenshots, llFeedback, llContainerExp;
    RelativeLayout rlExpandChangelog;
    Button btnDownload, btnShowChangelog, btnShowExp;
    ImageView imageSettings, changelogArrow, imageHeader, imageIcon;

    SharedPrefs shared;
    CoordinatorLayout coord;

    String downloadLink, currVersion, strPaketname, expChangelog;
    int counter = 0;
    boolean changelogExpanded = false;

    RecyclerView recyclerView;
    ArrayList<String> array;
    LinearLayoutManager llm;
    RAdapterExperimental rvE;

    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appbar;

    int actionBarHeight = 0;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_uebersicht);

        init();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(" ");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        loadFiles();

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.au_collaps);
        appbar = (AppBarLayout) findViewById(R.id.au_appbar);

        collapsingToolbarLayout.setScrimAnimationDuration(300);
        collapsingToolbarLayout.setScrimVisibleHeightTrigger(actionBarHeight + getStatusBarHeight() + 1);
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                if (scrollRange + verticalOffset > actionBarHeight) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = true;

                } else if (isShow && scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(bund.getString(Const.NAME));
                    isShow = false;
                    collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(getApplicationContext(), R.color.grey));
                }
            }
        });

    }

    public void init() {
        bund = getIntent().getExtras();
        shared = new SharedPrefs(this);

        Window w = getWindow();
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        w.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));

        strPaketname = bund.getString(Const.PAKETNAME);
        currVersion = bund.getString(Const.VERSION);
        downloadLink = Const.BASE_DOWNLOAD_FILES + bund.getString(Const.PAKETNAME) +
                "/" + bund.getString(Const.VERSION) + ".apk";

        tvDate = (TextView) findViewById(R.id.au_date);
        tvVersion = (TextView) findViewById(R.id.au_version);
        tvBeschreibung = (TextView) findViewById(R.id.au_beschreibung);
        tvChangelog = (TextView) findViewById(R.id.au_tv_changelog);
        tvChangelogExpanded = (TextView) findViewById(R.id.au_tv_changelog_expanded);
        tvExpVersion = (TextView) findViewById(R.id.au_tv_expversion);

        String dateVersion = bund.getString(Const.DATE) + " | " + bund.getString(Const.VERSION);

        tvDate.setText(dateVersion);
        tvVersion.setText(bund.getString(Const.NAME));
        tvBeschreibung.setText(Html.fromHtml(bund.getString(Const.BESCHREIBUNG)));
        tvChangelog.setText(Html.fromHtml(Utils.splitGetFirst("<x></x><br><br>", bund.getString(Const.CHANGELOG))));
        tvChangelogExpanded.setText(Html.fromHtml(Utils.splitGetFirst("<x></x><br><br>", bund.getString(Const.CHANGELOG))));

        llFeedback = (LinearLayout) findViewById(R.id.au_ll_feedback);
        llScreenshots = (LinearLayout) findViewById(R.id.au_ll_screenshots);
        llContainerExp = (LinearLayout) findViewById(R.id.au_ll_experimental);
        rlExpandChangelog = (RelativeLayout) findViewById(R.id.au_rl_changelog_and_icon);

        llContainerExp.setVisibility(View.GONE);

        btnDownload = (Button) findViewById(R.id.au_btn_download);
        btnShowChangelog = (Button) findViewById(R.id.au_btn_show_full_changelog);
        btnShowExp = (Button) findViewById(R.id.au_btn_show_exp_information);
        imageSettings = (ImageView) findViewById(R.id.au_imageSettings);
        changelogArrow = (ImageView) findViewById(R.id.au_image_changelog_expand);
        imageHeader = (ImageView) findViewById(R.id.au_header_image);
        imageIcon = (ImageView) findViewById(R.id.au_imageIcon);

        Glide.with(this)
                .load(Const.BASE_DOWNLOAD_FILES + strPaketname + "/header.png")
                .placeholder(R.drawable.placeholder_bg)
                .crossFade(300)
                .centerCrop()
                .fitCenter()
                .signature(new StringSignature(new SharedPrefs(this).getImageSetVersion()))
                .into(imageHeader);

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        array = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.au_recycler_exp);
        rvE = new RAdapterExperimental(array, this, bund);
        llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(rvE);

        llFeedback.setOnClickListener(this);
        llScreenshots.setOnClickListener(this);
        btnDownload.setOnClickListener(this);
        btnShowExp.setOnClickListener(this);
        btnShowChangelog.setOnClickListener(this);
        imageSettings.setOnClickListener(this);

        tvChangelog.post(new Runnable() {
            @Override
            public void run() {
                if (tvChangelog.getLineCount() == 7) {
                    changelogArrow.setVisibility(View.VISIBLE);
                    rlExpandChangelog.setOnClickListener(AppUebersicht.this);
                }
            }
        });

        if (shared.isTestBuild(bund.getString(Const.PAKETNAME))) {
            String s = getString(R.string.installed_experimental) + shared.getInstalledAppVersion(bund.getString(Const.PAKETNAME));
            tvExpVersion.setText(Html.fromHtml(s));
            tvExpVersion.setTextColor(ContextCompat.getColor(this, R.color.expVersion));
        }

        coord = (CoordinatorLayout) findViewById(R.id.au_coord);

        if (shared.getInstalledAppVersion(bund.getString(Const.PAKETNAME)) != null) {
            if (shared.getInstalledAppVersion(bund.getString(Const.PAKETNAME)).equals(bund.getString(Const.VERSION))) {
                btnDownload.setEnabled(false);
                btnDownload.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.downloadbtn_grey));
            }
        }
        if (bund.getString(Const.VERSION).contains("test-build")){
            btnDownload.setEnabled(false);
            btnDownload.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.downloadbtn_grey));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.au_ll_feedback:
                startActivity(new Intent(this, AUFeedback.class).putExtras(bund));
                overridePendingTransition(R.anim.slide_from_bottom, R.anim.stay);
                break;

            case R.id.au_ll_screenshots:
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(Const.GETIMAGEFILES + strPaketname)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String number = response.body().string();
                        int amount = Integer.parseInt(number);

                        if (amount > 0) {
                            bund.putInt("imageNumber", amount);
                            startActivity(new Intent(AppUebersicht.this, AUImageSlider.class).putExtras(bund));
                        } else {
                            Snacks.toastInBackground(AppUebersicht.this, "Keine Screenshots verfügbar", Snacks.SHORT);
                        }
                    }
                });
                break;

            case R.id.au_btn_download:
                if (counter < 2) {
                    if (shared.getWifiDownloadStatus() && !Utils.isWifiConnected(this)) {

                        Snacks.ShowSnack(getApplicationContext(), coord,
                                getString(R.string.no_wifi_connection), Snackbar.LENGTH_INDEFINITE,
                                R.color.greyStatus, R.color.blueish)
                                .setAction("Einstellungen", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(getApplicationContext(), Einstellungen.class));
                                    }
                                })
                                .show();

                    } else {
                        permission_check_download();
                        counter++;
                    }
                } else {
                    Snacks.toastInBackground(getApplicationContext(), getString(R.string.often), Toast.LENGTH_SHORT);
                }
                break;

            case R.id.au_btn_show_exp_information:
                if(expChangelog == null) {
                    loadExpChangelog();
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(AppUebersicht.this, R.style.MyDialogTheme)
                            .setTitle(R.string.experim_changelog)
                            .setCancelable(true)
                            .setMessage(Html.fromHtml(expChangelog))
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });

                    AlertDialog dialog = alert.create();
                    dialog.show();
                    TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                    if (textView != null) {
                        textView.setTextSize(13);
                    }
                }

                break;

            case R.id.au_btn_show_full_changelog:
                AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(AppUebersicht.this, R.style.MyDialogTheme)
                        .setTitle(R.string.full_changelog)
                        .setCancelable(true)
                        .setMessage(Html.fromHtml(Utils.splitGetSecond("<x></x><br><br>", bund.getString(Const.CHANGELOG))))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                AlertDialog dialog = alert.create();
                dialog.show();
                TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                if (textView != null) {
                    textView.setTextSize(13);
                }
                break;

            case R.id.au_imageSettings:
                BottomSheetSettings.newInstance(bund).show(getSupportFragmentManager(), "TAG");
                break;

            case R.id.au_rl_changelog_and_icon:
                if (!changelogExpanded) {
                    tvChangelogExpanded.setVisibility(View.VISIBLE);
                    tvChangelog.setVisibility(View.GONE);
                    changelogArrow.setImageDrawable(getDrawable(R.drawable.ic_chevron_up));
                    changelogExpanded = true;
                } else {
                    tvChangelog.setVisibility(View.VISIBLE);
                    tvChangelogExpanded.setVisibility(View.GONE);
                    changelogArrow.setImageDrawable(getDrawable(R.drawable.ic_chevron_down));
                    changelogExpanded = false;
                }
                break;

            default:
                break;
        }
    }

    private void permission_check_download() {
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
            }
        } else {
            checkLatestVersion();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            checkLatestVersion();
        } else
            Snacks.toastInBackground(getApplicationContext(), getString(R.string.permission_denied), Toast.LENGTH_SHORT);
    }

    public void checkLatestVersion() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Const.BASE_PHP + Const.GETAPPS + "?getVersionOf=" + strPaketname)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body().string().equals(currVersion)) {
                    downloadApk(downloadLink);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder alert = new AlertDialog.Builder(AppUebersicht.this, R.style.MyDialogTheme)
                                    .setTitle(R.string.not_latest_version_header)
                                    .setMessage(getString(R.string.not_latest_version_text))
                                    .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                            dialog.dismiss();
                                        }
                                    })
                                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alert.show();
                        }
                    });

                }
            }
        });
    }

    public void downloadApk(String url){

        DownloadManager downloadManager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(
                "/UpdateApps/" + bund.getString("name"), bund.getString("name") + " - " + bund.getString("version") + ".apk");
        request.setTitle("Update: " + bund.getString("name") + " - " + bund.getString("version"));

        File file = new File(Environment.getExternalStorageDirectory() +
                "/UpdateApps/" + bund.getString("name") + "/" + bund.getString("name") + " - " + bund.getString("version") + ".apk");
        if (file.exists()) {
            file.delete();
        }

        downloadManager.enqueue(request);

        if (shared.getAutoInstallStatus()) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                BroadcastReceiver onComplete = new BroadcastReceiver() {
                    public void onReceive(Context ctxt, Intent intent) {

                        String fileName = Environment.getExternalStorageDirectory() + "/UpdateApps/" + bund.getString("name") +
                                "/" + bund.getString("name") + " - " + bund.getString("version") + ".apk";

                        Uri file = FileProvider.getUriForFile(getApplicationContext(),
                                BuildConfig.APPLICATION_ID + ".provider",
                                new File(fileName));

                        Intent install = new Intent(Intent.ACTION_VIEW);
                        install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        install.setDataAndType(file,
                                "application/vnd.android.package-archive");

                        startActivity(install);
                        unregisterReceiver(this);
                    }
                };
                registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            } else {
                BroadcastReceiver onComplete = new BroadcastReceiver() {
                    public void onReceive(Context ctxt, Intent intent) {
                        String fileName = Environment.getExternalStorageDirectory() + "/UpdateApps/" + bund.getString("name") +
                                "/" + bund.getString("name") + " - " + bund.getString("version") + ".apk";
                        Intent intentO = new Intent(Intent.ACTION_VIEW);
                        intentO.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
                        startActivity(intentO);

                        unregisterReceiver(this);
                    }
                };
                registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }
        }

        Snacks.toastInBackground(getApplicationContext(), getString(R.string.download_started), Toast.LENGTH_SHORT);
    }

    public void loadFiles() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Const.GETEXPFILES + bund.getString(Const.PAKETNAME))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println(getString(R.string.error));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONArray array = new JSONArray(response.body().string());

                    for (int i = 0; i <array.length(); i++){

                        final String file_name = array.getString(i);
                        System.out.println(file_name);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rvE.newInsert(file_name, 0);
                            }
                        });
                    }

                    if (array.length() != 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                llContainerExp.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void loadExpChangelog() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Const.BASE_PHP + Const.GETEXPLOG + bund.getString(Const.PAKETNAME))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    expChangelog = new JSONArray(response.body().string()).getJSONObject(0).getString("exp");
                    System.out.println("Experi");

                    if (!expChangelog.isEmpty() && expChangelog != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder alert = new AlertDialog.Builder(AppUebersicht.this, R.style.MyDialogTheme)
                                        .setTitle(R.string.experim_changelog)
                                        .setCancelable(true)
                                        .setMessage(Html.fromHtml(expChangelog))
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });

                                AlertDialog dialog = alert.create();
                                dialog.show();
                                TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                                if (textView != null) {
                                    textView.setTextSize(13);
                                }
                            }
                        });
                    } else {
                        Snacks.toastInBackground(AppUebersicht.this, "Keine Infos zu Experimental", Snacks.SHORT);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
