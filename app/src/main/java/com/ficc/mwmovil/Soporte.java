package com.ficc.mwmovil;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Soporte extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soporte);

        final BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 5);


        final EditText edit_consulta= (EditText)findViewById(R.id.edit_consulta);
        final TextView txt_resultado= (TextView)findViewById(R.id.txt_resultado);
        final EditText edit_consulta2= (EditText)findViewById(R.id.Text12);



        Button btn_ejecutar= (Button)findViewById(R.id.btn_ejecutar);
        btn_ejecutar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    BaseDeDatos.getWritableDatabase().execSQL(edit_consulta.getText().toString().trim()); //order by nombre
                    txt_resultado.setText("Ejecutado con exito");
                    edit_consulta.setText("");
                }catch (Exception e){
                    txt_resultado.setText(e.getMessage().trim());
                }
            }
        });
        Button btn_consultar= (Button)findViewById(R.id.btn_consultar);
        btn_consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Cursor cursor = BaseDeDatos.getWritableDatabase().rawQuery(edit_consulta.getText().toString().trim(),null); //order by nombre
                    String ResultGen = "";
                    if (cursor.getCount() > 0) {
                        int vuelta = 0;

                        cursor.moveToFirst();

                        do {
                            String Result = " {";
                            for (int i = 0; i < cursor.getColumnCount(); i++) {
                                Result += cursor.getString(i) + ',';
                            }
                            Result += "\n";
                            ResultGen += Result+"} ";
                        } while (cursor.moveToNext());

                    }
                    txt_resultado.setText(ResultGen);
                    edit_consulta2.setText(ResultGen);
                }catch (Exception e) {
                    txt_resultado.setText(e.getMessage().trim());
                }
            }
        });
    }
}
