package de.vkay.updateapps.User;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import de.vkay.updateapps.Datenspeicher.SharedPrefs;
import de.vkay.updateapps.R;
import de.vkay.updateapps.Sonstiges.Const;
import de.vkay.updateapps.Sonstiges.Snacks;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserLogin extends AppCompatActivity {

    Button btnLogin;
    EditText editUser, editPass;
    TextView tvRegister;

    Context context;
    SharedPrefs shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        context = this;
        shared = new SharedPrefs(this);

        btnLogin = (Button) findViewById(R.id.user_btn_login);
        editUser = (EditText) findViewById(R.id.user_edit_username);
        editPass = (EditText) findViewById(R.id.user_edit_userpassword);
        tvRegister = (TextView) findViewById(R.id.user_tv_register);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, UserRegister.class));
                overridePendingTransition(R.anim.enter_activity, R.anim.exit_activity);
            }
        });

    }

    public void login() {

        final String username = editUser.getText().toString();
        String passwort = editPass.getText().toString();

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("name", username)
                .add("passwort", passwort)
                .build();

        Request request = new Request.Builder()
                .url(Const.BASE_PHP + Const.LOGINUSER)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body().string().matches("Pech")) {
                    Snacks.toastInBackground(context, "Benutzer oder Passwort falsch", Toast.LENGTH_SHORT);
                } else {
                    Snacks.toastInBackground(context, "Login erfolgreich", Toast.LENGTH_SHORT);
                    shared.setUsername(username);
                    shared.setLoggedInStatus(true);
                    setResult(2);
                    finish();
                }
            }
        });
    }

}
