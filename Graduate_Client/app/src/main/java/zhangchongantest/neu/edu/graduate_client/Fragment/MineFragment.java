package zhangchongantest.neu.edu.graduate_client.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import zhangchongantest.neu.edu.graduate_client.Activity.MainActivity;
import zhangchongantest.neu.edu.graduate_client.Activity.SettingActivity;
import zhangchongantest.neu.edu.graduate_client.Config;
import zhangchongantest.neu.edu.graduate_client.R;
import zhangchongantest.neu.edu.graduate_client.SocketConnect.ConnectManager;

/**
 * Created by Cheung SzeOn on 2019/3/29.
 */

public class MineFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private TextView tv_accountLogout, tv_setting,tv_exit;
    private CallBackFromFragment callBackFromFragment;
    private Boolean isPrepared;
    public MineFragment() {
        super();
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible){
            return;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mine_fragment,null);
        initView(view);
        isPrepared = true;
        return view;
    }

    private void initView(View view) {
        tv_accountLogout = (TextView)view.findViewById(R.id.textView_accountLogout);
        tv_setting = (TextView)view.findViewById(R.id.textView_setting);
        tv_exit = (TextView)view.findViewById(R.id.textView_exit);
        tv_accountLogout.setOnClickListener(this);
        tv_setting.setOnClickListener(this);
        tv_exit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textView_accountLogout:
                if (ConnectManager.getInstance().getPersonalObjectConfig().getAccountName() == null){
                    callBackFromFragment.showWarningDialog(getResources().getString(R.string.LogoutWarning));
                }else {
                    callBackFromFragment.setCommandAndSend(Config.SET_LOGOUT_COMMAND, null ,null,null);
                }
                break;
            case R.id.textView_setting:
                callBackFromFragment.startOtherActivity(Config.START_SETTING_ACTIVITY);
                break;
            case R.id.textView_exit:
                System.exit(0);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void setCallBackFromFragment(CallBackFromFragment callBackFromFragment) {
        this.callBackFromFragment = callBackFromFragment;
    }
}
