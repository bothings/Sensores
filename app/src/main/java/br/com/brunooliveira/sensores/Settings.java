package br.com.brunooliveira.sensores;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.CheckBox;


public class Settings extends ActionBarActivity {
    private CheckBox cbLocation;
    private CheckBox cbData;
    private static final String PREFS_NAME = "Settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        cbLocation = (CheckBox) findViewById(R.id.cBLocation);
        cbData = (CheckBox) findViewById(R.id.cBData);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        cbLocation.setChecked(settings.getBoolean("location",false));
        cbData.setChecked(settings.getBoolean("data",false));
    }

    @Override
    protected void onStop(){
        super.onStop();

        //Caso o checkbox esteja marcado gravamos o usuário
        if (cbLocation.isChecked()){
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("location", true);
            //Confirma a gravação dos dados
            editor.commit();
        }else{
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("location", false);
            //Confirma a gravação dos dados
            editor.commit();
        }

        if (cbData.isChecked()){
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("data", true);
            //Confirma a gravação dos dados
            editor.commit();
        }else{
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("data", false);
            //Confirma a gravação dos dados
            editor.commit();
        }

    }


}
