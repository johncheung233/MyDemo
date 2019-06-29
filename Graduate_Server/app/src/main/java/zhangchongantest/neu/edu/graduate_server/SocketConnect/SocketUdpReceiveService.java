package zhangchongantest.neu.edu.graduate_server.SocketConnect;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import zhangchongantest.neu.edu.graduate_server.Config;
import zhangchongantest.neu.edu.graduate_server.ObjectConfig;

public class SocketUdpReceiveService extends Service {
    private DatagramSocket mDataSocket;
    private String requestResult;

    public SocketUdpReceiveService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(Config.TAG, "UDP onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramPacket packet;
                    while (true) {
                        if (mDataSocket == null || mDataSocket.isClosed()) {
                            mDataSocket = new DatagramSocket(60000);
                        }
                        ObjectConfig objectConfig = new ObjectConfig();
                        byte[] datas = new byte[1024];
                        packet = new DatagramPacket(datas, datas.length);
                        Log.e(Config.TAG, "UDP waitting");
                        mDataSocket.receive(packet);
                        Log.e(Config.TAG, "UDP new connect");
                        requestResult = new String(packet.getData(), packet.getOffset(), packet.getLength());
                        Log.e(Config.TAG, "UDP receive msg=" + requestResult);
                        objectConfig.setGetSocketIP(packet.getAddress());
                        objectConfig.setRequestMsg(requestResult);
                        objectConfig.setSocketType(Config.UDP_TYPE);
                        ConnectManager.getInstance().getRequestWaitingList().add(objectConfig);
                    }
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
    public void onDestroy() {
        super.onDestroy();
    }
}
