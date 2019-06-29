package zhangchongantest.neu.edu.graduate_server;

import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Cheung SzeOn on 2019/1/22.
 */

public class ObjectConfig {
    private String cmd;
    private InetAddress getSocketIP;
    private String requestMsg;
    private String responseMsg;
    private String bookingSpaceId;
    private String ParkingDeviceId;
    private String carID;
    private String carType;
    private String accountName;
    private String accountPassword;
    private String accountType;
    private String baseResponse;
    private String time;
    private String loginTime;
    private String cost;
    private String parkingCheck;
    private Socket socket;
    private String socketType;
    private String bondStatus;
    private String udpResponse;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getCarID() {
        return carID;
    }

    public void setCarID(String carID) {
        this.carID = carID;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public InetAddress getGetSocketIP() {
        return getSocketIP;
    }

    public void setGetSocketIP(InetAddress getSocketIP) {
        this.getSocketIP = getSocketIP;
    }

    public String getRequestMsg() {
        return requestMsg;
    }

    public void setRequestMsg(String requestMsg) {
        this.requestMsg = requestMsg;
    }

    public String getBookingSpaceId() {
        return bookingSpaceId;
    }

    public void setBookingSpaceId(String bookingSpaceId) {
        this.bookingSpaceId = bookingSpaceId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountPassword() {
        return accountPassword;
    }

    public void setAccountPassword(String accountPassword) {
        this.accountPassword = accountPassword;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getBaseResponse() {
        return baseResponse;
    }

    public void setBaseResponse(String baseResponse) {
        this.baseResponse = baseResponse;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    public String getParkingDeviceId() {
        return ParkingDeviceId;
    }

    public void setParkingDeviceId(String packingDeviceId) {
        ParkingDeviceId = packingDeviceId;
    }

    public String getParkingCheck() {
        return parkingCheck;
    }

    public void setParkingCheck(String parkingCheck) {
        this.parkingCheck = parkingCheck;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String logoutTime) {
        this.loginTime = logoutTime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSocketType() {
        return socketType;
    }

    public void setSocketType(String socketType) {
        this.socketType = socketType;
    }

    public String getBondStatus() {
        return bondStatus;
    }

    public void setBondStatus(String bondStatus) {
        this.bondStatus = bondStatus;
    }

    public String getUdpResponseMsg() {
        return udpResponse;
    }

    public void setUdpResponseMsg(String udpResponse) {
        this.udpResponse = udpResponse;
    }
}
