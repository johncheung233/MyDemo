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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import zhangchongantest.neu.edu.graduate_client.Config;
import zhangchongantest.neu.edu.graduate_client.Fragment.CallBackFromFragment;
import zhangchongantest.neu.edu.graduate_client.R;
import zhangchongantest.neu.edu.graduate_client.SocketConnect.ConnectManager;

/**
 * Created by Cheung SzeOn on 2019/4/2.
 */

public class InputDialog extends AlertDialog implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private Context mContext;
    private TextView tv_title, tv_tips, tv_confirm , tv_cancel;
    private EditText ed_carID;
    private Spinner sp_carID ,sp_carType;
    private String currentID;
    private ArrayAdapter carIdAdapter,typeAdapter;
    private DialogSendMsgCallBack mCallback;
    private StringBuilder strBuilder = new StringBuilder();
    private String title="请输入车辆信息",tips="车牌样式为：粤AXXXXX";
    private int titleColorBg=Color.parseColor("#FF8200");
    private int colorRed = Color.parseColor("#FF0000");
    private Point point = new Point();
    public InputDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
        if (mContext instanceof DialogSendMsgCallBack) {
            mCallback = (DialogSendMsgCallBack) mContext;
        } else {
            throw new RuntimeException(context.toString() + " must implement Callback");
        }
        spinnerItemInit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(mContext, R.layout.input_dialog,null);
        setContentView(view);
        initView(view);
        setCanceledOnTouchOutside(false);
        initEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkData();
    }

    private void checkData(){
        if (ConnectManager.getInstance().getPersonalObjectConfig().getCarID()!=null){
            String carId = ConnectManager.getInstance().getPersonalObjectConfig().getCarID();
            ed_carID.setText(carId.substring(1,carId.length()));
            carIdAdapter.getCount();
            for (int index=0;index<carIdAdapter.getCount();index++)
            {
                if (carIdAdapter.getItem(index).equals(String.valueOf(carId.charAt(0)))){
                    sp_carID.setSelection(index);
                }
            }
        }else {
            ed_carID.setText("");
        }
    }

    private void initView(View view){
        tv_title = (TextView)view.findViewById(R.id.textView_title);
        tv_tips = (TextView)view.findViewById(R.id.textView_tips);
        tv_confirm = (TextView)view.findViewById(R.id.textView_confirm);
        tv_cancel = (TextView)view.findViewById(R.id.textView_cancel);
        ed_carID = (EditText)view.findViewById(R.id.editText_carId);
        sp_carID = (Spinner)view.findViewById(R.id.spinner_carId);
        sp_carType = (Spinner)view.findViewById(R.id.spinner_carType);
        tv_tips.setText(tips);
        tv_title.setText(title);
        tv_title.setBackgroundColor(titleColorBg);
        sp_carID.setAdapter(carIdAdapter);
        sp_carType.setAdapter(typeAdapter);
        setAttribute();
    }

    private void initEvent(){
        tv_confirm.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        sp_carID.setOnItemSelectedListener(this);
        sp_carType.setOnItemSelectedListener(this);
    }

    private void spinnerItemInit(){
        String carIdFirst[] = Config.CARID_FIRST.split("[" + Config.MSG_SPLIT + "]");
        List<String> carIdFirstArray = new ArrayList<>();
        for (String index:carIdFirst) {
            carIdFirstArray.add(index);
        }
        carIdAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_default_item, carIdFirstArray);
        carIdAdapter.setDropDownViewResource(R.layout.spinner_list);

        List<String> sizeType = new ArrayList<>();
        sizeType.add(Config.LARGE_TYPE);
        sizeType.add(Config.NORMAL_TYPE);
        sizeType.add(Config.SMALL_TYPE);
        typeAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_default_item, sizeType);
        typeAdapter.setDropDownViewResource(R.layout.spinner_list);

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
        switch (v.getId()){
            case R.id.textView_confirm:
                if (TextUtils.isEmpty(ed_carID.getText())) {
                    tv_tips.setTextColor(colorRed);
                    tv_tips.setText(mContext.getResources().getString(R.string.carIdEmpty));
                    return;
                }
                if (!matchCarId(ed_carID.getText().toString())) {
                    tv_tips.setTextColor(colorRed);
                    tv_tips.setText(mContext.getResources().getString(R.string.carIdWarning));
                    return;
                }
                strBuilder.append(Config.CMD_CAR_IN)
                        .append(Config.MSG_SPLIT)
                        .append(sp_carID.getSelectedItem() + ed_carID.getText().toString())
                        .append(Config.MSG_SPLIT)
                        .append(ConnectManager.getInstance().getPersonalObjectConfig().getAccountName())
                        .append(Config.MSG_SPLIT)
                        .append(sp_carType.getSelectedItem())
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private boolean matchCarId(String carId) {
        String regex = "^([A-Z]{1}(([0-9]{5}[DF])|([DF]([A-HJ-NP-Z0-9])[0-9]{4})))|([A-Z]{1}[A-HJ-NP-Z0-9]{4}[A-HJ-NP-Z0-9挂学警港澳]{1})$";
        return carId.matches(regex);
    }

    public TextView getTv_tips() {
        return tv_tips;
    }

    public void setTv_tips(TextView tv_tips) {
        this.tv_tips = tv_tips;
    }
}
