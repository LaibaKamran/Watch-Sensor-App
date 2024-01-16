package com.watchsensorapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout sensorsContainer;
    private String serverIP;

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

        // Prompt the user to enter the server's IP address
        showServerIpDialog();
    }

    private void addSensorCheckbox(String sensorName, int sensorType) {
        CheckBox checkBox = new CheckBox(this);
        checkBox.setText(sensorName);
        checkBox.setTag(sensorType);
        checkBox.setChecked(true); // Set the checkbox to checked by default
        sensorsContainer.addView(checkBox);
    }


    private void showServerIpDialog() {
        // Set up the input field for the IP address
        final EditText inputIp = new EditText(this);
        inputIp.setInputType(InputType.TYPE_CLASS_TEXT);
        inputIp.setHint("Enter server IP");

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Server IP");
        builder.setView(inputIp);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                serverIP = inputIp.getText().toString().trim();
            }
        });

        builder.setCancelable(false); // Prevent dialog dismissal when clicking outside

        // Show the AlertDialog
        builder.show();
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

        // Start SensorDisplayActivity and pass the selected sensor types and server IP
        Intent intent = new Intent(this, SensorDisplayActivity.class);
        intent.putIntegerArrayListExtra("selectedSensorTypes", selectedSensorTypes);
        intent.putExtra("serverIP", serverIP);
        startActivity(intent);
    }
}
