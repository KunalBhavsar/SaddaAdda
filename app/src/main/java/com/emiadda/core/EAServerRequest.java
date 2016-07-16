package com.emiadda.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kunal on 16/07/16.
 */
public class EAServerRequest {
    int requestCode;
    int extraRequestCode;
    boolean highPriority;
    List<String> params;

    public EAServerRequest(int requestCode, int extraRequestCode, boolean highPriority) {
        this.requestCode = requestCode;
        this.extraRequestCode = extraRequestCode;
        this.highPriority = highPriority;
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

    public boolean isHighPriority() {
        return highPriority;
    }

    public void setHighPriority(boolean highPriority) {
        this.highPriority = highPriority;
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
