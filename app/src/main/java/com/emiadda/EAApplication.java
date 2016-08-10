package com.emiadda;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.emiadda.asynctasks.ServerRequestProcessingThread;
import com.emiadda.core.EAPlaceOrderRequeset;
import com.emiadda.core.EAServerRequest;
import com.emiadda.interafaces.ServerRequestResponseObserver;
import com.emiadda.interafaces.ServerResponseSubscriber;
import com.emiadda.server.ServerResponse;
import com.emiadda.server.VectorProductsParams;
import com.emiadda.server.VectorTotalParams;
import com.emiadda.server.OrderParams;
import com.emiadda.utils.AppPreferences;
import com.emiadda.utils.AppUtils;
import com.emiadda.wsdl.ProductModel;

import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.List;

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
        Fabric.with(this, new Crashlytics());
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
    public void notifyServerResponse(ServerResponse response, int requestCode, String activityTag, int extraRequestId) {
        for (ServerResponseSubscriber serverResponseSubscriber : serverResponseInterfaces) {
            serverResponseSubscriber.responseReceived(response, requestCode, extraRequestId, activityTag);
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
        if (params.length > 0) {
            eaServerRequest.setParams(params);
        }
        ServerRequestProcessingThread.getInstance().addServerRequeset(eaServerRequest);
    }

    public static void makePlaceOrderServerRequest(int requestCode, int extraRequestCode, int priority, String activityTag, OrderParams orderparams,
                                  VectorProductsParams vectorproductsparams, VectorTotalParams vectortotalparams) {
        EAPlaceOrderRequeset eaServerRequest = new EAPlaceOrderRequeset(requestCode, extraRequestCode, priority,
                orderparams, vectorproductsparams, vectortotalparams);
        eaServerRequest.setActivityTag(activityTag);
        ServerRequestProcessingThread.getInstance().addServerRequeset(eaServerRequest);
    }
}
