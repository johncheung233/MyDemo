package zhangchongantest.neu.edu.graduate_client.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import zhangchongantest.neu.edu.graduate_client.R;

/**
 * Created by Cheung SzeOn on 2019/3/29.
 */

public class LoadingDialog extends AlertDialog{
    private TextView tv_title,tv_msg;
    private String title = "温馨提示" , message = "加载中";
    private int titleColorBg=Color.parseColor("#FF8200");
    private Context context;
    private Timer timer;
    private int count = 3;
    public LoadingDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(context, R.layout.loading_dialog,null);
        setContentView(view);
        initView(view);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        initTimer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        tv_title.setText(title);
        tv_msg.setText(message);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            count++;
            if (count>3){
                count=1;
            }
            switch (count){
                case 1:
                    tv_msg.setText(message+".");
                    break;
                case 2:
                    tv_msg.setText(message+"..");
                    break;
                case 3:
                    tv_msg.setText(message+"...");
                    break;
            }
        }
    };

    private void initView(View view){
        tv_title = (TextView)view.findViewById(R.id.textView_title);
        tv_msg = (TextView)view.findViewById(R.id.textView_loading);
        tv_title.setBackgroundColor(titleColorBg);
        setAttribute();
    }

    private void setAttribute(){
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager manager = window.getWindowManager();
        Display display = manager.getDefaultDisplay();
        WindowManager.LayoutParams params = getWindow().getAttributes();
        Point outSize = new Point();
        display.getSize(outSize);
        params.width = (int)(outSize.x*0.5);
        getWindow().setAttributes(params);
    }

    private void initTimer(){
        if (timer==null){
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        },0,1000);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (timer!=null){
                    timer.cancel();
                }
            }
        });
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
