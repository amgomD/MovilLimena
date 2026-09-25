package com.ficc.mwmovil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BaseDatos extends SQLiteOpenHelper {
    //NombreServer NomSer= new NombreServer();

    String TablaListasPrecios;
    String Temporal;
    String TablaVersiones;
    String ListaPorGrupoSubgrupo;
    String TablaPedidosDesc;
    String BonificacionesProductoDet;
    String BonificacionesProductoDetBon;
    String TablaBonificados;
    String TablaNewEscala;
    String TablaDctoGen;

    String ControlVentas;
    String TablaDescIba;
    String TablaBitacora;
    String TablaDescuentoMenta;
    String movparvalrango;
    String MovParLinea;
    String MovParArt;
    String CanalOferta;
    String PerfildeClientes;
    String TipodeClientes;
    String Zona;
    String CategoriaCliente;

    String VentasCliente;
    String CirreDia;
    String TablaClientesDcto;
    String Prospecto;
    String PreciosEsp;
    String DescGrupo;

    String ArticulosExi;
    String MovParMixBonificados;
    String MovParMixArticulos;
    String MovParMix;
    String MovParEsc;
    String Sincronizaciones;
    String MovTipDir;
    String Canales;
    String Bodegas;
    String TablaClientesDevoluciones;
    String TablaConceptoNCND;
    String MovParPrem;

    String MovParBonProdBon;
    String MovParBonBonificados;
    String TablaPedidos;
    String Pedidoenc;
    String PedidoInf;

    String TablaGrupos;
    String TablaSubgrupo;
    String TablaFamilia;
    String TablaMovCauPed;
    String TablaCartera;
    String TablaClasePerf;
    String TablaClasePre;

    String TablaBarrios;
    String TablaCiudades;
    String TablaClientes;
    String TablaEmpresaMovil;
    String EmpresaGlobal;
    String TablaUsuarios;
    String TablaArticulos;
    String Visita;

    String Recibo;
    String Reciboforma;
    String ConsignaRecibo;
    String ConsignaRecibofoto;

    String Bancos;
    String BancoCuenta;
    String InvCategoria;
    String obsCliente;

    String DescuentosFac;
    String JustificacionSaldo;
    String Proveedores;
    String BancoProv;

    String ReciboFormaFotos;
    String fototemp;
    String NumeroProvisional;


    public BaseDatos(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, "MantisMovil22", factory, 6);
    }

public void createtablas(){


    TablaBitacora = "CREATE TABLE IF NOT EXISTS  Bitacora("
            + "TxtLargo text(4000))";
    TablaListasPrecios = "CREATE TABLE IF NOT EXISTS  ListasPrecios("
            + "LisPreCod Numeric(12),LisPreNom text(100),LisPrebloqDes text(1))";

    TablaVersiones = "CREATE TABLE IF NOT EXISTS  VersionMovil("
            + "VerMovSec integer,VerMovVersion text(100),VerMovEstado text(1))";

    BonificacionesProductoDet = "CREATE TABLE IF NOT EXISTS  BonificacionesProductoDet("
            +"BonProSec INTEGER,"
            +"BonProArtSec text(40),"
            +"BomProDetDesUni  INTEGER,"
            +"PARBONCANGEN  INTEGER,"
            +"PARBONAPLCANGEN  text(1)," +
            " ArtParBonPreArtCod  INTEGER,"
            +"BonProDetDesCaj  INTEGER,"
            +"BonProDetEmb  INTEGER,"
            +"BonProDetIndOpc text(1))";

    BonificacionesProductoDetBon = "CREATE TABLE IF NOT EXISTS  BonificacionesProductoDetBon("
            +"DesoBonSec INTEGER,"
            +"DesoBonEscSec INTEGER,"
            +"DesoBonEscArtBonif text(40)," +
            " BonParBonPreArtCod INTEGER,"
            +"DesoBonEscBonif  INTEGER)";




    TablaBonificados = "CREATE TABLE IF NOT EXISTS  BonificacionesProducto("
            +"BonProSec INTEGER,"
            +"BonProSecLin INTEGER,"
            +"bontipo text(40),"
            +"BonProGrupo  text(1000),"
            +"SUBGRUPOS text(1000),"
            +"FAMILIAS text(1000),"
            +"ARTICULOS text(1000),"
            +"LABORATORIO text(1000),"
            +"CLASE text(1000),"
            +"SECCION text(1000),"
            +"MARCA text(1000),"
            +"LINEART text(1000),"
            +"CATEGORIA text(1000),"
            +"SUBCATEGORIA text(1000),"
            +"CANALES	text(1000),"
            +"SUBCANALES	text(1000),"
            +"TAMANOS	text(1000),"
            +"BODEGAS text(1000),"
            +"BonMovParTra text(3),"
            +"SUCURSALES text(1000),"
            +"VENDEDORES text(1000),"
            +"UNIDADES text(1000),"
            +"TIPOCLIENTE text(1000),"
            +"PERFIL text(1000),"
            +"CLIENTE text(1000),"
            +"EXTIPOCLIENTE text(1000),"
            +"EXPERFIL text(1000),"
            +"EXCLIENTE text(1000),"
            +"BonProEscArtSec	text(40),"
            +"BonProEscUniDes INTEGER, "
            +"BonProEscUniHas	 INTEGER, "
            +"BonProEscCajDes	 INTEGER, "
            +"BonProEscCajHas	 INTEGER, "
            +"BonProDesVal numeric(16,2), "
            +"BonProHasVal numeric(16,2), "
            +"BonProEscBonArtSec text(40),"
            +"BonProEscBonUni	INTEGER, "
            +"BonProEscBonCaj	INTEGER, "
            +"BomProMaxMixPeri INTEGER, "
            +"BonProMaxCli INTEGER, "
            +"BomProMixRefDis INTEGER,BonProCanOpc INTEGER, PARBONAPLCANGEN Text(1), PARBONCANGEN INTEGER,ParBonDes text(1000))";

    //+ "BonProSec INTEGER,BonProGrupo text(40))";

    TablaNewEscala = "CREATE TABLE IF NOT EXISTS  Descuentos("
            +"DESCSEC INTEGER,"
            + "tipodesc text(1000),"
            + "DESCAGRU text(5),"
            +"descgru text(1000),"
            +"SUBGRUPOS text(1000),"
            +"FAMILIAS text(1000),"
            +"ARTICULOS text(1000),"
            +"CANALES text(1000),"
            +"SUBCANALES text(1000),"
            +"TAMANOS text(1000),"
            +"UNIDADNEG text(1000),"
            +"CIUDADES text(1000),"
            +"CLIENTES text(100000),"
            +"LISTAS text(100000),"
            +"VENDEDORES text(1000),"
            +"SUCURSALES text(1000),"
            +"PERFILES text(1000),"
            +"BODEGAS text(1000),"
            +"LABORATORIO text(1000),"
            +"CLASE text(1000),"
            +"SECCION text(1000),"
            +"MARCA text(1000),"
            +"LINEART text(1000),"
            +"CATEGORIA text(1000),"
            +"SUBCATEGORIA text(1000),"
            +"DescPorMov text(3),"
            +"DescLinDesUni INTEGER,"
            +"DescLinHasUni INTEGER,"
            +"DescLin1CajMen1 text(1),"
            +"DescLinDesCaj INTEGER,"
            +"DescLinHasCaj INTEGER,"
            +"DescLinDesVal numeric(16,2),"
            +"DescLinHasVal numeric(16,2),"
            +"DescLinPorDesLin numeric(16,2),"
            +"DescArtDes text(8),"
            +"DesSecLin INTEGER,"
            +"EXCLIENTES text(1000), DESPROGAPLESCTOT text(1))";


    TablaDctoGen = "CREATE TABLE IF NOT EXISTS  DescuentosDetalle("
            +"DESCSEC INTEGER," +
            " ArtSec text(30))";


//        DescLinDesFec
//        DescLinHasFec
    EmpresaGlobal = "CREATE TABLE IF NOT EXISTS EmpresaGlobal("
            + "EmpCod text(500),"
            + "EmpresaIp text(80),"
            + "ParMovBon text(40),"
            + "ParMovManCanCaj text(40),"
            + "ParMovManDesConf text(40),"
            + "ParMovDescV2 text(40),"
            + "AliNegCod text(40),"
            + "SucCod text(40),"
            + "Version text(40),"
            + "ParMovNoOtorgar text(40)"
            + ")";

    TablaEmpresaMovil = "CREATE TABLE IF NOT EXISTS EmpresaMovil("
            + "EmpMovCod text(500),"
            + "EmpMovIp text(80),"
            + "EmpMovCue text(80),"
            + "EmpMovUrlEnvio text(500),"
            + "EmpMovUrlGetEnvio text(500),"
            + "EmpMovUrlBase text(500),"
            + "EmpImpre text(500)"
            + ")";

    TablaDescIba = "CREATE TABLE IF NOT EXISTS DescIbaEscala("
            + "llave	text(1000),"
            + "DescProDesUni INTEGER,"
            + "DescProHasUni	INTEGER,"
            + "DescPro1CajMen1	text(1),"
            + "DescProDesCaj	INTEGER,"
            + "DescProHasCaj	INTEGER,"
            + "DescProDesVal	numeric(16,2),"
            + "DescProHasVal	numeric(16,2),"
            + "DescProPorDesLin	numeric(16,2),"
            + "TIP	text(500),"
            + "llave2	text(500),"
            + "DescuArt	text(1000),"
            + "DescuCan	text(1000),"
            + "DescuCiu	text(1000),"
            + "DescuCli	text(1000),"
            + "DescuFam	text(1000),"
            + "DescuSca	text(1000),"
            + "DescuSgr	text(1000),"
            + "DescuSuc	text(1000),"
            + "DescuTam	text(1000),"
            + "DescuUni	text(1000),"
            + "DescuVen	text(1000))";

    ListaPorGrupoSubgrupo = "CREATE TABLE IF NOT EXISTS ListaPorGrupoSubgrupo("
            + "NitSec text(20) ,"
            + "CliSec INTEGER(20) ,"
            + "InvGruCod text(20),"
            + "InvSubGruCod text(20),"
            + "LisPreCod INTEGER(20)"
            + ")";


    VentasCliente = "CREATE TABLE IF NOT EXISTS VentasCliente("
            + "NitSec text(20) ,"
            + "CliSec INTEGER(20) ,"
            + "ArtSec text(30) ,"
            + "InvGruCod text(20),"
            + "InvSubGruCod text(20),"
            + "InvFamCod text(20),"
            + "KarValTotMenDes INTEGER"
            + ")";

    ControlVentas = "CREATE TABLE IF NOT EXISTS ControlVentas("
            + "ConVenGruSec INTEGER ,"
            + "ConVenGruGrup text(30) ,"
            + "Canales text(2000) ,"
            + "SubCanales text(2000),"
            + "Tamano text(2000),"
            + "Ciudad text(2000),"
            + "UnidadNegoc text(2000),"
            + "Vendedores text(2000),"
            + "Subgrupo text(2000),"
            + "Familia text(2000),"
            + "Cliente text(2000),"
            + "Articulos text(2000),"
            + "Sucursales text(2000)"
            + ")";

    TablaUsuarios = "CREATE TABLE IF NOT EXISTS Usuarios("
            + "VenCod text(40) ,"
            + "VENUSUMOV text(40) ,"
            + "VenId text(40) ,"
            + "VenNom text(100) ,"
            + "VenCla text(40) NOT NULL,"
            + "alinegcod INTEGER ,"
            + "ParMovPedMin INTEGER ,"
            + "ParMovBon text(1) ,"
            + "ParMovManCanCaj text(1) ,"
            + "ParMovManDesConf text(1) ,"
            + "ParMovNoOtorgar text(1) ,"
            + "ParGeoRef text(1) ,"
            + "ParModoRev text(1) ,"
            + "tat text(1) ,"
            + "EnvAlt text(1) ,"
            + "modDcto text(1) ,"
            + "SucCod INTEGER ,"
            + "ParMovSec INTEGER,VenCnt INTEGER,ParMovDescV2 text(1),ParMovTatTra text(3),ParMovBloqCar text(1),ParMovBloqNot text(1))";

    TablaClientesDcto = "CREATE TABLE IF NOT EXISTS ClientesDcto("
            + "nitsec taxt(25),"
            + "clisec INTEGER,CliDesInvGruCod text(16),CLiDesDcto numeric(16,2),CliDesFin numeric(16,2))";

    TablaClientes = "CREATE TABLE IF NOT EXISTS Clientes("
            + "nitsec taxt(25),"
            + "clisec INTEGER,"
            + "NitCom text(120),"
            + "CliNom text(120),"
            + "NitIde text(40),"
            + "CliDir text(200),"
            + "CliTel text(10)  ,"
            + "Lisprecod INTEGER ,"
            + "LisprecodLim INTEGER ,"
            + "CliConPag INTEGER,"
            + "CliBloCup text(1),"
            +"cliintlun text(5),"
            +"cliintmar text(5),"
            +"cliintmie text(5),"
            +"cliintjue text(5),"
            +"cliintvie text(5),"
            +"cliintsab text(5),"
            +"cliintdom text(5),"
            +"CliCartCom text(1),"
            +"CliIntTiempo INTEGER,"
            +"CliIntOrdDet INTEGER,"
            +"CliIntFre text(10),"
            +"FreNom text(100),"
            +"PerCliCod INTEGER ,"
            +"CanCod INTEGER,"
            +"CanNom text(100),"
            +"TipoCliente text(100),"
            +"perfilcliente text(100),"
            +"zona text(100),"
            +"ruta text(100),"
            +"categoria text(100),"
            +"CliTamCan INTEGER,"
            +"CiuCod text(10),"
            +"CiuNom text(100),"
            +"BarCod INTEGER,"
            +"BarNom text(100),"
            +"CanSubCod INTEGER,"
            +"CanSubNom text(100),"
            +"CliCup numeric(16,2),"
            +"CliPorAdi numeric(16,2),"
            +"CliVenCup numeric(14,7),"
            +"CarteraVend numeric(16,2),"
            +"CliDiasUltVen INTEGER,"
            +"CliIva text(1),"
            +"CliNoree text(1),"
            +"tricliica text(2),"
            +"TriCliRet text(2),"
            +"TriCliIva text(2),"
            +"triCliIcaPucSec text(16),"
            +"TriCliPucSec text(16),"
            +"TriCliivaPucSec text(16),"
            +"retpucVal numeric(16,2),"
            +"icapucVal numeric(16,2),"
            +"ivapucVal numeric(16,2),"
            +"retpucpor numeric(16,2),"
            +"icapucpor numeric(16,2),"
            +"ivapucpor numeric(16,2),"
            +"InaCod INTEGER, clidespagcont numeric(16,2), clidespagcre numeric(16,2), "
            +"PRIMARY KEY (nitsec, clisec))";



    TablaClientesDevoluciones = "CREATE TABLE IF NOT EXISTS ClientesDevoluciones ("
            + "NitSec text(20) ,"
            + "CliSec INTEGER(20) ,"
            + "ArtSec text(30) ,"
            + "KarUni INTEGER ,"
            + "KarPrePub INTEGER,Fecha text(40),dias INTEGER)";



    MovParLinea = "CREATE TABLE IF NOT EXISTS MovParLinea ("
            + "MovParLinSec INTEGER ,"
            + "MovParLinNomDes varchar(200),"
            + "MovParLinResCan varchar(100) ,"
            + "MovParLinViaDir varchar(1),"
            + "MovParLinNoOtor varchar(1),"
            + "MovParLinInvGruCod varchar(20),"
            + "MovParLinInvSubGruCod varchar(20),"
            + "MovParLinInvFamCod varchar(20),"
            + "MovParLinArtSec varchar(20),"
            + "MovParLINfecmod varchar(20),"
            + "MovParLinDes numeric(16,2)) ";




    MovParArt = "CREATE TABLE IF NOT EXISTS MovParArt ("
            + "MovParArtSec INTEGER ,"
            + "MovParResCan varchar(100) ,"
            + "MovParNumDcto varchar(1) ,"
            + "MovParViaDir varchar(1) ,"
            + "MovParNoOtor  varchar(1),"
            + "MovParArtDetArtSec varchar(30),"
            + "CLIENTES varchar(1000),"
            + "CANALES varchar(1000),"
            + "CLIENTESEX varchar(1000),"
            + "MovParArtFecMod varchar(30),"
            + "MovParArtciucod varchar(5),"
            + "MovParArtDetDesc numeric(16,2))";

    TablaDescuentoMenta =  "CREATE TABLE IF NOT EXISTS MovParDesPro ("
            + "MovParDesArtSec INTEGER ,"
            + "DesProCod INTEGER(8) ,"
            + "LISTAS varchar(1000),"
            + "CLIENTES varchar(1000),"
            + "MovParArtDetDesc numeric(16,2))";


    TablaCiudades = "CREATE TABLE IF NOT EXISTS Ciudades ("
            + "CiuCod text(20) ,"
            + "CiuNom text(100))";

    TablaConceptoNCND = "CREATE TABLE IF NOT EXISTS ConceptoNCND ("
            + "ConNotCod text(4) ,"
            + "ConNotNoAfeInv text(100),"
            + "ConNotNom text(100))";

    TablaBarrios = "CREATE TABLE IF NOT EXISTS Barrios ("
            + "BarCod text(20) ,"
            + "BarNom text(100),Ciudades text(1000))";

    TablaClasePerf = "CREATE TABLE IF NOT EXISTS PerfilClientesClase("
            + "PerCliCod INTEGER,"
            + "ClaArtCod INTEGER,"
            + "PerCliDetDes1 numeric(16,2))";


    TablaClasePre = "CREATE TABLE IF NOT EXISTS ArticulosPresentacion("
            + "ArtSec  text(20) ,"
            + "lisprecod INTEGER ,"
            + "PrePrefijval numeric(18,2),"
            + "PreArtCod INTEGER,"
            + "PreArtNom text(2000), PreArtFacConVal  INTEGER )";

    TablaCartera = "CREATE TABLE IF NOT EXISTS cartera("
            + "FacSec INTEGER,"
            + "MovNitsec INTEGER,"
            + "MovClisec INTEGER,"
            + "MovFacSec text(40),"
            + "FacTotalImpuestos numeric(18,2),"
            + "karvaltotmendes numeric(18,2),"
            + "FacAbonos numeric(18,2),"
            + "FacSaldo numeric(18,2),"
            + "FacFec text(40) ,"
            + "FacConPag integer ,"
            + "FacVen text(40) ,"
            + "FacVenCod text(10) ,"
            + "FacPedCon text(20) ,"
            + "FacNroDev text(80) ,"
            + "ConNotNom text(200) ,"
            + "FacMora integer )";

    TablaMovCauPed = "CREATE TABLE IF NOT EXISTS MovCauPed ("
            + "MovCauSec INTEGER ,"
            + "MovCauNom text(100),"
            + "MovConPed text(1),"
            + "MovPidFot text(1),"
            + "MovPidObs text(1))";

    TablaGrupos = "CREATE TABLE IF NOT EXISTS inventariogrupo("
            + "InvGruCod text(20) PRIMARY KEY,"
            + "InvCanCodStr text(100),"
            + "InvGruNom text(200) NOT NULL)";

    TablaSubgrupo = "CREATE TABLE IF NOT EXISTS inventariosubgrupo("
            + "InvSubGruCod text(20) PRIMARY KEY,"
            + "InvGruCod text(20),"
            + "InvSubGruNom text(200) NOT NULL)";

    TablaFamilia = "CREATE TABLE IF NOT EXISTS inventariofamilia("
            + "InvFamCod text(20) PRIMARY KEY,"
            + "InvSubGruCod text(20),"
            + "InvFamNom text(200) NOT NULL)";


    TablaArticulos = "CREATE TABLE IF NOT EXISTS articulos("
            + "ArtSec text(20) ,"
            + "ArtCod text(30),"
            + "ArtNom text(150),"
            + "ArtCodBar text(150),"
            + "ArtMedNomCom text(150),"
            + "InvGruCod text(20),"
            + "GruCheck text(1),"
            + "SubCheck text(1),"
            + "FamCheck text(1),"
            + "InvGruNom text(20),"
            + "InvSubGruCod text(20),"
            + "InvSubGruNom text(200),"
            + "Presentacion text(200),"
            + "InvFamCod text(20),"
            + "InvFamNom text(200),"
            + "InvCatCod text(10),"
            + "LabCod text(10),"
            + "invClaCod text(10),"
            + "invseccod text(10),"
            + "invmarcod text(10),"
            + "invlincod text(10),"
            + "invsubcatcod text(10),"
            + "ArtCantInf text(2),"
            + "ArtSolEnt text(1),"

            + "ArtLim Numeric(16,2),"
            + "ArtRen Numeric(16,2),"
            + "ParConIva INTEGER,"
            + "precio1 numeric(16,2),"
            + "precio2 numeric(16,2),"
            + "precio3 numeric(16,2),"
            + "precio4 numeric(16,2),"
            + "precio5 numeric(16,2),"
            + "precio6 numeric(16,2),"
            + "precio7 numeric(16,2),"
            + "precio8 numeric(16,2),"
            + "precio9 numeric(16,2),"
            + "precio10 numeric(16,2),"
            + "precio11 numeric(16,2),"
            + "precio12 numeric(16,2),"
            + "precio13 numeric(16,2),"
            + "precio14 numeric(16,2),"
            + "precio15 numeric(16,2),"
            + "precio16 numeric(16,2),"
            + "precio17 numeric(16,2),"
            + "precio18 numeric(16,2),"
            + "precio19 numeric(16,2),"
            + "precio20 numeric(16,2),"
            + "precio21 numeric(16,2),"
            + "precio22 numeric(16,2),"
            + "precio23 numeric(16,2),"
            + "precio24 numeric(16,2),"
            + "precio25 numeric(16,2),"
            + "precio26 numeric(16,2),"
            + "precio27 numeric(16,2),"
            + "precio28 numeric(16,2),"
            + "precio29 numeric(16,2),"
            + "precio30 numeric(16,2),"
            + "precio31 numeric(16,2),"
            + "precio32 numeric(16,2),"
            + "precio33 numeric(16,2),"
            + "precio34 numeric(16,2),"
            + "precio35 numeric(16,2),"
            + "precio36 numeric(16,2),"
            + "precio37 numeric(16,2),"
            + "precio38 numeric(16,2),"
            + "precio39 numeric(16,2),"
            + "precio40 numeric(16,2),"
            + "precio1PorRen numeric(16,2),"
            + "precio2PorRen numeric(16,2),"
            + "precio3PorRen numeric(16,2),"
            + "precio4PorRen numeric(16,2),"
            + "precio5PorRen numeric(16,2),"
            + "precio6PorRen numeric(16,2),"
            + "precio7PorRen numeric(16,2),"
            + "precio8PorRen numeric(16,2),"
            + "precio9PorRen numeric(16,2),"
            + "precio10PorRen numeric(16,2),"
            + "precio11PorRen numeric(16,2),"
            + "precio12PorRen numeric(16,2),"
            + "precio13PorRen numeric(16,2),"
            + "precio14PorRen numeric(16,2),"
            + "precio15PorRen numeric(16,2),"
            + "precio16PorRen numeric(16,2),"
            + "precio17PorRen numeric(16,2),"
            + "precio18PorRen numeric(16,2),"
            + "precio19PorRen numeric(16,2),"
            + "precio20PorRen numeric(16,2),"
            + "precio21PorRen numeric(16,2),"
            + "precio22PorRen numeric(16,2),"
            + "precio23PorRen numeric(16,2),"
            + "precio24PorRen numeric(16,2),"
            + "precio25PorRen numeric(16,2),"
            + "precio26PorRen numeric(16,2),"
            + "precio27PorRen numeric(16,2),"
            + "precio28PorRen numeric(16,2),"
            + "precio29PorRen numeric(16,2),"
            + "precio30PorRen numeric(16,2),"
            + "precio31PorRen numeric(16,2),"
            + "precio32PorRen numeric(16,2),"
            + "precio33PorRen numeric(16,2),"
            + "precio34PorRen numeric(16,2),"
            + "precio35PorRen numeric(16,2),"
            + "precio36PorRen numeric(16,2),"
            + "precio37PorRen numeric(16,2),"
            + "precio38PorRen numeric(16,2),"
            + "precio39PorRen numeric(16,2),"
            + "precio40PorRen numeric(16,2),"
            + "desc1 numeric(8,4),"
            + "desc2 numeric(8,4),"
            + "desc3 numeric(8,4),"
            + "desc4 numeric(8,4),"
            + "desc5 numeric(8,4),"
            + "desc6 numeric(8,4),"
            + "ArtEmb INTEGER,"
            + "Exist INTEGER,"
            + "ExistFec INTEGER,"
            + "PrePreFijCosPro numeric(16,2),"
            + "MovParArtFecMod text(30),"
            + "MovParArtNoOtor VARCHAR(1),"
            + "ClaArtCod INTEGER,"
            + "ArtIndMpm VARCHAR(1),"
            + "TieneDescEsp VARCHAR(1),"
            + "ClaArtNom text(100),ArtValImp numeric(16,2), ArtImgBlob  BLOB, PreArtCod Text(102) )";

    Visita = "CREATE TABLE IF NOT EXISTS Visita("
            + "VenCod text(40) ,"
            + "NitSec text(40) ,"
            + "CliSec INTEGER(100) ,"
            + "VisObs text(1000) ,"
            + "VisPref text(5) ,"
            + "VisFot INTEGER,"
            + "VisAno INTEGER,"
            + "VisMes INTEGER,"
            + "VisDia INTEGER,"
            + "VisHor INTEGER,"
            + "VisMin INTEGER,"
            + "VisSeg INTEGER,"
            + "VisHorFin INTEGER,"
            + "VisMinFin INTEGER,"
            + "VisSegFin INTEGER,"
            + "MovCauPed INTEGER,"
            + "visLisPreCod INTEGER,"
            + "MovCauNom text(100),"
            + "VisFotoimg BLOB,"
            + "VisLatitud text(40),"
            + "VisLongitud text(40))";



    PerfildeClientes = "CREATE TABLE IF NOT EXISTS PerfildeClientes("
            + "PercliCod text(4) ,"
            + "PerCliNom text(80))";

    TipodeClientes = "CREATE TABLE IF NOT EXISTS TipodeClientes("
            + "TipCliCod text(3) ,"
            + "TipCliNom text(80))";

    Zona = "CREATE TABLE IF NOT EXISTS Zona("
            + "ZonCod text(3) ,"
            + "ZonNom text(80))";

    CategoriaCliente = "CREATE TABLE IF NOT EXISTS CategoriaCliente("
            + "CatCliCod text(3) ,"
            + "CatCliNom text(80))";

    Recibo = "CREATE TABLE IF NOT EXISTS Recibo("
            + "VenCod text(40) ,"
            + "nitsec text(25),"
            + "clisec INTEGER,"
            + "facnro text(40),"
            + "RecNro text(40),"
            + "tipo text(1),"
            + "Enviado text(1),"
            + "saldo numeric(16,2),"
            + "abono numeric(16,2), "
            + "Observacion text(250), "
            + "Justificacion text(200), "
            + "retefue numeric(16,2), "
            + "retica numeric(16,2), "
            + "retiva numeric(16,2), "
            + "descuento numeric(16,2), "
            + "dctoprov numeric(16,2), "
            + "dctonooto numeric(16,2), "
            + "aprove numeric(16,2), "
            + "dctoconf numeric(16,2), "
            + "dctoprovpor numeric(16,2), "
            + "dctonootopor numeric(16,2), "
            + "dctoconfpor numeric(16,2), "
            + "pagototal text(1), "
            + "rcyear INTEGER,"
            + "rcmonth INTEGER,"
            + "rcday INTEGER)";

    Reciboforma = "CREATE TABLE IF NOT EXISTS Reciboforma("
            + "VenCod text(40) ,"
            + "nitsec text(25),"
            + "clisec INTEGER,"
            + "facnro text(40),"
            + "RecNro text(40),"
            + "TipoConsigna text(100),"
            + "ConNro text(40)," //Num consigna
            + "NroCheque text(40),"
            + "CodBanco INTEGER,"
            + "PucBanco  text(16), "
            + "TCNSEC INTEGER,"
            + "Valor numeric(16,2), "
            + "Ciudad text(40), "
            + "imgBase BLOB, "
            + "CtaBco  text(40), "
            + "Tipo  text(40), "
            + "aldia  text(1), "
            + "postfecha  text(1), "
            + "recicyear INTEGER," //año crea
            + "recicmonth INTEGER," // mes crea
            + "recicday INTEGER," // dia crea
            + "rcyear INTEGER,"
            + "rcmonth INTEGER,"
            + "rcday INTEGER)";

    ConsignaRecibo = "CREATE TABLE IF NOT EXISTS ConsignaRecibo("
            + "prefijo INTEGER ,"
            + "VenCod text(40) ,"
            + "RecNro text(40),"
            + "ConNro text(40),"
            + "Obs text(1600),"
            + "Enviado text(1),"
            + "Recibido text(1),"
            + "CodBanco INTEGER,"
            + "bancoProv INTEGER,"
            + "TCNSEC INTEGER,"
            + "Ciudad text(40), "
            + "TipoConsigna text(100),"
            + "checktesoreria text(1),"
            + "checkproveedor text(1),"
            + "NitSec text(40),"
            + "PucSec text(16), "
            + "Valor numeric(16,2), "
            + "conyear INTEGER," //año crea
            + "conmonth INTEGER," // mes crea
            + "conday INTEGER," // dia crea
            + "rconyear INTEGER,"
            + "rconmonth INTEGER,"
            + "rconday INTEGER)";

    ConsignaRecibofoto = "CREATE TABLE IF NOT EXISTS ConsignaRecibofoto("
            + "RecNro text(40),"
            + "ConNro text(40),"
            + "foto BLOB,"
            + "descr Text(220),"
            + "Valorfoto numeric(16,2), "
            + "checkfoto Text(1), "
            + "conyear INTEGER,"
            + "conmonth INTEGER,"
            + "conday INTEGER)";

    Proveedores = "CREATE TABLE IF NOT EXISTS Proveedores("
            + "NitSec text(40),"
            + "Nitide text(40),"
            + "NitCom text(80))";

    BancoProv = "CREATE TABLE IF NOT EXISTS BancoProv("
            + "NitSec text(40),"
            + "NitProBanCod integer,"
            + "BanNom text(200),"
            + "NitProBanInf text(400))";

    JustificacionSaldo = "CREATE TABLE IF NOT EXISTS JustificacionSaldo("
            + "JustSalSec INTEGER,"
            + "JustSalDes text(200))";

    ReciboFormaFotos = "CREATE TABLE IF NOT EXISTS ReciboFormaFotos("
            + "VenCod text(40) ,"
            + "nitsec text(25),"
            + "clisec INTEGER,"
            + "facnro text(40),"
            + "NroOp text(200),"
            + "ValorOp numeric(16,2),"
            + "imgBase BLOB )";

    fototemp = "CREATE TABLE IF NOT EXISTS fototemp("
            + "secuencia INTEGER,"
            + "NroOp text(200),"
            + "ValorOp numeric(16,2),"
            + "imgBase BLOB )";

    NumeroProvisional = "CREATE TABLE IF NOT EXISTS NumeroProvisional("
            + " ReForPagFotosDesc text(200),"
            + " RecPagCheque text(200),"
            + "RecFecyear integer,"
            + "RecFecmonth integer,"
            + "RecFecdia integer,"
            + "RecPagCiucod text(200),"
            + "RecPagVal  numeric(16,2)"
            + " )";



    Bancos = "CREATE TABLE IF NOT EXISTS Bancos("
            + "BANFINCOD INTEGER ,"
            + "BANFINNOM text(80),"
            + "BanFinCheckValPuc text(1),"
            + "TCNNOM text(100),"
            + "TCNSEC INTEGER)";


    BancoCuenta = "CREATE TABLE IF NOT EXISTS BancoCuenta("
            + "BANFINCOD INTEGER ,"
            + "BanFinPucSec text(16),"
            + "PucNom text(150),"
            + "puccod  text(150))";

    InvCategoria = "CREATE TABLE IF NOT EXISTS InvCategoria("
            + "InvCatCod text(10) ,"
            + "InvCatNom text(16))";

    obsCliente =  "CREATE TABLE IF NOT EXISTS obsCliente("
            + "NitSec text(10) ,"
            + "CliSec INTEGER ,"
            + "CliObsMovil text(1600))";




    DescuentosFac = "CREATE TABLE IF NOT EXISTS DescuentosFac("
            + "FacSec INTEGER ,"
            + "FacNro text(40) ,"
            + "NoOto numeric(16,2) ,"
            + "Conf numeric(16,2) ,"
            + "ConfProv numeric(16,2),"
            + "financiero numeric(16,2))";


    TablaPedidosDesc = "CREATE TABLE IF NOT EXISTS pedidoDesc("
            + "prefijo text(5),"
            + "nitsec text(25),"
            + "clisec INTEGER,"
            + "artsec text(20),"
            + "dPreArtCod text(20),"
            + "pdyear INTEGER,"
            + "pdmonth INTEGER,"
            + "pdday INTEGER,"
            + "DesSec INTEGER,"
            + "DesSecLin INTEGER,"
            + "TipoDesc text(20),"
            + "pordescapli1 numeric(8,4),pordescapli2 numeric(8,4),pordescapli3 numeric(8,4),pordescapli4 numeric(8,4),kardesgen text(1))";

    TablaPedidos = "CREATE TABLE IF NOT EXISTS pedido("
            + "prefijo text(5),"
            + "nitsec text(25),"
            + "clisec INTEGER,"
            + "artsec text(20),"
            + "vencod text(16),"
            + "pedenviado text(1),"
            + "NotaInv text(1),"
            + "NotaCar text(1),"
            + "FacFecEnt text(100),"
            + "ValEnviado text(100), "
            + "ValEnvAlt text(100), "
            + "cantcaj numeric(16,2),"
            + "cantcajinf numeric(16,2),"
            + "pedartemb INTEGER,"
            + "cant numeric(16,2),"
            + "cantinf numeric(16,2),"
            + "pdyear INTEGER,"
            + "pdmonth INTEGER,"
            + "pdday INTEGER,"
            + "bodcod INTEGER,"
            + "ConPagnom text(160),"
            + "sExistencia numeric(16,2), "
            + "precio numeric(16,2), "
            + "plazo INTEGER,"
            + "PedLisPreCod INTEGER,"
            + "PedIva numeric(16,2),"
            + "pordescCero INTEGER,pordesc2Cero INTEGER,pordesc3Cero INTEGER,pordesc4Cero INTEGER,pordesc5Cero INTEGER,pordesc6Cero INTEGER,"
            + "pordesc numeric(8,4),pordesc2 numeric(8,4),pordesc3 numeric(8,4),pordesc4 numeric(8,4),pordesc5 numeric(8,4),pordesc6 numeric(8,4),pordescno numeric(8,4),pordesc2no numeric(8,4),pordesc3no numeric(8,4),pordesc4no numeric(8,4),pordesc5no numeric(8,4),pordesc6no numeric(8,4),"
            + "fechahora text(30),confemp numeric(8,4),confprov numeric(8,4),confvend numeric(8,4),arttotimp numeric(16,2),ConNotCod Text(4),Autorizacion varchar(1),Bloqueo varchar(1), PreArtCod text(200), FechaPedido text(200)  "
            + ")";

    Pedidoenc = "CREATE TABLE IF NOT EXISTS Pedidoenc("
            + "prefijo text(5),"
            + "nitsec text(25),"
            + "clisec INTEGER,"
            + "vencod text(16),"
            + "Obs text(1000),"
            + "pdyear INTEGER,"
            + "elisprecod INTEGER,"
            + "pdmonth INTEGER,"
            + "planpuente varchar(1),"
            + "pdday INTEGER"
            + ")";

    PedidoInf = "CREATE TABLE IF NOT EXISTS PedidoInf("
            + "prefijo text(5),"
            + "nitsec text(25),"
            + "clisec INTEGER,"
            + "artsec text(20),"
            + "Cantidad numeric(16,2),"
            + "CantInf INTEGER,"
            + "secuencia INTEGER"
            + ")";



    CirreDia = "CREATE TABLE  IF NOT EXISTS  CirreDia("
            + "CirDay INTEGER,"
            + "CirMes INTEGER,"
            + "CirAno INTEGER)";

    Temporal = "CREATE TABLE  IF NOT EXISTS  aCirreDia("
            + "CirDay INTEGER,"
            + "CirMes INTEGER,"
            + "CirAno INTEGER)";

    Sincronizaciones = "CREATE TABLE  IF NOT EXISTS  Sincronizaciones("
            + "SerDay INTEGER,"
            + "SerMes INTEGER,"
            + "SerAno INTEGER,"
            + "SinDay INTEGER,"
            + "SinMes INTEGER,"
            + "SinAno INTEGER,"
            + "SinHor INTEGER,"
            + "SinMin INTEGER,"
            + "SinSeg INTEGER,"
            + "HorCie INTEGER)";

    MovParBonProdBon = "CREATE TABLE  IF NOT EXISTS  MovParBonProdBon("
            + "MovParBonSec INTEGER,"
            + "MovParBonEncArtSec text(30),"
            + "MovParBonFecMod text(30),"
            + "MovParBonClientes text(2000),"
            + "MovParBonClientesExlu text(2000),"
            + "MovParBonCanales text(500),"
            + "MovParBonEmb INTEGER,"
            + "MovParBonCantCaj INTEGER,"
            + "MovParBonCant INTEGER)";

    movparvalrango = "CREATE TABLE  IF NOT EXISTS  movparvalrango("
            + "MovParValSec INTEGER,"
            + "MovParValDetRan1 INTEGER,"
            + "MovParValDetRan2 INTEGER,"
            + "MovParValDetDesc INTEGER)";




    MovParBonBonificados = "CREATE TABLE  IF NOT EXISTS  MovParBonBonificados("
            + "MovParBonSec INTEGER,"
            + "MovParBonArtSec text(30),"
            + "MovParBonDetCantCaj INTEGER,"
            + "MovParBonDetCant INTEGER,"
            + "MovparbonResCan INTEGER)";

    MovParMix = " CREATE TABLE IF NOT EXISTS  MovParMix("
            + "MovParMixSec text(30),"
            + "MovParMixNom text(500),"
            + "MovParMixRefDis smallint NOT NULL,"
            + "MovParMixCntTotalCaj smallint ,"
            + "MovParMixCntTotal smallint NOT NULL,"
            + "MovParMixFecMod text(30),"
            + "MovParMixResCan text(1),"
            + "MovParMixNoOtor text(1),"
            + "MovParMixViaDir text(1),"
            + "MovParMixConic smallint,MovParMixPeri smallint)";

    MovParMixArticulos = " CREATE TABLE IF NOT EXISTS MovParMixArticulos("
            + "MovParMixSec INTEGER,"
            + "MovParMixDetArtSec text(30) NOT NULL,"
            + "MovParMixDetArtEmb INTEGER,"
            + "MovParMixDetCntOblCaj INTEGER,"
            + "MovParMixDetCntObl INTEGER,"
            + "MovParMixDetCntDes numeric(16,2))";

    MovParMixBonificados = "CREATE TABLE  IF NOT EXISTS  MovParMixBonificados("
            + "MovParMixSec INTEGER,"
            + "MovParMixBonArtSec text(30),"
            + "MovParMixBonCantCaj text(30),"
            + "MovParMixBonCant INTEGER)";

    MovParEsc = " CREATE TABLE IF NOT EXISTS  MovParEsc("
            + "MovParEscArtSec text(30),"
            + "MovParEscDeCaj integer,"
            + "MovParEscDe integer,"
            + "MovParEscHasta integer,"
            + "MovParEscHastaCaj integer,"
            + "MovParEscDesc1 numeric(16,2),"
            + "MovParEscDesc2 numeric(16,2),"
            + "MovParEscfecmod text(30),"
            + "MovParEscDesc text(1000),"
            +"MovParEscSec integer," +
            "MovParEscRanArtSec text(30)," +
            "MovParEscRanCant integer," +
            "MovParEscRanCantCaj integer," +
            "MovParEscArtEmb integer," +
            "MovParEscResCan text(1)," +
            "MovParEscNoOtor text(1)," +
            "MovParEscViaDir text(1)," +
            "MovParEscInd text(3))";

    MovParPrem = " CREATE TABLE IF NOT EXISTS  MovParPrem("
            + "prefijo text(5),"
            + "MovParNitSec text(20),"
            + "MovParCliSec integer,"
            + "MovParPremSec integer,"
            + "MovParPremSecLin integer,"
            + "MovParPremtipo text(40),"
            + "MovParPremcheckmax text(1),"
            + "MovParPremTip text(5),"
            + "MovParPremArtSec text(200),"
            + "MovParPremCantCaj integer,"
            + "MovParPremCant integer,"
            + "MovParPremArtSecOri text(200)," +
            "MovParPremDesc integer," +
            "MovParPremAno integer," +
            "MovParPremMes integer," +
            "MovParPremDia integer,MovParPremCantGen integer, MovParPremApli text(1), BonParBonPreArtCod Integer )";

    Canales = "CREATE TABLE  IF NOT EXISTS  Canales("
            + "cancod INTEGER,"
            + "cansubcod INTEGER,"
            + "cannom text(100),"
            + "cancona text(200),"
            + "canconb text(200),"
            + "canconc text(200),"
            + "cansubnom text(100))";


    PreciosEsp = "CREATE TABLE  IF NOT EXISTS  PreciosEspeciales("
            + "peArtSec text(40),"
            + "precioesp numeric(16,2), "
            + "peNitSec text(40))";

    DescGrupo = "CREATE TABLE  IF NOT EXISTS  DescGrupo("
            + "NitSec text(40),"
            + "CliSec integer, "
            + "clidesinvgrucod text(40) , "
            + "clidesdcto numeric(16,2) )";



    ArticulosExi = "CREATE TABLE  IF NOT EXISTS  ArticulosExi("
            + "ArtSec text(40),"
            + "ArtBodCod integer, "
            + "ArtExiAct numeric(16,2))";

    CanalOferta = "CREATE TABLE  IF NOT EXISTS  CanalOferta("
            +"FacNitSec text(40),"
            +"BonProSec integer,"
            +"BonProLinsec integer,"
            +"Tipo text(40),"
            +"canal integer,"
            +"veces integer,"
            +"puntos integer,"
            +"maxcan integer)";

    Bodegas = "CREATE TABLE  IF NOT EXISTS  Bodegas("
            + "BodCod integer,"
            +"BodCheckPred  text(2),"
            + "BodNom  text(200))";

    MovTipDir = "CREATE TABLE  IF NOT EXISTS  MovTipDir("
            + "MovTipDirCod text(30),"
            + "MovTipDirNom text(100))";



    Prospecto = "CREATE TABLE IF NOT EXISTS prospecto ("
            + "nit text(30) PRIMARY KEY,"
            + "nombre text(100),"
            + "prinom text(100),"
            + "segnom text(100),"
            + "priape text(100),"
            + "segape text(100),"
            + "fecha text(30),"
            + "direccion text(100),"
            + "establecimiento text(100),"
            + "ciudad text(100),"
            + "barrio text(100),"
            + "ciucod text(100),"
            + "Depcod text(100),"
            + "BarCod integer,"
            +"TipoCliente text(100),"
            +"perfilcliente text(100),"
            +"zona text(100),"
            +"categoria text(100),"
            +"CliCup numeric(16,2),"
            +"Plazo numeric(8,0),"
            + "telefono text(100),"
            + "celular text(100),Correo text(100),"
            + "lun text(1),"
            + "mar text(1),"
            + "mie text(1),"
            + "jue text(1),"
            + "vie text(1),"
            + "sab text(1),"
            + "dom text(1),"
            + "frecuencia text(1),"
            + "canal text(100),"
            + "subcanal text(100),"
            + "tamano text(10),"
            + "lisprecod INTEGER,"
            + "observacion text(800),"
            + "ruta text(800),"
            + "imagen text(100),vencod varchar(10),"
            + "fecyear integer ,"
            + "fecmonth integer ,"
            + "Enviado text(3) ,"
            +"ProsImg Blob,"
            +"ProsImg_GXI text(100),"
            + "fecday integer)";

}
    @Override
    public void onCreate(SQLiteDatabase BdSql) {
        createtablas();

        try {
            BdSql.execSQL(TablaListasPrecios);
            BdSql.execSQL(Temporal);
            BdSql.execSQL(TablaVersiones);
            BdSql.execSQL(ListaPorGrupoSubgrupo);
            BdSql.execSQL(TablaPedidosDesc);
            BdSql.execSQL(BonificacionesProductoDet);
            BdSql.execSQL(BonificacionesProductoDetBon);
            BdSql.execSQL(TablaBonificados);
            BdSql.execSQL(TablaNewEscala);
            BdSql.execSQL(TablaDctoGen);

            BdSql.execSQL(ControlVentas);
            BdSql.execSQL(TablaDescIba);
            BdSql.execSQL(TablaBitacora);
            BdSql.execSQL(TablaDescuentoMenta);
            BdSql.execSQL(movparvalrango);
            BdSql.execSQL(MovParLinea);
            BdSql.execSQL(MovParArt);
            BdSql.execSQL(CanalOferta);
            BdSql.execSQL(PerfildeClientes);
            BdSql.execSQL(TipodeClientes);
            BdSql.execSQL(Zona);
            BdSql.execSQL(CategoriaCliente);


            BdSql.execSQL(VentasCliente);
            BdSql.execSQL(CirreDia);
            BdSql.execSQL(TablaClientesDcto);
            BdSql.execSQL(Prospecto);
            BdSql.execSQL(PreciosEsp);
            BdSql.execSQL(DescGrupo);

            BdSql.execSQL(ArticulosExi);
            BdSql.execSQL(MovParMixBonificados);
            BdSql.execSQL(MovParMixArticulos);
            BdSql.execSQL(MovParMix);
            BdSql.execSQL(MovParEsc);
            BdSql.execSQL(Sincronizaciones);
            BdSql.execSQL(MovTipDir);
            BdSql.execSQL(Canales);
            BdSql.execSQL(Bodegas);
            BdSql.execSQL(TablaClientesDevoluciones);
            BdSql.execSQL(TablaConceptoNCND);
            BdSql.execSQL(MovParPrem);


            BdSql.execSQL(MovParBonProdBon);
            BdSql.execSQL(MovParBonBonificados);
            BdSql.execSQL(TablaPedidos);
            BdSql.execSQL(Pedidoenc);
            BdSql.execSQL(PedidoInf);

            BdSql.execSQL(TablaGrupos);
            BdSql.execSQL(TablaSubgrupo);
            BdSql.execSQL(TablaFamilia);
            BdSql.execSQL(TablaMovCauPed);
            BdSql.execSQL(TablaCartera);
            BdSql.execSQL(TablaClasePerf);
            BdSql.execSQL(TablaClasePre);

            BdSql.execSQL(TablaBarrios);
            BdSql.execSQL(TablaCiudades);
            BdSql.execSQL(TablaClientes);
            BdSql.execSQL(TablaEmpresaMovil);
           BdSql.execSQL(EmpresaGlobal);
            BdSql.execSQL(TablaUsuarios);
            BdSql.execSQL(TablaArticulos);
            BdSql.execSQL(Visita);
            BdSql.execSQL(Recibo);
            BdSql.execSQL(Reciboforma);
            BdSql.execSQL(ConsignaRecibo);
            BdSql.execSQL(ConsignaRecibofoto);

            BdSql.execSQL(Bancos);
            BdSql.execSQL(BancoCuenta);
            BdSql.execSQL(InvCategoria);
            BdSql.execSQL(obsCliente);

            BdSql.execSQL(DescuentosFac);
            BdSql.execSQL(JustificacionSaldo);
            BdSql.execSQL(Proveedores);
            BdSql.execSQL(BancoProv);

            BdSql.execSQL(ReciboFormaFotos);
            BdSql.execSQL(fototemp);

        }catch (Exception e){
            Integer jj=0;
        }

    }



    public void sincronizartodo(SQLiteDatabase BdSql){

        createtablas();
        syncTable(BdSql,TablaListasPrecios);
        syncTable(BdSql,Temporal);
        syncTable(BdSql,TablaVersiones);
        syncTable(BdSql,ListaPorGrupoSubgrupo);
        syncTable(BdSql,TablaPedidosDesc);
        syncTable(BdSql,BonificacionesProductoDet);
        syncTable(BdSql,BonificacionesProductoDetBon);
        syncTable(BdSql,TablaBonificados);
        syncTable(BdSql,TablaNewEscala);
        syncTable(BdSql,TablaDctoGen);

        syncTable(BdSql,ControlVentas);
        syncTable(BdSql,TablaDescIba);
        syncTable(BdSql,TablaBitacora);
        syncTable(BdSql,TablaDescuentoMenta);
        syncTable(BdSql,movparvalrango);
        syncTable(BdSql,MovParLinea);
        syncTable(BdSql,MovParArt);
        syncTable(BdSql,CanalOferta);
        syncTable(BdSql,PerfildeClientes);
        syncTable(BdSql,TipodeClientes);
        syncTable(BdSql,Zona);
        syncTable(BdSql,CategoriaCliente);


        syncTable(BdSql,VentasCliente);
        syncTable(BdSql,CirreDia);
        syncTable(BdSql,TablaClientesDcto);
        syncTable(BdSql,Prospecto);
        syncTable(BdSql,PreciosEsp);
        syncTable(BdSql,DescGrupo);

        syncTable(BdSql,ArticulosExi);
        syncTable(BdSql,MovParMixBonificados);
        syncTable(BdSql,MovParMixArticulos);
        syncTable(BdSql,MovParMix);
        syncTable(BdSql,MovParEsc);
        syncTable(BdSql,Sincronizaciones);
        syncTable(BdSql,MovTipDir);
        syncTable(BdSql,Canales);
        syncTable(BdSql,Bodegas);
        syncTable(BdSql,TablaClientesDevoluciones);
        syncTable(BdSql,TablaConceptoNCND);
        syncTable(BdSql,MovParPrem);


        syncTable(BdSql,MovParBonProdBon);
        syncTable(BdSql,MovParBonBonificados);
        syncTable(BdSql,TablaPedidos);
        syncTable(BdSql,Pedidoenc);
        syncTable(BdSql,PedidoInf);

        syncTable(BdSql,TablaGrupos);
        syncTable(BdSql,TablaSubgrupo);
        syncTable(BdSql,TablaFamilia);
        syncTable(BdSql,TablaMovCauPed);
        syncTable(BdSql,TablaCartera);
        syncTable(BdSql,TablaClasePerf);
        syncTable(BdSql,TablaClasePre);

        syncTable(BdSql,TablaBarrios);
        syncTable(BdSql,TablaCiudades);
        syncTable(BdSql,TablaClientes);
        syncTable(BdSql,TablaEmpresaMovil);
        syncTable(BdSql,EmpresaGlobal);
        syncTable(BdSql,TablaUsuarios);
        syncTable(BdSql,TablaArticulos);
        syncTable(BdSql,Visita);
        syncTable(BdSql,Recibo);
        syncTable(BdSql,Reciboforma);
        syncTable(BdSql,ConsignaRecibo);
        syncTable(BdSql,ConsignaRecibofoto);

        syncTable(BdSql,Bancos);
        syncTable(BdSql,BancoCuenta);
        syncTable(BdSql,InvCategoria);
        syncTable(BdSql,obsCliente);

        syncTable(BdSql,DescuentosFac);
        syncTable(BdSql,JustificacionSaldo);
        syncTable(BdSql,Proveedores);
        syncTable(BdSql,BancoProv);

        syncTable(BdSql,ReciboFormaFotos);
        syncTable(BdSql,fototemp);
        syncTable(BdSql,NumeroProvisional);
    }







    public void BorrarBd(SQLiteDatabase BdSql){

        String TablaListasPrecios = "drop table IF EXISTS ListasPrecios";
        String VersionMovil = "drop table IF EXISTS VersionMovil";
        String ListaPorGrupoSubgrupo = "drop table IF EXISTS ListaPorGrupoSubgrupo";
        String PedidosDesc = "drop table IF EXISTS PedidoDesc";
        String BonificacionesProductoDet = "drop table IF EXISTS BonificacionesProductoDet";
        String BonificacionesProductoDetBon = "drop table IF EXISTS BonificacionesProductoDetBon";
        String BonificacionesProducto = "drop table IF EXISTS BonificacionesProducto";
        String Descuentos = "drop table IF EXISTS Descuentos";
        String DescuentosDetalle = "drop table IF EXISTS DescuentosDetalle";

        String Recibo = "drop table IF EXISTS Recibo";
        String Reciboforma = "drop table IF EXISTS Reciboforma";
        String ConsignaRecibo = "drop table IF EXISTS ConsignaRecibo";
        String ReciboFormaFotos = "drop table IF EXISTS ReciboFormaFotos";


        String Proveedores = "drop table IF EXISTS Proveedores";
        String BancoProv = "drop table IF EXISTS BancoProv";

        String JustificacionSaldo = "drop table IF EXISTS JustificacionSaldo";
        String fototemp = "drop table IF EXISTS fototemp";
        String NumeroProvisional = "drop table IF EXISTS NumeroProvisional";
        String Bancos = "drop table IF EXISTS Bancos";
        String BancoCuenta = "drop table IF EXISTS BancoCuenta";
        String InvCategoria = "drop table IF EXISTS InvCategoria";
        String obsCliente = "drop table IF EXISTS obsCliente";


        String DescuentosFac = "drop table IF EXISTS DescuentosFac";
        String ControlVentas = "drop table IF EXISTS ControlVentas";
        String TablaBitacora = "drop table IF EXISTS Bitacora";
        String TablaMovParDesPro = "drop table IF EXISTS MovParDesPro";



        String TablaEmpresaMovil = "drop table IF EXISTS EmpresaMovil";
        String TablaEmpresa = "drop table IF EXISTS EmpresaGlobal";
        String VentasCliente = "drop table IF EXISTS VentasCliente";
        String TablaUsuarios = "drop table IF EXISTS Usuarios";
        String TablaClientesDcto = "drop table IF EXISTS ClientesDcto";
        String TablaClientes = "drop table IF EXISTS Clientes";
        String TablaClientesDevoluciones = "drop table IF EXISTS ClientesDevoluciones ";
        String MovParLinea = "drop table IF EXISTS MovParLinea ";
        String  MovParArt = "drop table IF EXISTS MovParArt ";
        String TablaCiudades = "drop table IF EXISTS Ciudades ";
        String TablaConceptoNCND = "drop table IF EXISTS ConceptoNCND ";
        String TablaBarrios = "drop table IF EXISTS Barrios ";
        String TablaClasePerf = "drop table IF EXISTS PerfilClientesClase";
        String TablaClasePre = "drop table IF EXISTS ArticulosPresentacion";


        String TablaCartera = "drop table IF EXISTS cartera";
        String TablaMovCauPed = "drop table IF EXISTS MovCauPed ";
        String TablaGrupos = "drop table IF EXISTS inventariogrupo";
        String TablaSubgrupo = "drop table IF EXISTS inventariosubgrupo";
        String TablaFamilia = "drop table IF EXISTS inventariofamilia";
        String TablaArticulos = "drop table IF EXISTS articulos";
        String Visita = "drop table IF EXISTS Visita";
        String CanalOferta = "drop table IF EXISTS CanalOferta";
        String PerfildeClientes = "drop table IF EXISTS PerfildeClientes";
        String TipodeClientes = "drop table IF EXISTS TipodeClientes";
        String Zona = "drop table IF EXISTS Zona";
        String CategoriaCliente = "drop table IF EXISTS CategoriaCliente";


        String Pedidoenc = "drop table IF EXISTS  Pedidoenc";
        String TablaPedidos = "drop table IF EXISTS pedido";
        String CirreDia = "drop table IF EXISTS CirreDia";
        String Sincronizaciones = "drop table IF EXISTS  Sincronizaciones";
        String MovParBonProdBon = "drop table  IF EXISTS MovParBonProdBon";
        String movparvalrango = "drop table IF EXISTS movparvalrango";
        String MovParBonBonificados = "drop table  IF EXISTS MovParBonBonificados";
        String MovParMix = " drop table IF EXISTS MovParMix";
        String MovParMixArticulos = " drop table IF EXISTS MovParMixArticulos";
        String MovParMixBonificados = "drop table IF EXISTS  MovParMixBonificados";
        String MovParEsc = " drop table IF EXISTS  MovParEsc";
        String MovParPrem = " drop table IF EXISTS  MovParPrem";
        String Canales = "drop table IF EXISTS  Canales";
        String MovTipDir = "drop table  IF EXISTS MovTipDir";
        String Prospecto = "drop table  IF EXISTS prospecto ";
        String PreciosEsp = "drop table  IF EXISTS PreciosEspeciales ";
        String DescGrupo = "drop table  IF EXISTS DescGrupo ";


        String ArticuloExit = "drop table  IF EXISTS ArticulosExi ";
        String bodega = "drop table  IF EXISTS Bodegas ";
        String DescIbaEscala = "drop table  IF EXISTS DescIbaEscala ";

        try {

            BdSql.execSQL(TablaListasPrecios);
            BdSql.execSQL(VersionMovil);
            BdSql.execSQL(ListaPorGrupoSubgrupo);
            BdSql.execSQL(PedidosDesc);
            BdSql.execSQL(BonificacionesProductoDet);
            BdSql.execSQL(BonificacionesProductoDetBon);
            BdSql.execSQL(BonificacionesProducto);
            BdSql.execSQL(Descuentos);
            BdSql.execSQL(DescuentosDetalle);

            BdSql.execSQL(ControlVentas);
            BdSql.execSQL(DescIbaEscala);
            BdSql.execSQL(TablaBitacora);
            BdSql.execSQL(movparvalrango);
            BdSql.execSQL(MovParLinea);
            BdSql.execSQL(MovParArt);
            BdSql.execSQL(VentasCliente);
            BdSql.execSQL(CirreDia);
            BdSql.execSQL(Prospecto);
            BdSql.execSQL(PreciosEsp);
            BdSql.execSQL(DescGrupo);
            BdSql.execSQL(TablaEmpresa);
            BdSql.execSQL(TablaMovParDesPro);

            BdSql.execSQL(CanalOferta);
            BdSql.execSQL(PerfildeClientes);
            BdSql.execSQL(TipodeClientes);
            BdSql.execSQL(Zona);
            BdSql.execSQL(CategoriaCliente);
            BdSql.execSQL(ArticuloExit);
            BdSql.execSQL(MovParMixBonificados);
            BdSql.execSQL(MovParMixArticulos);
            BdSql.execSQL(MovParMix);
            BdSql.execSQL(MovParEsc);
            BdSql.execSQL(Sincronizaciones);
            BdSql.execSQL(MovTipDir);
            BdSql.execSQL(Canales);
            BdSql.execSQL(TablaClientesDevoluciones);
            BdSql.execSQL(TablaConceptoNCND);
            BdSql.execSQL(bodega);
            //BdSql.execSQL(MovParPrem);
            BdSql.execSQL(MovParBonProdBon);
            BdSql.execSQL(MovParBonBonificados);
            //BdSql.execSQL(TablaPedidos);

            BdSql.execSQL(TablaGrupos);
            BdSql.execSQL(TablaSubgrupo);
            BdSql.execSQL(TablaFamilia);
            BdSql.execSQL(TablaMovCauPed);
            BdSql.execSQL(TablaCartera);
            BdSql.execSQL(TablaClasePerf);
            BdSql.execSQL(TablaClasePre);


            BdSql.execSQL(TablaBarrios);
            BdSql.execSQL(TablaCiudades);
            BdSql.execSQL(TablaClientes);
            BdSql.execSQL(TablaEmpresaMovil);
           // BdSql.execSQL(TablaEmpresa);
            BdSql.execSQL(TablaUsuarios);
            BdSql.execSQL(TablaArticulos);
            BdSql.execSQL(Visita);

            //BdSql.execSQL(Recibo);
           // BdSql.execSQL(Reciboforma);
          //  BdSql.execSQL(ConsignaRecibo);
           // BdSql.execSQL(ReciboFormaFotos);


            BdSql.execSQL(JustificacionSaldo);
            BdSql.execSQL(Proveedores);
            BdSql.execSQL(BancoProv);

            BdSql.execSQL(fototemp);
            BdSql.execSQL(NumeroProvisional);
            BdSql.execSQL(Bancos);
            BdSql.execSQL(BancoCuenta);
            BdSql.execSQL(InvCategoria);
            BdSql.execSQL(obsCliente);

            BdSql.execSQL(DescuentosFac);


        }catch (Exception e){
            Integer jj=0;
        }
        try {
            BdSql.execSQL(TablaClientesDcto);
        }catch (Exception e){
            Integer jj=0;
        }

    }
    public int[] CargarUsuarios(SQLiteDatabase BdSql,String VenId,Context context){
        int Insertados=0;
        int Errores=0;
        String vEmpresa,mantisficc;
        try {

            ConBd conbd = new ConBd();
            classbd classbd = new classbd();
            Connection conn = conbd.CargarConexion(context);
            Statement comm = conn.createStatement();
            conbd.Variables();
            mantisficc = conbd.MantisFicc;
            String nScript= "";
            nScript = "select VENUSUMOV,VenCod,VenId,VenNom,isnull(VenClaMov,'') VenCla,isnull(v.ParMovSec,1) ParMovSec,isnull(Succod,1) SucCod, isnull(alinegcod,1) alinegcod,isnull(ParMovPedMin,0) ParMovPedMin,isnull(ParMovBon,'') ParMovBon,isnull(ParMovManCanCaj,'') ParMovManCanCaj,isnull(ParMovManDesConf,'') ParMovManDesConf,isnull(ParMovNoOtorgar,'') ParMovNoOtorgar,isnull(ParMovDescV2,'N') ParMovDescV2,'TRA' ParMovTatTra, isnull(VenPerModDcto,'N')  VenPerModDcto , 'N' ParMovEnvAlt, isnull(ParMovBloqCar,'N') ParMovBloqCar, isnull(ParMovBloqNot,'N') ParMovBloqNot from Vendedores v left join ParametrosMoviles pm on v.parmovsec=pm.parmovsec   " +
                    " where venid='" + VenId + "' or VENUSUMOV = '"+VenId+"' ";


              Log.e("escriParmov",nScript);
            ResultSet rs = comm.executeQuery(classbd.FormatearMysql(nScript));

            BdSql.execSQL("Delete from Usuarios");

            while (rs.next()){


                String texto ="Insert into Usuarios (VenCod,VENUSUMOV,VenId,VenNom,VenCla,alinegcod,SucCod,ParMovPedMin,ParMovBon,ParMovManCanCaj,ParMovManDesConf,ParMovNoOtorgar,ParMovDescV2,ParMovTatTra,ParMovSec,EnvAlt,modDcto,ParMovBloqCar,ParMovBloqNot) values('"+
                        rs.getString("VenCod").trim() + "','" +
                        rs.getString("VENUSUMOV").trim() + "','" +
                        rs.getString("VenId").trim() + "','" +
                        rs.getString("VenNom").trim() + "','" +
                        rs.getString("VenCla").trim() + "'," +
                        rs.getString("alinegcod").trim() + "," +
                        rs.getString("SucCod").trim() + "," +
                        rs.getString("ParMovPedMin").trim() + ",'" +
                        rs.getString("ParMovBon").trim() + "','" +
                        rs.getString("ParMovManCanCaj").trim() + "','" +
                        rs.getString("ParMovManDesConf").trim() + "','" +
                        rs.getString("ParMovNoOtorgar").trim() + "','" +
                        rs.getString("ParMovDescV2").trim() + "','" +
                        rs.getString("ParMovTatTra").trim() + "'," +
                        rs.getString("ParMovSec").trim() + ",'" +
                        rs.getString("ParMovEnvAlt").trim() + "','" +
                        rs.getString("VenPerModDcto").trim() + "','"+
                        rs.getString("ParMovBloqCar").trim() + "','"+
                        rs.getString("ParMovBloqNot").trim() + "')";


                       // rs.getString("ParMovDescV2")+"')";

                try {
                    BdSql.execSQL(texto);
                    Insertados+=1;
                }catch (Exception ex){
                    Log.e("exxx",ex.toString());
                    Errores=1;
                }
            }
        } catch (SQLException e) {
            Errores=9999;
            Log.e("exxeeeex",e.toString());
        }
        int[] Resultado= new int[]{Insertados,Errores};
        Log.e("exxeeeexResultado",String.valueOf(Resultado[0]) );

        return Resultado;
    }


    public int[] CargarClientes(SQLiteDatabase BdSql,Context context){

        GlobalVariables vGlobalVariables= GlobalVariables.getInstance();
        String vUsuario=vGlobalVariables.getUsuario();
        int TotalFilas=0;
        int Insertados=0;
        int Errores=0;
        try {
            classbd classbd = new classbd();
            ConBd conbd = new ConBd();
            Connection conn = conbd.CargarConexion(context);
            Statement comm = conn.createStatement();
            String Script="select * from(select  ROW_NUMBER() OVER(ORDER BY name ASC) AS Row,c.nitsec,c.clisec,NitCom,CliNom,NitIde" +
                    ",'('+RTRIM(CIUNOM)+')'+'('+RTRIM(BARNOM)+') '+dbo.RemoveChars2(CliDir) CliDir,lisprecod,CliTel,CLICONPAG,CliIntLun,CliIntMar" +
                    " ,CliIntMie,CliIntJue,CliIntVie,CliInSab CliIntSab,CliIntDom,isnull(PerCliCod,1)," +
                    " isnull(c.CanCod,1) CanCod,isnull(CanNom,''),isnull(CanSubCod,1) CanSubCod,isnull(CanSubNom,''),CliCup,CliVenCup," +
                    " (select ISNULL(DATEDIFF(day, max(facfec), getdate()),999) Duration from factura where facnitsec=c.nitsec and facClisec=c.clisec and factiptra='FDV' AND FACEST='A') CliDiasUltVen " +
                    " from ClientesVendedores cv " +
                    " left join clientes c on cv.NitSec=C.NitSec and cv.CliSec=c.CliSec" +
                    " left join nit n on n.nitsec=c.nitsec" +
                    " left join ciudad on cliciucod=ciucod" +
                    " left join Canales cn on cn.cancod=c.cancod" +
                    " left join CanalesSubCanales cnsc  on cnsc.cancod=c.cancod and cnsc.cansubcod=c.cansubcod" +
                    " LEFT JOIN Barrio B ON B.BarCod=C.BarCod" +
                    " where VenCod='"+vUsuario+"') Consulta order by Row desc";

            ResultSet rsClientes = comm.executeQuery(classbd.FormatearMysql(Script));
            BdSql.execSQL("Delete from Clientes");
            while (rsClientes.next()){
                TotalFilas+=1;
                String InsertScript = "nitsec,"
                        + "clisec,"
                        + "NitCom,"
                        + "CliNom,"
                        + "NitIde,"
                        + "CliDir,"
                        + "CliTel,"
                        + "Lisprecod,"
                        + "CliConPag,"
                        +"cliintlun,"
                        +"cliintmar,"
                        +"cliintmie,"
                        +"cliintjue,"
                        +"cliintvie,"
                        +"cliintsab,"
                        +"cliintdom,"
                        +"PerCliCod,"
                        +"CanCod,"
                        +"CanNom,"
                        +"CanSubCod,"
                        +"CanSubNom,"
                        +"CliCup,"
                        +"CliVenCup,"
                        +"CliDiasUltVen values ("
                        + rsClientes.getString("nitsec").trim() + "',"
                        + rsClientes.getString("clisec").trim() + ","
                        + rsClientes.getString("NitCom").trim() + "','"
                        + rsClientes.getString("CliNom").trim() + "','"
                        + rsClientes.getString("NitIde").trim() + "','"
                        + rsClientes.getString("CliDir").trim() + "','"
                        + rsClientes.getString("CliTel").trim() + "',"
                        + rsClientes.getString("Lisprecod").trim() + ","
                        + rsClientes.getString("CliConPag").trim() + ","
                        +rsClientes.getString("cliintlun").trim() + "','"
                        +rsClientes.getString("cliintmar").trim() + "','"
                        +rsClientes.getString("cliintmie").trim() + "','"
                        +rsClientes.getString("cliintjue").trim() + "','"
                        +rsClientes.getString("cliintvie").trim() + "','"
                        +rsClientes.getString("cliintsab").trim() + "','"
                        +rsClientes.getString("cliintdom").trim() + "',"
                        +rsClientes.getString("PerCliCod").trim() + ","
                        +rsClientes.getString("CanCod").trim() + ","
                        +rsClientes.getString("CanNom").trim() + "',"
                        +rsClientes.getString("CanSubCod").trim() + ","
                        +rsClientes.getString("CanSubNom").trim() + "',"
                        +rsClientes.getString("CliCup").trim() + ","
                        +rsClientes.getString("CliVenCup").trim() + ","
                        +rsClientes.getString("CliDiasUltVen").trim() + ")" ;
                try {
                    BdSql.execSQL(InsertScript);
                    Insertados+=1;
                }catch (Exception ex){
                    Log.e("Clientes",ex.toString());
                    Errores=1;
                }
            }
        } catch (SQLException e) {
            Errores=9999;
        }
        int[] Resultado= new int[]{Insertados,Errores,TotalFilas};
        return Resultado;
    }


    public int[] CargarSubGrupos(SQLiteDatabase BdSql,Context context){
        int Insertados=0;
        int Errores=0;
        try {
            ConBd conbd = new ConBd();
            Connection conn = conbd.CargarConexion(context);
            Statement comm = conn.createStatement();
            ResultSet rs = comm.executeQuery("select invsubgrucod,invgrucod,invsubgrunom from inventarioSubgrupo");
            BdSql.execSQL("Delete from subgrupo");
            while (rs.next()){
                String texto = "Insert into subgrupo (idsubgrupsec,idgrupid,subgrunom) values" +
                        " ('"+rs.getString("invsubgrucod").trim()+"','" +
                        rs.getString("invgrucod").trim() + "','" + rs.getString("invsubgrunom") +"')";
                try {
                    BdSql.execSQL(texto);
                    Insertados+=1;
                }catch (Exception ex){
                    Errores=1;
                }
            }
        } catch (SQLException e) {
        }
        int[] Resultado= new int[]{Insertados,Errores};
        return Resultado;
    }
    public int[] CargarClaseXperf(String Respuesta,SQLiteDatabase BdSql,Context context){
        int Insertados=0;
        int Errores=0;
        try {
            ConBd conbd = new ConBd();
            Connection conn = conbd.CargarConexion(context);
            Statement comm = conn.createStatement();
            ResultSet rs = comm.executeQuery("select PerCliCod,ClaArtCod,PerCliDetDes1 from inventarioSubgrupo");
            BdSql.execSQL("Delete from subgrupo");
            while (rs.next()){
                String texto = "Insert into claseperf (PerCliCod,ClaArtCod,DetDes) values" +
                        " ("+rs.getString("PerCliCod").trim() + "," +
                        rs.getString("ClaArtCod").trim()+"," +rs.getString("PerCliDetDes1") + ")";
                try {
                    BdSql.execSQL(texto);
                    Insertados+=1;
                }catch (Exception ex){
                    Errores=1;
                }
            }
        } catch (SQLException e) {
        }
        int[] Resultado= new int[]{Insertados,Errores};
        return Resultado;
    }

    public int[] CargarCausales(String Respuesta,SQLiteDatabase BdSql,Context context){
        int Insertados=0;
        int Errores=0;
        try {
            ConBd conbd = new ConBd();
            Connection conn = conbd.CargarConexion(context);
            Statement comm = conn.createStatement();
            ResultSet rs = comm.executeQuery("select MovCauSec,MovCauNom,MovCauTip from MovCauPed");
            while (rs.next()){
                BdSql.execSQL("Delete from Ciudades where ciucod='"+rs.getString("ciucod").trim()+"'");
                String texto = "Insert into conceptos (idcod,nomcon,ch) values" +
                        " ("+rs.getString("MovCauSec").trim()+",'" +
                        rs.getString("MovCauNom").trim()+"',"+rs.getString("MovCauTip").trim()+")";
                try {
                    BdSql.execSQL(texto);
                    Insertados+=1;
                }catch (Exception ex){
                    Errores=1;
                }
            }
        } catch (SQLException e) {
        }
        int[] Resultado= new int[]{Insertados,Errores};
        return Resultado;
    }

    public int[] CargarCiudades(String Respuesta,SQLiteDatabase BdSql,Context context){
        int Insertados=0;
        int Errores=0;
        try {
            ConBd conbd = new ConBd();
            Connection conn = conbd.CargarConexion(context);
            Statement comm = conn.createStatement();
            ResultSet rs = comm.executeQuery("Select ciucod,ciunom from ciudad");
            BdSql.execSQL("Delete from ciudad");
            while (rs.next()){
                BdSql.execSQL("Delete from Ciudades where ciucod='"+rs.getString("ciucod").trim()+"'");
                String texto  = "Insert into ciudades(ciucod,ciunom) values" +
                        "('"+rs.getString("ciucod").trim() +"','" +
                        rs.getString("ciunom") +"')";
                try {
                    BdSql.execSQL(texto);
                    Insertados+=1;
                }catch (Exception ex){
                    Errores=1;
                }
            }
        } catch (SQLException e) {
        }
        int[] Resultado= new int[]{Insertados,Errores};
        return Resultado;
    }

    public int[] CargarBarrios(String Respuesta,SQLiteDatabase BdSql,Context context){
        int Insertados=0;
        int Errores=0;
        try {
            ConBd conbd = new ConBd();
            Connection conn = conbd.CargarConexion(context);
            Statement comm = conn.createStatement();
            ResultSet rs = comm.executeQuery("Select barcod,barnom from barrio");
            BdSql.execSQL("Delete from barrio");
            while (rs.next()) {
                BdSql.execSQL("Delete from barrios where barcod='"+rs.getString("barcod").trim()+"'");
                String texto  = "Insert into barrios(barcod,barnom) values" +
                        "('"+rs.getString("barcod").trim() +"','" +
                        rs.getString("barnom") +"')";
                try {
                    BdSql.execSQL(texto);
                    Insertados+=1;
                }catch (Exception ex){
                    Errores=1;
                }
            }
        } catch (SQLException e) {
        }
        int[] Resultado= new int[]{Insertados,Errores};
        return Resultado;
    }


    @Override
    public void onUpgrade(SQLiteDatabase BdSql, int i, int i2) {

    }
    @SuppressLint("Range")
    public static void syncTable(SQLiteDatabase db, String createSQL) {

        // 🔹 Obtener nombre tabla (FIX)
        String table = createSQL
                .substring(createSQL.toUpperCase().indexOf("TABLE") + 5, createSQL.indexOf("("))
                .replace("IF NOT EXISTS", "")
                .trim();

        // 🔹 Columnas definidas
        String columnasDef = createSQL.substring(
                createSQL.indexOf("(") + 1,
                createSQL.lastIndexOf(")")
        );

        String[] columnas = columnasDef.split(",");

        // 🔹 Columnas existentes
        List<String> existentes = new ArrayList<>();
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + table + ")", null);
        while (cursor.moveToNext()) {
            existentes.add(cursor.getString(cursor.getColumnIndex("name")));
        }
        cursor.close();

        for (String col : columnas) {

            col = col.trim();

            if (col.toUpperCase().startsWith("PRIMARY") ||
                    col.toUpperCase().startsWith("FOREIGN") ||
                    col.toUpperCase().startsWith("UNIQUE")) {
                continue;
            }

            String nombreCol = col.split(" ")[0];

            if (!existentes.contains(nombreCol)) {
                try {
                    db.execSQL("ALTER TABLE " + table + " ADD COLUMN " + col);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
