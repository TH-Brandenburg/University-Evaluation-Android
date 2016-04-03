package de.thb.ue.android.utility.Events;

//import response.Response;
//import response.Retrofit;

import retrofit.client.Response;

/**
 * Created by Admin on 14.12.2015.
 */
public class NetworkSuccessEvent <T>{
    private T requestedObject;
    private Response response;

   public NetworkSuccessEvent(T requestedObject, Response response) {
        this.response = response;
        this.requestedObject = requestedObject;
    }

    public T getRequestedObject() {
        return requestedObject;
    }

    public void setRequestedObject(T requestedObject) {
        this.requestedObject = requestedObject;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }


}
