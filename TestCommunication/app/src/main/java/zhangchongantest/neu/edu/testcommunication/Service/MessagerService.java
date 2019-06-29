package zhangchongantest.neu.edu.testcommunication.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class MessagerService extends Service {
    private static final String TAG = MessagerService.class.getSimpleName();
    private static final int MSG_FROM_CLIENT = 10;
    private static final int MSG_FROM_SERVICE = 11;

    private final Messenger mMessager = new Messenger(new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_FROM_CLIENT:
                    Log.e(TAG,"service receive msg:"+ msg.getData().getString("clientMessager"));
                    Messenger toClientMsger = msg.replyTo;
                    Message replyMessage = Message.obtain(null,MSG_FROM_SERVICE);
                    Bundle replyBundle = new Bundle();
                    replyBundle.putString("serviceMessenger","i got your msg,this is reply msg");
                    replyMessage.setData(replyBundle);
                    try {
                        toClientMsger.send(replyMessage);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    });
    public MessagerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mMessager.getBinder();
        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
