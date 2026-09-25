package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdapterDescuentoFicc extends BaseAdapter  {

    Context context;
    SDTDescuentosFicc[] SDTDescuentosFicc;

    public ListViewAdapterDescuentoFicc(Context context, SDTDescuentosFicc[] SDTDescuentosFicc) {
        this.context = context;
        this.SDTDescuentosFicc = SDTDescuentosFicc;
    }

    @Override
    public int getCount() {
        return SDTDescuentosFicc.length;
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
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.listadescuentos,parent, false);

         TextView Descuento = (TextView) itemView.findViewById(R.id.Descuento);
        TextView secuencia = (TextView) itemView.findViewById(R.id.secuencia);
        TextView escala = (TextView) itemView.findViewById(R.id.escala);
        //TextView txt_valfactura = (TextView) itemView.findViewById(R.id.txt_valfactura);

        try {

            secuencia.setText(SDTDescuentosFicc[position].DesoBonsec+". ");
            Descuento.setText(SDTDescuentosFicc[position].DesoBonDescr);
            escala.setText(SDTDescuentosFicc[position].Escala);



            //txt_valfactura.setText(SDTCartera[position].FacSaldo);
        }catch (Exception e)
        {

        }


        return itemView;
    }


}
