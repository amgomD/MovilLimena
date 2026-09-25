package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class ListViewAdapterEfectivo extends BaseAdapter  {

    Context context;
    SDTEfecitvo[] SDTEfecitvo;

    public ListViewAdapterEfectivo(Context context, SDTEfecitvo[] SDTEfecitvo) {
        this.context = context;
        this.SDTEfecitvo = SDTEfecitvo;
    }

    @Override
    public int getCount() {
        return SDTEfecitvo.length;
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
        View itemView = inflater.inflate(R.layout.listarefectivo,parent, false);

        TextView RecNro = (TextView) itemView.findViewById(R.id.RecNro);
        TextView tipoforma = (TextView) itemView.findViewById(R.id.tipoforma);

        TextView cliente = (TextView) itemView.findViewById(R.id.cliente);
        TextView Valor = (TextView) itemView.findViewById(R.id.Valor);
        View selefectivo = (View) itemView.findViewById(R.id.selefectivo);
        selefectivo.setEnabled(false);

        try {
            RecNro.setText(SDTEfecitvo[position].RecNro);
            Valor.setText(String.format("%,d",SDTEfecitvo[position].Valor.intValue()));
            cliente.setText(SDTEfecitvo[position].Clinom);

            tipoforma.setText(SDTEfecitvo[position].tipo);
            if(SDTEfecitvo[position].aldia.equalsIgnoreCase("S")){
                tipoforma.setText("Cheque al dia");
            }
            if(SDTEfecitvo[position].postfecha.equalsIgnoreCase("S")){
                tipoforma.setText("Cheque PostFechado");
            }
            if(SDTEfecitvo[position].check){
                selefectivo.setBackgroundColor(Color.parseColor("#E91E63"));
            }

        }catch (Exception e)
        {
            Log.e("Errolist",e.toString());
        }



        return itemView;
    }


}
