package zhangchongantest.neu.edu.graduate_server.Activity;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;
import zhangchongantest.neu.edu.graduate_server.CallBack.FromActivityCallBack;
import zhangchongantest.neu.edu.graduate_server.CallBack.ResponseListCallBack;
import zhangchongantest.neu.edu.graduate_server.Config;
import zhangchongantest.neu.edu.graduate_server.DataBase.DataBaseControl;
import zhangchongantest.neu.edu.graduate_server.DataBase.DataManager;
import zhangchongantest.neu.edu.graduate_server.Fragment.Fragment_0;
import zhangchongantest.neu.edu.graduate_server.Fragment.Fragment_1;
import zhangchongantest.neu.edu.graduate_server.Fragment.Fragment_2;
import zhangchongantest.neu.edu.graduate_server.FragmentViewPagerAdapter;
import zhangchongantest.neu.edu.graduate_server.MyViewPager;
import zhangchongantest.neu.edu.graduate_server.ObjectConfig;
import zhangchongantest.neu.edu.graduate_server.R;
import zhangchongantest.neu.edu.graduate_server.SocketConnect.RequestControl;
import zhangchongantest.neu.edu.graduate_server.SocketConnect.ResponseControl;
import zhangchongantest.neu.edu.graduate_server.SocketConnect.ConnectManager;
import zhangchongantest.neu.edu.graduate_server.SocketConnect.SocketTcpReceiveService;
import zhangchongantest.neu.edu.graduate_server.SocketConnect.SocketUdpReceiveService;
import zhangchongantest.neu.edu.graduate_server.listAdapter.CarInforListAdapter;
import zhangchongantest.neu.edu.graduate_server.listAdapter.PackingSpaceListAdapter;
import zhangchongantest.neu.edu.graduate_server.listAdapter.RegisterListAdapter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        Badge.OnDragStateChangedListener, ResponseListCallBack, Fragment_0.SwipeRefreshCallBack{
    private Button bt_dialog;
    private MyViewPager viewPager;
    private TabLayout tabLayout;
    private Intent tcpReceiveIntent, udpReceiveIntent, settingIntent;
    private SharedPreferences sp_listeningPort;
    private Thread requestThread, responseThread;
    private TextView tv_listTitle;
    private ListView ls_database;
    private AlertDialog mDialog;
    private RegisterListAdapter registerListAdapter;
    private CarInforListAdapter carInforListAdapter;
    private PackingSpaceListAdapter packingSpaceListAdapter;
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();
    private List<Integer> badgeCountList = new ArrayList<>();
    private FragmentViewPagerAdapter viewPagerAdapter;
//    private static NotificationManager noticeManager;
//    private static NotificationCompat.Builder noticeBuilder;
    private int count = 0;
    private FromActivityCallBack fromActivityCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(Config.TAG, "onMianCreat");
        DataManager.getInstance().setDb(MainActivity.this);
        initView();
        initViewData();
        sharedPreferencesInit();
        requestThread = new Thread(new RequestControl());
        requestThread.start();
        if (tcpReceiveIntent == null) {
            tcpReceiveIntent = new Intent(this, SocketTcpReceiveService.class);
        }
        startService(tcpReceiveIntent);
        if (udpReceiveIntent == null) {
            udpReceiveIntent = new Intent(this, SocketUdpReceiveService.class);
        }
        startService(udpReceiveIntent);
        responseThread = new Thread(new ResponseControl(handler));
        responseThread.start();
        listDialogInit();
        //notificationInit();
    }

    private void listDialogInit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.database_lsit, null);
        tv_listTitle = (TextView) view.findViewById(R.id.textView_listTitle);
        ls_database = (ListView) view.findViewById(R.id.listView_database);
        mDialog = builder.create();
        mDialog.setView(view);
    }

    private void initViewData(){
        Fragment_0 fragment_0 = new Fragment_0();
        fromActivityCallBack = fragment_0;
        fragmentList.add(fragment_0);
        fragmentList.add(new Fragment_1());
//        fragmentList.add(new Fragment_2());
        tabLayout.addTab(tabLayout.newTab(), false);
        tabLayout.addTab(tabLayout.newTab(), false);
//        tabLayout.addTab(tabLayout.newTab(), false);
        titleList.add("当前请求");
        titleList.add("盈利统计");
//        titleList.add("页面2");
        badgeCountList.add(count);
        badgeCountList.add(count);
//        badgeCountList.add(count);
        for (int index = 0; index < titleList.size(); index++) {
            tabLayout.getTabAt(index).setText(titleList.get(index));
        }
        tabLayout.setupWithViewPager(viewPager);
        viewPagerAdapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), this,
                fragmentList, titleList, badgeCountList);
        viewPager.setAdapter(viewPagerAdapter);
    }
    private void initView() {
        viewPager = (MyViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager.setOffscreenPageLimit(1);
    }

    private void setUpTabBadgeCount(int tabId,int badgeCount) {
        badgeCountList.set(tabId, badgeCount);
        for (int index = 0; index<fragmentList.size();index++) {
            TabLayout.Tab tab = tabLayout.getTabAt(index);
            if (tab != null) {
                View presentView = tab.getCustomView();
                if (presentView != null) {
                    ViewParent viewParent = presentView.getParent();
                    if (viewParent != null) {
                        ((ViewGroup) viewParent).removeView(presentView);
                    }
                }
            }
            tab.setCustomView(initTabView(index));
        }
        tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getCustomView().setSelected(true);
    }

    private View initTabView(int tabId) {
        View customView = LayoutInflater.from(this).inflate(R.layout.tab_item, null);
        TextView targetView = (TextView) customView.findViewById(R.id.tv_title);
        targetView.setText(titleList.get(tabId));
        Badge badge = new QBadgeView(this).bindTarget(targetView);
        badge.setBadgeNumber(badgeCountList.get(tabId));
        badge.setBadgeGravity(Gravity.END | Gravity.TOP);
        badge.setOnDragStateChangedListener(this);
        return customView;
    }

    private void sharedPreferencesInit() {
        sp_listeningPort = getSharedPreferences(Config.LISTENINGPORT, MODE_PRIVATE);
        ConnectManager.getInstance().setListenningPort(sp_listeningPort.getInt(Config.LISTENINGPORT, 60001));
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.normalmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Cursor cursor;
        switch (item.getItemId()) {
            case R.id.item_account:
                cursor = DataManager.getInstance().getDb().rawQuery("select * from " + Config.TABLE_REGIST, null);
                if (!cursor.moveToNext()) {
                    Toast.makeText(MainActivity.this, "cursor null", Toast.LENGTH_SHORT).show();
                    break;
                }
                registerListAdapter = new RegisterListAdapter(MainActivity.this, R.layout.baselist_item, cursor);
                ls_database.setAdapter(registerListAdapter);
                tv_listTitle.setText("用户信息表");
                mDialog.show();
                break;
            case R.id.item_carInfor:
                cursor = DataManager.getInstance().getDb().rawQuery("select * from " + Config.TABLE_CAR_INFOR, null);
                if (!cursor.moveToNext()) {
                    Toast.makeText(MainActivity.this, "cursor null", Toast.LENGTH_SHORT).show();
                    break;
                }
                carInforListAdapter = new CarInforListAdapter(MainActivity.this, R.layout.carinforlist_item, cursor);
                ls_database.setAdapter(carInforListAdapter);
                tv_listTitle.setText("车辆信息表");
                mDialog.show();
                break;
            case R.id.item_packingSpace:
                cursor = DataManager.getInstance().getDb().rawQuery("select * from " + Config.TABLE_PACKING_SPACE, null);
                if (!cursor.moveToNext()) {
                    Toast.makeText(MainActivity.this, "cursor null", Toast.LENGTH_SHORT).show();
                    break;
                }
                packingSpaceListAdapter = new PackingSpaceListAdapter(MainActivity.this, R.layout.baselist_item, cursor);
                ls_database.setAdapter(packingSpaceListAdapter);
                tv_listTitle.setText("停车位状态表");
                mDialog.show();
                break;
            case R.id.item_setting:
                if (settingIntent == null) {
                    settingIntent = new Intent(MainActivity.this, SettingActivity.class);
                }
                startActivity(settingIntent);
                break;
            case R.id.item_add:
                //setUpTabBadgeCount(0,++count);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i=0;i<5;i++) {
                            ContentValues values = new ContentValues();
                            values.put(Config.CAR_ID, i);
                            values.put(Config.REGIST_NAME, "0");
                            values.put(Config.CAR_TYPE, "0");
                            values.put(Config.PACKING_SPACE_ID, "0");
                            DataManager.getInstance().getDb().insert(Config.TABLE_CAR_INFOR, null, values);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.e(Config.TAG,"add finish");
                    }
                }).start();
                break;
            case R.id.item_logout:
                new Thread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        for (int i=0;i<5;i++) {
                            ObjectConfig objectConfig = new ObjectConfig();
                            objectConfig.setCarID(String.valueOf(i));
                            objectConfig.setCost(String.valueOf(i));
                            DataBaseControl.carConfirmOut(objectConfig);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.e(Config.TAG,"out finish");
                    }
                }).start();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (tcpReceiveIntent != null) {
            stopService(tcpReceiveIntent);
        }
        if (udpReceiveIntent != null) {
            stopService(udpReceiveIntent);
        }
        DataManager.getInstance().getDb().close();
        if (!Thread.State.RUNNABLE.equals(requestThread.getState())) {
            requestThread.interrupt();
        }
        if (!Thread.State.RUNNABLE.equals(responseThread.getState())) {
            responseThread.interrupt();
        }
        super.onDestroy();
    }

//    private void notificationInit() {
//        noticeManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
//        noticeBuilder = new NotificationCompat.Builder(this);
//        noticeBuilder.setContentTitle("消息")
//                .setContentText("有车辆入库")
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setWhen(System.currentTimeMillis())
//                .setDefaults(Notification.DEFAULT_VIBRATE);
//    }

//    public static void notificationShow() {
//        if (noticeManager != null && noticeBuilder != null) {
//            noticeManager.notify(10, noticeBuilder.build());
//        }
//    }

    public Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            notificationShow();
//            fromActivityCallBack.updataList();
            setUpTabBadgeCount(0,++count);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
//        if(!isServiceWorking(this,"zhangchongantest.neu.edu.graduate_server.SocketConnect.SocketTcpReceiveService")){
//            startService(tcpReceiveIntent);
//        }
    }

    public boolean isServiceWorking(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    @Override
    public void onDragStateChanged(int dragState, Badge badge, View targetView) {
        if (STATE_SUCCEED == dragState) {

        } else if (STATE_CANCELED == dragState) {

        }
    }

    @Override
    public void updataList() {

    }

    @Override
    public void swipeRefreshSuccess() {
        count = 0;
        setUpTabBadgeCount(0,count);
    }
}
