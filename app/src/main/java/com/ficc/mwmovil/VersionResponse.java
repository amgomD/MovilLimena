package com.ficc.mwmovil;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class VersionResponse {


    public String actualizar = "N";
    public String notas ="";
    public String nuevaVersion="";
    public String ruta="";

    public static VersionResponse consultarVersion(String versionActual) {

        VersionResponse resp = new VersionResponse();
        ConBd conbd = new ConBd();
        conbd.Variables();
        String sql= conbd.UrlGetEnvio;
        Log.e("Erorr versionz sql",sql);

        try {

            URL url = new URL(sql);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = "{"
                    + "\"apExtTip\":\"PED\","
                    + "\"ApExtVerCod\":\"" + versionActual + "\""
                    + "}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

                StringBuilder response = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }

                br.close();

                String respStr = response.toString();

                // Parseo simple
                resp.actualizar = extraerValor(respStr, "actualizar");
                resp.notas = extraerValor(respStr, "apExtVerNotas");
                resp.nuevaVersion = extraerValor(respStr, "nApExtVerCod");
                resp.ruta = extraerValor(respStr, "pApExtVerPath");
            } catch (Exception e) {
                Log.e("Erorr versionz ",e.toString());
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Erorr version ",e.toString());
        }

        return resp;
    }

    public static String extraerValor(String json, String clave) {

        try {

            String buscar = "\"" + clave + "\":";
            int inicio = json.indexOf(buscar);

            if (inicio == -1) {
                return "";
            }

            inicio = json.indexOf("\"", inicio + buscar.length()) + 1;
            int fin = json.indexOf("\"", inicio);

            return json.substring(inicio, fin);

        } catch (Exception e) {
            return "";
        }

    }
}
