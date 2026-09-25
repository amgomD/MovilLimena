package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListViewAdapterProductosasync<S> extends BaseAdapter implements Filterable {

    Context context;
    SDTProductos[] SDTProductos;
    SDTProductos[] SDTProductosOriginal;
    EditarProductoV2 pCapturaPedido;
    String[] CausalNombre;

    public ListViewAdapterProductosasync(Context context, SDTProductos[] SDTProductos, String[] CausalNombre) {
        this.context = context;
        this.SDTProductos = SDTProductos;
        this.SDTProductosOriginal=SDTProductos;
        this.CausalNombre=CausalNombre;
    }

    @Override
    public int getCount() {
        return SDTProductos.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {





        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View itemView = inflater.inflate(R.layout.listarproductosv2,parent, false);

        LinearLayout LinearLayout2 = (LinearLayout) itemView.findViewById(R.id.layoutfondo);
        int modulo= position % 2;
        if (modulo!=0){
            LinearLayout2.setBackgroundColor(Color.LTGRAY);
        }else{
            LinearLayout2.setBackgroundColor(Color.WHITE);
        }
        if (SDTProductos[position].Bloqueado!=0){
            LinearLayout2.setBackgroundColor(Color.RED);
        }
        TextView txt_codigo = (TextView) itemView.findViewById(R.id.txt_codigo);
        TextView txt_nombre = (TextView) itemView.findViewById(R.id.txt_nombre);
        TextView txt_nombrecomercial = (TextView) itemView.findViewById(R.id.txt_nombrecomercial);
        TextView txt_precio = (TextView) itemView.findViewById(R.id.txt_abonoori);

        TextView txt_dcto1 = (TextView) itemView.findViewById(R.id.txt_dcto1);
        TextView txt_dcto2 = (TextView) itemView.findViewById(R.id.txt_dcto2);

        TextView txt_iva = (TextView) itemView.findViewById(R.id.txt_retencion);
        TextView txt_precioneto = (TextView) itemView.findViewById(R.id.txt_neto);
        TextView txt_existencia = (TextView) itemView.findViewById(R.id.txt_existencia);
        TextView txt_embalaje = (TextView) itemView.findViewById(R.id.txt_embalaje);

        ImageView imgdcto = (ImageView) itemView.findViewById(R.id.imgdcto);

        if(SDTProductos[position].TienePromo.equalsIgnoreCase("S")){
            imgdcto.setVisibility(View.VISIBLE);
        }else{
            imgdcto.setVisibility(View.GONE);
        }

        txt_embalaje.setText(String.format("%,d",SDTProductos[position].Embalaje.intValue()));
        txt_codigo.setText(SDTProductos[position].Codigo);
        txt_nombre.setText(SDTProductos[position].Nombre);
        txt_nombrecomercial.setText(SDTProductos[position].Nombrecomercial);

        if (SDTProductos[position].Dct1>0) {
            txt_dcto1.setText(SDTProductos[position].Dct1.toString()+"%");
        }
        if (SDTProductos[position].Dct2>0) {
            txt_dcto2.setText(SDTProductos[position].Dct2.toString()+"%");
        }
        txt_existencia.setText(SDTProductos[position].Existencia.toString());
        txt_precio.setText(String.format("%,d",SDTProductos[position].Precio.intValue()));
        txt_iva.setText('%'+String.format("%,d",SDTProductos[position].Iva.intValue()));
        txt_precioneto.setText(String.format("%,d",SDTProductos[position].PrecioNeto.intValue()));

        return itemView;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String filterString = constraint.toString().toLowerCase();

                FilterResults results = new FilterResults();

                final SDTProductos[] list = SDTProductosOriginal;

                int count = list.length;

                String filterableString;

                Integer Vueltas=0;
                for (int i = 0; i < count; i++) {
                    filterableString =list[i].Codigo+list[i].Nombre+list[i].Nombrecomercial; //list[i].ArtSec+
                    if (filterableString.toLowerCase().contains(filterString) || ((filterString=="****" && list[i].Unidades>0.0) ) ) {
                        Vueltas+=1;
                    }
                }

                final SDTProductos[] nlist = new SDTProductos[Vueltas];

                Vueltas=0;
                for (int i = 0; i < count; i++) {
                    filterableString = list[i].Codigo+list[i].Nombre+list[i].Nombrecomercial; //list[i].ArtSec+
                    if (filterableString.toLowerCase().contains(filterString) || ((filterString=="****" && list[i].Unidades>0.0) )) {
                        nlist[Vueltas] = (SDTProductosOriginal[i]);
                        Vueltas+=1;
                    }
                }

                results.values = nlist;
                results.count = nlist.length;

                return results;
            }


            // protected void publishResults(CharSequence constraint, FilterResults results) {
            //     SDTClientesOriginal = (SDTClientes[]) results.values;
            //     notifyDataSetChanged();
            // }
            //@SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                // TODO Auto-generated method stub
                // if (results.count == 0) {
                //     notifyDataSetInvalidated();
                // }else{
                SDTProductos = (SDTProductos[]) results.values;
                pCapturaPedido.SDTProductos=SDTProductos;
                //Integer pp= TmpSDTClientes.length;
                //Integer pp2= TmpSDTClientes.length;
                notifyDataSetChanged();
                // }
            }

        };
    }

}
