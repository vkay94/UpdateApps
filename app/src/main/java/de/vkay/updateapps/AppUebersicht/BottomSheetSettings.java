package de.vkay.updateapps.AppUebersicht;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

import de.vkay.updateapps.Datenspeicher.SharedPrefs;
import de.vkay.updateapps.R;
import de.vkay.updateapps.Sonstiges.Const;

public class BottomSheetSettings extends BottomSheetDialogFragment {

    Bundle bund;
    SharedPrefs shared;

    CoordinatorLayout.Behavior behavior;
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    static BottomSheetSettings newInstance(Bundle bund) {
        BottomSheetSettings f = new BottomSheetSettings();
        f.setArguments(bund);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
        bund = getArguments();
        shared = new SharedPrefs(getActivity());

        if (shared.getInstalledAppVersion(bund.getString(Const.PAKETNAME)) != null) {
            TextView tvInstalled = (TextView) view.findViewById(R.id.bsheet_tv_installed);
            String installedText = getString(R.string.installed) + " " + shared.getInstalledAppVersion(bund.getString(Const.PAKETNAME));
            tvInstalled.setText(installedText);

            Button btnDetails = (Button) view.findViewById(R.id.bsheet_btn_details);
            btnDetails.setVisibility(View.VISIBLE);
            btnDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + bund.getString(Const.PAKETNAME)));
                        startActivity(intent);
                    }
                }
            });
        }

        SwitchCompat switchNotification = (SwitchCompat) view.findViewById(R.id.bsheet_switch_notiification);
        switchNotification.setChecked(shared.getAppStatus(bund.getString(Const.PAKETNAME)));
        switchNotification.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    shared.saveApp(bund.getString(Const.PAKETNAME));
                    FirebaseMessaging.getInstance().subscribeToTopic(bund.getString(Const.PAKETNAME));
                } else {
                    shared.removeApp(bund.getString(Const.PAKETNAME));
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(bund.getString(Const.PAKETNAME));
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Dafür, dass die Statusleiste nicht schwarz wird
        final WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
    }

}
