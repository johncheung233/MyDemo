package zhangchongantest.neu.edu.graduate_client;

/**
 * Created by Cheung SzeOn on 2019/1/22.
 */

public class Config {

    public static final String CMD_REGISTER_IN = "5";
    public static final String CMD_REGISTER_LOGOUT = "4";
    public static final String CMD_REGISTER_LOGIN = "3";
    public static final String CMD_CAR_IN = "1";
    public static final String CMD_PRE_OUT = "2";
    public static final String CMD_PARKING_CHECK = "6";
    public static final String CMD_CAR_OUT = "7";
    public static final String CMD_INIT_CHECK = "8";

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

    public static final String RECEIVE_FLAG = "ReceiveMsgFlag";

    public static final String RESPONSEMES = "ResponseMessage";
    public static final String CLIENTPERFERENCE = "ClientPreference";
    public static final String SERVICEIP = "SERVICEIP";
    public static final String LISTENINGPORT = "ListeningPort";
    public static final String SP_ACCOUNTNAME = "SP_accoutName";
    public static final String SP_ACCOUNTPASSWORD = "SP_accoutPassword";
    public static final String SP_CARID = "SP_carid";
    public static final String SP_PARKINGID = "SP_parkingid";

    public static final String SOCKETSEND = "SocketSendFlag";

    public static final String LARGE_TYPE = "大型车";
    public static final String NORMAL_TYPE = "中型车";
    public static final String SMALL_TYPE = "小型车";

    public static final String CARID_FIRST = "粤_京_津_沪_渝_冀_豫_云_辽_黑_湘_皖_鲁_新_苏_浙_赣_鄂_桂_甘_晋_蒙_陕_吉_闽_贵_青_藏_川_宁_琼_使_领";

    public static final int LoginRquestCode = 1;
    public static final int LoginResultCode = 2;

    public static final String BOND_PREPARE_ACTION = "android.bluetooth.device.action.PAIRING_REQUEST";

    public static final int BLUETOOTH_ENABLE_REQUEST = 1;

    public static int SEARCHFINISH = 1;

    public static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    public static final String End_char = "_E";

    public static final String BLTRETURN ="BLTRETURN";

    public static final String MAIN_TAG = "MainFragmentTag";
    public static final String MINE_TAG = "MineFragmentTag";

    public static final int START_BLUETOOTH_ACTIVITY = 0;
    public static final int START_SETTING_ACTIVITY = 1;

    public static final int SET_LOGOUT_COMMAND = 0;
    public static final int SET_PARKOUT_COMMAND = 1;
    public static final int SET_INITCHECK_COMMAND = 2;

    public static final String INITCHECKNULL = "null";
}
