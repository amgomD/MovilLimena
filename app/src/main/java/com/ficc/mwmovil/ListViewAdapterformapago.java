package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ListViewAdapterformapago extends BaseAdapter  {

    Context context;
    SDTFormaPago[] SDTFormaPago;

    public ListViewAdapterformapago(Context context, SDTFormaPago[] SDTFormaPago) {
        this.context = context;
        this.SDTFormaPago = SDTFormaPago;
    }

    @Override
    public int getCount() {
        return SDTFormaPago.length;
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
        View itemView = inflater.inflate(R.layout.listarformapago,parent, false);

        TextView formapago = (TextView) itemView.findViewById(R.id.formapago);
        TextView Valor = (TextView) itemView.findViewById(R.id.Valor);
        //    Button eliminarfp = (Button) itemView.findViewById(R.id.eliminarfp);

     /*   eliminarfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseDatos BaseDeDatos;
                BaseDeDatos = new BaseDatos(context, "MantisMovil", null, 6);

                String consulta = "delete from Reciboforma where nitsec='" + SDTFormaPago[position].NitSec + "' and clisec=" + SDTFormaPago[position].clisec + " and facnro ='" + SDTFormaPago[position].facnro + "'  ";
                BaseDeDatos.getWritableDatabase().execSQL(consulta);


            }
        });

      */




        try {
            formapago.setText(SDTFormaPago[position].tipo);
            Valor.setText(String.format("%,d",SDTFormaPago[position].valor.intValue()));

        }catch (Exception e)
        {
            Log.e("Errolist",e.toString());
        }
        return itemView;
    }


}
