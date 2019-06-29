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
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import zhangchongantest.neu.edu.graduate_client.Config;
import zhangchongantest.neu.edu.graduate_client.R;
import zhangchongantest.neu.edu.graduate_client.SocketConnect.ConnectManager;

/**
 * Created by Cheung SzeOn on 2019/5/4.
 */

public class AccountRegistDialog extends AlertDialog implements View.OnClickListener {
    private Context mContext;
    private TextView tv_title, tv_tips,tv_confirm,tv_cancel;
    private EditText ed_accoutName, ed_accoutPassword, ed_passwordConfirm;
    private StringBuilder strBuilder = new StringBuilder();
    private int titleColorBg=Color.parseColor("#FF8200");
    private DialogSendMsgCallBack mCallBack;
    private Point point = new Point();

    public AccountRegistDialog(@NonNull Context context) {
        super(context);
        mContext = context;
        if (mContext instanceof DialogSendMsgCallBack) {
            mCallBack = (DialogSendMsgCallBack) mContext;
        } else {
            throw new RuntimeException(context.toString() + " must implement Callback");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(mContext,R.layout.regist_dialog,null);
        setContentView(view);
        initView(view);
        setCanceledOnTouchOutside(false);
    }

    private void initView(View view){
        tv_title = view.findViewById(R.id.textView_title);
        tv_tips = view.findViewById(R.id.textView_tips);
        tv_confirm = view.findViewById(R.id.textView_confirm);
        tv_cancel = view.findViewById(R.id.textView_cancel);
        ed_accoutName = view.findViewById(R.id.editText_accountName);
        ed_accoutPassword = view.findViewById(R.id.editText_accountPassword);
        ed_passwordConfirm =view.findViewById(R.id.editText_passsordConfirm);
        tv_title.setBackgroundColor(titleColorBg);
        tv_title.setText("注册");
        tv_confirm.setOnClickListener(this);
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
            if(TextUtils.isEmpty(ed_accoutName.getText().toString())
                    || TextUtils.isEmpty(ed_accoutPassword.getText().toString())
                    || TextUtils.isEmpty(ed_passwordConfirm.getText().toString())){
                tv_tips.setText(mContext.getResources().getString(R.string.accountNameWarning));
                return;
            }
            if (!ed_passwordConfirm.getText().toString().equals(ed_accoutPassword.getText().toString())){
                tv_tips.setText(mContext.getResources().getString(R.string.passwordNotConfirm));
                return;
            }
            strBuilder.append(Config.CMD_REGISTER_IN)
                    .append(Config.MSG_SPLIT)
                    .append(ed_accoutName.getText().toString())
                    .append(Config.MSG_SPLIT)
                    .append(ed_accoutPassword.getText().toString())
                    .append(Config.End_char);
            ConnectManager.getInstance().getPersonalObjectConfig().setRequestMsg(strBuilder.toString());
            strBuilder.delete(0,strBuilder.length());
            dismiss();
            mCallBack.onConfirmClick();
        }else if (v == tv_cancel){
            dismiss();
        }
    }
}
