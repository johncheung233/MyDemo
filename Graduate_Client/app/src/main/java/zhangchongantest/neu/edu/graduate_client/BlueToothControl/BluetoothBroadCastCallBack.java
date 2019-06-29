package zhangchongantest.neu.edu.graduate_client.BlueToothControl;

/**
 * Created by Cheung SzeOn on 2018/12/3.
 */

public interface BluetoothBroadCastCallBack {
    void searchDeviceSuccess();
    void searchDeviceFailure();
    void autoBondedSuccess();
    void autoBondedFailure();
    void bluetoothReceivceMsg(String receiveMsg);
    void bluetoothBondedCheck();
}
