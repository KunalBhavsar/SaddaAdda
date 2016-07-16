package com.emiadda.asynctasks;

import android.content.Context;
import android.util.Log;

import com.emiadda.EAApplication;
import com.emiadda.core.EAServerRequest;
import com.emiadda.interafaces.ServerResponseSubscriber;
import com.emiadda.wsdl.categoriesAndProducts.VOKExtendedSoapSerializationEnvelope;
import com.emiadda.wsdl.categoriesAndProducts.VOKserverBinding;
import com.emiadda.wsdl.specialProducts.KESExtendedSoapSerializationEnvelope;
import com.emiadda.wsdl.specialProducts.KESserverBinding;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Kunal on 16/07/16.
 */
public class ServerRequestProcessingThread extends Thread {
    public static final int SHUTDOWN_REQUEST_CODE = -1;
    private static final String TAG = ServerRequestProcessingThread.class.getSimpleName();
    private static final String NAMESPACE = "http://www.mydevsystems.com";
    private static final String METHOD_NAME_GET_CATEGORY = "getCategories";
    private static final String METHOD_NAME_GET_PRODUCT_BY_PRODUCT_ID = "getProductByProductID";
    private static final String METHOD_NAME_GET_PRODUCT_IMAGE = "getProductImage";
    private static final String METHOD_NAME_GET_PRODUCTS_BY_CATEGORY = "getProductsByCategory";
    private static final String METHOD_NAME_GET_SPECIAL_PRODUCTS = "getSpecials";

    public static final int REQUEST_CODE_GET_CATEGORY = 1;
    public static final int REQUEST_CODE_GET_PRODUCT_BY_PRODUCT_ID = 2;
    public static final int REQUEST_CODE_GET_PRODUCT_IMAGE = 3;
    public static final int REQUEST_CODE_GET_PRODUCTS_BY_CATEGORY = 4;
    public static final int REQUEST_CODE_GET_SPECIAL_PRODUCTS = 5;

    private static ServerRequestProcessingThread thread;
    private EAApplication context;
    BlockingQueue<EAServerRequest> serverReuests;


    public ServerRequestProcessingThread(EAApplication context) {
        this.context = context;
        serverReuests = new ArrayBlockingQueue<>(20);
    }

    public static void init(EAApplication context) {
        thread = new ServerRequestProcessingThread(context);
        thread.start();
    }

    public static ServerRequestProcessingThread getInstance() {
        return thread;
    }

    public void addServerRequeset(EAServerRequest serverRequest) {
        if (shuttingDown || loggerTerminated) return;

        try {
            if (serverRequest.isHighPriority()) {
                serverReuests.put(serverRequest);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public void shutDown() throws InterruptedException {
        shuttingDown = true;
        serverReuests.put(new EAServerRequest(SHUTDOWN_REQUEST_CODE, -1, true));
    }

    private volatile boolean shuttingDown, loggerTerminated;
    // Sit in a loop, pulling strings off the queue and logging
    @Override
    public void run() {
        try {
            EAServerRequest item;
            while ((item = serverReuests.take()).getRequestCode() != SHUTDOWN_REQUEST_CODE) {
                switch (item.getRequestCode()) {
                    case REQUEST_CODE_GET_CATEGORY :
                        getCategories(item);
                        break;
                    case REQUEST_CODE_GET_PRODUCT_BY_PRODUCT_ID :
                        getProductByProductId(item);
                        break;
                    case REQUEST_CODE_GET_PRODUCT_IMAGE :
                        getProductImage(item);
                        break;
                    case REQUEST_CODE_GET_PRODUCTS_BY_CATEGORY :
                        getProductByCategory(item);
                        break;
                    case REQUEST_CODE_GET_SPECIAL_PRODUCTS :
                        getSpecialProducts(item);
                        break;
                }
            }
        }
        catch (InterruptedException iex) {
            Log.e(TAG, iex.getMessage(), iex);
        }
        finally {
            loggerTerminated = true;
        }
    }

    private void getCategories(EAServerRequest eaServerRequest) {
        try {
            //Using easysoap
            VOKserverBinding VOKServerBinding = new VOKserverBinding();

            //Using soap standard way
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_GET_CATEGORY);

            PropertyInfo propertyId = new PropertyInfo();
            propertyId.setValue(0);
            propertyId.setType(Integer.class);
            request.addProperty(propertyId);

            VOKExtendedSoapSerializationEnvelope soapEnvelope = new VOKExtendedSoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.encodingStyle = SoapEnvelope.ENC;
            soapEnvelope.dotNet = false;
            soapEnvelope.bodyOut = request;

            String response = VOKServerBinding.getCategories(eaServerRequest.getParams().size() > 0 ? eaServerRequest.getParams().get(0) : null);
            if(response != null && !response.isEmpty()) {
                context.notifyServerResponse(response, eaServerRequest.getRequestCode(), ServerResponseSubscriber.RESPONSE_CODE_OK, eaServerRequest.getExtraRequestCode());
            }
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            context.notifyServerResponse(null, eaServerRequest.getRequestCode(), ServerResponseSubscriber.RESPONSE_CODE_EXCEPTION, eaServerRequest.getExtraRequestCode());
        }
    }

    private void getProductByProductId(EAServerRequest serverRequest) {
        try {
            //Using easysoap
            com.emiadda.wsdl.categoriesAndProducts.VOKserverBinding vokServerBinding = new com.emiadda.wsdl.categoriesAndProducts.VOKserverBinding();

            //Using soap standard way
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_GET_PRODUCT_BY_PRODUCT_ID);

            PropertyInfo propertyId = new PropertyInfo();
            propertyId.setValue(0);
            propertyId.setType(Integer.class);
            request.addProperty(propertyId);

            com.emiadda.wsdl.categoriesAndProducts.VOKExtendedSoapSerializationEnvelope soapEnvelope = new com.emiadda.wsdl.categoriesAndProducts.VOKExtendedSoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.encodingStyle = SoapEnvelope.ENC;
            soapEnvelope.dotNet = false;
            soapEnvelope.bodyOut = request;

            String response = vokServerBinding.getProductByProductID(serverRequest.getParams().size() > 0 ? serverRequest.getParams().get(0) : null);
            if(response != null && !response.isEmpty()) {
                context.notifyServerResponse(response, serverRequest.getRequestCode(), ServerResponseSubscriber.RESPONSE_CODE_OK, serverRequest.getExtraRequestCode());
            }
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            context.notifyServerResponse(null, serverRequest.getRequestCode(), ServerResponseSubscriber.RESPONSE_CODE_EXCEPTION, serverRequest.getExtraRequestCode());
        }
    }

    private void getProductImage(EAServerRequest eaServerRequest) {
        try {
            //Using easysoap
            VOKserverBinding abmServerBinding = new VOKserverBinding();

            //Using soap standard way
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_GET_PRODUCT_IMAGE);

            PropertyInfo propertyId = new PropertyInfo();
            propertyId.setValue(0);
            propertyId.setType(Integer.class);
            request.addProperty(propertyId);

            VOKExtendedSoapSerializationEnvelope soapEnvelope = new VOKExtendedSoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.encodingStyle = SoapEnvelope.ENC;
            soapEnvelope.dotNet = false;
            soapEnvelope.bodyOut = request;

            String response = abmServerBinding.getProductImage(eaServerRequest.getParams().size() > 0 ? eaServerRequest.getParams().get(0) : null);
            if(response != null && !response.isEmpty()) {
                context.notifyServerResponse(response, eaServerRequest.getRequestCode(), ServerResponseSubscriber.RESPONSE_CODE_OK, eaServerRequest.getExtraRequestCode());
            }
        }
        catch (Exception e) {
            context.notifyServerResponse(null, eaServerRequest.getRequestCode(), ServerResponseSubscriber.RESPONSE_CODE_EXCEPTION, eaServerRequest.getExtraRequestCode());
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void getProductByCategory(EAServerRequest eaServerRequest) {
        try {
            //Using easysoap
            VOKserverBinding abmServerBinding = new VOKserverBinding();

            //Using soap standard way
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_GET_PRODUCTS_BY_CATEGORY);

            PropertyInfo propertyId = new PropertyInfo();
            propertyId.setValue(0);
            propertyId.setType(Integer.class);
            request.addProperty(propertyId);

            VOKExtendedSoapSerializationEnvelope soapEnvelope = new VOKExtendedSoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.encodingStyle = SoapEnvelope.ENC;
            soapEnvelope.dotNet = false;
            soapEnvelope.bodyOut = request;

            String response =  abmServerBinding.getProductsByCategory(eaServerRequest.getParams().size() > 0 ? eaServerRequest.getParams().get(0) : null);
            if(response != null && !response.isEmpty()) {
                context.notifyServerResponse(response, eaServerRequest.getRequestCode(), ServerResponseSubscriber.RESPONSE_CODE_OK, eaServerRequest.getExtraRequestCode());
            }

        }
        catch (Exception e) {
            context.notifyServerResponse(null, eaServerRequest.getRequestCode(), ServerResponseSubscriber.RESPONSE_CODE_EXCEPTION, eaServerRequest.getExtraRequestCode());
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void getSpecialProducts(EAServerRequest eaServerRequest) {
        try {
            //Using easysoap
            KESserverBinding abmServerBinding = new KESserverBinding();

            //Using soap standard way
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_GET_SPECIAL_PRODUCTS);

            PropertyInfo propertyId = new PropertyInfo();
            propertyId.setValue(0);
            propertyId.setType(Integer.class);
            request.addProperty(propertyId);

            KESExtendedSoapSerializationEnvelope soapEnvelope = new KESExtendedSoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.encodingStyle = SoapEnvelope.ENC;
            soapEnvelope.dotNet = false;
            soapEnvelope.bodyOut = request;

            String response =  abmServerBinding.getSpecials();
            if(response != null && !response.isEmpty()) {
                context.notifyServerResponse(response, eaServerRequest.getRequestCode(), ServerResponseSubscriber.RESPONSE_CODE_OK, eaServerRequest.getExtraRequestCode());
            }
        }
        catch (Exception e) {
            context.notifyServerResponse(null, eaServerRequest.getRequestCode(), ServerResponseSubscriber.RESPONSE_CODE_EXCEPTION, eaServerRequest.getExtraRequestCode());
            Log.e(TAG, e.getMessage(), e);
        }
    }
}

