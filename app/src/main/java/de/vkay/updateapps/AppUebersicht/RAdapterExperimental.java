package de.vkay.updateapps.AppUebersicht;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
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
import de.vkay.updateapps.Sonstiges.Sonst;


public class RAdapterExperimental extends RecyclerView.Adapter<RAdapterExperimental.ViewHolder> {

    List<String> array;
    Context context;
    Bundle bund;
    SharedPrefs shared;

    // Konstruktor
    public RAdapterExperimental(List<String> array, Context context, Bundle bund) {
        this.context = context;
        this.array = array;
        this.bund = bund;
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
                    int pos = getLayoutPosition();

                    if (shared.getWifiDownloadStatus() && !Sonst.isWifiConnected(context)) {
                        Snacks.toastInBackground(context, context.getString(R.string.no_wifi_connection), Snacks.LONG);
                    } else {
                        downloadApk(Const.BASE_DOWNLOAD_FILES + bund.getString(Const.PAKETNAME) +
                                "/exp/" + array.get(pos) + ".apk", array.get(pos));
                    }
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

        //final long downloadId = downloadManager.enqueue(request);
        downloadManager.enqueue(request);

        if (shared.getAutoInstallStatus()) {
            BroadcastReceiver onComplete = new BroadcastReceiver() {
                public void onReceive(Context ctxt, Intent intent) {

                    String fileName = Environment.getExternalStorageDirectory() + "/UpdateApps/" + bund.getString("name") +
                            "/" + name + ".apk";

                    System.out.println(fileName);

                    Uri file = FileProvider.getUriForFile(context,
                            BuildConfig.APPLICATION_ID + ".provider",
                            new File(fileName));

                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    install.setDataAndType(file,
                            //downloadManager.getMimeTypeForDownloadedFile(downloadId));
                            "application/vnd.android.package-archive");

                    context.startActivity(install);
                    context.unregisterReceiver(this);
                }
            };
            context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }

        Snacks.toastInBackground(context, context.getString(R.string.download_started), Toast.LENGTH_SHORT);
    }
}
