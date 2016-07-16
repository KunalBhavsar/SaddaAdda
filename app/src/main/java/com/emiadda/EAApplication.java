package com.emiadda;

import android.app.Application;

import com.emiadda.asynctasks.ServerRequestProcessingThread;
import com.emiadda.core.EAServerRequest;
import com.emiadda.interafaces.ServerRequestResponseObserver;
import com.emiadda.interafaces.ServerResponseSubscriber;
import com.emiadda.utils.AppPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Kunal on 06/07/16.
 */
public class EAApplication extends Application implements ServerRequestResponseObserver {

    List<ServerResponseSubscriber> serverResponseInterfaces;

    @Override
    public void onCreate() {
        super.onCreate();
        AppPreferences.init(this);
        ServerRequestProcessingThread.init(this);
        serverResponseInterfaces = new ArrayList<>();
    }


    @Override
    public void attach(ServerResponseSubscriber serverResponseSubscriber) {
        if(!serverResponseInterfaces.contains(serverResponseSubscriber)) {
            serverResponseInterfaces.add(serverResponseSubscriber);
        }
    }

    @Override
    public void dettach(ServerResponseSubscriber serverResponseSubscriber) {
        if(serverResponseInterfaces.contains(serverResponseSubscriber)) {
            serverResponseInterfaces.remove(serverResponseSubscriber);
        }
    }

    @Override
    public void notifyServerResponse(String response, int requestCode, int responseCode, int extraRequestId) {
        for (ServerResponseSubscriber serverResponseSubscriber : serverResponseInterfaces) {
            serverResponseSubscriber.responseReceived(response, requestCode, responseCode, extraRequestId);
        }
    }

    @Override
    public void addToServerRequest(int requestCode, int extraRequestCode, boolean highPriority, String... params) {
        EAServerRequest eaServerRequest = new EAServerRequest(requestCode, extraRequestCode, highPriority);
        if(params.length > 0) {
            eaServerRequest.setParams(params);
        }
        ServerRequestProcessingThread.getInstance().addServerRequeset(eaServerRequest);
    }

}
