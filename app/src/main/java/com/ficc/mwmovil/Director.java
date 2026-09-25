package com.ficc.mwmovil;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Director extends AppCompatActivity {
    WebView mywebview;
    String ulr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director);
        getSupportActionBar().hide();
        final Bundle Extras=this.getIntent().getExtras();
        mywebview=(WebView)findViewById(R.id.webView);


        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vEmpresa=vGlobalVariables.getEmpresa();
        vEmpresa=vEmpresa.toUpperCase();
        String Vendedor=vGlobalVariables.getUsuario();

        ConBd conbd = new ConBd();
        conbd.Variables();
        String UrlHis=conbd.UrlHistorial;

        WebSettings WebSettings= mywebview.getSettings();
        ulr=UrlHis+"com.version8.movventassuc"; //?"+Extras.getString("nitsec")+","+Extras.getInt("clisec")+","+Vendedor;
        // String ulr="http://190.144.161.250:8080/MantisWeb20apps/servlet/com.version8.wpmovrepultven?"+Extras.getString("nitsec")+","+Extras.getInt("clisec");
        WebSettings.setJavaScriptEnabled(true);
        mywebview.setWebViewClient(new WebViewClient());
        mywebview.loadUrl(ulr);
    }

    @Override
    public void onBackPressed() {

        if (mywebview.getUrl() != null && mywebview.getUrl().equals(ulr)){
            super.onBackPressed();
            //finish();
        }else{
            mywebview.goBack();
        }


    }
}