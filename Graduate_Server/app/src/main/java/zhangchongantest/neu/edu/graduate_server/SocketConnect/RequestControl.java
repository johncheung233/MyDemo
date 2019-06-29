package zhangchongantest.neu.edu.graduate_server.SocketConnect;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import zhangchongantest.neu.edu.graduate_server.Config;
import zhangchongantest.neu.edu.graduate_server.DataBase.DataBaseControl;
import zhangchongantest.neu.edu.graduate_server.ObjectConfig;
import zhangchongantest.neu.edu.graduate_server.SocketConnect.ConnectManager;

/**
 * Created by Cheung SzeOn on 2019/1/26.
 */

public class RequestControl implements Runnable{
    private ByteArrayOutputStream byteOutputStream;
    private static int tempIndex = 0;
    //private List<ObjectConfig> responseWaitingList = new ArrayList<>();
    public RequestControl() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void run() {
        while (true){
            if (ConnectManager.getInstance().getRequestWaitingList() == null &&
                    ConnectManager.getInstance().getRequestWaitingList().isEmpty()){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            if (tempIndex == ConnectManager.getInstance().getRequestWaitingList().size()){
                try {
                    Thread.sleep(500);
                    continue;
                } catch (InterruptedException e) {
                    Log.e(Config.TAG , "check Exception");
                    e.printStackTrace();
                }
            }else {
                //TODO:
                Log.e(Config.TAG , "request working");
                ObjectConfig tmpConfig = ConnectManager.getInstance().getRequestWaitingList().get(tempIndex);
                if (Config.TCP_TYPE.equals(tmpConfig.getSocketType())){
                    getRequestMsgFromTCP(tmpConfig);
                }
                ObjectConfig objectConfig = this.parseRequestMsg(tmpConfig);
                ConnectManager.getInstance().getResponseWaitingList().add(objectConfig);
                tempIndex ++ ;
            }
        }
    }

    private void getRequestMsgFromTCP(ObjectConfig objectConfig){
        try {
            InputStream inputStream = objectConfig.getSocket().getInputStream();
            if (byteOutputStream == null) {
                byteOutputStream = new ByteArrayOutputStream(1024);
            }
            byte[] buffer = new byte[1024];
            int length = 0;
            String recev = "";
            while ((length = inputStream.read(buffer)) != -1) {
                byteOutputStream.write(buffer, 0, length);
                recev = byteOutputStream.toString("utf-8");
                if (recev.contains(Config.End_char)) {
                    objectConfig.setRequestMsg(recev);
                    Log.e(Config.TAG, "socket requestMsg = " + recev);
                    byteOutputStream.reset();
                    break;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public  ObjectConfig parseRequestMsg(ObjectConfig objectConfig){
        String requestMsg[] = objectConfig.getRequestMsg().split("["+Config.MSG_SPLIT+"]");
        switch (Integer.parseInt(requestMsg[0])){
            case Config.CMD_CAR_IN:
                objectConfig.setCarID(requestMsg[1]);
                objectConfig.setAccountName(requestMsg[2]);
                objectConfig.setCarType(requestMsg[3]);
                if (DataBaseControl.checkRepeatIn(objectConfig)) {
                    objectConfig.setBookingSpaceId(Config.REPEAT);
                }else {
                    objectConfig.setBookingSpaceId(DataBaseControl.getEmptyPackingSpace());
                    objectConfig.setParkingDeviceId("0"+objectConfig.getBookingSpaceId());
                    DataBaseControl.carInforIn(objectConfig);
                }
                break;
            case Config.CMD_PRE_OUT:
                objectConfig.setCarID(requestMsg[1]);
                DataBaseControl.carPreOut(objectConfig);
                break;
            case Config.CMD_REGISTER_LOGIN:
                objectConfig.setAccountName(requestMsg[1]);
                objectConfig.setAccountPassword(requestMsg[2]);
//                objectConfig.setAccountType(requestMsg[3]);
                DataBaseControl.accountLogin(objectConfig);
                break;
            case Config.CMD_REGISTER_LOGOUT:
                objectConfig.setAccountName(requestMsg[1]);
                objectConfig.setAccountPassword(requestMsg[2]);
                DataBaseControl.accountLogout(objectConfig);
                break;
            case Config.CMD_REGISTER_IN:
                objectConfig.setAccountName(requestMsg[1]);
                objectConfig.setAccountPassword(requestMsg[2]);
//                objectConfig.setAccountType(requestMsg[3]);
                DataBaseControl.accoutRegister(objectConfig);
                break;
            case Config.CMD_PARKING_CHECK:
                objectConfig.setBookingSpaceId(requestMsg[1]);
                DataBaseControl.updateDeviceAddress(objectConfig);
                DataBaseControl.parkingCheck(objectConfig);
                break;
            case Config.CMD_CAR_OUT:
                objectConfig.setCarID(requestMsg[1]);
                objectConfig.setCost(requestMsg[2]);
                DataBaseControl.carConfirmOut(objectConfig);
                break;
            case Config.CMD_INIT_CHECK:
                objectConfig.setAccountName(requestMsg[1]);
                objectConfig.setAccountPassword(requestMsg[2]);
                DataBaseControl.clientInitCheck(objectConfig);
                break;
        }
        objectConfig.setCmd(requestMsg[0]);
        return objectConfig;
    }

}
