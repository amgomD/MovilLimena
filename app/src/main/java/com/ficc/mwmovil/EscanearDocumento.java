package com.ficc.mwmovil;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

//import com.google.zxing.BarcodeFormat;
//import com.google.zxing.Result;


//import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class EscanearDocumento extends AppCompatActivity{ // implements ZXingScannerView.ResultHandler
   // private ZXingScannerView EscanerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escanear_documento);

      //  if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) { ActivityCompat.requestPermissions(EscanearDocumento.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},100); }

    }

    public void EscanerQR(View view){
        try {


          /*  if(view!= null){
                ((ViewGroup)view.getParent()).removeView(view); // <- fix
            }
           // Collection<BarcodeFormat> BarcodeFormat= EscanerView.getFormats();
            EscanerView = new ZXingScannerView(this);
            setContentView(EscanerView);
            EscanerView.setResultHandler(this);
            //EscanerView.set
            ArrayList<BarcodeFormat> supported = new ArrayList<BarcodeFormat>();
            supported.add(BarcodeFormat.PDF_417);
            //supported.add(BarcodeFormat.DATA_MATRIX);

            EscanerView.setFormats(supported);
            //BarcodeFormat

            EscanerView.setShouldScaleToFill(true);


            EscanerView.startCamera();*/
        }catch (Exception e){
            int hh=0;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
       // EscanerView.stopCamera();
    }

/*
    @Override
    public void handleResult(Result result) {
        android.support.v7.app.AlertDialog.Builder Alerta = new android.support.v7.app.AlertDialog.Builder(this);
        Alerta.setMessage(result.getText());
        Alerta.setTitle("Alerta");
        Alerta.setPositiveButton("OK", null);
        Alerta.setCancelable(true);
        Alerta.create().show();
        EscanerView.resumeCameraPreview(this);
        EscanerView.stopCamera();
    }*/
}


//2020-10-01 21:20:12.183 1903-1947/com.mantiswebnew.movil E/Adreno-GSL: <gsl_memory_alloc_pure:2236>: GSL MEM ERROR: kgsl_sharedmem_alloc ioctl failed.
