package com.emiadda.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kunal on 16/07/16.
 */
public class EAServerRequest {

    public static final int PRIORITY_HIGH = 10;
    public static final int PRIORITY_MEDIUM = 5;
    public static final int PRIORITY_LOW = 1;

    int requestCode;
    int extraRequestCode;
    int priority;
    List<String> params;

    public EAServerRequest(int requestCode, int extraRequestCode, int priority) {
        this.requestCode = requestCode;
        this.extraRequestCode = extraRequestCode;
        this.priority = priority;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public int getExtraRequestCode() {
        return extraRequestCode;
    }

    public void setExtraRequestCode(int extraRequestCode) {
        this.extraRequestCode = extraRequestCode;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(String... params) {
        this.params = new ArrayList<>();
        for (int i = 0; i < params.length; i++) {
            this.params.add(params[i]);
        }
    }
}
