package com.watchsensorapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.InputType;
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

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        for (Sensor sensor : sensorList) {
            addSensorCheckbox(sensor.getName(), sensor.getType());
        }

        showServerIpDialog();
    }

    private void addSensorCheckbox(String sensorName, int sensorType) {
        CheckBox checkBox = new CheckBox(this);
        checkBox.setText(sensorName);
        checkBox.setTag(sensorType);
        checkBox.setChecked(true);
        sensorsContainer.addView(checkBox);
    }

    private void showServerIpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Server IP Address");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                serverIP = input.getText().toString();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void showSelectedSensors(View view) {
        StringBuilder selectedSensors = new StringBuilder("Selected Sensors:\n");

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

        Intent intent = new Intent(this, SensorDisplayActivity.class);
        intent.putIntegerArrayListExtra("selectedSensorTypes", selectedSensorTypes);
        intent.putExtra("serverIP", serverIP);
        startActivity(intent);
    }
}
