package com.ficc.mwmovil;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.ficc.mwmovil.databinding.ActivityHistoricoVentasBinding;

public class EnvioCartera extends AppCompatActivity {

    private ActivityHistoricoVentasBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_ventas);
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
            String nitsec=Extras.getString("nitsec");
            Integer clisec=Extras.getInt("clisec");

            WebSettings WebSettings= mywebview.getSettings();
            String ulr="http://161.18.225.175:8080/MantisFiccGx2Diagnostimax/wpcarteraclientemovil?NitSec="+nitsec+"&CliSec="+clisec+"&VenCod="+Vendedor;
            Log.e("String we",ulr);

            //        mywebview.getSettings().setSupportZoom(true);       //Zoom Control on web (You don't need this
            //if ROM supports Multi-Touch
            //        mywebview.getSettings().setBuiltInZoomControls(true); //Enable Multitouch if supported by ROM
            //mywebview.setBackgroundColor(Color.parseColor("#FFFFFF"));
            //        mywebview.getSettings().setUseWideViewPort(true);
            //        mywebview.getSettings().setLoadWithOverviewMode(false);

            WebSettings.setJavaScriptEnabled(true);
            mywebview.getSettings().setUseWideViewPort(true);
            mywebview.getSettings().setLoadWithOverviewMode(true);
            mywebview.loadUrl(ulr);
        }catch (Exception e){
            int u=1;
            Log.e("Error we",e.toString());
        }
    }

}