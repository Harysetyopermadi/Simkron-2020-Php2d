package com.weather.mini.c15.c15weathermonitoring;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Kelembaban extends AppCompatActivity {

    static ProgressBar freezeBar;
    static ProgressBar heatBar;




    static String lembabGraphURL;

    static WebView lembabGraph;

    static TextView lembab;
    static int lembabValue=0;
    static String lembabJSON;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelembaban);




        freezeBar = (ProgressBar) findViewById(R.id.progressBarlembab);
        freezeBar.setProgress(0);




        lembab = (TextView)findViewById(R.id.temperaturelembab);

        String urllembab = "https://api.thingspeak.com/channels/1122589/feeds/last.json?api_key=";
        String apikeylembab = "MAJUEEJH880F0OFX";
        final Kelembaban.UriApi uriapi02 = new UriApi();


        uriapi02.setUri(urllembab,apikeylembab);
        Timer timer = new Timer();
        TimerTask tasknew = new TimerTask(){
            public void run() {
              LoadJSONlembab task= new LoadJSONlembab();
              task.execute(uriapi02.getUri());
            }
        };
        timer.scheduleAtFixedRate(tasknew,1*2000,1*2000);

       lembabGraphURL = "<iframe width=\"450\" height=\"250\" style=\"border: 1px solid #cccccc;\" src=\"http://thingspeak.com/channels/1122589/charts/2?api_key=MAJUEEJH880F0OFX&dynamic=true\"></iframe>";
        lembabGraph = (WebView) findViewById(R.id.Graphlembab);
        lembabGraph.getSettings().setJavaScriptEnabled(true);
        lembabGraph.setInitialScale(210);
        lembabGraph.loadData(lembabGraphURL, "text/html", null);

    }




    static class UriApi {
        private String uri,urllembab,apikeylembab;

        protected void setUri(String url, String apikey){
            this.urllembab = url;
            this.apikeylembab = apikey;
            this.uri = url + apikey;
        }

        protected  String getUri(){
            return uri;
        }

    }

    private class LoadJSONlembab extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return (String) getText(urls[0]);
        }
        @Override
        protected void onPostExecute(String result) {


            try {
                JSONObject json = new JSONObject(result);

                lembabJSON = String.format("%s", json.getString("field2"));


            } catch (JSONException e)
            {
                e.printStackTrace();
            }

            lembab.setText(""+lembabJSON+" %");
            Log.d("VarX", ""+lembabJSON);






            try
            {   if(lembabJSON!=null) {
                lembabValue = Integer.parseInt(lembabJSON);
            }
            }
            catch(NumberFormatException nfe){}





            //Temperature Progress Bar Code --------------------------------------------------------

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
}}
