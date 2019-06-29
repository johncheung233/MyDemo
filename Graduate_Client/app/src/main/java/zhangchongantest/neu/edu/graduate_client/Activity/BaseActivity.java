package zhangchongantest.neu.edu.graduate_client.Activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;

import zhangchongantest.neu.edu.graduate_client.Dialog.WarningDialog;
import zhangchongantest.neu.edu.graduate_client.Config;
import zhangchongantest.neu.edu.graduate_client.Dialog.LoadingDialog;
import zhangchongantest.neu.edu.graduate_client.ObjectConfig;
import zhangchongantest.neu.edu.graduate_client.SocketConnect.ConnectManager;
import zhangchongantest.neu.edu.graduate_client.SocketConnect.ReceiveCallback;
import zhangchongantest.neu.edu.graduate_client.SocketConnect.ReceiverBroadcast;
import zhangchongantest.neu.edu.graduate_client.SocketConnect.SocketControlService;
import zhangchongantest.neu.edu.graduate_client.SocketConnect.SocketSend;

/**
 * Created by Cheung SzeOn on 2019/2/12.
 */

public class BaseActivity extends AppCompatActivity {
    public WarningDialog mWarningDialog;
    public LoadingDialog mLoadingDialog;
    public ReceiverBroadcast receiverBroadcast;
    public String requestMsg[];
    public StringBuilder builder;
    public IntentFilter intentFilter;
    public TimerThread timerThread;
    public Intent socketControlIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLoadingDialog();
//        intentFilter = new IntentFilter(Config.RECEIVE_FLAG);
//        receiverBroadcast = new ReceiverBroadcast(this);
//        registerReceiver(receiverBroadcast, intentFilter);
        builder = new StringBuilder();
    }

    @Override
    protected void onDestroy() {
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
        if (mWarningDialog.isShowing()) {
            mWarningDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //registerReceiver(receiverBroadcast, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(receiverBroadcast);
    }

    private void initLoadingDialog() {
//        View loadingview = View.inflate(this, R.layout.loading_dialog, null);
//        mLoadingDialog = new AlertDialog.Builder(this).create();
//        mLoadingDialog.setView(loadingview);
//        mLoadingDialog.setCancelable(false);
//        mLoadingDialog.setCanceledOnTouchOutside(false);
//        tv_loadingDialog = (TextView) loadingview.findViewById(R.id.textView_loading);
//        tv_loadingDialog.setText("loading....");
//
//        View warningview = View.inflate(this,R.layout.warning_dialog, null);
//        mWarningDialog = new AlertDialog.Builder(this).create();
//        mWarningDialog.setView(warningview);
//        mWarningDialog.setCancelable(false);
//        mWarningDialog.setCanceledOnTouchOutside(false);
//        tv_warningDialog = (TextView) warningview.findViewById(R.id.textView_wariningDialog);
//        bt_confirm = (Button) warningview.findViewById(R.id.button_confirm);
//        bt_confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mWarningDialog.dismiss();
//                return;
//            }
//        });
        mWarningDialog = new WarningDialog(this);
        mWarningDialog.setOnConfirmClick(new WarningDialog.OnConfirmClick() {
            @Override
            public void setOnConfirmClick() {
                return;
            }
        });

        mLoadingDialog = new LoadingDialog(this);
    }


    public void socketReceiveResponseMsg(ObjectConfig objectConfig) {
        timerThread.setManualStop(true);
        requestMsg = objectConfig.getResponseMsg().split("[" + Config.MSG_SPLIT + "]");
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

//    @Override
//    public void receviceResponseMsg() {
//        timerThread.setManualStop(true);
//        requestMsg = ConnectManager.getInstance().getPersonalObjectConfig().getResponseMsg().split("[" + Config.MSG_SPLIT + "]");
//        if (mLoadingDialog.isShowing()) {
//            mLoadingDialog.dismiss();
//        }
//    }

    public void sendMesBySocket() {
        if (socketControlIntent == null) {
            socketControlIntent = new Intent(BaseActivity.this, SocketControlService.class);
        }
        socketControlIntent.putExtra(SocketControlService.SET_COMMAND, SocketControlService.CMD_SENDMESSAGE);
        startService(socketControlIntent);
        if (timerThread == null){
            timerThread = new TimerThread();
         }
        timerThread.setManualStop(false);
        new Thread(timerThread).start();
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        builder.delete(0, builder.length());
    }

    class TimerThread implements Runnable {
        Message msg;
        Boolean isManualStop = false;
        int time = 0;

        public void run() {

            while (time < 10) {
                if (isManualStop) {
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    mLoadingDialog.dismiss();
                    Message msg = mHandler.obtainMessage();
                    msg.what = 2;
                    mHandler.sendMessage(msg);
                }
                ++time;
            }
            time = 0;
            if (!isManualStop) {
                if (mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                    msg = mHandler.obtainMessage();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                }
            }
        }

        public void setManualStop(Boolean manualStop) {
            isManualStop = manualStop;
        }
    }

        Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        if (socketControlIntent == null) {
                            socketControlIntent = new Intent(BaseActivity.this, SocketControlService.class);
                        }
                        socketControlIntent.putExtra(SocketControlService.SET_COMMAND, SocketControlService.CMD_DISCONNECT);
                        startService(socketControlIntent);
                        Toast.makeText(BaseActivity.this, "网络连接超时", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(BaseActivity.this, "interrupt error", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };
}
