package com.ficc.mwmovil;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class ListViewAdapterProductosEnv extends BaseAdapter implements Filterable {

    Context context;
    SDTProductos[] SDTProductos;
    SDTProductos[] SDTProductosOriginal;
    CapturaPedido pCapturaPedido;
    String[] CausalNombre;

    public ListViewAdapterProductosEnv(Context context, SDTProductos[] SDTProductos) {
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
        final View itemView = inflater.inflate(R.layout.listarproductosenv,parent, false);

        LinearLayout idLinearLayout2 = (LinearLayout) itemView.findViewById(R.id.idLinearLayout2);
        int modulo= position % 2;
        if (modulo!=0){
            idLinearLayout2.setBackgroundColor(Color.LTGRAY);
        }else{
            idLinearLayout2.setBackgroundColor(Color.WHITE);
        }

        TextView txt_porenvio = (TextView) itemView.findViewById(R.id.txt_portras);
        TextView txt_porval = (TextView) itemView.findViewById(R.id.txt_porval);
        GlobalVariables gGlobalVariables = GlobalVariables.getInstance();
        String vEmpresa=gGlobalVariables.getEmpresa();
        txt_porval.setText(String.format("%.2f",SDTProductos[position].ValorPedidoEnviado));
        //  txt_precio.setText(String.format("%.2f",SDTProductos[position].Precio));

        TextView txt_codigo = (TextView) itemView.findViewById(R.id.txt_codigo);
        TextView txt_nombre = (TextView) itemView.findViewById(R.id.txt_nombre);
        TextView txt_nombrecomercial = (TextView) itemView.findViewById(R.id.txt_nombrecomercial);
        TextView txt_precio = (TextView) itemView.findViewById(R.id.txt_precioenv);
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
        TextView Presentacion = (TextView) itemView.findViewById(R.id.Presentacion);

        TextView txt_poriva = (TextView) itemView.findViewById(R.id.txt_poriva);
        Button Btn_Enviarprod = itemView.findViewById(R.id.Btn_Enviarprod);
        Button Btn_Editarprod = itemView.findViewById(R.id.Btn_Editarprod);
        Spinner spinner_causal = (Spinner) itemView.findViewById(R.id.spinner_causal);
        Btn_Enviarprod.setVisibility(View.GONE);
        if(vEmpresa.equalsIgnoreCase("IBANEZ") || vEmpresa.equalsIgnoreCase("IBANEZPRU") ) {
            Btn_Enviarprod.setVisibility(View.VISIBLE);
        }
        final TextView txt_subtotal = (TextView) itemView.findViewById(R.id.txt_subtotalprod);
        final TextView txt_iva = (TextView) itemView.findViewById(R.id.txt_subtotaliva);
        final TextView txt_impoconsumo = (TextView) itemView.findViewById(R.id.txt_retencion);
        final TextView txt_total = (TextView) itemView.findViewById(R.id.txt_neto);

        txt_subtotal.setText("0");
        txt_iva.setText("0");
        txt_impoconsumo.setText("0");
        txt_total.setText("0");

        txt_codigo.setText(SDTProductos[position].Codigo);
        txt_nombre.setText(SDTProductos[position].Nombre);
        txt_nombrecomercial.setText(SDTProductos[position].Nombrecomercial);
        txt_precio.setText(String.format("%.2f",SDTProductos[position].PrecioIva));
        txt_precioneto.setText(String.format("%.2f",SDTProductos[position].PrecioNeto));
        txt_confemp.setText(SDTProductos[position].ConfEmp.toString());
        txt_confprov.setText(SDTProductos[position].ConfProv.toString());
        txt_existencia.setText(SDTProductos[position].Existencia.toString());
        txt_embalaje.setText(SDTProductos[position].Embalaje.toString());
        if (SDTProductos[position].Unidadesinf==0) {
            edit_cajas.setText("");
        }else{
            edit_cajas.setText(SDTProductos[position].Unidadesinf.toString());
        }
        edit_unidades.setText(SDTProductos[position].Unidades.toString());
        if (SDTProductos[position].Unidades==0) {
            edit_unidades.setText("");
        }else{
            edit_unidades.setText(String.valueOf(SDTProductos[position].Unidades));
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
        Presentacion.setText(SDTProductos[position].Presentacion);
        txt_dto8_n.setText(SDTProductos[position].Dct8no.toString());
        txt_poriva.setText(SDTProductos[position].Iva.toString());

        //       spinner_causal.setAdapter(new ArrayAdapter<String>(context,R.layout.support_simple_spinner_dropdown_item,CausalNombre)); // simple_spinner_item



        txt_subtotal.setText(String.format("%.2f",SDTProductos[position].TotSubtotal));
        txt_iva.setText(String.format("%.2f",SDTProductos[position].TotIva));
        txt_impoconsumo.setText(String.format("%.2f",SDTProductos[position].TotImpoconsumo));
        txt_total.setText(String.format("%.2f",SDTProductos[position].Total));
        Double Porcentaje=0.00;
        if(SDTProductos[position].ValorPedidoEnviado>0){
            if(SDTProductos[position].Total < SDTProductos[position].ValorPedidoEnviado ){
                Porcentaje=(SDTProductos[position].Total/SDTProductos[position].ValorPedidoEnviado)*100;
            }else{
                Porcentaje=(SDTProductos[position].ValorPedidoEnviado/SDTProductos[position].Total)*100;
            }

            if (Porcentaje<50){
                txt_porenvio.setBackgroundColor(Color.rgb( 176, 58, 46 ));
                txt_porval.setBackgroundColor(Color.rgb( 176, 58, 46 ));
            }
            if (Porcentaje>50){
                txt_porenvio.setBackgroundColor(Color.rgb( 36, 113, 163 ));
                txt_porval.setBackgroundColor(Color.rgb( 36, 113, 163 ));
            }
            if (Porcentaje>98){
                Porcentaje = 100.00;
                txt_porenvio.setBackgroundColor(Color.rgb(22,160,133));
                txt_porval.setBackgroundColor(Color.rgb(22,160,133));
            }
            txt_porenvio.setText(String.format("%.2f",Porcentaje)+'%');
        }else{
            txt_porenvio.setBackgroundColor(Color.GRAY);
            txt_porval.setBackgroundColor(Color.GRAY);
            txt_porenvio.setText("0%");
        }



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
                    if (Unidades+(Cajas*SDTProductos[position].Embalaje) > Existencia) {
                        Cajas=SDTProductos[position].Cajas;
                        edit_cajas.setText(String.valueOf(SDTProductos[position].Cajas));
                    }else {

                    }
                    SDTProductos[position].Cajas = Cajas;
                    SDTProductos[position].Guardar(1);

                }
                txt_subtotal.setText(String.format("%.2f",SDTProductos[position].TotSubtotal));
                txt_iva.setText(String.format("%.2f",SDTProductos[position].TotIva));
                txt_impoconsumo.setText(String.format("%.2f",SDTProductos[position].TotImpoconsumo));
                txt_total.setText(String.format("%.2f",SDTProductos[position].Total));
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
                if (Unidades+(SDTProductos[position].Cajas*SDTProductos[position].Embalaje) > Existencia) {
                    Unidades=SDTProductos[position].Unidades.intValue();
                    edit_unidades.setText(String.valueOf(SDTProductos[position].Unidades));
                }else{
                    SDTProductos[position].Unidades=Double.valueOf(Unidades);
                    SDTProductos[position].Guardar(1);
                    txt_subtotal.setText(String.format("%.2f",SDTProductos[position].TotSubtotal));
                    txt_iva.setText(String.format("%.2f",SDTProductos[position].TotIva));
                    txt_impoconsumo.setText(String.format("%.2f",SDTProductos[position].TotImpoconsumo));
                    txt_total.setText(String.format("%.2f",SDTProductos[position].Total));
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

        Btn_Enviarprod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  final GestorPedidos GestorPedidos = new GestorPedidos();
                ConBd conbd = new ConBd();
                conbd.Variables();
                String Mensaje = "";

                Intent intent = new Intent(context, ConfirmarEnvio.class);
                intent.putExtra("total", 1);
                intent.putExtra("clisec",SDTProductos[position].pCliSec);
                intent.putExtra("nitsec",SDTProductos[position].pNitSec);
                intent.putExtra("artsec",SDTProductos[position].ArtSec);
                intent.putExtra("prefijo",SDTProductos[position].Prefijo);
                BaseDatos BaseDeDatos;
                BaseDeDatos = new BaseDatos(context, "MantisMovil", null, 5);
                Cursor permisoenvalt =  BaseDeDatos.getReadableDatabase().rawQuery("select EnvAlt from usuarios where VenCnt=1  ", null);
                if(permisoenvalt.getCount()> 0){
                    permisoenvalt.moveToFirst();
                    if(permisoenvalt.getString(0).equalsIgnoreCase("S")){
                        intent.putExtra("envioalt",1);
                    }else{
                        intent.putExtra("envioalt",0);
                    }
                }

                intent.putExtra("position",position);

                ((ResumenPedidoEnv) context).startActivityForResult(intent, 1105);*/
            }
        });
        Btn_Editarprod.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.e("Lista ",String.valueOf( SDTProductos[position].LisPreCod));
/*
                Intent intent = new Intent(context, EditarCantidad.class);
                intent.putExtra("nitsec", SDTProductos[position].pNitSec);
                intent.putExtra("clisec", SDTProductos[position].pCliSec);
                intent.putExtra("lisprecod", SDTProductos[position].LisPreCod);
                intent.putExtra("invgrucod", "");
                intent.putExtra("invsubgrucod", "");
                intent.putExtra("plazo", SDTProductos[position].Plazo);
                intent.putExtra("plazoNom", SDTProductos[position].plazoNom);
                intent.putExtra("prefijo", SDTProductos[position].Prefijo);
                intent.putExtra("artsec", SDTProductos[position].ArtSec);
                intent.putExtra("bodega", SDTProductos[position].bodcod);
                intent.putExtra("NotaInv", SDTProductos[position].NotaInv);
                intent.putExtra("NotaCar", SDTProductos[position].NotaCar);

                intent.putExtra("position", position);





                ((ResumenPedidoEnv) context).startActivityForResult(intent, position);
*/


            }
        });

        TextView txttipo = (TextView) itemView.findViewById(R.id.txttipo);

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
