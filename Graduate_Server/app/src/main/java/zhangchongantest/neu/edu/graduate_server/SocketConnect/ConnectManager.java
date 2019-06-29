package zhangchongantest.neu.edu.graduate_server.SocketConnect;

import java.util.ArrayList;
import java.util.List;

import zhangchongantest.neu.edu.graduate_server.ObjectConfig;

/**
 * Created by Cheung SzeOn on 2019/1/22.
 */

public class ConnectManager {
    private int listenningPort;
    private volatile List<ObjectConfig> list = new ArrayList<>();
    private volatile List<ObjectConfig> responseList = new ArrayList<>();
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

    public List<ObjectConfig> getRequestWaitingList() {
        return list;
    }

    public void setRequestWaitingList(List<ObjectConfig> list) {
        this.list = list;
    }

    public List<ObjectConfig> getResponseWaitingList() {
        return responseList;
    }

    public void setResponseWaitingList(List<ObjectConfig> responseList) {
        this.responseList = responseList;
    }
}
