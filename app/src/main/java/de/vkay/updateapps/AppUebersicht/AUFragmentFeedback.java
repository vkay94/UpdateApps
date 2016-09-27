package de.vkay.updateapps.AppUebersicht;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import de.vkay.updateapps.AppUebersicht.Feedback.FeedbackDatatype;
import de.vkay.updateapps.AppUebersicht.Feedback.RAdapterFeedback;
import de.vkay.updateapps.R;
import de.vkay.updateapps.Sonstiges.Const;
import de.vkay.updateapps.Sonstiges.Snacks;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;


public class AUFragmentFeedback extends Fragment {

    public static AUFragmentFeedback newInstance(Bundle bundle) {
        AUFragmentFeedback fragmentFeedback = new AUFragmentFeedback();
        fragmentFeedback.setArguments(bundle);

        return fragmentFeedback;
    }

    RecyclerView recyclerView;
    ArrayList<FeedbackDatatype> array;
    LinearLayoutManager llm;
    RAdapterFeedback rvAF;

    SwipeRefreshLayout swipe;
    Bundle bund;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_appueber_feedback, container, false);

        bund = getArguments();

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_feedback);
        recyclerView.setNestedScrollingEnabled(false);

        array = new ArrayList<>();
        rvAF = new RAdapterFeedback(array, getActivity());
        llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(rvAF);

        swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainerFeedback);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRevsWithOkHttp2();
                rvAF.notifyDataSetChanged();

                swipe.setRefreshing(false);
            }
        });

        getRevsWithOkHttp2();

        return view;
    }

    public void getRevsWithOkHttp2() {
        String jsonArrayLink = Const.BASE_PHP + Const.GETFEED + "?paketname=" + bund.getString(Const.PAKETNAME);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(jsonArrayLink)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Snacks.toastInBackground(getActivity(), "Fehler beim Laden von Feedback", Toast.LENGTH_SHORT);
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                try {
                    String jsonData = response.body().string();
                    JSONArray Jarray = new JSONObject(jsonData).getJSONArray("updateapps");

                    for (int i = 0; i < Jarray.length(); i++){
                        JSONObject obj = Jarray.getJSONObject(i);

                        final String paketname = obj.getString(Const.FEED_PAKETNAME);
                        final String date = obj.getString(Const.FEED_TIME);
                        final String message = obj.getString(Const.FEED_TEXT);
                        final String art = obj.getString(Const.FEED_BETREFF);
                        final String autor = obj.getString(Const.FEED_AUTOR);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rvAF.newInsert(new FeedbackDatatype(paketname, autor, message, art, date), 0);
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
