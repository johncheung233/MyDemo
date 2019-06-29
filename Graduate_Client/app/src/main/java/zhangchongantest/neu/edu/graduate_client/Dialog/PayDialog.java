package zhangchongantest.neu.edu.graduate_client.Dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import zhangchongantest.neu.edu.graduate_client.Config;
import zhangchongantest.neu.edu.graduate_client.R;
import zhangchongantest.neu.edu.graduate_client.SocketConnect.ConnectManager;

/**
 * Created by Cheung SzeOn on 2019/4/2.
 */

public class PayDialog extends AlertDialog implements View.OnClickListener{
    private Context mContext;
    private TextView tv_title, tv_confirm , tv_cancel,tv_carid,tv_logintime,tv_time,tv_cost;
    private DialogSendMsgCallBack mCallback;
    private StringBuilder strBuilder = new StringBuilder();
    private String title="支付信息";
    private int titleColorBg=Color.parseColor("#FF8200");
    private Point point = new Point();
    public PayDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
        if (mContext instanceof DialogSendMsgCallBack) {
            mCallback = (DialogSendMsgCallBack) mContext;
        } else {
            throw new RuntimeException(context.toString() + " must implement Callback");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(mContext, R.layout.pay_dialog,null);
        setContentView(view);
        initView(view);
        setCanceledOnTouchOutside(false);
        initEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        tv_title.setText(title);
        initData();
    }

    private void initView(View view){
        tv_title = view.findViewById(R.id.textView_title);
        tv_confirm = view.findViewById(R.id.textView_confirm);
        tv_cancel = view.findViewById(R.id.textView_cancel);
        tv_carid = view.findViewById(R.id.textView_carid);
        tv_logintime = view.findViewById(R.id.textView_logintime);
        tv_time = view.findViewById(R.id.textView_betweentime);
        tv_cost = view.findViewById(R.id.textView_cost);
        tv_title.setBackgroundColor(titleColorBg);
        setAttribute();
    }

    private void initEvent(){
        tv_confirm.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
    }

    private void setAttribute(){
        Window window = getWindow();
        WindowManager manager = window.getWindowManager();
        window.setWindowAnimations(R.style.DialogAnimationStyle);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Display display = manager.getDefaultDisplay();
        WindowManager.LayoutParams params = getWindow().getAttributes();
        display.getSize(point);
        params.width = (int)(point.x*0.8);
        params.height = (int)(point.y*0.6);
        window.setAttributes(params);
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    private void initData(){
        String[] time = ConnectManager.getInstance().getPersonalObjectConfig().getTime().split("[" + Config.TIME_SPLIT + "]");
        String days = time[0];
        String hours = time[1];
        String mins = time[2];
        int days_money = Integer.parseInt(days)*20;
        int hours_money = Integer.parseInt(hours)*2;
        int mins_money = Integer.parseInt(mins)*1;
        ConnectManager.getInstance().getPersonalObjectConfig().setCost(String.valueOf(days_money+hours_money+mins_money));
        tv_carid.setText(ConnectManager.getInstance().getPersonalObjectConfig().getCarID());
        tv_logintime.setText(ConnectManager.getInstance().getPersonalObjectConfig().getLoginTime());
        tv_time.setText(days+"天"+hours+"小时"+mins+"分钟");
        tv_cost.setText( ConnectManager.getInstance().getPersonalObjectConfig().getCost());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textView_confirm:
                strBuilder.append(Config.CMD_CAR_OUT)
                        .append(Config.MSG_SPLIT)
                        .append(ConnectManager.getInstance().getPersonalObjectConfig().getCarID())
                        .append(Config.MSG_SPLIT)
                        .append(ConnectManager.getInstance().getPersonalObjectConfig().getCost())
                        .append(Config.End_char);
                ConnectManager.getInstance().getPersonalObjectConfig().setRequestMsg(strBuilder.toString());
                strBuilder.delete(0,strBuilder.length());
                dismiss();
                mCallback.onConfirmClick();
                break;
            case R.id.textView_cancel:
                dismiss();
                break;
        }
    }
}
