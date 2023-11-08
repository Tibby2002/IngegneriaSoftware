package com.example.cryptotracker.Supports;

import static android.content.Intent.getIntent;
import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC;

import static androidx.core.app.NotificationCompat.getExtras;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cryptotracker.AddAssets;
import com.example.cryptotracker.MainActivity;
import com.example.cryptotracker.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ForegroundService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String price = intent.getExtras().getString("price");
        String url = intent.getExtras().getString("url");
        String token = intent.getExtras().getString("token");
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                RequestQueue volleyQueue = Volley.newRequestQueue(getApplicationContext());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,response -> {
                    String curr_price = "";
                    try {
                        String jsonString =  response.get("data").toString();
                        JSONArray obj = new JSONArray(jsonString);
                        JSONObject obj1 = obj.getJSONObject(0);
                        curr_price = obj1.getJSONArray("prices").getJSONObject(0).getString("price");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    if(calculateDelta(Double.parseDouble(  curr_price.replaceAll(",","")),Double.parseDouble(price.replaceAll(",","")))){
                        notifyPrice(token,price);
                        scheduledExecutorService.shutdown();
                    }
                },error -> {});

                volleyQueue.add(jsonObjectRequest);
            }
        }, 0,15, TimeUnit.MINUTES);
        final String CHANNELID = "Foreground Service ID";
        NotificationChannel channel = new NotificationChannel(
                CHANNELID,
                CHANNELID,
                NotificationManager.IMPORTANCE_HIGH
        );

      getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notificationTest = new Notification.Builder(this, CHANNELID)
                .setContentText("Service is running")
                .setContentTitle("Service enabled")
                .setSmallIcon(R.drawable.ic_launcher_background);
        startForeground((int) (System.currentTimeMillis()%10000), notificationTest.build(),FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private int calculateSize(double value){
        String res = new DecimalFormat("#000.00").format(value);
        String text = Double.toString(Math.abs(value));
        return text.indexOf('.');
    }

    //usato per calcolare se il prezzo inserito per la notifica e il prezzo attuale della crypto sia abbastanza vicino da far notificare il prezzo raggiunto.
    private boolean calculateDelta(double actual, double target){
        int order_target = calculateSize(target);
        int order_actual = calculateSize(actual);
        return order_actual == order_target && Math.abs(actual-target) <= 0.25;
    }
    private void notifyPrice(String token, String price){
        String channelId = "PRICE_ALERT";
        NotificationCompat.Builder  builder = new NotificationCompat.Builder(getApplicationContext(),channelId);
        builder.setSmallIcon(R.drawable.ic_home)
                .setContentTitle("Raggiungimento target di prezzo ("+price+") per"+token)
                .setContentText(token+"ha raggiunto il prezzo richiest, Apri l'app per scoprire di più")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        //TODO: da cambiare quando avrò fatto la parte dei grafici delle diverse crypto
        Intent intents = new Intent(getApplicationContext(), MainActivity.class);

        intents.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intents,PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelId);
        if(notificationChannel == null){
            notificationChannel = new NotificationChannel(channelId,"descrizione",NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(0,builder.build());
    }
}
