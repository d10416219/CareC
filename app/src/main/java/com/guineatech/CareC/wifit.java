package com.guineatech.CareC;



import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;


import android.util.Log;
import android.net.Uri;

import android.net.wifi.WifiManager;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

import android.widget.Toast;


import static android.Manifest.permission.ACCESS_COARSE_LOCATION;



public class wifit extends AppCompatActivity {

    private Context context = this;
    private WifiManager wifiManager;
    private Spinner spinnerWifis;
    private String bcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifit);
        Intent it=this.getIntent();
         bcode=it.getStringExtra("bcode");
        spinnerWifis = (Spinner) findViewById(R.id.spinner);

        if (ActivityCompat.checkSelfPermission(wifit.this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(wifit.this,new String[]{ACCESS_COARSE_LOCATION},1);
        }

        openGPS(context);
        IsEnable();
        scan();
    }

    private void IsEnable(){
        //首先取得Wi-Fi服務控制Manager
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if(wifiManager.isWifiEnabled()){

        }else {

            //wifiManager.setWifiEnabled(true);
            wifiManager.setWifiEnabled(true);
            Toast.makeText(wifit.this, "Wi-Fi開啟中", Toast.LENGTH_SHORT).show();
        }


    }

    private void scan() {//搜尋WIFI
        // Register the Receiver in some part os fragment...
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                wifiScanReceive();
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wifiManager.startScan();

        // Inside the receiver:
    }


    private void wifiScanReceive(){
        // the result.size() is 0 after update to Android v6.0, same code working in older devices.
        List<ScanResult> scanResultList =  wifiManager.getScanResults();

        int size = scanResultList.size();
        final List<String> dataList = new ArrayList<String>(size);
        Toast.makeText(context,"scan result :" + size,Toast.LENGTH_SHORT).show();

         for(int i = 0 ; i <size  ; i++ )
        {
            //手機目前周圍的Wi-Fi環境
            String SSID  = scanResultList.get(i).SSID ;
            int LEVEL = scanResultList.get(i).level;
            String item = String.format("%s-%d",SSID,LEVEL);
            Log.d("wifi",item);
            dataList.add(item);
        }


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayAdapter adapter = new ArrayAdapter(context,android.R.layout.simple_spinner_item,dataList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerWifis.setAdapter(adapter);
            }
        });
    }
    public static final void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }


}