package com.example.medell.locator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Google_Map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Double longitude, latitude;
    LocationManager locationManager;
    ArrayList<String> lat, longi;
    ArrayList<Double> lat1, longi1;
    ArrayList<Double> mlat;
    ArrayList<Double> mlongi;
    int y = 0;
    LatLng[] marker;
    String[] place;
    LatLng marker1;
    String place1;
    String id;

    private LocationReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google__map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        lat = new ArrayList<String>();
        longi = new ArrayList<String>();
        lat1 = new ArrayList<Double>();
        longi1 = new ArrayList<Double>();
        mlat = new ArrayList<Double>();
        mlongi = new ArrayList<Double>();
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        Intent intent = getIntent();
        id= intent.getStringExtra("id");
        int id_check = intent.getIntExtra("id_value", 0);
        if (id_check == 1) {
            Double longnmea = Double.parseDouble(intent.getStringExtra("longitude"));
            Double latnmea = Double.parseDouble(intent.getStringExtra("latitude"));
            y = 1;
            NmeaToLatLong(latnmea, longnmea);
        } else if (id_check == 5) {
            lat = intent.getStringArrayListExtra("latitude_list");
            longi = intent.getStringArrayListExtra("longitude_list");
            y = 2;
        } else {
            Toast.makeText(this, "NO CHANNNEL ID RECEIVED", Toast.LENGTH_SHORT).show();
        }
        for (int l = 0; l < lat.size(); l++) {
            Log.d("sahil" + l, lat.get(l));
            Log.d("sahil" + l, longi.get(l));
            lat1.add(Double.parseDouble(lat.get(l)));
            longi1.add(Double.parseDouble(longi.get(l)));
        }
        for (int l = 0; l < lat.size(); l++) {
            NmeaToLatLongList(lat1.get(l), longi1.get(l));

        }

        mReceiver = new LocationReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UpdateLocation.BROADCAST_ACTION);
        registerReceiver(mReceiver, intentFilter);
    }

    public void NmeaToLatLong(Double lat, Double ln) {
        Double a, b, c, d;
        int y, z;
        a = lat;
        b = ln;
        c = ((a / 100) % 1) / 60;
        d = ((b / 100) % 1) / 60;
        y = (int) (a / 100);
        z = (int) (b / 100);
        latitude = y + (c * 100);
        longitude = z + (d * 100);
    }

    public LatLng NmeaToLatLon(Double lat, Double ln) {

        Double a, b, c, d;
        int y, z;
        a = lat;
        b = ln;
        c = ((a / 100) % 1) / 60;
        d = ((b / 100) % 1) / 60;
        y = (int) (a / 100);
        z = (int) (b / 100);

        return new LatLng((y + (c * 100)), (z + (d * 100)));
    }


    public void NmeaToLatLongList(Double lat, Double ln) {
        Double a, b, c, d, g, h;
        int y, z;
        a = lat;
        b = ln;
        c = ((a / 100) % 1) / 60;
        d = ((b / 100) % 1) / 60;
        y = (int) (a / 100);
        z = (int) (b / 100);
        g = y + (c * 100);
        h = z + (d * 100);
        mlat.add(g);
        mlongi.add(h);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (y == 1) {

            // Add a marker in Sydney and move the camera
            marker1 = new LatLng(latitude, longitude);
            place1 = getAddress(getApplicationContext(), latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(marker1).title(place1).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(marker1));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker1, 18));
        } else if (y == 2) {
            marker = new LatLng[longi1.size()];
            ;
            place = new String[longi1.size()];
            BitmapDescriptor[] color = {
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN),
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA),
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE),
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE),
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW),
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
            };
            int xyz = 0;
            for (int abc = 0; abc < longi.size(); abc++) {
                marker[abc] = new LatLng(mlat.get(abc), mlongi.get(abc));
                place[abc] = getAddress(getApplicationContext(), mlat.get(abc), mlongi.get(abc));
                mMap.addMarker(new MarkerOptions().position(marker[abc]).title(place[abc]).icon(color[xyz]));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker[abc], 10));
                xyz++;
                if (xyz == 10) {
                    xyz = 0;
                }
            }
        }
    }

    public String getAddress(Context context, double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();

            return add;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopService(new Intent(this, UpdateLocation.class));
        Intent intent = new Intent(Google_Map.this, Channelid.class);
        startActivity(intent);
    }

    private class LocationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("oil", intent.getStringExtra("latitude"));

            double latitude = Double.parseDouble(intent.getStringExtra("latitude"));
            double longitude = Double.parseDouble(intent.getStringExtra("longitude"));
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(NmeaToLatLon(latitude, longitude)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NmeaToLatLon(latitude, longitude), 17));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, UpdateLocation.class));
    }
}
