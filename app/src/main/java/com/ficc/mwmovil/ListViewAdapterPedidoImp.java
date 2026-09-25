package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ListViewAdapterPedidoImp extends BaseAdapter {


    Context context;
    SDTPedidosImportados[] SDTPedidosImportados;

    public ListViewAdapterPedidoImp(Context context, SDTPedidosImportados[] SDTPedidosImportados) {
        this.context=context;
        this.SDTPedidosImportados = SDTPedidosImportados;
    }



    @Override
    public int getCount() {
        return SDTPedidosImportados.length;
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

        View itemView = inflater.inflate(R.layout.listarimportados,parent, false);
        try{
            TextView codigo = (TextView) itemView.findViewById(R.id.codigo);
            TextView fenv = (TextView) itemView.findViewById(R.id.fecha);
            TextView items = (TextView) itemView.findViewById(R.id.nItems);
            codigo.setText(SDTPedidosImportados[position].Codigo);
            String dia = String.valueOf(SDTPedidosImportados[position].dia);
            String mes = String.valueOf(SDTPedidosImportados[position].mes);
            String ano = String.valueOf(SDTPedidosImportados[position].ano);
            fenv.setText(dia+"-"+mes+"-"+ano);
            items.setText(String.valueOf(SDTPedidosImportados[position].items));
           // Button btnImportar = (Button) itemView.findViewById(R.id.btnImportar);
      /*      btnImportar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
*/


        }catch (Exception e){
            int hh=0;
        }

        return itemView;

    }


}