package zhangchongantest.neu.edu.graduate_server.Activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import zhangchongantest.neu.edu.graduate_server.Config;
import zhangchongantest.neu.edu.graduate_server.DataBase.DataBaseControl;
import zhangchongantest.neu.edu.graduate_server.R;
import zhangchongantest.neu.edu.graduate_server.SocketConnect.ConnectManager;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{
    private Button bt_setTotal,bt_clear,bt_setPort;
    private EditText ed_setTotal,ed_setPort;
    private TextView tv_serverIP;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        paletteInit();
        sharedPreferencesInit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getServerIP();
    }

    private void sharedPreferencesInit(){
        sp = getSharedPreferences(Config.SERVICEPREFERENCE,MODE_PRIVATE);
        ed_setTotal.setText(String.valueOf(sp.getInt(Config.TOTALSPACE,0)));
        int listeningPort = sp.getInt(Config.LISTENINGPORT,60001);
        ed_setPort.setText(String.valueOf(listeningPort));
        ConnectManager.getInstance().setListenningPort(listeningPort);
        editor = sp.edit();
    }
    private void paletteInit(){
        bt_setTotal = (Button)findViewById(R.id.button_totalSpace);
        bt_setPort = (Button)findViewById(R.id.button_setPort);
        bt_clear = (Button)findViewById(R.id.button_clear);
        ed_setTotal = (EditText)findViewById(R.id.edit_totalSpace);
        ed_setPort = (EditText)findViewById(R.id.edit_listeningPort);
        tv_serverIP = (TextView)findViewById(R.id.textView_serverIP);
        bt_setTotal.setOnClickListener(this);
        bt_clear.setOnClickListener(this);
        bt_setPort.setOnClickListener(this);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("返回");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_totalSpace:
                int tempPort = Integer.parseInt(ed_setTotal.getText().toString());
                if (TextUtils.isEmpty(ed_setTotal.getText().toString())||tempPort==0){
                    return;
                }
                if(sp.getInt(Config.TOTALSPACE,0)!=tempPort) {
                    //重置车位总数会清空列表重新开始
                    DataBaseControl.settingPackingSpace(tempPort);
                    editor.putInt(Config.TOTALSPACE,tempPort);
                    editor.commit();
                    Toast.makeText(SettingActivity.this,"表格创建成功",Toast.LENGTH_SHORT).show();
                }else{
                    setClearPackingSpaceDialog();
                    Toast.makeText(SettingActivity.this,"已存在" ,Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_clear:

                break;

            case R.id.button_setPort:

                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getServerIP(){
        WifiManager wifiManager =(WifiManager)getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);
        tv_serverIP.setText(ip);
    }

    private String intToIp(int i) {
        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }

    private void setClearPackingSpaceDialog(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(SettingActivity.this);
        normalDialog.setTitle("警告");
        normalDialog.setMessage("点击确定将清空数据");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int tempPort = Integer.parseInt(ed_setTotal.getText().toString());
                        DataBaseControl.settingPackingSpace(tempPort);
                        editor.putInt(Config.TOTALSPACE,tempPort);
                        editor.commit();
                        Toast.makeText(SettingActivity.this,"入库成功",Toast.LENGTH_SHORT).show();
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
        normalDialog.show();
    }
}
