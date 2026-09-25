package com.ficc.mwmovil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class ListViewAdapterClientesNuevos extends BaseAdapter {


    Context context;
    SDTClientesNuevos[] SDTClientesNuevos;

    public ListViewAdapterClientesNuevos(Context context, SDTClientesNuevos[] SDTClientesNuevos) {
        this.context=context;
        this.SDTClientesNuevos = SDTClientesNuevos;
    }

    @Override
    public int getCount() {
        return SDTClientesNuevos.length;
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
        View itemView = inflater.inflate(R.layout.listarclientesnuevos,parent, false);

        TextView txt_nit = (TextView) itemView.findViewById(R.id.txt_nit);
        TextView txt_nombre = (TextView) itemView.findViewById(R.id.txt_nombre);
        CheckBox checkBox_enviado = (CheckBox) itemView.findViewById(R.id.checkBox_enviado);
        CheckBox rechazado = (CheckBox) itemView.findViewById(R.id.rechazado);
        CheckBox checkBox_creado = (CheckBox) itemView.findViewById(R.id.checkBox_creado);
        TextView txt_respuesta = (TextView) itemView.findViewById(R.id.txt_respuesta);


        txt_nit.setText(SDTClientesNuevos[position].Nit);
        txt_nombre.setText(SDTClientesNuevos[position].NombreCliente);
        txt_respuesta.setText(SDTClientesNuevos[position].Respuesta);

        //+'-'+SDTPedidosEnviados[position].sEnviado



        if (SDTClientesNuevos[position].sEnviado.equalsIgnoreCase("1.00") ||SDTClientesNuevos[position].sEnviado.equalsIgnoreCase("S")  ){
            checkBox_enviado.setChecked(true);
            //((CheckBox) checkBox_enviado).setChecked(SDTPedidosEnviados[position].Enviado);
        }
        if (SDTClientesNuevos[position].sCreado.equalsIgnoreCase("1.00") || SDTClientesNuevos[position].sCreado.equalsIgnoreCase("S") ){
            checkBox_creado.setChecked(true);
            //((CheckBox) checkBox_enviado).setChecked(SDTPedidosEnviados[position].Enviado);
        }

        if(SDTClientesNuevos[position].sCreado.equalsIgnoreCase("R")){
            rechazado.setChecked(true);
        }

       //


        return itemView;

    }
}
