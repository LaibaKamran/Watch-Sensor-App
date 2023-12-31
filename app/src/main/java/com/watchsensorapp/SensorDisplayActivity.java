// com.watchsensorapp.SensorDisplayActivity
package com.watchsensorapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensorDisplayActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private TextView textSensorData;
    private Map<Integer, String> sensorTypeMap = new HashMap<>();
    private List<Sensor> selectedSensors = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_display);

        textSensorData = findViewById(R.id.textSensorData);

        // Get the sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Build a map of sensor types to sensor names
        List<Sensor> allSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : allSensors) {
            sensorTypeMap.put(sensor.getType(), sensor.getName());
        }

        // Retrieve the list of selected sensors from the intent
        ArrayList<Integer> selectedSensorTypes = getIntent().getIntegerArrayListExtra("selectedSensorTypes");

        if (selectedSensorTypes != null) {
            // Register selected sensors for updates
            for (int sensorType : selectedSensorTypes) {
                registerSensor(sensorType);
            }
        } else {
            textSensorData.setText("No selected sensors");
        }
    }

    private void registerSensor(int sensorType) {
        Sensor sensor = sensorManager.getDefaultSensor(sensorType);
        if (sensor != null) {
            selectedSensors.add(sensor);
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            textSensorData.append("\n\nNo " + getSensorName(sensorType) + " sensor available");
        }
    }

    private String getSensorName(int sensorType) {
        return sensorTypeMap.get(sensorType);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Update UI with real-time sensor data dynamically for all selected sensors
        StringBuilder sensorData = new StringBuilder();

        for (Sensor sensor : selectedSensors) {
            if (event.sensor.getType() == sensor.getType()) {
                float[] values = event.values;

                sensorData.append("\n\n").append(getSensorName(sensor.getType())).append(" Data:\n");
                sensorData.append("X: ").append(values[0]).append("\n");
                sensorData.append("Y: ").append(values[1]).append("\n");
                sensorData.append("Z: ").append(values[2]).append("\n");
            }
        }

        // Display the sensor data
        textSensorData.setText(sensorData.toString());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes if needed
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume sensor updates when the activity is resumed
        for (Sensor sensor : selectedSensors) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister sensor updates when the activity is paused
        sensorManager.unregisterListener(this);
    }
}
