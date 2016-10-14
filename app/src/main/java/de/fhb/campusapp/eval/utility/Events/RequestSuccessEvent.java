package de.fhb.campusapp.eval.utility.Events;


import retrofit2.Response;

/**
 * Created by Admin on 14.12.2015.
 */
public class RequestSuccessEvent<T>{
    private T requestedObject;
    private Response response;

   public RequestSuccessEvent(T requestedObject, Response response) {
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
