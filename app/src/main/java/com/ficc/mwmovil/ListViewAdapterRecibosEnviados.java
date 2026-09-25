package com.ficc.mwmovil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListViewAdapterRecibosEnviados extends BaseAdapter {


    Context context;
    SDTPedidosEnviados[] SDTPedidosEnviados;

    public ListViewAdapterRecibosEnviados(Context context, SDTPedidosEnviados[] SDTPedidosEnviados) {
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.listarrecibos,parent, false);
        LinearLayout contenedorlista = (LinearLayout) itemView.findViewById(R.id.contenedorlista);
        TextView txt_nombrecliente = (TextView) itemView.findViewById(R.id.txt_nombrecliente);
        TextView txt_nombrenegocio = (TextView) itemView.findViewById(R.id.txt_nombrenegocio);
        TextView txt_pedido = (TextView) itemView.findViewById(R.id.txt_pedido);
        TextView txt_valorpedido = (TextView) itemView.findViewById(R.id.txt_valorpedido);
        TextView txt_valorpedidoenv = (TextView) itemView.findViewById(R.id.txt_valorpedidoenv);
        CheckBox checkBox_enviado = (CheckBox) itemView.findViewById(R.id.checkBox_enviado);
        Button btn_eliminar = (Button) itemView.findViewById(R.id.btn_eliminar);
        txt_nombrecliente.setText(SDTPedidosEnviados[position].NombreCliente);
        txt_nombrenegocio.setText(SDTPedidosEnviados[position].NombreNegocio);
        txt_pedido.setText(SDTPedidosEnviados[position].NumeroPedido);

        //+'-'+SDTPedidosEnviados[position].sEnviado
        if(SDTPedidosEnviados[position].ValorPedido == 0){
            contenedorlista.setVisibility(View.GONE);
        }
        txt_valorpedido.setText(String.format("%,d",SDTPedidosEnviados[position].ValorPedido.intValue()));
        if (!SDTPedidosEnviados[position].sEnviadoExi.equalsIgnoreCase("0.00")) {
            txt_valorpedidoenv.setText("Sin Exi = " + SDTPedidosEnviados[position].sEnviadoExi);
        }else{
            txt_valorpedidoenv.setText("");
        }

        if (!SDTPedidosEnviados[position].sEnviado.equalsIgnoreCase("0.0") ){
            checkBox_enviado.setChecked(true);
            txt_valorpedidoenv.setText(SDTPedidosEnviados[position].sEnviado);
           // txt_valorpedidoenv.setText(String.format("%,d", SDTPedidosEnviados[position].sEnviadoExi));
            //((CheckBox) checkBox_enviado).setChecked(SDTPedidosEnviados[position].Enviado);
        } else{
            txt_valorpedidoenv.setText("0");
        }
       //
        btn_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseDatos BaseDeDatos;
                BaseDeDatos = new BaseDatos(context, "MantisMovil", null, 6);
                String consulta = "delete from Recibo where nitsec ='"+SDTPedidosEnviados[position].sNitSec+"' and clisec = "+SDTPedidosEnviados[position].clisec+" ";
                AlertDialog.Builder Alerta = new AlertDialog.Builder(context);
                Alerta.setMessage("Eliminar recibo?");
                Alerta.setTitle("Notificacion");
                Alerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {BaseDeDatos.getWritableDatabase().execSQL(consulta);

                    SDTPedidosEnviados[position].ValorPedido = 0.0;
                        notifyDataSetChanged();
                    }
                });
                Alerta.setCancelable(true);
                Alerta.create().show();
                notifyDataSetChanged();
            }
        });

        return itemView;

    }
}
