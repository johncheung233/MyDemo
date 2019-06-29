package zhangchongantest.neu.edu.graduate_client.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import zhangchongantest.neu.edu.graduate_client.Config;
import zhangchongantest.neu.edu.graduate_client.R;
import zhangchongantest.neu.edu.graduate_client.SocketConnect.ConnectManager;


public class PayActivity extends BaseActivity implements View.OnClickListener{
    TextView tv_carid,tv_logintime,tv_time,tv_cost;
    Button bt_confirm,bt_cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        paletteInit();
    }

    private void paletteInit() {
        tv_carid = (TextView)findViewById(R.id.textView_carid);
        tv_logintime = (TextView)findViewById(R.id.textView_logintime);
        tv_time = (TextView)findViewById(R.id.textView_betweentime);
        tv_cost = (TextView)findViewById(R.id.textView_cost);
        bt_confirm = (Button)findViewById(R.id.button_confirm);
        bt_cancel = (Button)findViewById(R.id.button_cancel);
        bt_confirm.setOnClickListener(this);
        bt_cancel.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        countMoney();
        tv_carid.setText(ConnectManager.getInstance().getPersonalObjectConfig().getCarID());
        tv_logintime.setText(ConnectManager.getInstance().getPersonalObjectConfig().getLoginTime());
        tv_time.setText(ConnectManager.getInstance().getPersonalObjectConfig().getTime());
        tv_cost.setText( ConnectManager.getInstance().getPersonalObjectConfig().getCost());
    }

    private void countMoney(){
        String[] time = ConnectManager.getInstance().getPersonalObjectConfig().getTime().split("[" + Config.TIME_SPLIT + "]");
        int days = Integer.parseInt(time[0])*20;
        int hours = Integer.parseInt(time[1])*2;
        int mins = Integer.parseInt(time[2])*1;
        ConnectManager.getInstance().getPersonalObjectConfig().setCost(String.valueOf(days+hours+mins));
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_confirm:
                mLoadingDialog.show();
                builder.append(Config.CMD_CAR_OUT)
                        .append(Config.MSG_SPLIT)
                        .append(ConnectManager.getInstance().getPersonalObjectConfig().getCarID())
                        .append(Config.MSG_SPLIT)
                        .append(ConnectManager.getInstance().getPersonalObjectConfig().getCost())
                        .append(Config.End_char);
                ConnectManager.getInstance().getPersonalObjectConfig().setRequestMsg(builder.toString());
                sendMesBySocket();
                ConnectManager.getInstance().getPersonalObjectConfig().setCarID(null);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(10000);
                            mLoadingDialog.dismiss();
                            setResult(1);
                            finish();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.button_cancel:
                setResult(2);
                finish();
                break;
        }
    }
}
