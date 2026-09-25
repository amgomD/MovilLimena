package com.ficc.mwmovil;


public class PeticionesHTTPCLIENT {

//extends Activity
//    @Override
//   protected void onCreate(Bundle savedInstanceState) {
//       super.onCreate(savedInstanceState);
//       setContentView(R.layout.activity_home);
//    }
/*

    private static String convertStreamToString(InputStream is) {
        */
/*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         *//*

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public String leer(String laurl,String ides,String vendedor) throws MalformedURLException, JSONException {
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        HttpContext contexto = new BasicHttpContext();
        HttpParams httpParameters = new BasicHttpParams();

        //httpParameters.add("LOSIDES",ides);

        HttpConnectionParams.setConnectionTimeout(httpParameters, 30000);
        HttpConnectionParams.setSoTimeout(httpParameters, 30000);


        HttpClient cliente =new DefaultHttpClient(httpParameters);

        // HttpGet httpget = new HttpGet("http://181.51.250.234:8080/ventatotal/GetData.php");
        String resultado=null;
        try {
            HttpGet httpget;
            if(ides.isEmpty() && vendedor.isEmpty()){
                httpget = new HttpGet(laurl);
            }else{
                ides=ides.replace('{','*');
                ides=ides.replace('}','$');
                ides=ides.replace('"', '\'');
                //
                httpget = new HttpGet(laurl+ "?losides="+ides+"&vencod="+vendedor);
            }
            HttpResponse response = cliente.execute(httpget,contexto); // resultado
            HttpEntity entity = response.getEntity();
            InputStream instream = entity.getContent();
            resultado= convertStreamToString(instream);

        } catch (Exception e) {
            resultado= "error "+e.getMessage();
        }
        return resultado;
    }

    public String leerPOST(String laurl,String ides,String vendedor) throws MalformedURLException, JSONException {
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        HttpContext contexto = new BasicHttpContext();
        HttpParams httpParameters = new BasicHttpParams();

        //httpParameters.add("LOSIDES",ides);

        HttpConnectionParams.setConnectionTimeout(httpParameters, 30000);
        HttpConnectionParams.setSoTimeout(httpParameters, 30000);



        HttpClient cliente =new DefaultHttpClient(httpParameters);



        // HttpGet httpget = new HttpGet("http://181.51.250.234:8080/ventatotal/GetData.php");
        String resultado=null;
        try {
            HttpPost Http;

            HttpGet httpget;
            if(ides.isEmpty() && vendedor.isEmpty()){
                httpget = new HttpGet(laurl);
            }else{
                ides=ides.replace('{','*');
                ides=ides.replace('}','$');
                ides=ides.replace('"', '\'');
                //
                httpget = new HttpGet(laurl);
            }
            HttpResponse response = cliente.execute(httpget,contexto); // resultado
            HttpEntity entity = response.getEntity();
            InputStream instream = entity.getContent();
            resultado= convertStreamToString(instream);

        } catch (Exception e) {
            resultado= "error "+e.getMessage();
        }
        return resultado;
    }

    public String leerFor(String laurl) throws MalformedURLException, JSONException {

        HttpContext contexto = new BasicHttpContext();
        HttpParams httpParameters = new BasicHttpParams();
        //httpParameters.setIntParameter("desde",0);
        HttpConnectionParams.setConnectionTimeout(httpParameters, 30000);
        HttpConnectionParams.setSoTimeout(httpParameters, 30000);
        HttpClient cliente =new DefaultHttpClient(httpParameters);
        // HttpGet httpget = new HttpGet("http://181.51.250.234:8080/ventatotal/GetData.php");
        HttpGet httpget = new HttpGet(laurl);
        String resultado=null;

        try{
            HttpResponse response = cliente.execute(httpget,contexto);// ,resultado

            HttpEntity entity = response.getEntity();
            InputStream instream = entity.getContent();
            resultado= convertStreamToString(instream);

            //String result = EntityUtils.toString(response.getEntity());
            //resultado = result;
            //resultado = instream.toString();

        } catch (Exception e) {
            resultado= "error "+e.getMessage();
        }

        return resultado;
    }
*/

}