package com.ficc.mwmovil;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class AdapterProductosBon extends BaseAdapter {

    Context context;
    List<SDTProductosBon> lista;

    int posicionSeleccionada = -1;
    LayoutInflater inflater;

    public AdapterProductosBon(Context context, List<SDTProductosBon> lista) {
        this.context = context;
        this.lista = lista;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView txt_codigo, txt_nombre, Presentacion, edit_cajas,PrecioBon,txtGrupo,checkpre;
        EditText edit_unidades;
        Button btnsumar, btnrestar;
        CheckBox chkPredeterminado;
        TextWatcher watcher;
        int cantidadBon = 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listarproductosbon, null);

            holder = new ViewHolder();
            holder.txt_codigo = convertView.findViewById(R.id.txt_codigo);
            holder.txt_nombre = convertView.findViewById(R.id.txt_nombre);
            holder.Presentacion = convertView.findViewById(R.id.Presentacion);
            holder.edit_cajas = convertView.findViewById(R.id.edit_cajas);
            holder.edit_unidades = convertView.findViewById(R.id.edit_unidadesbon);
            holder.btnsumar = convertView.findViewById(R.id.btnsumar);
            holder.btnrestar = convertView.findViewById(R.id.btnrestar);
            holder.chkPredeterminado = convertView.findViewById(R.id.chkPredeterminado);
            holder.PrecioBon = convertView.findViewById(R.id.preciobon);
            holder.txtGrupo = convertView.findViewById(R.id.txtGrupo);
            holder.checkpre = convertView.findViewById(R.id.checkpre);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SDTProductosBon item = lista.get(position);

        holder.checkpre.setOnClickListener(v -> {
            holder.chkPredeterminado.performClick();
        });


        // Mostrar título de grupo si cambia el BonDesc
        if (position == 0) {
            holder.txtGrupo.setVisibility(View.VISIBLE);
            holder.txtGrupo.setText(item.BonDesc);
        } else {
            String grupoActual = item.BonDesc;
            String grupoAnterior = lista.get(position - 1).BonDesc;

            if (!grupoActual.equals(grupoAnterior)) {
                holder.txtGrupo.setVisibility(View.VISIBLE);
                holder.txtGrupo.setText(grupoActual);
            } else {
                holder.txtGrupo.setVisibility(View.GONE);
            }
        }


// Quitar watcher anterior
        if (holder.watcher != null) {
            holder.edit_unidades.removeTextChangedListener(holder.watcher);
        }
        holder.txt_codigo.setText(item.ArtCod);
        holder.txt_nombre.setText(item.ArtNom);
        holder.Presentacion.setText(item.PreArtNom);
        holder.edit_cajas.setText(String.valueOf(item.CantidadGen));

        holder.edit_unidades.setText(String.valueOf(item.Cantidad));
        holder.PrecioBon.setText("$" + String.format("%.2f", item.PrePrefijval));


        // Check predeterminado
        if ("S".equals(item.aplicadobon)) {
            holder.btnrestar.setVisibility(View.VISIBLE);
            holder.btnsumar.setVisibility(View.VISIBLE);
            holder.edit_unidades.setEnabled(true);


        } else {

            holder.btnrestar.setVisibility(View.INVISIBLE);
            holder.btnsumar.setVisibility(View.INVISIBLE);
            holder.edit_unidades.setEnabled(false);

        }
        holder.chkPredeterminado.setChecked("S".equals(item.aplicadobon));


        // SUMAR
        holder.btnsumar.setOnClickListener(v -> {
            int valor = 0;
            try {
                valor = Integer.parseInt(holder.edit_unidades.getText().toString());
            } catch (Exception e) {
            }

            valor++;
            holder.edit_unidades.setText(String.valueOf(valor));
            // item.Cantidad = valor;
        });

        // RESTAR
        holder.btnrestar.setOnClickListener(v -> {
            int valor = 0;
            try {
                valor = Integer.parseInt(holder.edit_unidades.getText().toString());
            } catch (Exception e) {
            }

            if (valor > 0) valor--;
            holder.edit_unidades.setText(String.valueOf(valor));
            //  item.Cantidad = valor;
        });


       /* holder.watcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                int valor = 0;
                try {
                    valor = Integer.parseInt(s.toString());
                } catch (Exception e) {}

                if (valor > item.CantidadGen) {
                    valor = item.CantidadGen;
                    holder.edit_unidades.setText(String.valueOf(valor));
                    holder.edit_unidades.setSelection(holder.edit_unidades.getText().length());
                }

                item.Cantidad = valor;
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        };*/


        holder.watcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

                int valor = 0;
                try {
                    valor = Integer.parseInt(s.toString());
                } catch (Exception e) {
                }

                int totalGrupo = 0;

                // Sumar las cantidades de los demás seleccionados del grupo
                for (int i = 0; i < lista.size(); i++) {
                    if (lista.get(i).MovParPremSec == item.MovParPremSec
                            && "S".equals(lista.get(i).aplicadobon)
                            && lista.get(i) != item) {

                        totalGrupo += lista.get(i).Cantidad;
                    }
                }

                // Validar que no exceda el máximo del grupo
                if (valor + totalGrupo > item.CantidadGen) {
                    valor = item.CantidadGen - totalGrupo;

                    if (valor < 0) {
                        valor = 0;
                    }
                    //holder.chkPredeterminado.setChecked(false);
                    holder.edit_unidades.removeTextChangedListener(holder.watcher);
                    holder.edit_unidades.setText(String.valueOf(valor));
                    holder.edit_unidades.setSelection(holder.edit_unidades.getText().length());
                    holder.edit_unidades.addTextChangedListener(holder.watcher);
                }

                item.Cantidad = valor;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };





        holder.edit_unidades.addTextChangedListener(holder.watcher);


       /* holder.chkPredeterminado.setOnClickListener(v -> {

            int secSeleccionado = item.MovParPremSec;
            int cantidadAnterior = 0;

            // 1. Buscar el que estaba marcado antes en ese grupo
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).MovParPremSec == secSeleccionado
                        && "S".equals(lista.get(i).aplicadobon)) {

                    cantidadAnterior = lista.get(i).Cantidad;
                    break;
                }
            }

            // 2. Desmarcar todos los del grupo y marcar el nuevo
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).MovParPremSec == secSeleccionado) {

                    if (i == position) {
                        lista.get(i).aplicadobon = "S";

                        // 3. Pasar la cantidad anterior al nuevo seleccionado
                        if (cantidadAnterior > 0) {
                            lista.get(i).Cantidad = cantidadAnterior;
                        }

                    } else {
                        lista.get(i).aplicadobon = "N";
                        lista.get(i).Cantidad = 0; // opcional: limpiar al desmarcar
                    }
                }
            }

            notifyDataSetChanged();
        });
        */

        holder.chkPredeterminado.setOnClickListener(v -> {

            int secSeleccionado = item.MovParPremSec;
            int cantidadSeleccionada = 0;

            // Sumar las cantidades de todos los seleccionados del grupo
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).MovParPremSec == secSeleccionado
                        && "S".equals(lista.get(i).aplicadobon)
                       ) {

                    cantidadSeleccionada += lista.get(i).Cantidad;
                }
            }

            if ("N".equals(item.aplicadobon)) {

                // Validar que no exceda la cantidad disponible
                if (cantidadSeleccionada + item.Cantidad >= item.CantidadGen) {
                    holder.chkPredeterminado.setChecked(false);
                    Toast.makeText(context,
                            "Solo puede seleccionar " + item.CantidadGen + " unidades.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                item.aplicadobon = "S";

            } else {

                item.aplicadobon = "N";
                item.Cantidad = 0;
            }

            notifyDataSetChanged();
        });










        return convertView;
    }
}