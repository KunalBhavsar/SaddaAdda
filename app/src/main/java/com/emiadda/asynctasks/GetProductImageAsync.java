package com.emiadda.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.emiadda.interafaces.ServerResponseSubscriber;
import com.emiadda.wsdl.categoriesAndProducts.VOKExtendedSoapSerializationEnvelope;
import com.emiadda.wsdl.categoriesAndProducts.VOKserverBinding;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

/**
 * Created by Kunal on 08/07/16.
 */
public class GetProductImageAsync extends AsyncTask<String, Void, String> {

    private static final String TAG = GetProductImageAsync.class.getSimpleName();
    private static final String METHOD_NAME = "getProductImage";
    private static final String NAMESPACE = "http://www.mydevsystems.com";

    private int requestCode;
    private int responseCode;
    private ServerResponseSubscriber serverResponseInterface;

    public GetProductImageAsync(ServerResponseSubscriber serverResponseInterface, int requestCode) {
        this.requestCode =requestCode;
        this.serverResponseInterface = serverResponseInterface;
    }


    @Override
    protected String doInBackground(String... params) {
        try {
            //Using easysoap
            VOKserverBinding abmServerBinding = new VOKserverBinding();

            //Using soap standard way
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo propertyId = new PropertyInfo();
            propertyId.setValue(0);
            propertyId.setType(Integer.class);
            request.addProperty(propertyId);

            VOKExtendedSoapSerializationEnvelope soapEnvelope = new VOKExtendedSoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.encodingStyle = SoapEnvelope.ENC;
            soapEnvelope.dotNet = false;
            soapEnvelope.bodyOut = request;

            responseCode = ServerResponseSubscriber.RESPONSE_CODE_OK;
            return abmServerBinding.getProductImage(params[0]);
        }
        catch (Exception e) {
            requestCode = ServerResponseSubscriber.RESPONSE_CODE_EXCEPTION;
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        if(serverResponseInterface != null) {
            this.serverResponseInterface.responseReceived(s, requestCode, responseCode, 0);
        }
    }

}
