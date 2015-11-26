package br.com.brunooliveira.sensores;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

/**
 * Created by bo on 04/09/15.
 * Classe que realiza a conexão e obtenção de dados do servidor de tempo Open Weather Map caso o aparelho não possua sensores de
 * temperatura e umidade
 */
public class Weather extends AsyncTask<Void, Void, String[]> {

    private WeatherListener listener;
    private static final String URLLocal = "http://api.openweathermap.org/data/2.5/weather?q=Salvador,br&appid=779c8195e6d62b92cda391166bef6f28";

    public Weather(WeatherListener listener){
        this.listener=listener;
    }

    @Override
    protected String[] doInBackground(Void... params) {

        try {
            String resultado = null;
            resultado= consultaServidor();
            return interpretaResultado(resultado);
        }catch (IOException e){
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String[] interpretaResultado(String result) throws JSONException {
        JSONObject object = new JSONObject(result);
        JSONObject obj = object.getJSONObject("main");

        DecimalFormat df = new DecimalFormat("00");

        String [] resultTemp = new String[2];
        resultTemp[0] = df.format(obj.getDouble("temp")-273)+"ºC"+" (by openweathermap)";
        resultTemp[1]=  obj.getInt("humidity")+"%"+" (by openweathermap)";
        Log.i("news", "pos0: " + resultTemp[0] + " pos1: " + resultTemp[1]);
        return resultTemp;
    }


    private String consultaServidor() throws IOException {
        InputStream is = null;
        try{
            URL url = new URL(URLLocal);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.connect();
            conn.getResponseCode();

            is = conn.getInputStream();

            Reader reader = null;
            reader = new InputStreamReader(is);
            char[] buffer = new char[2048];
            reader.read(buffer);
            return new String(buffer);

        }finally {
            if(is != null){
                is.close();
            }
        }
    }

    @Override
    protected void onPostExecute(String [] result) {
        listener.onResult(result);
    }

    public interface WeatherListener {
        void onResult(String[] result);
    }
}
