package com.ficc.mwmovil;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
//import com.google.android.gms.vision.Frame;
//import com.google.android.gms.vision.barcode.Barcode;
//import com.google.android.gms.vision.barcode.BarcodeDetector;

public class BarcodePDF extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcodepdf);
/*
        ActivityCompat.requestPermissions(BarcodePDF.this, new
                String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 200);

        ImageView myImageView = (ImageView) findViewById(R.id.imgview);
         final Bitmap myBitmap = BitmapFactory.decodeResource(
                getApplicationContext().getResources(),
                R.drawable.puppy);
        myImageView.setImageBitmap(myBitmap);

        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BarcodeDetector detector=null;
                try {
                    detector =new BarcodeDetector.Builder(getApplicationContext()).setBarcodeFormats(Barcode.PDF417).build();

                    //new BarcodeDetector.Builder(getApplicationContext())
                    //      .setBarcodeFormats(Barcode.QR_CODE)
                    //    .build();
                }catch (Exception e){
                    int hh=0;
                }

                TextView txt_mensaje = (TextView) findViewById(R.id.txt_mensaje);

                if(!detector.isOperational()){
                    txt_mensaje.setText("Could not set up the detector!");
                    return;
                }


                Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
                SparseArray<Barcode> barcodes = detector.detect(frame);

                Barcode thisCode = barcodes.valueAt(0);
                TextView txtView = (TextView) findViewById(R.id.txtContent);
                txtView.setText(thisCode.rawValue);

            }
        });
*/


    }
}
