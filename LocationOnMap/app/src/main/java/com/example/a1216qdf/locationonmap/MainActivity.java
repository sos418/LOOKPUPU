package com.example.a1216qdf.locationonmap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private WebView webView;
    private Button button,button1;
    private LocationManager locationManager;
    private TextView textView,textView1;

    private SensorManager mSensorManager;

    private Sensor accelerometer; // 加速度传感器
    private Sensor magnetic; // 地磁场传感器

    private TextView azimuthAngle;

    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];

    public final static int REQUEST_CODE_GPS_PERMISSIONS = 0;
    private static final String TAG = "---MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.webView);
        button = (Button) findViewById(R.id.button);
        button1 = (Button)findViewById(R.id.button1);
        textView = (TextView)findViewById(R.id.textView);
        textView1 = (TextView)findViewById(R.id.textView1);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/Map.html");

        button.setOnClickListener(listener);
        button1.setOnClickListener(listener1);
        // 实例化传感器管理者
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // 初始化加速度传感器
        accelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // 初始化地磁场传感器
        magnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        azimuthAngle = (TextView) findViewById(R.id.azimuth_angle_value);
        calculateOrientation();
    }

    private Button.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

//            String s = LocationManager.GPS_PROVIDER;
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_CODE_GPS_PERMISSIONS);
                return;
            }
//            Location location = locationManager.getLastKnownLocation(s);
//            locationManager.requestLocationUpdates(s, 0, 0, locationListener);

            savepupu();
        }
    };

    public void savepupu(){
        String centerURL = "javascript:centerAt(" + 24.969490 + ","+121.263671 + ")";
        webView.loadUrl(centerURL);
        textView.setText("經度:121.26367\n緯度:24.969490");
    }

    private Button.OnClickListener listener1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String centerURL1 = "javascript:centerAtt(" + 24.970121 + ","+121.267176 + ")";
            webView.loadUrl(centerURL1);
            textView1.setText("經度:121.267176\n緯度:24.970121");
        }
    };

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            String centerURL = "javascript:centerAt(" + location.getLatitude() + ","+location.getLongitude() + ")";
            webView.loadUrl(centerURL);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        // 注册监听
        mSensorManager.registerListener(new MySensorEventListener(),
                accelerometer, Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(new MySensorEventListener(), magnetic,
                Sensor.TYPE_MAGNETIC_FIELD);
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        // 解除注册
        mSensorManager.unregisterListener(new MySensorEventListener());
        super.onPause();
    }

    // 计算方向
    private void calculateOrientation() {
        float[] values = new float[3];
        float[] R = new float[9];
        SensorManager.getRotationMatrix(R, null, accelerometerValues,
                magneticFieldValues);
        SensorManager.getOrientation(R, values);
        values[0] = (float) Math.toDegrees(values[0]);

        Log.i(TAG, values[0] + "");
        if (values[0] >= -5 && values[0] < 5) {
            azimuthAngle.setText("正北");
        } else if (values[0] >= 5 && values[0] < 85) {
            azimuthAngle.setText("東北");
        } else if (values[0] >= 85 && values[0] <= 95) {
            azimuthAngle.setText("正東");
        } else if (values[0] >= 95 && values[0] < 175) {
            azimuthAngle.setText("東南");
        } else if ((values[0] >= 175 && values[0] <= 180)
                || (values[0]) >= -180 && values[0] < -175) {
            azimuthAngle.setText("正南");
        } else if (values[0] >= -175 && values[0] < -95) {
            azimuthAngle.setText("西南");
        } else if (values[0] >= -95 && values[0] < -85) {
            azimuthAngle.setText("正西");
        } else if (values[0] >= -85 && values[0] < -5) {
            azimuthAngle.setText("西北");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_GPS_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    savepupu();
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "LOCATION Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    class MySensorEventListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            // TODO Auto-generated method stub
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelerometerValues = event.values;
            }
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticFieldValues = event.values;
            }
            calculateOrientation();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

    }
}
