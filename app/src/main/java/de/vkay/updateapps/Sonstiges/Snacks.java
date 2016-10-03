package de.vkay.updateapps.Sonstiges;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.StaticLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.Toast;

import de.vkay.updateapps.R;

public class Snacks {

    public static final int SHORT = -1;
    public static final int LONG = 0;

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

    public static Snackbar ShowSnack(Context context, View view, String text, int duration, int backgroundColor, int actionColor) {
        Snackbar snackbar = Snackbar.make(view, text, duration);

        if (backgroundColor != 0) {
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, backgroundColor));
        }

        if (actionColor != 0) {
            snackbar.setActionTextColor(ContextCompat.getColor(context, actionColor));
        }

        return snackbar;
    }
}
