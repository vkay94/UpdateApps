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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import de.vkay.updateapps.BuildConfig;
import de.vkay.updateapps.Datenspeicher.SharedPrefs;
import de.vkay.updateapps.R;
import de.vkay.updateapps.Sonstiges.Const;
import de.vkay.updateapps.Sonstiges.Snacks;
import de.vkay.updateapps.Sonstiges.Utils;
import de.vkay.updateapps.User.Einstellungen;


public class RAdapterExperimental extends RecyclerView.Adapter<RAdapterExperimental.ViewHolder> {

    List<String> array;
    Context context;
    Bundle bund;
    SharedPrefs shared;
    View rootView;

    // Konstruktor
    public RAdapterExperimental(List<String> array, Context context, Bundle bund, View rootView) {
        this.context = context;
        this.array = array;
        this.bund = bund;
        this.rootView = rootView;
        shared = new SharedPrefs(context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView imageDownload;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.card_exp_title);
            imageDownload = (ImageView) itemView.findViewById(R.id.card_exp_btn_download);

            imageDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    permission_check_download(getLayoutPosition());
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_experimental_cardview, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(array.get(position));
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public void newInsert(String newEntry, int position){
        if (array.indexOf(newEntry) == -1) {
            array.add(position, newEntry);
            notifyItemInserted(position);
        }
    }

    // Download

    public void downloadApk(String url, final String name){

        final DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir("/UpdateApps/" + bund.getString(Const.NAME), name + ".apk");
        request.setTitle("Experimental: " + bund.getString(Const.NAME) + " - " + name);

        File file = new File(Environment.getExternalStorageDirectory() +
                "/UpdateApps/" + bund.getString(Const.NAME) + "/" + name +  ".apk");
        if (file.exists()) {
            file.delete();
        }

        downloadManager.enqueue(request);

        if (shared.getAutoInstallStatus()) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                BroadcastReceiver onComplete = new BroadcastReceiver() {
                    public void onReceive(Context ctxt, Intent intent) {

                        String fileName = Environment.getExternalStorageDirectory() + "/UpdateApps/" + bund.getString("name") +
                                "/" + name + ".apk";

                        Uri file = FileProvider.getUriForFile(context,
                                BuildConfig.APPLICATION_ID + ".provider",
                                new File(fileName));

                        Intent install = new Intent(Intent.ACTION_VIEW);
                        install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        install.setDataAndType(file,
                                "application/vnd.android.package-archive");

                        context.startActivity(install);
                        context.unregisterReceiver(this);
                    }
                };
                context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            } else {
                BroadcastReceiver onComplete = new BroadcastReceiver() {
                    public void onReceive(Context ctxt, Intent intent) {
                        String fileName = Environment.getExternalStorageDirectory() + "/UpdateApps/" + bund.getString("name") +
                                "/" + name + ".apk";

                        Intent intentO = new Intent(Intent.ACTION_VIEW);
                        intentO.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
                        context.startActivity(intentO);

                        context.unregisterReceiver(this);
                    }
                };
                context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }
        }

        Snacks.toastInBackground(context, context.getString(R.string.download_started), Toast.LENGTH_SHORT);
    }

    private void permission_check_download(int pos) {
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.MyDialogTheme)
                    .setTitle(R.string.permission)
                    .setCancelable(true)
                    .setMessage("Der Download benötigt Zugriff auf den Speicher, um die APK zu speichern und zu starten. Klicke auf Einstellungen, um " +
                            "die Berechtigung zu aktivieren und probiere es erneut.")
                    .setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + bund.getString(Const.PAKETNAME)));
                            context.startActivity(intent);
                        }
                    });
            alert.show();
        } else {
            if (shared.getWifiDownloadStatus() && !Utils.isWifiConnected(context)) {

                Snacks.ShowSnack(context, rootView,
                        context.getString(R.string.no_wifi_connection), Snackbar.LENGTH_INDEFINITE,
                        R.color.greyStatus, R.color.blueish)
                        .setAction("Einstellungen", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                context.startActivity(new Intent(context, Einstellungen.class));
                            }
                        })
                        .show();

            } else {
                downloadApk(Const.BASE_DOWNLOAD_FILES + bund.getString(Const.PAKETNAME) +
                        "/exp/" + array.get(pos) + ".apk", array.get(pos));
            }
        }
    }
}
