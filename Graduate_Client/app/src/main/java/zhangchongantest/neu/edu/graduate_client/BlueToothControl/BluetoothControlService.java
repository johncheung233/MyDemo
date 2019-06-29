package zhangchongantest.neu.edu.graduate_client.BlueToothControl;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import zhangchongantest.neu.edu.graduate_client.Activity.BaseActivity;
import zhangchongantest.neu.edu.graduate_client.Activity.BlueToothActivity;
import zhangchongantest.neu.edu.graduate_client.Config;
import zhangchongantest.neu.edu.graduate_client.SocketConnect.ConnectManager;

/**
 * Created by Cheung SzeOn on 2019/5/6.
 */

public class BluetoothControlService extends Service {
    private BluetoothAdapter mAdapter;
    private BluetoothSocket mSocket;
    private BluetoothLeScanner mleScanner;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothReceiveMsgBean msgBean;
    private InputStream inputStream;
    private OutputStream outputStream;
    private BluetoothStatusBean statusBean;
    private BluetoothGattCharacteristic com_characteristic;
    private TimerThread timerThread;
    public static final String BLUETOOTHCONNECT_CMD = "BluetoothConnnect_CMD";
    public static final int DISCOVER_CLASSICAL = 1;
    public static final int CONNECT_CLASSICAL = 2;
    public static final int DISCOVER_BLE = 3;
    public static final int CONNECT_BLE = 4;
    public static final int CLOSE_SOCKET = 5;
    public static final int WRITE_BLE = 6;
    private ClassicalBluetoothReceive classicalBluetoothReceive;
    private ClassicalBluetoothConnect classicalBluetoothConnect;

    public BluetoothControlService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        if (statusBean == null) {
            statusBean = new BluetoothStatusBean();
        }
        if (msgBean == null) {
            msgBean = new BluetoothReceiveMsgBean();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            switch (intent.getIntExtra(BLUETOOTHCONNECT_CMD, 0)) {
                case DISCOVER_CLASSICAL:
                    mAdapter.enable();
                    if (mAdapter.isDiscovering()) {
                        mAdapter.cancelDiscovery();
                    }
                    mAdapter.startDiscovery();
                    Log.e(Config.TAG, "Searching2.0");
                    break;
                case CONNECT_CLASSICAL:
                    if (classicalBluetoothConnect == null) {
                        classicalBluetoothConnect = new ClassicalBluetoothConnect();
                    }
                    new Thread(classicalBluetoothConnect).start();
                    break;
                case DISCOVER_BLE:
                    mAdapter.enable();
                    mleScanner = mAdapter.getBluetoothLeScanner();
                    mleScanner.startScan(scanCallBack);
                    Log.e(Config.TAG, "Searching4.0");
                    if (timerThread ==null){
                        timerThread = new TimerThread();
                    }
                    timerThread.setManualStop(false);
                    new Thread(timerThread).start();
                    break;
                case CONNECT_BLE:
                    BluetoothDevice targetDevice = BluetoothManager.getInstance().getTargetDevice();
                    mBluetoothGatt = targetDevice.connectGatt(this, false, gattCallback);
                    break;
                case CLOSE_SOCKET:
                    disConnectBluetooth();
                    break;
                case WRITE_BLE:
                    if (com_characteristic != null) {
                        Log.e(Config.TAG,"writing ble");
                        com_characteristic.setValue("1");
                        mBluetoothGatt.writeCharacteristic(com_characteristic);
                    }
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    class ClassicalBluetoothConnect implements Runnable {
        @Override
        public void run() {
            BluetoothDevice bluetoothDevice = mAdapter.getRemoteDevice(BluetoothManager.getInstance().getTargetDevice().getAddress());
            try {
                mSocket = bluetoothDevice.createRfcommSocketToServiceRecord(
                        UUID.fromString(Config.SPP_UUID));
                Log.e(Config.TAG, "ClassicalBluetooth rfcomm success");
            } catch (IOException e1) {
                e1.printStackTrace();
                Log.e(Config.TAG, "ClassicalBluetooth rfcomm fail");
            }
            try {
                mSocket.connect();
                if (classicalBluetoothReceive == null) {
                    classicalBluetoothReceive = new ClassicalBluetoothReceive();
                    new Thread(classicalBluetoothReceive).start();
                }
                statusBean.setBluetoothStatus(BluetoothStatusBean.BTstatus_CONNECTSUCCESS);
                EventBus.getDefault().post(statusBean);
                Log.e(Config.TAG, "ClassicalBluetooth connect success");
            } catch (IOException e) {
                timerThread.setManualStop(true);
                statusBean.setBluetoothStatus(BluetoothStatusBean.BTstatus_CONNECTFAIL);
                EventBus.getDefault().post(statusBean);
                e.printStackTrace();
                Log.e(Config.TAG, "ClassicalBluetooth connect fail");
            }
        }
    }

    class ClassicalBluetoothReceive implements Runnable {
        private BluetoothReceiveMsgBean msgBean;

        @Override
        public void run() {
            try {
                inputStream = mSocket.getInputStream();
                ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];
                int length;
                String recev;
                Log.e(Config.TAG, "ClassicalBlueTooth receiveing");
                while ((length = inputStream.read(buffer)) != -1) {
                    byteOutputStream.write(buffer, 0, length);
                    recev = byteOutputStream.toString("utf-8");
                    Log.e(Config.TAG, "ClassicalBlueTooth receiv = " + recev);
                    if (recev.length() > 15) {
                        byteOutputStream.reset();
                        continue;
                    }
                    if (recev.contains(Config.End_char)) {
                        msgBean.setMessage(recev);
                        EventBus.getDefault().post(msgBean);
                        byteOutputStream.reset();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private final ScanCallback scanCallBack = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice bluetoothDevice = result.getDevice();
            String name = bluetoothDevice.getName();
            String mac = bluetoothDevice.getAddress();
            Log.e(Config.TAG, "name:" + name + "****mac:" + mac);
            if (ConnectManager.getInstance().getPersonalObjectConfig().getBookingSpaceId().equals(name)) {
                mleScanner.stopScan(scanCallBack);
                BluetoothManager.getInstance().setTargetDevice(bluetoothDevice);
                statusBean.setBluetoothStatus(BluetoothStatusBean.BTstatus_SCANSUCCESS);
                EventBus.getDefault().post(statusBean);
                Log.e(Config.TAG, "bluetooth start gatt");
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            timerThread.setManualStop(true);
            statusBean.setBluetoothStatus(BluetoothStatusBean.BTstatus_SCANFAIL);
            EventBus.getDefault().post(statusBean);
        }
    };

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            timerThread.setManualStop(true);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(Config.TAG, "GATT_SUCCESS");
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    statusBean.setBluetoothStatus(BluetoothStatusBean.BTstatus_CONNECTSUCCESS);
                    EventBus.getDefault().post(statusBean);
                    mBluetoothGatt.discoverServices();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    //mMessageEvent.setCmd(-1);
                    statusBean.setBluetoothStatus(BluetoothStatusBean.BTstatus_DISCONNECTSUCCESS);
                    EventBus.getDefault().post(statusBean);
                    mBluetoothGatt.close();
                    Log.e(Config.TAG, "STATE_DISCONNECTED");
                    stopSelf();
                }
            } else if (status == BluetoothGatt.GATT_FAILURE) {
                //mMessageEvent.setCmd(-2);
                timerThread.setManualStop(true);
                statusBean.setBluetoothStatus(BluetoothStatusBean.BTstatus_CONNECTFAIL);
                EventBus.getDefault().post(statusBean);
                Log.e(Config.TAG, "GATT_FAILURE");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(Config.TAG, "discover gatt success");
                if (mBluetoothGatt.getService(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")) != null) {
                    Log.e(Config.TAG, "discover service success");
                    //EventBus.getDefault().post(mMessageEvent);
                    BluetoothGattService gattService = mBluetoothGatt.getService(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
                    com_characteristic = gattService.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
                    if (com_characteristic != null) {
                        if (mBluetoothGatt.setCharacteristicNotification(com_characteristic, true)) {
                            Log.e(Config.TAG, "characteristic Notification enable");
                        } else {
                            Log.e(Config.TAG, "characteristic Notification disable");
                        }
                    } else {
                        Log.e(Config.TAG, "write characteristic not found");
                    }
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] value = characteristic.getValue();
            String strValue = new String(value);
            Log.e(Config.TAG, "value :" + strValue);
            msgBean.setMessage(strValue);
            EventBus.getDefault().post(msgBean);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(Config.TAG,"Write SUCCESS");
                statusBean.setBluetoothStatus(BluetoothStatusBean.BTstatus_SENDSUCCESS);
                EventBus.getDefault().post(statusBean);
            }
        }
    };

    public void disConnectBluetooth() {
        if (mSocket != null && mSocket.isConnected()) {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                mSocket.close();
                Log.e(Config.TAG, "bluetoothSocket close");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            Log.e(Config.TAG, "bluetoothGatt close");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class TimerThread implements Runnable {
        Boolean isManualStop = false;
        int time = 0;

        public void run() {
            while (time < 10) {
                if (isManualStop) {
                    break;
                }
                try {
                    Thread.sleep(1000);
                    ++time;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    mleScanner.stopScan(scanCallBack);
                    isManualStop = true;
                    statusBean.setBluetoothStatus(BluetoothStatusBean.BTstatus_SCANFAIL);
                    EventBus.getDefault().post(statusBean);
                    break;
                }
                Log.e(Config.TAG,"time:"+time);
            }
            time = 0;
            if (!isManualStop) {
                mleScanner.stopScan(scanCallBack);
                statusBean.setBluetoothStatus(BluetoothStatusBean.BTstatus_SCANFAIL);
                EventBus.getDefault().post(statusBean);
            }
        }
        public void setManualStop(Boolean manualStop) {
            isManualStop = manualStop;
        }
    }
}
