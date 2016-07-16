package com.emiadda.interafaces;

/**
 * Created by Kunal on 04/07/16.
 */
public interface ServerResponseSubscriber {
    int RESPONSE_CODE_OK = 1;
    int RESPONSE_CODE_EXCEPTION = 2;

    void responseReceived(String response, int requestCode, int responseCode, int extraRequestCode);
}
