package de.vkay.updateapps.Sonstiges;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.Toast;

import de.vkay.updateapps.R;

public class Snacks {

    public static final int SHORT = -1;
    public static final int LONG = 0;

    ViewGroup viewGroup;
    Snackbar snack;

    private static ViewGroup findSuitableParent(View view) {
        ViewGroup fallback = null;
        do {
            if (view instanceof CoordinatorLayout) {
                return (ViewGroup) view;
            } else if (view instanceof FrameLayout) {
                if (view.getId() == android.R.id.content) {
                    return (ViewGroup) view;
                } else {
                    fallback = (ViewGroup) view;
                }
            }

            if (view != null) {
                final ViewParent parent = view.getParent();
                view = parent instanceof View ? (View) parent : null;
            }
        } while (view != null);

        return fallback;
    }

    public void setupVG(String bg){
        viewGroup = (ViewGroup) snack.getView();
        viewGroup.setBackgroundColor(Color.parseColor(bg));
    }

    public void Grey (View view, Context context, String text, int dauer){
        snack = Snackbar.make(findSuitableParent(view), text, dauer);
        setupVG(String.format("#%06X", 0xFFFFFF & ContextCompat.getColor(context, R.color.snack_grey)));

        snack.show();
    }

    public static void toastInBackground(final Context context, final String msg, final int dauer) {
        if (context != null && msg != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context, msg, dauer).show();
                }
            });
        }
    }
}
