package com.emiadda.core;

/**
 * Created by Rashmi on 02/07/16.
 */
public class EAAddress extends EABase {
    private String userName;
    private String address;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
