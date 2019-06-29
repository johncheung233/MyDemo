package zhangchongantest.neu.edu.graduate_client.BlueToothControl;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Cheung SzeOn on 2019/2/16.
 */

public class BluetoothManager {
    private static BluetoothManager blueToothManager;

    private BluetoothDevice device;

    private BluetoothManager(){}

    public static synchronized BluetoothManager getInstance(){
        if (blueToothManager==null){
            blueToothManager = new BluetoothManager();
        }
        return blueToothManager;
    }

    public BluetoothDevice getTargetDevice() {
        return device;
    }

    public void setTargetDevice(BluetoothDevice device) {
        this.device = device;
    }
}
