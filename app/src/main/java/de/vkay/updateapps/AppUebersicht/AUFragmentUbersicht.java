package de.vkay.updateapps.AppUebersicht;

import android.Manifest;
import android.app.AlertDialog;
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
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import de.vkay.updateapps.BuildConfig;
import de.vkay.updateapps.Datenspeicher.SharedPrefs;
import de.vkay.updateapps.R;
import de.vkay.updateapps.Sonstiges.Const;
import de.vkay.updateapps.Sonstiges.Snacks;
import de.vkay.updateapps.Sonstiges.Sonst;
import de.vkay.updateapps.User.Einstellungen;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AUFragmentUbersicht extends android.support.v4.app.Fragment {

    public static AUFragmentUbersicht newInstance(Bundle b) {

        AUFragmentUbersicht fragmentUbersicht = new AUFragmentUbersicht();
        fragmentUbersicht.setArguments(b);

        return fragmentUbersicht;
    }

    Bundle bund;
    String downloadLink, currVersion, paket;

    SharedPrefs shared;
    int counter = 0;
    CoordinatorLayout coord;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
//
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_appueber_start, container, false);

        bund = getArguments();
        shared = new SharedPrefs(getActivity());
        coord = (CoordinatorLayout) getActivity().findViewById(R.id.appueber_coord);
        currVersion = bund.getString(Const.VERSION);
        paket = bund.getString(Const.PAKETNAME);

        TextView tvVersion = (TextView) view.findViewById(R.id.austart_version);
        TextView tvDate = (TextView) view.findViewById(R.id.austart_date);
        TextView tvBeschreibung = (TextView) view.findViewById(R.id.austart_beschreibung);

        tvBeschreibung.setText(Html.fromHtml(bund.getString(Const.BESCHREIBUNG)));
        tvBeschreibung.setMovementMethod(LinkMovementMethod.getInstance());
        tvVersion.setText(bund.getString(Const.VERSION));
        tvDate.setText(bund.getString(Const.DATE));

        downloadLink = Const.BASE_DOWNLOAD_FILES + bund.getString(Const.PAKETNAME) +
                "/" + bund.getString(Const.VERSION) + ".apk";

        Button btnDownload = (Button) view.findViewById(R.id.austart_btn_download);

        if (bund.getString(Const.VERSION).matches("experimental")){
            btnDownload.setEnabled(false);
            btnDownload.setTextColor(ContextCompat.getColor(getActivity(), R.color.downloadbtn_grey));
        }

        if (shared.getInstalledAppVersion(bund.getString(Const.PAKETNAME)) != null) {
            if (shared.getInstalledAppVersion(bund.getString(Const.PAKETNAME)).equals(bund.getString(Const.VERSION))) {
                btnDownload.setEnabled(false);
                btnDownload.setTextColor(ContextCompat.getColor(getActivity(), R.color.downloadbtn_grey));
            }
        }

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (counter < 2) {
                    if (shared.getWifiDownloadStatus() && !Sonst.isWifiConnected(getActivity())) {

                        Snacks.ShowSnack(getActivity(), coord, getString(R.string.no_wifi_connection), Snackbar.LENGTH_INDEFINITE,
                                R.color.greyStatus, R.color.blueish)
                                .setAction("Einstellungen", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(getActivity(), Einstellungen.class));
                                    }
                                })
                                .show();

                    } else {
                        permission_check_download();
                        counter++;
                    }
                } else {
                    Snacks.toastInBackground(getActivity(), getString(R.string.often), Toast.LENGTH_SHORT);
                }
            }
        });

        ImageView imageSettingsBS = (ImageView) view.findViewById(R.id.austart_imageSettings);
        imageSettingsBS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetSettings.newInstance(bund).show(getActivity().getSupportFragmentManager(), "TAG");
            }
        });

        return view;
    }

    public void downloadApk(String url){

        DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(
                "/UpdateApps/" + bund.getString("name"), bund.getString("name") + " - " + bund.getString("version") + ".apk");
        request.setTitle("Update: " + bund.getString("name") + " - " + bund.getString("version"));

        //final long downloadId = downloadManager.enqueue(request);
        downloadManager.enqueue(request);

        if (shared.getAutoInstallStatus()) {
            BroadcastReceiver onComplete = new BroadcastReceiver() {
                public void onReceive(Context ctxt, Intent intent) {

                    String fileName = Environment.getExternalStorageDirectory() + "/UpdateApps/" + bund.getString("name") +
                            "/" + bund.getString("name") + " - " + bund.getString("version") + ".apk";

                    Uri file = FileProvider.getUriForFile(getActivity(),
                            BuildConfig.APPLICATION_ID + ".provider",
                            new File(fileName));

                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    install.setDataAndType(file,
                            //downloadManager.getMimeTypeForDownloadedFile(downloadId));
                            "application/vnd.android.package-archive");

                    getActivity().startActivity(install);
                    getActivity().unregisterReceiver(this);
                }
            };
            getActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }

        Snacks.toastInBackground(getActivity(), getActivity().getString(R.string.download_started), Toast.LENGTH_SHORT);
    }

    // Permissions

    private void permission_check_download() {
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

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
            Snacks.toastInBackground(getActivity(), getString(R.string.permission_denied), Toast.LENGTH_SHORT);
    }

    public void checkLatestVersion() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Const.BASE_PHP + Const.GETAPPS + "?getVersionOf=" + paket)
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme)
                                    .setTitle(R.string.not_latest_version_header)
                                    .setMessage(getString(R.string.not_latest_version_text))
                                    .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            getActivity().finish();
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
}
