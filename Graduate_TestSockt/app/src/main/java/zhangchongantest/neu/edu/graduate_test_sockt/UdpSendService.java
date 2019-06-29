package zhangchongantest.neu.edu.graduate_test_sockt;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class UdpSendService extends Service {
    private MyBinder mbinder = new MyBinder();
    private DatagramSocket mSocket;
    private String str_Message;
    private String str_IP;
    private String str_Port;
    public UdpSendService() {
    }

    class MyBinder extends Binder{
        UdpSendService getServiceBinder(){
            return UdpSendService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            if (mSocket==null||mSocket.isClosed()){
                mSocket= new DatagramSocket();
                Log.e(Config.TAG,"service on create");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(Config.TAG,"onStartCommand");
        str_IP=intent.getStringExtra(Config.GET_IP);
        str_Port=intent.getStringExtra(Config.GET_PORT);
        str_Message=intent.getStringExtra(Config.GET_MES);
        new Thread(new Runnable() {
            @Override
            public void run() {
//                if (mSocket == null || mSocket.isClosed()) {
//                    try {
//                        Log.e(Config.TAG, "socket start create");
//                        socket = new Socket(str_IP,Integer.parseInt(str_Port));
//                        socket.setReuseAddress(true);
//                        DataManager.getInstance().setSocket(socket);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        return;
//                    }
//                    Log.e(Config.TAG, "socket is create");
//                }
//                try {
//                    OutputStream outputStream = socket.getOutputStream();
//                    outputStream.write(str_Message.getBytes());
//                    outputStream.flush();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                try{
                    if (mSocket==null||mSocket.isClosed()){
                        return;
                    }
                    byte[] datas = str_Message.getBytes();
                    DatagramPacket dataPacket = new DatagramPacket(datas,datas.length, InetAddress.getByName(str_IP),Integer.parseInt(str_Port));
                    mSocket.send(dataPacket);
                    Log.e(Config.TAG,"Sended!!");
                }catch (SocketException e){
                    Log.e(Config.TAG,"catch Socket");
                    e.printStackTrace();
                }catch (IOException e){
                    Log.e(Config.TAG,"catch IO");
                    e.printStackTrace();
                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mbinder;
    }
}
