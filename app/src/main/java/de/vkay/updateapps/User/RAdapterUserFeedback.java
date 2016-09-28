package de.vkay.updateapps.User;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import de.vkay.updateapps.AppUebersicht.Feedback.FeedbackDatatype;
import de.vkay.updateapps.Datenspeicher.DB_AlleApps;
import de.vkay.updateapps.R;
import de.vkay.updateapps.Sonstiges.Const;
import de.vkay.updateapps.Sonstiges.Snacks;
import de.vkay.updateapps.Sonstiges.Sonst;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class RAdapterUserFeedback extends RecyclerView.Adapter<RAdapterUserFeedback.ViewHolder> {

    List<FeedbackDatatype> array;
    Context context;
    DB_AlleApps db;

    public RAdapterUserFeedback(List<FeedbackDatatype> array, Context context) {
        this.array = array;
        this.context = context;
        db = new DB_AlleApps(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_user_feedback, parent, false));
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_art, tv_message, tv_date, tv_app;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_app = (TextView) itemView.findViewById(R.id.rec_user_feedback_app);
            tv_art = (TextView) itemView.findViewById(R.id.rec_user_feedback_art);
            tv_date = (TextView) itemView.findViewById(R.id.rec_user_feedback_date);
            tv_message = (TextView) itemView.findViewById(R.id.rec_user_feedback_message);

            cardView = (CardView) itemView.findViewById(R.id.rec_feedback_cardview);
            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = getLayoutPosition();
                    showDeleteDialog(pos);
                    return false;
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_art.setVisibility(View.GONE);

        holder.tv_art.setText(array.get(position).art);
        holder.tv_app.setText(db.getAppName(array.get(position).paketname));
        holder.tv_message.setText(array.get(position).message);
        holder.tv_date.setText(Sonst.getTimeDifference(array.get(position).date));

        if (!array.get(position).art.isEmpty()) {
            holder.tv_art.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return array.size();
    }

    // Methoden

    public void newInsert(FeedbackDatatype newEntry, int position){
        if (array.indexOf(newEntry) == -1) {
            array.add(position, newEntry);
            notifyItemInserted(position);
        }
    }

    public void removeItem(FeedbackDatatype feed, int position) {
        array.remove(feed);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, array.size());
    }

    public void deleteEntry(String time){

        String finalUrl = Const.BASE_PHP + Const.GETFEED + "?delete=" + time;
        finalUrl = finalUrl.replaceAll(" ", "%20");

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Snacks.toastInBackground(context, "Fehler beim Löschen", Toast.LENGTH_SHORT);
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                Snacks.toastInBackground(context, "Löschen erfolgreich", Toast.LENGTH_SHORT);
            }
        });
    }

    public void showDeleteDialog(final int position) {
        AlertDialog.Builder explDialog = new AlertDialog.Builder(context, R.style.MyDialogTheme);
        explDialog.setTitle(R.string.dialog_delete)
                .setMessage(R.string.dialog_delete_msg);

        explDialog.setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteEntry(array.get(position).date);
                removeItem(array.get(position), position);

                dialog.dismiss();
            }
        });
        explDialog.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }
}
