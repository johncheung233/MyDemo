package zhangchongantest.neu.edu.graduate_test_sockt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static zhangchongantest.neu.edu.graduate_test_sockt.Config.BLUETOOTH_ENABLE_REQUEST;

public class MainActivity extends AppCompatActivity implements ServiceConnection, BluetoothBroadCastCallBack, AdapterView.OnItemClickListener {
    @BindView(R.id.bonded_ListView)
    ListView ls_bonded;
    @BindView(R.id.new_ListView)
    ListView ls_new;
    @BindView(R.id.frameLayout_device)
    FrameLayout frameLayoutDevice;
    @BindView(R.id.editText_IP)
    EditText ed_ip;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.editText_PORT)
    EditText ed_port;
    @BindView(R.id.editText_MES)
    EditText ed_mes;
    @BindView(R.id.button_TCPsend)
    Button bt_tcpSend;
    @BindView(R.id.button_TCPreceive)
    Button bt_tcpRec;
    @BindView(R.id.button_UDPsend)
    Button bt_udpsend;
    @BindView(R.id.button_UDPreceive)
    Button bt_udprec;
    @BindView(R.id.edit_receive)
    EditText ed_rec;
    @BindView(R.id.button_search)
    Button bt_search;
    @BindView(R.id.currentIP_textView)
    TextView currentIPTextView;
    @BindView(R.id.textView_socketType)
    TextView tv_socketType;
    @BindView(R.id.button_searchBLE)
    Button bt_searchBLE;
    @BindView(R.id.activity_main)
    LinearLayout activityMain;
    private Intent tcpReceIntent, tcpSendintent, udpReceIntent, udpSendIntent, BLEintent;
    private IntentFilter intentFilter;
    private BlueToothReceiver mReceiver;
    private BluetoothAdapter mAdapter;
    private BluetoothSocket mSocket;
    private NewDeviceAdapter newAdapter;
    private BondedDeviceAdapter bonderAdapter;
    private AlertDialog mListDialog;
    private AlertDialog mLoadingDialog;
    private TextView tv_loading;
    private BluetoothLeScanner mleScanner;
    private BLEcontrollerService bleService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initLoadingDialog();
        DataManager.getInstance().setPin("1");
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void initLoadingDialog() {
        View view = View.inflate(MainActivity.this, R.layout.loading_dialog, null);
        mLoadingDialog = new AlertDialog.Builder(MainActivity.this).create();
        mLoadingDialog.setView(view);
        tv_loading = (TextView) view.findViewById(R.id.loading_textView);
    }

    /**
     * 更新接收数据线程
     */
    public void tcpUpdata() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(mTcpReceiveService.getStr_result())) {
                    if (!mTcpReceiveService.getStr_result().equals(ed_rec.getText().toString())) {
                        Message message = handler.obtainMessage();
                        message.what = Config.SOCKET_MSG_HANDLER;
                        handler.sendMessage(message);
                    }
                }
                handler.postDelayed(this, 1000);
            }
        }).start();
    }

    public void udpUpdata() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(mUdpReceiveService.getStr_result())) {
                    if (!mUdpReceiveService.getStr_result().equals(ed_rec.getText().toString())) {
                        Message message = handler.obtainMessage();
                        message.what = 3;
                        handler.sendMessage(message);
                    }
                }
                handler.postDelayed(this, 1000);
            }
        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e(Config.TAG, "mes=" + msg.what);
            switch (msg.what) {
                case Config.SOCKET_MSG_HANDLER:
                    ed_rec.setText(mTcpReceiveService.getStr_result().toString());
                    break;
                case 3:
                    ed_rec.setText(mUdpReceiveService.getStr_result().toString());
                    break;
                case Config.BLUETOOTH_MSG_HANDLER:
                    ed_rec.setText((String) msg.obj);
                    break;
                case Config.BLUETOOTH_CONNECT_HANDLER:
                    Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    return;
            }

        }
    };

    @OnClick({R.id.button_TCPsend, R.id.button_TCPreceive, R.id.button_UDPsend, R.id.button_UDPreceive, R.id.button_search, R.id.button_searchBLE})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_TCPsend:
                tv_socketType.setText("TCP");
                if (tcpSendintent == null) {
                    tcpSendintent = new Intent(this, TcpSendService.class);
                }
                tcpSendintent.putExtra(Config.GET_IP, ed_ip.getText().toString());
                tcpSendintent.putExtra(Config.GET_PORT, ed_port.getText().toString());
                tcpSendintent.putExtra(Config.GET_MES, ed_mes.getText().toString());
                startService(tcpSendintent);
                break;
            case R.id.button_UDPsend:
                tv_socketType.setText("UDP");
                if (udpSendIntent == null) {
                    udpSendIntent = new Intent(this, UdpSendService.class);
                }
                udpSendIntent.putExtra(Config.GET_IP, ed_ip.getText().toString());
                udpSendIntent.putExtra(Config.GET_PORT, ed_port.getText().toString());
                udpSendIntent.putExtra(Config.GET_MES, ed_mes.getText().toString());
                startService(udpSendIntent);
                break;
            case R.id.button_UDPreceive:
                if (udpReceIntent == null) {
                    udpReceIntent = new Intent(this, UdpReceiveService.class);
                }
                udpReceIntent.putExtra(Config.GET_PORT, ed_port.getText().toString());
                getApplicationContext().bindService(udpReceIntent, this, BIND_AUTO_CREATE);
                break;
            case R.id.button_TCPreceive:
                if (tcpReceIntent == null) {
                    tcpReceIntent = new Intent(this, TcpReceiveService.class);
                }
                tcpReceIntent.putExtra(Config.GET_PORT, ed_port.getText().toString());
                getApplicationContext().bindService(tcpReceIntent, this, BIND_AUTO_CREATE);
                break;
            case R.id.button_search:
                blueToothSearch();
                break;
            case R.id.button_searchBLE:
                permissionCheck();
                searchBLEdevice();
                break;
            default:
                return;
        }
    }

    /**
     * 绑定socket接收数据服务
     */
    private TcpReceiveService mTcpReceiveService;
    private UdpReceiveService mUdpReceiveService;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.e(Config.TAG, "Class name = " + name.getClassName());
        if ("zhangchongantest.neu.edu.graduate_test_sockt.TcpReceiveService".equals(name.getClassName())) {
            TcpReceiveService.ReceiveBinder tcpBinder = (TcpReceiveService.ReceiveBinder) service;
            mTcpReceiveService = tcpBinder.getBinder();
            tcpUpdata();
        }
        if ("zhangchongantest.neu.edu.graduate_test_sockt.UdpReceiveService".equals(name.getClassName())) {
            UdpReceiveService.ReceiveBinder udpBinder = (UdpReceiveService.ReceiveBinder) service;
            mUdpReceiveService = udpBinder.getBinder();
            udpUpdata();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tcpSendintent != null) {
            stopService(tcpSendintent);
        }
        if (udpSendIntent != null) {
            stopService(udpSendIntent);
        }
        unbindService(this);
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        Log.e(Config.TAG, "onDestroy");
    }

    /**
     * 打开蓝牙，注册广播监听
     */
    private void blueToothSearch() {
        if (intentFilter == null) {
            mReceiver = new BlueToothReceiver(this);
            intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mReceiver, intentFilter);
            intentFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(mReceiver, intentFilter);
            intentFilter = new IntentFilter(Config.BOND_PREPARE_ACTION);
            registerReceiver(mReceiver, intentFilter);
        }
        if (mAdapter == null) {
            mAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (mAdapter.isEnabled()) {
            Config.SEARCH_FINISHED = 1;
            mLoadingDialog.show();
            tv_loading.setText(Config.LOADING_MESSAGE);
            mAdapter.enable();
            if (mAdapter.isDiscovering()) {
                mAdapter.cancelDiscovery();
            }
            mAdapter.startDiscovery();
        } else {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BLUETOOTH_ENABLE_REQUEST);
        }
    }

    /**
     * 若检测到附近有蓝牙设备回调
     */
    @Override
    public void SearchDeciceSuccess() {
        mAdapter.cancelDiscovery();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.device_list, null);
        ls_new = (ListView) view.findViewById(R.id.new_ListView);
        ls_bonded = (ListView) view.findViewById(R.id.bonded_ListView);
        ls_new.setOnItemClickListener(this);
        ls_bonded.setOnItemClickListener(this);
        if (DataManager.getInstance().getEntity().getNewDevice().size() != 0) {
            newAdapter = new NewDeviceAdapter(MainActivity.this, R.layout.item_list, DataManager.getInstance().getEntity().getNewDevice());
            ls_new.setAdapter(newAdapter);
        }
        if (DataManager.getInstance().getEntity().getBondedDevice().size() != 0) {
            bonderAdapter = new BondedDeviceAdapter(MainActivity.this, R.layout.item_list, DataManager.getInstance().getEntity().getBondedDevice());
            ls_bonded.setAdapter(bonderAdapter);
        }
        mListDialog = builder.create();
        mListDialog.setView(view);
        mLoadingDialog.dismiss();
        mListDialog.show();
    }

    @Override
    public void SearchDecicefailure() {
        Toast.makeText(MainActivity.this, "附近无蓝牙设备", Toast.LENGTH_LONG).show();
    }

    @Override
    public void MatchDeviceSuccess() {
        startConnect(DataManager.getInstance().getCurrentDevice().getAddress());
        Toast.makeText(MainActivity.this, "匹配成功", Toast.LENGTH_LONG).show();
    }

    @Override
    public void MatchDeviceFailure() {
        Toast.makeText(MainActivity.this, "匹配失败", Toast.LENGTH_LONG).show();
    }

    /**
     * 检测到的蓝牙设备列表点击事件
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.new_ListView:
                if (DataManager.getInstance().getEntity().getNewDevice().get(position).getDevice() != null) {
                    DataManager.getInstance().setCurrentDevice(
                            DataManager.getInstance().getEntity().getNewDevice().get(position).getDevice());
                    autoBond(DataManager.getInstance().getCurrentDevice());
                }
                Log.e(Config.TAG, "new_ListView position=" + position);
//                startConnect(DataManager.getInstance().getCurrentDevice().getAddress());
                break;
            case R.id.bonded_ListView:
                Log.e(Config.TAG, "bonded_ListView position=" + position);
                startConnect(DataManager.getInstance().getEntity().getBondedDevice().get(position).getAddress());
                break;
        }
        mListDialog.dismiss();
    }

    private void autoBond(BluetoothDevice device) {
        try {
            // 调用配对的方法，此方法是异步的，系统会触发BluetoothDevice.ACTION_PAIRING_REQUEST的广播
            // 收到此广播后，设置配对的密码
            ClsUtils.createBond(device.getClass(), device);
//            ClsUtils.cancelPairingUserInput(device.getClass(), device);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     根据设备地址建立连接的线程
     */
    private void startConnect(final String address) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BluetoothDevice bluetoothDevice = mAdapter.getRemoteDevice(address);
                try {
                    mSocket = bluetoothDevice.createRfcommSocketToServiceRecord(
                            UUID.fromString(Config.SPP_UUID));
                    Message msg = handler.obtainMessage();
                    msg.what = Config.BLUETOOTH_CONNECT_HANDLER;
                    msg.obj = "match success";
                    handler.sendMessage(msg);
                    Log.e(Config.TAG, "match success");
                } catch (IOException e) {
                    Message msg = handler.obtainMessage();
                    msg.what = Config.BLUETOOTH_CONNECT_HANDLER;
                    msg.obj = "match fail";
                    handler.sendMessage(msg);
                    Log.e(Config.TAG, "match fail");
                    e.printStackTrace();
                }
                try {
                    mSocket.connect();
                    MessageManager(mSocket);
                    Message msg = handler.obtainMessage();
                    msg.what = Config.BLUETOOTH_CONNECT_HANDLER;
                    msg.obj = "conncet success";
                    handler.sendMessage(msg);
                    Log.e(Config.TAG, "conncet success");
                } catch (IOException e) {
                    Message msg = handler.obtainMessage();
                    msg.what = Config.BLUETOOTH_CONNECT_HANDLER;
                    msg.obj = "conncet fail";
                    handler.sendMessage(msg);
                    Log.e(Config.TAG, "conncet fail");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /*
        接收蓝牙设备数据的线程
     */
    private void MessageManager(final BluetoothSocket dataSocket) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e(Config.TAG, "BlueTooth receiveing");
                    InputStream inputStream = dataSocket.getInputStream();
                    OutputStream outputStream = dataSocket.getOutputStream();
                    ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(1024);
                    byte[] buffer = new byte[1024];
                    int length = 0;
                    String recev = "";
                    String send = "response";
                    while ((length = inputStream.read(buffer)) != -1) {
//                        for(int index = 0 ; index <length ; index++){
//                            int v = buffer[index]& 0xFF;
//                            recev = String.format("%02X", buffer[index]);;
//                        }
                        byteOutputStream.write(buffer, 0, length);
                        recev = byteOutputStream.toString("utf-8");
                        Message msg = handler.obtainMessage();
                        msg.obj = recev;
                        Log.e(Config.TAG, "msg.obj=" + msg.obj);
                        msg.what = Config.BLUETOOTH_MSG_HANDLER;
                        handler.sendMessage(msg);
                        if (recev.contains(Config.End_char)) {
                            byteOutputStream.reset();
                        }
                        feedBacktoHC(mSocket);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void feedBacktoHC(final BluetoothSocket dataSocket) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String backmsg = "this is feedback msg";
                    OutputStream outputStream = dataSocket.getOutputStream();
                    byte[] buffer = backmsg.getBytes();
                    outputStream.write(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BLUETOOTH_ENABLE_REQUEST:
                if (resultCode == RESULT_OK) {
                    blueToothSearch();
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(MainActivity.this, "请代开蓝牙", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void getCurrentIp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                //检查Wifi状态
                if (!wm.isWifiEnabled())
                    wm.setWifiEnabled(true);
                WifiInfo wi = wm.getConnectionInfo();
                //获取32位整型IP地址
                int ipAdd = wi.getIpAddress();
                //把整型地址转换成“*.*.*.*”地址
                String ip = intToIp(ipAdd);
            }
        }).start();

    }

    private String intToIp(int i) {
        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }

    private static String str2HexStr(String str) {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    private void permissionCheck() {
        if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_COARSE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION}, 1);
            }
        } else {
            searchBLEdevice();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            searchBLEdevice();
        }
    }

    private void searchBLEdevice() {
        if (mAdapter == null) {
            mAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (mAdapter.isEnabled()) {
            mLoadingDialog.show();
//            if (bleService==null){
//                bleService = new BLEcontrollerService(mAdapter);
//            }
            tv_loading.setText(Config.LOADING_MESSAGE);
            mAdapter.enable();
        } else {
            Toast.makeText(this, "plz open bluetooth", Toast.LENGTH_LONG).show();
            return;
        }
        mleScanner = mAdapter.getBluetoothLeScanner();
        mleScanner.startScan(mCallBack);
    }

    private final ScanCallback mCallBack = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice bluetoothDevice = result.getDevice();
            String name = bluetoothDevice.getName();
            String mac = bluetoothDevice.getAddress();
            Log.e(Config.TAG, "name:" + name + "****mac:" + mac);
            if ("1".equals(name)) {
                mleScanner.stopScan(mCallBack);
                if (BLEintent == null) {
                    BLEintent = new Intent(MainActivity.this, BLEcontrollerService.class);
                }
                BLEintent.putExtra(BLEcontrollerService.SERVICE_COMMAND, BLEcontrollerService.COMMAND_START_CONNECT);
                BLEintent.putExtra(BLEcontrollerService.CONNECT_ADDRESS, bluetoothDevice.getAddress());
                startService(BLEintent);
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handerEventBus(MessageEvent messageEvent) {
        switch (messageEvent.getCmd()) {
            case MessageEvent.CONNECT_SUCCESS:
                Toast.makeText(this, "Bluetooth connect success", Toast.LENGTH_LONG).show();
                break;
            case MessageEvent.CONNECT_FAIL:
                if (mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                Toast.makeText(this, "Bluetooth connect fail", Toast.LENGTH_LONG).show();
                break;
            case MessageEvent.GATT_FAIL:
                if (mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                Toast.makeText(this, "Bluetooth gatt fail", Toast.LENGTH_LONG).show();
                break;
            case MessageEvent.WRITE_SUCCESS:
                if (mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                Toast.makeText(this, "Bluetooth write success", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
