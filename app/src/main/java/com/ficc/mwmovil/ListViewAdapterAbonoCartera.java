package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListViewAdapterAbonoCartera extends BaseAdapter  {

    Context context;
    SDTAbono[] SDTAbono;

    public ListViewAdapterAbonoCartera(Context context, SDTAbono[] SDTAbono) {
        this.context = context;
        this.SDTAbono = SDTAbono;
    }

    @Override
    public int getCount() {
        return SDTAbono.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    @SuppressLint("MissingInflatedId")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.listarabonos,parent, false);

        TextView Facnro = (TextView) itemView.findViewById(R.id.Facnro);
        TextView Subtotal = (TextView) itemView.findViewById(R.id.Subtotal);
        TextView Saldo = (TextView) itemView.findViewById(R.id.Saldo);
        TextView total = (TextView) itemView.findViewById(R.id.total);
        TextView retencion = (TextView) itemView.findViewById(R.id.retencion);
        TextView Reteica = (TextView) itemView.findViewById(R.id.Reteica);
        TextView ReteIva = (TextView) itemView.findViewById(R.id.ReteIva);
        TextView dctoFin = (TextView) itemView.findViewById(R.id.dctoFin);
        TextView netoapagar = (TextView) itemView.findViewById(R.id.netoapagar);
        TextView abono = (TextView) itemView.findViewById(R.id.abono);
        TextView tipopago = itemView.findViewById(R.id.tipopago);
        TextView DctoProv = itemView.findViewById(R.id.DctoProv);
        TextView DctoNto = itemView.findViewById(R.id.DctoNto);
        TextView facturadev = itemView.findViewById(R.id.facturadev);
        TextView condev = itemView.findViewById(R.id.condev);

        TextView tipofac = itemView.findViewById(R.id.tipofac);
        TextView dctoconf = itemView.findViewById(R.id.dctoconf);
        TextView valDctoProv = itemView.findViewById(R.id.valDctoProv);
        TextView valDctoNto = itemView.findViewById(R.id.valDctoNto);
        TextView valdctoconf = itemView.findViewById(R.id.valdctoconf);
        TextView aprovecha = itemView.findViewById(R.id.aprovecha);
        TextView vendedor = itemView.findViewById(R.id.vendedor);
        View seleccion = itemView.findViewById(R.id.seleccion);
        LinearLayout contenedor_factura = itemView.findViewById(R.id.contenedor_factura);




        try {

            if(SDTAbono[position].pagototal.equalsIgnoreCase("S")){
                tipopago.setText("Vlr Pagado");
            }
            if(SDTAbono[position].pagototal.equalsIgnoreCase("N")){
                tipopago.setText("Abono");
            }
            if(SDTAbono[position].Abono == 0){
                tipopago.setText("");
            }

            Facnro.setText(SDTAbono[position].FacNro);

           if(SDTAbono[position].Seleccionado.equalsIgnoreCase("S")){
               seleccion.setBackground(ContextCompat.getDrawable(context, R.drawable.circulosel));
           }

            Subtotal.setText(String.format("%,d",SDTAbono[position].Subtotal.intValue()));
            Saldo.setText(String.format("%,d",SDTAbono[position].Saldo.intValue()));
            total.setText(String.format("%,d",SDTAbono[position].Total.intValue()));
            retencion.setText(String.format("%,d",SDTAbono[position].Retencion.intValue()));
            Reteica.setText(String.format("%,d",SDTAbono[position].ReteIca.intValue()));
            ReteIva.setText(String.format("%,d",SDTAbono[position].ReteIva.intValue()));
            abono.setText(String.format("%,d",SDTAbono[position].Abono.intValue()));
            dctoFin.setText(String.format("%,d",SDTAbono[position].Dcto.intValue()));
            vendedor.setText(SDTAbono[position].Vencod);
            BaseDatos BaseDeDatos;
            BaseDeDatos = new BaseDatos(context, "MantisMovil", null, 6);
            Cursor user =  BaseDeDatos.getWritableDatabase().rawQuery("select  VenId from Usuarios where vencod = '"+SDTAbono[position].Vencod+"' ", null); //  where idgrupid = '996'  and (select count(*) from articulos a where a.idsubgrupsec=g.idsubgrupsec )>0 //Ciudades='XX,' Oi
            if(user.getCount() > 0){
                user.moveToFirst();
                vendedor.setText(user.getString(0));
            }


            DctoProv.setText(String.valueOf(SDTAbono[position].dctoprov));
            DctoNto.setText(String.valueOf(SDTAbono[position].dctonooto));
            dctoconf.setText(String.valueOf(SDTAbono[position].dctoconf));
            facturadev.setText(SDTAbono[position].FacnroDev);
            valDctoProv.setText(String.format("%,d",SDTAbono[position].valdctoprov.intValue()));
            valDctoNto.setText(String.format("%,d",SDTAbono[position].valdctonooto.intValue()));
            valdctoconf.setText(String.format("%,d",SDTAbono[position].valdctoconf.intValue()));
            condev.setText(SDTAbono[position].condev);
            aprovecha.setText(String.format("%,d",SDTAbono[position].aprove.intValue()));
            contenedor_factura.setVisibility(View.GONE);

            if(SDTAbono[position].nota.equalsIgnoreCase("N")){
                tipofac.setText("Nota");
                contenedor_factura.setVisibility(View.VISIBLE);
            }else{
                contenedor_factura.setVisibility(View.GONE);
                tipofac.setText("Factura");
            }




          //  pago.setText("0");
            netoapagar.setText(String.format("%,d",SDTAbono[position].NetoPago.intValue()));
            //txt_valfactura.setText(SDTCartera[position].FacSaldo);
        }catch (Exception e)
        {
            Log.e("Errolist",e.toString());
        }
        return itemView;
    }


}
