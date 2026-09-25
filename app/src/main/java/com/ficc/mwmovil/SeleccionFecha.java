package com.ficc.mwmovil;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ficc.mwmovil.databinding.ActivitySeleccionFechaBinding;

import java.util.Calendar;

public class SeleccionFecha extends AppCompatActivity {

    Spinner spinnerMes;
    EditText inputAnio;
    Button btnAplicar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_fecha);
        getSupportActionBar().hide();
        CalendarView calendario;
        Button verPedidos ;
        spinnerMes = findViewById(R.id.spinnerMes);
        inputAnio = findViewById(R.id.inputAnio);
        btnAplicar = findViewById(R.id.btnAplicarFecha);
        // Lista de meses
        String[] meses = {"Enero","Febrero","Marzo","Abril","Mayo","Junio",
                "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, meses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMes.setAdapter(adapter);
        // 📌 Obtener mes y año actuales
        Calendar hoy = Calendar.getInstance();
        int mesActual = hoy.get(Calendar.MONTH);   // 0 = Enero
        int anioActual = hoy.get(Calendar.YEAR);

        // 📌 Setear en los campos
        spinnerMes.setSelection(mesActual);  // Spinner en el mes actual
        inputAnio.setText(String.valueOf(anioActual)); // Año actual en EditText

        TextView FechaSel;
        calendario = findViewById(R.id.Calendario);
        FechaSel = findViewById(R.id.FechaSel);
        verPedidos =  findViewById(R.id.verPedidos);
        Time now = new Time();
        now.setToNow();
        Time time = new Time();
        time.setToNow();
        FechaSel.setText( now.format("%Y-%m-%d"));
        calendario.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // Crear un objeto Time




                time.set(now.second, now.minute, now.hour, dayOfMonth, month, year);
                FechaSel.setText( time.format("%Y-%m-%d"));

            }
        });


        btnAplicar.setOnClickListener(v -> {
            int mes = spinnerMes.getSelectedItemPosition(); // 0 = Enero
            String anioTexto = inputAnio.getText().toString();
            int anio =now.year;

            if (!anioTexto.isEmpty()) {
                anio = Integer.parseInt(anioTexto);
            }


                Calendar nuevaFecha = Calendar.getInstance();

                if(anio > 0){
                    nuevaFecha.set(Calendar.YEAR, anio);
                }else{
                    nuevaFecha.set(Calendar.YEAR, now.year);
                }

                nuevaFecha.set(Calendar.MONTH, mes);
                nuevaFecha.set(Calendar.DAY_OF_MONTH, now.monthDay);

                // Setear en CalendarView
                calendario.setDate(nuevaFecha.getTimeInMillis());




        });

        verPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ResumenPedidosDia.class);
                /*i.putExtra("ano", time.year);
                i.putExtra("mes", (time.month));
                i.putExtra("dia", (time.monthDay));*/
                AppGlobals.year = time.year;
                AppGlobals.dayOfMonth =(time.monthDay) ;
                AppGlobals.month = (time.month);
                startActivity(i);
            }
        });

    }

}