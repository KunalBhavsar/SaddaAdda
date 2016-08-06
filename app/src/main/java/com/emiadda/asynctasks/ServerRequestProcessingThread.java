package com.emiadda.asynctasks;

import android.util.Log;

import com.emiadda.EAApplication;
import com.emiadda.core.EAPlaceOrderRequeset;
import com.emiadda.core.EAServerRequest;
import com.emiadda.interafaces.ServerResponseSubscriber;
import com.emiadda.server.GetCategoriesWSDL;
import com.emiadda.server.VectorProductsParams;
import com.emiadda.server.VectorTotalParams;
import com.emiadda.server.OrderParams;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Kunal on 16/07/16.
 */
public class ServerRequestProcessingThread extends Thread {
    public static final int SHUTDOWN_REQUEST_CODE = -1;
    private static final String TAG = ServerRequestProcessingThread.class.getSimpleName();

    public static final int REQUEST_CODE_GET_CATEGORY = 1;
    public static final int REQUEST_CODE_GET_PRODUCT_BY_PRODUCT_ID = 2;
    public static final int REQUEST_CODE_GET_PRODUCT_IMAGE = 3;
    public static final int REQUEST_CODE_GET_PRODUCTS_BY_CATEGORY = 4;
    public static final int REQUEST_CODE_GET_SPECIAL_PRODUCTS = 5;
    public static final int REQUEST_CODE_PLACE_ORDER = 6;

    private static ServerRequestProcessingThread thread;
    private EAApplication context;
    LinkedBlockingDeque<EAServerRequest> serverRequestLinkedBlockingDeque;
    List<EAServerRequest> tempServerRequestList;

    public ServerRequestProcessingThread(EAApplication context) {
        this.context = context;

        serverRequestLinkedBlockingDeque = new LinkedBlockingDeque<>(50);
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
            int existingServerRequestSize = serverRequestLinkedBlockingDeque.size();

            if (existingServerRequestSize > 0) {
                if(serverRequestLinkedBlockingDeque.getFirst().getPriority() == serverRequest.getPriority() &&
                        serverRequestLinkedBlockingDeque.getLast().getPriority() == serverRequest.getPriority()) {
                    serverRequestLinkedBlockingDeque.putLast(serverRequest);
                }
                if (serverRequestLinkedBlockingDeque.getFirst().getPriority() < serverRequest.getPriority()) {
                    serverRequestLinkedBlockingDeque.putFirst(serverRequest);
                }
                else if(serverRequestLinkedBlockingDeque.getLast().getPriority() > serverRequest.getPriority()) {
                    serverRequestLinkedBlockingDeque.putLast(serverRequest);
                }
                else {
                    tempServerRequestList = new ArrayList<>();
                    tempServerRequestList.addAll(serverRequestLinkedBlockingDeque);
                    serverRequestLinkedBlockingDeque.clear();
                    for (int i = 0; i < tempServerRequestList.size(); i++) {
                        if(tempServerRequestList.get(i).getPriority() < serverRequest.getPriority()) {
                            tempServerRequestList.add(i, serverRequest);
                            break;
                        }
                    }
                    serverRequestLinkedBlockingDeque.addAll(tempServerRequestList);
                    tempServerRequestList = null;
                }
            } else {
                serverRequestLinkedBlockingDeque.put(serverRequest);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public void shutDown() throws InterruptedException {
        shuttingDown = true;
        serverRequestLinkedBlockingDeque.putFirst(new EAServerRequest(SHUTDOWN_REQUEST_CODE, -1, EAServerRequest.PRIORITY_HIGH));
    }

    private volatile boolean shuttingDown, loggerTerminated;

    // Sit in a loop, pulling strings off the queue and logging
    @Override
    public void run() {
        try {
            EAServerRequest item;
            while ((item = serverRequestLinkedBlockingDeque.take()).getRequestCode() != SHUTDOWN_REQUEST_CODE) {
                switch (item.getRequestCode()) {
                    case REQUEST_CODE_GET_CATEGORY:
                        getCategories(item);
                        break;
                    case REQUEST_CODE_GET_PRODUCT_BY_PRODUCT_ID:
                        getProductByProductId(item);
                        break;
                    case REQUEST_CODE_GET_PRODUCT_IMAGE:
                        getProductImage(item);
                        break;
                    case REQUEST_CODE_GET_PRODUCTS_BY_CATEGORY:
                        getProductByCategory(item);
                        break;
                    case REQUEST_CODE_GET_SPECIAL_PRODUCTS:
                        getSpecialProducts(item);
                        break;
                    case REQUEST_CODE_PLACE_ORDER:
                        Log.i(TAG,"Found request for place order");
                        placeOrder((EAPlaceOrderRequeset) item);
                        break;
                }
            }
        } catch (InterruptedException iex) {
            Log.e(TAG, iex.getMessage(), iex);
        } finally {
            loggerTerminated = true;
        }
    }

    private void getCategories(EAServerRequest eaServerRequest) {
        try {
            String response = new GetCategoriesWSDL().getCategories(eaServerRequest.getParams().size() > 0 ? eaServerRequest.getParams().get(0) : null);

            if (response != null && !response.isEmpty()) {
                context.notifyServerResponse(response, eaServerRequest.getRequestCode(), ServerResponseSubscriber.RESPONSE_CODE_OK, eaServerRequest.getActivityTag(), eaServerRequest.getExtraRequestCode());
            } else {
                context.notifyServerResponse(response, eaServerRequest.getRequestCode(), ServerResponseSubscriber.RESPONSE_CODE_CANCEL, eaServerRequest.getActivityTag(), eaServerRequest.getExtraRequestCode());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            context.notifyServerResponse(null, eaServerRequest.getRequestCode(), ServerResponseSubscriber.RESPONSE_CODE_EXCEPTION, eaServerRequest.getActivityTag(), eaServerRequest.getExtraRequestCode());
        }
    }

    private void getProductByProductId(EAServerRequest eaServerRequest) {
        try {
            String response = new GetCategoriesWSDL().getProductByProductID(eaServerRequest.getParams().size() > 0 ? eaServerRequest.getParams().get(0) : null);

            if (response != null && !response.isEmpty()) {
                context.notifyServerResponse(response, eaServerRequest.getRequestCode(), ServerResponseSubscriber.RESPONSE_CODE_OK, eaServerRequest.getActivityTag(), eaServerRequest.getExtraRequestCode());
            } else {
                context.notifyServerResponse(response, eaServerRequest.getRequestCode(), ServerResponseSubscriber.RESPONSE_CODE_CANCEL, eaServerRequest.getActivityTag(), eaServerRequest.getExtraRequestCode());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            context.notifyServerResponse(null, eaServerRequest.getRequestCode(), ServerResponseSubscriber.RESPONSE_CODE_EXCEPTION, eaServerRequest.getActivityTag(), eaServerRequest.getExtraRequestCode());
        }
    }

    private void getProductImage(EAServerRequest eaServerRequest) {
        try {
            String response = new GetCategoriesWSDL().getProductImage(eaServerRequest.getParams().size() > 0 ? eaServerRequest.getParams().get(0) : null);

            if (response != null && !response.isEmpty()) {
                context.notifyServerResponse(response, eaServerRequest.getRequestCode(), ServerResponseSubscriber.RESPONSE_CODE_OK, eaServerRequest.getActivityTag(), eaServerRequest.getExtraRequestCode());
            } else {
                context.notifyServerResponse(response, eaServerRequest.getRequestCode(), ServerResponseSubscriber.RESPONSE_CODE_CANCEL, eaServerRequest.getActivityTag(), eaServerRequest.getExtraRequestCode());
            }
        } catch (Exception e) {
            context.notifyServerResponse(null, eaServerRequest.getRequestCode(), ServerResponseSubscriber.RESPONSE_CODE_EXCEPTION, eaServerRequest.getActivityTag(), eaServerRequest.getExtraRequestCode());
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void getProductByCategory(EAServerRequest eaServerRequest) {
        try {
            String response = new GetCategoriesWSDL().getProductsByCategory(eaServerRequest.getParams().size() > 0 ? eaServerRequest.getParams().get(0) : null);

            if (response != null && !response.isEmpty()) {
                context.notifyServerResponse(response, eaServerRequest.getRequestCode(), ServerResponseSubscriber.RESPONSE_CODE_OK, eaServerRequest.getActivityTag(), eaServerRequest.getExtraRequestCode());
            } else {
                context.notifyServerResponse(response, eaServerRequest.getRequestCode(), ServerResponseSubscriber.RESPONSE_CODE_CANCEL, eaServerRequest.getActivityTag(), eaServerRequest.getExtraRequestCode());
            }

        } catch (Exception e) {
            context.notifyServerResponse(null, eaServerRequest.getRequestCode(), ServerResponseSubscriber.RESPONSE_CODE_EXCEPTION, eaServerRequest.getActivityTag(), eaServerRequest.getExtraRequestCode());
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void getSpecialProducts(EAServerRequest eaServerRequest) {
        try {
            String response = new GetCategoriesWSDL().getSpecials(eaServerRequest.getParams().size() > 0 ? eaServerRequest.getParams().get(0) : null);

            if (response != null && !response.isEmpty()) {
                context.notifyServerResponse(response, eaServerRequest.getRequestCode(), ServerResponseSubscriber.RESPONSE_CODE_OK, eaServerRequest.getActivityTag(), eaServerRequest.getExtraRequestCode());
            } else {
                context.notifyServerResponse(response, eaServerRequest.getRequestCode(), ServerResponseSubscriber.RESPONSE_CODE_CANCEL, eaServerRequest.getActivityTag(), eaServerRequest.getExtraRequestCode());
            }
        } catch (Exception e) {
            context.notifyServerResponse(null, eaServerRequest.getRequestCode(), ServerResponseSubscriber.RESPONSE_CODE_EXCEPTION, eaServerRequest.getActivityTag(), eaServerRequest.getExtraRequestCode());
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void placeOrder(EAPlaceOrderRequeset eaServerRequest) {
        Log.i(TAG,"Started processing place order request");
        try {
            String response = new GetCategoriesWSDL().placeOrder(eaServerRequest.getOrderparams(),
                    eaServerRequest.getVectorproductsparams(), eaServerRequest.getVectortotalparams());
            Log.i(TAG, "place order response "+response);
            if (response != null && !response.isEmpty()) {
                context.notifyServerResponse(response, eaServerRequest.getRequestCode(), ServerResponseSubscriber.RESPONSE_CODE_OK, eaServerRequest.getActivityTag(), eaServerRequest.getExtraRequestCode());
            } else {
                context.notifyServerResponse(response, eaServerRequest.getRequestCode(), ServerResponseSubscriber.RESPONSE_CODE_CANCEL, eaServerRequest.getActivityTag(), eaServerRequest.getExtraRequestCode());
            }
        }
        catch (Exception e) {
            context.notifyServerResponse(null, eaServerRequest.getRequestCode(), ServerResponseSubscriber.RESPONSE_CODE_EXCEPTION, eaServerRequest.getActivityTag(), eaServerRequest.getExtraRequestCode());
            Log.e(TAG, e.getMessage(), e);
        }
        Log.i(TAG,"Processed place order request");
    }
}

