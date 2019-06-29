package zhangchongantest.neu.edu.graduate_client.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import zhangchongantest.neu.edu.graduate_client.Config;
import zhangchongantest.neu.edu.graduate_client.R;
import zhangchongantest.neu.edu.graduate_client.SocketConnect.ConnectManager;

public class RegisterActivity extends BaseActivity implements View.OnClickListener{
    private Button bt_register, bt_cancel;
    private EditText ed_accoutName, ed_accoutPassword, ed_passwordConfirm;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("返回");
        paletteInit();
    }

    private void paletteInit(){
        bt_register = (Button)findViewById(R.id.button_register);
//        bt_cancel = (Button)findViewById(R.id.button_cancel);
        ed_accoutName = (EditText) findViewById(R.id.editText_accountName);
        ed_accoutPassword = (EditText) findViewById(R.id.editText_accountPassword);
        ed_passwordConfirm = (EditText) findViewById(R.id.editText_passsordConfirm);
        bt_register.setOnClickListener(this);
//        bt_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_register:
                if(TextUtils.isEmpty(ed_accoutName.getText().toString())
                        || TextUtils.isEmpty(ed_accoutPassword.getText().toString())
                        || TextUtils.isEmpty(ed_passwordConfirm.getText().toString())){
                    mWarningDialog.setMessage(getResources().getString(R.string.accountNameWarning));
                    mWarningDialog.show();
                    return;
                }
                if (!ed_passwordConfirm.getText().toString().equals(ed_accoutPassword.getText().toString())){
                    mWarningDialog.setMessage(getResources().getString(R.string.passwordNotConfirm));
                    mWarningDialog.show();
                    return;
                }
                mLoadingDialog.show();
                builder.append(Config.CMD_REGISTER_IN)
                        .append(Config.MSG_SPLIT)
                        .append(ed_accoutName.getText().toString())
                        .append(Config.MSG_SPLIT)
                        .append(ed_accoutPassword.getText().toString())
                        .append(Config.End_char);
                ConnectManager.getInstance().getPersonalObjectConfig().setRequestMsg(builder.toString());
                sendMesBySocket();
                break;
            case R.id.button_cancel:
                finish();
                break;
        }

    }

    public void receviceResponseMsg() {
        switch (requestMsg[2]){
            case Config.OK:
                editor = ConnectManager.getInstance().getSharedPreferences().edit();
                editor.putString(Config.SP_ACCOUNTNAME,ed_accoutName.getText().toString());
                editor.putString(Config.SP_ACCOUNTPASSWORD,ed_accoutPassword.getText().toString());
                editor.commit();
                ConnectManager.getInstance().getPersonalObjectConfig().setAccountName(ed_accoutName.getText().toString());
                ConnectManager.getInstance().getPersonalObjectConfig().setAccountPassword(ed_accoutPassword.getText().toString());
                Intent intent = new Intent();
                setResult(Config.LoginResultCode,intent);
                finish();
                break;
            case Config.FAIL:
                mWarningDialog.setMessage(getResources().getString(R.string.registerInFail));
                mWarningDialog.show();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
