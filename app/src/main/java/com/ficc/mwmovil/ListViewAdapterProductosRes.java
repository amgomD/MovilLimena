package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class ListViewAdapterProductosRes extends BaseAdapter implements Filterable {

    Context context;
    SDTProductos[] SDTProductos;
    SDTProductos[] SDTProductosOriginal;
    CapturaPedido pCapturaPedido;
    Bundle Extras;
    String[] CausalNombre;

    interface AdapterInteractions {
        public void refreshActivity();
    }


    public ListViewAdapterProductosRes(Context context, SDTProductos[] SDTProductos) {  //,Bundle Extras
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

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View itemView = inflater.inflate(R.layout.listarproductosres,parent, false);

        TextView txt_codigo = (TextView) itemView.findViewById(R.id.txt_codigo);
        TextView txt_nombre = (TextView) itemView.findViewById(R.id.txt_nombre);
        TextView txt_nombrecomercial = (TextView) itemView.findViewById(R.id.txt_nombrecomercial);
        TextView txt_precio = (TextView) itemView.findViewById(R.id.txt_precio);
        TextView txt_precioneto = (TextView) itemView.findViewById(R.id.txt_precioneto);
        TextView txt_confemp = (TextView) itemView.findViewById(R.id.txt_confemp);
        TextView txt_confprov = (TextView) itemView.findViewById(R.id.txt_confprov);
        TextView txt_confvend = (TextView) itemView.findViewById(R.id.txt_confven);
        TextView txttipo = (TextView) itemView.findViewById(R.id.txttipo);

        final TextView txt_existencia = (TextView) itemView.findViewById(R.id.txt_existencia);
        TextView txt_embalaje = (TextView) itemView.findViewById(R.id.txt_embalaje);
        final TextView txt_cajas = (TextView) itemView.findViewById(R.id.txt_cajas);
        final TextView txt_unidades = (TextView) itemView.findViewById(R.id.txt_unidades);
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
        TextView Presentacion = (TextView) itemView.findViewById(R.id.Presentacion);


        Spinner spinner_causal = (Spinner) itemView.findViewById(R.id.spinner_causal);

        Button btn_eliminar = (Button) itemView.findViewById(R.id.btn_eliminar);


        final TextView txt_subtotal = (TextView) itemView.findViewById(R.id.txt_subtotal);
        final TextView txt_iva = (TextView) itemView.findViewById(R.id.txt_retencion);
        final TextView txt_impoconsumo = (TextView) itemView.findViewById(R.id.txt_retencionica);
        final TextView txt_total = (TextView) itemView.findViewById(R.id.txt_neto);

        txt_subtotal.setText("0");
        txt_iva.setText("0");
        txt_impoconsumo.setText("0");
        txt_total.setText("0");

         txt_codigo.setText(SDTProductos[position].Codigo);
         if(SDTProductos[position].NotaCar.equalsIgnoreCase("S")){
             txt_nombre.setText("C"+SDTProductos[position].Nombre);
         }else{
             txt_nombre.setText(SDTProductos[position].Nombre);
         }

         txt_nombrecomercial.setText(SDTProductos[position].Nombrecomercial);
         txt_precio.setText(String.format("%,d",SDTProductos[position].PrecioIva.intValue()));
         txt_precioneto.setText("$ "+String.format("%.2f",SDTProductos[position].TotSubtotal));
         txt_confemp.setText(SDTProductos[position].ConfEmp.toString());
         txt_confprov.setText(SDTProductos[position].ConfProv.toString());
         String ven=SDTProductos[position].ConfVend.toString();
         txt_confvend.setText(ven);
         txt_existencia.setText(SDTProductos[position].Existencia.toString());
         txt_embalaje.setText(SDTProductos[position].Embalaje.toString());

        Log.e("SDTProductos",SDTProductos[position].Unidadesinf.toString());



        if (SDTProductos[position].Unidadesinf==0) {
             txt_cajas.setText("");
         }else{
             txt_cajas.setText(SDTProductos[position].Unidadesinf.toString());
         }
         txt_unidades.setText(SDTProductos[position].Unidades.toString());
        if (SDTProductos[position].Unidades+SDTProductos[position].Unidadesinf==0) {
            txt_unidades.setText("");
        }else{
            txt_unidades.setText(String.valueOf(SDTProductos[position].Unidades)); //.intValue()
        }


         txt_dto1.setText(SDTProductos[position].Dct1.toString());
         txt_dto2.setText(SDTProductos[position].Dct2.toString());
         txt_dto3.setText(SDTProductos[position].Dct3.toString());
         txt_dto4.setText(SDTProductos[position].Dct4.toString());
         txt_dto5.setText(SDTProductos[position].Dct5.toString());
         txt_dto6.setText(SDTProductos[position].Dct6.toString());
        Presentacion.setText(SDTProductos[position].Presentacion);
 //        txt_dto7.setText(SDTProductos[position].Dct7.toString());
 //        txt_dto8.setText(SDTProductos[position].Dct8.toString());
//         txt_dto1_n.setText(SDTProductos[position].Dct1no.toString());
//         txt_dto2_n.setText(SDTProductos[position].Dct2no.toString());
//         txt_dto3_n.setText(SDTProductos[position].Dct3no.toString());
//         txt_dto4_n.setText(SDTProductos[position].Dct4no.toString());
//         txt_dto5_n.setText(SDTProductos[position].Dct5no.toString());
//         txt_dto6_n.setText(SDTProductos[position].Dct6no.toString());
//         txt_dto7_n.setText(SDTProductos[position].Dct7no.toString());
//         txt_dto8_n.setText(SDTProductos[position].Dct8no.toString());
         txt_poriva.setText(SDTProductos[position].Iva.toString());

    //    spinner_causal.setAdapter(new ArrayAdapter<String>(context,R.layout.support_simple_spinner_dropdown_item,CausalNombre)); // simple_spinner_item


        txt_subtotal.setText(String.format("%.2f",SDTProductos[position].TotSubtotal));
        txt_iva.setText(String.format("%.2f",SDTProductos[position].TotIva));
        txt_impoconsumo.setText(String.format("%.2f",SDTProductos[position].TotImpoconsumo));
        txt_total.setText(String.format("%.2f",SDTProductos[position].Total));

        LinearLayout LinearLayout2 = (LinearLayout) itemView.findViewById(R.id.layoutfondo);

       /* int modulo= position % 2;
        if (modulo!=0){
            LinearLayout2.setBackgroundColor(Color.LTGRAY);
        }else{
            LinearLayout2.setBackgroundColor(Color.WHITE);
        }*/

       if( SDTProductos[position].Unidades+SDTProductos[position].Unidadesinf == 0.0){
            LinearLayout2.setVisibility(View.GONE);
        }

        txttipo.setText("Pedido");
        txttipo.setBackgroundColor(Color.parseColor("#2196F3"));

        if(SDTProductos[position].NotaInv.equalsIgnoreCase("S") ){
            txttipo.setText("Credito Bueno");
            txttipo.setBackgroundColor(Color.parseColor("#009688"));
            //LinearLayout2.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_credito));
        }
        if(SDTProductos[position].Unidades>0 && SDTProductos[position].NotaCar.equalsIgnoreCase("S") ){
           // LinearLayout2.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_creditomalo));
            txttipo.setText("Credito Malo");
            txttipo.setBackgroundColor(Color.parseColor("#E91E63"));
        }

        if(SDTProductos[position].PrecioNeto ==0){
            txttipo.setText("Bonificado");
            txttipo.setBackgroundColor(Color.parseColor("#079107"));
        }


        btn_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                SDTProductos[position].Cajas=0;
                SDTProductos[position].Unidades=0.0;
                SDTProductos[position].Unidadesinf =0.0;
                SDTProductos[position].Guardar(1);

                AlertDialog.Builder Alerta = new AlertDialog.Builder(context);
                Alerta.setMessage("Producto Eliminado");
                Alerta.setTitle("Notificacion");
                Alerta.setPositiveButton("OK", null);
                Alerta.setCancelable(true);
                Alerta.create().show();
                notifyDataSetChanged();
                ((ResumenPedido)context).refreshActivity();
            }
        });


        return itemView;
    }
    public void Refrescar(){
        notifyDataSetChanged();
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
