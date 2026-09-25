package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListViewAdapterProductosv2 extends BaseAdapter implements Filterable {

    Context context;
    public Activity activity;
    SDTProductos[] SDTProductos;
    SDTProductos[] SDTProductosOriginal;
    EditarProductoV2 pCapturaPedido;
    String[] CausalNombre;
    Double totalpedido =0.0;
    public ListViewAdapterProductosv2(Activity activity,Context context, SDTProductos[] SDTProductos, EditarProductoV2 pEditarProductoV2, String[] CausalNombre) {
        this.context = context;
        this.activity = activity;
        this.SDTProductos = SDTProductos;
        this.SDTProductosOriginal=SDTProductos;
        this.pCapturaPedido=pEditarProductoV2;
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
        final View itemView = inflater.inflate(R.layout.wp_listarproductos,parent, false);

        LinearLayout LinearLayout2 = (LinearLayout) itemView.findViewById(R.id.layoutfondo);
        LinearLayout controles = (LinearLayout) itemView.findViewById(R.id.controles);
        FrameLayout contendorproducto = (FrameLayout) itemView.findViewById(R.id.contendorproducto);


        int modulo= position % 2;
       /* if (modulo!=0){
            LinearLayout2.setBackgroundColor(Color.LTGRAY);
        }else{
            LinearLayout2.setBackgroundColor(Color.WHITE);
        }
        if (SDTProductos[position].Bloqueado!=0){
            LinearLayout2.setBackgroundColor(Color.RED);
        }*/

       TextView txt_codigo = (TextView) itemView.findViewById(R.id.txt_codigo);
        TextView txtCategoria = (TextView) itemView.findViewById(R.id.txtCategoria);
        TextView txt_nombre = (TextView) itemView.findViewById(R.id.txt_nombre);
        TextView txt_Presentacion = (TextView) itemView.findViewById(R.id.txt_Presentacion);
        TextView Historialtext = (TextView) itemView.findViewById(R.id.Historialtext);


       // TextView txt_nombrecomercial = (TextView) itemView.findViewById(R.id.txt_nombrecomercial);
        TextView txt_precio = (TextView) itemView.findViewById(R.id.txt_abonoori);

       // TextView txt_dcto1 = (TextView) itemView.findViewById(R.id.txt_dcto1);
      //  TextView txt_dcto2 = (TextView) itemView.findViewById(R.id.txt_dcto2);

        TextView txt_iva = (TextView) itemView.findViewById(R.id.txt_retencion);
        TextView txt_precioneto = (TextView) itemView.findViewById(R.id.txt_neto);
        TextView txt_existencia = (TextView) itemView.findViewById(R.id.txt_existencia);

        Button btnsumar = itemView.findViewById(R.id.btnsumar);
        Button btnrestar= itemView.findViewById(R.id.btnrestar);
        EditText txtUnidades = itemView.findViewById(R.id.txtUnidades);
        ImageView imgbono = (ImageView) itemView.findViewById(R.id.imgbono);
        ImageView imagenproducto = (ImageView) itemView.findViewById(R.id.imagenproducto);

       // TextView txt_embalaje = (TextView) itemView.findViewById(R.id.txt_embalaje);

        ImageView imgdcto = (ImageView) itemView.findViewById(R.id.imgdcto);


        if(SDTProductos[position].TienePromo.equalsIgnoreCase("S")){
            imgdcto.setVisibility(View.VISIBLE);
        }else{
            imgdcto.setVisibility(View.GONE);
        }

        if(SDTProductos[position].TieneBono.equalsIgnoreCase("S")){
            imgbono.setVisibility(View.VISIBLE);
        }else{
            imgbono.setVisibility(View.GONE);
        }

        byte[] img = SDTProductos[position].ArtImgBlob;

        if (img != null && img.length > 0) {
            Bitmap bmp = BitmapFactory.decodeByteArray(img, 0, img.length);
            imagenproducto.setImageBitmap(bmp);
        } else {
            imagenproducto.setImageResource(R.mipmap.sinimagenn);
        }


       // txt_embalaje.setText(String.valueOf(SDTProductos[position].Embalaje));
        txt_codigo.setText(SDTProductos[position].Codigo);
        txt_nombre.setText(SDTProductos[position].Nombre);
        txt_Presentacion.setText(SDTProductos[position].Presentacion);
        txtUnidades.setText(SDTProductos[position].Unidades.toString());

        txtCategoria.setText(SDTProductos[position].Nombrecomercial);
       // txt_nombrecomercial.setText(SDTProductos[position].Nombrecomercial);

        if (SDTProductos[position].Dct1>0) {
           // txt_dcto1.setText(SDTProductos[position].Dct1.toString()+"%");
        }
        if (SDTProductos[position].Dct2>0) {
           // txt_dcto2.setText(SDTProductos[position].Dct2.toString()+"%");
        }

        Historialtext.setText(SDTProductos[position].Historico);




        txt_existencia.setText(String.format( "%.2f",SDTProductos[position].Existencia).replace(",","."));
        txt_precio.setText(String.valueOf(SDTProductos[position].Precio));
        txt_iva.setText('%'+String.valueOf(SDTProductos[position].Iva));
        txt_precioneto.setText (String.format( "%.2f",SDTProductos[position].PrecioNeto));

        controles.setVisibility(View.VISIBLE);
        if(SDTProductos[position].ArtIndMpm.equalsIgnoreCase("S") &&
                SDTProductos[position].NotaInv.equalsIgnoreCase("N")
                && SDTProductos[position].NotaCar.equalsIgnoreCase("N")){

            controles.setVisibility(View.GONE);
        }


        LinearLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SDTProductos[position].ArtIndMpm.equalsIgnoreCase("S") &&
                        SDTProductos[position].NotaInv.equalsIgnoreCase("N")
                        && SDTProductos[position].NotaCar.equalsIgnoreCase("N")){

                    AlertDialog.Builder Alerta = new AlertDialog.Builder(view.getContext());
                    Alerta.setMessage("Articulo marcado como descontinuado para venta, solo se puede para creditos ");
                    Alerta.setTitle("Alerta");
                    Alerta.setPositiveButton("OK", null);
                    Alerta.setCancelable(true);
                    Alerta.create().show();

                }else{
                    Intent intent = new Intent(context, EditarCantidad.class);
                    if(SDTProductos[position].ArtPesFac.equalsIgnoreCase("S")){
                        intent = new Intent(context, EditarCantidadInf.class);
                    }
                    Log.e("lisprecod editarc",String.valueOf(SDTProductos[position].LisPrecod));
                    intent.putExtra("nitsec", SDTProductos[position].NitSec);
                    intent.putExtra("clisec", SDTProductos[position].CliSec);
                    intent.putExtra("lisprecod", SDTProductos[position].LisPrecod);
                    intent.putExtra("invgrucod", "");
                    intent.putExtra("invsubgrucod", "");
                    intent.putExtra("invfamcod", "");
                    intent.putExtra("prefijo",SDTProductos[position].Prefijo);
                    intent.putExtra("artsec", SDTProductos[position].ArtSec);
                    intent.putExtra("plazo", SDTProductos[position].Plazo);
                    intent.putExtra("plazoNom", SDTProductos[position].plazoNom);
                    intent.putExtra("bodega", SDTProductos[position].bodcod);
                    intent.putExtra("NotaInv", SDTProductos[position].NotaInv);
                    intent.putExtra("NotaCar", SDTProductos[position].NotaCar);
                    intent.putExtra("PreArtCod", SDTProductos[position].PreArtCod);

                    intent.putExtra("position", position);
                    activity.startActivityForResult(intent, 3);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.no_anim);
                }

            }
        });


        // totalpedido = totales(SDTProductos[position].Prefijo,SDTProductos[position].NitSec,SDTProductos[position].CliSec,context);

        totalpedido = SDTProductos[position].totalpedido;
        btnsumar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double Unidades  =0.0;
        if(SDTProductos[position].Precio > 0){


                if(txtUnidades.getText().toString().equals("")){

                }else{
                    Unidades = Double.valueOf(txtUnidades.getText().toString());
                }
                Unidades +=1;

            if(SDTProductos[position].NotaInv.equalsIgnoreCase("S")) {
                if (SDTProductos[position].Historico.isEmpty()) {
                    if(Unidades>0){
                        Notificacion.aviso(
                                context,
                                "Se está realizando un credito bueno a un producto que el cliente no ha comprado: "+SDTProductos[position].Nombre
                        );
                        Unidades = 0.0;
                    }
                    //Toast.makeText(context, "Se está realizando un credito bueno a un producto que el cliente no ha comprado", Toast.LENGTH_LONG).show();


                }
            }

                txtUnidades.setText(String.valueOf(Unidades));


        }else{
           // Toast.makeText(context, , Toast.LENGTH_LONG).show();
       Notificacion.aviso(
                        context,
                        "Precio de la presentación "+SDTProductos[position].Presentacion+" en 0"
                );
        }

                /*SDTProductos[position].Unidades = Unidades;
                SDTProductos[position].Guardar(1);*/
            }
        });


        btnrestar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double Unidades  =0.0;
                double Existencia = 0.0;
                if(SDTProductos[position].Precio > 0){

                    if("".equals(txt_existencia.getText().toString())){

                }else{
                    Existencia = Double.valueOf(txt_existencia.getText().toString());
                }

                if("".equals(txtUnidades.getText().toString())){

                }else{
                    Unidades = Double.valueOf(txtUnidades.getText().toString());
                }
                if(Unidades == 0){

                }else{
                    Unidades -=1;
                }


                    if(SDTProductos[position].NotaInv.equalsIgnoreCase("S")) {
                        if (SDTProductos[position].Historico.isEmpty()) {
                            if(Unidades>0){
                                Notificacion.aviso(
                                        context,
                                        "Se está realizando un credito bueno a un producto que el cliente no ha comprado: "+SDTProductos[position].Nombre
                                );
                                Unidades = 0.0;
                            }
                            //Toast.makeText(context, "Se está realizando un credito bueno a un producto que el cliente no ha comprado", Toast.LENGTH_LONG).show();


                        }
                    }



                    txtUnidades.setText(String.valueOf(Unidades));



            }else{
               // Toast.makeText(context, "Precio de la presentación "+SDTProductos[position].Presentacion+" en 0", Toast.LENGTH_LONG).show();
                    Notificacion.aviso(
                            context,
                            "Precio de la presentación "+SDTProductos[position].Presentacion+" en 0"
                    );

            }


               /* SDTProductos[position].Unidades = Unidades;
                SDTProductos[position].Guardar(1);*/
            }
        });



        txtUnidades.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try{
                double Unidades  =0.0;
                if(editable.toString().equals("")){

                }else{
                    Unidades = Double.valueOf(editable.toString().trim());
                }

                double Unidadesoinf  =0.0;
                double Existencia = 0.0;

                    Log.e("Existencia: ",txt_existencia.getText().toString());

                if(txt_existencia.getText().toString().equals("")){

                }else{
                    Existencia = Double.valueOf(txt_existencia.getText().toString());
                }

                if(Existencia < 1){
                    Existencia = 0.0;
                }



                if(SDTProductos[position].NotaInv.equalsIgnoreCase("S") ||
                        SDTProductos[position].NotaCar.equalsIgnoreCase("S")){
                    SDTProductos[position].Dct4 = 0.0;
                    SDTProductos[position].Dct4no = 0.0;



                }else{
                    Log.e("position: ",String.valueOf(position));
                    Log.e("Unidades: ",String.valueOf(Unidades));


                    if (Unidades > Existencia && Unidades > 0) {
                        Unidadesoinf = Unidades - Existencia;
                        if (Unidadesoinf < 0) {
                            Unidadesoinf = Unidadesoinf*-1;
                        }else{
                            Unidadesoinf = Unidades;
                        }

                        Unidades = Existencia;
                    }else{
                        if (Unidades < Existencia) {
                            Unidadesoinf = 0.0;

                        }
                    }

                }





                    Log.e("Unidadesoinf: ",String.valueOf(Unidadesoinf));
                    Log.e("Unidades2: ",String.valueOf(Unidades));

                SDTProductos[position].Unidadesinf = Unidadesoinf;
                SDTProductos[position].Unidades = Unidades;
                SDTProductos[position].Guardar(55);

                //totalpedido =  SDTProductos[position].Total;
                ((EditarProductoV2) context).actualizarinfopedido();
                if(SDTProductos[position].Unidades>0 && SDTProductos[position].NotaCar.equalsIgnoreCase("S") ){
                    LinearLayout2.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_credito));
                }
                if(SDTProductos[position].Unidades>0 && SDTProductos[position].NotaInv.equalsIgnoreCase("S") ){
                    LinearLayout2.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_creditomalo));
                }
            }catch (Exception e){
                    new AlertDialog.Builder(context)
                            .setTitle("Error ")
                            .setMessage(e.toString())
                            .setIcon(android.R.drawable.ic_dialog_alert)

                            .setPositiveButton("Sí", (dialog, which) -> {
                               //eliminarPedido();
                            })

                            .setNegativeButton("No", (dialog, which) -> {
                                dialog.dismiss(); // Cierra el diálogo
                            })

                            .show();


            }
            }
        });


        if(SDTProductos[position].NotaInv.equalsIgnoreCase("S") && SDTProductos[position].Unidades>0 ){

            LinearLayout2.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_creditomalo));
        }
        if(SDTProductos[position].Unidades>0 && SDTProductos[position].NotaCar.equalsIgnoreCase("S") ){
            LinearLayout2.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_credito));
        }

        if(SDTProductos[position].Unidadesinf>0){
            LinearLayout2.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_pendiente));
        }



        /* Log.e("FiltroDcto> ",SDTProductos[position].FiltroDcto);
        if(SDTProductos[position].FiltroDcto.equalsIgnoreCase("S")){
            if(SDTProductos[position].TienePromo.equalsIgnoreCase("S")){
                contendorproducto.setVisibility(View.VISIBLE);
            }else{
                contendorproducto.setVisibility(View.GONE);
            }
        }else{
            contendorproducto.setVisibility(View.VISIBLE);
        }
*/


        return itemView;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();

                if (constraint == null) {
                    results.values = SDTProductosOriginal;
                    results.count = SDTProductosOriginal.length;
                    return results;
                }

                String filtro = normalizar(constraint.toString());

                String[] palabras = filtro.split(" ");

                List<SDTProductos> filtrados = new ArrayList<>();

                for (SDTProductos p : SDTProductosOriginal) {

                    String texto = normalizar(
                            p.Codigo + " " +
                                    p.Nombre + " " +
                                    p.Nombrecomercial
                    );

                    boolean coincide = true;

                    for (String palabra : palabras) {
                        if (!texto.contains(palabra)) {
                            coincide = false;
                            break;
                        }
                    }

                    if (coincide || (filtro.equals("****") && p.Unidades > 0.0)) {
                        filtrados.add(p);
                    }
                }

                SDTProductos[] nlist = filtrados.toArray(new SDTProductos[0]);

                results.values = nlist;
                results.count = nlist.length;

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                SDTProductos = (SDTProductos[]) results.values;
                pCapturaPedido.SDTProductos = SDTProductos;
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
    
   /* public Filter getFilter() {

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
    }*/

    public double  totales(String Prefijo,String nitsec,int clisec,Context context){
        double carritoTotal = 0.0;
        try {
            Time time = new Time();
            time.setToNow();

            GestorPedidos gestorpedidos = new GestorPedidos();
            SDTResumenPedidos sdtResumenPedidos = gestorpedidos.TotalesPedido(context, Prefijo, nitsec, clisec,"","","");

            BaseDatos BaseDeDatos;
            BaseDeDatos = new BaseDatos(context, "MantisMovil", null, 5);

            String SelectClienteImpactado = "Select count(*) as total,    sum(\n" +
                    "        precio\n" +
                    "        * (1-(pordesc/100))\n" +
                    "        * (1-(pordesc2/100))\n" +
                    "        * (1-(pordesc3/100))\n" +
                    "        * (1-(pordesc4/100))\n" +
                    "        * (1-(pordesc5/100))\n" +
                    "        * (1-(pordesc6/100))\n" +
                    "        * cant\n" +
                    "    ) as valor  " +
                    " from pedido p where   cant+ifnull(cantinf,0)<>0 and prefijo='" + Prefijo+ "' and p.nitsec='" + nitsec + "' and p.clisec='" + clisec + "' and pdyear=" + time.year + " and pdmonth=" + (time.month + 1) + " and pdday=" + time.monthDay + "" +
                    " ";

            try{
                Cursor xClientes = BaseDeDatos.getWritableDatabase().rawQuery(SelectClienteImpactado, null);
                if (xClientes.getCount() > 0) {
                    xClientes.moveToFirst();
                    do {
                        carritoTotal =xClientes.getDouble(1);
                    } while (xClientes.moveToNext());
                }
            }catch (Exception e){
                Log.e("EROOOR",e.toString());
            }


        }catch (Exception e){
            Integer error=0;
        }
        return  carritoTotal;
    }
}


