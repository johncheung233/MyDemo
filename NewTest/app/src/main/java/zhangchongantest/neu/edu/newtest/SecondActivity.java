package zhangchongantest.neu.edu.newtest;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import zhangchongantest.neu.edu.newtest.databinding.ActivitySecondBinding;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySecondBinding mDatabing = DataBindingUtil.setContentView(this,R.layout.activity_second);
        User user = new User("sone",123);
        mDatabing.setUserInfo(user);
    }
}
