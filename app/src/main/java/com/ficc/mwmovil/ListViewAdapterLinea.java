package com.ficc.mwmovil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewAdapterLinea extends BaseAdapter {

    Context context;
    SDTVentasxLinea[] SDTVentasxLinea;

    public ListViewAdapterLinea(Context context,SDTVentasxLinea[] SDTVentasxLinea) {
        this.context = context;
        this.SDTVentasxLinea = SDTVentasxLinea;
    }

    @Override
    public int getCount() {
        return SDTVentasxLinea.length;
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
        View itemView = inflater.inflate(R.layout.listarlineas,parent, false);

        TextView txt_nombre = (TextView) itemView.findViewById(R.id.txt_nombre);
        TextView txt_venta = (TextView) itemView.findViewById(R.id.txt_venta);
        TextView txt_dctovalor = (TextView) itemView.findViewById(R.id.txt_dctovalor);
        TextView txt_dctoprod = (TextView) itemView.findViewById(R.id.txt_dctoprod);
        TextView txt_dctoescala = (TextView) itemView.findViewById(R.id.txt_dctoescala);
        TextView txt_dctomixto = (TextView) itemView.findViewById(R.id.txt_dctomixto);
        TextView txt_bonf = (TextView) itemView.findViewById(R.id.txt_bonf);

        //txt_dctolinea.setText(String.format("%,d",SDTVentasxLinea[position].DctoLinea.intValue())+"%");
        txt_dctoescala.setText(String.format("%,d",SDTVentasxLinea[position].DctoEsc.intValue()));
        txt_dctomixto.setText(String.format("%,d",SDTVentasxLinea[position].DctoMix.intValue()));
        txt_dctoprod.setText(String.format("%,d",SDTVentasxLinea[position].DctoProm.intValue()));
        txt_bonf.setText(String.format("%,d",SDTVentasxLinea[position].DctoBonf.intValue()));

        txt_nombre.setText(SDTVentasxLinea[position].Nombre);
        txt_venta.setText(String.format("%,d",SDTVentasxLinea[position].Venta.intValue()));

        return itemView;

    }
}
