package zhangchongantest.neu.edu.graduate_server.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Cheung SzeOn on 2018/11/30.
 */

public class DataManager  {
    private SQLiteDatabase db;

    private DataManager(){}

    public static DataManager getInstance(){
        return DataManagerHolder.dataManager;
    }

    private static class DataManagerHolder{
        private static DataManager dataManager = new DataManager();
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public void setDb(Context context) {
        DataBaseControl dataBaseControl = new DataBaseControl(context);
        SQLiteDatabase db = dataBaseControl.getWritableDatabase();
        this.db = db;
    }

}
