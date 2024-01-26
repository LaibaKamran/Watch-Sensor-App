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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensorDisplayActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private LinearLayout sensorsContainer;
    private Map<Integer, TextView> sensorTextViewMap = new HashMap<>();
    private Map<Integer, List<Float>> sensorDataMap = new HashMap<>();

    // ...

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

// ...

    private void createSensorLayout(String sensorName, int sensorType) {
        LinearLayout sensorLayout = new LinearLayout(this);
        sensorLayout.setOrientation(LinearLayout.VERTICAL);

        TextView sensorNameTextView = createSensorTextView(sensorName);
        sensorLayout.addView(sensorNameTextView);

        TextView sensorDataTextView = createSensorTextView("");
        sensorLayout.addView(sensorDataTextView);

        sensorsContainer.addView(sensorLayout);

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

        if (sensorValues.size() < 3) {
            for (int i = 0; i < 3; i++) {
                sensorValues.add(0.0f);
            }
        }

        for (int i = 0; i < Math.min(3, event.values.length); i++) {
            sensorValues.set(i, event.values[i]);
        }

        String sensorData = sensorType + "\n" +
                "X:" + sensorValues.get(0) + "\n" +
                "Y:" + sensorValues.get(1) + "\n" +
                "Z:" + sensorValues.get(2);

        TextView sensorTextView = sensorTextViewMap.get(sensorType);
        sensorTextView.setText(sensorData);

        // Send data to the server
        sendDataToServer(sensorData);
    }

    private void sendDataToServer(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Sending message to server: " + message);
                    Socket socket = new Socket("192.168.148.7", 12345);

                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                    // Send the message to the server
                    writer.write(message);
                    writer.newLine();
                    writer.flush();

                    // Close the socket
                    socket.close();
                    System.out.println("Message sent successfully");
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    System.out.println("UnknownHostException: " + e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("IOException: " + e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Exception: " + e.getMessage());
                }
            }
        }).start();
    }




    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes if needed
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        sensorManager.unregisterListener(this);
    }
}