package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintJob;
import android.print.PrintManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.BitSet;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.widget.EditText;

import java.util.Set;
import java.util.UUID;

public class ImprimirRecibo extends AppCompatActivity {
    PrintAttributes oldAttributes;
    PrintAttributes newAttributes;
    CancellationSignal cancellationSignal;
    PrintDocumentAdapter.LayoutResultCallback callback;
    Bundle metadata;
    SDTRecibo[] SDTRecibo ;
    ListViewAdapterRecibo ListViewAdapterRecibo;
    Bundle Extras;
    WebView webView;
    String dirpath;
    int dHe;
    int dWi;

    TextView myLabel;

    // will enable user to enter any text to be printed
    EditText myTextbox;

    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    String Imp="";
    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    String nitsec;
    Integer clisec;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    BitSet dots;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imprimir_recibo);

        Extras=this.getIntent().getExtras();
         nitsec=Extras.getString("nitsec");
         clisec=Extras.getInt("clisec");


        try {
            //Button openButton = (Button) findViewById(R.id.open);
            //Button sendButton = (Button) findViewById(R.id.send);
            //Button closeButton = (Button) findViewById(R.id.close);

// text label and input box
            myLabel = (TextView) findViewById(R.id.textView70a);
            myTextbox = (EditText) findViewById(R.id.editTextTextPersonName);
           //  more codes will be here
        }catch(Exception e) {
            e.printStackTrace();
        }



        //verifyStoragePermissions(this);

//        GestorCartera gestorCartera = new GestorCartera();
        //      gestorCartera.TotalesCatera(getApplicationContext(),nitsec,clisec);

        //      txt_carteragen.setText(gestorCartera.CarteraVendedor);

        BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);

        Cursor SqlEmpresa = BaseDeDatos.getWritableDatabase().rawQuery("select ifnull(EmpImpre,'') from EmpresaMovil ", null); //order by nombre
        if (SqlEmpresa.getCount() > 0) {
            int vuelta = 0;
            SqlEmpresa.moveToFirst();
            do {
                Imp = SqlEmpresa.getString(0);
            } while (SqlEmpresa.moveToNext());
        }

        Time time = new Time();
        time.setToNow();

        Integer TotalCartera=0;
        Integer TotalAbonos=0;
        try {
            Cursor cursor = BaseDeDatos.getWritableDatabase().rawQuery("select p.NitSec,p.CliSec,c.NitCom,c.CliNom,facnro,ifnull(sum(abono),0) abono,ifnull(sum(retefue),0) retfue,ifnull(sum(retica),0) retica,ifnull(sum(descuento),0) retdes,ifnull(sum(saldo),0) saldo,ifnull(sum(abono-retefue-retica-descuento),0) efectivo,NitIde from recibo p left join clientes c on p.nitsec=c.nitsec and p.clisec=c.clisec " +
                    " where rcyear=" + time.year + " and rcmonth=" + (time.month + 1) + " and rcday=" + time.monthDay + " and ifNULL(abono,0)<>0 and p.nitsec="+nitsec+" and p.clisec="+clisec+" group by p.NitSec,p.CliSec,c.NitCom,c.CliNom,facnro ", null); //order by nombre
            SDTRecibo=new SDTRecibo[cursor.getCount()];
            Integer vuelta=0;

            String NombreClie="";
            String NitClie="";
            Double valAbono=0.0;
            Double valRetencion=0.0;
            Double valReteica=0.0;
            Double valDescuento=0.0;
            Double valTotal=0.0;

            if (cursor.getCount()>0){
                cursor.moveToFirst();
                do {
                    try {
                        valAbono+=cursor.getDouble(5);
                       //valRetencion+=cursor.getDouble(6);
                       // valReteica+=cursor.getDouble(7);
                        valDescuento+=cursor.getDouble(8)+cursor.getDouble(7)+cursor.getDouble(6);
                        valTotal+=cursor.getDouble(10);

                        SDTRecibo SDTReciboItem= new SDTRecibo();
                        SDTReciboItem.FacNro = cursor.getString(4);
                        SDTReciboItem.Saldo=cursor.getDouble(9);
                        SDTReciboItem.Abono = cursor.getDouble(5);
                        SDTReciboItem.Retencion = cursor.getDouble(6);
                        SDTReciboItem.Reteica = cursor.getDouble(7);
                        SDTReciboItem.Descuento = cursor.getDouble(8);
                        SDTReciboItem.Efectivo =cursor.getDouble(10);
                        NombreClie=cursor.getString(2);
                        NitClie=cursor.getString(11);
                        //TotalCartera+=cursor.getInt(7);
                        //TotalAbonos+=cursor.getInt(8);
                        SDTRecibo[vuelta]=SDTReciboItem;
                    }catch (Exception e){
                        Integer Error=1;
                    }
                    vuelta=vuelta+1;
                } while (cursor.moveToNext());
            }

            TextView TotAbono = (TextView) findViewById(R.id.TotAbono);
            TextView TotDescuentos = (TextView) findViewById(R.id.TotDescuentos);
            TextView TotRecibido = (TextView) findViewById(R.id.TotRecibido);
            TextView txtRecibo = (TextView) findViewById(R.id.txtRecibo);
            TextView txtFecha = (TextView) findViewById(R.id.txtFecha);
            TextView txtNitIde = (TextView) findViewById(R.id.txtNitIde);
            TextView txtNombre = (TextView) findViewById(R.id.txtNombre);
            //txtRecibo

            TotAbono.setText(String.format("%,d", valAbono.intValue()));
            TotDescuentos.setText(String.format("%,d", valDescuento.intValue()));
            TotRecibido.setText(String.format("%,d", valTotal.intValue()));

            String NumPed = nitsec+clisec.toString().trim() +  time.year +  (time.month + 1) +  time.monthDay  ;
            String Fecha =  ""+time.year +"-" + (time.month + 1) +"-"+  time.monthDay  ;
            txtRecibo.setText(NumPed);
            txtFecha.setText(Fecha);
            txtNitIde.setText(NitClie);
            txtNombre.setText(NombreClie);

            Button Imprimir = (Button) findViewById(R.id.ImprimirRecibo);
            Imprimir.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View view) {
                if (Imp.isEmpty()){
                    AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                    Alerta.setMessage("Debe configurar una impresora para imprimir");
                    Alerta.setTitle("Alerta");
                    Alerta.setPositiveButton("OK", null);
                    Alerta.setCancelable(true);
                    Alerta.create().show();
                    //txt_existencia.setText("N/D");
                }else{
                    try {
                        layoutToImage(getWindow().getDecorView().findViewById(android.R.id.content));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        imageToPDF();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        findBT();
                        openBT();
                        sendData();
                        closeBT();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                    //mostrarPDF("",getApplicationContext());

                }
            });
            //TextView txt_carteraabonada = (TextView) findViewById(R.id.txt_carteraabonada);
            //txt_carteraabonada.setText(String.format("%,d",TotalAbonos));
           // TextView txt_carteragen = (TextView) findViewById(R.id.txt_carteragen);
           // txt_carteragen.setText(String.format("%,d",TotalCartera));

            final ListView list_clientes = (ListView) findViewById(R.id.ListViewRecibo);
            ListViewAdapterRecibo = new ListViewAdapterRecibo(this, SDTRecibo);
            list_clientes.setAdapter(ListViewAdapterRecibo);

        }catch (Exception e){
            Integer Error=1;
        }


    }

    void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(mBluetoothAdapter == null) {
                myLabel.setText("No bluetooth adapter available");
            }
            if(!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if(pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // RPP300 is the name of the bluetooth printer device
                    // we got this name from the list of paired devices
                    if (device.getName().equalsIgnoreCase(Imp)) {  //"Printer_E76E"
                        mmDevice = device;
                        break;
                    }
                }
            }
            myLabel.setText("Bluetooth device found.");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    void openBT() throws IOException {
        try {
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();

            myLabel.setText("Bluetooth Opened");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                                myLabel.setText(data);
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void sendData() throws IOException {

      // String msg = myTextbox.getText().toString();
       // msg += "\n";

        TextView TotAbono = (TextView) findViewById(R.id.TotAbono);
        TextView TotDescuentos = (TextView) findViewById(R.id.TotDescuentos);
        TextView TotRecibido = (TextView) findViewById(R.id.TotRecibido);
        TextView txtRecibo = (TextView) findViewById(R.id.txtRecibo);
        TextView txtFecha = (TextView) findViewById(R.id.txtFecha);
        TextView txtNitIde = (TextView) findViewById(R.id.txtNitIde);
        TextView txtNombre = (TextView) findViewById(R.id.txtNombre);

        String msg;
        msg= "900566739 - GELVEZ DISTRIBUCIONES S.A.A";
        msg += "\n";
        msg+= "Recibo:"+txtRecibo.getText().toString()+" Fecha:"+txtFecha.getText().toString();
        msg += "\n";
        msg+= "Nit:"+txtNitIde.getText().toString()+" txtNombre:"+txtNombre.getText().toString();
        msg += "\n";
        msg+= "-----------------------------";
        msg += "\n";
        msg+= "Abono Total:     "+TotAbono.getText().toString();
        msg += "\n";
        msg+= "Abono Descuentos:"+TotAbono.getText().toString();
        msg += "\n";
        msg+= "Recibido:        "+TotRecibido.getText().toString();
        msg += "\n";
        //msg= "Factura:"+TotRecibido.getText().toString();

        BaseDatos BaseDeDatos;
        BaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);

        Time time = new Time();
        time.setToNow();
        try {
            Cursor cursor = BaseDeDatos.getWritableDatabase().rawQuery("select p.NitSec,p.CliSec,c.NitCom,c.CliNom,facnro,ifnull(sum(abono),0) abono,ifnull(sum(retefue),0) retfue,ifnull(sum(retica),0) retica,ifnull(sum(descuento),0) retdes,ifnull(sum(saldo),0) saldo,ifnull(sum(abono-retefue-retica-descuento),0) efectivo,NitIde from recibo p left join clientes c on p.nitsec=c.nitsec and p.clisec=c.clisec " +
                    " where rcyear=" + time.year + " and rcmonth=" + (time.month + 1) + " and rcday=" + time.monthDay + " and ifNULL(abono,0)<>0 and p.nitsec="+nitsec+" and p.clisec="+clisec+" group by p.NitSec,p.CliSec,c.NitCom,c.CliNom,facnro ", null); //order by nombre
            SDTRecibo=new SDTRecibo[cursor.getCount()];
            Integer vuelta=0;

            String NombreClie="";
            String NitClie="";
            Double valAbono=0.0;
            Double valRetencion=0.0;
            Double valReteica=0.0;
            Double valDescuento=0.0;
            Double valTotal=0.0;

            if (cursor.getCount()>0){
                cursor.moveToFirst();
                do {
                    try {
                        valAbono+=cursor.getDouble(5);
                        //valRetencion+=cursor.getDouble(6);
                        // valReteica+=cursor.getDouble(7);
                        valDescuento+=cursor.getDouble(8)+cursor.getDouble(7)+cursor.getDouble(6);
                        valTotal+=cursor.getDouble(10);

                        SDTRecibo SDTReciboItem= new SDTRecibo();
                        SDTReciboItem.FacNro = cursor.getString(4);
                        SDTReciboItem.Saldo=cursor.getDouble(9);
                        SDTReciboItem.Abono = cursor.getDouble(5);
                        SDTReciboItem.Retencion = cursor.getDouble(6);
                        SDTReciboItem.Reteica = cursor.getDouble(7);
                        SDTReciboItem.Descuento = cursor.getDouble(8);
                        SDTReciboItem.Efectivo =cursor.getDouble(10);
                        NombreClie=cursor.getString(2);
                        NitClie=cursor.getString(11);

                        msg += "\n";

                        msg+= "Factura: "+SDTReciboItem.FacNro+" Saldo:"+String.format("%,d", SDTReciboItem.Saldo.intValue())+" Abono: "+String.format("%,d", SDTReciboItem.Abono.intValue());
                        msg += "\n";
                        if (SDTReciboItem.Reteica!=0  ||   SDTReciboItem.Retencion!=0 )
                        {
                            msg+= "Retencion: "+SDTReciboItem.Retencion+" Reteica:"+SDTReciboItem.Reteica;
                            msg += "\n";
                        }
                        if ( SDTReciboItem.Descuento!=0  )
                        {
                            msg+= "Descuento:"+SDTReciboItem.Descuento;
                            msg += "\n";
                        }
                        msg+= "RECIBIDO:"+SDTReciboItem.Efectivo;
                        msg += "\n";
                        msg += "\n";


                    }catch (Exception e){
                        Integer Error=1;
                    }
                    vuelta=vuelta+1;
                } while (cursor.moveToNext());
            }
            msg+= "-----------------------------";
            msg += "\n";
           // msg += "\n";
           // msg += "\n";
          //  msg += "\n";
        //    msg += "\n";
            byte[] FEED_PAPER_AND_CUT = {0x1D, 0x56, 66, 0x00};

            mmOutputStream.write(msg.getBytes());
            mmOutputStream.write(FEED_PAPER_AND_CUT);

        //mmOutputStream.write('A');
        //myLabel.setText("Data Sent");

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            myLabel.setText("Bluetooth Closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void layoutToImage(View view) {
        // get view group using reference
        FrameLayout relativeLayout = (FrameLayout) view.findViewById(R.id.FramePrincipal);
        //LinearLayout relativeLayout = (LinearLayout) view.findViewById(R.id.LayautPrin);
        // convert view group to bitmap
        relativeLayout.setDrawingCacheEnabled(true);
        relativeLayout.buildDrawingCache();
        Bitmap bm = relativeLayout.getDrawingCache();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
      //  bm.getHeight();
      //  bm.getWidth();
        int dHe= bm.getHeight();
        int dWi=bm.getWidth();
//Environment.getExternalStorageDirectory()
        //Environment.getRootDirectory();
        //Environment.getExternalStorageDirectory().toString()
      /*  OutputStream fOut = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Uri outputFileUri;
        File sdImageMainDirectory = null;
        try {
            File root = new File(Environment.getExternalStorageDirectory()+"/MantisWeb/");
            root.mkdirs();
            File ruta_sd = Environment.getExternalStorageDirectory();
            File nuevaCarpeta = new File(ruta_sd.getAbsolutePath(), "MantisWeb");
            sdImageMainDirectory = new File(nuevaCarpeta, "image.jpg");
            outputFileUri = Uri.fromFile(sdImageMainDirectory);
            fOut = new FileOutputStream(sdImageMainDirectory);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();
        }*/
        try {
            File root = new File(Environment.getExternalStorageDirectory()+"/Mantis/");
            root.mkdirs();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();
        }
        File f = new File( Environment.getExternalStorageDirectory()+"/Mantis/"+ File.separator + "image.jpg");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void imageToPDF() throws FileNotFoundException {
        File f = new File( Environment.getExternalStorageDirectory()+"/Mantis/"+ File.separator + "NewPDF.jpg");
        try {
            f.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Document document = new Document();
            Image img = Image.getInstance(Environment.getExternalStorageDirectory() + File.separator+"/Mantis/"+ "image.jpg");
            document.setPageSize(new Rectangle( img.getWidth(),img.getHeight()));
            dirpath = android.os.Environment.getExternalStorageDirectory().toString();
            PdfWriter.getInstance(document, new FileOutputStream(dirpath + "/Mantis/NewPDF.pdf")); //  Change pdf's name.

            document.open();


            float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                    - document.rightMargin() - 0) / img.getWidth()) * 100;
            img.scalePercent(scaler);
            img.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
            //Rectangle rec=new Rectangle()
            //document.setPageSize()

            document.add(img);
            //document.
            document.close();
            Toast.makeText(this, "PDF Generated successfully!.." +dirpath + "/Mantis/NewPDF.pdf", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @SuppressLint("WrongViewCast")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void mostrarPDF(String nombPdf, Context context) {
        webView =(WebView) findViewById(R.id.WebViewPdf);
        try {


            try {

                // Get a PrintManager instance
                PrintManager printManager = (PrintManager)    this.getSystemService(Context.PRINT_SERVICE);

// Get a print adapter instance and wrap it in my own adapter
                PrintDocumentAdapter printAdapter = new PdfDocumentAdapter(getApplicationContext(),dirpath + "/Mantis/NewPDF.pdf" );
                //PrintDocumentAdapter printAdapter = new PrintDocumentAdapterWrapper(webView.createPrintDocumentAdapter());

// Create a print job with name and adapter instance
                //String jobName = getString(R.string.app_name) + " Document";
                PrintAttributes.Builder builder = new PrintAttributes.Builder();
                //builder.setMediaSize(PrintAttributes.MediaSize.NA_LETTER);
                Image img = Image.getInstance(Environment.getExternalStorageDirectory() + File.separator+"/Mantis/"+ "image.jpg");


                PrintAttributes.MediaSize MediaSize= new PrintAttributes.MediaSize("123","123",(int) img.getWidth(),(int) img.getHeight());
                builder.setMediaSize(MediaSize);

                PrintJob printJob = printManager.print("Document", printAdapter,
                        builder.build());
                        //new PrintAttributes.Builder().build());
            } catch (Exception e) {
                throw new RuntimeException("Error generating file", e);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public class PrintDocumentAdapterWrapper extends PrintDocumentAdapter{

        private final PrintDocumentAdapter delegate;
        public PrintDocumentAdapterWrapper(PrintDocumentAdapter adapter){
            super();
            this.delegate = adapter;
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {

        }

        @Override
        public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {

        }

        public void onFinish(){
            delegate.onFinish();
            //insert hook here
        }

        //override all other methods with a trivial implementation calling to the delegate
    }
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public class PdfDocumentAdapter extends PrintDocumentAdapter {

            Context context = null;
            String pathName = "";
            public PdfDocumentAdapter(Context ctxt, String pathName) {
                context = ctxt;
                this.pathName = pathName;
            }
            @Override
            public void onLayout(PrintAttributes printAttributes, PrintAttributes printAttributes1, CancellationSignal cancellationSignal, LayoutResultCallback layoutResultCallback, Bundle bundle) {
                if (cancellationSignal.isCanceled()) {
                    layoutResultCallback.onLayoutCancelled();
                }
                else {
                    PrintDocumentInfo.Builder builder=
                            new PrintDocumentInfo.Builder("NewPDF.pdf");
                    builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                            .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                            .build();
                    layoutResultCallback.onLayoutFinished(builder.build(),
                            !printAttributes1.equals(printAttributes));
                }
            }

            @Override
            public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor parcelFileDescriptor, CancellationSignal cancellationSignal, WriteResultCallback writeResultCallback) {
                InputStream in=null;
                OutputStream out=null;
                try {
                    File file = new File(pathName);
                    in = new FileInputStream(file);
                    out=new FileOutputStream(parcelFileDescriptor.getFileDescriptor());

                    byte[] buf=new byte[16384];
                    int size;

                    while ((size=in.read(buf)) >= 0
                            && !cancellationSignal.isCanceled()) {
                        out.write(buf, 0, size);
                    }

                    if (cancellationSignal.isCanceled()) {
                        writeResultCallback.onWriteCancelled();
                    }
                    else {
                        writeResultCallback.onWriteFinished(new PageRange[] { PageRange.ALL_PAGES });
                    }
                }
                catch (Exception e) {
                    writeResultCallback.onWriteFailed(e.getMessage());
                   // Logger.logError( e);
                }
                finally {
                    try {
                        in.close();
                        out.close();
                    }
                    catch (IOException e) {
                   //     Logger.logError( e);
                    }
                }
            }}

}