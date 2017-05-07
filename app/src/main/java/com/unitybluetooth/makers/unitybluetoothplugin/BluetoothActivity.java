package com.unitybluetooth.makers.unitybluetoothplugin;

import android.os.Bundle;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

public class BluetoothActivity extends UnityPlayerActivity {
    private  BluetoothService bluetoothService = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //BluetoothInit();
        //ConnectedDevice("BLUE");
    }

    public void BluetoothInit()
    {
        UnityPlayer.UnitySendMessage("BluetoothControl", "ReceiveData", "Initializing");

        if(bluetoothService == null)
        {
            bluetoothService = new BluetoothService();
            if(!bluetoothService.IsBluetoothAvailable()) {
                UnityPlayer.UnitySendMessage("BluetoothControl", "ErrorMessage", "Bluetooth Not Available");
            }
            else
            {
                UnityPlayer.UnitySendMessage("BluetoothControl", "SearchDevices", bluetoothService.SearchDevices());
            }
        }
    }

    public  int isBluetoothConnected()
    {
        if(bluetoothService.isConnected() == 1) {
            return 1;
        } else {
            return -1;
        }
    }

    public void SendData(int data) {
        bluetoothService.SendData(data);
    }

    public void ConnectedDevice(String deviceName)
    {
        bluetoothService.ConnectDevice(deviceName);
    }

    public void StopThread()
    {
        bluetoothService.stopThread = true;
    }
}
