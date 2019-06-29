package zhangchongantest.neu.edu.graduate_test_sockt;

import android.bluetooth.BluetoothDevice;

import java.net.Socket;

/**
 * Created by Cheung SzeOn on 2018/11/30.
 */

public class DataManager {
    private static DataManager manager;
    private DataEntity entity;
    private BluetoothDevice device;
    private String pin;
    private Socket socket;

    private DataManager(){}

    public static synchronized DataManager getInstance(){
        if (manager==null){
            manager = new DataManager();
        }
        return manager;
    }

    public DataEntity getEntity() {
        return entity;
    }

    public void setEntity(DataEntity entity) {
        this.entity = entity;
    }

    public BluetoothDevice getCurrentDevice() {
        return device;
    }

    public void setCurrentDevice(BluetoothDevice device) {
        this.device = device;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
