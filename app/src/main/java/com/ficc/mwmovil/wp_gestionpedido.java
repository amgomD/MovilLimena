package com.ficc.mwmovil;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import  java.util.Date;
import java.util.Locale;
public class wp_gestionpedido extends AppCompatActivity {


    private TextView tvTitulo;
    private TextView tvNombreVendedor;
    private TextView tvFechaConsulta;
    private TextView tvCantidadPedidos;

    private ImageButton btnFechaConsulta;

    private ListView recyclerPedidos;

    private Calendar calendarioSeleccionado;

    private final SimpleDateFormat formatoSQL =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private ArrayList<SdtPedido> listaPedidos;
    private PedidoAdapter pedidoAdapter;

    private Dialog dialogCargando;
    String Vencod ;
    protected void onCreate(Bundle savedInstanceState) {
 super.onCreate(savedInstanceState);
        setContentView(R.layout.wp_alistamiento);
        getSupportActionBar().hide();
        GlobalVariables vGlobalVariables2= GlobalVariables.getInstance();
        Time time = new Time();
        time.setToNow();
        ConBd conbd = new ConBd();
        conbd.Variables();
        BaseDatos vBaseDeDatos;
        vBaseDeDatos = new BaseDatos(getApplicationContext(), "MantisMovil", null, 6);

        inicializarComponentes();


        listaPedidos = new ArrayList<>();
        pedidoAdapter = new PedidoAdapter(
                this,
                listaPedidos,
                new PedidoAdapter.OnFechaGuardadaListener() {
                    @Override
                    public void onFechaGuardada(String numeroPedido, String nuevaFecha,String FacSecRel) {

                        Log.d("PEDIDO", "Pedido: " + numeroPedido);
                        Log.d("PEDIDO", "Nueva fecha: " + nuevaFecha);

                        // Aquí ya tienes los datos para hacer el UPDATE
                        actualizarFechaPedido(numeroPedido, nuevaFecha,FacSecRel);
                    }
                }
        );
        recyclerPedidos.setAdapter(pedidoAdapter);
        inicializarFechaActual();
        eventos();
        String vendedor ="select Vencod, Vennom from usuarios";
        Cursor infovendedor = vBaseDeDatos.getReadableDatabase().rawQuery(vendedor , null);
        infovendedor.moveToFirst();
        if (infovendedor.getCount() == 1) {
            tvNombreVendedor.setText(infovendedor.getString(1));
            Vencod = infovendedor.getString(0);
        }

        cargarPedidos();






    }
    private void inicializarComponentes() {

        // TextViews
        tvTitulo = findViewById(R.id.tvTitulo);
        tvNombreVendedor = findViewById(R.id.tvNombreVendedor);
        tvFechaConsulta = findViewById(R.id.tvFechaConsulta);
        tvCantidadPedidos = findViewById(R.id.tvCantidadPedidos);

        // Botón
        btnFechaConsulta = findViewById(R.id.btnFechaConsulta);

        // Lista
        recyclerPedidos = findViewById(R.id.recyclerPedidos);
    }

    private void cargarPedidos() {

        mostrarCargando();

        new Thread(() -> {

            cargarlista();

            runOnUiThread(() -> ocultarCargando());

        }).start();
    }


    private void actualizarFechaPedido(String FacSec, String nuevaFecha,String FacSecRel) {

        new Thread(() -> {

            Connection conn = null;
            PreparedStatement statement = null;

            try {

                ConBd conbd = new ConBd();
                conn = conbd.CargarConexion(getApplicationContext());



                String sql = "  UPDATE factura SET FacFecEnt = ? WHERE FACSEC = ? ";
                String sqlRel = "  UPDATE factura SET FacFecEnt = ? WHERE FACSECREL = ? and ConNotCod in ('13','11')  ";

                statement = conn.prepareStatement(sql);

                statement.setString(1, nuevaFecha);
                statement.setString(2, FacSec);

                int filas = statement.executeUpdate();


                statement = conn.prepareStatement(sqlRel);

                statement.setString(1, nuevaFecha);
                statement.setString(2, FacSecRel);

                int filasre  = statement.executeUpdate();



                runOnUiThread(() -> {

                    if (filas > 0) {
                        Toast.makeText(
                                this,
                                "Fecha actualizada correctamente",
                                Toast.LENGTH_SHORT
                        ).show();

                        cargarPedidos();
                    } else {
                        Toast.makeText(
                                this,
                                "No se encontró el pedido",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });

            } catch (Exception e) {

                Log.e("UPDATE_PEDIDO", "Error actualizando fecha", e);

                runOnUiThread(() ->
                        Toast.makeText(
                                this,
                                "Error al actualizar la fecha",
                                Toast.LENGTH_SHORT
                        ).show()
                );

            } finally {

                try {
                    if (statement != null) statement.close();
                } catch (Exception ignored) {}

                try {
                    if (conn != null) conn.close();
                } catch (Exception ignored) {}
            }

        }).start();
    }



    public void cargarlista() {

        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rsClientes = null;

        try {

            String fechaSeleccionada =
                    tvFechaConsulta.getText().toString();

            ConBd conbd = new ConBd();

            conn = conbd.CargarConexion(getApplicationContext());

            String sql = "SELECT " +
                    " CASE \n" +
                    "    WHEN f.FacFecEnt IS NULL\n" +
                    "         OR f.FacFecEnt = '1753-01-01'\n" +
                    "    THEN ''\n" +
                    "    ELSE CONVERT(VARCHAR(10), f.FacFecEnt, 23)\n" +
                    "END AS NuevaFecha ,f.facnro,f.FacSec, " +
                    "f.facfectra AS FechaCreacionPedido, " +
                    "N.nitide, " +
                    "nitcom rutdes,FacSecRel, " +
                    "CASE WHEN (FACALIMER='N') THEN 'SI' ELSE 'NO' END ALISTADO, " +
                    "CASE WHEN (FACEMPMER='N') THEN 'SI' ELSE 'NO' END EMPACADO, " +
                    "CAST(prox.FechaInicio AS DATE) AS FECHA_DE_ALISTAMIENTO " +
                    "FROM factura f WITH (NOLOCK) " +
                    "INNER JOIN tipos t WITH (NOLOCK) ON t.tipcod=f.FacTipCod " +
                    "INNER JOIN NIT N WITH (NOLOCK) ON N.NITSEC=FacNitSec " +
                    "INNER JOIN clientes c WITH (NOLOCK) ON c.nitsec=f.FacNitSec " +
                    "AND f.FacCliSec=c.clisec " +
                    "INNER JOIN ruta r WITH (NOLOCK) ON r.rutcod=c.rutcod " +
                    "OUTER APPLY ( " +
                    "    SELECT TOP 1 rf.RutFecIni FechaInicio, rf.RutFecFin FechaFinal " +
                    "    FROM RutaRutaFecha rf " +
                    "    WHERE rf.rutcod=r.rutcod " +
                    "    AND rf.RutFecFin >= f.facfectra " +
                    "    ORDER BY rf.RutFecFin ASC " +
                    ") prox " +
                    "WHERE Facvencod =? and fuecod='REMI' " +
                    "AND FACFEC=? " +
                    "AND FACEST='A'";

            statement = conn.prepareStatement(sql);

            statement.setString(1, Vencod);
            statement.setString(2, fechaSeleccionada);

            rsClientes = statement.executeQuery();


            // Limpiar lista anterior
            listaPedidos.clear();


            while (rsClientes.next()) {
                String FacSec =  rsClientes.getString("FacSec");
                String FacSecRel =  rsClientes.getString("FacSecRel");

                String facdocfec =
                        rsClientes.getString("NuevaFecha");
                String facnro =
                        rsClientes.getString("facnro");

                String fechaCreacion =
                        rsClientes.getString("FechaCreacionPedido");

                String nitide =
                        rsClientes.getString("nitide");

                String rutdes =
                        rsClientes.getString("rutdes");

                String alistado =
                        rsClientes.getString("ALISTADO");

                String empacado =
                        rsClientes.getString("EMPACADO");

                String fechaAlistamiento =
                        rsClientes.getString("FECHA_DE_ALISTAMIENTO");


                SdtPedido pedido = new SdtPedido(
                        facnro,
                        fechaCreacion,
                        nitide,
                        rutdes,
                        alistado,
                        empacado,
                        fechaAlistamiento,
                        facdocfec,FacSec,FacSecRel
                );

                listaPedidos.add(pedido);
            }


            runOnUiThread(() -> {

                pedidoAdapter.notifyDataSetChanged();

                tvCantidadPedidos.setText(
                        listaPedidos.size() + " pedidos"
                );

            });


        } catch (Exception e) {

            Log.e("Errorclif2icce", e.toString());

        } finally {

            try {
                if (rsClientes != null)
                    rsClientes.close();
            } catch (Exception errorRS) {
                errorRS.printStackTrace();
            }

            try {
                if (statement != null)
                    statement.close();
            } catch (Exception errorST) {
                errorST.printStackTrace();
            }

            try {
                if (conn != null)
                    conn.close();
            } catch (Exception errorCONN) {
                errorCONN.printStackTrace();
            }
        }
    }



    private void inicializarFechaActual() {

        // Obtiene la fecha actual
        calendarioSeleccionado = Calendar.getInstance();
        // Mostrar fecha en formato SQL
        actualizarFecha();
    }
    private void eventos() {
        btnFechaConsulta.setOnClickListener(v -> abrirCalendario());
    }

    private void abrirCalendario() {

        int anio = calendarioSeleccionado.get(Calendar.YEAR);
        int mes = calendarioSeleccionado.get(Calendar.MONTH);
        int dia = calendarioSeleccionado.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {

                    calendarioSeleccionado.set(
                            year,
                            month,
                            dayOfMonth
                    );

                    actualizarFecha();
                    cargarPedidos();
                    // Aquí puedes volver a consultar los pedidos
                    // cargarPedidos();

                },
                anio,
                mes,
                dia
        );

        datePickerDialog.show();
    }


    private void actualizarFecha() {

        String fechaSQL =
                formatoSQL.format(calendarioSeleccionado.getTime());

        tvFechaConsulta.setText(fechaSQL);
    }

    private void mostrarCargando() {

        dialogCargando = new Dialog(this);
        dialogCargando.setContentView(R.layout.dialog_cargando);

        dialogCargando.setCancelable(false);

        if (dialogCargando.getWindow() != null) {
            dialogCargando.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );
        }

        dialogCargando.show();
    }

    private void ocultarCargando() {

        if (dialogCargando != null && dialogCargando.isShowing()) {
            dialogCargando.dismiss();
        }
    }

}
