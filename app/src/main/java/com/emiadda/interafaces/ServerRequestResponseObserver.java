package com.emiadda.interafaces;

import com.emiadda.server.ServerResponse;

/**
 * Created by Kunal on 16/07/16.
 */
public interface ServerRequestResponseObserver {
    void attach(ServerResponseSubscriber serverResponseInterface);
    void dettach(ServerResponseSubscriber serverResponseSubscriber);

    void notifyServerResponse(ServerResponse response, int requestCode, String activityTag, int extraRequestId);
    void addToServerRequest(int requestCode, int extraRequestCode, int priority, String activityTag, String... params);
}
