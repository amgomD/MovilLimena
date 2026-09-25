package com.ficc.mwmovil;

import android.app.Application;
import android.content.res.Configuration;
import android.os.Build;

import java.util.Locale;

public class MiAplicacion extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Fuerza el Locale de Java para toda la aplicación
        Locale locale = Locale.US;
        Locale.setDefault(locale);

        // Fuerza el Locale de Android para los recursos/formateos
        Configuration config = getResources().getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }

        getResources().updateConfiguration(
                config,
                getResources().getDisplayMetrics()
        );
    }
}