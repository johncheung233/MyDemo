package zhangchongantest.neu.edu.graduate_client.Dialog;

import android.content.Context;
import android.content.pm.ProviderInfo;
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
import android.widget.EditText;
import android.widget.TextView;

import zhangchongantest.neu.edu.graduate_client.Config;
import zhangchongantest.neu.edu.graduate_client.R;
import zhangchongantest.neu.edu.graduate_client.SocketConnect.ConnectManager;

/**
 * Created by Cheung SzeOn on 2019/4/6.
 */

public class LoginDialog extends AlertDialog implements View.OnClickListener {
    private Context mContext;
    private DialogSendMsgCallBack sendMsgCallBack;
    private EditText ed_accountName, ed_accountPassword;
    private TextView tv_confirm,tv_register,tv_cancel,tv_title,tv_tips;
    private int titleColorBg=Color.parseColor("#FF8200");
    private StringBuilder strBuilder = new StringBuilder();
    private Point point = new Point();

    public LoginDialog(@NonNull Context context) {
        super(context);
        mContext = context;
        if (mContext instanceof DialogSendMsgCallBack) {
            sendMsgCallBack = (DialogSendMsgCallBack) mContext;
        } else {
            throw new RuntimeException(context.toString() + " must implement Callback");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(mContext, R.layout.login_dialog,null);
        setContentView(view);
        initView(view);
    }

    private void initView(View view) {
        ed_accountName = view.findViewById(R.id.editText_accountName);
        ed_accountPassword = view.findViewById(R.id.editText_accountPassword);
        tv_confirm = view.findViewById(R.id.textView_confirm);
        tv_register = view.findViewById(R.id.textView_register);
        tv_cancel = view.findViewById(R.id.textView_cancel);
        tv_title = view.findViewById(R.id.textView_title);
        tv_tips  = view.findViewById(R.id.textView_tips);
        tv_title.setBackgroundColor(titleColorBg);
        tv_title.setText("登陆");
        tv_confirm.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        setAttribute();
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

    @Override
    public void onClick(View v) {
        if (v == tv_confirm){
            if (TextUtils.isEmpty(ed_accountName.getText().toString())
                    || TextUtils.isEmpty(ed_accountPassword.getText().toString())) {
                tv_tips.setText(mContext.getResources().getString(R.string.accountNameWarning));
                return;
            }
            strBuilder.append(Config.CMD_REGISTER_LOGIN)
                    .append(Config.MSG_SPLIT)
                    .append(ed_accountName.getText().toString())
                    .append(Config.MSG_SPLIT)
                    .append(ed_accountPassword.getText().toString())
                    .append(Config.End_char);
            ConnectManager.getInstance().getPersonalObjectConfig().setRequestMsg(strBuilder.toString());
            strBuilder.delete(0,strBuilder.length());
            dismiss();
            sendMsgCallBack.onConfirmClick();
        }else if (v == tv_register){
            dismiss();
            sendMsgCallBack.showRegistDialogFromLoginDialog();
        }else if(v == tv_cancel){
            dismiss();
        }

    }
}
