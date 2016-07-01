package de.fhb.campusapp.eval.utility.Events;

import retrofit2.Response;

/**
 * Created by Admin on 14.06.2016.
 * Signifies that information was send over the network but some error occured during transmission.
 * Either the server had an error (5xx), was unreachable or answered with an error message.
 *
 */
public class NetworkErrorEvent <T> {
    Response<T> resposne;

    public NetworkErrorEvent(Response<T> resposne) {
        this.resposne = resposne;
    }

    public Response<T> getResposne() {
        return resposne;
    }

    public void setResposne(Response<T> resposne) {
        this.resposne = resposne;
    }
}
