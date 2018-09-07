package com.example.huiyicao.music_pedometer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener,SensorEventListener,StepListener {

    private static double EARTH_RADIUS = 6378.137;

    private GoogleMap mMap;
    private Polyline line;
    private Marker marker;
    private double latitude;
    private double longitude;

    private double latitude_1;
    private double longitude_1;

    private double latitude_2;
    private double longitude_2;

    private int count = 0;
    private int TIME=5000;//5s
    private int lastStep=0;
    public static boolean isRun=false;
    public static int threshold=10;//need to change
    private int runIndex=1;
    private int walkIndex=1;
    //Csv part begin here
    private int walkStep=0;
    private int runStep=0;
    private double walkDist=0;
    private double runDist=0;
    private double walkSpeed = 0;
    private double runSpeed = 0;
    private double dist=0;//total distance
    private double walkDuration = 0;
    private double runDuration = 0;
    //Csv part ends

    //private Handler handler=new Handler();
    private TextView textView;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps;
    private boolean StartFlag = false;
    private boolean StopFlag = false;
    private boolean isPause=false;

    //button
    Button BtnStart;
    Button BtnStop;
    ImageButton BtnMusic;
    Button BtnDownload;
    Button BtnOpen;
    ImageButton BtnMusicStop;
    ImageButton BtnMusicPause;

    //textview
    TextView TvSteps;
    TextView WalkDist;
    TextView RunDist;
    TextView WalkTime;
    TextView RunTime;
    TextView WalkSpeed;
    TextView RunSpeed;
    TextView TotalDist;

    //ImageView
    ImageView Album;

    MediaPlayer player;
    private int TIME1=1000;//1s
    private boolean laststat=false;
    private Handler handler=new Handler();

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Get an instance of the SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        TvSteps = (TextView) findViewById(R.id.textView);
        WalkDist = (TextView) findViewById(R.id.textView2);
        WalkTime = (TextView) findViewById(R.id.textView3);
        WalkSpeed = (TextView) findViewById(R.id.textView4);
        RunDist = (TextView) findViewById(R.id.textView5);
        RunTime = (TextView)findViewById(R.id.textView6);
        RunSpeed = (TextView) findViewById(R.id.textView7);
        TotalDist = (TextView) findViewById(R.id.textView8);

        BtnStart = (Button) findViewById(R.id.btn_start);
        BtnStop = (Button) findViewById(R.id.btn_stop);
        BtnMusic = (ImageButton)findViewById(R.id.music) ;
        BtnDownload = (Button)findViewById(R.id.download) ;
//        BtnOpen = (Button)findViewById(R.id.open);
        BtnMusicStop = (ImageButton) findViewById(R.id.musicstop);
        BtnMusicPause = (ImageButton)findViewById(R.id.musicpause);

        Album = (ImageView)findViewById(R.id.album);
        //music start button
        BtnMusic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                player  =   new MediaPlayer();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                //String  path   =  "Music/test.mp3";
                String  walkMusic   =  "/walk1.mp3";
                String  runMusic  =  "/run1.mp3";//change
                handler.postDelayed(runnable2, TIME1);
                if(!MapsActivity.isRun){
                    try {
                        player.setDataSource(Environment.getExternalStorageDirectory().getPath() + walkMusic);
                        player.prepareAsync();
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {

                        player.setDataSource(Environment.getExternalStorageDirectory().getPath() + runMusic);
                        player.prepareAsync();
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                player.start();
                Album.setImageDrawable(Drawable.createFromPath(Environment.getExternalStorageDirectory().getPath() + "/" + "walk"+walkIndex+".png"));
                Animation rotateAnimation  = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setDuration(10000);
                rotateAnimation.setRepeatCount(Animation.INFINITE);
                Album.setAnimation(rotateAnimation);
                Album.startAnimation(rotateAnimation);
            }
        });

        //start button
        BtnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                numSteps = 0;
                sensorManager.registerListener(MapsActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
                handler.postDelayed(runnable, TIME);
                StartFlag = true;
                StopFlag = false;
                TvSteps.setText("");
                WalkDist.setText("");
                WalkTime.setText("");
                WalkSpeed.setText("");
                RunDist.setText("");
                RunTime.setText("");
                RunSpeed.setText("");
                TotalDist.setText("");
            }
        });

        //pause button
        BtnMusicPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPause){
                    player.pause();
                    Album.clearAnimation();
                    isPause=true;
                }
                else{
                    Album.setImageDrawable(Drawable.createFromPath(Environment.getExternalStorageDirectory().getPath() + "/" + "walk"+walkIndex+".png"));
                    Animation rotateAnimation  = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    rotateAnimation.setDuration(10000);
                    rotateAnimation.setRepeatCount(Animation.INFINITE);
                    Album.setAnimation(rotateAnimation);
                    Album.startAnimation(rotateAnimation);
                    player.start();
                    isPause=false;
                }
            }
        });

        //stop button
        BtnStop.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View arg0) {
                sensorManager.unregisterListener(MapsActivity.this);
                StartFlag = false;
                StopFlag = true;
                Album.clearAnimation();
                Album.setVisibility(View.INVISIBLE);
                Album.invalidate();
                TvSteps.setText("Number of Steps: " + numSteps);
                WalkDist.setText("Walk Distance: " + walkDist*1000+" m");
                WalkTime.setText("Walk Duration: "+(int)walkDuration/1000/60 +"min " +(walkDuration/1000-(int)(walkDuration/1000/60)*60) +"s");
                WalkSpeed.setText("Walk Speed: "+walkDist/walkDuration*1000000+" m/s");
                RunDist.setText("Run Distance: " + runDist*1000+" m");
                RunTime.setText("Run Duration: " +(int)runDuration/1000/60 +"min " +(runDuration/1000-(int)(runDuration/1000/60)*60) +"s");
                RunSpeed.setText("Run Speed: "+runDist/runDuration*1000000+ "m/s");
                TotalDist.setText("Total Distance： " + dist*1000+" m");
                Log.e("dist:",dist+"walk"+walkDist+"run"+runDist);
            }
        });


        //music stop button
        BtnMusicStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.reset();
                player.stop();
                Album.clearAnimation();
                Album.setVisibility(View.INVISIBLE);
                Album.invalidate();
            }
        });
        //download button
        BtnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                walkSpeed=walkDist/walkDuration;
                runSpeed=runDist/runDuration;
                CsvFiles csvFiles=new CsvFiles(walkStep,runStep,walkDist,runDist,dist,walkDuration,runDuration,walkSpeed,runSpeed);

            }
        });

        //open button
//        BtnOpen.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String uri = android.os.Environment.getExternalStorageDirectory() + "/DailyReport/20171202-232637_report.csv";
//                openAssignFolder(uri);
//            }
//        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //location manager
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
            Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null && StartFlag == true && StopFlag == false) {
                onLocationChanged(location);
            }
            locationManager.requestLocationUpdates(bestProvider, 2000, 0, this);
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

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onLocationChanged(Location location) {
        {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);

//            locationTv.setText("Latitude:" + latitude + ", Longitude:" + longitude);

            if (marker != null) {
                marker.remove();
            }
            marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18));


            if(StartFlag==true&&StopFlag==false) {
                count++;
//                mMap.addMarker(new MarkerOptions()
//                        .position(latLng)
//                        .title("Team 10(Huiyi Cao,Yue Kang)")
//                        .snippet("Latitude:" + latitude + ", Longitude:" + longitude));
                if(count%2 != 0) {
                    latitude_1 = location.getLatitude();
                    longitude_1 = location.getLongitude();
                }
                if(count%2 == 0 || count == 1){
                    latitude_2=location.getLatitude();
                    longitude_2=location.getLongitude();
                    double temp=GetDistance(latitude_1,longitude_1,latitude_2,longitude_2);
                    dist+=temp;
                    if(isRun){
                        runDist+=temp;
                    }else{
                        walkDist+=temp;
                    }
                    //Csv
                }
                line = mMap.addPolyline(new PolylineOptions()
                        .add(new LatLng (latitude_1, longitude_1), new LatLng (latitude_2,longitude_2))
                        .width(5)
                        .visible(true)
                        .color(Color.BLUE));
            }

        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void step(long timeNs) {
        numSteps++;
        Log.e("step test:",numSteps+"");
//        TvSteps.setText(TEXT_NUM_STEPS + numSteps);
    }

//    private double calDistance(double la_1,double lo_1,double la_2,double lo_2){
//        double dist=0;
//        dist=Math.sqrt((la_1-la_2)*(la_1-la_2)+(lo_1-lo_2)*(lo_1-lo_2));
//        return dist;
//    }


    private static double rad(double d)
    {
        return d * Math.PI / 180.0;
    }

    /**
     * 根据两点间经纬度坐标（double值），计算两点间距离，单位为米
     */
    private double GetDistance(double lat1, double lng1, double lat2, double lng2)
    {
//        double dist=0;
        Log.e("latilongti",lat1+"aaa"+lng1+"lati2long"+lat2+"bbb"+lng2);
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        Log.e("rad",radLat1+"aaa"+radLat2+"a is "+a+"b is "+b);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +
                Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));

        s = s * EARTH_RADIUS;

//        s = Math.round(s * 10000) / 10000;
        Log.e("s is ",s+"");
        return  s;
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                handler.postDelayed(this, TIME);
                if(numSteps-lastStep>=threshold){
                    runStep+=numSteps-lastStep;
                    isRun=true;
                }
                else{
                    isRun=false;
                    walkStep+=numSteps-lastStep;
                }
                lastStep=numSteps;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, TIME1);
            if (laststat != MapsActivity.isRun && MapsActivity.isRun == false) {//run->walk
                try {
                    Random random2 = new Random();
                    walkIndex = random2.nextInt(3)+1;
                    String walkMusic = "/walk"+walkIndex+".mp3";
                    Album.setImageDrawable(Drawable.createFromPath(Environment.getExternalStorageDirectory().getPath() + "/" + "walk"+walkIndex+".png"));
                    Animation rotateAnimation  = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    rotateAnimation.setDuration(10000);
                    rotateAnimation.setRepeatCount(Animation.INFINITE);
                    Album.setAnimation(rotateAnimation);
                    Album.startAnimation(rotateAnimation);
                    if(StopFlag!=true){
                        walkDuration+=TIME1;
                    }
                    player.reset();
                    player.setDataSource(Environment.getExternalStorageDirectory().getPath() + walkMusic);
                    Log.e("runnable2:",Environment.getExternalStorageDirectory().getPath() + walkMusic);
                    player.prepareAsync();

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (laststat != MapsActivity.isRun && MapsActivity.isRun == true) {//run
                try {
                    Random random1 = new Random();
                    runIndex = random1.nextInt(3)+1;
                    String runMusic = "/run"+runIndex+".mp3";//change
                    Album.setImageDrawable(Drawable.createFromPath(Environment.getExternalStorageDirectory().getPath() + "/" + "run"+runIndex+".png"));
                    Animation rotateAnimation  = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    rotateAnimation.setDuration(10000);
                    rotateAnimation.setRepeatCount(Animation.INFINITE);
                    Album.setAnimation(rotateAnimation);
                    Album.startAnimation(rotateAnimation);
                    if(StopFlag!=true){
                        runDuration+=TIME1;
                    }
                    player .reset();player.setDataSource(Environment.getExternalStorageDirectory().getPath() + runMusic);
                    Log.e("runnable2:",Environment.getExternalStorageDirectory().getPath() + runMusic);
                    player.prepareAsync();

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if(laststat == MapsActivity.isRun && MapsActivity.isRun){//run->run
                if(StopFlag!=true){
                    runDuration+=TIME1;
                }
            }else if(laststat == MapsActivity.isRun && !MapsActivity.isRun ){//walk->walk
                if(StopFlag!=true){
                    walkDuration+=TIME1;
                }
            }
            if(!isPause)
                player.start();
            laststat=MapsActivity.isRun;
        }
    };

    private void openAssignFolder(String path){
        File file = new File(path);
        if(null==file || !file.exists()){
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("*/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri URI = FileProvider.getUriForFile(getApplicationContext(), "com.example.huiyicao.music_pedometer.fileprovider",file);
//        intent.setDataAndType(FileProvider.getUriForFile(getApplicationContext(), "com.example.huiyicao.music_pedometer.fileprovider",file);

        intent.setDataAndType(URI, " application/vnd.ms-excel");
//        intent.setDataAndType(URI, "file/*");
        Log.i("uri",URI+"");
        try {
            startActivity(intent);
            startActivity(Intent.createChooser(intent, "Open folder"));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

}
