package zhangchongantest.neu.edu.graduate_client;

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
    private String packingDeviceId;
    private String carID;
    private String carType;
    private String accountName;
    private String accountPassword;
    private String accountType;
    private String registerResponse;
    private String time;
    private String cost;
    private String loginTime;
    private String bondStatus;
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

    public String getRegisterResponse() {
        return registerResponse;
    }

    public void setRegisterResponse(String registerResponse) {
        this.registerResponse = registerResponse;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getPackingDeviceId() {
        return packingDeviceId;
    }

    public void setPackingDeviceId(String packingDeviceId) {
        this.packingDeviceId = packingDeviceId;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getBondStatus() {
        return bondStatus;
    }

    public void setBondStatus(String bondStatus) {
        this.bondStatus = bondStatus;
    }
}
