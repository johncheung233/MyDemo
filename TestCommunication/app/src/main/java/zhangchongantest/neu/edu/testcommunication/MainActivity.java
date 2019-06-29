package zhangchongantest.neu.edu.testcommunication;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        DatabaseManager dbManager = DatabaseManager.getInstance(getApplicationContext());
//        if (dbManager!=null){
//            dbManager.insertValue(DatabaseHelper.bookTableName,"bookName","java");
//            dbManager.insertValue(DatabaseHelper.personTableName,"personName","sone");
//        }
//        Cursor bookCursor = dbManager.queryValue(DatabaseHelper.bookTableName);
//        Cursor personCursor = dbManager.queryValue(DatabaseHelper.personTableName);
//
//        while (bookCursor.moveToNext()){
//            Log.e(TAG,"book name:"+bookCursor.getString(1));
//        }
//        bookCursor.close();
//
//        while (personCursor.moveToNext()){
//            Log.e(TAG,"person name:"+personCursor.getString(1));
//        }
//        personCursor.close();
    }
}
