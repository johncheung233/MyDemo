package zhangchongantest.neu.edu.graduate_client.SocketConnect;

import android.content.SharedPreferences;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import zhangchongantest.neu.edu.graduate_client.ObjectConfig;

/**
 * Created by Cheung SzeOn on 2019/1/22.
 */

public class ConnectManager {
    private int listenningPort;
    private String serverIP;
    private ObjectConfig objectConfig;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private Socket socket;
    private ConnectManager(){}

    public static ConnectManager getInstance(){
        return ConnectManagerHolder.connectManager;
    }

    private static class ConnectManagerHolder{
        private static ConnectManager connectManager = new ConnectManager();
    }

    public int getListenningPort() {
        return listenningPort;
    }

    public void setListenningPort(int listenningPort) {
        this.listenningPort = listenningPort;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public synchronized ObjectConfig getPersonalObjectConfig() {
        return objectConfig;
    }

    public void setPersonalObjectConfig(ObjectConfig objectConfig) {
        this.objectConfig = objectConfig;
    }

    public SharedPreferences getSharedPreferences() {
        return sp;
    }

    public void setSharedPreferences(SharedPreferences sp) {
        this.sp = sp;
        setEditor(sp.edit());
    }

    public SharedPreferences.Editor getEditor() {
        return editor;
    }

    private void setEditor(SharedPreferences.Editor editor) {
        this.editor = editor;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
