package de.vkay.updateapps.AppUebersicht;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;

import de.vkay.updateapps.Datenspeicher.SharedPrefs;
import de.vkay.updateapps.R;
import de.vkay.updateapps.Sonstiges.Const;
import de.vkay.updateapps.Sonstiges.Snacks;
import de.vkay.updateapps.Sonstiges.Sonst;
import de.vkay.updateapps.User.Einstellungen;

public class AUFragmentUbersicht extends android.support.v4.app.Fragment {

    public static AUFragmentUbersicht newInstance(Bundle b) {

        AUFragmentUbersicht fragmentUbersicht = new AUFragmentUbersicht();
        fragmentUbersicht.setArguments(b);

        return fragmentUbersicht;
    }

    Bundle bund;
    String downloadLink;

    SharedPrefs shared;

    int counter = 0;

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
            public void onClick(View view) {
                if (counter < 2) {
                    if (shared.getWifiDownloadStatus() && !Sonst.isWifiConnected(getActivity())) {
                        Snacks.toastInBackground(getActivity(), "Keine WiFi-Verbindung (-> Einstellungen)", Snacks.LONG);
                    } else {
                        permission_check_download();
                        counter++;
                    }
                } else {
                    Snacks.toastInBackground(getActivity(), "Oft genug geladen", Toast.LENGTH_SHORT);
                }
            }
        });

        CheckBox checkBoxNotify = (CheckBox) view.findViewById(R.id.austart_checkbox_notify);
        checkBoxNotify.setChecked(shared.getAppStatus(bund.getString(Const.PAKETNAME)));
        checkBoxNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    shared.saveApp(bund.getString(Const.PAKETNAME));
                    FirebaseMessaging.getInstance().subscribeToTopic(bund.getString(Const.PAKETNAME));
                    Snacks.toastInBackground(getActivity(), "App abonniert", Toast.LENGTH_SHORT);
                } else
                if (!compoundButton.isChecked()) {
                    shared.removeApp(bund.getString(Const.PAKETNAME));
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(bund.getString(Const.PAKETNAME));
                    Snacks.toastInBackground(getActivity(), "App deabonniert", Toast.LENGTH_SHORT);
                }
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

        downloadManager.enqueue(request);

        if (shared.getAutoInstallStatus()) {
            BroadcastReceiver onComplete = new BroadcastReceiver() {
                public void onReceive(Context ctxt, Intent intent) {
                    String fileName = Environment.getExternalStorageDirectory() + "/UpdateApps/" + bund.getString("name") +
                            "/" + bund.getString("name") + " - " + bund.getString("version") + ".apk";
                    Intent intentO = new Intent(Intent.ACTION_VIEW);
                    intentO.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
                    getActivity().startActivity(intentO);

                    getActivity().unregisterReceiver(this);
                }
            };
            getActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }

        //new Snacks().Grey(getActivity().findViewById(android.R.id.content), getActivity(), "Download gestartet", Snacks.SHORT);
        Snacks.toastInBackground(getActivity(), "Download gestartet", Toast.LENGTH_LONG);
    }

    // Permissions

    private void permission_check_download() {
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
                return;
            }
        } else {
            downloadApk(downloadLink);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            downloadApk(downloadLink);
        } else
            Snacks.toastInBackground(getActivity(), "Verweigert", Toast.LENGTH_SHORT);
    }
}
