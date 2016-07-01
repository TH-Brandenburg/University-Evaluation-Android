package de.fhb.campusapp.eval.utility.Events;


/**
 * Created by Admin on 14.12.2015.
 */
public class NetworkFailureEvent {
    private Throwable retrofitError;

    public NetworkFailureEvent(Throwable retrofitError) {
        this.retrofitError = retrofitError;
    }

    public Throwable getRetrofitError() {
        return retrofitError;
    }

    public void setRetrofitError(Throwable retrofitError) {
        this.retrofitError = retrofitError;
    }
}
