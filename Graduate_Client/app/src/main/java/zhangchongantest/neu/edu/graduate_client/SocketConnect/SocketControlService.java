package zhangchongantest.neu.edu.graduate_client.SocketConnect;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import zhangchongantest.neu.edu.graduate_client.Config;
import zhangchongantest.neu.edu.graduate_client.ObjectConfig;


public class SocketControlService extends Service {
    //private DatagramSocket mDataSocket;
    private Socket socket;
    private Intent broadcastIntent;
    private String requestResult;
    private InputStream inputStream;
    private OutputStream outputStream;
    private ByteArrayOutputStream byteOutputStream;
    private Boolean isWorking = false;
    private Boolean isSocketAvaliable = false;
    public static final int CMD_SENDMESSAGE = 1;
    public static final int CMD_DISCONNECT = 2;
    public static final int CMD_STOPRECEIVE = 3;
    public static final String SET_COMMAND = "SocketServiceCommand";
    private SendThread sendThread;
    private ReceiveThread receiveThread;

    public SocketControlService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(Config.TAG, "control service created");
//        broadcastIntent = new Intent();
//        broadcastIntent.setAction(Config.RECEIVE_FLAG);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            switch (intent.getIntExtra(SET_COMMAND, 0)) {
                case CMD_SENDMESSAGE:
                    if (sendThread == null) {
                        sendThread = new SendThread();
                    }
                    new Thread(sendThread).start();
                    break;
                case CMD_DISCONNECT:
                    disConnectSocket();
                    break;
                case CMD_STOPRECEIVE:
                    disconnectReceive();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    class SendThread implements Runnable {
        @Override
        public void run() {
            Log.e(Config.TAG, "SendThread run");
            ObjectConfig objectConfig = ConnectManager.getInstance().getPersonalObjectConfig();
            if (socket == null || socket.isClosed()) {
                try {
                    Log.e(Config.TAG, "socket null or close");
                    socket = new Socket(ConnectManager.getInstance().getServerIP(), ConnectManager.getInstance().getListenningPort());
                    socket.setReuseAddress(true);
                    ConnectManager.getInstance().setSocket(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(Config.TAG, "socket create fail");
                    isSocketAvaliable = false;
                    return;
                }
                if (receiveThread == null) {
                    receiveThread = new ReceiveThread();
                }
                new Thread(receiveThread).start();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            isWorking = true;
            try {
                outputStream = socket.getOutputStream();
                Log.e(Config.TAG, "socket sendMsg:" + objectConfig.getRequestMsg());
                outputStream.write(objectConfig.getRequestMsg().getBytes());
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ReceiveThread implements Runnable {
        @Override
        public void run() {
            try {
                //DatagramPacket packet;
                Log.e(Config.TAG, "ReceiveThread run");
                if (socket == null || socket.isClosed()) {
                    Log.e(Config.TAG, "ReceiveThread FAIL");
                }
                inputStream = socket.getInputStream();
                if (byteOutputStream == null) {
                    byteOutputStream = new ByteArrayOutputStream(1024);
                }
                byte[] buffer = new byte[1024];
                int length;
                String recev;
                while ((length = inputStream.read(buffer)) != -1 && isWorking) {
                    byteOutputStream.write(buffer, 0, length);
                    recev = byteOutputStream.toString("utf-8");
                    Log.e(Config.TAG, "socket receiveMsg = " + recev);
                    if (recev.contains(Config.End_char)) {
                        ConnectManager.getInstance().getPersonalObjectConfig().setResponseMsg(recev);
                        byteOutputStream.reset();
                        EventBus.getDefault().post(ConnectManager.getInstance().getPersonalObjectConfig());
                        //sendBroadcast(broadcastIntent);
                        disconnectReceive();
                        break;
                    }
                }
//                        if (mDataSocket == null || mDataSocket.isClosed()) {
//                            mDataSocket = new DatagramSocket(ConnectManager.getInstance().getListenningPort());
//                        }
//                        byte[] datas = new byte[1024];
//                        packet = new DatagramPacket(datas, datas.length);
//                        Log.e(Config.TAG, "receive Init");
//                        mDataSocket.receive(packet);
//                        requestResult = new String(packet.getData(), packet.getOffset(), packet.getLength());
//                        Log.e(Config.TAG, "result=" + requestResult);
//                        ConnectManager.getInstance().getPersonalObjectConfig().setResponseMsg(requestResult);
//                        Thread.sleep(2000);
//                        sendBroadcast(broadcastIntent);
//                        Thread.sleep(200);
            }  catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        return null;
    }

    private void disConnectSocket() {
        if (socket != null) {
            if (!socket.isClosed()) {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                    socket.close();
                } catch (IOException e) {
                    Log.e(Config.TAG, "socket close fail");
                    e.printStackTrace();
                }
            }
        }
        isWorking = false;
    }

    private void disconnectReceive() {
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(Config.TAG, "inputStream close fail");
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(Config.TAG, "outputStream close fail");
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(Config.TAG, "socket close fail");
        }
        isWorking = false;
    }

    @Override
    public void onDestroy() {
//        if (mDataSocket != null || !mDataSocket.isClosed()) {
//            mDataSocket.close();
//        }
        Log.e(Config.TAG, "socketControlService destoryed");
        super.onDestroy();
    }


}
