package com.ficc.mwmovil;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListViewAdapterClientes extends BaseAdapter implements Filterable {

    Context context;
    SDTClientes[] SDTClientes;
    SDTClientes[] SDTClientesOriginal = null;
    ClientesPedido pClientesPedido;

    ///private ItemFilter mFilter = new ItemFilter();

    public ListViewAdapterClientes(Context context,SDTClientes[] SDTClientes,ClientesPedido ClientesPedido) {
        this.context = context;
        this.SDTClientes = SDTClientes;
        this.SDTClientesOriginal= SDTClientes;
        this.pClientesPedido=ClientesPedido;
    }

    @Override
    public int getCount() {
    //    if (SDTClientes.length>25){
    //        return 25;
    //    }else {
            return SDTClientes.length;
    //    }
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.listarclientespedidos,viewGroup, false);



        Switch switch_terminado = (Switch) itemView.findViewById(R.id.switch_terminado);
        TextView txt_nit = (TextView) itemView.findViewById(R.id.txt_nit);
        TextView txt_Nombre = (TextView) itemView.findViewById(R.id.txt_Nombre);
        TextView txt_nombrenegocio = (TextView) itemView.findViewById(R.id.txt_nombrenegocio);
        TextView txt_direccion = (TextView) itemView.findViewById(R.id.txt_direccion);
        TextView txt_causal = (TextView) itemView.findViewById(R.id.txt_causal);
        TextView txt_ciudad = (TextView) itemView.findViewById(R.id.txt_ciudad);
        TextView txt_valpedido = (TextView) itemView.findViewById(R.id.txt_valpedido);
        TextView txt_lun = (TextView) itemView.findViewById(R.id.txt_lun);
        TextView txt_mar = (TextView) itemView.findViewById(R.id.txt_mar);
        TextView txt_mie = (TextView) itemView.findViewById(R.id.txt_mie);
        TextView txt_jue = (TextView) itemView.findViewById(R.id.txt_jue);
        TextView txt_vie = (TextView) itemView.findViewById(R.id.txt_vie);
        TextView txt_sab = (TextView) itemView.findViewById(R.id.txt_sab);
        TextView txt_dom = (TextView) itemView.findViewById(R.id.txt_dom);
        TextView txt_orden = (TextView) itemView.findViewById(R.id.txt_orden);
        TextView txt_frecuencia = (TextView) itemView.findViewById(R.id.txt_frecuencia);
        TextView txt_cartera = (TextView) itemView.findViewById(R.id.txt_cartera);

        try {
            txt_nit.setText("Nit:"+SDTClientes[i].NitIde+" Tel:"+SDTClientes[i].CliTel);
            txt_ciudad.setText(SDTClientes[i].CliCiudad);
            txt_Nombre.setText("("+SDTClientes[i].NitIde+") "+SDTClientes[i].NitCom);
            txt_nombrenegocio.setText(SDTClientes[i].CliNom);
            txt_direccion.setText(SDTClientes[i].CliDir);
            String hh=SDTClientes[i].Causal;
            txt_causal.setText(SDTClientes[i].Causal);
            txt_frecuencia.setText(SDTClientes[i].FreNom);
            txt_orden.setText(SDTClientes[i].CliIntOrdDet.toString());
            txt_cartera.setText(String.format("%,d",SDTClientes[i].Cartera));

            if (SDTClientes[i].Cliintlun.equalsIgnoreCase("S"))
            {txt_lun.setBackgroundColor(Color.MAGENTA);}
            if (SDTClientes[i].Cliintmar.equalsIgnoreCase("S"))
            {txt_mar.setBackgroundColor(Color.MAGENTA);}
            if (SDTClientes[i].Cliintmie.equalsIgnoreCase("S"))
            {txt_mie.setBackgroundColor(Color.MAGENTA);}
            if (SDTClientes[i].Cliintjue.equalsIgnoreCase("S"))
            {txt_jue.setBackgroundColor(Color.MAGENTA);}
            if (SDTClientes[i].Cliintvie.equalsIgnoreCase("S"))
            {txt_vie.setBackgroundColor(Color.MAGENTA);}
            if (SDTClientes[i].Cliintsab.equalsIgnoreCase("S"))
            {txt_sab.setBackgroundColor(Color.MAGENTA);}
            if (SDTClientes[i].Cliintdom.equalsIgnoreCase("S"))
            {txt_dom.setBackgroundColor(Color.MAGENTA);}

            try {
                txt_valpedido.setText( String.format("%.2f",SDTClientes[i].TotalPedido));
                if (SDTClientes[i].TotalPedido.intValue()>0) {
                    switch_terminado.setChecked(true);
                }
            }catch(Exception e){
                if (!txt_causal.getText().toString().equals("")) {
                    switch_terminado.setChecked(true);
                }
            }
            //int lentext=;
           // if (SDTClientes[i].TotalPedido.intValue()>0 || !txt_causal.getText().toString().equals("")) {
             //   switch_terminado.setChecked(true);
           // }
            //txt_direccion.setText(SDTClientes[i].CliDir);
        }catch (Exception e)
        {
            int lentext=0;
        }
        return itemView;
    }

    @Override


    public Filter getFilter() {

        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    results.values = SDTClientesOriginal;
                    results.count = SDTClientesOriginal.length;
                    return results;
                }

                String filterString = normalizar(constraint.toString());

                List<SDTClientes> filtrados = new ArrayList<>();

                for (SDTClientes c : SDTClientesOriginal) {

                    String texto = normalizar(
                            c.NitCom + " " +
                                    c.NitIde + " " +
                                    c.CliNom + " " +
                                    c.CliCiudad
                    );

                    if (texto.contains(filterString)) {
                        filtrados.add(c);
                    }
                }

                SDTClientes[] nlist = filtrados.toArray(new SDTClientes[0]);

                results.values = nlist;
                results.count = nlist.length;

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                SDTClientes = (SDTClientes[]) results.values;
                pClientesPedido.SDTClientes = SDTClientes;
                notifyDataSetChanged();
            }
        };
    }



    private String normalizar(String texto) {

        texto = texto.toLowerCase();

        texto = texto.replace("#", " ");
        texto = texto.replace("(", " ");
        texto = texto.replace(")", " ");

        texto = texto.replaceAll("\\s+", " ");

        return texto.trim();
    }

    /*public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String filterString = constraint.toString().toLowerCase();

                FilterResults results = new FilterResults();

                final SDTClientes[] list = SDTClientesOriginal;

                int count = list.length;



                String filterableString;

                Integer Vueltas=0;
                for (int i = 0; i < count; i++) {
                    filterableString = list[i].NitCom+list[i].NitIde+list[i].CliNom+list[i].CliCiudad;
                    if (filterableString.toLowerCase().contains(filterString)) {
                        Vueltas+=1;
                    }
                }

                final SDTClientes[] nlist = new SDTClientes[Vueltas];

                Vueltas=0;
                for (int i = 0; i < count; i++) {
                    filterableString = list[i].NitCom+list[i].NitIde+list[i].CliNom+list[i].CliCiudad;
                    if (filterableString.toLowerCase().contains(filterString)) {
                        nlist[Vueltas] = (SDTClientesOriginal[i]);
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
                    SDTClientes = (SDTClientes[]) results.values;
                    pClientesPedido.SDTClientes=SDTClientes;
                    //Integer pp= TmpSDTClientes.length;
                    //Integer pp2= TmpSDTClientes.length;
                    notifyDataSetChanged();
               // }
            }

        };
    }*/

}
