package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ListViewAdapterArtFam extends BaseAdapter {


    Context context;
    Bundle Extras;
    SDTArtFam[] SDTArtFam;

    ListViewAdapterProductosv2 ListViewAdapterProductosv2;
    String invfamcod;
    public ListViewAdapterArtFam(Context context, SDTArtFam[] SDTArtFam) {
        this.context=context;
        this.SDTArtFam = SDTArtFam;
    }



    @Override
    public int getCount() {
        return SDTArtFam.length;
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

        View itemView = inflater.inflate(R.layout.listararticulosfam,parent, false);
        try{
            TextView FamNombre = (TextView) itemView.findViewById(R.id.FamNombre);
            TextView SubNombre = (TextView) itemView.findViewById(R.id.SubNombre);
            TextView ArtCod = (TextView) itemView.findViewById(R.id.ArtCod);
            TextView ArtNom = (TextView) itemView.findViewById(R.id.ArtNom);
            TextView existencia = (TextView) itemView.findViewById(R.id.existencia);
            TextView Ean = (TextView) itemView.findViewById(R.id.Ean);
            LinearLayout contenedor = itemView.findViewById(R.id.contenedor);
            String mFam = SDTArtFam[position].mFam;
            String mSub = SDTArtFam[position].mSub;
            int titulo = Color.parseColor("#E4E9F1");
            if(position % 2 == 0){
                contenedor.setBackgroundColor(titulo);
            }

            if (SDTArtFam[position].Bloqueado!=0){
                contenedor.setBackgroundColor(Color.RED);
            }


            FamNombre.setText(SDTArtFam[position].InvFamNom);
            SubNombre.setText(SDTArtFam[position].InvSubNom);
            if(!mFam.equalsIgnoreCase("S")){
                FamNombre.setVisibility(View.GONE);
            }
            if(!mSub.equalsIgnoreCase("S")){
                SubNombre.setVisibility(View.GONE);
            }

            ArtCod.setText(SDTArtFam[position].ArtCod);
            ArtNom.setText(SDTArtFam[position].ArtNom);
            existencia.setText(String.valueOf(SDTArtFam[position].Exi));
            Ean.setText(SDTArtFam[position].Ean);


            final BaseDatos BaseDeDatos;
            BaseDeDatos = new BaseDatos(context, "MantisMovil", null, 5);

        }catch (Exception e){
            int hh=0;
        }

        return itemView;

    }


}