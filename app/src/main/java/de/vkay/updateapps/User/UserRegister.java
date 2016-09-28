package de.vkay.updateapps.User;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

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

public class UserRegister extends AppCompatActivity {

    Button btnLogin;
    EditText editUser, editPass, editPass2;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        context = this;

        btnLogin = (Button) findViewById(R.id.user_btn_login);
        editUser = (EditText) findViewById(R.id.user_edit_username);
        editPass = (EditText) findViewById(R.id.user_edit_userpassword);
        editPass2 = (EditText) findViewById(R.id.user_edit_userpassword_erneut);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editPass.getText().toString().matches(editPass2.getText().toString())) {
                    Snacks.toastInBackground(getApplicationContext(), "Passwörter stimmen nicht überein", Toast.LENGTH_SHORT);
                } else
                    register();
            }
        });
    }

    public void register() {

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add(Const.USER_NAME, editUser.getText().toString())
                .add(Const.USER_PASS, editPass.getText().toString())
                .build();

        Request request = new Request.Builder()
                .url(Const.BASE_PHP + Const.REGISTERUSER)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Snacks.toastInBackground(context, context.getString(R.string.error), Toast.LENGTH_SHORT);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.body().string().equals("OK")) {
                    finish();
                    overridePendingTransition(R.anim.enter_activty_backwards, R.anim.exit_activity_backwards);
                } else {
                    Snacks.toastInBackground(context, getString(R.string.userregister_username_vergeben), Toast.LENGTH_SHORT);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_activty_backwards, R.anim.exit_activity_backwards);
    }
}
