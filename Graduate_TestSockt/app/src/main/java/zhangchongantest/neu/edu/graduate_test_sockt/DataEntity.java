package zhangchongantest.neu.edu.graduate_test_sockt;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cheung SzeOn on 2018/11/30.
 */

public class DataEntity {
    private List<BlueToothDeviceParce> newDevice = new ArrayList();
    private List<BlueToothDeviceParce> bondedDevice = new ArrayList();

    public List<BlueToothDeviceParce> getNewDevice() {
        return newDevice;
    }

    public void setNewDevice(List<BlueToothDeviceParce> newDevice) {
        this.newDevice = newDevice;
    }

    public List<BlueToothDeviceParce> getBondedDevice() {
        return bondedDevice;
    }

    public void setBondedDevice(List<BlueToothDeviceParce> bondedDevice) {
        this.bondedDevice = bondedDevice;
    }
}
