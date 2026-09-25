package com.ficc.mwmovil;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ficc.mwmovil.databinding.ActivityConfirmarEnvioConBinding;

public class ConfirmarEnvioCon extends AppCompatActivity {

    private Handler handler ;
    Bundle Extras;
    Button btnenviar,btnenvioalt;
    ProgressBar progreso;
    TextView codigo,pedidos,total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_envio_con);
        getSupportActionBar().hide();
        getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.TrasparenteGris));
        Extras=this.getIntent().getExtras();
        String ConNro = Extras.getString("ConNro");
        int sinEnviar = Extras.getInt("Sinenviar");
        pedidos = findViewById(R.id.pedidos);
        total = findViewById(R.id.total);
        progreso = findViewById(R.id.progreso);
        handler = new Handler();
        btnenviar = findViewById(R.id.btnenviar);
        progreso.setVisibility(View.GONE);



        btnenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnenviar.setVisibility(View.GONE);
                progreso.setVisibility(View.VISIBLE);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String Mensaje = "";
                        Cursor Clientes = null;
                         GestorPedidos gestorPedidos = new GestorPedidos();
                         Mensaje = gestorPedidos.EnviarConsigna(ConfirmarEnvioCon.this,ConNro);
                       Log.e("MEsnaje json ",Mensaje);

                        if(!Mensaje.isEmpty()){

                            Intent intent = new Intent();
                            intent.putExtra("status", 200);
                            intent.putExtra("Mensaje", Mensaje);
                            setResult(RESULT_OK, intent);
                            finish();

                        }else{
                        }
                    }
                }).start();
            }
        });
    }

}