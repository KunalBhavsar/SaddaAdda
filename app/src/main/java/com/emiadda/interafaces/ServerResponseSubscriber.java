package com.emiadda.interafaces;

import com.emiadda.server.ServerResponse;

/**
 * Created by Kunal on 04/07/16.
 */
public interface ServerResponseSubscriber {
    void responseReceived(ServerResponse response, int requestCode, int extraRequestCode, String activityTag);
}
