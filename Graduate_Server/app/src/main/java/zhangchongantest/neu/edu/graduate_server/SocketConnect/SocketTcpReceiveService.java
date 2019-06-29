package zhangchongantest.neu.edu.graduate_server.SocketConnect;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import zhangchongantest.neu.edu.graduate_server.Config;
import zhangchongantest.neu.edu.graduate_server.ObjectConfig;

public class SocketTcpReceiveService extends Service {
    private DatagramSocket mDataSocket;
    private ServerSocket mServerSocket;
    //private List<ObjectConfig> waitingList = new ArrayList<>();
    private int mPort;
    private String requestResult;

    public SocketTcpReceiveService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(Config.TAG, "Tcp onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mPort = ConnectManager.getInstance().getListenningPort();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mServerSocket==null){
                        mServerSocket = new ServerSocket(mPort,50);
                    }
                    while (true){
                        Socket mSocket = mServerSocket.accept();
                        ObjectConfig objectConfig = new ObjectConfig();
                        Log.e(Config.TAG, "TCP new connect");
                        objectConfig.setSocket(mSocket);
                        objectConfig.setSocketType(Config.TCP_TYPE);
                        //ObjectConfig.setGetSocketIP(packet.getAddress());
                        //ObjectConfig.setRequestMsg(requestResult);
                        //waitingList.add(objectConfig);
                        ConnectManager.getInstance().getRequestWaitingList().add(objectConfig);
                    }
//                    DatagramPacket packet;
//                    while (true) {
//                        if (mDataSocket == null || mDataSocket.isClosed()) {
//                            mDataSocket = new DatagramSocket(mPort);
//                        }
//                        ObjectConfig ObjectConfig = new ObjectConfig();
//                        byte[] datas = new byte[1024];
//                        packet = new DatagramPacket(datas, datas.length);
//                        Log.e(Config.TAG, "receive Init");
//                        mDataSocket.receive(packet);
//                        Log.e(Config.TAG, "received");
//                        requestResult = new String(packet.getData(), packet.getOffset(), packet.getLength());
//                        Log.e(Config.TAG, "result=" + requestResult);
//                        ObjectConfig.setGetSocketIP(packet.getAddress());
//                        ObjectConfig.setRequestMsg(requestResult);
//                        waitingList.add(ObjectConfig);
//                        ConnectManager.getInstance().setRequestWaitingList(waitingList);
//                        Thread.sleep(200);
//                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.e(Config.TAG, "onBind");
        return null;
    }
}
