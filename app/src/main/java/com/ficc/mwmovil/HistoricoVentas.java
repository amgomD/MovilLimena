package com.ficc.mwmovil;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ficc.mwmovil.databinding.ActivityHistoricoVentasBinding;

public class HistoricoVentas extends AppCompatActivity {

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

            WebSettings WebSettings= mywebview.getSettings();
            String ulr="http://161.18.225.175:8080/MantisFiccGx2Diagnostimax/wpconsultarmantis?VenCod="+Vendedor;

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