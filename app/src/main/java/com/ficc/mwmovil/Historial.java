package com.ficc.mwmovil;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class Historial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);
        getSupportActionBar().hide();
        final Bundle Extras=this.getIntent().getExtras();

        try{
      WebView mywebview=(WebView)findViewById(R.id.webView);

        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vEmpresa=vGlobalVariables.getEmpresa();
        vEmpresa=vEmpresa.toUpperCase();
        String Vendedor=vGlobalVariables.getUsuario();

        ConBd conbd = new ConBd();
        conbd.Variables();
        String UrlHis=conbd.UrlHistorial;

     WebSettings WebSettings= mywebview.getSettings();
     String ulr=UrlHis+"com.version8.wpmovrepultven?"+Extras.getString("nitsec")+","+Extras.getInt("clisec")+","+Vendedor;

    //        mywebview.getSettings().setSupportZoom(true);       //Zoom Control on web (You don't need this
            //if ROM supports Multi-Touch
    //        mywebview.getSettings().setBuiltInZoomControls(true); //Enable Multitouch if supported by ROM
            //mywebview.setBackgroundColor(Color.parseColor("#FFFFFF"));
    //        mywebview.getSettings().setUseWideViewPort(true);
    //        mywebview.getSettings().setLoadWithOverviewMode(false);

     WebSettings.setJavaScriptEnabled(true);
     mywebview.loadUrl(ulr);
    }catch (Exception e){
    int u=1;
    }

    }
}
