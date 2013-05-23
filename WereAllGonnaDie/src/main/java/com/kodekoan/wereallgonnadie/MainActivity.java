package com.kodekoan.wereallgonnadie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

    private float mLastX, mLastY, mLastZ;
    private float mMinX, mMinY, mMinZ;
    private float mMaxX, mMaxY, mMaxZ;

    private boolean mInitialized;

    private SensorManager mSensorManager;

    private Sensor mAccelerometer;

    private final float NOISE = (float) 2.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_demo);

        mInitialized = false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        final Button button = (Button) findViewById(R.id.btn_preferences);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                // Perform action on click
            }
        });

        SharedPreferences p = getSharedPreferences(getPackageName(), 0);

        TextView sound_selectors = (TextView)findViewById(R.id.sound_selectors);
        sound_selectors.setText(String.format("[%s]\n[%s]", p.getString("sound_file", "NONE"), p.getString("notification", "")));
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        // Change here your image
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // can be safely ignored for this demo
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        TextView tvX= (TextView)findViewById(R.id.x_axis);
        TextView tvY= (TextView)findViewById(R.id.y_axis);
        TextView tvZ= (TextView)findViewById(R.id.z_axis);
        ImageView iv = (ImageView)findViewById(R.id.image);
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        if (!mInitialized) {
            mLastX = x;
            mMaxX = mMinX = 0.0f;
            mLastY = y;
            mMaxY = mMinY = 0.0f;
            mLastZ = z;
            mMaxZ = mMinZ = 0.0f;
            tvX.setText("0.0");
            tvY.setText("0.0");
            tvZ.setText("0.0");
            mInitialized = true;
            return;
        }

        float deltaX = Math.abs(mLastX - x);
        float deltaY = Math.abs(mLastY - y);
        float deltaZ = Math.abs(mLastZ - z);
        if (deltaX < NOISE) deltaX = (float)0.0;
        if (deltaY < NOISE) deltaY = (float)0.0;
        if (deltaZ < NOISE) deltaZ = (float)0.0;
        mLastX = x;
        if (deltaX > mMaxX) mMaxX = deltaX;
        if (deltaX < mMinX) mMinX = deltaX;

        mLastY = y;
        if (deltaY > mMaxY) mMaxY = deltaY;
        if (deltaY < mMinY) mMinY = deltaY;

        mLastZ = z;
        if (deltaZ > mMaxZ) mMaxZ = deltaZ;
        if (deltaZ < mMinZ) mMinZ = deltaZ;

        tvX.setText(Float.toString(deltaX) + "(" + Float.toString(mMinX) + "-" + Float.toString(mMaxX) + ")");
        tvY.setText(Float.toString(deltaY) + "(" + Float.toString(mMinY) + "-" + Float.toString(mMaxY) + ")");
        tvZ.setText(Float.toString(deltaZ) + "(" + Float.toString(mMinZ) + "-" + Float.toString(mMaxZ) + ")");

        iv.setVisibility(View.VISIBLE);
        if (deltaX > deltaY) {
            iv.setImageResource(R.drawable.horizontal);
        } else if (deltaY > deltaX) {
            iv.setImageResource(R.drawable.vertical);
        } else {
            iv.setVisibility(View.INVISIBLE);
        }
    }
}
