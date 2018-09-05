package com.example.tiber.bitcoinwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MainActivity extends AppWidgetProvider {

    public static final double BTC_owned = 0.67954;
    public static final double investedValueInRON = 14500.01;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for(int i=0; i<appWidgetIds.length; i++){
            int currentWidgetId = appWidgetIds[i];
            new GetMethodDemo(context.getApplicationContext()).execute("https://trade.bitcoinromania.ro");
            Intent intentUpdate = new Intent(context, MainActivity.class);
            intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intentUpdate.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intentUpdate.putExtra("refresh",true);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0, intentUpdate,0);
            RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.activity_main);
            views.setOnClickPendingIntent(R.id.myWidget, pendingIntent);
            appWidgetManager.updateAppWidget(currentWidgetId,views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        new GetMethodDemo(context.getApplicationContext())
                .execute("https://trade.bitcoinromania.ro");
    }
}