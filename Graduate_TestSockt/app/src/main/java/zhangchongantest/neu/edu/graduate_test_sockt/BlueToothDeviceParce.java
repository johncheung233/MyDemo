package zhangchongantest.neu.edu.graduate_test_sockt;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Cheung SzeOn on 2018/12/5.
 */

public class BlueToothDeviceParce {
    private String name;
    private String address;
    private BluetoothDevice device;

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
