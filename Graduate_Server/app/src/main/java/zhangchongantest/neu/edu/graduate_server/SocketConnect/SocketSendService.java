package zhangchongantest.neu.edu.graduate_server.SocketConnect;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import zhangchongantest.neu.edu.graduate_server.Config;
import zhangchongantest.neu.edu.graduate_server.ObjectConfig;

public class SocketSendService extends Service {
    private MyBinder mbinder = new MyBinder();
    private DatagramSocket mSocket;
    private String str_Message,Json;
    public SocketSendService() {
    }

    class MyBinder extends Binder{
        SocketSendService getServiceBinder(){
            return SocketSendService.this;
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
        Json=intent.getStringExtra(Config.SOCKETSEND);
        ObjectConfig objectConfig=new Gson().fromJson(Json,ObjectConfig.class);
        try{
            if (mSocket==null||mSocket.isClosed()){
                return super.onStartCommand(intent, flags, startId);
            }
            byte[] datas = objectConfig.getResponseMsg().getBytes();
            DatagramPacket dataPacket = new DatagramPacket(datas,datas.length
                    , objectConfig.getGetSocketIP()
                    , ConnectManager.getInstance().getListenningPort());
            mSocket.send(dataPacket);
            Log.e(Config.TAG,"Sended!!");
        }catch (SocketException e){
            Log.e(Config.TAG,"catch Socket");
            e.printStackTrace();
        }catch (IOException e){
            Log.e(Config.TAG,"catch IO");
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mbinder;
    }
}
