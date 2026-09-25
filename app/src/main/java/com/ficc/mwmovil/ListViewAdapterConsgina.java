package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ListViewAdapterConsgina extends BaseAdapter  {

    Context context;
    SDTConsigna[] SDTConsigna;

    public ListViewAdapterConsgina(Context context, SDTConsigna[] SDTConsigna) {
        this.context = context;
        this.SDTConsigna = SDTConsigna;
    }

    @Override
    public int getCount() {
        return SDTConsigna.length;
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
        View itemView = inflater.inflate(R.layout.listaconsigna,parent, false);

        TextView ConNro = (TextView) itemView.findViewById(R.id.ConNro);
        TextView Valor = (TextView) itemView.findViewById(R.id.Valorcon);
        TextView nroRec = (TextView) itemView.findViewById(R.id.nroRec);
        TextView nroFotos = (TextView) itemView.findViewById(R.id.nroFotos);
        Button enviarsolo = (Button) itemView.findViewById(R.id.enviarsolo);
        TextView enviado = (TextView) itemView.findViewById(R.id.enviado);
        TextView pendiente = (TextView) itemView.findViewById(R.id.pendiente);
        TextView recibido = (TextView) itemView.findViewById(R.id.recibido);

        enviado.setVisibility(View.GONE);
        pendiente.setVisibility(View.VISIBLE);
        recibido.setVisibility(View.GONE);
        Button ver = (Button) itemView.findViewById(R.id.ver);


        try {
            if(SDTConsigna[position].enviado.equalsIgnoreCase("S")){
                enviado.setVisibility(View.VISIBLE);
                pendiente.setVisibility(View.GONE);
            }else{
                pendiente.setVisibility(View.VISIBLE);
                enviado.setVisibility(View.GONE);
            }

            if(SDTConsigna[position].Recibido.equalsIgnoreCase("A")){
                enviado.setVisibility(View.GONE);
                pendiente.setVisibility(View.GONE);
                ///ver.setVisibility(View.GONE);
                recibido.setVisibility(View.VISIBLE);
            }

            enviarsolo.setVisibility(View.GONE);


            if(SDTConsigna[position].valorFoto.intValue() == SDTConsigna[position].ValorCon.intValue() ){
                enviarsolo.setVisibility(View.VISIBLE);
            }

                ConNro.setText(SDTConsigna[position].ConNro);
            Valor.setText(String.format("%,d",SDTConsigna[position].ValorCon.intValue()));
            nroRec.setText(String.valueOf(SDTConsigna[position].numRec));
            nroFotos.setText(String.format("%,d",SDTConsigna[position].valorFoto.intValue()));


        }catch (Exception e)
        {
            Log.e("Errolist",e.toString());
        }
        ver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SDTConsigna[position].Recibido.equalsIgnoreCase("A")){
                    Intent i = new Intent(context, VerConsigna.class);
                    i.putExtra("ConNro",SDTConsigna[position].ConNro);
                    context.startActivity(i);
                }else{
                    Intent i = new Intent(context, ConsignacionEfectivo.class);
                    i.putExtra("ConNro",SDTConsigna[position].ConNro);
                    context.startActivity(i);
                }

            }
        });
        enviarsolo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(context, ConfirmarEnvioCon.class);
                intent.putExtra("ConNro",SDTConsigna[position].ConNro);
                intent.putExtra("Sinenviar",0);
                ((ResumenConsignacion) context).startActivityForResult(intent, 300);
            }
        });


        return itemView;
    }


}
