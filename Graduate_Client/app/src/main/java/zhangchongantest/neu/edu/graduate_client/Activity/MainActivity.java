package zhangchongantest.neu.edu.graduate_client.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import zhangchongantest.neu.edu.graduate_client.Config;
import zhangchongantest.neu.edu.graduate_client.CustomViewPagerSwitch;
import zhangchongantest.neu.edu.graduate_client.Dialog.AccountRegistDialog;
import zhangchongantest.neu.edu.graduate_client.Dialog.DialogSendMsgCallBack;
import zhangchongantest.neu.edu.graduate_client.Dialog.LoginDialog;
import zhangchongantest.neu.edu.graduate_client.Dialog.PayDialog;
import zhangchongantest.neu.edu.graduate_client.Fragment.CallBackFromFragment;
import zhangchongantest.neu.edu.graduate_client.Fragment.CallBackFromActivity;
import zhangchongantest.neu.edu.graduate_client.Fragment.MainFragment;
import zhangchongantest.neu.edu.graduate_client.Fragment.MineFragment;
import zhangchongantest.neu.edu.graduate_client.FragmentViewPagerAdapter;
import zhangchongantest.neu.edu.graduate_client.Dialog.InputDialog;
import zhangchongantest.neu.edu.graduate_client.ObjectConfig;
import zhangchongantest.neu.edu.graduate_client.R;
import zhangchongantest.neu.edu.graduate_client.SocketConnect.ConnectManager;
import zhangchongantest.neu.edu.graduate_client.SocketConnect.CustomViewPagerScoller;
import zhangchongantest.neu.edu.graduate_client.SocketConnect.SocketControlService;

import static zhangchongantest.neu.edu.graduate_client.Activity.BlueToothActivity.START_REQUESTCODE;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ViewPager.OnPageChangeListener, CallBackFromFragment, DialogSendMsgCallBack {
    private TextView tv_accountName, tv_mainFragment, tv_mineFragment;
    private ImageView im_login;
    private Intent settingIntent, bluetoothIntent, payIntent;
    private volatile boolean mBackKeyPressed = false;//记录是否有首次按键
    private Fragment currentFragment = null;
    private InputDialog inputDialog;
    private LoginDialog loginDialog;
    private PayDialog payDialog;
    private AccountRegistDialog registDialog;
    private List<Fragment> fragmentList = new ArrayList<>();
    private ViewPager viewPager;
    private CallBackFromActivity callBackFromActivity;
    private String sp_accountName, sp_accountPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferencesInit();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);
        tv_accountName = (TextView) view.findViewById(R.id.textView7);
        initView();
        initFragment();
    }

    private void sharedPreferencesInit() {
        ConnectManager.getInstance().setSharedPreferences(getSharedPreferences(Config.CLIENTPERFERENCE, MODE_PRIVATE));
        String listeningPort = ConnectManager.getInstance().getSharedPreferences().getString(Config.LISTENINGPORT, "60001");
        String serviceIP = ConnectManager.getInstance().getSharedPreferences().getString(Config.SERVICEIP, "192.168.1.100");
        sp_accountName = ConnectManager.getInstance().getSharedPreferences().getString(Config.SP_ACCOUNTNAME, "null");
        sp_accountPassword = ConnectManager.getInstance().getSharedPreferences().getString(Config.SP_ACCOUNTPASSWORD, "null");
//        String carid = ConnectManager.getInstance().getSharedPreferences().getString(Config.SP_CARID, "null");
//        String parkingId = ConnectManager.getInstance().getSharedPreferences().getString(Config.SP_PARKINGID, "null");
        if (ConnectManager.getInstance().getPersonalObjectConfig() == null) {
            ConnectManager.getInstance().setPersonalObjectConfig(new ObjectConfig());
        }
        ConnectManager.getInstance().setListenningPort(Integer.parseInt(listeningPort));
        ConnectManager.getInstance().setServerIP(serviceIP);
//        if (!"null".equals(accountName)) {
//            ConnectManager.getInstance().getPersonalObjectConfig().setAccountName(accountName);
//        }
//        if (!"null".equals(accountPassword)) {
//            ConnectManager.getInstance().getPersonalObjectConfig().setAccountPassword(accountPassword);
//        }
//        if (!"null".equals(carid)) {
//            ConnectManager.getInstance().getPersonalObjectConfig().setCarID(carid);
//        }
//        if (!"null".equals(parkingId)) {
//            ConnectManager.getInstance().getPersonalObjectConfig().setBookingSpaceId(parkingId);
//        }
        if (!"null".equals(sp_accountName) && !"null".equals(sp_accountPassword)) {
            setCommandAndSend(Config.SET_INITCHECK_COMMAND, sp_accountName, sp_accountPassword,getResources().getString(R.string.initCheck));
        }
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tv_mainFragment = (TextView) findViewById(R.id.textView_toMain);
        tv_mineFragment = (TextView) findViewById(R.id.textView_toMine);
        im_login = (ImageView) findViewById(R.id.imageView_login);
        im_login.setOnClickListener(this);
        tv_mainFragment.setOnClickListener(this);
        tv_mineFragment.setOnClickListener(this);
        viewPager.addOnPageChangeListener(this);
        viewPager.setPageTransformer(true, new CustomViewPagerSwitch());
        viewPager.setOffscreenPageLimit(1);
        CustomViewPagerScoller mScroller = new CustomViewPagerScoller(this);
        mScroller.setmScrollDuration(1000);
        mScroller.initViewPagerScroll(viewPager);
        if (inputDialog == null) {
            inputDialog = new InputDialog(this);
        }
        if (registDialog == null) {
            registDialog = new AccountRegistDialog(this);
        }
        if (loginDialog == null) {
            loginDialog = new LoginDialog(this);
        }
    }

    private void initFragment() {
        MainFragment mainFragment = new MainFragment();
        MineFragment mineFragment = new MineFragment();
        fragmentList.add(mainFragment);
        fragmentList.add(mineFragment);
        mainFragment.setCallBackFromFragment(this);
        mineFragment.setCallBackFromFragment(this);
        callBackFromActivity = mainFragment;
        FragmentViewPagerAdapter viewPagerAdapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(0);
        tv_mainFragment.setBackgroundResource(R.drawable.textview_selected);
    }

    /*
        add fragment by fragmentmanager
     */
    public void judgeFragment(Fragment fragment, int id, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //优先检查，fragment是否存在，避免重叠
        Fragment tempFragment = (Fragment) fragmentManager.findFragmentByTag(tag);
        if (tempFragment != null) {
            fragment = tempFragment;
        }
        if (fragment.isAdded()) {
            addOrShowFragment(fragmentTransaction, fragment, id, tag);
        } else {
            if (currentFragment != null && currentFragment.isAdded()) {
                fragmentTransaction.hide(currentFragment).add(id, fragment, tag).commit();
            } else {
                fragmentTransaction.add(id, fragment, tag).commit();
            }
            currentFragment = fragment;
        }
    }

    private void addOrShowFragment(FragmentTransaction transaction, Fragment fragment, int id, String tag) {
        if (currentFragment == fragment)
            return;
        if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
            transaction.hide(currentFragment).add(id, fragment, tag).commit();
        } else {
            transaction.hide(currentFragment).show(fragment).commit();
        }
        currentFragment.setUserVisibleHint(false);
        currentFragment = fragment;
        currentFragment.setUserVisibleHint(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!mBackKeyPressed) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mBackKeyPressed = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mBackKeyPressed = false;
                    }
                }).start();
            } else {//退出程序
                System.exit(0);
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        String accountName = ConnectManager.getInstance().getPersonalObjectConfig().getAccountName();
        if (id == R.id.account) {
            if (Config.INITCHECKNULL.equals(accountName) || accountName==null) {
                loginDialog.show();
            } else if (!Config.INITCHECKNULL.equals(accountName)) {
                mWarningDialog.setMessage("您已登录");
                mWarningDialog.show();
            }
        } else if (id == R.id.accountLogout) {
            if (Config.INITCHECKNULL.equals(accountName)) {
                mWarningDialog.setMessage(getResources().getString(R.string.LogoutWarning));
                mWarningDialog.show();
            } else {
                setCommandAndSend(Config.SET_LOGOUT_COMMAND, null, null,null);
            }
        } else if (id == R.id.setting) {
            startOtherActivity(Config.START_SETTING_ACTIVITY);
        }
//        else if (id == R.id.nav_share) {
//            startOtherActivity(Config.START_BLUETOOTH_ACTIVITY);
//        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView_toMain:
                tv_mainFragment.setBackgroundResource(R.drawable.textview_selected);
                tv_mineFragment.setBackgroundResource(R.drawable.textview_default);
                viewPager.setCurrentItem(0);
                break;
            case R.id.textView_toMine:
                tv_mainFragment.setBackgroundResource(R.drawable.textview_default);
                tv_mineFragment.setBackgroundResource(R.drawable.textview_selected);
                viewPager.setCurrentItem(1);
                break;
            case R.id.imageView_login:
                //TODO:for debug
                String carID = ConnectManager.getInstance().getPersonalObjectConfig().getCarID();
                String accountName = ConnectManager.getInstance().getPersonalObjectConfig().getAccountName();
                if (TextUtils.isEmpty(accountName)||Config.INITCHECKNULL.equals(accountName)) {
                    mWarningDialog.setMessage(getResources().getString(R.string.unLoginWarning));
                    mWarningDialog.show();
                    return;
                }
                if (carID==null || !Config.INITCHECKNULL.equals(carID) ) {
                    mWarningDialog.setMessage(getResources().getString(R.string.repaetCarin));
                    mWarningDialog.show();
                    return;
                }
                inputDialog.show();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void socketReceiveResponseMsg(ObjectConfig objectConfig) {
        super.socketReceiveResponseMsg(objectConfig);
        if (Config.CMD_CAR_IN.equals(requestMsg[0])) {
            switch (requestMsg[2]) {
                case Config.FULL:
                    mWarningDialog.setMessage(getResources().getString(R.string.parkingSpaceFull));
                    mWarningDialog.show();
                    break;
                case Config.REPEAT:
                    mWarningDialog.setMessage(getResources().getString(R.string.repeatWarning));
                    mWarningDialog.show();
                    break;
                //TODO
                default:
//                    ConnectManager.getInstance().getEditor().putString(Config.SP_CARID, requestMsg[1]);
//                    ConnectManager.getInstance().getEditor().putString(Config.SP_PARKINGID, requestMsg[2]);
//                    ConnectManager.getInstance().getEditor().commit();
                    ConnectManager.getInstance().getPersonalObjectConfig().setBookingSpaceId(requestMsg[2]);
                    ConnectManager.getInstance().getPersonalObjectConfig().setPackingDeviceId(requestMsg[3]);
                    ConnectManager.getInstance().getPersonalObjectConfig().setCarID(requestMsg[1]);
//                    ("您的车位为：" + requestMsg[2]);
//                    mWarningDialog.show();
                    if (socketControlIntent != null) {
                        socketControlIntent.putExtra(SocketControlService.SET_COMMAND, SocketControlService.CMD_DISCONNECT);
                    }
                    startService(socketControlIntent);
                    if (bluetoothIntent == null) {
                        bluetoothIntent = new Intent(MainActivity.this, BlueToothActivity.class);
                    }
                    startActivityForResult(bluetoothIntent,BlueToothActivity.START_REQUESTCODE);
                    break;
            }
        } else if (Config.CMD_REGISTER_LOGOUT.equals(requestMsg[0])) {
            switch (requestMsg[1]) {
                case Config.OK:
                    ConnectManager.getInstance().getEditor().putString(Config.SP_ACCOUNTNAME, "null");
                    ConnectManager.getInstance().getEditor().putString(Config.SP_ACCOUNTPASSWORD, "null");
                    ConnectManager.getInstance().getEditor().commit();
                    ConnectManager.getInstance().getPersonalObjectConfig().setAccountName(Config.INITCHECKNULL);
                    ConnectManager.getInstance().getPersonalObjectConfig().setAccountPassword(Config.INITCHECKNULL);
                    ConnectManager.getInstance().getPersonalObjectConfig().setBookingSpaceId(Config.INITCHECKNULL);
                    ConnectManager.getInstance().getPersonalObjectConfig().setBondStatus(Config.INITCHECKNULL);
                    ConnectManager.getInstance().getPersonalObjectConfig().setCarID(Config.INITCHECKNULL);
                    Toast.makeText(MainActivity.this, "账号已登出", Toast.LENGTH_LONG).show();
                    tv_accountName.setText("未登录");
                    break;
                case Config.FAIL:
                    mWarningDialog.setMessage("注销失败");
                    mWarningDialog.show();
                    break;
            }
        } else if (Config.CMD_PRE_OUT.equals(requestMsg[0])) {
            switch (requestMsg[2]) {
                case Config.OK:
                    ConnectManager.getInstance().getPersonalObjectConfig().setCarID(requestMsg[1]);
                    ConnectManager.getInstance().getPersonalObjectConfig().setLoginTime(requestMsg[3]);
                    ConnectManager.getInstance().getPersonalObjectConfig().setTime(requestMsg[4]);
                    if (payDialog == null) {
                        payDialog = new PayDialog(this);
                    }
                    payDialog.show();
                    break;
                case Config.FAIL:
                    mWarningDialog.setMessage("车辆位停放到正确车位，请与工作人员联系");
                    mWarningDialog.show();
                    break;
                case Config.EMPTY:
                    mWarningDialog.setMessage("请稍后重试");
                    mWarningDialog.show();
                    break;
            }
        } else if (Config.CMD_INIT_CHECK.equals(requestMsg[0])) {
            if (!Config.INITCHECKNULL.equals(requestMsg[1])) {
                ConnectManager.getInstance().getPersonalObjectConfig().setAccountName(requestMsg[1]);
            }else {
                ConnectManager.getInstance().getPersonalObjectConfig().setAccountName(Config.INITCHECKNULL);
            }
            if (!Config.INITCHECKNULL.equals(requestMsg[2])) {
                ConnectManager.getInstance().getPersonalObjectConfig().setAccountPassword(requestMsg[2]);
            }else {
                ConnectManager.getInstance().getPersonalObjectConfig().setAccountPassword(Config.INITCHECKNULL);
            }
            if (!Config.INITCHECKNULL.equals(requestMsg[3])) {
                ConnectManager.getInstance().getPersonalObjectConfig().setCarID(requestMsg[3]);
            }else {
                ConnectManager.getInstance().getPersonalObjectConfig().setCarID(Config.INITCHECKNULL);
            }
            if (!Config.INITCHECKNULL.equals(requestMsg[4])) {
                ConnectManager.getInstance().getPersonalObjectConfig().setBondStatus(requestMsg[4]);
            }else {
                ConnectManager.getInstance().getPersonalObjectConfig().setBondStatus(Config.INITCHECKNULL);
            }
            if (!Config.INITCHECKNULL.equals(requestMsg[5])) {
                ConnectManager.getInstance().getPersonalObjectConfig().setBookingSpaceId(requestMsg[5]);
            }else {
                ConnectManager.getInstance().getPersonalObjectConfig().setBookingSpaceId(Config.INITCHECKNULL);
            }
            refreshView();
            Toast.makeText(this, "初始化成功", Toast.LENGTH_LONG).show();
        } else if (Config.CMD_REGISTER_IN.equals(requestMsg[0])) {
            switch (requestMsg[3]) {
                case Config.OK:
                    ConnectManager.getInstance().getEditor().putString(Config.SP_ACCOUNTNAME, requestMsg[1]);
                    ConnectManager.getInstance().getEditor().putString(Config.SP_ACCOUNTPASSWORD, requestMsg[2]);
                    ConnectManager.getInstance().getEditor().commit();
                    ConnectManager.getInstance().getPersonalObjectConfig().setAccountName(requestMsg[1]);
                    ConnectManager.getInstance().getPersonalObjectConfig().setAccountPassword(requestMsg[2]);
                    Toast.makeText(MainActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                    break;
                case Config.FAIL:
                    mWarningDialog.setMessage(getResources().getString(R.string.registerInFail));
                    mWarningDialog.show();
                    break;
            }
        } else if (Config.CMD_REGISTER_LOGIN.equals(requestMsg[0])) {
            switch (requestMsg[3]) {
                case Config.OK:
                    ConnectManager.getInstance().getEditor().putString(Config.SP_ACCOUNTNAME, requestMsg[1]);
                    ConnectManager.getInstance().getEditor().putString(Config.SP_ACCOUNTPASSWORD, requestMsg[2]);
                    ConnectManager.getInstance().getEditor().commit();
                    ConnectManager.getInstance().getPersonalObjectConfig().setAccountName(requestMsg[1]);
                    ConnectManager.getInstance().getPersonalObjectConfig().setAccountPassword(requestMsg[2]);
                    Toast.makeText(MainActivity.this, "登陆成功", Toast.LENGTH_LONG).show();
                    setCommandAndSend(Config.SET_INITCHECK_COMMAND, requestMsg[1], requestMsg[2],getResources().getString(R.string.initCheck));
                    break;
                case Config.FAIL:
                    mWarningDialog.setMessage(getResources().getString(R.string.loginFail));
                    mWarningDialog.show();
                    break;
                case Config.EMPTY:
                    mWarningDialog.setMessage(getResources().getString(R.string.loginEmpty));
                    mWarningDialog.show();
                    break;
            }
        } else if (Config.CMD_CAR_OUT.equals(requestMsg[0])) {
            switch (requestMsg[1]) {
                case Config.OK:
                    ConnectManager.getInstance().getPersonalObjectConfig().setBondStatus(Config.INITCHECKNULL);
                    ConnectManager.getInstance().getPersonalObjectConfig().setBookingSpaceId(Config.INITCHECKNULL);
                    ConnectManager.getInstance().getPersonalObjectConfig().setCarID(Config.INITCHECKNULL);
                    Toast.makeText(this, "支付成功", Toast.LENGTH_LONG).show();
                    break;
                case Config.FAIL:
                    mWarningDialog.setMessage("支付失败，请稍后重试");
                    mWarningDialog.show();
                    break;
            }
        }
        callBackFromActivity.feedBackMsg();
    }

    @Override
    protected void onDestroy() {
        if (socketControlIntent == null) {
            socketControlIntent = new Intent(MainActivity.this, SocketControlService.class);
        }
        socketControlIntent.putExtra(SocketControlService.SET_COMMAND, SocketControlService.CMD_DISCONNECT);
        startService(socketControlIntent);
        if (socketControlIntent != null) {
            stopService(socketControlIntent);
        }
        super.onDestroy();

//        if (ConnectManager.getInstance().getPersonalObjectConfig().getAccountName()!=null
//                || ConnectManager.getInstance().getPersonalObjectConfig().getAccountPassword() !=null) {
//            builder.append(Config.CMD_REGISTER_LOGOUT)
//                    .append(Config.MSG_SPLIT)
//                    .append(ConnectManager.getInstance().getPersonalObjectConfig().getAccountName())
//                    .append(Config.MSG_SPLIT)
//                    .append(ConnectManager.getInstance().getPersonalObjectConfig().getAccountPassword());
//            ConnectManager.getInstance().getPersonalObjectConfig().setRequestMsg(builder.toString());
//            sendMesBySocket();
//        }
    }

    private void refreshView() {
        String acountName = ConnectManager.getInstance().getPersonalObjectConfig().getAccountName();
        if (tv_accountName==null||Config.INITCHECKNULL.equals(acountName)) {
            tv_accountName.setText(getResources().getString(R.string.unLoginWarning));
        } else {
            tv_accountName.setText(acountName);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        refreshView();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //Log.e(Config.TAG, "position:" + position + "  positionOffset:" + positionOffset);
        if (position == 0) {
            if (positionOffset > 0.4) {
                callBackFromActivity.viewScrolling();
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                tv_mainFragment.setBackgroundResource(R.drawable.textview_selected);
                tv_mineFragment.setBackgroundResource(R.drawable.textview_default);
                viewPager.setCurrentItem(0);
                break;
            case 1:
                tv_mainFragment.setBackgroundResource(R.drawable.textview_default);
                tv_mineFragment.setBackgroundResource(R.drawable.textview_selected);
                viewPager.setCurrentItem(1);
                break;
        }
    }


    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void showWarningDialog(String msg) {
        if (!mWarningDialog.isShowing()) {
            mWarningDialog.setMessage(msg);
            mWarningDialog.show();
        }
    }

    public void setCallBackFromActivity(CallBackFromActivity callBackFromActivity) {
        this.callBackFromActivity = callBackFromActivity;
    }

    @Override
    public void dissWarningDialog() {
        if (mWarningDialog.isShowing()) {
            mWarningDialog.dismiss();
        }
    }

    @Override
    public void showLoadingDialog() {
        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    @Override
    public void dissLoadingDialog() {
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public void setCommandAndSend(int cmd, String arg1,String arg2,String dialogTitle) {
        if (!TextUtils.isEmpty(dialogTitle)){
            mLoadingDialog.setMessage(dialogTitle);
        }else{
            mLoadingDialog.setMessage(getResources().getString(R.string.loading));
        }
        showLoadingDialog();
        switch (cmd) {
            case Config.SET_LOGOUT_COMMAND:
                builder.append(Config.CMD_REGISTER_LOGOUT)
                        .append(Config.MSG_SPLIT)
                        .append(ConnectManager.getInstance().getPersonalObjectConfig().getAccountName())
                        .append(Config.MSG_SPLIT)
                        .append(ConnectManager.getInstance().getPersonalObjectConfig().getAccountPassword())
                        .append(Config.End_char);
                ConnectManager.getInstance().getPersonalObjectConfig().setRequestMsg(builder.toString());
                break;
            case Config.SET_PARKOUT_COMMAND:
                builder.append(Config.CMD_PRE_OUT)
                        .append(Config.MSG_SPLIT)
                        .append(ConnectManager.getInstance().getPersonalObjectConfig().getCarID())
                        .append(Config.End_char);
                ConnectManager.getInstance().getPersonalObjectConfig().setRequestMsg(builder.toString());
                break;
            case Config.SET_INITCHECK_COMMAND:
                builder.append(Config.CMD_INIT_CHECK)
                        .append(Config.MSG_SPLIT)
                        .append(arg1)
                        .append(Config.MSG_SPLIT)
                        .append(arg2)
                        .append(Config.End_char);
                ConnectManager.getInstance().getPersonalObjectConfig().setRequestMsg(builder.toString());
                break;
        }
        builder.delete(0, builder.length());
        sendMesBySocket();
    }

    @Override
    public void startOtherActivity(int arg) {
        switch (arg) {
            case Config.START_BLUETOOTH_ACTIVITY:
                String parkingSpaceID = ConnectManager.getInstance().getPersonalObjectConfig().getBookingSpaceId();
                String bondStatus = ConnectManager.getInstance().getPersonalObjectConfig().getBondStatus();
                if (Config.INITCHECKNULL.equals(parkingSpaceID)) {
                    mWarningDialog.setMessage(getResources().getString(R.string.parkingSparceID_null));
                    mWarningDialog.show();
                    return;
                }
                if ( !Config.INITCHECKNULL.equals(bondStatus)) {
                    mWarningDialog.setMessage(getResources().getString(R.string.bondStatus_OK));
                    mWarningDialog.show();
                    return;
                }
                if (socketControlIntent != null) {
                    socketControlIntent.putExtra(SocketControlService.SET_COMMAND, SocketControlService.CMD_DISCONNECT);
                }
                startService(socketControlIntent);
                if (bluetoothIntent == null) {
                    bluetoothIntent = new Intent(MainActivity.this, BlueToothActivity.class);
                }
                startActivityForResult(bluetoothIntent,BlueToothActivity.START_REQUESTCODE);
                break;
            case Config.START_SETTING_ACTIVITY:
                if (settingIntent == null) {
                    settingIntent = new Intent(MainActivity.this, SettingActivity.class);
                }
                startActivity(settingIntent);
                break;
        }
    }

    @Override
    public void onConfirmClick() {
        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
        sendMesBySocket();
    }

    @Override
    public void showRegistDialogFromLoginDialog() {
        if (!registDialog.isShowing()) {
            registDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            if (!"null".equals(sp_accountName) && !"null".equals(sp_accountPassword)) {
                setCommandAndSend(Config.SET_INITCHECK_COMMAND, sp_accountName, sp_accountPassword,getResources().getString(R.string.initCheck));
            } else {
                Toast.makeText(this, "请登录", Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(BlueToothActivity.START_REQUESTCODE==requestCode){
            if (BlueToothActivity.START_RESULTCODE==resultCode){
                callBackFromActivity.feedBackMsg();
            }
        }
    }
}
