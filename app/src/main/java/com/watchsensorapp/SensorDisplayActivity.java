// com.watchsensorapp.SensorDisplayActivity
package com.watchsensorapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SensorDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_display);

        TextView textSelectedSensors = findViewById(R.id.textSelectedSensors);

        // Retrieve selected sensor values from the intent
        String selectedSensors = getIntent().getStringExtra("selectedSensors");

        // Display the selected sensor values
        textSelectedSensors.setText(selectedSensors);
    }
}
