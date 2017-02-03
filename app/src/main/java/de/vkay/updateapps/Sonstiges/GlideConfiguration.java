package de.vkay.updateapps.Sonstiges;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.GlideModule;

/**
 *  Verbessert die Qualität von Bildern, die mit Glide geladen werden übers Netzwerk
 *  Füge folgendes in AndroidManifest unter application hinzu:
 *
 *      <meta-data
            android:name="de.vkay.updateapps.Sonstiges.GlideConfiguration"
            android:value="GlideModule" />
 */
public class GlideConfiguration implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }
}
