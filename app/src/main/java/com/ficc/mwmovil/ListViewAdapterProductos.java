package com.ficc.mwmovil;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Spinner;
import android.widget.TextView;

public class ListViewAdapterProductos extends BaseAdapter implements Filterable {

    Context context;
    SDTProductos[] SDTProductos;
    SDTProductos[] SDTProductosOriginal;
    CapturaPedido pCapturaPedido;
    String[] CausalNombre;

    public ListViewAdapterProductos(Context context,SDTProductos[] SDTProductos,CapturaPedido pCapturaPedido,String[] CausalNombre) {
        this.context = context;
        this.SDTProductos = SDTProductos;
        this.SDTProductosOriginal=SDTProductos;
        this.pCapturaPedido=pCapturaPedido;
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View itemView = inflater.inflate(R.layout.listarproductos,parent, false);

        TextView txt_codigo = (TextView) itemView.findViewById(R.id.txt_codigo);
        TextView txt_nombre = (TextView) itemView.findViewById(R.id.txt_nombre);
        TextView txt_nombrecomercial = (TextView) itemView.findViewById(R.id.txt_nombrecomercial);
        TextView txt_precio = (TextView) itemView.findViewById(R.id.txt_abonoori);
        TextView txt_precioneto = (TextView) itemView.findViewById(R.id.txt_precioneto);
        TextView txt_confemp = (TextView) itemView.findViewById(R.id.txt_confemp);
        TextView txt_confprov = (TextView) itemView.findViewById(R.id.txt_confprov);
        final TextView txt_existencia = (TextView) itemView.findViewById(R.id.txt_existencia);
        TextView txt_embalaje = (TextView) itemView.findViewById(R.id.txt_embalaje);
        final TextView edit_cajas = (TextView) itemView.findViewById(R.id.edit_cajas);
        final TextView edit_unidades = (TextView) itemView.findViewById(R.id.edit_unidades);
        TextView txt_dto1 = (TextView) itemView.findViewById(R.id.txt_dto1);
        TextView txt_dto2 = (TextView) itemView.findViewById(R.id.txt_dto2);
        TextView txt_dto3 = (TextView) itemView.findViewById(R.id.txt_dto3);
        TextView txt_dto4 = (TextView) itemView.findViewById(R.id.txt_dto4);
        TextView txt_dto5 = (TextView) itemView.findViewById(R.id.txt_dto5);
        TextView txt_dto6 = (TextView) itemView.findViewById(R.id.txt_dto6);
        TextView txt_dto7 = (TextView) itemView.findViewById(R.id.txt_dto7);
        TextView txt_dto8 = (TextView) itemView.findViewById(R.id.txt_dto8);
        TextView txt_dto1_n = (TextView) itemView.findViewById(R.id.txt_dto1_n);
        TextView txt_dto2_n = (TextView) itemView.findViewById(R.id.txt_dto2_n);
        TextView txt_dto3_n = (TextView) itemView.findViewById(R.id.txt_dto3_n);
        TextView txt_dto4_n = (TextView) itemView.findViewById(R.id.txt_dto4_n);
        TextView txt_dto5_n = (TextView) itemView.findViewById(R.id.txt_dto5_n);
        TextView txt_dto6_n = (TextView) itemView.findViewById(R.id.txt_dto6_n);
        TextView txt_dto7_n = (TextView) itemView.findViewById(R.id.txt_dto7_n);
        TextView txt_dto8_n = (TextView) itemView.findViewById(R.id.txt_dto8_n);
        TextView txt_poriva = (TextView) itemView.findViewById(R.id.txt_poriva);
        Spinner spinner_causal = (Spinner) itemView.findViewById(R.id.spinner_causal);

        final TextView txt_subtotal = (TextView) itemView.findViewById(R.id.txt_subtotalprod);
        final TextView txt_iva = (TextView) itemView.findViewById(R.id.txt_subtotaliva);
        final TextView txt_impoconsumo = (TextView) itemView.findViewById(R.id.txt_retencionica);
        final TextView txt_total = (TextView) itemView.findViewById(R.id.txt_neto);

        txt_subtotal.setText("0");
        txt_iva.setText("0");
        txt_impoconsumo.setText("0");
        txt_total.setText("0");

         txt_codigo.setText(SDTProductos[position].Codigo);
         txt_nombre.setText(SDTProductos[position].Nombre);
         txt_nombrecomercial.setText(SDTProductos[position].Nombrecomercial);
         txt_precio.setText(String.format("%,d",SDTProductos[position].PrecioIva.intValue()));
         txt_precioneto.setText(String.format("%,d",SDTProductos[position].PrecioNeto.intValue()));
         txt_confemp.setText(SDTProductos[position].ConfEmp.toString());
         txt_confprov.setText(SDTProductos[position].ConfProv.toString());
         txt_existencia.setText(SDTProductos[position].Existencia.toString());
         txt_embalaje.setText(SDTProductos[position].Embalaje.toString());
         if (SDTProductos[position].Cajas==0) {
             edit_cajas.setText("");
         }else{
             edit_cajas.setText(SDTProductos[position].Cajas.toString());
         }
         edit_unidades.setText(SDTProductos[position].Unidades.toString());
        if (SDTProductos[position].Unidades==0) {
            edit_unidades.setText("");
        }else{
            edit_unidades.setText(String.valueOf(SDTProductos[position].Unidades.intValue()));
        }


         txt_dto1.setText(SDTProductos[position].Dct1.toString());
         txt_dto2.setText(SDTProductos[position].Dct2.toString());
         txt_dto3.setText(SDTProductos[position].Dct3.toString());
         txt_dto4.setText(SDTProductos[position].Dct4.toString());
         txt_dto5.setText(SDTProductos[position].Dct5.toString());
         txt_dto6.setText(SDTProductos[position].Dct6.toString());
         txt_dto7.setText(SDTProductos[position].Dct7.toString());
         txt_dto8.setText(SDTProductos[position].Dct8.toString());
         txt_dto1_n.setText(SDTProductos[position].Dct1no.toString());
         txt_dto2_n.setText(SDTProductos[position].Dct2no.toString());
         txt_dto3_n.setText(SDTProductos[position].Dct3no.toString());
         txt_dto4_n.setText(SDTProductos[position].Dct4no.toString());
         txt_dto5_n.setText(SDTProductos[position].Dct5no.toString());
         txt_dto6_n.setText(SDTProductos[position].Dct6no.toString());
         txt_dto7_n.setText(SDTProductos[position].Dct7no.toString());
         txt_dto8_n.setText(SDTProductos[position].Dct8no.toString());
         txt_poriva.setText(SDTProductos[position].Iva.toString());

        spinner_causal.setAdapter(new ArrayAdapter<String>(context,R.layout.support_simple_spinner_dropdown_item,CausalNombre)); // simple_spinner_item


        txt_subtotal.setText(String.format("%,d",SDTProductos[position].TotSubtotal.intValue()));
        txt_iva.setText(String.format("%,d",SDTProductos[position].TotIva.intValue()));
        txt_impoconsumo.setText(String.format("%,d",SDTProductos[position].TotImpoconsumo.intValue()));
        txt_total.setText(String.format("%,d",SDTProductos[position].Total.intValue()));

        edit_cajas.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().isEmpty()){
                    int hh=0;
                }else{


                    Integer Unidades;
                    if(edit_unidades.getText().toString().trim().isEmpty()){
                        Unidades=0;
                    }else{
                        Unidades=Integer.valueOf(edit_unidades.getText().toString().trim());
                    }
                    Integer Cajas = Integer.valueOf(s.toString());
                    Double Existencia = Double.valueOf(txt_existencia.getText().toString());
                    if (Unidades+(Cajas*SDTProductos[position].Embalaje) > Existencia.intValue()) {
                        Cajas=SDTProductos[position].Cajas.intValue();
                        edit_cajas.setText(String.valueOf(SDTProductos[position].Cajas.intValue()));
                    }else {

                    }
                    SDTProductos[position].Cajas = Cajas;
                    SDTProductos[position].Guardar(1);

                }
                txt_subtotal.setText(String.format("%,d",SDTProductos[position].TotSubtotal.intValue()));
                txt_iva.setText(String.format("%,d",SDTProductos[position].TotIva.intValue()));
                txt_impoconsumo.setText(String.format("%,d",SDTProductos[position].TotImpoconsumo.intValue()));
                txt_total.setText(String.format("%,d",SDTProductos[position].Total.intValue()));
                //notifyDataSetChanged();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }
        });

        edit_unidades.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {

                    try {
                       // GestorPedidos GestorPedidos = new GestorPedidos();
                       // String Existencia = GestorPedidos.TraerExistencia(SDTProductos[position].ArtSec);
                       // txt_existencia.setText(Existencia);
                    }catch (Exception e) {
                       // txt_existencia.setText("N/D");
                    }

                    //Toast.makeText(getApplicationContext(), "Got the focus", Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(getApplicationContext(), "Lost the focus", Toast.LENGTH_LONG).show();
                }
            }
        });

        edit_unidades.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Integer Unidades;
                if(s.toString().trim().isEmpty()){
                    Unidades=0;
                }else{
                    Unidades=Integer.valueOf(s.toString());
                }
                Double Existencia = Double.valueOf(txt_existencia.getText().toString());
                if (Unidades+(SDTProductos[position].Cajas*SDTProductos[position].Embalaje) > Existencia.intValue()) {
                    Unidades=SDTProductos[position].Unidades.intValue();
                    edit_unidades.setText(String.valueOf(SDTProductos[position].Unidades.intValue()));
                }else{
                    SDTProductos[position].Unidades=Double.valueOf(Unidades);
                    SDTProductos[position].Guardar(1);
                    txt_subtotal.setText(String.format("%,d",SDTProductos[position].TotSubtotal.intValue()));
                    txt_iva.setText(String.format("%,d",SDTProductos[position].TotIva.intValue()));
                    txt_impoconsumo.setText(String.format("%,d",SDTProductos[position].TotImpoconsumo.intValue()));
                    txt_total.setText(String.format("%,d",SDTProductos[position].Total.intValue()));
                }
                pCapturaPedido.Totalizar();

               // context.
                //    notifyDataSetChanged();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }
        });


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
                    filterableString = list[i].Codigo+list[i].Nombre+list[i].Nombrecomercial;
                    if (filterableString.toLowerCase().contains(filterString) || ((filterString=="****" && list[i].Unidades>0.0) ) ) {
                        Vueltas+=1;
                    }
                }

                final SDTProductos[] nlist = new SDTProductos[Vueltas];

                Vueltas=0;
                for (int i = 0; i < count; i++) {
                    filterableString = list[i].Codigo+list[i].Nombre+list[i].Nombrecomercial;
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
                //Integer pp= TmpSDTClientes.length;
                //Integer pp2= TmpSDTClientes.length;
                notifyDataSetChanged();
                // }
            }

        };
    }

}
