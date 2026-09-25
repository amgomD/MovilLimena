package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ListViewAdapterPedidosEnviadosV2 extends BaseAdapter {


    Context context;
    SDTPedidosEnviados[] SDTPedidosEnviados;

    public ListViewAdapterPedidosEnviadosV2(Context context, SDTPedidosEnviados[] SDTPedidosEnviados) {
        this.context=context;
        this.SDTPedidosEnviados = SDTPedidosEnviados;
    }

    @Override
    public int getCount() {
        return SDTPedidosEnviados.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    @SuppressLint("MissingInflatedId")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.listarpedidossuhogar,parent, false);
        try{
         TextView txt_nombrecliente = (TextView) itemView.findViewById(R.id.txt_nombrecliente);
         TextView txt_bodega = (TextView) itemView.findViewById(R.id.txt_bodega);
        TextView txt_nombrenegocio = (TextView) itemView.findViewById(R.id.txt_nombrenegocio);
        TextView txt_pedido = (TextView) itemView.findViewById(R.id.txt_pedido);
        TextView txt_valorpedido = (TextView) itemView.findViewById(R.id.txt_valorpedido);
        //TextView txt_valorpedidoenv = (TextView) itemView.findViewById(R.id.txt_valorpedidoenv);
        //TextView txt_porpedidoenv = (TextView) itemView.findViewById(R.id.txt_porpedidoenv);

        //TextView txt_valorpedidoenvalt = (TextView) itemView.findViewById(R.id.txt_valorpedidoenvalt);
        //TextView txt_porpedidoenvalt = (TextView) itemView.findViewById(R.id.txt_porpedidoenvalt);


            //CheckBox checkBox_enviado = (CheckBox) itemView.findViewById(R.id.checkBox_enviado);
            CheckBox checkBox_envalt = (CheckBox) itemView.findViewById(R.id.checkBox_envalt);


        TextView txt_pedenviado = (TextView) itemView.findViewById(R.id.pedenviado);
        txt_bodega.setText(SDTPedidosEnviados[position].BodNom);
        txt_nombrecliente.setText(SDTPedidosEnviados[position].NombreCliente);
        txt_nombrenegocio.setText(SDTPedidosEnviados[position].NombreNegocio);
        txt_pedido.setText(SDTPedidosEnviados[position].NumeroPedido);
            txt_pedenviado.setText(SDTPedidosEnviados[position].checkenviado);



            if(txt_pedenviado.getText().toString().equalsIgnoreCase("S")){
                checkBox_envalt.setChecked(true);
            }





            Button btn_Emviar = (Button) itemView.findViewById(R.id.button3Env);
            BaseDatos BaseDeDatos;
            BaseDeDatos = new BaseDatos(context, "MantisMovil", null, 5);
            GlobalVariables gGlobalVariables = GlobalVariables.getInstance();
            String vEmpresa=gGlobalVariables.getEmpresa();
            Button btnEnvPen = (Button) itemView.findViewById(R.id.btnEnvPen);
            btnEnvPen.setVisibility(View.GONE);
            LinearLayout contPedAlt =  itemView.findViewById(R.id.contentenvalt);
            contPedAlt.setVisibility(View.GONE);

            if(vEmpresa.equalsIgnoreCase("IBANEZ") || vEmpresa.equalsIgnoreCase("IBANEZPRU" ) || vEmpresa.equalsIgnoreCase("SURTIMARCAS" )) {
                btnEnvPen.setVisibility(View.VISIBLE);

                //Nuevo andres ------------------- envioalt
                Cursor permisoenvalt =  BaseDeDatos.getReadableDatabase().rawQuery("select EnvAlt from usuarios where VenCnt=1  ", null);
                if(permisoenvalt.getCount()> 0){
                    permisoenvalt.moveToFirst();
                    if(permisoenvalt.getString(0).equalsIgnoreCase("S")){
                        btnEnvPen.setText("Enviar");
                        btn_Emviar.setVisibility(View.GONE);
                        contPedAlt.setVisibility(View.VISIBLE);
                    }
                }
                //Envioalt --------------------------------------


            }



        //+'-'+SDTPedidosEnviados[position].sEnviado

        //txt_valorpedido.setText(String.format("%,d",SDTPedidosEnviados[position].ValorPedido.intValue()));
            txt_valorpedido.setText(String.format("%.2f",SDTPedidosEnviados[position].ValorPedido));

        if (!SDTPedidosEnviados[position].sEnviadoExi.equalsIgnoreCase("0.00")) {
            //txt_valorpedidoenv.setText("Sin Exi = " + SDTPedidosEnviados[position].sEnviadoExi);
        }else{

           // txt_valorpedidoenv.setText("");
        }



       if (!SDTPedidosEnviados[position].sEnviado.equalsIgnoreCase("0.00") ){
        //    if(SDTPedidosEnviados[position].checkenviado.equalsIgnoreCase("S") ){ // andres
            //checkBox_enviado.setChecked(true);
         // txt_valorpedidoenv.setText( SDTPedidosEnviados[position].sEnviado);
          double enviado =SDTPedidosEnviados[position].ValorPedidoEnviado ;
          double porcentajeenviado = (enviado/SDTPedidosEnviados[position].ValorPedido)*100;
          int redondeo = (int) Math.round(porcentajeenviado);
          if(redondeo >= 99 && redondeo <= 101){
              redondeo = 100;
          }



         //  txt_porpedidoenv.setText(String.valueOf(redondeo)+"%");

        } else{
           // txt_valorpedidoenv.setText("0");
           //txt_porpedidoenv.setText("0%");
        }


           //Nuevo andres ------------------- envioalt
            if (!SDTPedidosEnviados[position].valenvalt.equalsIgnoreCase("0.00") ){
                //    if(SDTPedidosEnviados[position].checkenviado.equalsIgnoreCase("S") ){ // andres
             //   checkBox_envalt.setChecked(true);
                //txt_valorpedidoenvalt.setText( SDTPedidosEnviados[position].valenvalt);
                double enviado =SDTPedidosEnviados[position].ValorPedidoEnvialt ;
                double porcentajeenviado = (enviado/SDTPedidosEnviados[position].ValorPedido)*100;
                int redondeo = (int) Math.round(porcentajeenviado);
                if(redondeo >= 99 && redondeo <= 101){
                    redondeo = 100;
                }

               // txt_porpedidoenvalt.setText(String.valueOf(redondeo)+"%");

            } else{
                //txt_valorpedidoenvalt.setText("0");
               // txt_porpedidoenvalt.setText("0%");
            }
            // Envio alt ---------------------------------







btn_Emviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String Mensaje = "";
                        try {
                            final GestorPedidos GestorPedidos = new GestorPedidos();
                            ConBd conbd = new ConBd();
                            conbd.Variables();
                            String MantisFicc = conbd.MantisFicc;
                            GlobalVariables gGlobalVariables = GlobalVariables.getInstance();
                            String vEmpresa=gGlobalVariables.getEmpresa();
                                if (MantisFicc.equalsIgnoreCase("S")) {
                                    Mensaje = GestorPedidos.EnviarPedidosFicc(context, "", SDTPedidosEnviados[position].sNitSec, 0, 0);
                                }
                                else{
                                    Mensaje = GestorPedidos.EnviarPedidos(context, "", SDTPedidosEnviados[position].sNitSec, SDTPedidosEnviados[position].clisec, 0,"");
                                }
                                AlertDialog.Builder Alerta = new AlertDialog.Builder(context);
                                Alerta.setMessage("Enviado " + Mensaje);
                                Alerta.setTitle("Notificacion");
                                Alerta.setPositiveButton("OK", null);
                                Alerta.setCancelable(true);
                                Alerta.create().show();
                                notifyDataSetChanged();
                        } catch (Exception e) {
                            int aa = 0;
                            e.printStackTrace();
                        }


                }
            });


btnEnvPen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String Mensaje = "";

                        try {
                            final GestorPedidos GestorPedidos = new GestorPedidos();
                            ConBd conbd = new ConBd();
                            conbd.Variables();
                            String MantisFicc = conbd.MantisFicc;
                            GlobalVariables gGlobalVariables = GlobalVariables.getInstance();
                            String vEmpresa=gGlobalVariables.getEmpresa();
                            if(vEmpresa.equalsIgnoreCase("IBANEZ") || vEmpresa.equalsIgnoreCase("IBANEZPRU") || vEmpresa.equalsIgnoreCase("SURTIMARCAS") ) {

                                Intent intent = new Intent(context, ConfirmarEnvio.class);
                                intent.putExtra("total", 1);
                                intent.putExtra("clisec",SDTPedidosEnviados[position].clisec);
                                intent.putExtra("nitsec",SDTPedidosEnviados[position].sNitSec);
                                intent.putExtra("artsec","");
                                intent.putExtra("prefijo",SDTPedidosEnviados[position].prefijo);
                                intent.putExtra("position",position);
                                intent.putExtra("envioalt",1);
                                ((Wp_ResumenPedidosnew) context).startActivityForResult(intent, 1600);

                            }

                        } catch (Exception e) {
                            int aa = 0;
                            e.printStackTrace();
                        }


                }
            });

 Button btn_ver = (Button) itemView.findViewById(R.id.button4ver);
btn_ver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

                        Intent i = new Intent(context, ResumenPedidoEnvV2.class);
                        i.putExtra("nitsec", SDTPedidosEnviados[position].sNitSec);
                        i.putExtra("invfamcod", "");
                        i.putExtra("invsubgrucod", "");
                        i.putExtra("Numped",SDTPedidosEnviados[position].NumeroPedido);
                        i.putExtra("invgrucod", "");
                        i.putExtra("clisec", SDTPedidosEnviados[position].clisec);
                        i.putExtra("lisprecod", "");
                        i.putExtra("prefijo", SDTPedidosEnviados[position].prefijo);
                        Log.e("Entro","dddddddddddddddddddddddd");

                        context.startActivity(i);
                    }catch (Exception e){
                        Log.e("ErrorVer",e.toString());
                        int aa=0;
                    }
                }
            });

            Button btnwhatsap = (Button) itemView.findViewById(R.id.btnwhatsap);
            btnwhatsap.setVisibility(View.VISIBLE);

            btnwhatsap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final GestorPedidos GestorPedidos = new GestorPedidos();


                    Intent sendIntent = new Intent("android.intent.action.MAIN");
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setType("*/*");

                    String mensaje =  GestorPedidos.EnviarPedidosTexto(context, "", SDTPedidosEnviados[position].sNitSec, SDTPedidosEnviados[position].clisec, 0);


                    sendIntent.putExtra(Intent.EXTRA_TEXT,mensaje);


                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                        android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setText(mensaje);
                    } else {
                        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", mensaje);
                        clipboard.setPrimaryClip(clip);
                    }

                    sendIntent.setPackage("com.whatsapp");
                    try{
                        context.startActivity(sendIntent);
                    }catch (Exception e){
                        try {
                            sendIntent.setPackage("com.whatsapp.w4b");
                            context.startActivity(sendIntent);
                        }catch (Exception ee){
                            Toast.makeText(context, "Whatsapp no se encuentra instalado.", Toast.LENGTH_LONG).show();

                        }
                    }

                }
            });

       //
    }catch (Exception e){
            int hh=0;
        }

        return itemView;

    }


}
