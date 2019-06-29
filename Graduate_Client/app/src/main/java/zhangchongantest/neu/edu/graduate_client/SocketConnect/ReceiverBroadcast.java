package zhangchongantest.neu.edu.graduate_client.SocketConnect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import zhangchongantest.neu.edu.graduate_client.Config;

/**
 * Created by Cheung SzeOn on 2019/1/31.
 */

public class ReceiverBroadcast extends BroadcastReceiver{
    private ReceiveCallback callback;

    public ReceiverBroadcast(ReceiveCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Config.RECEIVE_FLAG.equals(intent.getAction())){
            Log.e(Config.TAG,"receive!!!");
            callback.receviceResponseMsg();
        }
    }

}
