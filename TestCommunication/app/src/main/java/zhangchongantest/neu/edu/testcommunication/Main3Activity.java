package zhangchongantest.neu.edu.testcommunication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Main3Activity extends AppCompatActivity {
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        imageView = (ImageView)findViewById(R.id.image);
        final Animation anim = AnimationUtils.loadAnimation(this,R.anim.scale);
        anim.setFillAfter(true);
        imageView.startAnimation(anim);
    }
}
