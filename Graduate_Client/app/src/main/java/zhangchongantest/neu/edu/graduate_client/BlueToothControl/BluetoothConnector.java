package zhangchongantest.neu.edu.graduate_client.BlueToothControl;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import zhangchongantest.neu.edu.graduate_client.Config;

/**
 * Created by Cheung SzeOn on 2019/2/17.
 */

public class BluetoothConnector {
    private BluetoothSocket mSocket;
    private BluetoothBroadCastCallBack callBack;
    public BluetoothConnector(final BluetoothAdapter mAdapter, BluetoothBroadCastCallBack callBack) {
        this.callBack=callBack;
        new Thread(new Runnable() {
            @Override
            public void run() {
                BluetoothDevice bluetoothDevice = mAdapter.getRemoteDevice(BluetoothManager.getInstance().getTargetDevice().getAddress());
                try {
                    mSocket = bluetoothDevice.createRfcommSocketToServiceRecord(
                            UUID.fromString(Config.SPP_UUID));
                    Log.e(Config.TAG, "create tToServiceRecord success");
                } catch (IOException e1) {
                    e1.printStackTrace();
                    Log.e(Config.TAG, "create tToServiceRecord fail");
                }
                try {
                    mSocket.connect();
                    blueToothReadMsg();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(Config.TAG, "conncet fail");
                }
            }
        }).start();
    }

    public void blueToothWriteMsg(String msg) {
        try {
            OutputStream outputStream = mSocket.getOutputStream();
            byte[] buffer = msg.getBytes();
            outputStream.write(buffer);
            Log.e(Config.TAG, "blt send="+msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void blueToothReadMsg() {
        try {
            Log.e(Config.TAG, "BlueTooth receiveing");
            InputStream inputStream = mSocket.getInputStream();
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];
            int length = 0;
            String recev = "";
            while ((length = inputStream.read(buffer)) != -1) {
                byteOutputStream.write(buffer, 0, length);
                recev = byteOutputStream.toString("utf-8");
                Log.e(Config.TAG, "bluetooth recev = " + recev);
                if (recev.length()>15){
                    byteOutputStream.reset();
                    continue;
                }
                if (recev.contains(Config.End_char)) {
                    Message msg = handler.obtainMessage();
                    byteOutputStream.reset();
                    msg.obj=recev;
                    handler.sendMessage(msg);
//                    try {
//                        Thread.sleep(3000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    blueToothWriteMsg("1");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void socketClose() {
        try {
            mSocket.close();
            Log.e(Config.TAG, "socket close success");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(Config.TAG, "socket close fail");
        }
    }

    public boolean socketIsConnecting() {
        return mSocket.isConnected();
    }


    public void removeHandlerMsg(){
        handler.removeCallbacksAndMessages(null);
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String str = (String) msg.obj;
            String receiveMsg[] = str.split(("[" + Config.MSG_SPLIT + "]"));
            switch (receiveMsg[0]){
                case "OK":
                    callBack.bluetoothBondedCheck();
                    break;
                default:
                    callBack.bluetoothReceivceMsg(receiveMsg[0]);
            }
        }
    };
}
