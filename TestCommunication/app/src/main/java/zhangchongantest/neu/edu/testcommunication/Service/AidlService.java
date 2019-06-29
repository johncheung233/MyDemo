package zhangchongantest.neu.edu.testcommunication.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import zhangchongantest.neu.edu.testcommunication.Account;
import zhangchongantest.neu.edu.testcommunication.IAccountManager;

public class AidlService extends Service {
    private static final String TAG = AidlService.class.getSimpleName();
    private List<Account> mList = new ArrayList<>();

    private IAccountManager.Stub mManager = new IAccountManager.Stub() {
        @Override
        public List<Account> getAccounts() throws RemoteException {
            return mList;
        }

        @Override
        public void addAccount(Account account) throws RemoteException {
            Log.e(TAG,"account name:"+account.getName() + "password:"+account.getPassword());
            mList.add(account);
        }
    };
    public AidlService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            mManager.addAccount(new Account("john","123"));
            Log.e(TAG,"onCreate");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mManager;
    }
}
