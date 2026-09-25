package com.ficc.mwmovil;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class PedidoAdapter extends ArrayAdapter<SdtPedido> {

    private Context context;
    private ArrayList<SdtPedido> listaPedidos;
    private OnFechaGuardadaListener listener;

    public interface OnFechaGuardadaListener {
        void onFechaGuardada(String numeroPedido, String nuevaFecha,String FacSecRel);
    }

    public PedidoAdapter(Context context, ArrayList<SdtPedido> listaPedidos, OnFechaGuardadaListener listener) {
        super(context, 0, listaPedidos);

        this.context = context;
        this.listaPedidos = listaPedidos;
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.item_pedido,parent, false);


        SdtPedido pedido = listaPedidos.get(position);

        TextView tvNumeroPedido =
                itemView.findViewById(R.id.tvNumeroPedido);

        TextView tvPendiente =
                itemView.findViewById(R.id.tvPendiente);



        TextView tvCliente =
                itemView.findViewById(R.id.tvCliente);

        TextView tvRuta =
                itemView.findViewById(R.id.tvRuta);

        TextView tvFechaCreacion =
                itemView.findViewById(R.id.tvFechaCreacion);

        TextView tvAlistado =
                itemView.findViewById(R.id.tvAlistado);

        TextView tvEmpacado =
                itemView.findViewById(R.id.tvEmpacado);

        TextView tvFechaAlistamiento =
                itemView.findViewById(R.id.tvFechaAlistamiento);
        ImageButton btnNuevaFecha =
                itemView.findViewById(R.id.btnNuevaFecha);

        Button btnGuardarFecha =
                itemView.findViewById(R.id.btnGuardarFecha);

        TextView tvNuevaFecha =
                itemView.findViewById(R.id.tvNuevaFecha);




        // ==========================
        // LLENAR DATOS
        // ==========================

        tvNumeroPedido.setText("#" + pedido.getFacnro());

        tvCliente.setText("ID: " + pedido.getNitide());

        tvRuta.setText("Ruta: " + pedido.getRutdes());

        tvFechaCreacion.setText(
                "Creado: " + pedido.getFechaCreacion()
        );


        tvAlistado.setVisibility(View.GONE);
        tvEmpacado.setVisibility(View.GONE);
        tvPendiente.setVisibility(View.VISIBLE);

        if(pedido.getAlistado().equalsIgnoreCase("SI")){
            tvPendiente.setVisibility(View.GONE);

   tvAlistado.setVisibility(View.VISIBLE);
        }

        if(pedido.getEmpacado().equalsIgnoreCase("SI")){
            tvEmpacado.setVisibility(View.VISIBLE);
            tvPendiente.setVisibility(View.GONE);

        }


        tvAlistado.setText(
                "ALISTADO: " + pedido.getAlistado()
        );

        tvEmpacado.setText(
                "EMPACADO: " + pedido.getEmpacado()
        );




        tvFechaAlistamiento.setText(

                        pedido.getFechaAlistamiento()
        );


        // IMPORTANTE:
        // Como ListView recicla las vistas, debemos
        // volver a asignar la fecha guardada.

        if (pedido.getFechaNueva() == null ||
                pedido.getFechaNueva().isEmpty()) {

            tvNuevaFecha.setText("Nueva fecha");

        } else {

            tvNuevaFecha.setText(pedido.getFechaNueva());
        }

        // ==========================
        // ABRIR CALENDARIO
        // ==========================

        btnNuevaFecha.setOnClickListener(v -> {

            Calendar calendario = Calendar.getInstance();

            int anio = calendario.get(Calendar.YEAR);
            int mes = calendario.get(Calendar.MONTH);
            int dia = calendario.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog =
                    new DatePickerDialog(
                            context,
                            (view, year, month, dayOfMonth) -> {

                                Calendar fechaSeleccionada =
                                        Calendar.getInstance();

                                fechaSeleccionada.set(
                                        year,
                                        month,
                                        dayOfMonth
                                );


                                SimpleDateFormat formato =
                                        new SimpleDateFormat(
                                                "yyyy-MM-dd",
                                                Locale.getDefault()
                                        );


                                String fecha =
                                        formato.format(
                                                fechaSeleccionada.getTime()
                                        );


                                // Guardar en objeto
                                pedido.setFechaNueva(fecha);

                                // Mostrar en item
                                tvNuevaFecha.setText(fecha);

                            },
                            anio,
                            mes,
                            dia
                    );

            datePickerDialog.show();

        });

        // ==========================
        // GUARDAR FECHA
        // ==========================

        btnGuardarFecha.setOnClickListener(v -> {

            String nuevaFecha = pedido.getFechaNueva();

            if (nuevaFecha == null || nuevaFecha.isEmpty()) {

                Toast.makeText(
                        context,
                        "Seleccione una nueva fecha",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            mostrarConfirmacion(
                    pedido,
                    nuevaFecha
            );


        });


        return itemView;
    }

    private void mostrarConfirmacion(
            SdtPedido pedido,
            String nuevaFecha
    ) {

        Dialog dialog = new Dialog(context);

        dialog.setContentView(
                R.layout.popup_confirmar_fecha
        );

        dialog.setCancelable(false);


        TextView tvPedidoPopup =
                dialog.findViewById(
                        R.id.tvPedidoPopup
                );

        TextView tvFechaActualPopup =
                dialog.findViewById(
                        R.id.tvFechaActualPopup
                );

        TextView tvNuevaFechaPopup =
                dialog.findViewById(
                        R.id.tvNuevaFechaPopup
                );

        Button btnCancelar =
                dialog.findViewById(
                        R.id.btnCancelar
                );

        Button btnConfirmar =
                dialog.findViewById(
                        R.id.btnConfirmar
                );


        tvPedidoPopup.setText(
                "Pedido #" + pedido.getFacnro()
        );

        tvFechaActualPopup.setText(
                pedido.getFechaAlistamiento()
        );

        tvNuevaFechaPopup.setText(
                nuevaFecha
        );


        btnCancelar.setOnClickListener(v ->
                dialog.dismiss()
        );


        btnConfirmar.setOnClickListener(v -> {

            String FacSec = pedido.getFacSec();
            String cnuevaFecha = pedido.getFechaNueva();
            String FacSecRel = pedido.getFacSecRel();

            if (listener != null) {
                listener.onFechaGuardada(FacSec, cnuevaFecha,FacSecRel);
            }

            dialog.dismiss();

        });


        dialog.show();


        if (dialog.getWindow() != null) {

            dialog.getWindow()
                    .setBackgroundDrawable(
                            new ColorDrawable(Color.TRANSPARENT)
                    );

            dialog.getWindow()
                    .setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
        }
    }




}