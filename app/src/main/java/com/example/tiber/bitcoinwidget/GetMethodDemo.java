package com.example.tiber.bitcoinwidget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * Created by tiber on 10/18/2017.
 */

public class GetMethodDemo extends AsyncTask<String , Void ,String> {
    private String server_response;
    private Context context;
    private double fxRON_BUY;
    private double fxRON_SELL;
    private AppWidgetManager appWidgetManager;
    private boolean internet = true;
    private ComponentName thisWidget;
    RemoteViews views;

    public GetMethodDemo(Context context) {
        this.context = context;
        this.appWidgetManager = AppWidgetManager.getInstance(context);;
        fxRON_BUY = fxRON_SELL = 0;
        views = new RemoteViews(context.getPackageName(), R.layout.activity_main);
        thisWidget = new ComponentName(context, MainActivity.class);
        setTextOnWidget("$$$");
    }

    @Override
    protected String doInBackground(String... strings) {
        URL url;
        HttpURLConnection urlConnection = null;

        try {
            url = new URL(strings[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            int responseCode = 0;
            try {
                responseCode  = urlConnection.getResponseCode();

            }catch (UnknownHostException e){
                //no internet
                internet = false;
            }
            if(internet && responseCode == HttpURLConnection.HTTP_OK){
                server_response = readStream(urlConnection.getInputStream());
                Log.v("CatalogClient", server_response);
                try {
                    //FX BUY
                    String before = "<b> C: </b> ";
                    String after = "<br> <b> V: <";
                    int indexBefore = server_response.indexOf(before) + before.length();
                    int indexAfter = server_response.indexOf(after);
                    String deInteres = server_response.substring(indexBefore,indexAfter);
                    fxRON_BUY = Double.parseDouble(deInteres);

                    //FX SELL
                    before = "<br> <b> V: </b> ";
                    indexBefore = server_response.indexOf(before) + before.length();
                    indexAfter = indexBefore + deInteres.trim().length();
                    deInteres = server_response.substring(indexBefore,indexAfter);
                    fxRON_SELL = Double.parseDouble(deInteres);
                }
                catch (Exception e){
                    //au schimbat ceva la site...
                    fxRON_BUY = -1;
                    fxRON_SELL = -1;
                }


                Log.v("Valoare in RON ", String.valueOf(fxRON_BUY));
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        String text;
        if(internet) {
            if (fxRON_BUY == -1) {
                text = "error...shet";
            } else {
               //good
                double currentWorthRon = Math.round(MainActivity.BTC_owned * fxRON_SELL * 100.0) / 100.0;
                double netValueRon = Math.round((currentWorthRon - MainActivity.investedValueInRON) * 100.0) / 100.0;
                text = "BTC(buy): " + fxRON_BUY + "(RON)";
                text += "\n";
                text += "BTC(sell): " + fxRON_SELL + "(RON)";
                text += "\n";
                text += "~worth: " + currentWorthRon + " (RON)";
                text += "\n";
                text += "~net: " + netValueRon + " (RON)";
            }
        }
        else
        {
            text = "no internet";
        }

        setTextOnWidget(text);
    }

    private void setTextOnWidget(String text){
        views.setTextViewText(R.id.textBitcoinValue, text);
        appWidgetManager.updateAppWidget(thisWidget, views);
    }

    // Converting InputStream to String

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
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
        return response.toString();
    }
}
