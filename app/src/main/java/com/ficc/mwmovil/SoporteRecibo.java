package com.ficc.mwmovil;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class SoporteRecibo extends AppCompatActivity {
    private final String ruta_fotos = Environment.getExternalStorageDirectory().toString() + "/MantisWeb/";
    Uri uri;
    File mi_foto;
    String rrfile;
    Bundle Extras=null;
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soporte_recibo);

        Button Capturar= (Button) findViewById(R.id.Capturar);
        Capturar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    File filedir=new File(ruta_fotos); if(!filedir.exists()) filedir.mkdirs();
                    rrfile = ruta_fotos +"mwapp.jpg";
                    mi_foto=new File(ruta_fotos, "mwapp.jpg");
                    imageUri = Uri.fromFile(mi_foto);
                    uri = Uri.fromFile(mi_foto);
                    String fileName = "mwapp.jpg";
                    // Create parameters for Intent with filename
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, fileName);
                    values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by camera");
                    imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    //Abre la camara para tomar la foto
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //Guarda imagen
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    //Retorna a la actividad
                    startActivityForResult(cameraIntent, 1);

                }catch (Exception e){
                    String aaaa = "mwapp.jpg";
                }
            }
        });
    }
}