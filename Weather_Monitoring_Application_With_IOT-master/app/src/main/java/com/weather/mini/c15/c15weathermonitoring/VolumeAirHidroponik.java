package com.weather.mini.c15.c15weathermonitoring;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class VolumeAirHidroponik extends AppCompatActivity {

    private TextToSpeech tts;
    private ArrayList<String> questions;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static final String PREFS = "prefs";

    Button btnvoiceph;


    static ProgressBar freezeBar;
    static ProgressBar heatBar;
    static String airGraphURL;
    static WebView airGraph;
    static TextView air,indika;
    static int airValue=0;
    static String airJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volume_air_hidroponik);











        btnvoiceph=findViewById(R.id.btnvoice);

        btnvoiceph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listen();
            }
        });
        loadQuestions();
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }



                } else {
                    Log.e("TTS", "Initilization Failed!");

                }
            }
        });

        indika = (TextView)findViewById(R.id.indikator);
        freezeBar = (ProgressBar) findViewById(R.id.progressBarair);
        freezeBar.setProgress(0);

        air = (TextView)findViewById(R.id.temperatureair);

        String urlair = "https://api.thingspeak.com/channels/1147056/feeds/last.json?api_key=";
        String apikeyair = "73Y9KC3O8VSKLO2H";
        final VolumeAirHidroponik.UriApi uriapi05 = new UriApi();

        uriapi05.setUri(urlair,apikeyair);
        Timer timer = new Timer();
        TimerTask tasknew = new TimerTask(){
            public void run() {
                LoadJSONair task= new LoadJSONair();
                task.execute(uriapi05.getUri());
            }
        };
        timer.scheduleAtFixedRate(tasknew,1*2000,1*2000);

        airGraphURL = "<iframe width=\"450\" height=\"250\" style=\"border: 1px solid #cccccc;\" src=\"http://thingspeak.com/channels/1147056/charts/1?api_key=73Y9KC3O8VSKLO2H&dynamic=true\"></iframe>";
        airGraph = (WebView) findViewById(R.id.Graphair);
        airGraph.getSettings().setJavaScriptEnabled(true);
        airGraph.setInitialScale(210);
        airGraph.loadData(airGraphURL, "text/html", null);
    }
    static class UriApi {
        private String uri,urlair,apikeyair;

        protected void setUri(String url, String apikey){
            this.urlair = url;
            this.apikeyair = apikey;
            this.uri = url + apikey;
        }

        protected  String getUri(){
            return uri;
        }

    }
    private class LoadJSONair extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return (String) getText(urls[0]);
        }
        @Override
        protected void onPostExecute(String result) {


            try {
                JSONObject json = new JSONObject(result);

                airJSON = String.format("%s", json.getString("field1"));


            } catch (JSONException e)
            {
                e.printStackTrace();
            }

            air.setText(""+airJSON+" Cm");
            Log.d("VarX", ""+airJSON);






            try
            {   if(airJSON!=null) {
                airValue = Integer.parseInt(airJSON);
            }
            }
            catch(NumberFormatException nfe){}





            //Temperature Progress Bar Code --------------------------------------------------------
            if(airValue>=25){
                indika.setTextColor(Color.parseColor("#ffffff"));
                indika.setBackgroundResource(R.drawable.customstatuswarning);
                indika.setText("  Air Kurang  ");
            }if (airValue<25){
                indika.setTextColor(Color.parseColor("#ffffff"));
                indika.setBackgroundResource(R.drawable.customstatusnormal);
                indika.setText("  Air Normal  ");
            }

        }

    }

    private static String getText(String strUrl) {
        String strResult = "";
        try {
            URL url = new URL(strUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            strResult = readStream(con.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strResult;
    }

    private static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }









    private void uploadtiga(){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url("https://api.thingspeak.com/update?api_key=O35GSYIUZGTRPRKL&field1=3").build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                updateView("Error - " + e.getMessage());
            }

            @Override
            public void onResponse(Response response) {
                if (response.isSuccessful()) {
                    try {
                        updateView(response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                        updateView("Error - " + e.getMessage());
                    }
                } else {
                    updateView("Not Success - code : " + response.code());
                }
            }

            public void updateView(final String strResult) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {



                    }
                });
            }
        });

    }




    private void uploaddua(){

        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url("https://api.thingspeak.com/update?api_key=O35GSYIUZGTRPRKL&field1=2").build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                updateView("Error - " + e.getMessage());
            }

            @Override
            public void onResponse(Response response) {
                if (response.isSuccessful()) {
                    try {
                        updateView(response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                        updateView("Error - " + e.getMessage());
                    }
                } else {
                    updateView("Not Success - code : " + response.code());
                }
            }

            public void updateView(final String strResult) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {



                    }
                });
            }
        });

    }



    private void loadQuestions(){
        questions = new ArrayList<>();
        questions.clear();
        questions.add("Ada Yang Bisa Saya Bantu");

    }

    private void listen(){
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");

        try {
            startActivityForResult(i, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(VolumeAirHidroponik.this, "Your device doesn't support Speech Recognition", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private void speak(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);


        }else{
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String inSpeech = res.get(0);
                recognition(inSpeech);
            }
        }
    }

    private void recognition(String text){
        Log.e("Speech",""+text);
        String[] speech = text.split(" ");
        if(text.contains("hello")){
            speak(questions.get(0));

        }
        //



        if(text.contains("Tambahkan air")){
            final MediaPlayer mp1= MediaPlayer.create(this, R.raw.tambahair);

            uploaddua();
            mp1.start();




        }
        if(text.contains("stop air")){

            final MediaPlayer mp2= MediaPlayer.create(this, R.raw.prosestambahair);
            uploadtiga();
            mp2.start();


    }
}}
