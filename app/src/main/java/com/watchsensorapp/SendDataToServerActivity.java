package com.watchsensorapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class SendDataToServerActivity extends AppCompatActivity {

    private String serverIP;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_data_to_server);

        serverIP = getIntent().getStringExtra("serverIP");
        userId = getIntent().getStringExtra("userID");

        sendDataToServer();
        finish(); // Finish the activity after sending data
    }

    private void sendDataToServer() {
        // Allow network operations on the main thread (for demonstration purposes only, not recommended for production)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Construct the message to send
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("User ID: ").append(userId).append("\n");
        messageBuilder.append("Selected Sensors:\n");

        ArrayList<Integer> selectedSensorTypes = getIntent().getIntegerArrayListExtra("selectedSensorTypes");
        if (selectedSensorTypes != null) {
            for (int sensorType : selectedSensorTypes) {
                messageBuilder.append(sensorType).append("\n");
            }
        }

        try {
            Socket socket = new Socket(serverIP, 12345);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Send the message to the server
            writer.write(messageBuilder.toString());
            writer.newLine();
            writer.flush();

            // Close the socket
            socket.close();
            showToast("Message sent successfully");
        } catch (IOException e) {
            e.printStackTrace();
            showToast("Failed to send message: " + e.getMessage());
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
