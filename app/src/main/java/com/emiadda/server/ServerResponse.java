package com.emiadda.server;

import java.io.Serializable;

/**
 * Created by Kunal on 08/08/16.
 */
public class ServerResponse implements Serializable {
    private static final long serialVersionUID = 0L;

    public static final int SERVER_OK = 1;
    public static final int SERVER_ERROR = 2;
    public static final int NETWORK_ERROR = 3;

    private String response;
    private String error;
    private int responseStatus;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }
}
