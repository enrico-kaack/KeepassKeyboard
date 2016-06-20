package ek.de.keepasskeyboard.connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ListAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import ek.de.keepasskeyboard.Constants;
import ek.de.keepasskeyboard.wizard.DeviceList;

/**
 * Created by Enrico on 10.06.2016.
 */
public class BluetoothModul {
    public static short NOT_CONNECTED = 0;
    public static short CONNECTING = 1;
    public static short CONNECTED = 2;
    private short connectionStatus;


    private  BluetoothAdapter blue;
    ConnectedThread connectedThread;
    ConnectThread connectThread;
    private ListAdapter discoveredDevice;

    private UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final String TAG = "KEEPASS_BLUETOOTH";




    public BluetoothModul() {
        blue = BluetoothAdapter.getDefaultAdapter();
        if (blue == null){
            //TODO: Bluetooth not supported
        }
    }

    public  boolean enable(){
        if (!blue.isEnabled()) {
            return blue.enable();
        }else{
            return false;
        }
    }

    public  boolean disable(){
        if (blue.isEnabled()){
            if (connectionStatus == CONNECTED){
                connectedThread.cancel();
            }else if (connectionStatus == CONNECTING){
                connectThread.cancel();
            }
            return blue.disable();
        }else{
            return false;
        }
    }

    public void connect(String adress){
        //Check if device was connected before
        Set<BluetoothDevice> pairedDevices = blue.getBondedDevices();
        for (BluetoothDevice singleDevice : pairedDevices) {
            if (singleDevice.getAddress().equals(adress) && connectionStatus == NOT_CONNECTED){
                connectionStatus = CONNECTING;
                establishConnection(singleDevice);

            }
        }
    }

    public void connect(Context context){
        //Show a List with found devices
        Intent discovery = new Intent(context, DeviceList.class);
        context.startActivity(discovery);

    }

    public void write(String toWrite){
        if (connectedThread != null){
            connectedThread.write(toWrite.getBytes());
            Log.d(TAG, "Written: " + toWrite);
        }
    }

    public  void manageConnectedSocket(BluetoothSocket mmSocket, BluetoothDevice mmDevice){
        connectedThread = new ConnectedThread(mmSocket);
        connectedThread.start();
    }

    private void establishConnection(BluetoothDevice device) {
        connectThread = new ConnectThread(device);
        Log.d(TAG, "Connect to: " + device.getName() + device.getAddress());
        connectThread.start();
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            blue.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                connectionStatus = CONNECTING;
                mmSocket.connect();
                Log.d(TAG, "Connection SUCCESFULL");
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                    connectionStatus = NOT_CONNECTED;
                    Log.d(TAG, "Connection FAILED");
                    connectException.printStackTrace();
                } catch (IOException closeException) {
                    closeException.printStackTrace();
                }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            manageConnectedSocket(mmSocket, mmDevice);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
                connectionStatus = NOT_CONNECTED;
            } catch (IOException e) { }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread: ");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;
            connectionStatus = CONNECTED;
            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    Log.d(TAG, "Received: " + String.valueOf(bytes));
                    // Send the obtained bytes to the UI Activity
                    // mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer)
                    //       .sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionStatus = NOT_CONNECTED;
                    // Start the service over to restart listening mode
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                Log.d(TAG, String.valueOf(buffer));
                mmOutStream.write(buffer);

            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
                connectionStatus = NOT_CONNECTED;
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }


}
