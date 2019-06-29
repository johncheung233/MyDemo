package zhangchongantest.neu.edu.testcommunication;

import android.os.Parcel;
import android.os.Parcelable;

public class Account implements Parcelable {
    private String name;
    private String password;

    public Account(String name, String password) {
        this.name = name;
        this.password = password;
    }

    protected Account(Parcel in) {
        this.name = in.readString();
        this.password = in.readString();
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(password);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
