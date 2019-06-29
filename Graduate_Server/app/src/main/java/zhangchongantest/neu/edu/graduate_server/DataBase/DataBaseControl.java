package zhangchongantest.neu.edu.graduate_server.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import zhangchongantest.neu.edu.graduate_server.Config;
import zhangchongantest.neu.edu.graduate_server.ObjectConfig;

/**
 * Created by Cheung SzeOn on 2019/1/20.
 */

public class DataBaseControl extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "graduate.db";


    public DataBaseControl(Context context) {
        super(context, DATABASE_NAME, null, 17); //the previous version 15
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + Config.TABLE_CAR_INFOR +
                " ( " + Config.NUMBER + " INTEGER PRIMARY KEY Autoincrement,"
                + Config.CAR_ID + " VARCHAR(10) NOT NULL,"
                + Config.CAR_TYPE + " VARCHAR(5) NOT NULL,"
                + Config.LOGIN_TIME + " TIMESTAMP DEFAULT (datetime('now','localtime')),"
                + Config.LOGOUT_TIME + " VARCHAR(50) DEFAULT '" + Config.LOGOUT_TIME_DEFAULT + "',"
                + Config.PACKING_SPACE_ID + " VARCHAR(5) NOT NULL,"
                + Config.PACKING_CHECK + " VARCHAR(3) ,"
                + Config.COST + " REAL ,"
                + Config.REGIST_NAME + " VARCHAR(25) NOT NULL )");

        db.execSQL("create table if not exists " + Config.TABLE_PACKING_SPACE +
                " ( " + Config.NUMBER + " INTEGER PRIMARY KEY Autoincrement,"
                + Config.PACKING_SPACE_ID + " VARCHAR(3) NOT NULL,"
                + Config.PACKING_DEVICE_ID + " VARCHAR(4) NOT NULL,"
                + Config.PACKING_DEVICE_ADDRESS + " VARCHAR(25) ,"
                + Config.PACKING_SPACE_STATUS + " VARCHAR(8) DEFAULT '" + Config.DEFAULT_PACKING_STATUS + "' )");

        db.execSQL("create table if not exists " + Config.TABLE_REGIST +
                " ( " + Config.NUMBER + " INTEGER PRIMARY KEY Autoincrement,"
                + Config.REGIST_NAME + " VARCHAR(25) NOT NULL,"
                + Config.REGIST_PASSWORD + " VARCHAR(6) NOT NULL,"
                + Config.ONLINE_STATUS + " VARCHAR(9) NOT NULL,"
                + Config.REGIST_AUTHORITY + " VARCHAR(3) )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + Config.TABLE_CAR_INFOR);
        db.execSQL("drop table if exists " + Config.TABLE_REGIST);
        db.execSQL("drop table if exists " + Config.TABLE_PACKING_SPACE);
        this.onCreate(db);
    }

    public static void updateDeviceAddress(ObjectConfig objectConfig) {
        SQLiteDatabase db = DataManager.getInstance().getDb();
        Cursor cursor = db.query(Config.TABLE_PACKING_SPACE
                , null
                , Config.PACKING_SPACE_ID + "= ?"
                , new String[]{objectConfig.getBookingSpaceId()}
                , null
                , null
                , null);
        while (cursor.moveToNext()) {
            String address = objectConfig.getGetSocketIP().toString();
            db.execSQL("update " + Config.TABLE_PACKING_SPACE + " set " + Config.PACKING_DEVICE_ADDRESS + "= '" + address.substring(address.indexOf("/") + 1, address.length())
                    + "' where " + Config.PACKING_SPACE_ID + " = '" + objectConfig.getBookingSpaceId() + "'");
        }
        cursor.close();
    }

    public static void settingPackingSpace(int total) {
        SQLiteDatabase db = DataManager.getInstance().getDb();
        db.execSQL(" delete from " + Config.TABLE_PACKING_SPACE);
        db.execSQL("update sqlite_sequence set seq=0 where name= ' " + Config.TABLE_PACKING_SPACE + " '");
        for (int index = 1; index <= total; index++) {
            db.execSQL(" insert into " + Config.TABLE_PACKING_SPACE + "(" + Config.PACKING_SPACE_ID + "," + Config.PACKING_DEVICE_ID + ") values('" + index + "'," + "'0" + index + "')");
        }
    }

    public static String getEmptyPackingSpace() {
        String bookingID;
        SQLiteDatabase db = DataManager.getInstance().getDb();
        Cursor cursor = db.query(Config.TABLE_PACKING_SPACE
                , new String[]{Config.PACKING_SPACE_ID}
                , Config.PACKING_SPACE_STATUS + "= ?"
                , new String[]{Config.DEFAULT_PACKING_STATUS}
                , null
                , null
                , null);
        if (cursor.moveToNext()) {
            bookingID = cursor.getString(0);
            db.execSQL("update " + Config.TABLE_PACKING_SPACE + " set " + Config.PACKING_SPACE_STATUS + "= '" + Config.OCCUPY_PACKING_STATUS + "' where " + Config.PACKING_SPACE_ID + " = " + Integer.parseInt(bookingID));
        } else {
            bookingID = Config.FULL;
        }
        cursor.close();
        return bookingID;
    }

    public static boolean checkRepeatIn(ObjectConfig objectConfig) {
        Boolean result = false;
        SQLiteDatabase db = DataManager.getInstance().getDb();
        Cursor cursor = db.query(Config.TABLE_CAR_INFOR
                , null
                , Config.CAR_ID + "= ? AND " + Config.LOGOUT_TIME + "= ?"
                , new String[]{objectConfig.getCarID(), Config.LOGOUT_TIME_DEFAULT}
                , null
                , null
                , null);
        while (cursor.moveToNext()) {
            result = true;
        }
        cursor.close();
        return result;
    }

    public static void carInforIn(ObjectConfig objectConfig) {
        SQLiteDatabase db = DataManager.getInstance().getDb();
        if (Config.FULL.equals(objectConfig.getBookingSpaceId())) {
            return;
        }
//        Cursor cursor = db.query(Config.TABLE_CAR_INFOR
//                , new String[]{Config.CAR_ID,Config.LOGOUT_TIME }
//                , Config.CAR_ID + "= ?"
//                , new String[]{objectConfig.getCarID()}
//                , null
//                , null
//                , null);
//        while (cursor.moveToNext()){
//            if (TextUtils.isEmpty(cursor.getString(1))){
//
//                return;
//            }
//        }
        ContentValues values = new ContentValues();
        values.put(Config.CAR_ID, objectConfig.getCarID());
        values.put(Config.REGIST_NAME, objectConfig.getAccountName());
        values.put(Config.CAR_TYPE, objectConfig.getCarType());
        values.put(Config.PACKING_SPACE_ID, objectConfig.getBookingSpaceId());
        db.insert(Config.TABLE_CAR_INFOR, null, values);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static StringBuilder builder = new StringBuilder();

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static ObjectConfig carReadyOut(String strInTime, ObjectConfig objectConfig) {
        try {
            timeFormat.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
            Date loginTime = timeFormat.parse(strInTime);
            Date preOutTime = new Date(System.currentTimeMillis());
            long between = preOutTime.getTime() - loginTime.getTime();
            long days = between / (1000 * 60 * 60 * 24); //换算成天数
            long hours = (between - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60); //换算成小时
            long minutes = (between - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);//换算成分钟
            builder.append(days)
                    .append(Config.TIME_SPLIT)
                    .append(hours)
                    .append(Config.TIME_SPLIT)
                    .append(minutes);
            objectConfig.setTime(builder.toString());
            builder.delete(0, builder.length());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return objectConfig;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static ObjectConfig carPreOut(ObjectConfig objectConfig) {
        SQLiteDatabase db = DataManager.getInstance().getDb();
        Cursor cursor = db.query(Config.TABLE_CAR_INFOR
                , new String[]{Config.LOGIN_TIME, Config.LOGOUT_TIME, Config.PACKING_SPACE_ID, Config.PACKING_CHECK}
                , Config.CAR_ID + "= ? AND " + Config.LOGOUT_TIME + "= ?"
                , new String[]{objectConfig.getCarID(), Config.LOGOUT_TIME_DEFAULT}
                , null
                , null
                , null);
        while (cursor.moveToNext()) {
            if (cursor.getString(3) == null) {
                objectConfig.setParkingCheck(Config.FAIL);
                return objectConfig;
            } else {
                objectConfig.setLoginTime(cursor.getString(0));
                objectConfig.setParkingCheck(Config.OK);
                return carReadyOut(cursor.getString(0), objectConfig);
            }
        }
        objectConfig.setParkingCheck(Config.EMPTY);
        cursor.close();
        return objectConfig;
    }

    public static ObjectConfig accountLogin(ObjectConfig objectConfig) {
        SQLiteDatabase db = DataManager.getInstance().getDb();
        Cursor cursor = db.query(Config.TABLE_REGIST
                , new String[]{Config.REGIST_NAME, Config.REGIST_PASSWORD, Config.ONLINE_STATUS}
                , Config.REGIST_NAME + "= ?"
                , new String[]{objectConfig.getAccountName()}
                , null
                , null
                , null);
        if (!cursor.moveToNext()) {
            objectConfig.setBaseResponse(Config.EMPTY);
        } else {//cursor.getString(1).equals(objectConfig.getAccountPassword()) &&Config.OFFLINE.equals(cursor.getString(2))
            if (cursor.getString(1).equals(objectConfig.getAccountPassword()) || Config.OFFLINE.equals(cursor.getString(2))) { //TODO:|| IS DEBUG
                db.execSQL("update " + Config.TABLE_REGIST + " set " + Config.ONLINE_STATUS + "= '" + Config.ONLINE + "' where " + Config.REGIST_NAME + " = '" + objectConfig.getAccountName() + "'");
                objectConfig.setBaseResponse(Config.OK);
            } else {
                objectConfig.setBaseResponse(Config.FAIL);
            }
        }
        cursor.close();
        return objectConfig;
    }

    public static ObjectConfig accountLogout(ObjectConfig objectConfig) {
        SQLiteDatabase db = DataManager.getInstance().getDb();
        Cursor cursor = db.query(Config.TABLE_REGIST
                , new String[]{Config.REGIST_NAME, Config.REGIST_PASSWORD, Config.ONLINE_STATUS}
                , Config.REGIST_NAME + "= ?"
                , new String[]{objectConfig.getAccountName()}
                , null
                , null
                , null);
        while (cursor.moveToNext()) {
            if (cursor.getString(1).equals(objectConfig.getAccountPassword())) {
                db.execSQL("update " + Config.TABLE_REGIST + " set " + Config.ONLINE_STATUS + " = '" + Config.OFFLINE + "' where " + Config.REGIST_NAME + " = '" + objectConfig.getAccountName() + "'");
                objectConfig.setBaseResponse(Config.OK);
            } else {
                objectConfig.setBaseResponse(Config.FAIL);
            }
        }
        cursor.close();
        return objectConfig;
    }

    public static ObjectConfig accoutRegister(ObjectConfig objectConfig) {
        SQLiteDatabase db = DataManager.getInstance().getDb();
        Cursor cursor = db.query(Config.TABLE_REGIST
                , new String[]{Config.REGIST_NAME}
                , Config.REGIST_NAME + "= ?"
                , new String[]{objectConfig.getAccountName()}
                , null
                , null
                , null);
        if (cursor.moveToNext()) {
            objectConfig.setBaseResponse(Config.FAIL);
        } else {
            objectConfig.setBaseResponse(Config.OK);
            ContentValues values = new ContentValues();
            values.put(Config.REGIST_NAME, objectConfig.getAccountName());
            values.put(Config.REGIST_PASSWORD, objectConfig.getAccountPassword());
            values.put(Config.ONLINE_STATUS, Config.ONLINE);
            db.insert(Config.TABLE_REGIST, null, values);
        }
        cursor.close();
        return objectConfig;
    }

    public static ObjectConfig clientInitCheck(ObjectConfig objectConfig) {
        SQLiteDatabase db = DataManager.getInstance().getDb();
        Cursor cursorAcount = db.query(Config.TABLE_REGIST
                , new String[]{Config.REGIST_NAME, Config.REGIST_PASSWORD}
                , Config.REGIST_NAME + "=? AND " + Config.REGIST_PASSWORD + "= ?"
                , new String[]{objectConfig.getAccountName(), objectConfig.getAccountPassword()}
                , null
                , null
                , null);
        if (cursorAcount.moveToNext()) {
            Cursor cursorCar = db.query(Config.TABLE_CAR_INFOR
                    , null
                    , Config.REGIST_NAME + "= ? AND " + Config.LOGOUT_TIME + "= ?"
                    , new String[]{objectConfig.getAccountName(), Config.LOGOUT_TIME_DEFAULT}
                    , null
                    , null
                    , null);
            if (cursorCar.moveToLast()) {
                objectConfig.setBookingSpaceId(cursorCar.getString(5));
                objectConfig.setCarID(cursorCar.getString(1));
                objectConfig.setBondStatus(cursorCar.getString(6));
            }
            cursorCar.close();
        } else {
            objectConfig.setAccountName(null);
            objectConfig.setAccountPassword(null);
        }
        cursorAcount.close();
        return objectConfig;
    }

    public static ObjectConfig parkingCheck(ObjectConfig objectConfig) {
        SQLiteDatabase db = DataManager.getInstance().getDb();
        Cursor cursor = db.query(Config.TABLE_CAR_INFOR
                , new String[]{Config.NUMBER, Config.CAR_ID, Config.LOGOUT_TIME, Config.COST}
                , Config.PACKING_SPACE_ID + "= ? AND " + Config.LOGOUT_TIME + "= ?"
                , new String[]{objectConfig.getBookingSpaceId(), Config.LOGOUT_TIME_DEFAULT}
                , null
                , null
                , null);
        while (cursor.moveToNext()) {
            db.execSQL("update " + Config.TABLE_CAR_INFOR +
                    " set " + Config.PACKING_CHECK + " = '" + Config.OK +
                    "' where " + Config.NUMBER + " = '" + cursor.getString(0) + "'");
            objectConfig.setUdpResponseMsg(Config.PACKING_CHECK_SET);
            return objectConfig;
        }
        objectConfig.setUdpResponseMsg(Config.PACKING_CHECK_RELEASE);
        cursor.close();
        return objectConfig;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static ObjectConfig carConfirmOut(ObjectConfig objectConfig) {
        SQLiteDatabase db = DataManager.getInstance().getDb();
        Cursor cursor = db.query(Config.TABLE_CAR_INFOR
                , new String[]{Config.NUMBER, Config.PACKING_SPACE_ID}
                , Config.CAR_ID + "= ? AND " + Config.LOGOUT_TIME + "= ?"
                , new String[]{objectConfig.getCarID(), Config.LOGOUT_TIME_DEFAULT}
                , null
                , null
                , null);
        while (cursor.moveToNext()) {
            Date confirmOutTime = new Date(System.currentTimeMillis());
            timeFormat.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
            timeFormat.format(confirmOutTime);
            db.execSQL("update " + Config.TABLE_CAR_INFOR + " set " + Config.COST + " = '" + objectConfig.getCost()
                    + "' , " + Config.LOGOUT_TIME + " = '" + timeFormat.format(confirmOutTime) +
                    "' where " + Config.NUMBER + " = '" + cursor.getString(0) + "'");
            db.execSQL("update " + Config.TABLE_PACKING_SPACE + " set " + Config.PACKING_SPACE_STATUS + " = '" + Config.DEFAULT_PACKING_STATUS
                    + "' where " + Config.PACKING_SPACE_ID + " = '" + cursor.getString(1) + "'");
            objectConfig.setBaseResponse(Config.OK);
            objectConfig.setUdpResponseMsg(Config.PACKING_CHECK_RELEASE);
            return feedBackToDevice(objectConfig, cursor.getString(1));
        }
        objectConfig.setBaseResponse(Config.FAIL);
        cursor.close();
        return objectConfig;
    }

    private static ObjectConfig feedBackToDevice(ObjectConfig objectConfig, String parkingSpaceId) {
        SQLiteDatabase db = DataManager.getInstance().getDb();
        Cursor cursor = db.query(Config.TABLE_PACKING_SPACE
                , new String[]{Config.PACKING_DEVICE_ADDRESS}
                , Config.PACKING_SPACE_ID + "= ?"
                , new String[]{parkingSpaceId}
                , null
                , null
                , null);
        while (cursor.moveToNext()) {
            try {
                objectConfig.setGetSocketIP(InetAddress.getByName(cursor.getString(0)));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return objectConfig;
    }

    public static Map<String, Integer> updatedBenifitChart() {
        Map<String, Integer> map = null;
        if (map == null) {
            map = new LinkedHashMap<>();
        }
        SQLiteDatabase db = DataManager.getInstance().getDb();
        Cursor cursor = db.query(Config.TABLE_CAR_INFOR
                , new String[]{Config.LOGOUT_TIME, Config.COST}
                , Config.LOGOUT_TIME + "!= ?"
                , new String[]{Config.LOGOUT_TIME_DEFAULT}
                , null
                , null
                , Config.LOGIN_TIME + " ASC");
        while (cursor.moveToNext()) {
            String spliteList[] = cursor.getString(0).split("\\s+");
            if (map.get(spliteList[0]) == null) {
                map.put(spliteList[0], Integer.parseInt(cursor.getString(1)));
            } else {
                int value = map.get(spliteList[0]);
                map.put(spliteList[0], value + Integer.parseInt(cursor.getString(1)));
            }
            Log.e(Config.TAG, "map key:" + spliteList[0]);
        }
        cursor.close();
        return map;
    }
}
