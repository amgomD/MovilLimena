package com.ficc.mwmovil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewAdapterCartera extends BaseAdapter  {

    Context context;
    SDTCartera[] SDTCartera;

    public ListViewAdapterCartera(Context context, SDTCartera[] SDTCartera) {
        this.context = context;
        this.SDTCartera = SDTCartera;
    }

    @Override
    public int getCount() {
        return SDTCartera.length;
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
        View itemView = inflater.inflate(R.layout.listarcartera,parent, false);

        TextView txt_factura = (TextView) itemView.findViewById(R.id.txt_factura);
        TextView txt_plazo = (TextView) itemView.findViewById(R.id.txt_plazo);
        TextView txt_fecha = (TextView) itemView.findViewById(R.id.txt_fecha);
        TextView txt_fechavence = (TextView) itemView.findViewById(R.id.txt_fechavence);
        TextView txt_mora = (TextView) itemView.findViewById(R.id.txt_mora);
        TextView txt_valor = (TextView) itemView.findViewById(R.id.txt_valor);
        TextView txt_abono = (TextView) itemView.findViewById(R.id.txt_abono);
        TextView txt_saldo = (TextView) itemView.findViewById(R.id.txt_saldo);
        TextView txt_abono2 = (TextView) itemView.findViewById(R.id.txt_abono2);
        //TextView txt_valfactura = (TextView) itemView.findViewById(R.id.txt_valfactura);

        try {
            txt_factura.setText(SDTCartera[position].FacNro);
            txt_plazo.setText(SDTCartera[position].FacConPag);
            txt_fecha.setText(SDTCartera[position].FacFec);
            txt_fechavence.setText(SDTCartera[position].FacFecVen);
            txt_mora.setText(SDTCartera[position].Mora);
            txt_valor.setText(SDTCartera[position].FacTotalImpuestos);
            txt_abono.setText(SDTCartera[position].FacAbonos);
            txt_saldo.setText(SDTCartera[position].FacSaldo);
            txt_abono2.setText(SDTCartera[position].FacAbonosReci);
            //txt_valfactura.setText(SDTCartera[position].FacSaldo);
        }catch (Exception e)
        {

        }
        return itemView;
    }


}
