package com.emiadda;

import android.app.Application;
import android.content.Context;

import com.emiadda.asynctasks.ServerRequestProcessingThread;
import com.emiadda.core.EAServerRequest;
import com.emiadda.interafaces.ServerRequestResponseObserver;
import com.emiadda.interafaces.ServerResponseSubscriber;
import com.emiadda.utils.AppPreferences;
import com.emiadda.wsdl.ProductModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Kunal on 06/07/16.
 */
public class EAApplication extends Application implements ServerRequestResponseObserver {

    List<ServerResponseSubscriber> serverResponseInterfaces;
    static Context mAppContext;

    private static ProductModel transientSelectedProductModel;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = this;
        AppPreferences.init(this);
        ServerRequestProcessingThread.init(this);
        serverResponseInterfaces = new ArrayList<>();
    }

    private static Context getInstance() {
        return mAppContext;
    }

    public static ProductModel getTransientSelectedProductModel() {
        return transientSelectedProductModel;
    }

    public static void setTransientSelectedProductModel(ProductModel selectedProductModel) {
        transientSelectedProductModel = selectedProductModel;
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
    public void notifyServerResponse(String response, int requestCode, int responseCode, String activityTag, int extraRequestId) {
        for (ServerResponseSubscriber serverResponseSubscriber : serverResponseInterfaces) {
            serverResponseSubscriber.responseReceived(response, requestCode, responseCode, extraRequestId, activityTag);
        }
    }

    @Override
    public void addToServerRequest(int requestCode, int extraRequestCode, int priority, String activityTag, String... params) {
        EAServerRequest eaServerRequest = new EAServerRequest(requestCode, extraRequestCode, priority);
        eaServerRequest.setActivityTag(activityTag);
        if(params.length > 0) {
            eaServerRequest.setParams(params);
        }
        ServerRequestProcessingThread.getInstance().addServerRequeset(eaServerRequest);
    }

    public static void makeServerRequest(int requestCode, int extraRequestCode, int priority, String activityTag, String... params) {
        EAServerRequest eaServerRequest = new EAServerRequest(requestCode, extraRequestCode, priority);
        eaServerRequest.setActivityTag(activityTag);
        if(params.length > 0) {
            eaServerRequest.setParams(params);
        }
        ServerRequestProcessingThread.getInstance().addServerRequeset(eaServerRequest);
    }
}
