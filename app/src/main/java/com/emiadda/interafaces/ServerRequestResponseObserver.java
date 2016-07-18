package com.emiadda.interafaces;

/**
 * Created by Kunal on 16/07/16.
 */
public interface ServerRequestResponseObserver {
    void attach(ServerResponseSubscriber serverResponseInterface);
    void dettach(ServerResponseSubscriber serverResponseSubscriber);

    void notifyServerResponse(String response, int requestCode, int responseCode, int extraRequestId);
    void addToServerRequest(int requestCode, int extraRequestCode, int priority, String... params);
}
