package com.emiadda.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.emiadda.interafaces.ServerResponseInterface;
import com.emiadda.wsdl.categoriesAndProducts.VOKExtendedSoapSerializationEnvelope;
import com.emiadda.wsdl.categoriesAndProducts.VOKserverBinding;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

/**
 * Created by Rashmi on 05/07/16.
 */
public class GetCategoriesAsync extends AsyncTask<String, Void, String> {
    private static final String TAG = GetCategoriesAsync.class.getSimpleName();
    private static final String METHOD_NAME = "getCategories";
    private static final String NAMESPACE = "http://www.mydevsystems.com";

    private int requestCode;
    private int responseCode;
    private ServerResponseInterface serverResponseInterface;

    public GetCategoriesAsync(ServerResponseInterface serverResponseInterface, int requestCode) {
        this.serverResponseInterface = serverResponseInterface;
        this.requestCode =requestCode;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
                //Using easysoap
                VOKserverBinding VOKServerBinding = new VOKserverBinding();

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

                responseCode = ServerResponseInterface.RESPONSE_CODE_OK;
                return VOKServerBinding.getCategories(params[0]);
        }
        catch (Exception e) {
            requestCode = ServerResponseInterface.RESPONSE_CODE_EXCEPTION;
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        if (serverResponseInterface != null) {
            serverResponseInterface.responseReceived(s, requestCode, responseCode);
        }
    }
}
