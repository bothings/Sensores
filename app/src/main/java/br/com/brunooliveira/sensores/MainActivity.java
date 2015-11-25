package br.com.brunooliveira.sensores;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

//Activity que representa a tela principal do aplicativo. Os trechos de códigos relativos ao envio dos dados dos sensores ao
//servidor foram desabilitadas, pois o servidor ainda não está disponível.
//Todas as informações e métodos para obtenção de dados de sensores estão nesta classe.
public class MainActivity extends ActionBarActivity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, Weather.WeatherListener{ //ISensoresDAO - Retirado pois as configurações de Servidor estão desativas

    //Declaraçao de atributos da Activity
    private Sensor lightSensor;
    private Sensor temperatureSensor;
    private Sensor humiditySensor;
    private SensorManager sm;
    private TextView light;
    private TextView temperature;
    private TextView humidity;
    private TextView GPS;
    private TextView noise;
    private GoogleApiClient mGoogleApiClient;
    //private static String url_insert_row = "http://192.168.0.102/insert.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instanciaçao de componentes de Layout
        light = (TextView) findViewById(R.id.lightSensorTx);
        temperature= (TextView) findViewById(R.id.temperatureSensorTx);
        humidity =(TextView) findViewById(R.id.humiditySensorTx);
        GPS= (TextView) findViewById(R.id.GPSSensorTx);
        noise=(TextView) findViewById(R.id.noiseSensorTx);

        //Inicializaçao e registro de Sensores
        initAndRegisterSensors();

        //Desabilitei a rotina de envio de dados para o servidor porque o serviço ainda não está disponívelna web.
        //synchronizedDataToServer();
    }

    private void initAndRegisterSensors() {
        //Obtem serviço de Sensores
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);

        //Os Sensores de luminosidade, temperatura e umidade estao disponiveis via API.
        //Neste momento ocorre a obtençao de instancia de serviço.
        lightSensor = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        temperatureSensor = sm.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        humiditySensor = sm.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        //A obtençao de dados do GPS e Ruido ocorrem de forma diferente, pois fazem parte de outra API.
        //Os metodos abaixo obtem os dados de GPS e ruido respectivamente.
        callConnectionGPS();
        startNoiseRecorder();

        //Registro de eventos ocorridos nos sensores
        sm.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);

        //Verifica se existe sensor de Temperatura. Se nao houver, o valor retornado sera nulo.
        //A Classe ConsultaSituaçao obtem dados de temperatura e umidade de um servidor de condições climáticas.
        //if (hasConnection()) {
        //    if (temperatureSensor == null) {
        //      new Weather(this).execute();
        //  }

        //}
    }

    //Verifica se existe conexao com a internet
    public  boolean hasConnection() {
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            return true;
        } else { return false; }
    }

    //Sincroniza e obtem coordenadas a partir do GPS
    private synchronized void callConnectionGPS(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    //obtem os dados de ruido através do mic. A Classe RecorderTask é uma inner class auxiliar para obtençao do nivel de ruido.
    public void startNoiseRecorder(){

        MediaRecorder recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new RecorderTask(recorder), 0, 800);
        recorder.setOutputFile("/dev/null");

        try {
            recorder.prepare();
            recorder.start();
        } catch(IllegalStateException e){
        } catch (IOException e) {}
    }

    //Instancia do objeto que envia os dados para o servidor.
    //Desabilitado pois o servidor que receberá os dados ainda não está disponível
    /*public void sendDataToServer(){
        if(hasConnection()){
            SensoresDAO sensores = new SensoresDAO(this, this);
            sensores.execute(light.getText() + "", temperature.getText() + "", humidity.getText() + "",
                    GPS.getText() + "", noise.getText() + "", url_insert_row);
        }
        else Toast.makeText(this, "Verifique sua conexão com a Internet e reinicie o aplicativo.", Toast.LENGTH_LONG).show();
    }

    //Metodo que sincroniza os dados com servidor a cada 5 minutos.
    //Desabilitado pois o servidor que receberá os dados ainda não está disponível
    /*public void synchronizedDataToServer(){
        if(hasConnection()) {
            final int TIMEOUT = 9000;//300000;
            new Thread() {
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(TIMEOUT);
                                sendDataToServer();
                        } catch (Exception erro) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getBaseContext(), "Ocorreu um erro ao enviar dados.", Toast.LENGTH_LONG).show();}});
                        }}}}.start();
        }else { Toast.makeText(this, "Verifique sua conexão com a Internet e reinicie o aplicativo.", Toast.LENGTH_LONG).show();}
    }*/

    //Metodo de retorno após obtenção de dados do servidor de tempo
    @Override
    public void onResult(String[] result) {
        temperature.setText(result[0]);
        humidity.setText(result[1]);
    }

    //Método de retorno após envio de dados ao servidor
    //Desabilitado pois o servidor que receberá os dados ainda não está disponível
   /* @Override
    public void afterSend(String arg) {
        Toast.makeText(this, "Dados enviados com sucesso!", Toast.LENGTH_LONG).show();
    }*/

    //Inner class auxiliar para obtenção do ruido.
    private class RecorderTask extends TimerTask {

        private MediaRecorder recorder;
        private DecimalFormat dec = new DecimalFormat("00.00");

        public RecorderTask(MediaRecorder recorder) {
            this.recorder = recorder;
        }

        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int amplitude = recorder.getMaxAmplitude();
                    double amplitudeDb = 20 * Math.log10((double) Math.abs(amplitude));
                    noise.setText("" + dec.format(amplitudeDb));
                }
            });
        }
    }

    //Método auxiliar de conexão para obter dados do GPS
    @Override
    public void onConnected(Bundle bundle) {
         Location l = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(l != null){
            GPS.setText(l.getLatitude()+" | "+l.getLongitude());
        }
    }

    //Método que identifica a mudança nos sensores de luminosidade, temperatura e umidade e realiza a atualização.
    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        DecimalFormat df =  new DecimalFormat("00.00");

        switch (type){
            case Sensor.TYPE_LIGHT: light.setText("" + event.values[0]);
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE: temperature.setText(""+ df.format(event.values[0]));
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:   humidity.setText("" + df.format(event.values[0]));
                break;
        }
    }



    //Métodos obrigatórios no uso de algumas interfaces. Estão em modo default.

    @Override
    public void onConnectionSuspended(int i) {}
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //Menu Settings
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
