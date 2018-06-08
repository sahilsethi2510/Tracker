package com.example.medell.locator;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateLocation extends Service {

    public static final String BROADCAST_ACTION = "location";

    private Handler handler = new Handler();
    public static final long INTERVAL = 10 * 1000; // 10 seconds
    private Timer mTimer = new Timer();
    String id, latitude = "0", longitude = "0";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        id = intent.getStringExtra("id");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mTimer.schedule(new RequestLocation(), 0, INTERVAL);
    }

    class RequestLocation extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            handler.post(new Runnable() {

                @Override
                public void run() {
                    Trace();
                }
            });
        }

        public void Trace() {
            sendTraceReq(id);
        }

        private void sendTraceReq(String id) {

            String URL = "http://api.thingspeak.com/channels/" + id + "/feeds/last.json";
            StringRequest stringRequest = new StringRequest(URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    jsonParse_trace(response);
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                        }

                    });
            RequestQueue requestQueue = Volley.newRequestQueue(UpdateLocation.this);
            requestQueue.add(stringRequest);
        }


        public void jsonParse_trace(String response) {

            try {
                latitude = "0";
                longitude = "0";
                String s[] = response.split(",");
                for (int j = 0; j < s.length; j++) {
                    String s1[] = (s[j].split(":"));

                    if (s1[0].contains("field1")) {
                        String a = s1[1];
                        latitude = a.substring(1, a.length() - 1);
                    }
                    if (s1[0].contains("field2")) {
                        String b = s1[1];
                        longitude = b.substring(1, b.length() - 2);
                    }
                    Intent intent = new Intent();
                    intent.setAction(BROADCAST_ACTION);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    sendBroadcast(intent);

                }
            } catch (Exception e) {
            }
        }

    }


}