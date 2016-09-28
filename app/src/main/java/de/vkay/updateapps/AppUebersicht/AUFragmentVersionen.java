package de.vkay.updateapps.AppUebersicht;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import de.vkay.updateapps.Datenspeicher.SharedPrefs;
import de.vkay.updateapps.R;
import de.vkay.updateapps.Sonstiges.Const;
import de.vkay.updateapps.Sonstiges.Snacks;
import de.vkay.updateapps.Sonstiges.Sonst;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AUFragmentVersionen extends Fragment{

    public static AUFragmentVersionen newInstance(Bundle bundle) {
        AUFragmentVersionen fragmentVersionen = new AUFragmentVersionen();
        fragmentVersionen.setArguments(bundle);

        return fragmentVersionen;
    }

    Bundle bund;
    TextView tvChangelog;
    SharedPrefs shared;

    RecyclerView recyclerView;
    ArrayList<String> array;
    LinearLayoutManager llm;
    RAdapterExperimental rvE;

    boolean isLoaded = false;
    Button btnLoadExp;

    String expChangelog = "";

    boolean isExpanded = false;
    TextView tvChangelogExpanded;

    ImageView imageExpand;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_appueber_version, container, false);

        bund = getArguments();
        shared = new SharedPrefs(getActivity());

        tvChangelog = (TextView) view.findViewById(R.id.auversion_tv_changelog);
        tvChangelog.setText(Html.fromHtml(Sonst.splitGetFirst("<x></x>", bund.getString(Const.CHANGELOG))));

        array = new ArrayList<>();

        recyclerView = (RecyclerView) view.findViewById(R.id.auversion_recycler_exp);
        rvE = new RAdapterExperimental(array, getActivity(), bund);
        llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(rvE);

        ImageView imageChangelog = (ImageView) view.findViewById(R.id.auversion_image_changelog);
        imageChangelog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme)
                        .setTitle(R.string.full_changelog)
                        .setCancelable(true)
                        .setMessage(Html.fromHtml(Sonst.splitGetSecond("<x></x><br><br>", bund.getString(Const.CHANGELOG))))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                AlertDialog dialog = alert.create();
                dialog.show();
                TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                if (textView != null) {
                    textView.setTextSize(13);
                }
            }
        });

        if (shared.isTestBuild(bund.getString(Const.PAKETNAME))) {
            TextView tvExp = (TextView) view.findViewById(R.id.auversion_tv_expversion);
            String s = getString(R.string.installed_experimental) + shared.getInstalledAppVersion(bund.getString(Const.PAKETNAME));
            tvExp.setText(Html.fromHtml(s));
            tvExp.setTextColor(ContextCompat.getColor(getActivity(), R.color.expVersion));
        }

        btnLoadExp = (Button) view.findViewById(R.id.auversion_btn_load);
        btnLoadExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLoaded) {
                    if (permission_check_download()) {
                        loadFiles();
                        loadExpChangelog();

                    }
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme)
                            .setTitle(R.string.experim_changelog)
                            .setCancelable(true)
                            .setMessage(expChangelog)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });

                    AlertDialog dialog = alert.create();
                    dialog.show();
                    TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                    if (textView != null) {
                        textView.setTextSize(13);
                    }
                }
            }
        });

        tvChangelogExpanded = (TextView) view.findViewById(R.id.auversion_tv_changelog_expanded);
        imageExpand = (ImageView) view.findViewById(R.id.auversion_image_changelog_expand);

        tvChangelog.post(new Runnable() {
            @Override
            public void run() {
                int lc = tvChangelog.getLineCount();
                System.out.println(lc);

                if (lc == 7) {
                    tvChangelogExpanded.setText(Html.fromHtml(Sonst.splitGetFirst("<x></x>", bund.getString(Const.CHANGELOG))));
                    imageExpand.setVisibility(View.VISIBLE);
                    imageExpand.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isExpanded) {
                                tvChangelogExpanded.setVisibility(View.VISIBLE);
                                tvChangelog.setVisibility(View.GONE);
                                imageExpand.setImageDrawable(getActivity().getDrawable(R.drawable.arrow_up));
                                isExpanded = true;
                            } else if (isExpanded) {
                                tvChangelog.setVisibility(View.VISIBLE);
                                tvChangelogExpanded.setVisibility(View.GONE);
                                imageExpand.setImageDrawable(getActivity().getDrawable(R.drawable.arrow_down));
                                isExpanded = false;
                            }
                        }
                    });
                }
            }
        });

        return view;
    }

    private boolean permission_check_download() {
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},200);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 200 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            loadFiles();
        } else
            Snacks.toastInBackground(getActivity(), getString(R.string.permission_denied), Toast.LENGTH_SHORT);
    }

    public void loadFiles() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Const.GETEXPFILES + bund.getString(Const.PAKETNAME))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println(getString(R.string.error));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONArray array = new JSONArray(response.body().string());

                    for (int i = 0; i <array.length(); i++){

                        final String file_name = array.getString(i);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rvE.newInsert(file_name, 0);
                                isLoaded = true;
                                btnLoadExp.setText(R.string.info);
                            }
                        });
                    }

                    if (array.length() == 0) {
                        Snacks.toastInBackground(getActivity(), getString(R.string.no_experim_builds_available), Toast.LENGTH_LONG);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void loadExpChangelog() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Const.BASE_PHP + Const.GETEXPLOG + bund.getString(Const.PAKETNAME))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    expChangelog = new JSONArray(response.body().string()).getJSONObject(0).getString("exp");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
