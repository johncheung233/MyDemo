package zhangchongantest.neu.edu.graduate_client.Activity;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import zhangchongantest.neu.edu.graduate_client.Config;
import zhangchongantest.neu.edu.graduate_client.R;
import zhangchongantest.neu.edu.graduate_client.SocketConnect.ConnectManager;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText ed_setIP,ed_setPort;
    private TextView tv_save , tv_cancel,tv_title;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setAttribute();
        paletteInit();
        sharedPreferencesInit();
    }

    private void setAttribute(){
        Point point = null;
        if (point==null){
            point = new Point();
        }
        Window window = getWindow();
        WindowManager windowManager = window.getWindowManager();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        Display display = windowManager.getDefaultDisplay();
        display.getSize(point);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = (int)(point.x * 0.8);
        params.height = (int)(point.y * 0.6);
        getWindow().setAttributes(params);
        setFinishOnTouchOutside(false);
    }

    private void sharedPreferencesInit(){
        editor = ConnectManager.getInstance().getSharedPreferences().edit();
        ed_setPort.setText(String.valueOf(ConnectManager.getInstance().getListenningPort()));
        ed_setIP.setText(ConnectManager.getInstance().getServerIP());
    }

    private void paletteInit(){
        ed_setIP = (EditText)findViewById(R.id.editText_IP);
        ed_setPort = (EditText)findViewById(R.id.editText_PORT);
        tv_save = (TextView) findViewById(R.id.textView_save);
        tv_cancel =(TextView) findViewById(R.id.textView_cancel);
        tv_title =(TextView)findViewById(R.id.textView_title);
        tv_save.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        tv_title.setText("设置");
        tv_title.setBackgroundColor(getResources().getColor(R.color.colorDialogTitle,null));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textView_save:
                ConnectManager.getInstance().setListenningPort(Integer.parseInt(ed_setPort.getText().toString()));
                ConnectManager.getInstance().setServerIP(ed_setIP.getText().toString());
                editor.putString(Config.SERVICEIP,ed_setIP.getText().toString());
                editor.putString(Config.LISTENINGPORT,ed_setPort.getText().toString());
                editor.commit();
                finish();
                break;
            case R.id.textView_cancel:
                finish();
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_down);
    }
}
