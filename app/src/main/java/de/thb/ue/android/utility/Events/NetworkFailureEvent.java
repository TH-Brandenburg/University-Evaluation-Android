package de.thb.ue.android.utility.Events;

import retrofit.RetrofitError;

/**
 * Created by Admin on 14.12.2015.
 */
public class NetworkFailureEvent {
    private RetrofitError retrofitError;

    public NetworkFailureEvent(RetrofitError retrofitError) {
        this.retrofitError = retrofitError;
    }

    public RetrofitError getRetrofitError() {
        return retrofitError;
    }

    public void setRetrofitError(RetrofitError retrofitError) {
        this.retrofitError = retrofitError;
    }
}
