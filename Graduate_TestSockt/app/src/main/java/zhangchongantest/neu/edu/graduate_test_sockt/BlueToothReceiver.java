package zhangchongantest.neu.edu.graduate_test_sockt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BlueToothReceiver extends BroadcastReceiver {
    private List<BlueToothDeviceParce> newDevice;
    private List<BlueToothDeviceParce> bondedDevice;
    private DataEntity entity;
    private BluetoothBroadCastCallBack callBack;
    private boolean new_exist;
    private boolean bonded_exit;

    public BlueToothReceiver() {
    }

    public BlueToothReceiver(BluetoothBroadCastCallBack callBack) {
        newDevice = new ArrayList();
        bondedDevice = new ArrayList();
        entity = new DataEntity();
        this.callBack = callBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            BlueToothDeviceParce deviceParce = new BlueToothDeviceParce();
            if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                if (newDevice.size() == 0) {
                    deviceParce.setAddress(device.getAddress());
                    deviceParce.setName(device.getName());
                    deviceParce.setDevice(device);
                    newDevice.add(deviceParce);
                    entity.setNewDevice(newDevice);
                } else {
                    for (int index = 0; index < newDevice.size(); index++) {
                        if (newDevice.get(index).getAddress().equals(device.getAddress())) {
                            new_exist = true;
                            break;
                        } else {
                            new_exist = false;
                        }
                    }
                    if (!new_exist) {
                        deviceParce.setAddress(device.getAddress());
                        deviceParce.setName(device.getName());
                        deviceParce.setDevice(device);
                        newDevice.add(deviceParce);
                        entity.setNewDevice(newDevice);
                    }
                }
            } else {
                if (bondedDevice.size() == 0) {
                    deviceParce.setAddress(device.getAddress());
                    deviceParce.setName(device.getName());
                    deviceParce.setDevice(device);
                    bondedDevice.add(deviceParce);
                    entity.setBondedDevice(bondedDevice);
                } else {
                    for (int index = 0; index < bondedDevice.size(); index++) {
                        if (bondedDevice.get(index).getAddress() != device.getAddress()) {
                            bonded_exit = true;
                            break;
                        } else {
                            bonded_exit = false;
                        }
                    }
                    if (!bonded_exit) {
                        deviceParce.setAddress(device.getAddress());
                        deviceParce.setName(device.getName());
                        deviceParce.setDevice(device);
                        bondedDevice.add(deviceParce);
                        entity.setBondedDevice(bondedDevice);
                    }
                }
            }
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
            if (Config.SEARCH_FINISHED == 1) {
                if (bondedDevice.size() == 0 && newDevice.size() == 0) {
                    callBack.SearchDecicefailure();
                } else {
                    DataManager.getInstance().setEntity(entity);
                    Config.SEARCH_FINISHED =0;
                    callBack.SearchDeciceSuccess();
                }
            }else {
                if (newDevice.size()!=0){
                    newDevice = new ArrayList();
                }
                if (bondedDevice.size()!=0){
                    bondedDevice = new ArrayList();
                }
            }
        }else if (Config.BOND_PREPARE_ACTION.equals(intent.getAction())){
            try {
                BluetoothDevice currentDevice = DataManager.getInstance().getCurrentDevice();
                //1.确认配对
//                ClsUtils.setPairingConfirmation(currentDevice.getClass(), currentDevice, true);
                //2.终止有序广播
                Log.i("order...", "isOrderedBroadcast:"+isOrderedBroadcast()+",isInitialStickyBroadcast:"+isInitialStickyBroadcast());
                abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框。
                //3.调用setPin方法进行配对...
                boolean ret = ClsUtils.setPin(currentDevice.getClass(), currentDevice, DataManager.getInstance().getPin());
                if (ret){
                    callBack.MatchDeviceSuccess();
                }else {
                    callBack.MatchDeviceFailure();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                callBack.MatchDeviceFailure();
                e.printStackTrace();
            }
        }
    }
}
