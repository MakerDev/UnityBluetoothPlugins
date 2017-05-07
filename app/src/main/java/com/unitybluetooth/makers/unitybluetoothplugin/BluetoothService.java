package com.unitybluetooth.makers.unitybluetoothplugin;

/**
 * Created by ShinYujin on 2017-04-02.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import com.unity3d.player.UnityPlayer;

public class BluetoothService {
    private BluetoothAdapter bluetoothAdapter;
    private int isConnected = -1;

    OutputStream outputStream;
    InputStream inputStream;
    BluetoothDevice selectDevice;

    public BluetoothService() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean IsBluetoothAvailable() {
        if(bluetoothAdapter==null)
            return false;
        else
            return true;
    }

    Set<BluetoothDevice> pairedDevices;
    public  String SearchDevices() {
        String connectInfo = "";

        if(bluetoothAdapter.isEnabled())
        {
            bluetoothAdapter.startDiscovery();

            pairedDevices = bluetoothAdapter.getBondedDevices();

            if(pairedDevices.size() >0) {
                for(BluetoothDevice device : pairedDevices) {
                    connectInfo += device.getName().toString() + "/";
                    connectInfo += device.getAddress().toString() + ",";
                }
            }
        }
        else
        {
            connectInfo += "No Connect has made";
        }

        return connectInfo;
    }

    public void ConnectDevice(String deviceName) {
        if(pairedDevices.size() >0) {

            UnityPlayer.UnitySendMessage("BluetoothControl", "ReceiveData", "Connecting...");

            for(BluetoothDevice device : pairedDevices) {
                if(device.getName().equals(deviceName)) {
                    selectDevice = device;
                }
            }
        }

        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try
        {
            BluetoothSocket mSocket = selectDevice.createRfcommSocketToServiceRecord(uuid);
            mSocket.connect();

            outputStream = mSocket.getOutputStream();
            inputStream = mSocket.getInputStream();
            isConnected = 1;
            UnityPlayer.UnitySendMessage("BluetoothControl", "ReceiveData", "Success");
            ReceiveData();
        } catch (IOException e) {
            UnityPlayer.UnitySendMessage("BluetoothControl", "ErrorMessage", "Connection Error");
            e.printStackTrace();
        }
    }

    Thread thread;
    boolean stopThread = false;

    void ReceiveData() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted() && !stopThread) {
                    try
                    {
                        int receivedBytes = inputStream.available();
                        if(receivedBytes > 0) {
                            byte[] packetBytes = new byte[receivedBytes];
                            inputStream.read(packetBytes);
                            final String data = new String(packetBytes, 0, receivedBytes);
                            UnityPlayer.UnitySendMessage("BluetoothControl", "ReceiveData", data);
                        }

                    }
                    catch (IOException e )
                    {
                        stopThread = true;
                    }
                }
            }
        });
        thread.start();
    }

    public void SendData(int data) {
        try{
            String msg = "aa" + "\n";
            outputStream.write(msg.getBytes());
        }catch(Exception e) {
            UnityPlayer.UnitySendMessage("BluetoothControl", "ErrorMessage", "Data Sending Error");
        }
    }

    public int isConnected()
    {
        return isConnected;
    }
}
