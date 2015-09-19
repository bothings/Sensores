package br.com.brunooliveira.sensores;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bo on 11/09/15.
 */
public class SensoresDAO extends AsyncTask<String, String, String> {
    private Context context;
    private ISensoresDAO pi;
    JSONParser jsonParser = new JSONParser();
    private static final String TAG_SUCCESS = "success";


    public SensoresDAO(Context ctx, ISensoresDAO in){
        this.context = ctx;
        this.pi=in;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected String doInBackground(String... args) {
        String light = args[0];
        String temperature = args[1];
        String humidity = args[2];
        String location=args[3];
        String noise = args[4];

        //Criando parâmetros
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("light", light));
        params.add(new BasicNameValuePair("temperature", temperature));
        params.add(new BasicNameValuePair("humidity", humidity));
        params.add(new BasicNameValuePair("location", location));
        params.add(new BasicNameValuePair("noise", noise));

        JSONObject json = jsonParser.makeHttpRequest(args[5],
                "POST", params);

        try {
            int success = json.getInt(TAG_SUCCESS);

            if (success == 1) {
                // successfully

                // closing this screen
                //finish();
            } else {
                // failed
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String params) {
        pi.afterSend(params);

    }

}

