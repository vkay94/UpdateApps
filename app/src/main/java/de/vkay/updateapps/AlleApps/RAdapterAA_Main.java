package de.vkay.updateapps.AlleApps;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.vkay.updateapps.AppUebersicht.AUMain;
import de.vkay.updateapps.Datenspeicher.SharedPrefs;
import de.vkay.updateapps.R;
import de.vkay.updateapps.Sonstiges.Const;

public class RAdapterAA_Main extends RecyclerView.Adapter<RAdapterAA_Main.ViewHolder>{

    private List<AlleAppsDatatype> array;
    private Context context, c;
    private SharedPrefs shared;

    public RAdapterAA_Main(List<AlleAppsDatatype> array, Context context){
        this.array = array;
        this.context = context;
        shared = new SharedPrefs(context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView appname;
        CardView cardView;
        Intent intent;

        public ViewHolder(View itemView) {
            super(itemView);
            c = itemView.getContext();

            appname = (TextView) itemView.findViewById(R.id.rec_aa_main_appname);

            cardView = (CardView) itemView.findViewById(R.id.rec_aa_main_cardview);
            cardView.setOnClickListener(this);
            intent = new Intent(itemView.getContext(), AUMain.class);
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

            c.startActivity(intent);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_aa_main_cardview, parent, false));
    }

    @Override
    public void onBindViewHolder(RAdapterAA_Main.ViewHolder holder, int position) {

        holder.cardView.setBackgroundColor(ContextCompat.getColor(context, R.color.cardViewBGDarK));

        if (shared.getInstalledAppVersion(array.get(position).paketname) != null) {
            if ((!shared.getInstalledAppVersion(array.get(position).paketname).matches(array.get(position).version)))
                if (shared.getAppStatus(array.get(position).paketname)) {
                    holder.cardView.setBackgroundColor(ContextCompat.getColor(context, R.color.cardViewBGUpdate));
                }
        }

        holder.appname.setText(array.get(position).name);
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public void addAll(List<AlleAppsDatatype> list) {
        array.clear();
        array.addAll(list);
        notifyDataSetChanged();
    }
}
