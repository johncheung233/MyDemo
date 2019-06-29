package zhangchongantest.neu.edu.graduate_test_sockt;

/**
 * Created by Cheung SzeOn on 2019/5/1.
 */

public class MessageEvent {
    public static final int CONNECT_SUCCESS = 1;
    public static final int CONNECT_FAIL = -1;
    public static final int GATT_FAIL = -2;
    public static final int WRITE_SUCCESS = 2;
    private int cmd;
    public MessageEvent() {
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }
}
