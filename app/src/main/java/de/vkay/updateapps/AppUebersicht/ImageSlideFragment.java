package de.vkay.updateapps.AppUebersicht;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import de.vkay.updateapps.Datenspeicher.SharedPrefs;
import de.vkay.updateapps.R;

public class ImageSlideFragment extends Fragment {

    public static ImageSlideFragment newInstance(String s) {

        ImageSlideFragment fragmentUbersicht = new ImageSlideFragment();
        Bundle b = new Bundle();
        b.putString("a", s);
        fragmentUbersicht.setArguments(b);

        return fragmentUbersicht;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.imageslider_image, container, false);

        Bundle bund = getArguments();
        ImageView image = (ImageView) rootView.findViewById(R.id.imageslider_imageview);

        Glide.with(this)
                .load(bund.getString("a"))
                .crossFade()
                .signature(new StringSignature(new SharedPrefs(getActivity()).getImageSetVersion()))
                .into(image);

        return rootView;
    }
}
