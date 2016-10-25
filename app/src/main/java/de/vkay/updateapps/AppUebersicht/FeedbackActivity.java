package de.vkay.updateapps.AppUebersicht;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import de.vkay.updateapps.Datenspeicher.SharedPrefs;
import de.vkay.updateapps.R;
import de.vkay.updateapps.Sonstiges.Const;
import de.vkay.updateapps.Sonstiges.Snacks;
import de.vkay.updateapps.Sonstiges.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FeedbackActivity extends AppCompatActivity {

    EditText editBetreff, editNachricht;
    CheckBox checkBoxVersion;

    SharedPrefs shared;
    Bundle bund;

    boolean addVersion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){ getSupportActionBar().setDisplayHomeAsUpEnabled(true); }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        bund = getIntent().getExtras();
        shared = new SharedPrefs(getApplicationContext());

        editBetreff = (EditText) findViewById(R.id.feedback_edit_betreff);
        editNachricht = (EditText) findViewById(R.id.feedback_edit_nachricht);

        checkBoxVersion = (CheckBox) findViewById(R.id.feedback_checkbox);
        checkBoxVersion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    addVersion = true;
                } else if (!compoundButton.isChecked()) {
                    addVersion = false;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_feedback, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_send_feedback:

                String beitrag = editNachricht.getText().toString();

                if (addVersion) {
                    beitrag += "\n\nVersion: " + shared.getInstalledAppVersion(bund.getString(Const.PAKETNAME));
                }


                sendFeedback(this,
                        editBetreff.getText().toString(),
                        shared.getUsername(),
                        beitrag);

                finish();

                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        showCancelDialog();
    }

    public void showCancelDialog() {
        AlertDialog.Builder explDialog = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        explDialog.setTitle(R.string.dialog_back)
                .setCancelable(false)
                .setMessage(R.string.dialog_remove_feedback);

        explDialog.setPositiveButton(R.string.dialog_verwerfen, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        explDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    public void sendFeedback(final Context context,
                             String betreff,
                             String name,
                             String mess) {
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add(Const.PAKETNAME, bund.getString(Const.PAKETNAME))
                .add(Const.FEED_BETREFF, betreff)
                .add(Const.FEED_AUTOR, name)
                .add(Const.FEED_TEXT, mess)
                .add(Const.FEED_TIME, Utils.getCurrentTimeStamp())
                .build();

        Request request = new Request.Builder()
                .url(Const.BASE_PHP + Const.WRITEFEED)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Snacks.toastInBackground(context, getString(R.string.error_send), Toast.LENGTH_SHORT);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                Snacks.toastInBackground(context, getString(R.string.send_success), Toast.LENGTH_SHORT);
            }
        });
    }
}
