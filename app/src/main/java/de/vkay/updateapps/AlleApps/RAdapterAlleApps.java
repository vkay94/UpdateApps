package de.vkay.updateapps.AlleApps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.vkay.updateapps.AppUebersicht.AppUebersicht;
import de.vkay.updateapps.Datenspeicher.SharedPrefs;
import de.vkay.updateapps.R;
import de.vkay.updateapps.Sonstiges.Const;

public class RAdapterAlleApps extends RecyclerView.Adapter<RAdapterAlleApps.ViewHolder> {

    private List<AlleAppsDatatype> array;
    private Context context, c;
    private SharedPrefs shared;
    private Activity activity;

    //Konstruktor
    public RAdapterAlleApps(List<AlleAppsDatatype> array, Context context, Activity activity){
        this.array = array;
        this.context = context;
        shared = new SharedPrefs(context);
        this.activity = activity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView appname, version, date;
        CardView cardView;
        Intent intent;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            c = itemView.getContext();

            appname = (TextView) itemView.findViewById(R.id.rec_all_appname);
            version = (TextView) itemView.findViewById(R.id.rec_all_version);
            date = (TextView) itemView.findViewById(R.id.rec_all_date);
            image = (ImageView) itemView.findViewById(R.id.rec_all_image);

            cardView = (CardView) itemView.findViewById(R.id.rec_allapps_cardview);
            cardView.setOnClickListener(this);
            intent = new Intent(itemView.getContext(), AppUebersicht.class);
        }

        @Override
        public void onClick(View v) {
            int pos = getLayoutPosition();
            intent.putExtra(Const.PAKETNAME, array.get(pos).paketname);
            intent.putExtra(Const.NAME, array.get(pos).name);
            intent.putExtra(Const.VERSION, array.get(pos).version);
            intent.putExtra(Const.DATE, array.get(pos).date);
            intent.putExtra(Const.BESCHREIBUNG, array.get(pos).beschreibung);
            intent.putExtra(Const.CHANGELOG, array.get(pos).changelog);

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity, image, "image");

            c.startActivity(intent, options.toBundle());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_alleapps_cardview, parent, false));
    }

    @Override
    public void onBindViewHolder(RAdapterAlleApps.ViewHolder holder, int position) {

        holder.cardView.setBackgroundColor(ContextCompat.getColor(context, R.color.cardViewBGDarK));

        if (shared.getInstalledAppVersion(array.get(position).paketname) != null) {
            if((!shared.getInstalledAppVersion(array.get(position).paketname).matches(array.get(position).version))
                    && shared.getAppStatus(array.get(position).paketname)) {
                holder.cardView.setBackgroundColor(ContextCompat.getColor(context, R.color.cardViewBGUpdate));
            }
        }

        holder.appname.setText(array.get(position).name);
        holder.version.setText(array.get(position).version);
        holder.date.setText(array.get(position).date);
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

}
