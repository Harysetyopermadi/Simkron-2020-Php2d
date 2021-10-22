package com.weather.mini.c15.c15weathermonitoring;




import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;


import android.os.Handler;
import android.provider.MediaStore;
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


public class NutrisiTanaman extends AppCompatActivity {
    private TextToSpeech tts;
    private ArrayList<String> questions;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static final String PREFS = "prefs";

    Button btnvoiceph;

    static ProgressBar freezeBar;
    static ProgressBar heatBar;
    static String nutrisiGraphURL;
    static WebView nutrisiGraph;
    static TextView nutrisi,indika2;
    static int nutrisiValue=0;
    static float nutrisikeluar=0;
    static String nutrisiJSON;
    static TextView ambilnutrisi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrisi_tanaman);
        btnvoiceph=findViewById(R.id.btnvoice);

        btnvoiceph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listen();
            }
        });

        freezeBar = (ProgressBar) findViewById(R.id.progressBarnutrisi);
        freezeBar.setProgress(0);
        indika2=(TextView)findViewById(R.id.indikatornutrisi);
        nutrisi = (TextView)findViewById(R.id.temperaturenutrisi);
        ambilnutrisi=(TextView)findViewById(R.id.temperaturenutrisi);

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

        String urlnutrisi = "https://api.thingspeak.com/channels/1144805/feeds/last.json?api_key=";
        String apikeynutrisi = "NVR76S2NN2FES1C4";
        final NutrisiTanaman.UriApi uriapi04 = new UriApi();

        uriapi04.setUri(urlnutrisi,apikeynutrisi);
        Timer timer = new Timer();
        TimerTask tasknew = new TimerTask(){
            public void run() {
                LoadJSONnutrisi task= new LoadJSONnutrisi();
                task.execute(uriapi04.getUri());
            }
        };
        timer.scheduleAtFixedRate(tasknew,1*2000,1*2000);

        nutrisiGraphURL = "<iframe width=\"450\" height=\"250\" style=\"border: 1px solid #cccccc;\" src=\"http://thingspeak.com/channels/1144805/charts/1?api_key=NVR76S2NN2FES1C4&dynamic=true\"></iframe>";
        nutrisiGraph = (WebView) findViewById(R.id.Graphnutrisi);
        nutrisiGraph.getSettings().setJavaScriptEnabled(true);
        nutrisiGraph.setInitialScale(210);
        nutrisiGraph.loadData(nutrisiGraphURL, "text/html", null);


    }

    static class UriApi {
        private String uri,urlnutrisi,apikeynutrisi;

        protected void setUri(String url, String apikey){
            this.urlnutrisi = url;
            this.apikeynutrisi = apikey;
            this.uri = url + apikey;
        }

        protected  String getUri(){
            return uri;
        }

    }
    private class LoadJSONnutrisi extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return (String) getText(urls[0]);
        }
        @Override
        protected void onPostExecute(String result) {


            try {
                JSONObject json = new JSONObject(result);

                nutrisiJSON = String.format("%s", json.getString("field1"));


            } catch (JSONException e)
            {
                e.printStackTrace();
            }

            nutrisi.setText(""+nutrisiJSON+"");
            Log.d("VarX", ""+nutrisiJSON);






            try
            {   if(nutrisiJSON!=null) {
                nutrisiValue = Integer.parseInt(nutrisiJSON);
                nutrisikeluar = Float.valueOf(nutrisiJSON);
            }
            }
            catch(NumberFormatException nfe){}





            //Temperature Progress Bar Code --------------------------------------------------------


            if(nutrisikeluar<730){
                indika2.setTextColor(Color.parseColor("#ffffff"));
                indika2.setBackgroundResource(R.drawable.customstatuskurang);
                indika2.setText("  Nutrisi Kurang  ");
            }if (nutrisikeluar>=730){
                indika2.setTextColor(Color.parseColor("#ffffff"));
                indika2.setBackgroundResource(R.drawable.customstatusnormal);
                indika2.setText("  Nutrisi Normal  ");
            }if (nutrisikeluar>1500){
                indika2.setTextColor(Color.parseColor("#ffffff"));
                indika2.setBackgroundResource(R.drawable.customstatuswarning);
                indika2.setText("  Nutrisi Tinggi  ");
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




    private void uploadsatu(){

        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url("https://api.thingspeak.com/update?api_key=O35GSYIUZGTRPRKL&field1=1").build();

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
            Toast.makeText(NutrisiTanaman.this, "Your device doesn't support Speech Recognition", Toast.LENGTH_SHORT).show();
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



        if(text.contains("tambahkan nutrisi")){
            final MediaPlayer mp7 = MediaPlayer.create(this, R.raw.nutrisitambah);
            final MediaPlayer mp8 =  MediaPlayer.create(this, R.raw.prosestambahnutrisi);
            uploadsatu();
            mp7.start();
            btnvoiceph.setEnabled(false);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    uploadtiga();
                    mp8.start();
                }
            }, 20000);

            final Handler handler1 = new Handler();
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    btnvoiceph.setEnabled(true);
                }
            },35000);

        }
    }
}