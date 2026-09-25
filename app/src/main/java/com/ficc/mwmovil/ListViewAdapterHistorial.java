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

public class ListViewAdapterHistorial extends BaseAdapter  {

    Context context;
    SDTHistorial[] SDTHistorial;

    public ListViewAdapterHistorial(Context context, SDTHistorial[] SDTHistorial) {
        this.context = context;
        this.SDTHistorial = SDTHistorial;
    }

    @Override
    public int getCount() {
        return SDTHistorial.length;
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
        View itemView = inflater.inflate(R.layout.listahistorial,parent, false);

         TextView txt_factura = (TextView) itemView.findViewById(R.id.hFacNro);
        TextView txt_fecha = (TextView) itemView.findViewById(R.id.hFecha);
        ImageView image = (ImageView) itemView.findViewById(R.id.hir);
        TextView txt_valor = (TextView) itemView.findViewById(R.id.hvalor);
        //TextView txt_valfactura = (TextView) itemView.findViewById(R.id.txt_valfactura);

        try {

                txt_factura.setText(SDTHistorial[position].FacNro);
                txt_fecha.setText(SDTHistorial[position].FacFec);
                txt_valor.setText(SDTHistorial[position].FacTotalImpuestos);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), DetalleFactura.class);
                    intent.putExtra("Facsec",SDTHistorial[position].FacSec);
                    context.startActivity(intent);

                }
            });


            //txt_valfactura.setText(SDTCartera[position].FacSaldo);
        }catch (Exception e)
        {

        }


        return itemView;
    }


}
