package com.ficc.mwmovil;

import java.io.IOException;
import java.net.InetAddress;

public class ComprobarConexionIP {
    public ComprobarConexionIP() {
    }

    public boolean PingIP() {
        boolean network=false;
        try {
            ConBd conbd = new ConBd();
            //Connection conn = conbd.CargarConexion();
            String ipEmpresa= conbd.getIpEmpresa();
            //;
            if (InetAddress.getByAddress(InetAddress.getByName(ipEmpresa).getAddress()).isReachable(1000)==true)
            {
                //Boolean variable named network
                network=true; //Ping works
            }
            else
            {
                network=false; //Ping doesnt work
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return network;
    }


}
