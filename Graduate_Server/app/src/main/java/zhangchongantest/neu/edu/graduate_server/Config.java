package zhangchongantest.neu.edu.graduate_server;

/**
 * Created by Cheung SzeOn on 2019/1/22.
 */

public class Config {
    public static final String TABLE_PACKING_SPACE = "TableOfPackingSpace";
    public static final String PACKING_SPACE_ID = "ParkingSpaceId";
    public static final String PACKING_SPACE_STATUS = "ParkingSpaceStatus";
    public static final String PACKING_DEVICE_ADDRESS= "ParkingDeviceAddress";
    public static final String DEFAULT_PACKING_STATUS = "EMPTY";
    public static final String OCCUPY_PACKING_STATUS = "OCCUPY";
    public static final String PACKING_DEVICE_ID = "ParkingDeviceId";

    public static final String TABLE_CAR_INFOR = "TableOfCarInfor";
    public static final String CAR_ID = "CarLicenseNumber";
    public static final String CAR_TYPE = "CarType";
    public static final String LOGIN_TIME = "LoginTime";
    public static final String LOGOUT_TIME = "LogoutTime";
    public static final String COST = "Cost";
    public static final String PACKING_CHECK = "PackingCheck";

    public static final String TABLE_REGIST = "TableOfRegist";
    public static final String REGIST_NAME = "RegistName";
    public static final String REGIST_PASSWORD = "RegistPassword";
    public static final String ONLINE_STATUS = "OnlineType";
    public static final String REGIST_AUTHORITY = "RegistAuthority";

    public static final String NUMBER = "_id";
    public static final int CMD_REGISTER_IN = 5;
    public static final int CMD_REGISTER_LOGOUT = 4;
    public static final int CMD_REGISTER_LOGIN = 3;
    public static final int CMD_CAR_IN = 1;
    public static final int CMD_PRE_OUT = 2;
    public static final int CMD_PARKING_CHECK = 6;
    public static final int CMD_CAR_OUT = 7;
    public static final int CMD_EMPTY_DATA = 0;
    public static final int CMD_INIT_CHECK = 8;

    public static final String OK = "OK";
    public static final String EMPTY = "EMPTY";
    public static final String FAIL = "FAIL";

    public static final String ONLINE = "Online";
    public static final String OFFLINE = "Offline";

    public static final String REPEAT = "Repeat";
    public static final String FULL = "Full";

    public static final String TIME_SPLIT = ":";
    public static final String MSG_SPLIT = "_";
    public static final String TAG = "debug";

    public static final String End_char = "_E";

    public static final String RESPONSEMES = "ResponseMessage";
    public static final String SERVICEPREFERENCE = "ServicePreference";
    public static final String TOTALSPACE = "TotalSpace";
    public static final String LISTENINGPORT = "ListeningPort";

    public static final String SOCKETSEND = "SocketSendFlag";

    public static final String LOGOUT_TIME_DEFAULT = "@null";

    public static final String PACKING_CHECK_SET = "1";
    public static final String PACKING_CHECK_RELEASE = "2";

    public static final String UDP_TYPE = "UDP";
    public static final String TCP_TYPE = "TCP";

    public static final String INITCHECKNULL = "null";
}
