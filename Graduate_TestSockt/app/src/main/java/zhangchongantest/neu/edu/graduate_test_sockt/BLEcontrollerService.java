package zhangchongantest.neu.edu.graduate_test_sockt;

import android.app.Service;
import android.app.usage.UsageEvents;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.IllegalFormatCodePointException;
import java.util.UUID;

/**
 * Created by Cheung SzeOn on 2019/4/16.
 */

public class BLEcontrollerService extends Service {
    public static final String CONNECT_ADDRESS = "address";
    public static final String SERVICE_COMMAND = "command";
    public static final String COMMAND_START_CONNECT = "start_connect";

    private BluetoothAdapter mAdapter;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothGatt mBluetoothGatt;

    private MessageEvent mMessageEvent;
    ByteArrayOutputStream byteOutputStream;


    private final BluetoothGattCallback mCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (mMessageEvent == null) {
                mMessageEvent = new MessageEvent();
            }
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(Config.TAG, "GATT_SUCCESS");
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    mBluetoothGatt.discoverServices();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    mMessageEvent.setCmd(-1);
                    Log.e(Config.TAG, "STATE_DISCONNECTED");
                }
            } else if (status == BluetoothGatt.GATT_FAILURE) {
                mMessageEvent.setCmd(-2);
                Log.e(Config.TAG, "GATT_FAILURE");
            }
            EventBus.getDefault().post(mMessageEvent);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(Config.TAG, "discover gatt success");
                if (mBluetoothGatt.getService(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")) != null) {
                    Log.e(Config.TAG, "discover service success");
                    EventBus.getDefault().post(mMessageEvent);
                    BluetoothGattService gattService = mBluetoothGatt.getService(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
                    BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
                    if (characteristic!=null){
                        Log.e(Config.TAG, "write characteristic found");
                        characteristic.setValue("1");
                        mBluetoothGatt.writeCharacteristic(characteristic);
                        if (mBluetoothGatt.setCharacteristicNotification(characteristic,true)){
                            Log.e(Config.TAG,"characteristic Notification enable");
                        }else {
                            Log.e(Config.TAG,"characteristic Notification disable");
                        }

                        //new Thread(new ReadCharacteristic(mBluetoothGatt,characteristic)).start();
                    }else {
                        Log.e(Config.TAG, "write characteristic not found");
                    }
                }
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS){
                Log.e(Config.TAG, "write characteristic success");
                if (mMessageEvent == null) {
                    mMessageEvent = new MessageEvent();
                }
                mMessageEvent.setCmd(2);
                EventBus.getDefault().post(mMessageEvent);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] value = characteristic.getValue();
            Log.e(Config.TAG, "value :" + new String(value));
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            if (status == BluetoothGatt.GATT_SUCCESS){
                Log.e(Config.TAG,"rssi:"+rssi);
            }
        }
    };

    class ReadCharacteristic implements Runnable{
        private BluetoothGattCharacteristic characteristic;
        private BluetoothGatt gatt;
        public ReadCharacteristic(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic) {
            this.characteristic = characteristic;
            this.gatt = gatt;
        }

        @Override
        public void run() {
            while (true){
                gatt.readCharacteristic(characteristic);
                Log.e(Config.TAG,"read");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            switch (intent.getStringExtra(SERVICE_COMMAND)) {
                case COMMAND_START_CONNECT:
                    String address = intent.getStringExtra(CONNECT_ADDRESS);
                    mBluetoothDevice = mAdapter.getRemoteDevice(address);
                    mBluetoothGatt = mBluetoothDevice.connectGatt(this, false, mCallback);
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
