package zhangchongantest.neu.edu.graduate_server.SocketConnect;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

import zhangchongantest.neu.edu.graduate_server.CallBack.ResponseListCallBack;
import zhangchongantest.neu.edu.graduate_server.Config;
import zhangchongantest.neu.edu.graduate_server.ObjectConfig;
import zhangchongantest.neu.edu.graduate_server.SocketConnect.ConnectManager;


/**
 * Created by Cheung SzeOn on 2019/1/29.
 */

public class ResponseControl implements Runnable{
    private static int tempIndex = 0;
    private DatagramSocket udpSocket;
    private StringBuilder builder = new StringBuilder();
    private Handler handler;
    public ResponseControl(Handler handler ) {
        this.handler = handler;
//        try {
//            if (mSocket==null||mSocket.isClosed()){
//                mSocket= new DatagramSocket();
//                Log.e(Config.TAG,"service on create");
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    @Override
    public void run() {
        while (true){
            if (ConnectManager.getInstance().getResponseWaitingList()==null &&
                    ConnectManager.getInstance().getResponseWaitingList().isEmpty()){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            if (tempIndex == ConnectManager.getInstance().getResponseWaitingList().size()){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }else {
                //TODO:
                Log.e(Config.TAG , "response working");
                parseResponseMsg(ConnectManager.getInstance().getResponseWaitingList().get(tempIndex));
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
                tempIndex++;
            }
        }
    }

    public  void parseResponseMsg(ObjectConfig objectConfig){
        switch (Integer.parseInt(objectConfig.getCmd())){
            case Config.CMD_CAR_IN:
                builder.append(objectConfig.getCmd())
                        .append(Config.MSG_SPLIT)
                        .append(objectConfig.getCarID())
                        .append(Config.MSG_SPLIT)
                        .append(objectConfig.getBookingSpaceId())
                        .append(Config.MSG_SPLIT)
                        .append(objectConfig.getParkingDeviceId())
                        .append(Config.End_char);
                objectConfig.setResponseMsg(builder.toString());
                sendTcpResponseMsg(objectConfig);
                break;
            case Config.CMD_PRE_OUT:
                builder.append(objectConfig.getCmd())
                        .append(Config.MSG_SPLIT)
                        .append(objectConfig.getCarID())
                        .append(Config.MSG_SPLIT)
                        .append(objectConfig.getParkingCheck())
                        .append(Config.MSG_SPLIT)
                        .append(objectConfig.getLoginTime())
                        .append(Config.MSG_SPLIT)
                        .append(objectConfig.getTime())
                        .append(Config.End_char);
                objectConfig.setResponseMsg(builder.toString());
                sendTcpResponseMsg(objectConfig);
                break;
            case Config.CMD_REGISTER_LOGIN:
                builder.append(objectConfig.getCmd())
                        .append(Config.MSG_SPLIT)
                        .append(objectConfig.getAccountName())
                        .append(Config.MSG_SPLIT)
                        .append(objectConfig.getAccountPassword())
                        .append(Config.MSG_SPLIT)
                        .append(objectConfig.getBaseResponse())
                        .append(Config.End_char);
                objectConfig.setResponseMsg(builder.toString());
                sendTcpResponseMsg(objectConfig);
                break;
            case Config.CMD_REGISTER_LOGOUT:
                builder.append(objectConfig.getCmd())
                        .append(Config.MSG_SPLIT)
                        .append(objectConfig.getBaseResponse())
                        .append(Config.End_char);
                objectConfig.setResponseMsg(builder.toString());
                sendTcpResponseMsg(objectConfig);
                break;
            case Config.CMD_REGISTER_IN:
                builder.append(objectConfig.getCmd())
                        .append(Config.MSG_SPLIT)
                        .append(objectConfig.getAccountName())
                        .append(Config.MSG_SPLIT)
                        .append(objectConfig.getAccountPassword())
                        .append(Config.MSG_SPLIT)
                        .append(objectConfig.getBaseResponse())
                        .append(Config.End_char);
                objectConfig.setResponseMsg(builder.toString());
                sendTcpResponseMsg(objectConfig);
                break;
            case Config.CMD_PARKING_CHECK:
                //builder.append(objectConfig.getUdpResponseMsg());
                objectConfig.setUdpResponseMsg(objectConfig.getUdpResponseMsg());
                sendUdpResponseMsg(objectConfig);
                break;
            case Config.CMD_CAR_OUT:
                if (Config.OK.equals(objectConfig.getBaseResponse())){
                    sendUdpResponseMsg(objectConfig);
                    builder.append(objectConfig.getCmd())
                            .append(Config.MSG_SPLIT)
                            .append(objectConfig.getBaseResponse())
                            .append(Config.End_char);
                    objectConfig.setResponseMsg(builder.toString());
                    sendTcpResponseMsg(objectConfig);
                }else {
                    builder.append(objectConfig.getCmd())
                            .append(Config.MSG_SPLIT)
                            .append(objectConfig.getBaseResponse())
                            .append(Config.End_char);
                    objectConfig.setResponseMsg(builder.toString());
                    sendTcpResponseMsg(objectConfig);
                }
                break;
            case Config.CMD_INIT_CHECK:
                builder.append(objectConfig.getCmd())
                        .append(Config.MSG_SPLIT)
                        .append(TextUtils.isEmpty(objectConfig.getAccountName())? Config.INITCHECKNULL : objectConfig.getAccountName())
                        .append(Config.MSG_SPLIT)
                        .append(TextUtils.isEmpty(objectConfig.getAccountPassword())? Config.INITCHECKNULL : objectConfig.getAccountPassword())
                        .append(Config.MSG_SPLIT)
                        .append(TextUtils.isEmpty(objectConfig.getCarID())? Config.INITCHECKNULL : objectConfig.getCarID())
                        .append(Config.MSG_SPLIT)
                        .append(TextUtils.isEmpty(objectConfig.getBondStatus())? Config.INITCHECKNULL : objectConfig.getBondStatus())
                        .append(Config.MSG_SPLIT)
                        .append(TextUtils.isEmpty(objectConfig.getBookingSpaceId())? Config.INITCHECKNULL : objectConfig.getBookingSpaceId())
                        .append(Config.End_char);;
                objectConfig.setResponseMsg(builder.toString());
                sendTcpResponseMsg(objectConfig);
                break;
        }
        builder.delete(0,builder.length());
    }

    public void sendTcpResponseMsg(ObjectConfig objectConfig){
        try{
//            if (mSocket==null||mSocket.isClosed()){
//                return;
//            }
            Socket tcpSocket = objectConfig.getSocket();
            if (tcpSocket == null||tcpSocket.isClosed()){
                Log.e(Config.TAG,"client socket is close");
                return;
            }
            OutputStream outputStream = tcpSocket.getOutputStream();
            outputStream.write(objectConfig.getResponseMsg().getBytes());
            outputStream.flush();
            Log.e(Config.TAG,"backToClient="+objectConfig.getResponseMsg());
            //socket.close();
//            byte[] datas = objectConfig.getResponseMsg().getBytes();
//            DatagramPacket dataPacket = new DatagramPacket(datas,datas.length
//                    , objectConfig.getGetSocketIP()
//                    , ConnectManager.getInstance().getListenningPort());
//            Log.e(Config.TAG,"msg="+objectConfig.getResponseMsg());
//            mSocket.send(dataPacket);
//            Log.e(Config.TAG,"Sended!!");
        }catch (SocketException e){
            Log.e(Config.TAG,"catch Socket");
            e.printStackTrace();
        }catch (IOException e){
            Log.e(Config.TAG,"catch IO");
            e.printStackTrace();
        }
    }

    private void sendUdpResponseMsg(ObjectConfig objectConfig){
        try {
            if (udpSocket==null||udpSocket.isClosed()){
                udpSocket= new DatagramSocket();
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.e(Config.TAG,"udp create fail");
            return;
        }
        byte[] datas = objectConfig.getUdpResponseMsg().getBytes();
        DatagramPacket dataPacket = new DatagramPacket(datas,datas.length
                , objectConfig.getGetSocketIP()
                , 60000);
        Log.e(Config.TAG,"udp send msg="+objectConfig.getUdpResponseMsg());
        try {
            udpSocket.send(dataPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(Config.TAG,"Sended!!");
    }

}
