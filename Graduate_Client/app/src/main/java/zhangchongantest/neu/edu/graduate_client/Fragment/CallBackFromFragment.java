package zhangchongantest.neu.edu.graduate_client.Fragment;

/**
 * Created by Cheung SzeOn on 2019/3/30.
 */

public interface CallBackFromFragment {
    void showWarningDialog(String msg);
    void dissWarningDialog();
    void showLoadingDialog();
    void dissLoadingDialog();
    void setCommandAndSend(int cmd, String arg1,String arg2,String dialogTitle);
    void startOtherActivity(int arg);
}
