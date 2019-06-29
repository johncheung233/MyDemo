package zhangchongantest.neu.edu.graduate_client.Dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import zhangchongantest.neu.edu.graduate_client.R;

/**
 * Created by Cheung SzeOn on 2019/3/28.
 */

public class WarningDialog extends AlertDialog implements View.OnClickListener {
    private TextView tv_title,tv_msg,tv_confirm;
    private OnConfirmClick onConfirmClick;
    private String title="温馨提示",message,confirmText="确定";
    private int titleColorBg=Color.parseColor("#FF8200");
    private int confirmColorBg=Color.parseColor("#F8F8F8");
    private Context context;
    public WarningDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(context, R.layout.warning_dialog,null);
        setContentView(view);
        setCanceledOnTouchOutside(false);
        initView(view);
        initEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        tv_title.setText(title);
        tv_msg.setText(message);
        tv_confirm.setText(confirmText);
    }

    private void setAttribute(){
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        getWindow().setAttributes(params);
    }

    private void initView(View view){
        tv_title = (TextView)view.findViewById(R.id.textView_title);
        tv_msg = (TextView)view.findViewById(R.id.textView_message);
        tv_confirm = (TextView)view.findViewById(R.id.textView_confirm);
        tv_title.setBackgroundColor(titleColorBg);
        tv_confirm.setBackgroundColor(confirmColorBg);
        setAttribute();
    }

    private void initEvent(){
        tv_confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textView_confirm:
                onConfirmClick.setOnConfirmClick();
                dismiss();
                break;
        }
    }

    public void setOnConfirmClick(OnConfirmClick onConfirmClick) {
        this.onConfirmClick = onConfirmClick;
    }

    public interface OnConfirmClick{
        void setOnConfirmClick();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getConfirmText() {
        return confirmText;
    }

    public void setConfirmText(String confirmText) {
        this.confirmText = confirmText;
    }
}
