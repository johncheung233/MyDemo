package zhangchongantest.neu.edu.graduate_client.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Method;

import zhangchongantest.neu.edu.graduate_client.BlueToothControl.BluetoothControlService;
import zhangchongantest.neu.edu.graduate_client.BlueToothControl.BluetoothManager;
import zhangchongantest.neu.edu.graduate_client.BlueToothControl.BluetoothBroadcast;
import zhangchongantest.neu.edu.graduate_client.BlueToothControl.BluetoothBroadCastCallBack;
import zhangchongantest.neu.edu.graduate_client.BlueToothControl.BluetoothReceiveMsgBean;
import zhangchongantest.neu.edu.graduate_client.BlueToothControl.BluetoothStatusBean;
import zhangchongantest.neu.edu.graduate_client.BlueToothControl.ClsUtils;
import zhangchongantest.neu.edu.graduate_client.Config;
import zhangchongantest.neu.edu.graduate_client.Dialog.WarningDialog;
import zhangchongantest.neu.edu.graduate_client.R;
import zhangchongantest.neu.edu.graduate_client.SocketConnect.ConnectManager;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class BlueToothActivity extends BaseActivity implements BluetoothBroadCastCallBack, View.OnClickListener {
    private TextView tv_parkingId, tv_distance, tv_tips;
    private Intent bluetoothRequestIntent, connectServiceIntent;
    private IntentFilter intentFilter;
    private BluetoothBroadcast mReceiver;
    private Button bt_search;
    private static Boolean bondedState = false;
    private BluetoothControlService connectService;
    private WarningDialog quitDialog;

    public static final int START_REQUESTCODE = 1;
    public static final int START_RESULTCODE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth);
        if (connectService == null) {
            connectService = new BluetoothControlService();
        }
        intiView();
        permissionCheck();
    }

    private void intiView() {
        tv_parkingId = (TextView) findViewById(R.id.textView_parkingspaceid);
        tv_tips = (TextView) findViewById(R.id.textView_tips);
        tv_distance = (TextView) findViewById(R.id.textView_distance);
        bt_search = (Button) findViewById(R.id.button_search);
        bt_search.setOnClickListener(this);
        bt_search.setEnabled(false);
        quitDialog = new WarningDialog(this);
        quitDialog.setConfirmText("退出绑定");
        quitDialog.setMessage("退出绑定车位将影响离场计费，点击任意键取消");
        quitDialog.setOnConfirmClick(new WarningDialog.OnConfirmClick() {
            @Override
            public void setOnConfirmClick() {
                if (connectServiceIntent == null) {
                    connectServiceIntent = new Intent(BlueToothActivity.this, BluetoothControlService.class);
                }
                connectServiceIntent.putExtra(BluetoothControlService.BLUETOOTHCONNECT_CMD, BluetoothControlService.CLOSE_SOCKET);
                startService(connectServiceIntent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(Config.TAG, "SpaceId=" + ConnectManager.getInstance().getPersonalObjectConfig().getBookingSpaceId());
        tv_parkingId.setText(ConnectManager.getInstance().getPersonalObjectConfig().getBookingSpaceId());
        mReceiver = new BluetoothBroadcast(this);
        intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, intentFilter);
        intentFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, intentFilter);
        intentFilter = new IntentFilter(Config.BOND_PREPARE_ACTION);
        registerReceiver(mReceiver, intentFilter);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
//        if (connector!=null&&connector.socketIsConnecting()) {
//            connector.socketClose();
//        }
        if (BluetoothManager.getInstance().getTargetDevice() != null) {
            unpairDevice(BluetoothManager.getInstance().getTargetDevice());
        }
        EventBus.getDefault().unregister(this);
    }


    private void enableBlueTooth() {
        BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mAdapter.isEnabled()) {
            mLoadingDialog.show();
            if (connectServiceIntent == null) {
                connectServiceIntent = new Intent(BlueToothActivity.this, BluetoothControlService.class);
            }
            switch (ConnectManager.getInstance().getPersonalObjectConfig().getBookingSpaceId()) {
                case "1":
                    connectServiceIntent.putExtra(BluetoothControlService.BLUETOOTHCONNECT_CMD, BluetoothControlService.DISCOVER_BLE);
                    break;
                case "2":
                default:
                    Config.SEARCHFINISH = 1;
                    connectServiceIntent.putExtra(BluetoothControlService.BLUETOOTHCONNECT_CMD, BluetoothControlService.DISCOVER_CLASSICAL);
                    break;
            }
            startService(connectServiceIntent);
        } else {
            if (bluetoothRequestIntent == null) {
                bluetoothRequestIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            }
            startActivityForResult(bluetoothRequestIntent, Config.BLUETOOTH_ENABLE_REQUEST);
        }
    }

    @Override
    public void searchDeviceSuccess() {
        Log.e(Config.TAG, "SearchDeviceSuccess");
        mLoadingDialog.setMessage("自动匹配中");
        try {
            autoBond();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        try {
//            // 调用配对的方法，此方法是异步的，系统会触发BluetoothDevice.ACTION_PAIRING_REQUEST的广播
//            // 收到此广播后，设置配对的密码
//            Thread.sleep(3000);
//            Log.e(Config.TAG,"SearchDeviceSuccess");
//            BluetoothDevice device = BluetoothManager.getInstance().getTargetDevice();
//            ClsUtils.createBond(device.getClass(), device);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void searchDeviceFailure() {
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
        mWarningDialog.setMessage("请靠近车位后再试");
        mWarningDialog.show();
    }

    @Override
    public void autoBondedSuccess() {
        Log.e(Config.TAG, "MatchDeviceSuccess");
        mWarningDialog.setMessage("连接中");
        bt_search.setEnabled(false);
        connectServiceIntent.putExtra(BluetoothControlService.BLUETOOTHCONNECT_CMD, BluetoothControlService.CONNECT_CLASSICAL);
        startService(connectServiceIntent);
    }

    @Override
    public void autoBondedFailure() {
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
        mWarningDialog.setMessage("自动匹配失败，请稍后重试");
        mWarningDialog.show();
        Log.e(Config.TAG, "MatchDeviceFailure");
    }

    @Override
    public void bluetoothReceivceMsg(String receiveMsg) {
        tv_distance.setText(receiveMsg);
    }

    @Override
    public void bluetoothBondedCheck() {

    }

    @Override
    protected void onDestroy() {
        if (connectServiceIntent == null) {
            connectServiceIntent = new Intent(BlueToothActivity.this, BluetoothControlService.class);
        }
        connectServiceIntent.putExtra(BluetoothControlService.BLUETOOTHCONNECT_CMD, BluetoothControlService.CLOSE_SOCKET);
        startService(connectServiceIntent);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_search:
                bt_search.setEnabled(false);
                enableBlueTooth();
                break;
        }
    }

    private void autoBond() {
        try {
            // 调用配对的方法，此方法是异步的，系统会触发BluetoothDevice.ACTION_PAIRING_REQUEST的广播
            // 收到此广播后，设置配对的密码
            BluetoothDevice device = BluetoothManager.getInstance().getTargetDevice();
            ClsUtils.createBond(device.getClass(), device);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Config.BLUETOOTH_ENABLE_REQUEST) {
            if (resultCode == RESULT_OK) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (connectServiceIntent == null) {
                            connectServiceIntent = new Intent(BlueToothActivity.this, BluetoothControlService.class);
                        }
                        if ("1".equals(ConnectManager.getInstance().getPersonalObjectConfig().getBookingSpaceId())) {
                            connectServiceIntent.putExtra(BluetoothControlService.BLUETOOTHCONNECT_CMD, BluetoothControlService.DISCOVER_BLE);
                        } else {
                            connectServiceIntent.putExtra(BluetoothControlService.BLUETOOTHCONNECT_CMD, BluetoothControlService.DISCOVER_CLASSICAL);
                        }
                        startService(connectServiceIntent);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } else {
                mWarningDialog.setMessage("打开蓝牙失败");
                mWarningDialog.show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (bondedState) {
//            if (mReceiver != null) {
//                unregisterReceiver(mReceiver);
//            }
//            if (connector!=null&&connector.socketIsConnecting()) {
//                connector.socketClose();
//            }
//            if (BluetoothManager.getInstance().getTargetDevice()!=null) {
//                BluetoothConnector.unpairDevice(BluetoothManager.getInstance().getTargetDevice());
//            }
//            if (connector!=null){
//                connector.removeHandlerMsg();
//            }
            if (connectServiceIntent == null) {
                connectServiceIntent = new Intent(BlueToothActivity.this, BluetoothControlService.class);
            }
            connectServiceIntent.putExtra(BluetoothControlService.BLUETOOTHCONNECT_CMD, BluetoothControlService.WRITE_BLE);
            startService(connectServiceIntent);
            return;
        } else {
            quitDialog.show();
            return;
        }
        //super.onBackPressed();
    }


//    public static Handler handler = new Handler(Looper.getMainLooper()) {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            String str = (String) msg.obj;
//            String receiveMsg[] = str.split(("[" + Config.MSG_SPLIT + "]"));
//            switch (receiveMsg[0]){
//                case "OK":
//                    bondedState = true;
//                    ConnectManager.getInstance().getPersonalObjectConfig().setBookingSpaceId(null);
//                    break;
//                default:
//                    if (tv_distance!=null) {
//                        tv_distance.setText(receiveMsg[0]);
//                    }
//            }
//        }
//    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void bluetoothReceiveMsg(BluetoothReceiveMsgBean msgBean) {
        String receiveMsg[] = msgBean.getMessage().split(("[" + Config.MSG_SPLIT + "]"));
        switch (receiveMsg[0]) {
            case "OK":
                bondedState = true;
                ConnectManager.getInstance().getPersonalObjectConfig().setBondStatus("OK");
                tv_tips.setText("已绑定车位");
                break;
            default:
                tv_distance.setText(receiveMsg[0] + "CM");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void bluetoothStatuschange(BluetoothStatusBean statusBean) {
        switch (statusBean.getBluetoothStatus()) {
            case BluetoothStatusBean.BTstatus_SCANSUCCESS:
                connectServiceIntent.putExtra(BluetoothControlService.BLUETOOTHCONNECT_CMD, BluetoothControlService.CONNECT_BLE);
                startService(connectServiceIntent);
                mLoadingDialog.setMessage("连接中");
                break;
            case BluetoothStatusBean.BTstatus_SCANFAIL:
                mLoadingDialog.dismiss();
                mWarningDialog.setMessage("搜索失败，请稍后重试");
                mWarningDialog.show();
                bt_search.setEnabled(true);
                break;
            case BluetoothStatusBean.BTstatus_CONNECTSUCCESS:
                mLoadingDialog.dismiss();
                Toast.makeText(this, "连接成功", Toast.LENGTH_LONG).show();
                break;
            case BluetoothStatusBean.BTstatus_CONNECTFAIL:
                mLoadingDialog.dismiss();
                mWarningDialog.setMessage("连接失败，请稍后重试");
                mWarningDialog.show();
                bt_search.setEnabled(true);
                break;
            case BluetoothStatusBean.BTstatus_SENDSUCCESS:
                if (connectServiceIntent == null) {
                    connectServiceIntent = new Intent(BlueToothActivity.this, BluetoothControlService.class);
                }
                connectServiceIntent.putExtra(BluetoothControlService.BLUETOOTHCONNECT_CMD, BluetoothControlService.CLOSE_SOCKET);
                startService(connectServiceIntent);
                break;
            case BluetoothStatusBean.BTstatus_DISCONNECTSUCCESS:
                setResult(START_RESULTCODE);
                finish();
                break;
        }
    }

    public static void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass()
                    .getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
            Log.e(Config.TAG, "removeBond");
        } catch (Exception e) {
            Log.e(Config.TAG, e.getMessage());
        }
    }

    private void permissionCheck() {
        if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_COARSE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION}, 1);
            }
        } else {
            bt_search.setEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            bt_search.setEnabled(true);
        }
    }
}
