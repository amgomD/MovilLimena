package com.ficc.mwmovil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewAdapterRecibo extends BaseAdapter  {

    Context context;
    SDTRecibo[] SDTRecibo;

    public ListViewAdapterRecibo(Context context, SDTRecibo[] SDTRecibo) {
        this.context = context;
        this.SDTRecibo = SDTRecibo;
    }

    @Override
    public int getCount() {
        return SDTRecibo.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.listarrecibo,parent, false);

        TextView Factura = (TextView) itemView.findViewById(R.id.Factura);
        TextView Abono = (TextView) itemView.findViewById(R.id.RecAbono);
        TextView RecSaldo = (TextView) itemView.findViewById(R.id.RecSaldo);
        TextView Retencion = (TextView) itemView.findViewById(R.id.Retencion);
        TextView Reteica = (TextView) itemView.findViewById(R.id.Reteica);
        TextView descuento = (TextView) itemView.findViewById(R.id.Descuento);
        //TextView recibido = (TextView) itemView.findViewById(R.id.Recibido);


        try {
            Factura.setText(SDTRecibo[position].FacNro);
            Abono.setText(String.format("%,d",SDTRecibo[position].Abono.intValue()));
            RecSaldo.setText(String.format("%,d",SDTRecibo[position].Saldo.intValue()));
            Retencion.setText(String.format("%,d",SDTRecibo[position].Retencion.intValue()));
            Reteica.setText(String.format("%,d",SDTRecibo[position].Reteica.intValue()));
            descuento.setText(String.format("%,d",SDTRecibo[position].Descuento.intValue()));
          //  recibido.setText(String.format("%,d",SDTRecibo[position].Efectivo.intValue()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemView;
    }


}
