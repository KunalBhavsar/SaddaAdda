package com.emiadda.server;

//------------------------------------------------------------------------------
// <wsdl2code-generated>
//    This code was generated by http://www.wsdl2code.com version  2.6
//
// Date Of Creation: 8/3/2016 5:50:27 PM
//    Please dont change this code, regeneration will override your changes
//</wsdl2code-generated>
//
//------------------------------------------------------------------------------
//
//This source code was auto-generated by Wsdl2Code  Version
//

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.SocketTimeoutException;
import java.util.List;

import static android.content.ContentValues.TAG;

public class GetCategoriesWSDL {

    public String NAMESPACE = "urn:server";
    public String url = "http://www.mydevsystems.com/dev/emiaddanew/jnusoap/emiadda/getCategories.php";
    public int timeOut = 10000;
    public IWsdl2CodeEvents eventHandler;
    public WS_Enums.SoapProtocolVersion soapVersion;

    public GetCategoriesWSDL() {
    }

    public GetCategoriesWSDL(IWsdl2CodeEvents eventHandler) {
        this.eventHandler = eventHandler;
    }

    public GetCategoriesWSDL(IWsdl2CodeEvents eventHandler, String url) {
        this.eventHandler = eventHandler;
        this.url = url;
    }

    public GetCategoriesWSDL(IWsdl2CodeEvents eventHandler, String url, int timeOutInSeconds) {
        this.eventHandler = eventHandler;
        this.url = url;
        this.setTimeOut(timeOutInSeconds);
    }

    public void setTimeOut(int seconds) {
        this.timeOut = seconds * 1000;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void getCategoriesAsync(String params) throws Exception {
        if (this.eventHandler == null)
            throw new Exception("Async Methods Requires IWsdl2CodeEvents");
        getCategoriesAsync(params, null);
    }

    public void getCategoriesAsync(final String params, final List<HeaderProperty> headers) throws Exception {

        new AsyncTask<Void, Void, ServerResponse>() {
            @Override
            protected void onPreExecute() {
                eventHandler.Wsdl2CodeStartedRequest();
            }

            @Override
            protected ServerResponse doInBackground(Void... paramss) {
                return getCategories(params, headers);
            }

            @Override
            protected void onPostExecute(ServerResponse result) {
                eventHandler.Wsdl2CodeEndedRequest();
                if (result != null) {
                    eventHandler.Wsdl2CodeFinished("getCategories", result);
                }
            }
        }.execute();
    }

    public ServerResponse getCategories(String params) {
        return getCategories(params, null);
    }

    public ServerResponse getCategories(String params, List<HeaderProperty> headers) {

        ServerResponse serverResponse = new ServerResponse();

        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapEnvelope.implicitTypes = true;
        soapEnvelope.dotNet = true;
        SoapObject soapReq = new SoapObject("urn:server", "getCategories");
        soapReq.addProperty("params", params);
        soapEnvelope.setOutputSoapObject(soapReq);
        HttpTransportSE httpTransport = new HttpTransportSE(url, timeOut);
        try {
            if (headers != null) {
                httpTransport.call("urn:server/getCategories", soapEnvelope, headers);
            } else {
                httpTransport.call("urn:server/getCategories", soapEnvelope);
            }
            Object retObj = soapEnvelope.bodyIn;
            if (retObj instanceof SoapFault) {
                SoapFault fault = (SoapFault) retObj;
                Exception ex = new Exception(fault.faultstring);
                if (eventHandler != null)
                    eventHandler.Wsdl2CodeFinishedWithException(ex);
                serverResponse.setError(ex.getMessage());
                serverResponse.setResponseStatus(ServerResponse.SERVER_ERROR);
                return serverResponse;
            } else {
                SoapObject result = (SoapObject) retObj;
                if (result.getPropertyCount() > 0) {
                    Object obj = result.getProperty(0);
                    if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                        SoapPrimitive j = (SoapPrimitive) obj;
                        String resultVariable = j.toString();

                        serverResponse.setResponse(resultVariable);
                        serverResponse.setResponseStatus(ServerResponse.SERVER_OK);
                        return serverResponse;
                    }
                    else if (obj != null && obj instanceof String) {
                        String resultVariable = (String) obj;
                        serverResponse.setResponse(resultVariable);
                        serverResponse.setResponseStatus(ServerResponse.SERVER_OK);
                        return serverResponse;
                    }
                }
            }
        }
        catch (SocketTimeoutException e) {
            if (eventHandler != null)
                eventHandler.Wsdl2CodeFinishedWithException(e);
            Log.e(TAG, e.getMessage(), e);
            serverResponse.setError(e.getMessage());
            serverResponse.setResponseStatus(ServerResponse.NETWORK_ERROR);
            return serverResponse;
        }
        catch (Exception e) {
            if (eventHandler != null)
                eventHandler.Wsdl2CodeFinishedWithException(e);
            Log.e(TAG, e.getMessage(), e);
            serverResponse.setError(e.getMessage());
            serverResponse.setResponseStatus(ServerResponse.SERVER_ERROR);
            return serverResponse;
        }
        return null;
    }

    public void getProductsByCategoryAsync(String params) throws Exception {
        if (this.eventHandler == null)
            throw new Exception("Async Methods Requires IWsdl2CodeEvents");
        getProductsByCategoryAsync(params, null);
    }

    public void getProductsByCategoryAsync(final String params, final List<HeaderProperty> headers) throws Exception {

        new AsyncTask<Void, Void, ServerResponse>() {
            @Override
            protected void onPreExecute() {
                eventHandler.Wsdl2CodeStartedRequest();
            }

            @Override
            protected ServerResponse doInBackground(Void... paramss) {
                return getProductsByCategory(params, headers);
            }

            @Override
            protected void onPostExecute(ServerResponse result) {
                eventHandler.Wsdl2CodeEndedRequest();
                if (result != null) {
                    eventHandler.Wsdl2CodeFinished("getProductsByCategory", result.getResponse());
                }
            }
        }.execute();
    }

    public ServerResponse getProductsByCategory(String params) {
        return getProductsByCategory(params, null);
    }

    public ServerResponse getProductsByCategory(String params, List<HeaderProperty> headers) {

        ServerResponse serverResponse = new ServerResponse();

        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapEnvelope.implicitTypes = true;
        soapEnvelope.dotNet = true;
        SoapObject soapReq = new SoapObject("urn:server", "getProductsByCategory");
        soapReq.addProperty("params", params);
        soapEnvelope.setOutputSoapObject(soapReq);
        HttpTransportSE httpTransport = new HttpTransportSE(url, timeOut);
        try {
            if (headers != null) {
                httpTransport.call("urn:server/getProductsByCategory", soapEnvelope, headers);
            } else {
                httpTransport.call("urn:server/getProductsByCategory", soapEnvelope);
            }
            Object retObj = soapEnvelope.bodyIn;
            if (retObj instanceof SoapFault) {
                SoapFault fault = (SoapFault) retObj;
                Exception ex = new Exception(fault.faultstring);
                if (eventHandler != null)
                    eventHandler.Wsdl2CodeFinishedWithException(ex);

                serverResponse.setError(ex.getMessage());
                serverResponse.setResponseStatus(ServerResponse.SERVER_ERROR);
                return serverResponse;
            } else {
                SoapObject result = (SoapObject) retObj;
                if (result.getPropertyCount() > 0) {
                    Object obj = result.getProperty(0);
                    if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                        SoapPrimitive j = (SoapPrimitive) obj;
                        String resultVariable = j.toString();

                        serverResponse.setResponse(resultVariable);
                        serverResponse.setResponseStatus(ServerResponse.SERVER_OK);
                        return serverResponse;
                    } else if (obj != null && obj instanceof String) {
                        String resultVariable = (String) obj;

                        serverResponse.setResponse(resultVariable);
                        serverResponse.setResponseStatus(ServerResponse.SERVER_OK);
                        return serverResponse;
                    }
                }
            }
        }
        catch (SocketTimeoutException e) {
            if (eventHandler != null)
                eventHandler.Wsdl2CodeFinishedWithException(e);
            Log.e(TAG, e.getMessage(), e);
            serverResponse.setError(e.getMessage());
            serverResponse.setResponseStatus(ServerResponse.NETWORK_ERROR);
            return serverResponse;
        }
        catch (Exception e) {
            if (eventHandler != null)
                eventHandler.Wsdl2CodeFinishedWithException(e);
            Log.e(TAG, e.getMessage(), e);
            serverResponse.setError(e.getMessage());
            serverResponse.setResponseStatus(ServerResponse.SERVER_ERROR);
            return serverResponse;
        }
        return null;

    }

    public void getProductByProductIDAsync(String params) throws Exception {
        if (this.eventHandler == null)
            throw new Exception("Async Methods Requires IWsdl2CodeEvents");
        getProductByProductIDAsync(params, null);
    }

    public void getProductByProductIDAsync(final String params, final List<HeaderProperty> headers) throws Exception {

        new AsyncTask<Void, Void, ServerResponse>() {
            @Override
            protected void onPreExecute() {
                eventHandler.Wsdl2CodeStartedRequest();
            }

            ;

            @Override
            protected ServerResponse doInBackground(Void... paramss) {
                return getProductByProductID(params, headers);
            }

            @Override
            protected void onPostExecute(ServerResponse result) {
                eventHandler.Wsdl2CodeEndedRequest();
                if (result != null) {
                    eventHandler.Wsdl2CodeFinished("getProductByProductID", result.getResponse());
                }
            }
        }.execute();
    }

    public ServerResponse getProductByProductID(String params) {
        return getProductByProductID(params, null);
    }

    public ServerResponse getProductByProductID(String params, List<HeaderProperty> headers) {

        ServerResponse serverResponse = new ServerResponse();

        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapEnvelope.implicitTypes = true;
        soapEnvelope.dotNet = true;
        SoapObject soapReq = new SoapObject("urn:server", "getProductByProductID");
        soapReq.addProperty("params", params);
        soapEnvelope.setOutputSoapObject(soapReq);
        HttpTransportSE httpTransport = new HttpTransportSE(url, timeOut);
        try {
            if (headers != null) {
                httpTransport.call("urn:server/getProductByProductID", soapEnvelope, headers);
            } else {
                httpTransport.call("urn:server/getProductByProductID", soapEnvelope);
            }
            Object retObj = soapEnvelope.bodyIn;
            if (retObj instanceof SoapFault) {
                SoapFault fault = (SoapFault) retObj;
                Exception ex = new Exception(fault.faultstring);
                if (eventHandler != null)
                    eventHandler.Wsdl2CodeFinishedWithException(ex);

                serverResponse.setError(ex.getMessage());
                serverResponse.setResponseStatus(ServerResponse.SERVER_ERROR);
                return serverResponse;

            } else {
                SoapObject result = (SoapObject) retObj;
                if (result.getPropertyCount() > 0) {
                    Object obj = result.getProperty(0);
                    if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                        SoapPrimitive j = (SoapPrimitive) obj;
                        String resultVariable = j.toString();

                        serverResponse.setResponse(resultVariable);
                        serverResponse.setResponseStatus(ServerResponse.SERVER_OK);
                        return serverResponse;
                    } else if (obj != null && obj instanceof String) {
                        String resultVariable = (String) obj;

                        serverResponse.setResponse(resultVariable);
                        serverResponse.setResponseStatus(ServerResponse.SERVER_OK);
                        return serverResponse;
                    }
                }
            }
        } catch (SocketTimeoutException e) {
            if (eventHandler != null)
                eventHandler.Wsdl2CodeFinishedWithException(e);
            Log.e(TAG, e.getMessage(), e);
            serverResponse.setError(e.getMessage());
            serverResponse.setResponseStatus(ServerResponse.NETWORK_ERROR);
            return serverResponse;
        }
        catch (Exception e) {
            if (eventHandler != null)
                eventHandler.Wsdl2CodeFinishedWithException(e);
            Log.e(TAG, e.getMessage(), e);
            serverResponse.setError(e.getMessage());
            serverResponse.setResponseStatus(ServerResponse.SERVER_ERROR);
            return serverResponse;
        }
        return null;
    }

    public void getProductImageAsync(String params) throws Exception {
        if (this.eventHandler == null)
            throw new Exception("Async Methods Requires IWsdl2CodeEvents");
        getProductImageAsync(params, null);
    }

    public void getProductImageAsync(final String params, final List<HeaderProperty> headers) throws Exception {

        new AsyncTask<Void, Void, ServerResponse>() {
            @Override
            protected void onPreExecute() {
                eventHandler.Wsdl2CodeStartedRequest();
            }

            ;

            @Override
            protected ServerResponse doInBackground(Void... paramss) {
                return getProductImage(params, headers);
            }

            @Override
            protected void onPostExecute(ServerResponse result) {
                eventHandler.Wsdl2CodeEndedRequest();
                if (result != null) {
                    eventHandler.Wsdl2CodeFinished("getProductImage", result.getResponse());
                }
            }
        }.execute();
    }

    public ServerResponse getProductImage(String params) {
        return getProductImage(params, null);
    }

    public ServerResponse getProductImage(String params, List<HeaderProperty> headers) {

        ServerResponse serverResponse = new ServerResponse();

        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapEnvelope.implicitTypes = true;
        soapEnvelope.dotNet = true;
        SoapObject soapReq = new SoapObject("urn:server", "getProductImage");
        soapReq.addProperty("params", params);
        soapEnvelope.setOutputSoapObject(soapReq);
        HttpTransportSE httpTransport = new HttpTransportSE(url, timeOut);
        try {
            if (headers != null) {
                httpTransport.call("urn:server/getProductImage", soapEnvelope, headers);
            } else {
                httpTransport.call("urn:server/getProductImage", soapEnvelope);
            }
            Object retObj = soapEnvelope.bodyIn;
            if (retObj instanceof SoapFault) {
                SoapFault fault = (SoapFault) retObj;
                Exception ex = new Exception(fault.faultstring);
                if (eventHandler != null)
                    eventHandler.Wsdl2CodeFinishedWithException(ex);

                serverResponse.setError(ex.getMessage());
                serverResponse.setResponseStatus(ServerResponse.SERVER_ERROR);
                return serverResponse;
            } else {
                SoapObject result = (SoapObject) retObj;
                if (result.getPropertyCount() > 0) {
                    Object obj = result.getProperty(0);
                    if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                        SoapPrimitive j = (SoapPrimitive) obj;
                        String resultVariable = j.toString();

                        serverResponse.setResponse(resultVariable);
                        serverResponse.setResponseStatus(ServerResponse.SERVER_OK);
                        return serverResponse;
                    } else if (obj != null && obj instanceof String) {
                        String resultVariable = (String) obj;

                        serverResponse.setResponse(resultVariable);
                        serverResponse.setResponseStatus(ServerResponse.SERVER_OK);
                        return serverResponse;
                    }
                }
            }
        }
        catch (SocketTimeoutException e) {
            if (eventHandler != null)
                eventHandler.Wsdl2CodeFinishedWithException(e);
            Log.e(TAG, e.getMessage(), e);
            serverResponse.setError(e.getMessage());
            serverResponse.setResponseStatus(ServerResponse.NETWORK_ERROR);
            return serverResponse;
        }
        catch (Exception e) {
            if (eventHandler != null)
                eventHandler.Wsdl2CodeFinishedWithException(e);
            Log.e(TAG, e.getMessage(), e);
            serverResponse.setError(e.getMessage());
            serverResponse.setResponseStatus(ServerResponse.SERVER_ERROR);
            return serverResponse;
        }
        return null;
    }

    public void getSpecialsAsync(String params) throws Exception {
        if (this.eventHandler == null)
            throw new Exception("Async Methods Requires IWsdl2CodeEvents");
        getSpecialsAsync(params, null);
    }

    public void getSpecialsAsync(final String params, final List<HeaderProperty> headers) throws Exception {

        new AsyncTask<Void, Void, ServerResponse>() {
            @Override
            protected void onPreExecute() {
                eventHandler.Wsdl2CodeStartedRequest();
            }

            ;

            @Override
            protected ServerResponse doInBackground(Void... paramss) {
                return getSpecials(params, headers);
            }

            @Override
            protected void onPostExecute(ServerResponse result) {
                eventHandler.Wsdl2CodeEndedRequest();
                if (result != null) {
                    eventHandler.Wsdl2CodeFinished("getSpecials", result);
                }
            }
        }.execute();
    }

    public ServerResponse getSpecials(String params) {
        return getSpecials(params, null);
    }

    public ServerResponse getSpecials(String params, List<HeaderProperty> headers) {

        ServerResponse serverResponse = new ServerResponse();

        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapEnvelope.implicitTypes = true;
        soapEnvelope.dotNet = true;
        SoapObject soapReq = new SoapObject("urn:server", "getSpecials");
        soapReq.addProperty("params", params);

        soapEnvelope.setOutputSoapObject(soapReq);
        HttpTransportSE httpTransport = new HttpTransportSE(url, timeOut);
        try {
            if (headers != null) {
                httpTransport.call("urn:server/getSpecials", soapEnvelope, headers);
            } else {
                httpTransport.call("urn:server/getSpecials", soapEnvelope);
            }
            Object retObj = soapEnvelope.bodyIn;
            if (retObj instanceof SoapFault) {
                SoapFault fault = (SoapFault) retObj;
                Exception ex = new Exception(fault.faultstring);
                if (eventHandler != null)
                    eventHandler.Wsdl2CodeFinishedWithException(ex);

                serverResponse.setError(ex.getMessage());
                serverResponse.setResponseStatus(ServerResponse.SERVER_ERROR);
                return serverResponse;

            } else {
                SoapObject result = (SoapObject) retObj;
                if (result.getPropertyCount() > 0) {
                    Object obj = result.getProperty(0);
                    if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                        SoapPrimitive j = (SoapPrimitive) obj;
                        String resultVariable = j.toString();

                        serverResponse.setResponse(resultVariable);
                        serverResponse.setResponseStatus(ServerResponse.SERVER_OK);
                        return serverResponse;
                    } else if (obj != null && obj instanceof String) {
                        String resultVariable = (String) obj;

                        serverResponse.setResponse(resultVariable);
                        serverResponse.setResponseStatus(ServerResponse.SERVER_OK);
                        return serverResponse;
                    }
                }
            }
        }
        catch (SocketTimeoutException e) {
            if (eventHandler != null)
                eventHandler.Wsdl2CodeFinishedWithException(e);
            Log.e(TAG, e.getMessage(), e);
            serverResponse.setError(e.getMessage());
            serverResponse.setResponseStatus(ServerResponse.NETWORK_ERROR);
            return serverResponse;
        }
        catch (Exception e) {
            if (eventHandler != null)
                eventHandler.Wsdl2CodeFinishedWithException(e);
            Log.e(TAG, e.getMessage(), e);
            serverResponse.setError(e.getMessage());
            serverResponse.setResponseStatus(ServerResponse.SERVER_ERROR);
            return serverResponse;
        }
        return null;
    }

    public void placeOrderAsync(OrderParams params, VectorProductsParams products_array, VectorTotalParams total_array) throws Exception {
        if (this.eventHandler == null)
            throw new Exception("Async Methods Requires IWsdl2CodeEvents");
        placeOrderAsync(params, products_array, total_array, null);
    }

    public void placeOrderAsync(final OrderParams params, final VectorProductsParams products_array, final VectorTotalParams total_array, final List<HeaderProperty> headers) throws Exception {

        new AsyncTask<Void, Void, ServerResponse>() {
            @Override
            protected void onPreExecute() {
                eventHandler.Wsdl2CodeStartedRequest();
            }

            ;

            @Override
            protected ServerResponse doInBackground(Void... paramss) {
                return placeOrder(params, products_array, total_array, headers);
            }

            @Override
            protected void onPostExecute(ServerResponse result) {
                eventHandler.Wsdl2CodeEndedRequest();
                if (result != null) {
                    eventHandler.Wsdl2CodeFinished("placeOrder", result.getResponse());
                }
            }
        }.execute();
    }

    public ServerResponse placeOrder(OrderParams params, VectorProductsParams products_array, VectorTotalParams total_array) {
        return placeOrder(params, products_array, total_array, null);
    }

    public ServerResponse placeOrder(OrderParams params, VectorProductsParams products_array, VectorTotalParams total_array, List<HeaderProperty> headers) {

        ServerResponse serverResponse = new ServerResponse();

        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapEnvelope.implicitTypes = true;
        soapEnvelope.dotNet = true;
        soapEnvelope.addMapping("http://tempuri.org", "params", new OrderParams().getClass());

        SoapObject soapReq = new SoapObject("http://tempuri.org", "placeOrder");
        soapReq.addProperty("params", params);

        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setNamespace("");
        propertyInfo.setName("products_array");
        propertyInfo.setValue(products_array);
        propertyInfo.setType("SOAP-ENC:Array");
        soapReq.addPropertyIfValue(propertyInfo);
        //soapReq.addProperty("products_array", products_array);

        propertyInfo = new PropertyInfo();
        propertyInfo.setNamespace("");
        propertyInfo.setName("total_array");
        propertyInfo.setValue(total_array);
        propertyInfo.setType("SOAP-ENC:Array");
        soapReq.addPropertyIfValue(propertyInfo);
        //soapReq.addProperty("total_array", total_array);

        soapEnvelope.setOutputSoapObject(soapReq);
        HttpTransportSE httpTransport = new HttpTransportSE(url, timeOut);
        httpTransport.debug = true;
        try {
            if (headers != null) {
                httpTransport.call("urn:server/placeOrder", soapEnvelope, headers);
            } else {
                httpTransport.call("urn:server/placeOrder", soapEnvelope);
            }

            String requestDump = httpTransport.requestDump;
            String responseDump = httpTransport.responseDump;

            Log.i("PlaceOrder", requestDump);
            Log.i("PlaceOrder", responseDump);

            Object retObj = soapEnvelope.bodyIn;
            if (retObj instanceof SoapFault) {
                SoapFault fault = (SoapFault) retObj;
                Exception ex = new Exception(fault.faultstring);
                if (eventHandler != null)
                    eventHandler.Wsdl2CodeFinishedWithException(ex);

                serverResponse.setError(ex.getMessage());
                serverResponse.setResponseStatus(ServerResponse.SERVER_ERROR);
                return serverResponse;

            } else {
                SoapObject result = (SoapObject) retObj;
                if (result.getPropertyCount() > 0) {
                    Object obj = result.getProperty(0);
                    if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
                        SoapPrimitive j = (SoapPrimitive) obj;
                        String resultVariable = j.toString();

                        serverResponse.setResponse(resultVariable);
                        serverResponse.setResponseStatus(ServerResponse.SERVER_OK);
                        return serverResponse;
                    } else if (obj != null && obj instanceof String) {
                        String resultVariable = (String) obj;

                        serverResponse.setResponse(resultVariable);
                        serverResponse.setResponseStatus(ServerResponse.SERVER_OK);
                        return serverResponse;
                    }
                }
            }
        }
        catch (SocketTimeoutException e) {
            if (eventHandler != null)
                eventHandler.Wsdl2CodeFinishedWithException(e);
            Log.e(TAG, e.getMessage(), e);
            serverResponse.setError(e.getMessage());
            serverResponse.setResponseStatus(ServerResponse.NETWORK_ERROR);
            return serverResponse;
        }
        catch (Exception e) {
            if (eventHandler != null)
                eventHandler.Wsdl2CodeFinishedWithException(e);
            Log.e(TAG, e.getMessage(), e);
            serverResponse.setError(e.getMessage());
            serverResponse.setResponseStatus(ServerResponse.SERVER_ERROR);
            return serverResponse;
        }
        return null;
    }
}
