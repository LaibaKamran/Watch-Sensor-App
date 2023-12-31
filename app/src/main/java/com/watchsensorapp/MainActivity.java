package com.watchsensorapp;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout sensorsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorsContainer = findViewById(R.id.sensorsContainer);

        // Get the list of available sensors
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        // Dynamically create checkboxes for each sensor
        for (Sensor sensor : sensorList) {
            addSensorCheckbox(sensor.getName(), sensor.getType());
        }
    }

    private void addSensorCheckbox(String sensorName, int sensorType) {
        CheckBox checkBox = new CheckBox(this);
        checkBox.setText(sensorName);
        checkBox.setTag(sensorType);
        sensorsContainer.addView(checkBox);
    }

    public void showSelectedSensors(View view) {
        StringBuilder selectedSensors = new StringBuilder("Selected Sensors:\n");

        // Iterate through checkboxes to find selected sensors
        ArrayList<Integer> selectedSensorTypes = new ArrayList<>();
        for (int i = 0; i < sensorsContainer.getChildCount(); i++) {
            View childView = sensorsContainer.getChildAt(i);

            if (childView instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) childView;

                if (checkBox.isChecked()) {
                    selectedSensors.append(checkBox.getText()).append("\n");
                    selectedSensorTypes.add((Integer) checkBox.getTag());
                }
            }
        }

        // Start SensorDisplayActivity and pass the selected sensor types
        Intent intent = new Intent(this, SensorDisplayActivity.class);
        intent.putIntegerArrayListExtra("selectedSensorTypes", selectedSensorTypes);
        startActivity(intent);
    }
}
