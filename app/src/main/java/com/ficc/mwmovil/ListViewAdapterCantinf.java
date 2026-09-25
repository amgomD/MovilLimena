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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListViewAdapterCantinf extends BaseAdapter  {

    Context context;
    SDTArticuloinf[] SDTArticuloinf;

    public ListViewAdapterCantinf(Context context, SDTArticuloinf[] SDTArticuloinf) {
        this.context = context;
        this.SDTArticuloinf = SDTArticuloinf;
    }

    @Override
    public int getCount() {
        return SDTArticuloinf.length;
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
        View itemView = inflater.inflate(R.layout.listacantidades,parent, false);

        TextView Cantidad = (TextView) itemView.findViewById(R.id.Cantidad);
        TextView Cantidadin = (TextView) itemView.findViewById(R.id.Cantidadin);
         ImageButton eliminar = (ImageButton) itemView.findViewById(R.id.eliminar);
        LinearLayout contcan = itemView.findViewById(R.id.contcan);

        try {
            Cantidad.setText(SDTArticuloinf[position].Cantidad);
            Cantidadin.setText(SDTArticuloinf[position].CantInf);
            if  (SDTArticuloinf[position].Cantidad != "0"){
                contcan.setVisibility(View.VISIBLE);

            }else{
                contcan.setVisibility(View.GONE);
            }

        }catch (Exception e)
        {
            Log.e("Errolist",e.toString());
        }
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
          String dele = "Delete from  PedidoInf where prefijo='"+SDTArticuloinf[position].Prefijo+"' and nitsec='"+SDTArticuloinf[position].Nitsec +"' and clisec='"+ SDTArticuloinf[position].Clisec +"' and artsec='"+SDTArticuloinf[position].ArtSec +"' and secuencia ="+ SDTArticuloinf[position].secuencia+" " ;
          Log.e("SQL",dele);
          final BaseDatos BaseDeDatos;
                BaseDeDatos = new BaseDatos(context, "MantisMovil", null, 5);
                BaseDeDatos.getWritableDatabase().execSQL(dele);
                SDTArticuloinf[position].Cantidad = "0";
                notifyDataSetChanged();
                ((EditarCantidadInf)context).actualizartotal( SDTArticuloinf[position].ArtSec);
            }
        });

        return itemView;
    }


}
