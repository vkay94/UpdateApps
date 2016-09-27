package de.vkay.updateapps.Willkommen;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Instanz eines Bildschirmes im Willkommensbildschirm
 */
public class WelcomeScreenFragment extends Fragment {
 
    final static String LAYOUT_ID = "layoutId";
 
    public static WelcomeScreenFragment newInstance(int layoutId) {
        WelcomeScreenFragment pane = new WelcomeScreenFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(LAYOUT_ID, layoutId);
        pane.setArguments(bundle);
        return pane;
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getArguments().getInt(LAYOUT_ID, -1), container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}