package com.ficc.mwmovil;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Notificacion {

    // =========================
    // ÉXITO
    // =========================
    public static void exito(Context context, String mensaje) {
        mostrar(context, mensaje, Color.rgb(40, 167, 69));
    }

    // =========================
    // ERROR
    // =========================
    public static void error(Context context, String mensaje) {
        mostrar(context, mensaje, Color.rgb(220, 53, 69));
    }

    // =========================
    // AVISO
    // =========================
    public static void aviso(Context context, String mensaje) {
        mostrar(context, mensaje, Color.rgb(255, 165, 7));
    }

    // =========================
    // INFORMACIÓN
    // =========================
    public static void info(Context context, String mensaje) {
        mostrar(context, mensaje, Color.rgb(0, 123, 255));
    }

    // =========================
    // MÉTODO GENERAL
    // =========================
    private static void mostrar(Context context, String mensaje, int color) {

        Toast toast = new Toast(context);

        TextView texto = new TextView(context);

        texto.setText(mensaje);
        texto.setTextColor(Color.WHITE);
        texto.setTextSize(15);
        texto.setGravity(Gravity.CENTER);

        texto.setPadding(45, 25, 45, 25);

        GradientDrawable fondo = new GradientDrawable();
        fondo.setColor(color);
        fondo.setCornerRadius(35);

        texto.setBackground(fondo);

        toast.setView(texto);

        toast.setGravity(
                Gravity.TOP | Gravity.CENTER_HORIZONTAL,
                0,
                120
        );

        toast.setDuration(Toast.LENGTH_LONG);

        toast.show();
    }
}