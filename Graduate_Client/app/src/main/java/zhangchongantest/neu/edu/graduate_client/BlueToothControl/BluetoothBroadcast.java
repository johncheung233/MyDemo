package zhangchongantest.neu.edu.graduate_client.BlueToothControl;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import zhangchongantest.neu.edu.graduate_client.Config;
import zhangchongantest.neu.edu.graduate_client.SocketConnect.ConnectManager;

import static zhangchongantest.neu.edu.graduate_client.Activity.BlueToothActivity.unpairDevice;

public class BluetoothBroadcast extends BroadcastReceiver {
    private BluetoothBroadCastCallBack callBack;
    private BluetoothStatusBean statusBean;

    public BluetoothBroadcast() {
        super();
    }

    public BluetoothBroadcast(BluetoothBroadCastCallBack callBack) {
        this.callBack = callBack;
        if (statusBean==null){
            statusBean = new BluetoothStatusBean();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.e(Config.TAG,"device name:"+device.getName());
            Log.e(Config.TAG,"device address:"+device.getAddress());
            if (ConnectManager.getInstance().getPersonalObjectConfig().getBookingSpaceId().equals(device.getName())) {
                if (device.getBondState() == BluetoothDevice.BOND_BONDED){
                    unpairDevice(device);
                }
                BluetoothManager.getInstance().setTargetDevice(device);
            }
//            if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
//                if (newDevice.size() == 0) {
//                    deviceParce.setAddress(device.getAddress());
//                    deviceParce.setName(device.getName());
//                    deviceParce.setDevice(device);
//                    newDevice.add(deviceParce);
//                    entity.setNewDevice(newDevice);
//                } else {
//                    for (int index = 0; index < newDevice.size(); index++) {
//                        if (newDevice.get(index).getAddress().equals(device.getAddress())) {
//                            new_exist = true;
//                            break;
//                        } else {
//                            new_exist = false;
//                        }
//                    }
//                    if (!new_exist) {
//                        deviceParce.setAddress(device.getAddress());
//                        deviceParce.setName(device.getName());
//                        deviceParce.setDevice(device);
//                        newDevice.add(deviceParce);
//                        entity.setNewDevice(newDevice);
//                    }
//                }
//            } else {
//                if (bondedDevice.size() == 0) {
//                    deviceParce.setAddress(device.getAddress());
//                    deviceParce.setName(device.getName());
//                    deviceParce.setDevice(device);
//                    bondedDevice.add(deviceParce);
//                    entity.setBondedDevice(bondedDevice);
//                } else {
//                    for (int index = 0; index < bondedDevice.size(); index++) {
//                        if (bondedDevice.get(index).getAddress() != device.getAddress()) {
//                            bonded_exit = true;
//                            break;
//                        } else {
//                            bonded_exit = false;
//                        }
//                    }
//                    if (!bonded_exit) {
//                        deviceParce.setAddress(device.getAddress());
//                        deviceParce.setName(device.getName());
//                        deviceParce.setDevice(device);
//                        bondedDevice.add(deviceParce);
//                        entity.setBondedDevice(bondedDevice);
//                    }
//                }
//            }
        }   else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
            if (Config.SEARCHFINISH == 1) {
                Log.e(Config.TAG, "DISCOVERY_FINISHED");
                Config.SEARCHFINISH = 0;
                if (BluetoothManager.getInstance().getTargetDevice() == null) {
                    callBack.searchDeviceFailure();
                } else {
                    callBack.searchDeviceSuccess();
                }
            }
        } else if (Config.BOND_PREPARE_ACTION.equals(intent.getAction())) {
            try {
                Log.e(Config.TAG,"PREPARE_ACTION");
                BluetoothDevice currentDevice = BluetoothManager.getInstance().getTargetDevice();
                //1.确认配对
                //ClsUtils.setPairingConfirmation(currentDevice.getClass(), currentDevice, true);
                //2.终止有序广播
                Log.i(Config.TAG, "isOrderedBroadcast:" + isOrderedBroadcast() + ",isInitialStickyBroadcast:" + isInitialStickyBroadcast());
                abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框。
                //3.调用setPin方法进行配对...
                boolean ret = ClsUtils.setPin(currentDevice.getClass(), currentDevice, ConnectManager.getInstance().getPersonalObjectConfig().getBookingSpaceId());
                if (ret) {
                    callBack.autoBondedSuccess();
                } else {
                    callBack.autoBondedFailure();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                callBack.autoBondedFailure();
                e.printStackTrace();
            }
        }
    }
}
