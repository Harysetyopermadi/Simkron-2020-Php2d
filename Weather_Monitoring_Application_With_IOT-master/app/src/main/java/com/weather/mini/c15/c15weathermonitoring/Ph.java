package com.weather.mini.c15.c15weathermonitoring;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.*;

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
import java.text.SimpleDateFormat;
import java.util.*;

public class Ph extends AppCompatActivity {

    private TextToSpeech tts;
    private ArrayList<String> questions;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static final String PREFS = "prefs";

   Button btnvoiceph;

    static ProgressBar freezeBar;
    static ProgressBar heatBar;
    static String phGraphURL;
    static WebView phGraph;
    static TextView ph,indika1;
    static int phValue=0;
    static float phkeluar=0;
    static String phJSON;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ph);


indika1= (TextView)findViewById(R.id.indikatorph);

        freezeBar = (ProgressBar) findViewById(R.id.progressBarph);
        freezeBar.setProgress(0);

        ph = (TextView)findViewById(R.id.temperatureph);


        loatan();





        phGraphURL = "<iframe width=\"450\" height=\"250\" style=\"border: 1px solid #cccccc;\" src=\"http://thingspeak.com/channels/1161055/charts/1?api_key=QM44R0VR1B9AA0CP&dynamic=true\"></iframe>";
        phGraph = (WebView) findViewById(R.id.Graphph);
        phGraph.getSettings().setJavaScriptEnabled(true);
        phGraph.setInitialScale(210);
        phGraph.loadData(phGraphURL, "text/html", null);





        preferences = getSharedPreferences(PREFS,0);
        editor = preferences.edit();



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


    }

private void loatan(){
    String urlph = "https://api.thingspeak.com/channels/1161055/feeds/last.json?api_key=";
    String apikeyph = "QM44R0VR1B9AA0CP";
    final Ph.UriApi uriapi02 = new UriApi();
    uriapi02.setUri(urlph,apikeyph);
    Timer timer = new Timer();
    TimerTask tasknew = new TimerTask(){
        public void run() {
            LoadJSONph task= new LoadJSONph();
            task.execute(uriapi02.getUri());
        }
    };
    timer.scheduleAtFixedRate(tasknew,1*1000,1*1000);
}

    static class UriApi {
        private String uri,urlph,apikeyph;

        protected void setUri(String url, String apikey){
            this.urlph = url;
            this.apikeyph = apikey;
            this.uri = url + apikey;
        }

        protected  String getUri(){
            return uri;
        }

    }
    private class LoadJSONph extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return (String) getText(urls[0]);
        }
        @Override
        protected void onPostExecute(String result) {


            try {
                JSONObject json = new JSONObject(result);

                phJSON = String.format("%s", json.getString("field1"));


            } catch (JSONException e)
            {
                e.printStackTrace();
            }

            ph.setText(""+phJSON+"");
            Log.d("VarX", ""+phJSON);






            try
            {   if(phJSON!=null) {
                phValue = Integer.parseInt(phJSON);
            }
            }
            catch(NumberFormatException nfe){}





            //Temperature Progress Bar Code --------------------------------------------------------
            phkeluar = Float.parseFloat(phJSON);
            if(phkeluar < 6){
                indika1.setTextColor(Color.parseColor("#ffffff"));
                indika1.setBackgroundResource(R.drawable.customstatuskurang);
                indika1.setText("  PH Kurang  ");
            }if (phkeluar >= 6){
                indika1.setTextColor(Color.parseColor("#ffffff"));
                indika1.setBackgroundResource(R.drawable.customstatusnormal);
                indika1.setText("  PH Normal  ");
            }
            if (phkeluar > 7){
                indika1.setTextColor(Color.parseColor("#ffffff"));
                indika1.setBackgroundResource(R.drawable.customstatuswarning);
                indika1.setText("  PH Tinggi  ");
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


    boolean run = true;
    private void waktumati() {

        final Timer myTimer = new Timer();
        //Set the schedule function and rate
        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //Called at every 1000 milliseconds (1 second)


                if(run) {
                    uploadtiga();
                } else {

                    myTimer.cancel();
                    myTimer.purge();
                }

            }
        };myTimer.schedule(task,60000);
        //set the amount of time in milliseconds before first execution
        myTimer.cancel();
        myTimer.purge();

    }

    private void uploadtiga(){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url("https://api.thingspeak.com/update?api_key=P6J86R3SRFIO23MJ&field1=3").build();

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
        Request request = builder.url("https://api.thingspeak.com/update?api_key=P6J86R3SRFIO23MJ&field1=1").build();

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

    private void uploadnol(){


        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url("https://api.thingspeak.com/update?api_key=P6J86R3SRFIO23MJ&field1=2").build();

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
            Toast.makeText(Ph.this, "Your device doesn't support Speech Recognition", Toast.LENGTH_SHORT).show();
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

        if(text.contains("kurangi PH")){
            final MediaPlayer mp5= MediaPlayer.create(this, R.raw.phdikurangkan);
            final MediaPlayer mp6= MediaPlayer.create(this, R.raw.prosesphkurang);

            uploadnol();
            mp5.start();
            btnvoiceph.setEnabled(false);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                  uploadtiga();
                  mp6.start();
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

        if(text.contains("tambahkan PH")){
            final MediaPlayer mp3= MediaPlayer.create(this, R.raw.phditambahkan);
            final MediaPlayer mp4 = MediaPlayer.create(this, R.raw.prosesphtambah);
            uploadsatu();
            mp3.start();
            btnvoiceph.setEnabled(false);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    uploadtiga();
                    mp4.start();
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


