package zhangchongantest.neu.edu.testcommunication;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


public class DBContentProvider extends ContentProvider {

    public static final String AUTHORITY = "zhangchongantest.neu.edu.testcommunication.communicate";
    public static final int BOOK_URI_CODE = 0;
    public static final int PERSON_URI_CODE = 1;

    private Context mContext;
    private SQLiteDatabase db;

    private static final UriMatcher mUrimatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        mUrimatcher.addURI(AUTHORITY,"book",BOOK_URI_CODE);
        mUrimatcher.addURI(AUTHORITY,"person",PERSON_URI_CODE );

    }

    private String getTableName(Uri uri){
        String tableName = null;
        switch (mUrimatcher.match(uri)){
            case BOOK_URI_CODE:
                tableName = DatabaseHelper.bookTableName;
                break;
            case PERSON_URI_CODE:
                tableName = DatabaseHelper.personTableName;
                break;
        }
        return tableName;
    }



    @Override
    public boolean onCreate() {
        mContext = getContext();
        db = DatabaseManager.getInstance(mContext).getDb();
        return true;
    }

    @Override
    public Cursor query(Uri uri,String[] projection, String selection,String[] selectionArgs,String sortOrder) {
        Cursor cursor = null;
        String tableName = getTableName(uri);
        if (tableName!=null){
            cursor = db.query(tableName,projection,selection,selectionArgs,null,null,sortOrder);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String tableName = getTableName(uri);
        if (tableName!=null){
            db.insert(tableName,null,values);
            mContext.getContentResolver().notifyChange(uri,null);
        }
        return uri;
    }

    @Override
    public int delete(Uri uri,String selection,String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri,ContentValues values,String selection,String[] selectionArgs) {
        return 0;
    }
}
