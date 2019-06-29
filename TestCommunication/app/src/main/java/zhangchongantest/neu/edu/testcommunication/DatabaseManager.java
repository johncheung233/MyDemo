package zhangchongantest.neu.edu.testcommunication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManager {
    private volatile static DatabaseManager instance = null;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    private DatabaseManager(Context context) {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public static DatabaseManager getInstance(Context context){
        if (instance == null){
            synchronized (DatabaseManager.class){
                if (instance == null){
                    instance = new DatabaseManager(context);
                }
            }
        }
        return instance;
    }

    public SQLiteDatabase getDb(){
        return db;
    }

    public void insertValue(String tableName,String keyName,String value){
        ContentValues values = new ContentValues();
        values.put(keyName,value);
        db.insert(tableName,null,values);
    }

    public Cursor queryValue(String tableName){
        Cursor cursor = db.query(tableName,null,null,null,null,null,null);
        if (cursor.moveToNext()){
            return cursor;
        }
        return null;
    }


}
