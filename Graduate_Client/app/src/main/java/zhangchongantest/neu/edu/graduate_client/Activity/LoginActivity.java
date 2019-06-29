package zhangchongantest.neu.edu.graduate_client.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import zhangchongantest.neu.edu.graduate_client.Config;
import zhangchongantest.neu.edu.graduate_client.R;
import zhangchongantest.neu.edu.graduate_client.SocketConnect.ConnectManager;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText ed_accountName, ed_accountPassword;
    private TextView tv_login,tv_register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        paletteInit();
        setActionBar();
    }

    private void paletteInit() {
        ed_accountName = (EditText) findViewById(R.id.editText_accountName);
        ed_accountPassword = (EditText) findViewById(R.id.editText_accountPassword);
        tv_login = (TextView)findViewById(R.id.textView_login);
        tv_register = (TextView)findViewById(R.id.textView_register);
        tv_login.setOnClickListener(this);
        tv_register.setOnClickListener(this);
    }

    private void setActionBar() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        // 显示返回按钮
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("返回");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:   //返回键的id
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView_login:
                if (TextUtils.isEmpty(ed_accountName.getText().toString())
                        || TextUtils.isEmpty(ed_accountPassword.getText().toString())) {
                    mWarningDialog.setMessage(getResources().getString(R.string.accountNameWarning));
                    mWarningDialog.show();
                    return;
                }
                mLoadingDialog.show();
                builder.append(Config.CMD_REGISTER_LOGIN)
                        .append(Config.MSG_SPLIT)
                        .append(ed_accountName.getText().toString())
                        .append(Config.MSG_SPLIT)
                        .append(ed_accountPassword.getText().toString())
                        .append(Config.End_char);
                ConnectManager.getInstance().getPersonalObjectConfig().setRequestMsg(builder.toString());
                sendMesBySocket();
                break;
            case R.id.textView_register:
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(registerIntent, Config.LoginRquestCode);
                break;

        }
    }

    public void receviceResponseMsg() {
        switch (requestMsg[2]) {
            case Config.OK:
                ConnectManager.getInstance().getEditor().putString(Config.SP_ACCOUNTNAME,ed_accountName.getText().toString());
                ConnectManager.getInstance().getEditor().putString(Config.SP_ACCOUNTPASSWORD,ed_accountPassword.getText().toString());
                ConnectManager.getInstance().getEditor().commit();
                ConnectManager.getInstance().getPersonalObjectConfig().setAccountName(ed_accountName.getText().toString());
                ConnectManager.getInstance().getPersonalObjectConfig().setAccountPassword(ed_accountPassword.getText().toString());
                finish();
                break;
            case Config.FAIL:
                mWarningDialog.setMessage(getResources().getString(R.string.loginFail));
                mWarningDialog.show();
                break;
            case Config.EMPTY:
                mWarningDialog.setMessage(getResources().getString(R.string.loginEmpty));
                mWarningDialog.show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Config.LoginRquestCode) {
            if (resultCode == Config.LoginResultCode) {
                finish();
            }
        }
    }
}
