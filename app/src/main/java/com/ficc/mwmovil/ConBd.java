package com.ficc.mwmovil;


import android.content.Context;
import android.database.Cursor;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConBd {

    public String IpEmpresa;
    public String CuentaCartera;
    public String UrlEnvio;
    public String UrlValidarBloqueo;
    public String UrlGetEnvio;
    public String UrlEnvioRecibos;
    public String UrlEnvioRecibosformas;
    public String UrlGetEnvioREcibos;
    public String UrlEnvioConsigna;
    public String UrlEnvioVisita;
    public String UrlEnvioCliente;
    public String UrlrevCliente = "";

    public String UrlEnvioReciboscorreo;
    public String UrlHistorial;
    public String UrlExistencia;
    public String ActulizaOnline="N";
    public String sincronizalinea = "N";
    public String MantisFicc = "N";
    public String TipoPedido= "PED";
    public String Mysql = "N";
    public String FiccRem = "N";
    public String BloqueaCupo = "N";
    public String PrecioMov = "N";
    public String FiltrarFecha = "N";

    public void Variables() {
        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String Empresa=vGlobalVariables.getEmpresa();
     try{ //errorrrrrrrrrrrrrrrrrrrrrrrr rcorregir
            Empresa=Empresa.toUpperCase();

        if (Empresa.trim().equalsIgnoreCase("DINGLESA")) {
         //   IpEmpresa="144.202.65.38";
            IpEmpresa="45.32.193.29";
            ActulizaOnline="S";
            CuentaCartera="'13050501'";
         /*  UrlEnvio="http://"+IpEmpresa+":8080/DistribuidorainglesaMoviles/rest/pSetRemiMoviles3";
            UrlGetEnvio="http://"+IpEmpresa+":8080/DistribuidorainglesaMoviles/rest/pGetPedidoEnviado";
            UrlHistorial="http://"+IpEmpresa+":8080/DistribuidorainglesaMoviles/servlet/";
*/
            UrlEnvio="http://"+IpEmpresa+":8082/DistribuidorainglesaParalela/rest/pSetRemiMoviles3";
            UrlGetEnvio="http://"+IpEmpresa+":8082/DistribuidorainglesaParalela/rest/pGetPedidoEnviado";
            UrlHistorial="http://"+IpEmpresa+":8082/DistribuidorainglesaParalela/servlet/";
        }

         if (Empresa.trim().equalsIgnoreCase("TOTALFOOD")) {
             IpEmpresa="107.191.55.45";
             CuentaCartera="'13050501'";
             UrlEnvio="http://"+IpEmpresa+":8090/TotalFoodPruebasProd/rest/pSetPedidoMoviles";
             UrlGetEnvio="http://"+IpEmpresa+":8090/TotalFoodPruebasProd/rest/pGetPedidoEnviado";
             UrlHistorial="http://"+IpEmpresa+":8090/TotalFoodPruebasProd/servlet/";
         }


        if (Empresa.trim().equalsIgnoreCase("INDULAC")) {
            IpEmpresa="43.228.125.3";
            CuentaCartera="'13050501'";
            Mysql = "S";
            MantisFicc = "S";
            UrlEnvio="http://"+IpEmpresa+":8080/MantisIndulac/rest/pSetPedidosIndulac";
            UrlGetEnvio="http://"+IpEmpresa+":8080/MantisIndulac/rest/pGetPedidoEnviado";
            UrlHistorial="http://"+IpEmpresa+":8080/MantisIndulac/servlet/";
        }

         if (Empresa.trim().equalsIgnoreCase("FARMACOMERCIAL")) {
             IpEmpresa="162.251.146.228";
             CuentaCartera="'13050501'";
             Mysql = "N";
             MantisFicc = "S";
             FiccRem = "S" ;
             UrlEnvio="http://"+IpEmpresa+":8081/MediMantisFarma/rest/pSetRemisionMovil";
             UrlGetEnvio="http://"+IpEmpresa+":8081/MediMantisFarma/rest/pGetPedidoEnviado";
             UrlHistorial="http://"+IpEmpresa+":8081/MediMantisFarma/servlet/";
             UrlEnvioVisita="http://"+IpEmpresa+":8081/MediMantisFarma/rest/pSetVisitaMovil";
         }

         if (Empresa.trim().equalsIgnoreCase("DIAGNOSTIMAX")) {
             IpEmpresa="161.18.225.175";
             CuentaCartera="'13050501'";
             Mysql = "N";
             MantisFicc = "S";
             PrecioMov = "S";
             FiccRem = "S" ;
             UrlEnvio="http://"+IpEmpresa+":8080/MantisFiccGx2Diagnostimax/rest/pSetRemisionMovil";
             UrlGetEnvio="http://"+IpEmpresa+":8080/MantisFiccGx2Diagnostimax/rest/pGetPedidoEnviado";
             UrlHistorial="http://"+IpEmpresa+":8080/MantisFiccGx2Diagnostimax/wpconsultarmantis?VenCod=";
             UrlEnvioVisita="http://"+IpEmpresa+":8080/MantisFiccGx2Diagnostimax/rest/pSetVisitaMovil";
         }


         if (Empresa.trim().equalsIgnoreCase("DISTRIJN")) {
             IpEmpresa="155.138.234.67";
             CuentaCartera="'13050501'";
             Mysql = "N";
             MantisFicc = "S";
             PrecioMov = "N";
             FiccRem = "S" ;
             UrlEnvio="http://"+IpEmpresa+":8086/MantisFiccGx2DistribucionesJN/rest/pSetRemisionMovil";
             UrlGetEnvio="http://"+IpEmpresa+":8086/MantisFiccGx2DistribucionesJN/rest/pGetPedidoEnviado";
             UrlHistorial="http://"+IpEmpresa+":8086/MantisFiccGx2DistribucionesJN/wpconsultarmantis?VenCod=";
             UrlEnvioVisita="http://"+IpEmpresa+":8086/MantisFiccGx2DistribucionesJN/rest/pSetVisitaMovil";
         }
         if (Empresa.trim().equalsIgnoreCase("LIMENA")) {
             IpEmpresa="172.214.127.52";
             String UrlEmpresa="dlimenainc.com";
             CuentaCartera="'103-00-00','103-00-01'";
             Mysql = "N";
             TipoPedido = "REM";
             MantisFicc = "S";
             PrecioMov = "N";
             FiccRem = "S" ;


             UrlEnvio="http://dlimenainc.com/DistribuidoraLimenaMovil/rest/pSetRemisionMovil";
             UrlGetEnvio="https://dlimenainc.com/DistribuidoraLimenaMovil/rest/wsConsultaVersion";
             UrlHistorial="http://dlimenainc.com/DistribuidoraLimenaMovil/wpconsultarmantis?VenCod=";
             UrlEnvioVisita="http://dlimenainc.com/DistribuidoraLimenaMovil/rest/pSetVisitaMovil";
             UrlValidarBloqueo="http://dlimenainc.com/DistribuidoraLimenaMovil/rest/wsvalidarBloqueorem";

            /* UrlEnvio="http://dlimenainc.com/DistribuidoraLimenaMovilParalela/rest/pSetRemisionMovil";
             UrlGetEnvio="https://dlimenainc.com/DistribuidoraLimenaMovilParalela/rest/wsConsultaVersion";
             UrlHistorial="http://dlimenainc.com/DistribuidoraLimenaMovilParalela/wpconsultarmantis?VenCod=";
             UrlEnvioVisita="http://dlimenainc.com/DistribuidoraLimenaMovilParalela/rest/pSetVisitaMovil";
             UrlValidarBloqueo="http://dlimenainc.com/DistribuidoraLimenaMovilParalela/rest/wsvalidarBloqueorem";
*/


         }
         if (Empresa.trim().equalsIgnoreCase("LIMENAPARALELA")) {
             IpEmpresa="172.214.127.52";
             String UrlEmpresa="dlimenainc.com";
             CuentaCartera="'103-00-00','103-00-01'";
             Mysql = "N";
             TipoPedido = "REM";
             MantisFicc = "S";
             PrecioMov = "N";
             FiccRem = "S" ;
             /*UrlEnvio="http://"+IpEmpresa+":8090/DistribuidoraLimenaINC/rest/pSetRemisionMovil";
             UrlGetEnvio="http://"+IpEmpresa+":8090/DistribuidoraLimenaINC/rest/pGetPedidoEnviado";
             UrlHistorial="http://"+IpEmpresa+":8090/DistribuidoraLimenaINC/wpconsultarmantis?VenCod=";
             UrlEnvioVisita="http://"+IpEmpresa+":8090/DistribuidoraLimenaINC/rest/pSetVisitaMovil";*/

             UrlEnvio="http://dlimenainc.com/DistribuidoraLimenaMovilParalela/rest/pSetRemisionMovil";
             UrlGetEnvio="https://dlimenainc.com/DistribuidoraLimenaMovilParalela/rest/wsConsultaVersion";
             UrlHistorial="http://dlimenainc.com/DistribuidoraLimenaMovilParalela/wpconsultarmantis?VenCod=";
             UrlEnvioVisita="http://dlimenainc.com/DistribuidoraLimenaMovilParalela/rest/pSetVisitaMovil";
             UrlValidarBloqueo="http://dlimenainc.com/DistribuidoraLimenaMovilParalela/rest/wsvalidarBloqueorem";
         }



         if (Empresa.trim().equalsIgnoreCase("DIAGNOSTIMAXALT")) {
             IpEmpresa="161.18.225.175";
             CuentaCartera="'13050501'";
             PrecioMov = "S";
             Mysql = "N";
             MantisFicc = "S";
             FiccRem = "S" ;
             UrlEnvio="http://"+IpEmpresa+":8081/DiagnostimaxParalela/rest/pSetRemisionMovil";
             UrlGetEnvio="http://"+IpEmpresa+":8081/DiagnostimaxParalela/rest/pGetPedidoEnviado";
             UrlHistorial="http://"+IpEmpresa+":8081/DiagnostimaxParalela/servlet/";
             UrlEnvioVisita="http://"+IpEmpresa+":8081/DiagnostimaxParalela/rest/pSetVisitaMovil";
         }




         if (Empresa.trim().equalsIgnoreCase("DEMO")) {
             IpEmpresa="43.228.125.3";
             CuentaCartera="'13050501'";
             Mysql = "S";
             MantisFicc = "S";
             UrlEnvio="http://"+IpEmpresa+":8080/Demo/rest/pSetPedidos";
             UrlGetEnvio="http://"+IpEmpresa+":8080/Demo/rest/pGetPedidoEnviado";
             UrlHistorial="http://"+IpEmpresa+":8080/Demo/servlet/";
         }

         if (Empresa.trim().equalsIgnoreCase("BIOMETRIKA")) {
             IpEmpresa="93.189.95.109";
             CuentaCartera="'13050501'";
             Mysql = "N";
             MantisFicc = "S";
             UrlEnvio="http://"+IpEmpresa+":8080/MantisFiccBiometrika/rest/pSetPedidos";
             UrlGetEnvio="http://"+IpEmpresa+":8080/MantisFiccBiometrika/rest/pGetPedidoEnviado";
             UrlHistorial="http://"+IpEmpresa+":8080/MantisFiccBiometrika/servlet/";
         }


        if (Empresa.trim().equalsIgnoreCase("TESORO")) {
            IpEmpresa="103.230.15.47";
            CuentaCartera="'130505'";
            UrlEnvio="http://"+IpEmpresa+":8080/MantisWeb20apps/rest/pSetPedidoMoviles";
            UrlGetEnvio="http://"+IpEmpresa+":8080/MantisWeb20apps/rest/pGetPedidoEnviado";
            UrlHistorial="http://"+IpEmpresa+":8080/MantisWeb20apps/servlet/";
        }
            if (Empresa.trim().equalsIgnoreCase("SUHOGAR")) {
            IpEmpresa="103.30.17.56";
            CuentaCartera="'13050501'";
            MantisFicc = "S";
            BloqueaCupo = "S";
            UrlEnvio="http://"+IpEmpresa+":8080/MantisFiccSuHogar/rest/pSetPedidosV2";
            UrlGetEnvio="http://"+IpEmpresa+":8080/MantisFiccSuHogar/rest/pGetPedidoEnviado";
            UrlEnvioCliente = "http://"+IpEmpresa+":8080/MantisFiccSuHogar/rest/pSetClientemovil";
            UrlHistorial="http://"+IpEmpresa+":8080/MantisFiccSuHogar/servlet/";
            UrlEnvioVisita="http://"+IpEmpresa+":8080/MantisFiccSuHogar/rest/pSetVisitaMovil";
        }

         if (Empresa.trim().equalsIgnoreCase("SUHOGARPRU")) {
             IpEmpresa="103.30.17.56";
             CuentaCartera="'13050501'";
             MantisFicc = "S";
             BloqueaCupo = "S";
             UrlEnvio="http://"+IpEmpresa+":8080/SuHogarPru/rest/pSetPedidosV2";
             UrlGetEnvio="http://"+IpEmpresa+":8080/SuHogarPru/rest/pGetPedidoEnviado";
             UrlEnvioCliente = "http://"+IpEmpresa+":8080/SuHogarPru/rest/pSetClientemovil";
             UrlHistorial="http://"+IpEmpresa+":8080/SuHogarPru/servlet/";
             UrlEnvioVisita="http://"+IpEmpresa+":8080/SuHogarPru/rest/pSetVisitaMovil";
         }


         if (Empresa.trim().equalsIgnoreCase("CASALINS")) {
            // IpEmpresa="200.234.234.245";
             IpEmpresa="93.189.95.109";
             CuentaCartera="'13050501'";
             MantisFicc = "S";
             BloqueaCupo = "S";
             UrlEnvio="http://"+IpEmpresa+":8080/MantisFiccCasalins/rest/pSetPedidos";
             UrlGetEnvio="http://"+IpEmpresa+":8080/MantisFiccCasalins/rest/pGetPedidoEnviado";
             UrlEnvioCliente = "http://"+IpEmpresa+":8080/MantisFiccCasalins/rest/pSetClientemovil";
             UrlHistorial="http://"+IpEmpresa+":8080/MantisFiccCasalins/servlet/";
             UrlEnvioVisita="http://"+IpEmpresa+":8080/MantisFiccCasalins/rest/pSetVisitaMovil";
         }


         if (Empresa.trim().equalsIgnoreCase("CASALINSPRU")) {
             //IpEmpresa="200.234.234.245";
             IpEmpresa="93.189.95.109";
             CuentaCartera="'13050501'";
             MantisFicc = "S";
             BloqueaCupo = "S";
             UrlEnvio="http://"+IpEmpresa+":8080/MantisFiccCasalinsPruebas/rest/pSetPedidos";
             UrlGetEnvio="http://"+IpEmpresa+":8080/MantisFiccCasalinsPruebas/rest/pGetPedidoEnviado";
             UrlEnvioCliente = "http://"+IpEmpresa+":8080/MantisFiccCasalinsPruebas/rest/pSetClientemovil";
             UrlHistorial="http://"+IpEmpresa+":8080/MantisFiccCasalinsPruebas/servlet/";
             UrlEnvioVisita="http://"+IpEmpresa+":8080/MantisFiccCasalinsPruebas/rest/pSetVisitaMovil";
         }





        if (Empresa.trim().equalsIgnoreCase("CARSON")) {
            IpEmpresa="181.141.10.30";
            CuentaCartera="'130505'";
            UrlEnvio="http://"+IpEmpresa+"/Carson/rest/pSetPedidoMoviles";
            UrlGetEnvio="http://"+IpEmpresa+"/Carson/rest/pGetPedidoEnviado";
            UrlHistorial="http://"+IpEmpresa+"/Carson/servlet/";
        }

        if (Empresa.trim().equalsIgnoreCase("IBANEZ")) {
            IpEmpresa="181.49.42.34";
            ActulizaOnline="N";
            CuentaCartera="'1305050101'";
            //UrlEnvio="http://"+IpEmpresa+":8080/WebServiceMoviles/rest/pSetPedidoMovilesN6";
            UrlEnvio="http://"+IpEmpresa+":8080/WebServiceMoviles/rest/pSetPedidoMovilesN7";
            UrlGetEnvio="http://"+IpEmpresa+":8080/WebServiceMoviles/rest/pGetPedidoEnviado";
            UrlHistorial="http://"+IpEmpresa+":8080/WebServiceMoviles/servlet/";
            UrlEnvioRecibos="http://"+IpEmpresa+":8080/WebServiceMoviles/rest/pSetReciboMoviles";
            UrlEnvioRecibosformas ="http://"+IpEmpresa+":8080/WebServiceMoviles/rest/pSetReciboFormas";
            UrlEnvioCliente = "http://"+IpEmpresa+":8080/WebServiceMoviles/rest/pSetProspecto";
            UrlrevCliente = "http://"+IpEmpresa+":8080/WebServiceMoviles/rest/pConsultarClienteProspecto";


            sincronizalinea = "S";
        }
        if (Empresa.trim().equalsIgnoreCase("SUPERLIDER")) {
            IpEmpresa="190.3.208.225";
            CuentaCartera="'1305050101'";
            UrlEnvio="http://"+IpEmpresa+":8080/MantisWebSuperLiderWS/rest/pSetPedidoMoviles";
            UrlGetEnvio="http://"+IpEmpresa+":8080/MantisWebSuperLiderWS/rest/pGetPedidoEnviado";
            UrlHistorial="http://"+IpEmpresa+":8080/MantisWebSuperLiderWS/servlet/";
            sincronizalinea = "S";
        }
        if (Empresa.trim().equalsIgnoreCase("SUPERLIDERPRU")) {
            IpEmpresa="190.3.208.225";
            CuentaCartera="'1305050101'";
            UrlEnvio="http://"+IpEmpresa+":8080/SuperLiderPruebas/rest/pSetPedidoMoviles";
            UrlGetEnvio="http://"+IpEmpresa+":8080/SuperLiderPruebas/rest/pGetPedidoEnviado";
            UrlHistorial="http://"+IpEmpresa+":8080/SuperLiderPruebas/servlet/";
            sincronizalinea = "S";
        }



        if (Empresa.trim().equalsIgnoreCase("BOSCONIA")) {
            IpEmpresa="190.3.208.225";
            CuentaCartera="'130505'";
            UrlEnvio="http://"+IpEmpresa+":4020/MantisWebapps20/rest/pSetPedidoMoviles";
            UrlGetEnvio="http://"+IpEmpresa+":4020/MantisWebapps20/rest/pGetPedidoEnviado";
            UrlHistorial="http://"+IpEmpresa+":4020/MantisWebapps20/servlet/";
        }


        if (Empresa.trim().equalsIgnoreCase("SURTI")) {
            IpEmpresa="190.3.208.225";
            CuentaCartera="'130505'";
            UrlEnvio="http://"+IpEmpresa+":4020/SurtiMov/rest/pSetPedidoMoviles";
            UrlGetEnvio="http://"+IpEmpresa+":4020/SurtiMov/rest/pGetPedidoEnviado";
            UrlHistorial="http://"+IpEmpresa+":4020/SurtiMov/servlet/";
        }

        if (Empresa.trim().equalsIgnoreCase("BACATA")) {
            IpEmpresa="186.30.53.130";
            CuentaCartera="'13050501'";
            UrlEnvio="http://"+IpEmpresa+":8080/MantisWebapps/rest/pSetPedidoMoviles";
            UrlGetEnvio="http://"+IpEmpresa+":8080/MantisWebapps/rest/pGetPedidoEnviado";
        }
        if (Empresa.trim().equalsIgnoreCase("DISCOLMEDICA")) {
            IpEmpresa="144.202.53.243";
            CuentaCartera="'13050501','13050502','13050503'";
            UrlEnvio="http://"+IpEmpresa+":8080/MantisWebapps/rest/pSetRemiDYD";
            UrlGetEnvio="http://"+IpEmpresa+":8080/MantisWebapps/rest/pGetRemisionEnviado";
        }
        if (Empresa.trim().equalsIgnoreCase("SURTIMARCAS")) {
            IpEmpresa="190.144.161.250";
            CuentaCartera="'1305050101'";
           /*UrlEnvio="http://"+IpEmpresa+":8080/Moviles/rest/pSetPedidoMoviles";
            UrlGetEnvio="http://"+IpEmpresa+":8080/Moviles/rest/pGetPedidoEnviado";
            UrlHistorial="http://"+IpEmpresa+":8080/Moviles/servlet/";
*/
            UrlEnvio="http://"+IpEmpresa+":8080/Moviles/rest/pSetPedidoMovilesN7";
            UrlGetEnvio="http://"+IpEmpresa+":8080/Moviles/rest/pGetPedidoEnviado";
            UrlHistorial="http://"+IpEmpresa+":8080/Moviles/servlet";
            UrlEnvioCliente = "http://"+IpEmpresa+":8080/Moviles/rest/pSetProspecto";
            UrlrevCliente = "http://"+IpEmpresa+":8080/Moviles/rest/pConsultarClienteProspecto";

        }

         if (Empresa.trim().equalsIgnoreCase("SURTIMARCASAnt")) {
             IpEmpresa="190.144.161.250";
             CuentaCartera="'1305050101'";
           /*UrlEnvio="http://"+IpEmpresa+":8080/Moviles/rest/pSetPedidoMoviles";
            UrlGetEnvio="http://"+IpEmpresa+":8080/Moviles/rest/pGetPedidoEnviado";
            UrlHistorial="http://"+IpEmpresa+":8080/Moviles/servlet/";
*/
             UrlEnvio="http://"+IpEmpresa+":8082/Moviles/rest/pSetPedidoMovilesN7";
             UrlGetEnvio="http://"+IpEmpresa+":8082/Moviles/rest/pGetPedidoEnviado";
             UrlHistorial="http://"+IpEmpresa+":8082/Moviles/servlet";
             UrlEnvioCliente = "http://"+IpEmpresa+":8082/Moviles/rest/pSetProspecto";

         }


        if (Empresa.trim().equalsIgnoreCase("IBANEZPRU")) {
            IpEmpresa="181.49.42.34";
            CuentaCartera="'1305050101'";
            sincronizalinea = "S";
           // UrlEnvio="http://"+IpEmpresa+":8080/WebServiceMovilesPru/rest/pSetPedidoMovilesN4";
           UrlEnvio="http://"+IpEmpresa+":8080/WebServiceMovilesPru/rest/pSetPedidoMovilesN7";
            UrlGetEnvio="http://"+IpEmpresa+":8080/WebServiceMovilesPru/rest/pGetPedidoEnviado";
            UrlEnvioRecibos="http://"+IpEmpresa+":8080/WebServiceMovilesPru/rest/pSetReciboMoviles";//nuevo
            UrlEnvioRecibosformas ="http://"+IpEmpresa+":8080/WebServiceMovilesPru/rest/pSetReciboFormas"; //nuevo
            UrlEnvioReciboscorreo ="http://"+IpEmpresa+":8080/WebServiceMovilesPru/rest/wsEnviarCorreoRec"; //nuevo
            UrlEnvioConsigna = "http://"+IpEmpresa+":8080/WebServiceMovilesPru/rest/pSetConsignaEfe";
            UrlEnvioCliente = "http://"+IpEmpresa+":8080/WebServiceMovilesPru/rest/pSetProspecto";

            UrlHistorial="http://"+IpEmpresa+":8080/WebServiceMovilesPru/servlet/";
        }


        if (Empresa.trim().equalsIgnoreCase("TATENDEMOS")) {
            IpEmpresa="190.144.19.206";
            CuentaCartera="'1305050101'";
            UrlEnvio="http://"+IpEmpresa+":8080/Tatendemos/rest/pSetPedidoMoviles";
            UrlGetEnvio="http://"+IpEmpresa+":8080/Tatendemos/rest/pGetPedidoEnviado";
            UrlHistorial="http://"+IpEmpresa+":8080/Tatendemos/servlet/";
        }
        if (Empresa.trim().equalsIgnoreCase("IMPACTAMOS")) {
            IpEmpresa="107.191.55.45";
            CuentaCartera="'1305050101'";
            UrlEnvio="http://"+IpEmpresa+":8080/Impactamos/rest/pSetPedidoMoviles";
            UrlGetEnvio="http://"+IpEmpresa+":8080/Impactamos/rest/pGetPedidoEnviado";
            UrlHistorial="http://"+IpEmpresa+":8080/Impactamos/servlet/";
        }

        if (Empresa.trim().equalsIgnoreCase("SOMIC")) {
            IpEmpresa="207.148.6.86";
            CuentaCartera="'1305051001','1305051005','1305051010','1305051015','1305051020','1305051025','1305051030','1305051035','1305051505','1305051510','1305051515','1305051520','1305051525','1305051530','1305051535','1305051540'";
            UrlEnvio="http://"+IpEmpresa+":8081/SomicSoluciones/rest/pSetPedidoMoviles";
            UrlGetEnvio="http://"+IpEmpresa+":8081/SomicSoluciones/rest/pGetPedidoEnviado";
            UrlHistorial="http://"+IpEmpresa+":8081/SomicSoluciones/servlet/";
        }

        if (Empresa.trim().equalsIgnoreCase("GELVEZ")) {
            ActulizaOnline="N";
            IpEmpresa="149.28.254.208";
            CuentaCartera="'13050501'";
            UrlEnvio="http://"+IpEmpresa+":8080/MantisWeb20apps2/rest/pSetPedidoMovilesGel";
            UrlGetEnvio="http://"+IpEmpresa+":8080/MantisWeb20apps2/rest/pGetPedidoEnviado";
            UrlEnvioRecibos="http://"+IpEmpresa+":8080/MantisWeb20apps2/rest/pSetReciboMoviles";
            UrlGetEnvioREcibos="http://"+IpEmpresa+":8080/MantisWeb20apps2/rest/pGetReciboEnviado";
            UrlHistorial="http://"+IpEmpresa+":8080/MantisWeb20apps2/servlet/";

        }
        if (Empresa.trim().equalsIgnoreCase("GELVEZSUCARA")) {
            ActulizaOnline="N";
            IpEmpresa="190.252.225.158";
            CuentaCartera="'13050501'";
            UrlEnvio="http://"+IpEmpresa+":8080/MantisWep20apps/rest/pSetPedidoMovilesGel";
            UrlGetEnvio="http://"+IpEmpresa+":8080/MantisWep20apps/rest/pGetPedidoEnviado";
            UrlEnvioRecibos="http://"+IpEmpresa+":8080/MantisWep20apps/rest/pSetReciboMoviles";
            UrlGetEnvioREcibos="http://"+IpEmpresa+":8080/MantisWep20apps/rest/pGetReciboEnviado";
            UrlHistorial="http://"+IpEmpresa+":8080/MantisWep20apps/servlet/";

        }
        if (Empresa.trim().equalsIgnoreCase("GELVEZARA")) {
            ActulizaOnline="N";
            IpEmpresa="201.49.129.30";
            CuentaCartera="'13050501'";

            UrlEnvio="http://"+IpEmpresa+":8081/MantisWep20apps/rest/pSetPedidoMovilesGel";
            UrlGetEnvio="http://"+IpEmpresa+":8081/MantisWep20apps/rest/pGetPedidoEnviado";
            UrlEnvioRecibos="http://"+IpEmpresa+":8081/MantisWep20apps/rest/pSetReciboMoviles";
            UrlGetEnvioREcibos="http://"+IpEmpresa+":8081/MantisWep20apps/rest/pGetReciboEnviado";
            UrlHistorial="http://"+IpEmpresa+":8081/MantisWep20apps/servlet/";


           /* UrlEnvio="http://"+IpEmpresa+":8081/GelvezParalela/rest/pSetPedidoMovilesGel";
            UrlGetEnvio="http://"+IpEmpresa+":8081/GelvezParalela/rest/pGetPedidoEnviado";
            UrlEnvioRecibos="http://"+IpEmpresa+":8081/GelvezParalela/rest/pSetReciboMoviles";
            UrlGetEnvioREcibos="http://"+IpEmpresa+":8081/GelvezParalela/rest/pGetReciboEnviado";
            UrlHistorial="http://"+IpEmpresa+":8081/GelvezParalela/servlet/";*/

        }
        if (Empresa.trim().equalsIgnoreCase("GELVEZGIR")) {
            ActulizaOnline="N";
            IpEmpresa="149.28.254.208";
            CuentaCartera="'13050501'";
            UrlEnvio="http://"+IpEmpresa+":8080/MantisWeb20apps2/rest/pSetPedidoMovilesGel";
            UrlGetEnvio="http://"+IpEmpresa+":8080/MantisWeb20apps2/rest/pGetPedidoEnviado";
            UrlEnvioRecibos="http://"+IpEmpresa+":8080/MantisWeb20apps2/rest/pSetReciboMoviles";
            UrlGetEnvioREcibos="http://"+IpEmpresa+":8080/MantisWeb20apps2/rest/pGetReciboEnviado";
            UrlHistorial="http://"+IpEmpresa+":8080/MantisWeb20apps2/servlet/";

        }
        if (Empresa.trim().equalsIgnoreCase("GELVEZGIRREM" )|| Empresa.trim().equalsIgnoreCase("GELVEZREM") || Empresa.trim().equalsIgnoreCase("GELVEZCALREM")) {
            ActulizaOnline="N";
            IpEmpresa="149.28.254.208";
            CuentaCartera="'13050501'";
            UrlEnvio="http://"+IpEmpresa+":8080/MantisWeb20apps2/rest/pSetRemiDYD";
            UrlGetEnvio="http://"+IpEmpresa+":8080/MantisWeb20apps2/rest/pGetRemisionEnviado";
            UrlEnvioRecibos="http://"+IpEmpresa+":8080/MantisWeb20apps2/rest/pSetReciboMoviles";
            UrlGetEnvioREcibos="http://"+IpEmpresa+":8080/MantisWeb20apps2/rest/pGetReciboEnviado";
            UrlHistorial="http://"+IpEmpresa+":8080/MantisWeb20apps2/servlet/";

        }
        if (Empresa.trim().equalsIgnoreCase("GELVEZCAL")) {
            ActulizaOnline="N";
            IpEmpresa="149.28.254.208";
            CuentaCartera="'13050501'";
            UrlEnvio="http://"+IpEmpresa+":8080/MantisWeb20apps2/rest/pSetPedidoMovilesGel";
            UrlGetEnvio="http://"+IpEmpresa+":8080/MantisWeb20apps2/rest/pGetPedidoEnviado";
            UrlEnvioRecibos="http://"+IpEmpresa+":8080/MantisWeb20apps2/rest/pSetReciboMoviles";
            UrlGetEnvioREcibos="http://"+IpEmpresa+":8080/MantisWeb20apps2/rest/pGetReciboEnviado";
            UrlHistorial="http://"+IpEmpresa+":8080/MantisWeb20apps2/servlet/";

        }
        if (Empresa.trim().equalsIgnoreCase("GELVEZEJE")) {
            ActulizaOnline="N";
            IpEmpresa="149.28.254.208";
            CuentaCartera="'13050501'";
            UrlEnvio="http://"+IpEmpresa+":8080/MantisWeb20apps2/rest/pSetPedidoMovilesGel";
            UrlGetEnvio="http://"+IpEmpresa+":8080/MantisWeb20apps2/rest/pGetPedidoEnviado";
            UrlEnvioRecibos="http://"+IpEmpresa+":8080/MantisWeb20apps2/rest/pSetReciboMoviles";
            UrlGetEnvioREcibos="http://"+IpEmpresa+":8080/MantisWeb20apps2/rest/pGetReciboEnviado";
            UrlHistorial="http://"+IpEmpresa+":8080/MantisWeb20apps2/servlet/";

        }
        if (Empresa.trim().equalsIgnoreCase("BEHNER")) {
            //ActulizaOnline="S";
            IpEmpresa="190.252.104.140";
            CuentaCartera="'130505'";
            UrlEnvio="http://"+IpEmpresa+":8080/MantisWepapps/rest/pSetPedidoMoviles";
            UrlGetEnvio="http://"+IpEmpresa+":8080/MantisWepapps/rest/pGetPedidoEnviado";
            //UrlEnvio="http://"+IpEmpresa+":8080/MantisWeb20apps2/rest/pSetRemiDYD";
            UrlHistorial="http://"+IpEmpresa+":8080/MantisWepapps/servlet/";
        }
        if (Empresa.trim().equalsIgnoreCase("PROMEFAR")) {
            //ActulizaOnline="S";
            ActulizaOnline="S";
            IpEmpresa="207.148.0.33";
            CuentaCartera="'130505'";
            UrlExistencia="http://"+IpEmpresa+":8081/Promefar/rest/pGetExistenciaPrecioWs";
            UrlEnvio="http://"+IpEmpresa+":8081/Promefar/rest/pSetPedidoMoviles";
            //UrlGetEnvio="http://"+IpEmpresa+":8080/Promefar/rest/pGetPedidoEnviado";
            //UrlEnvio="http://"+IpEmpresa+":8080/MantisWeb20apps2/rest/pSetRemiDYD";
            UrlEnvio="http://"+IpEmpresa+":8081/Promefar/rest/pSetRemiDYD";
            UrlGetEnvio="http://"+IpEmpresa+":8081/Promefar/rest/pGetRemisionEnviado";
            UrlHistorial="http://"+IpEmpresa+":8081/Promefar/servlet/";
        }
        if (Empresa.trim().equalsIgnoreCase("DYD")) {
            ActulizaOnline="S";
            IpEmpresa="181.51.253.237";
            CuentaCartera="'13050501'";
            UrlEnvio="http://"+IpEmpresa+":8080/MantisWeb20apps/rest/pSetRemiDYD";
            UrlGetEnvio="http://"+IpEmpresa+":8080/MantisWeb20apps/rest/pGetRemisionEnviado";
            UrlHistorial="http://"+IpEmpresa+":8081/MantisWebTrd/servlet/";
        }
        if (Empresa.trim().equalsIgnoreCase("MENTAHAIR")) {
            ActulizaOnline="S";
            FiltrarFecha = "S";
            IpEmpresa="137.220.49.87";
            CuentaCartera="'130505'";
          UrlEnvio="http://"+IpEmpresa+":8082/Mentahair/rest/pSetRemiDYD";
            UrlGetEnvio="http://"+IpEmpresa+":8082/Mentahair/rest/pGetRemisionEnviado";
            UrlHistorial="http://"+IpEmpresa+":8082/Mentahair/servlet/";
            UrlExistencia="http://"+IpEmpresa+":8082/Mentahair/rest/pGetExistenciaPrecioWs";

          /*  UrlEnvio="http://"+IpEmpresa+":8089/MentahairPruebasProd/rest/pSetRemiDYD";
            UrlGetEnvio="http://"+IpEmpresa+":8089/MentahairPruebasProd/rest/pGetRemisionEnviado";
            UrlHistorial="http://"+IpEmpresa+":8089/MentahairPruebasProd/servlet/";
            UrlExistencia="http://"+IpEmpresa+":8089/MentahairPruebasProd/rest/pGetExistenciaPrecioWs";

*/

        }
        if (Empresa.trim().equalsIgnoreCase("MENTAHAIRCOT")) {
            ActulizaOnline="S";
            FiltrarFecha = "S";
            IpEmpresa="137.220.49.87";
            CuentaCartera="'130505'";
            UrlEnvio="http://"+IpEmpresa+":8082/Mentahair/rest/pSetPedidoMoviles";
            UrlGetEnvio="http://"+IpEmpresa+":8082/Mentahair/rest/pGetRemisionEnviado";
            UrlHistorial="http://"+IpEmpresa+":8082/Mentahair/servlet/";
            UrlExistencia="http://"+IpEmpresa+":8082/Mentahair/rest/pGetExistenciaPrecioWs";


        }


        if (Empresa.trim().equalsIgnoreCase("MEDIVALLE")) {
            ActulizaOnline="S";
            IpEmpresa="181.204.165.251";
            CuentaCartera="'13050501'";
            http://181.204.165.250:8080/Medivalle/servlet/com.version8.loginempresa
            UrlEnvio="http://"+IpEmpresa+":8080/Medivalle/rest/pSetRemiDYD";
            UrlGetEnvio="http://"+IpEmpresa+":8080/Medivalle/rest/pGetRemisionEnviado";
            UrlHistorial="http://"+IpEmpresa+":8080/Medivalle/servlet/";
        }
        if (Empresa.trim().equalsIgnoreCase("TODORAPIDAS")) {
            ActulizaOnline="N";
            IpEmpresa="179.33.13.149";
            CuentaCartera="'1305054'";
            UrlEnvio="http://"+IpEmpresa+":8080/MantisWebTrd/rest/pSetPedidoMoviles";
            UrlGetEnvio="http://"+IpEmpresa+":8080/MantisWebTrd/rest/pGetRemisionEnviado";
            UrlHistorial="http://"+IpEmpresa+":8080/MantisWebTrd/servlet/";
        }

         if (Empresa.trim().equalsIgnoreCase("SUMMEDSAN")) {
             ActulizaOnline="S";
             IpEmpresa="104.156.254.144";
             CuentaCartera="'13050501'";
             UrlEnvio="http://"+IpEmpresa+":8080/Summedsan/rest/pSetRemiMoviles3";
             UrlGetEnvio="http://"+IpEmpresa+":8080/Summedsan/rest/pGetRemisionEnviado";
             UrlHistorial="http://"+IpEmpresa+":8080/Summedsan/servlet/";
         }



        if (Empresa.trim().equalsIgnoreCase("PIEDEMONTE")) {
            ActulizaOnline="N";
            IpEmpresa="107.191.55.45";
            CuentaCartera="'13050501'";
            UrlEnvio="http://"+IpEmpresa+":8080/MantisWepapps/rest/pSetPedidoMoviles";
            UrlGetEnvio="http://"+IpEmpresa+":8080/MantisWepapps/rest/pGetPedidoEnviado";
            UrlHistorial="http://"+IpEmpresa+":8080/MantisWepapps/servlet/";
        }
        if (Empresa.trim().equalsIgnoreCase("BRILLO")) {
            ActulizaOnline="N";
            IpEmpresa="181.236.251.41";
            CuentaCartera="'13050501'";
            UrlEnvio="http://"+IpEmpresa+":8080/MantisWeb20apps/rest/pSetPedidoMoviles";
            UrlGetEnvio="http://"+IpEmpresa+":8080/MantisWeb20apps/rest/pGetPedidoEnviado";
            UrlHistorial="http://"+IpEmpresa+":8080/MantisWeb20apps/servlet/";
        }

        if (Empresa.trim().equalsIgnoreCase("REDEMOTOS")) {
            ActulizaOnline="N";
            IpEmpresa="181.206.114.45";
            CuentaCartera="'13050501'";
            UrlEnvio="http://"+IpEmpresa+":8080/MantisWeb20apps/rest/pSetPedidoMoviles";
            UrlGetEnvio="http://"+IpEmpresa+":8080/MantisWeb20apps/rest/pGetPedidoEnviado";
            UrlHistorial="http://"+IpEmpresa+":8080/MantisWeb20apps/servlet/";
        }
        if (Empresa.trim().equalsIgnoreCase("SNACKS")) {
            ActulizaOnline="N";
            IpEmpresa="190.84.151.14";
            CuentaCartera="'130505'";
            UrlEnvio="http://"+IpEmpresa+":8080/SnacksDistribuciones/rest/pSetPedidoMoviles";
            UrlGetEnvio="http://"+IpEmpresa+":8080/SnacksDistribuciones/rest/pGetPedidoEnviado";
            UrlHistorial="http://"+IpEmpresa+":8080/SnacksDistribuciones/servlet/";
        }
        if (Empresa.trim().equalsIgnoreCase("SNACKSPRU")) {
            ActulizaOnline="N";
            IpEmpresa="190.84.151.14";
            CuentaCartera="'130505'";
            UrlEnvio="http://"+IpEmpresa+":8081/SnacksParalela/rest/pSetPedidoMoviles";
            UrlGetEnvio="http://"+IpEmpresa+":8081/SnacksParalela/rest/pGetPedidoEnviado";
            UrlHistorial="http://"+IpEmpresa+":8081/SnacksParalela/servlet/";
        }

        if (Empresa.trim().equalsIgnoreCase("FARMA")) {
            ActulizaOnline="S";
            IpEmpresa="213.255.227.137";
            CuentaCartera="'130505'";
            UrlEnvio="http://"+IpEmpresa+":8082/FarmaComercialDiscol/rest/pSetPedidoMoviles";
            UrlGetEnvio="http://"+IpEmpresa+":8082/FarmaComercialDiscol/rest/pGetPedidoEnviado";
            UrlHistorial="http://"+IpEmpresa+":8082/FarmaComercialDiscol/servlet/";
        }
        if (Empresa.trim().equalsIgnoreCase("ACOAVANZAR")) {
            ActulizaOnline="N";
            IpEmpresa="43.228.125.57";
            CuentaCartera="'1305050101'";
            UrlEnvio="http://"+IpEmpresa+":8080/AcoAvanzar/rest/pSetPedidoMoviles";
            UrlGetEnvio="http://"+IpEmpresa+":8080/AcoAvanzar/rest/pGetPedidoEnviado";
            UrlHistorial="http://"+IpEmpresa+":8080/AcoAvanzar/servlet/";
        }
         if (Empresa.trim().equalsIgnoreCase("ACOAVANZARPRU")) {
             ActulizaOnline="N";
             IpEmpresa="43.228.125.57";
             CuentaCartera="'1305050101'";
             UrlEnvio="http://"+IpEmpresa+":8082/AcoAvanzarParalela/rest/pSetPedidoMoviles";
             UrlGetEnvio="http://"+IpEmpresa+":8082/AcoAvanzarParalela/rest/pGetPedidoEnviado";
             UrlHistorial="http://"+IpEmpresa+":8082/AcoAvanzarParalela/servlet/";
         }
        if (Empresa.trim().equalsIgnoreCase("PROVEEMOS")) {
            ActulizaOnline="S";
            IpEmpresa="137.220.49.87";
            CuentaCartera="'130505'";
            UrlEnvio="http://"+IpEmpresa+":8082/Proveemos/rest/pSetRemiDYD";
            UrlGetEnvio="http://"+IpEmpresa+":8082/Proveemos/rest/pGetRemisionEnviado";
            UrlHistorial="http://"+IpEmpresa+":8080/Proveemos/servlet/";
        }
     }catch (Exception e){

     }
    }

    public Connection CargarConexion(Context context){
        Variables();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Connection conn=null;

        try {

            GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
            String Empresa=vGlobalVariables.getEmpresa();
            //Empresa=Empresa.toUpperCase();
            BaseDatos vBaseDeDatos;

          /*  String Script = "select empcod from empresaglobal";
            vBaseDeDatos = new BaseDatos(context, "MantisMovil", null, 6);
            Cursor vCursorempresa = vBaseDeDatos.getReadableDatabase().rawQuery(Script, null);
            vCursorempresa.moveToFirst();
            GlobalVariables vGlobalVariables2= GlobalVariables.getInstance();
            if (vCursorempresa.getCount() == 1) {
                Empresa = vCursorempresa.getString(0);
                vGlobalVariables2.setEmpresa(Empresa,context);
                Log.e("empresagloba2",Empresa);
            }*/
            Empresa = Empresa.toUpperCase();


            if (Empresa.trim().equalsIgnoreCase("INDULAC")) {
                try {
                String url = "jdbc:mysql://" + IpEmpresa + ":3306/INDULAC?characterEncoding=latin1";
                String user = "root";
                String passwd = "Mantis321";
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(url, user, passwd);


                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                    Log.e("Errorconb",e.toString());
                }
            }


            if (Empresa.trim().equalsIgnoreCase("DEMO")) {
                try {
                    String url = "jdbc:mysql://" + IpEmpresa + ":3306/DemoSomic?characterEncoding=latin1";
                    String user = "root";
                    String passwd = "Mantis321";
                    Class.forName("com.mysql.jdbc.Driver");
                    conn = DriverManager.getConnection(url, user, passwd);


                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                    Log.e("Errorconb",e.toString());
                }
            }


            if (Empresa.trim().equalsIgnoreCase("TOTALFOOD")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/TotalFood;"
                        + "user=sa;password=Mantis321");
            }
            if (Empresa.trim().equalsIgnoreCase("FARMACOMERCIAL")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/FarmaFiccgx2;"
                        + "user=sa;password=Mantis321");
            }
            if (Empresa.trim().equalsIgnoreCase("DIAGNOSTIMAX")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/Diagnostimax;"
                        + "user=sa;password=Mantis321");
            }

            if (Empresa.trim().equalsIgnoreCase("DISTRIJN")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/DistribucionesJN;"
                        + "user=sa;password=Mantis321");
            }


            if (Empresa.trim().equalsIgnoreCase("LIMENA")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
               conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/DistribuidoraLimena;"
                        + "user=sa;password=Mantis321");

               /*  conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/LimenaTest;"
                        + "user=sa;password=Mantis321");*/


            }
            if (Empresa.trim().equalsIgnoreCase("LIMENAPARALELA")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/DistribuidoraLimenaQA;"
                        + "user=sa;password=Mantis321");

               /*  conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/LimenaTest;"
                        + "user=sa;password=Mantis321");*/


            }



            if (Empresa.trim().equalsIgnoreCase("DIAGNOSTIMAXALT")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/DisgnostimaxParalela;"
                        + "user=sa;password=Mantis321");
            }



                if (Empresa.trim().equalsIgnoreCase("TESORO")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/MantisWebapps;"
                        + "user=sa;password=Mantis321;instance=tesoro");
            }

            if (Empresa.trim().equalsIgnoreCase("DINGLESA")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/DistribuidoraInglesaParalela;"
                        + "user=sa;password=Mantis321");
            }

            if (Empresa.trim().equalsIgnoreCase("SUMMEDSAN")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/Summedsan;"
                        + "user=sa;password=Mantis321;instance=sql2024");
            }





            if (Empresa.trim().equalsIgnoreCase("TESORO")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/MantisWebapps;"
                        + "user=sa;password=Mantis321;instance=tesoro");
            }

            if (Empresa.trim().equalsIgnoreCase("SUHOGAR")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/SUHOGAR;"
                        + "user=sa;password=Mantis321;");
            }
            if (Empresa.trim().equalsIgnoreCase("SUHOGARPRU")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/SuHogar110625;"
                        + "user=sa;password=Mantis321;");
            }
            if (Empresa.trim().equalsIgnoreCase("CASALINS")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/CASALINS;"
                        + "user=sa;password=Mantis321;");
            }
            if (Empresa.trim().equalsIgnoreCase("CASALINSPRU")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/CASALINSPRUEBAS;"
                        + "user=sa;password=Mantis321;");
            }

            if (Empresa.trim().equalsIgnoreCase("BIOMETRIKA")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/BIOMETRIKA;"
                        + "user=sa;password=Mantis321;");
            }



            if (Empresa.trim().equalsIgnoreCase("IBANEZ")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/ibanezweb;"
                        + "user=sa;password=Mantis321;");
            }




            if (Empresa.trim().equalsIgnoreCase("SUPERLIDER")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/SuperLider;"
                        + "user=sa;password=Mantis321;");
            }
            if (Empresa.trim().equalsIgnoreCase("SUPERLIDERPRU")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/SuperLiderPruebas;"
                        + "user=sa;password=Mantis321;");
            }
            if (Empresa.trim().equalsIgnoreCase("CARSON")) {

                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/Carson;"
                        + "user=sa;password=Mantis321;");
            }

            if (Empresa.trim().equalsIgnoreCase("SURTIMARCAS")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/mantiswebev3;"
                        + "user=sa;password=Mantis321;instance=sql2017");

                /*conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/Pruebasselect * from vended;"
                        + "user=sa;password=Mantis321;instance=sql2017");*/

            }

            if (Empresa.trim().equalsIgnoreCase("SURTIMARCASAnt")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/Pruebas;"
                        + "user=sa;password=Mantis321;instance=sql2017");

                /*conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/Pruebasselect * from vended;"
                        + "user=sa;password=Mantis321;instance=sql2017");*/

            }

            if (Empresa.trim().equalsIgnoreCase("IBANEZPRU")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/pruebas;"
                        + "user=sa;password=Mantis321;instance=sql2017");


            }
            if (Empresa.trim().equalsIgnoreCase("DISCOLMEDICA")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/Discolmedica;"
                        + "user=sa;password=Mantis321;");

            }
            if (Empresa.trim().equalsIgnoreCase("BOSCONIA")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/BosconiaTemp;"
                        + "user=sa;password=Mantis321;");

            }
            if (Empresa.trim().equalsIgnoreCase("SUPERLIDER")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/SuperLider;"
                        + "user=sa;password=Mantis321;");
            }
            if (Empresa.trim().equalsIgnoreCase("SURTI")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/SURTI;"
                        + "user=sa;password=Mantis321;");

            }
            if (Empresa.trim().equalsIgnoreCase("BACATA")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/MantisWebEv3;"
                        + "user=sa;password=Mantis321;");

            }


            if (Empresa.trim().equalsIgnoreCase("TATENDEMOS")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/tatendemos;"
                        + "user=sa;password=Mantis321;");
            }


            if (Empresa.trim().equalsIgnoreCase("IMPACTAMOS")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/IMPACTAMOS;"
                        + "user=sa;password=Mantis321;");
            }
            if (Empresa.trim().equalsIgnoreCase("SOMIC")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/SomicSoluciones;"
                        + "user=sa;password=Mantis321;");
            }
            if (Empresa.trim().equalsIgnoreCase("GELVEZSUCARA")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/MantisWebEv3;"
                        + "user=userbpmantis;password=Mantis321;");
            }

            if (Empresa.trim().equalsIgnoreCase("GELVEZ") ||Empresa.trim().equalsIgnoreCase("GELVEZARA") || Empresa.trim().equalsIgnoreCase("GELVEZGIR") || Empresa.trim().equalsIgnoreCase("GELVEZGIRREM")  || Empresa.trim().equalsIgnoreCase("GELVEZREM")  || Empresa.trim().equalsIgnoreCase("GELVEZCALREM")  ||Empresa.trim().equalsIgnoreCase("GELVEZCAL")   ||Empresa.trim().equalsIgnoreCase("GELVEZEJE")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/MantisWebEv3;"
                        + "user=userbpmantis;password=Mantis321;");
            }

            if (Empresa.trim().equalsIgnoreCase("BEHNER")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":2523/mantiswebapps;"
                        + "user=userbpmantis;password=Mantis321;");
            }

            if (Empresa.trim().equalsIgnoreCase("PROMEFAR")) {
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/Promefar;"
                        + "user=sa;password=Mantis321;instance=sql2014");
            }

            if (Empresa.trim().equalsIgnoreCase("IBANEZPR")) {
                IpEmpresa="181.49.42.34";
                CuentaCartera="'1305050101'";
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/ibanezweb;"
                        + "user=sa;password=Mantis321;");
            }

            if (Empresa.trim().equalsIgnoreCase("DYD")) {
                IpEmpresa="181.51.253.237";
                CuentaCartera="'13050501'";
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":8090/MantisWebEv3;"
                        + "user=sa;password=Mantis321;");
            }
            if (Empresa.trim().equalsIgnoreCase("MENTAHAIR")) {
                IpEmpresa="137.220.49.87";
                CuentaCartera="'130505'";
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/Mentahairnew;"
                        + "user=sa;password=Mantis321;");
                /*conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/mentahairparalela;"
                        + "user=sa;password=Mantis321;");*/

            }
            if (Empresa.trim().equalsIgnoreCase("MENTAHAIRCOT")) {
                IpEmpresa="137.220.49.87";
                CuentaCartera="'130505'";
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/Mentahairnew;"
                        + "user=sa;password=Mantis321;");
            }
            if (Empresa.trim().equalsIgnoreCase("MEDIVALLE")) {
                IpEmpresa="181.204.165.251";
                CuentaCartera="'13050501'";
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/Medivalle;"
                        + "user=USERBPMANTIS;password=Mantis321;");
            }
            if (Empresa.trim().equalsIgnoreCase("TODORAPIDAS")) {
                IpEmpresa="179.33.13.149";
                CuentaCartera="'130505'";
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/trdWeb;"
                        + "user=sa;password=Mantis321");
            }
            if (Empresa.trim().equalsIgnoreCase("PIEDEMONTE")) {
                IpEmpresa="107.191.55.45";
                CuentaCartera="'130505'";
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/mantiswepapps;"
                        + "user=sa;password=Mantis321;");
            }
            if (Empresa.trim().equalsIgnoreCase("BRILLO")) {
                IpEmpresa="181.236.251.41";
                CuentaCartera="'13050501'";
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/ERPWEB1108;"
                        + "user=sa;password=Mantis321;instance=sql2014new");
            }
            if (Empresa.trim().equalsIgnoreCase("REDEMOTOS")) {
                IpEmpresa="181.206.114.45";
                CuentaCartera="'13050501'";
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/MantisWebEv3;"
                        + "user=sa;password=Mantis321;");
            }
            if (Empresa.trim().equalsIgnoreCase("FARMA")) {

                IpEmpresa="213.255.227.137";
                CuentaCartera="'130505'";
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/FarmaComercial;"
                       + "user=userbpmantis;password=Mantis321;");
            }
            if (Empresa.trim().equalsIgnoreCase("ACOAVANZAR")) {

                IpEmpresa="43.228.125.57";
                CuentaCartera="'1305050101'";
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/AcoAvanzar;"
                      + "user=userbpmantis;password=Mantis321;instance=sql2014");
            }
            if (Empresa.trim().equalsIgnoreCase("ACOAVANZARPRU")) {

                IpEmpresa="43.228.125.57";
                CuentaCartera="'1305050101'";
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/AcoAvanzarParalela;"
                        + "user=userbpmantis;password=Mantis321;instance=sql2014");
            }


            if (Empresa.trim().equalsIgnoreCase("PROVEEMOS")) {

                IpEmpresa="137.220.49.87";
                CuentaCartera="'130505'";
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/Proveemos;"
                        + "user=userbpmantis;password=Mantis321");
            }


            if (Empresa.trim().equalsIgnoreCase("SNACKS")) {
                IpEmpresa="190.84.151.14";
                CuentaCartera="'130505'";
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/Snacks;"
                       + "user=userbpmantis;password=Mantis321;");
             //   IpEmpresa="181.49.42.34";
             //   CuentaCartera="'1305050101'";
             //   Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
             //   conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/ibanezweb;"
             //           + "user=sa;password=Mantis321;");

            }
            if (Empresa.trim().equalsIgnoreCase("SNACKSPRU")) {
                IpEmpresa="190.84.151.14";
                CuentaCartera="'130505'";
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+IpEmpresa+":1433/SnacksParalela;"
                        + "user=userbpmantis;password=Mantis321;");
            }



        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return conn;
    }
    public String getIpEmpresa() {
        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String Empresa=vGlobalVariables.getEmpresa();
        Empresa=Empresa.toUpperCase();
        byte[] ipv4 = new byte[] {0, 0, 0, 0};



        if (Empresa.trim().equalsIgnoreCase("DINGLESA")) {
          //  IpEmpresa="144.202.65.38";
            IpEmpresa="45.32.193.29";
        }
        if (Empresa.trim().equalsIgnoreCase("SUMMEDSAN")) {

            IpEmpresa="104.156.254.144";

        }



        if (Empresa.trim().equalsIgnoreCase("TESORO")) {
            IpEmpresa="103.230.15.47";
        }
        if (Empresa.trim().equalsIgnoreCase("INDULAC")) {
            IpEmpresa="43.228.125.3";
        }
        if (Empresa.trim().equalsIgnoreCase("DEMO")) {
            IpEmpresa="43.228.125.3";
        }
        if (Empresa.trim().equalsIgnoreCase("IBANEZ")) {
            IpEmpresa="181.49.42.34";
        }

        if (Empresa.trim().equalsIgnoreCase("SUPERLIDERPRU")) {
            IpEmpresa="190.3.208.225";
        }
        if (Empresa.trim().equalsIgnoreCase("SURTIMARCAS")) {
            IpEmpresa="190.144.161.250";
        }
        if (Empresa.trim().equalsIgnoreCase("SURTIMARCASPRUEBAS")) {
            IpEmpresa="190.144.161.250";
        }

        if (Empresa.trim().equalsIgnoreCase("IBANEZPRU")) {
            IpEmpresa="181.49.42.34";
        }
        if (Empresa.trim().equalsIgnoreCase("CARSON")) {
            IpEmpresa="181.141.10.30";
        }
        if (Empresa.trim().equalsIgnoreCase("DISCOLMEDICA")) {
            IpEmpresa="144.202.53.243";
        }
        if (Empresa.trim().equalsIgnoreCase("BOSCONIA")) {
            IpEmpresa="190.3.208.225";
        }
        if (Empresa.trim().equalsIgnoreCase("SUPERLIDER")) {
            IpEmpresa="190.3.208.225";
        }
        if (Empresa.trim().equalsIgnoreCase("SURTI")) {
            IpEmpresa="190.3.208.225";
        }
        if (Empresa.trim().equalsIgnoreCase("BACATA")) {
            IpEmpresa="186.30.53.130";
        }

        if (Empresa.trim().equalsIgnoreCase("TATENDEMOS")) {
            IpEmpresa="190.144.19.206";
        }
        if (Empresa.trim().equalsIgnoreCase("IMPACTAMOS")) {
            IpEmpresa="107.191.55.45";
        }
        if (Empresa.trim().equalsIgnoreCase("SOMIC")) {
            IpEmpresa="207.148.6.86";
        }
        if (Empresa.trim().equalsIgnoreCase("GELVEZ") ||Empresa.trim().equalsIgnoreCase("GELVEZARA") || Empresa.trim().equalsIgnoreCase("GELVEZGIR") ||Empresa.trim().equalsIgnoreCase("GELVEZGIRREM") || Empresa.trim().equalsIgnoreCase("GELVEZREM") || Empresa.trim().equalsIgnoreCase("GELVEZCALREM") || Empresa.trim().equalsIgnoreCase("GELVEZCAL")|| Empresa.trim().equalsIgnoreCase("GELVEZEJE") ) {
            IpEmpresa="201.49.129.30";
        }
        if (Empresa.trim().equalsIgnoreCase("GELVEZSUCARA")) {
            IpEmpresa="190.252.225.158";
        }
        if (Empresa.trim().equalsIgnoreCase("BEHNER")) {
            IpEmpresa="190.252.104.140";
        }
        if (Empresa.trim().equalsIgnoreCase("PROMEFAR")) {
            IpEmpresa="207.148.0.33";
        }
        if (Empresa.trim().equalsIgnoreCase("DYD")) {
            IpEmpresa="181.51.253.237";
        }
        if (Empresa.trim().equalsIgnoreCase("MENTAHAIR")) {
            IpEmpresa="137.220.49.87";
        }
        if (Empresa.trim().equalsIgnoreCase("TOTALFOOD")) {
            IpEmpresa="107.191.55.45";
        }
        if (Empresa.trim().equalsIgnoreCase("FARMACOMERCIAL")) {
            IpEmpresa="162.251.146.228";
        }
        if (Empresa.trim().equalsIgnoreCase("DIAGNOSTIMAX")) {
            IpEmpresa="161.18.225.174";
        }
        if (Empresa.trim().equalsIgnoreCase("DIAGNOSTIMAXALT")) {
            IpEmpresa="161.18.225.174";
        }

        if (Empresa.trim().equalsIgnoreCase("DISTRIJN")) {
            IpEmpresa="155.138.234.67";
        }
        if (Empresa.trim().equalsIgnoreCase("LIMENA")) {
            IpEmpresa="172.214.127.52";
        }
        if (Empresa.trim().equalsIgnoreCase("LIMENAPARALELA")) {
            IpEmpresa="172.214.127.52";
        }
        if (Empresa.trim().equalsIgnoreCase("GX1")) {
            IpEmpresa="192.168.2.99";
        }
        if (Empresa.trim().equalsIgnoreCase("MENTAHAIRCOT")) {
            IpEmpresa="137.220.49.87";
        }
        if (Empresa.trim().equalsIgnoreCase("MEDIVALLE")) {
            IpEmpresa="181.204.165.251";
        }
        if (Empresa.trim().equalsIgnoreCase("TODORAPIDAS")) {
            IpEmpresa="" +
                    "" +
                    "" +
                    "" +
                    "";
        }
        if (Empresa.trim().equalsIgnoreCase("PIEDEMONTE")) {
            IpEmpresa="107.191.55.45";
        }
        if (Empresa.trim().equalsIgnoreCase("BIOMETRIKA")) {
            IpEmpresa="93.189.95.109";
        }
        if (Empresa.trim().equalsIgnoreCase("BRILLO")) {
            IpEmpresa="181.236.251.41";
        }
        if (Empresa.trim().equalsIgnoreCase("REDEMOTOS")) {
            IpEmpresa="181.206.114.45";
        }
        if (Empresa.trim().equalsIgnoreCase("SNACKS")) {
            IpEmpresa="190.84.151.14";
        }
        if (Empresa.trim().equalsIgnoreCase("SNACKSPRU")) {
            IpEmpresa="190.84.151.14";
        }
        if (Empresa.trim().equalsIgnoreCase("FARMA")) {
            IpEmpresa="213.255.227.137";
        }
        if (Empresa.trim().equalsIgnoreCase("ACOAVANZAR")) {
            IpEmpresa="43.228.125.57";
        }
        if (Empresa.trim().equalsIgnoreCase("ACOAVANZARPRU")) {
            IpEmpresa="43.228.125.57";
        }
        if (Empresa.trim().equalsIgnoreCase("PROVEEMOS")) {

            IpEmpresa="137.220.49.87";
        }
        return IpEmpresa;
    }
    public String getCuentaCartera() {
        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String Empresa=vGlobalVariables.getEmpresa();
        Empresa=Empresa.toUpperCase();
        byte[] ipv4 = new byte[] {0, 0, 0, 0};
        if (Empresa.trim().equalsIgnoreCase("DINGLESA")) {
            CuentaCartera="'13050501','28050501'";
        }

        if (Empresa.trim().toUpperCase().contains("LIMENA")) {
            CuentaCartera="'103-00-00','103-00-01'";
        }



        if (Empresa.trim().equalsIgnoreCase("SUMMEDSAN")) {
            CuentaCartera="'13050501'";
        }
        if (Empresa.trim().equalsIgnoreCase("TESORO")) {
            CuentaCartera="'130505'";
        }
        if (Empresa.trim().equalsIgnoreCase("INDULAC")) {
            CuentaCartera="'13050501'";
        }
        if (Empresa.trim().equalsIgnoreCase("DEMO")) {
            CuentaCartera="'13050501'";
        }
        if (Empresa.trim().equalsIgnoreCase("BIOMETRIKA")) {
            CuentaCartera="'13050501'";
        }
        if (Empresa.trim().equalsIgnoreCase("43.228.125.3")) {
            CuentaCartera="'130505'";
        }
        if (Empresa.trim().equalsIgnoreCase("IBANEZ")) {
            CuentaCartera="'1305050101'";
        }
        if (Empresa.trim().equalsIgnoreCase("SURTIMARCAS")) {
            CuentaCartera="'1305050101'";
        }
        if (Empresa.trim().equalsIgnoreCase("SURTIMARCASPRUEBAS")) {
            CuentaCartera="'1305050101'";
        }
        if (Empresa.trim().equalsIgnoreCase("IBANEZPRU")) {
            CuentaCartera="'1305050101'";
        }
        if (Empresa.trim().equalsIgnoreCase("DISCOLMEDICA")) {
            CuentaCartera="'13050501','13050502','13050503'";
        }
        if (Empresa.trim().equalsIgnoreCase("BOSCONIA")) {
            CuentaCartera="'130505'";
        }
        if (Empresa.trim().equalsIgnoreCase("SUPERLIDER")) {
            CuentaCartera="'130505'";
        }
        if (Empresa.trim().equalsIgnoreCase("SURTI")) {
            CuentaCartera="'130505'";
        }
        if (Empresa.trim().equalsIgnoreCase("BACATA")) {
            CuentaCartera="'13050501'";
        }
        if (Empresa.trim().equalsIgnoreCase("DYD")) {
            CuentaCartera="'13050501'";
        }
        if (Empresa.trim().equalsIgnoreCase("MEDIVALLE")) {
            CuentaCartera="'13050501'";
        }
        if (Empresa.trim().equalsIgnoreCase("MENTAHAIR")) {
            CuentaCartera="'130505'";
        }
        if (Empresa.trim().equalsIgnoreCase("TOTALFOOD")) {
            CuentaCartera="'13050501'";
        }
        if (Empresa.trim().equalsIgnoreCase("FARMACOMERCIAL")) {
            CuentaCartera="'13050501'";
        }


        if (Empresa.trim().equalsIgnoreCase("GX1")) {
            CuentaCartera="'13050501'";
        }
        if (Empresa.trim().equalsIgnoreCase("MENTAHAIRCOT")) {
            CuentaCartera="'130505'";
        }
        if (Empresa.trim().equalsIgnoreCase("IMPACTAMOS")) {
            CuentaCartera="'1305050101'";
        }
        if (Empresa.trim().equalsIgnoreCase("SOMIC")) {
            CuentaCartera="'1305051001','1305051005','1305051010','1305051015','1305051020','1305051025','1305051030','1305051035','1305051505','1305051510','1305051515','1305051520','1305051525','1305051530','1305051535','1305051540'";
        }

        if (Empresa.trim().equalsIgnoreCase("GELVEZ") || Empresa.trim().equalsIgnoreCase("GELVEZARA") || Empresa.trim().equalsIgnoreCase("GELVEZGIR") || Empresa.trim().equalsIgnoreCase("GELVEZGIRREM") || Empresa.trim().equalsIgnoreCase("GELVEZREM") || Empresa.trim().equalsIgnoreCase("GELVEZCALREM")  || Empresa.trim().equalsIgnoreCase("GELVEZCAL")||Empresa.trim().equalsIgnoreCase("GELVEZEJE")) {
            CuentaCartera="'13050501'";
        }
        if (Empresa.trim().equalsIgnoreCase("GELVEZSUCARA")) {
            CuentaCartera="'13050501'";
        }
        if (Empresa.trim().equalsIgnoreCase("BEHNER")) {
            CuentaCartera="'130505'";
        }
        if (Empresa.trim().equalsIgnoreCase("PROMEFAR")) {
            CuentaCartera="'130505'";
        }
        if (Empresa.trim().equalsIgnoreCase("TODORAPIDAS")) {
            CuentaCartera="'1305054'";
        }
        if (Empresa.trim().equalsIgnoreCase("PIEDEMONTE")) {
            CuentaCartera="'130505'";
        }
        if (Empresa.trim().equalsIgnoreCase("BRILLO")) {
            CuentaCartera="'13050501'";
        }
        if (Empresa.trim().equalsIgnoreCase("REDEMOTOS")) {
            CuentaCartera="'13050501'";
        }
        if (Empresa.trim().equalsIgnoreCase("REDEMOTOS")) {
            CuentaCartera="'13050501'";
        }
        if (Empresa.trim().equalsIgnoreCase("FARMA")) {
            CuentaCartera="'130505'";
        }
        if (Empresa.trim().equalsIgnoreCase("CARSON")) {
            CuentaCartera="'130505'";
        }
        if (Empresa.trim().equalsIgnoreCase("ACOAVANZAR")) {
            CuentaCartera="'1305050101'";
        }
        if (Empresa.trim().equalsIgnoreCase("ACOAVANZARPRU")) {
            CuentaCartera="'1305050101'";
        }
        if (Empresa.trim().equalsIgnoreCase("PROVEEMOS")) {
            CuentaCartera="'130505'";
        }
        return CuentaCartera;
    }

}
