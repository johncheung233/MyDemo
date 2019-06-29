package zhangchongantest.neu.edu.graduate_client.BlueToothControl;

/**
 * Created by Cheung SzeOn on 2019/5/7.
 */

public class BluetoothStatusBean {
    public static final int BTstatus_SCANSUCCESS = 1;
    public static final int BTstatus_SCANFAIL = 2;
    public static final int BTstatus_CONNECTSUCCESS = 3;
    public static final int BTstatus_CONNECTFAIL = 4;
    public static final int BTstatus_SENDSUCCESS = 5;
    public static final int BTstatus_DISCONNECTSUCCESS = 6;
    int bluetoothStatus;

    public int getBluetoothStatus() {
        return bluetoothStatus;
    }

    public void setBluetoothStatus(int bluetoothStatus) {
        this.bluetoothStatus = bluetoothStatus;
    }
}
