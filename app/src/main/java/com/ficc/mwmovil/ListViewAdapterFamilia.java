package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ListViewAdapterFamilia extends BaseAdapter {


    Context context;
    Bundle Extras;
    SDTSubGrupo[] SDTSubGrupo;
    SDTProductos[] SDTProductos ;
    ListViewAdapterProductosv2 ListViewAdapterProductosv2;
    String invfamcod;
    public ListViewAdapterFamilia(Context context, SDTSubGrupo[] SDTSubGrupo) {
        this.context=context;
        this.SDTSubGrupo = SDTSubGrupo;
    }



    @Override
    public int getCount() {
        return SDTSubGrupo.length;
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

        View itemView = inflater.inflate(R.layout.familia,parent, false);
        try{
            TextView Nombre = (TextView) itemView.findViewById(R.id.FamNombre);
            TextView Codigo = (TextView) itemView.findViewById(R.id.FamCodigo);
            ListView lista = itemView.findViewById(R.id.articulos);
            Nombre.setText(SDTSubGrupo[position].Nombre);
            Codigo.setText(SDTSubGrupo[position].Codigo);

            final BaseDatos BaseDeDatos;
            BaseDeDatos = new BaseDatos(context, "MantisMovil", null, 5);




        }catch (Exception e){
            int hh=0;
        }

        return itemView;

    }


}