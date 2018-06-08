package com.example.medell.locator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Channelid extends AppCompatActivity {

    EditText channel_id;
    Button locate, trace;
    String id, latitude = "0", longitude = "0";
    SharedPreferences prefs;
    DatabaseReference mDatabase;
    Handler handler;
    ArrayList<String> lat, longi;
    ArrayList<RequestQueue> requestQueues;
    ArrayList<StringRequest> stringRequests;
    int count=0,id_length=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channelid);
        channel_id = (EditText) findViewById(R.id.channel_id);
        locate = (Button) findViewById(R.id.channel_id_locate_btn);
        trace = (Button) findViewById(R.id.channel_id_trace_btn);
        prefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        mDatabase = FirebaseDatabase.getInstance().getReference("user");
        handler = new Handler();
        lat = new ArrayList<String>();
        longi = new ArrayList<String>();
        requestQueues = new ArrayList<RequestQueue>();
        stringRequests = new ArrayList<StringRequest>();

    }

    /*
     * Tracing a single marker
     * */
    public void Trace(View view) {

        id = channel_id.getText().toString();

        startService(new Intent(this, UpdateLocation.class).putExtra("id",id));
        Toast.makeText(this, "Tracking Started", Toast.LENGTH_SHORT).show();
        if (id.contains(",")) {

            Toast.makeText(this, "Enter Single ID Only", Toast.LENGTH_SHORT).show();
        } else {
            if (!id.isEmpty()) {
                sendTraceReq(id);
            } else {
                Toast.makeText(this, "Enter Chanel Id", Toast.LENGTH_SHORT).show();
            }
        }
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
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
                Intent intent = new Intent(Channelid.this, Google_Map.class);
                if ((!latitude.equals("0")) && (!longitude.equals("0"))) {
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    intent.putExtra("id_value", 1).putExtra("id",id);
                    channel_id.setText("");
                    startActivity(intent);
                } else {
                }

            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    /*
    *  Locating Different Marker
    */
    public void Locate(View view) {
        id = channel_id.getText().toString();
            String ids[] = id.split(",");
            id_length=ids.length;
            lat.clear();
            longi.clear();
            for (int x = 0; x < ids.length; x++) {
                //id_list.add(ids[x]);
                sendLocateReq(ids[x]);
            }
     }

    private void sendLocateReq(String id) {
        String URL = "http://api.thingspeak.com/channels/" + id + "/feeds/last.json";
        StringRequest stringRequest = new StringRequest(URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                jsonParse_locate(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }

                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }

    public void jsonParse_locate(String response) {

        try {
            String s[] = response.split(",");

            for (int j = 0; j < s.length; j++) {

                String s1[] = (s[j].split(":"));

                if (s1[0].contains("field1")) {
                    String a = s1[1];
                    lat.add(a.substring(1, a.length() - 1));
                }
                if (s1[0].contains("field2")) {
                    String b = s1[1];
                    longi.add(b.substring(1, b.length() - 2));
                }

            }
            count++;
            if(count==id_length){
                callMap();
            }


        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.logoutmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout: {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("login", false);
                editor.commit();
                finish();
                Intent intent=new Intent(Channelid.this,Login.class);
                startActivity(intent);
                break;
            }

            case R.id.delete_account: {
                String currentUser = prefs.getString("currentUser", null);
                mDatabase.child(currentUser).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("login", false);
                            editor.commit();
                            finish();
                            Intent intent=new Intent(Channelid.this,Login.class);
                            startActivity(intent);
                            Toast.makeText(Channelid.this, "Account Deleted", Toast.LENGTH_SHORT).show();
                            // take action
                        }
                    }
                });
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void callMap() {
        Intent intent = new Intent(Channelid.this, Google_Map.class);

        if (lat.size() > 0) {
            if ((!lat.get(lat.size() - 1).equals("0")) && (!longi.get(longi.size() - 1).equals("0"))) {
                intent.putExtra("latitude_list", lat);
                intent.putExtra("longitude_list", longi);
                intent.putExtra("id_value", 5);
                channel_id.setText("");
                startActivity(intent);
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Channelid.this, Login.class);
        startActivity(intent);
    }

}