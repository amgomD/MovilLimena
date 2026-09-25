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

public class ListViewAdapterKardex extends BaseAdapter  {

    Context context;
    SDTKardex[] SDTKardex;

    public ListViewAdapterKardex(Context context, SDTKardex[] SDTKardex) {
        this.context = context;
        this.SDTKardex = SDTKardex;
    }

    @Override
    public int getCount() {
        return SDTKardex.length;
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
        View itemView = inflater.inflate(R.layout.listakardex,parent, false);

         TextView ArtCod = (TextView) itemView.findViewById(R.id.ArtCod);
        TextView ArtNom = (TextView) itemView.findViewById(R.id.ArtNom);
        TextView KarTotUni = (TextView) itemView.findViewById(R.id.karuni);
        TextView KarValTot = (TextView) itemView.findViewById(R.id.karvalor);
        TextView KarPrePub = (TextView) itemView.findViewById(R.id.KarPrePub);
        TextView KarDes = (TextView) itemView.findViewById(R.id.KarDes);
        TextView KarArtIva = (TextView) itemView.findViewById(R.id.KarArtIva);
        //TextView txt_valfactura = (TextView) itemView.findViewById(R.id.txt_valfactura);

        try {

            ArtCod.setText(SDTKardex[position].ArtCod);
            ArtNom.setText(SDTKardex[position].ArtNom);
            KarTotUni.setText(SDTKardex[position].KarTotUni);
            KarValTot.setText(SDTKardex[position].karvaltotMenDes);
            KarArtIva.setText(SDTKardex[position].KarArtIva);
            KarPrePub.setText(SDTKardex[position].KarPrePub);
            KarDes.setText(SDTKardex[position].Kardes);

            //txt_valfactura.setText(SDTCartera[position].FacSaldo);
        }catch (Exception e)
        {

        }


        return itemView;
    }


}
