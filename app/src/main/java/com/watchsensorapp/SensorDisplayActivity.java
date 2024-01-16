// com.watchsensorapp.SensorDisplayActivity
package com.watchsensorapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensorDisplayActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private LinearLayout sensorsContainer;
    private Map<Integer, TextView> sensorTextViewMap = new HashMap<>();
    private Map<Integer, List<Float>> sensorDataMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_display);

        sensorsContainer = findViewById(R.id.sensorsContainer);

        // Get the sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Retrieve the list of selected sensors from the intent
        ArrayList<Integer> selectedSensorTypes = getIntent().getIntegerArrayListExtra("selectedSensorTypes");

        if (selectedSensorTypes != null) {
            // Register selected sensors for updates and create layout for each sensor
            for (int sensorType : selectedSensorTypes) {
                Sensor sensor = sensorManager.getDefaultSensor(sensorType);
                if (sensor != null) {
                    registerSensor(sensorType);
                    createSensorLayout(sensor.getName(), sensorType);
                } else {
                    // Handle case where sensor is not available
                    setNoSensorAvailableText(sensorType);
                }
            }
        } else {
            // Handle case where no sensors are selected
            setNoSelectedSensorsText();
        }
    }

    private void createSensorLayout(String sensorName, int sensorType) {
        // Create a layout for each sensor containing its name and data
        LinearLayout sensorLayout = new LinearLayout(this);
        sensorLayout.setOrientation(LinearLayout.VERTICAL);

        // Create TextView for sensor name
        TextView sensorNameTextView = createSensorTextView(sensorName);
        sensorLayout.addView(sensorNameTextView);

        // Create TextView for sensor data
        TextView sensorDataTextView = createSensorTextView("");
        sensorLayout.addView(sensorDataTextView);

        sensorsContainer.addView(sensorLayout);

        // Update sensor maps
        sensorTextViewMap.put(sensorType, sensorDataTextView);
        sensorDataMap.put(sensorType, new ArrayList<>());
    }


    private TextView createSensorTextView(String sensorName) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setText(sensorName);
        return textView;
    }

    private void registerSensor(int sensorType) {
        Sensor sensor = sensorManager.getDefaultSensor(sensorType);
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            // Handle case where sensor is not available
            setNoSensorAvailableText(sensorType);
        }
    }

    private void setNoSelectedSensorsText() {
        TextView noSensorsText = findViewById(R.id.textSensorData);
        noSensorsText.setText("No selected sensors");
    }

    private void setNoSensorAvailableText(int sensorType) {
        TextView sensorTextView = sensorTextViewMap.get(sensorType);
        sensorTextView.setText("No sensor available");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        List<Float> sensorValues = sensorDataMap.get(sensorType);

        // Ensure that the list has at least three elements
        if (sensorValues.size() < 3) {
            // Initialize the list with zeros if not already done
            for (int i = 0; i < 3; i++) {
                sensorValues.add(0.0f);
            }
        }

        // Update the sensor values for each dimension
        for (int i = 0; i < Math.min(3, event.values.length); i++) {
            sensorValues.set(i, event.values[i]);
        }

        // Display the sensor data for all three dimensions
        String sensorData = "X: " + sensorValues.get(0) + "\n"
                + "Y: " + sensorValues.get(1) + "\n"
                + "Z: " + sensorValues.get(2);

        // Update the TextView for the sensor type
        TextView sensorTextView = sensorTextViewMap.get(sensorType);
        sensorTextView.setText(sensorData);
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes if needed
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume sensor updates when the activity is resumed
        for (int sensorType : sensorTextViewMap.keySet()) {
            Sensor sensor = sensorManager.getDefaultSensor(sensorType);
            if (sensor != null) {
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister sensor updates when the activity is paused
        sensorManager.unregisterListener(this);
    }
}
