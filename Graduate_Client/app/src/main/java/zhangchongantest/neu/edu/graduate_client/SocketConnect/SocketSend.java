package zhangchongantest.neu.edu.graduate_client.SocketConnect;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import zhangchongantest.neu.edu.graduate_client.Config;
import zhangchongantest.neu.edu.graduate_client.ObjectConfig;

/**
 * Created by Cheung SzeOn on 2019/1/29.
 */

public class SocketSend implements Runnable{
    private DatagramSocket mSocket;
    private Socket socket;
    private ObjectConfig objectConfig;
    public SocketSend() {

    }

    @Override
    public void run() {
        objectConfig = ConnectManager.getInstance().getPersonalObjectConfig();
        if (socket == null || socket.isClosed()) {
            try {
                Log.e(Config.TAG, "socket start create");
                socket = new Socket(ConnectManager.getInstance().getServerIP(),ConnectManager.getInstance().getListenningPort());
                socket.setReuseAddress(true);
                ConnectManager.getInstance().setSocket(socket);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Log.e(Config.TAG, "socket is create");
        }
        try {
            OutputStream outputStream = socket.getOutputStream();
            Log.e(Config.TAG, "sendMsg"+objectConfig.getRequestMsg());
            outputStream.write(objectConfig.getRequestMsg().getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//            if (mSocket == null || mSocket.isClosed()) {
//                mSocket = new DatagramSocket();
//                Log.e(Config.TAG, "SocketSend on create");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            if (mSocket == null || mSocket.isClosed()) {
//                return;
//            }
//            byte[] datas = objectConfig.getRequestMsg().getBytes();
//            DatagramPacket dataPacket = new DatagramPacket(datas, datas.length
//                    , InetAddress.getByName(ConnectManager.getInstance().getServerIP())
//                    , ConnectManager.getInstance().getListenningPort());
//            mSocket.send(dataPacket);
//            Log.e(Config.TAG, "Sended!!");
//        } catch (SocketException e) {
//            Log.e(Config.TAG, "catch Socket");
//            e.printStackTrace();
//        } catch (IOException e) {
//            Log.e(Config.TAG, "catch IO");
//            e.printStackTrace();
//        }
    }

    public void socketClose(){
//        if (mSocket!=null && !mSocket.isClosed()){
//            mSocket.close();
//        }
        if (socket!=null && !socket.isClosed()){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
