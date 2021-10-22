package com.weather.mini.c15.c15weathermonitoring;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class Monitoringsuhu extends AppCompatActivity {
    static ProgressBar freezeBar;
    static ProgressBar heatBar;
    static TextView temperatureTV;


    static int graphUpdater=0;
    static int temperatureValue=0;



    static String tempJSON;



    static String temperatureGraphURL;

    static WebView tempGraph;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoringsuhu);
        freezeBar = (ProgressBar)findViewById(R.id.progressBar); freezeBar.setProgress(0);
        heatBar = (ProgressBar)findViewById(R.id.progressBar2); heatBar.setProgress(0);


        temperatureTV = (TextView)findViewById(R.id.temperature);


        String url = "https://api.thingspeak.com/channels/1122589/feeds/last.json?api_key=";
        String apikey = "MAJUEEJH880F0OFX";
        final Monitoringsuhu.UriApi uriapi011 = new UriApi();


        uriapi011.setUri(url,apikey);
        Timer timer = new Timer();
        TimerTask tasknew = new TimerTask(){
            public void run() {
               LoadJSONsuhu task = new LoadJSONsuhu();
                task.execute(uriapi011.getUri());
            }
        };
        timer.scheduleAtFixedRate(tasknew,1*2000,1*2000);

        // TEMPERATURE GRAPH
        temperatureGraphURL = "<iframe width=\"450\" height=\"250\" style=\"border: 1px solid #cccccc;\" src=\"http://thingspeak.com/channels/1122589/charts/1?api_key=MAJUEEJH880F0OFX&dynamic=true\"></iframe>";
        tempGraph = (WebView) findViewById(R.id.Graph);
        tempGraph.getSettings().setJavaScriptEnabled(true);
        tempGraph.setInitialScale(210);
        tempGraph.loadData(temperatureGraphURL, "text/html", null);
        // HUMIDITY GRAPH



    }

    static class UriApi {

        private String uri,url,apikey;

        protected void setUri(String url, String apikey){
            this.url = url;
            this.apikey = apikey;
            this.uri = url + apikey;
        }

        protected  String getUri(){
            return uri;
        }

    }

    private class LoadJSONsuhu extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return getText(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {


            try {
                JSONObject json = new JSONObject(result);

                tempJSON = String.format("%s", json.getString("field1"));


            } catch (JSONException e)
            {
                e.printStackTrace();
            }
            graphUpdater++;
            temperatureTV.setText(""+tempJSON+"°C");
            Log.d("VarX", ""+tempJSON);



            if(graphUpdater==5)  //to update graphs every 5 seconds
            {
                tempGraph.loadData(temperatureGraphURL, "text/html", null);  //updates temperature graph

                graphUpdater=0;
            }


            try
            {   if(tempJSON!=null) {
                temperatureValue = Integer.parseInt(tempJSON);
            }
            }
            catch(NumberFormatException nfe){}


            //Temperature Progress Bar Code --------------------------------------------------------
            if(temperatureValue==0)
            {
                heatBar.setProgress(0);

                Drawable bgDrawable = heatBar.getProgressDrawable();
                bgDrawable.setColorFilter(Color.parseColor("#FFA4EEFA"), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
            else if(temperatureValue>0)
            {

                heatBar.setProgress(temperatureValue);
                freezeBar.setProgress(0);

                Drawable bgDrawable = heatBar.getProgressDrawable();
                Drawable bgDrawable1 = freezeBar.getProgressDrawable();
                bgDrawable1.setColorFilter(Color.parseColor("#00ddff"), android.graphics.PorterDuff.Mode.MULTIPLY);

                if(temperatureValue<20)  {bgDrawable.setColorFilter(Color.parseColor("#00fab3"), android.graphics.PorterDuff.Mode.MULTIPLY); }
                if(temperatureValue>20)  {bgDrawable.setColorFilter(Color.parseColor("#0cfb4c"), android.graphics.PorterDuff.Mode.MULTIPLY);}
                if(temperatureValue>40)  {bgDrawable.setColorFilter(Color.parseColor("#ff9500"), android.graphics.PorterDuff.Mode.MULTIPLY); }
                if(temperatureValue>60)  {bgDrawable.setColorFilter(Color.parseColor("#eb4e01"), android.graphics.PorterDuff.Mode.MULTIPLY); }
                if(temperatureValue>80)  {bgDrawable.setColorFilter(Color.parseColor("#ff0400"), android.graphics.PorterDuff.Mode.MULTIPLY); }
            }
            else if(temperatureValue<0)
            {

                freezeBar.setProgress(temperatureValue);
                heatBar.setProgress(0);

                Drawable bgDrawable = freezeBar.getProgressDrawable();
                Drawable bgDrawable1 = heatBar.getProgressDrawable();
                bgDrawable1.setColorFilter(Color.parseColor("#ff9500"), android.graphics.PorterDuff.Mode.MULTIPLY);

                if(temperatureValue>-20) {bgDrawable.setColorFilter(Color.parseColor("#00ddff"), android.graphics.PorterDuff.Mode.MULTIPLY); }
                if(temperatureValue<-20) {bgDrawable.setColorFilter(Color.parseColor("#0091ca"), android.graphics.PorterDuff.Mode.MULTIPLY); }
                if(temperatureValue<-40) {bgDrawable.setColorFilter(Color.parseColor("#006dc6"), android.graphics.PorterDuff.Mode.MULTIPLY); }
                if(temperatureValue<-60) {bgDrawable.setColorFilter(Color.parseColor("#8f05ff"), android.graphics.PorterDuff.Mode.MULTIPLY); }
                if(temperatureValue<-80) {bgDrawable.setColorFilter(Color.parseColor("#7100c2"), android.graphics.PorterDuff.Mode.MULTIPLY); }
            }

            //Humidity Progress Bar Code -----------------------------------------------------------

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



}

