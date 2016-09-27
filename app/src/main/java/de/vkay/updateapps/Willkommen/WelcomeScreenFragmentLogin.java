package de.vkay.updateapps.Willkommen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.vkay.updateapps.Datenspeicher.SharedPrefs;
import de.vkay.updateapps.R;
import de.vkay.updateapps.User.UserLogin;


public class WelcomeScreenFragmentLogin extends Fragment {

    public static WelcomeScreenFragmentLogin newInstance() {
        return new WelcomeScreenFragmentLogin();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.frag_welcome_setdetails, container, false);

        SharedPrefs shared = new SharedPrefs(getActivity());
        shared.setWelcome();

        Button btnLogin = (Button) root.findViewById(R.id.welcome_login_btn);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                startActivity(new Intent(getActivity(), UserLogin.class));
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
