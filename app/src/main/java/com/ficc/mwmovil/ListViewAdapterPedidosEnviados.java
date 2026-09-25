package com.ficc.mwmovil;

import static android.support.v4.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.format.Time;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ListViewAdapterPedidosEnviados extends BaseAdapter {


    Context context;
    SDTPedidosEnviados[] SDTPedidosEnviados;

    public ListViewAdapterPedidosEnviados(Context context, SDTPedidosEnviados[] SDTPedidosEnviados) {
        this.context=context;
        this.SDTPedidosEnviados = SDTPedidosEnviados;
    }
    public String banderadia(){
        String bander = "S";
        try {
            ConBd conbd = new ConBd();
            conbd.Variables();
            String sincroniza = conbd.sincronizalinea;
            Time time = new Time();
            int Anio = 0;
            int mes = 0;
            int Dia = 0;
            int Anioant = 0;
            int mesant = 0;
            int Diaant = 0;
            time.setToNow();
            Connection conn = conbd.CargarConexion(context);


            Statement comm = conn.createStatement();
            String  Script="SELECT YEAR(GETDATE()) AS Anio, MONTH(GETDATE()) AS Mes, DAY(GETDATE()) AS Dia, YEAR(GETDATE()-1) AS Anioant, MONTH(GETDATE()-1) AS Mesant, DAY(GETDATE()-1) AS Diaant;";
           classbd classbd = new classbd();
            ResultSet rsImport = comm.executeQuery(classbd.FormatearMysql(Script));
            while (rsImport.next()) {
                Anio = rsImport.getInt("Anio");
                mes = rsImport.getInt("Mes");
                Dia = rsImport.getInt("Dia");
                Anioant = rsImport.getInt("Anioant");
                mesant = rsImport.getInt("Mesant");
                Diaant = rsImport.getInt("Diaant");
            }

            if(Anio == time.year && mes == (time.month + 1) && Dia == time.monthDay){
                bander = "S";
            }
            if(Anioant == time.year && mesant == (time.month + 1) && Diaant == time.monthDay){
                bander = "S";
            }

            bander = "S";
        } catch (Exception e) {
            Log.e("error",e.toString());
        }


        return bander;
    }
    public Boolean isOnlineNet() {




        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String Empresa=vGlobalVariables.getEmpresa();
        if (!Empresa.equalsIgnoreCase("IBANEZ") || !Empresa.equalsIgnoreCase("SUHOGAR") ){
            return true;
        }else{
            ConBd conbd = new ConBd();
            conbd.Variables();
            try {
                Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 "+conbd.IpEmpresa);
                int val           = p.waitFor();
                boolean reachable = (val == 0);
                return reachable;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return false;
        }



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

        View itemView = inflater.inflate(R.layout.listarpedidos,parent, false);
        try {
            TextView txt_nombrecliente = (TextView) itemView.findViewById(R.id.txt_nombrecliente);
            TextView txt_bodega = (TextView) itemView.findViewById(R.id.txt_bodega);
            TextView txt_nombrenegocio = (TextView) itemView.findViewById(R.id.txt_nombrenegocio);
            TextView txt_pedido = (TextView) itemView.findViewById(R.id.txt_pedido);
            TextView txt_valorpedido = (TextView) itemView.findViewById(R.id.txt_valorpedido);
            TextView txt_valorpedidoenv = (TextView) itemView.findViewById(R.id.txt_valorpedidoenv);
            TextView txt_porpedidoenv = (TextView) itemView.findViewById(R.id.txt_porpedidoenv);

            CheckBox checkBox_enviado = (CheckBox) itemView.findViewById(R.id.checkBox_enviado);
            TextView txt_pedenviado = (TextView) itemView.findViewById(R.id.pedenviado);
            txt_bodega.setText(SDTPedidosEnviados[position].BodNom);
            txt_nombrecliente.setText(SDTPedidosEnviados[position].NombreCliente);
            txt_nombrenegocio.setText(SDTPedidosEnviados[position].NombreNegocio);
            txt_pedido.setText(SDTPedidosEnviados[position].NumeroPedido);
            txt_pedenviado.setText(SDTPedidosEnviados[position].checkenviado);
            String bloqmora = SDTPedidosEnviados[position].Mora;


            GlobalVariables gGlobalVariables = GlobalVariables.getInstance();
            String vEmpresa = gGlobalVariables.getEmpresa();
            Button btnEnvPen = (Button) itemView.findViewById(R.id.btnEnvPen);
            Button btnwhatsap = (Button) itemView.findViewById(R.id.btnwhatsap);
            btnEnvPen.setVisibility(View.GONE);

            LinearLayout contPedAlt = itemView.findViewById(R.id.contentenvalt);
            contPedAlt.setVisibility(View.GONE);
            btnwhatsap.setVisibility(View.GONE);

            if (vEmpresa.equalsIgnoreCase("IBANEZ") || vEmpresa.equalsIgnoreCase("IBANEZPRU") || vEmpresa.equalsIgnoreCase("SURTIMARCAS")) {
                btnEnvPen.setVisibility(View.GONE);
            }
            if (vEmpresa.equalsIgnoreCase("SUHOGAR")) {
                btnwhatsap.setVisibility(View.VISIBLE);
            }


            //+'-'+SDTPedidosEnviados[position].sEnviado

            //txt_valorpedido.setText(String.format("%,d", SDTPedidosEnviados[position].ValorPedido.intValue()));
            txt_valorpedido.setText(String.format("%.2f",SDTPedidosEnviados[position].ValorPedido));
            if (!SDTPedidosEnviados[position].sEnviadoExi.equalsIgnoreCase("0.00")) {
                txt_valorpedidoenv.setText("Sin Exi = " + SDTPedidosEnviados[position].sEnviadoExi);
            } else {

                txt_valorpedidoenv.setText("");
            }
            if (!SDTPedidosEnviados[position].sEnviado.equalsIgnoreCase("0.00")) {
                //    if(SDTPedidosEnviados[position].checkenviado.equalsIgnoreCase("S") ){ // andres
                checkBox_enviado.setChecked(true);
                txt_valorpedidoenv.setText(SDTPedidosEnviados[position].sEnviado);
                double enviado = SDTPedidosEnviados[position].ValorPedidoEnviado;
                double porcentajeenviado = (enviado / SDTPedidosEnviados[position].ValorPedido) * 100;
                int redondeo = (int) Math.round(porcentajeenviado);
                if (redondeo >= 99 && redondeo <= 101) {
                    redondeo = 100;
                }

                txt_porpedidoenv.setText(String.valueOf(redondeo) + "%");

            } else {
                txt_valorpedidoenv.setText("0");
                txt_porpedidoenv.setText("0%");
            }
            Button btn_Emviar = (Button) itemView.findViewById(R.id.button3Env);


            if (vEmpresa.equalsIgnoreCase("DINGLESA")) {
                if (bloqmora.equalsIgnoreCase("S")) {
                    btn_Emviar.setVisibility(View.GONE);
                }
            }


            btn_Emviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String Mensaje = "";

                    if (isOnlineNet()) {
                        if (banderadia().equalsIgnoreCase("S")) {
                            try {
                                final GestorPedidos GestorPedidos = new GestorPedidos();
                                ConBd conbd = new ConBd();
                                conbd.Variables();
                                String MantisFicc = conbd.MantisFicc;
                                GlobalVariables gGlobalVariables = GlobalVariables.getInstance();
                                String vEmpresa = gGlobalVariables.getEmpresa();
                                if (vEmpresa.equalsIgnoreCase("IBANEZ") || vEmpresa.equalsIgnoreCase("IBANEZPRU")
                                        || vEmpresa.equalsIgnoreCase("MENTAHAIR") || vEmpresa.equalsIgnoreCase("MENTAHAIRCOT")
                                        || vEmpresa.equalsIgnoreCase("ACOAVANZAR") || vEmpresa.equalsIgnoreCase("ACOAVANZARPRU")
                                ) {

                                    Intent intent = new Intent(context, ConfirmarEnvio.class);
                                    intent.putExtra("total", 1);
                                    intent.putExtra("clisec", SDTPedidosEnviados[position].clisec);
                                    intent.putExtra("nitsec", SDTPedidosEnviados[position].sNitSec);
                                    intent.putExtra("artsec", "");
                                    intent.putExtra("prefijo", SDTPedidosEnviados[position].prefijo);
                                    ((ResumenPedidosDia) context).startActivityForResult(intent, 3);

                                } else {
                                    if (MantisFicc.equalsIgnoreCase("S")) {
                                        Mensaje = GestorPedidos.EnviarPedidosFiccGx5(context, "", SDTPedidosEnviados[position].sNitSec, 0, 0);
                                    } else {
                                        Log.e("Clisec: ", String.valueOf(SDTPedidosEnviados[position].clisec));
                                        Mensaje = GestorPedidos.EnviarPedidos(context, "", SDTPedidosEnviados[position].sNitSec, SDTPedidosEnviados[position].clisec, 0, "");
                                    }

                                    AlertDialog.Builder Alerta = new AlertDialog.Builder(context);
                                    Alerta.setMessage("Enviado " + Mensaje);
                                    Alerta.setTitle("Notificacion");
                                    Alerta.setPositiveButton("OK", null);
                                    Alerta.setCancelable(true);
                                    Alerta.create().show();
                                    notifyDataSetChanged();
                                }


                            } catch (Exception e) {
                                int aa = 0;
                                e.printStackTrace();
                            }
                        } else {
                            Mensaje = "No tiene permitido enviar, fecha del dispositivo equivocada";
                            AlertDialog.Builder Alerta = new AlertDialog.Builder(context);
                            Alerta.setMessage("Alerta " + Mensaje);
                            Alerta.setTitle("Notificacion");
                            Alerta.setPositiveButton("OK", null);
                            Alerta.setCancelable(true);
                            Alerta.create().show();
                            notifyDataSetChanged();

                        }

                    } else {
                        AlertDialog.Builder Alerta = new AlertDialog.Builder(context, R.style.MyDialogsinconRojo); //
                        Alerta.setMessage("Error de conexón, compruebe su internet");
                        Alerta.setTitle("Error");
                        Alerta.setPositiveButton("OK", null);
                        Alerta.setCancelable(true);
                        Alerta.create().show();
                    }


                }
            });

            btnwhatsap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final GestorPedidos GestorPedidos = new GestorPedidos();


                    Intent sendIntent = new Intent("android.intent.action.MAIN");
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setType("*/*");

                    String mensaje = GestorPedidos.EnviarPedidosTexto(context, "", SDTPedidosEnviados[position].sNitSec, SDTPedidosEnviados[position].clisec, 0);


                    sendIntent.putExtra(Intent.EXTRA_TEXT, mensaje);


                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                        android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setText(mensaje);
                    } else {
                        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", mensaje);
                        clipboard.setPrimaryClip(clip);
                    }

                    sendIntent.setPackage("com.whatsapp");
                    try {
                        context.startActivity(sendIntent);
                    } catch (Exception e) {
                        try {
                            sendIntent.setPackage("com.whatsapp.w4b");
                            context.startActivity(sendIntent);
                        } catch (Exception ee) {
                            Toast.makeText(context, "Whatsapp no se encuentra instalado.", Toast.LENGTH_LONG).show();

                        }
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
                                intent.putExtra("envioalt",1);
                                intent.putExtra("artsec","");
                                intent.putExtra("prefijo",SDTPedidosEnviados[position].prefijo);
                                ((ResumenPedidosDia) context).startActivityForResult(intent, 1600);

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
                        Intent i = new Intent(context, ResumenPedidoEnv.class);

                        i.putExtra("nitsec", SDTPedidosEnviados[position].sNitSec);
                        i.putExtra("invfamcod", "");
                        i.putExtra("invsubgrucod", "");
                        i.putExtra("Numped",SDTPedidosEnviados[position].NumeroPedido);
                        i.putExtra("invgrucod", "");
                        i.putExtra("clisec", SDTPedidosEnviados[position].clisec);
                        i.putExtra("lisprecod", SDTPedidosEnviados[position].LisPreCod);
                        i.putExtra("prefijo", SDTPedidosEnviados[position].prefijo);

                        /*i.putExtra("ano", SDTPedidosEnviados[position].SelAno);
                        i.putExtra("mes", SDTPedidosEnviados[position].selMes);
                        i.putExtra("dia", SDTPedidosEnviados[position].selDia);
*/
                        context.startActivity(i);
                    }catch (Exception e){
                        int aa=0;
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
