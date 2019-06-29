package zhangchongantest.neu.edu.testcommunication;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class Main2Activity extends AppCompatActivity {
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        textView = (TextView)findViewById(R.id.textView_title);
        Uri uri = getIntent().getData();
        if (uri!=null){
            String host = uri.getHost();
            List<String> pathSergent = uri.getPathSegments();

            Bundle bundle = getIntent().getExtras();
            String res = bundle.getString("arg1");
            textView.setText(res);
        }

    }
}
