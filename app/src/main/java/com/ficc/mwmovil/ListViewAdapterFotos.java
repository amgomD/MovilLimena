package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListViewAdapterFotos extends BaseAdapter {

    Context context;
    SDTFotos[] SDTFotos;

    public ListViewAdapterFotos(Context context, SDTFotos[] SDTFotos) {
        this.context = context;
        this.SDTFotos = SDTFotos;
    }

    @Override
    public int getCount() {
        return SDTFotos.length;
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
        View itemView = inflater.inflate(R.layout.fotos,parent, false);
    LinearLayout contenedorfoto = itemView.findViewById(R.id.contenedorfoto);
        TextView numoperacion = (TextView) itemView.findViewById(R.id.numoperacion);
        TextView valorop = (TextView) itemView.findViewById(R.id.valorop);
       String ConNro = SDTFotos[position].ConNro;
        Button eliminar = itemView.findViewById(R.id.eliminar);
        ImageView foto = (ImageView) itemView.findViewById(R.id.foto);
         numoperacion.setText(SDTFotos[position].RegOperacion);
        String base64String = SDTFotos[position].image64;
        valorop.setText(String.format("%,d",SDTFotos[position].valorop.intValue()));

if(SDTFotos[position].NitSec.isEmpty()){
    contenedorfoto.setVisibility(View.GONE);
}
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
       foto.setImageBitmap(decodedByte);


        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseDatos BaseDeDatos;
                BaseDeDatos = new BaseDatos(context, "MantisMovil", null, 6);
                Log.e("llave ConNro: ",ConNro);
                String consulta = "";
               if(ConNro.isEmpty()){
                    consulta = "delete from fototemp where NroOp = '"+SDTFotos[position].RegOperacion+"'";
               }else{
                   consulta = "delete from ConsignaRecibofoto where ConNro = '"+ConNro+"' and RecNro = '"+SDTFotos[position].NitSec+"'";
               }



                BaseDeDatos.getWritableDatabase().execSQL(consulta);


                SDTFotos[position].NitSec = "";
                notifyDataSetChanged();
            }
        });

        return itemView;
    }

}
