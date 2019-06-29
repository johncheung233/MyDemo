package zhangchongantest.neu.edu.testcommunication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String dbName = "communicate";
    private static final int version = 1;
    public static final String bookTableName = "book";
    public static final String personTableName = "person";
    public DatabaseHelper(Context context) {
        super(context, dbName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " +bookTableName +
                " ( _id INTEGER PRIMARY KEY Autoincrement,bookName VARCHAR(10) NOT NULL)");
        db.execSQL("create table if not exists " +personTableName +
                " ( _id INTEGER PRIMARY KEY Autoincrement,personName VARCHAR(10) NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + bookTableName);
        db.execSQL("drop table if exists " + personTableName);
        this.onCreate(db);
    }
}
