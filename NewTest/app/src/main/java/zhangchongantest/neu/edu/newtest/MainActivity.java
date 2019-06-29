package zhangchongantest.neu.edu.newtest;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

import javax.xml.transform.Result;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zhangchongantest.neu.edu.newtest.Database.BaseBean;
import zhangchongantest.neu.edu.newtest.Database.DatabasePresenter;
import zhangchongantest.neu.edu.newtest.Database.StudentBean;
import zhangchongantest.neu.edu.newtest.RecyclerView.RecyclerViewAdapter;
import zhangchongantest.neu.edu.testcommunication.Account;
import zhangchongantest.neu.edu.testcommunication.IAccountManager;

public class MainActivity extends AppCompatActivity implements Contract.IView {

    @BindView(R.id.textView_title)
    TextView tv_title;
    @BindView(R.id.button_add)
    Button bt_add;
    @BindView(R.id.button_update)
    Button bt_update;
    @BindView(R.id.button_delete)
    Button bt_delete;
    @BindView(R.id.button_query)
    Button bt_query;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.button_addSingle)
    Button bt_addSingle;
    @BindView(R.id.button_communicate)
    Button bt_communicate;

    private TextView tv_set;
    private DatabasePresenter databasePresenter;
    private List<StudentBean> addList = new ArrayList<>();
    private List<? extends BaseBean> initList = new ArrayList<>();
    private static final String LOG = "DEBUG";
    private RecyclerViewAdapter reAdapter;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MSG_FROM_CLIENT = 10;
    private static final int MSG_FROM_SERVICE = 11;
    private Messenger toServiceMsger;
    private boolean isBind = false;

    private boolean isAidlBind = false;
    private IAccountManager iAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        tv_title.setText("this is title");
        databasePresenter = new DatabasePresenter(this, this);
        reAdapter = new RecyclerViewAdapter(this, initList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(reAdapter);
    }

    @Override
    public void DbPresenterResult(Object o) {
        if (o instanceof Long) {
            Toast.makeText(MainActivity.this, "result:" + (Long) o, Toast.LENGTH_LONG).show();
        } else if (o instanceof String) {
            Toast.makeText(MainActivity.this, "result:" + (String) o, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void DbPresenterResultList(List list) {
        reAdapter.updataList(list);
    }

    @OnClick({R.id.button_add, R.id.button_update, R.id.button_delete, R.id.button_query
            , R.id.button_addSingle, R.id.button_communicate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_add:
                for (int i = 0; i < 10; i++) {
                    StudentBean bean = new StudentBean();
                    bean.setStudentId(i);
                    bean.setStudentName("zhang_" + i);
                    addList.add(bean);
                }
                databasePresenter.insertStudentDao(addList);
                Log.e(LOG, "add");
                break;
            case R.id.button_update:
                break;
            case R.id.button_delete:
                break;
            case R.id.button_query:
                databasePresenter.queryStudentDao();
                break;
            case R.id.button_addSingle:
                StudentBean bean = new StudentBean();
                bean.setStudentId(123);
                bean.setStudentName("zhang_" + 123);
                databasePresenter.insertStudentDao(bean);
                break;
            case R.id.button_communicate:
//                Intent intent = new Intent();
//                intent.setData(Uri.parse("info://main/arg1=1/arg2=2"));
//                intent.setClassName("zhangchongantest.neu.edu.testcommunication",
//                        "zhangchongantest.neu.edu.testcommunication.android.intent.COMMUNICATE");
//                startActivity(intent);

                //Messenger type
//                Intent intent = new Intent();
//                intent.setClassName("zhangchongantest.neu.edu.testcommunication",
//                        "zhangchongantest.neu.edu.testcommunication.Service.MessagerService");
//                bindService(intent, sCon, BIND_AUTO_CREATE);
//                break;

                //AIDL type
//                Intent intent = new Intent();
//                intent.setClassName("zhangchongantest.neu.edu.testcommunication",
//                        "zhangchongantest.neu.edu.testcommunication.Service.AidlService");
//                bindService(intent,aidlConnect, Context.BIND_AUTO_CREATE);
//                break;

                //ContentProvider
                Uri bookUri = Uri.parse("content://zhangchongantest.neu.edu.testcommunication.communicate/book");
                Uri personUri = Uri.parse("content://zhangchongantest.neu.edu.testcommunication.communicate/person");
                ContentValues values = new ContentValues();
                values.put("personName","dudu");
                getContentResolver().insert(personUri,values);
                Cursor cursor = getContentResolver().query(personUri,null,null,null);
                while (cursor.moveToNext()){
                    Log.e(TAG,"NAME:"+cursor.getString(1));
                }
                cursor.close();
        }
    }

    private ServiceConnection aidlConnect = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isAidlBind = true;
            iAccountManager  = IAccountManager.Stub.asInterface(service);
            try {
                List<Account> dataList = iAccountManager.getAccounts();
                for (Account account : dataList){
                    Log.e(TAG,"account name:"+account.getName() + "password:"+account.getPassword());
                }
                iAccountManager.addAccount(new Account("sone","123"));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isAidlBind = false;
        }
    };

    private ServiceConnection sCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            toServiceMsger = new Messenger(service);
            Log.e(TAG, "connect service server success");
            isBind = true;
            Message message = Message.obtain(null,MSG_FROM_CLIENT);
            Bundle dataBundle = new Bundle();
            dataBundle.putString("clientMessager","this is message from client");
            message.setData(dataBundle);
            message.replyTo = clientMsger;
            try {
                toServiceMsger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        private Messenger clientMsger = new Messenger(new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MSG_FROM_SERVICE:
                        Log.e(TAG,"client recive msg:"+ msg.getData().getString("serviceMessenger"));
                        break;
                }
                super.handleMessage(msg);
            }
        });

        @Override
        public void onServiceDisconnected(ComponentName name) {
            toServiceMsger = null;
            Log.e(TAG, "disconnect service server");
            isBind = false;
        }
    };

    public void update() {

    }

    public static class MyHandler extends Handler {
        WeakReference<MainActivity> mWeakRefrence;

        public MyHandler(MainActivity activity) {
            mWeakRefrence = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final MainActivity activity = mWeakRefrence.get();
            if (activity != null) {
                activity.tv_title.setText("hhh");
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBind) {
            unbindService(sCon);
        }
        if (isAidlBind){
            unbindService(aidlConnect);
        }
    }

    public class MyAsync extends AsyncTask<String,Integer, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(Bitmap bitmap) {
            super.onCancelled(bitmap);
        }
    }

    public void aaa(){
        MyAsync asy = new MyAsync();
        asy.execute("start");
    }

}
