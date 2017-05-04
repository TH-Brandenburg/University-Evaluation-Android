package de.thb.ue.android.data;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import javax.inject.Inject;

import de.thb.ue.android.data.VOs.ProcessedResponse;
import de.thb.ue.android.data.local.MappingHelper;
import de.thb.ue.android.data.local.PreferencesHelper;
import de.thb.ue.android.data.remote.RetrofitHelper;
import de.thb.ue.android.injection.ApplicationContext;
import de.thb.ue.android.utility.Connectivity;
import de.thb.ue.android.utility.eventbus.EventBus;
import de.thb.ue.android.utility.eventbus.events.ConnectivityChangedEvent;
import io.reactivex.Single;
import retrofit2.Response;



public class DataManager implements IDataManager{
    private static final String TAG = "DATA_MANAGER";


    private final PreferencesHelper mPreferencesHelper;
    private final RetrofitHelper mRetrofitHelper;
    private final MappingHelper mMappingHelper;
    private final Context mContext;
    private final Connectivity mConnectivity;
    private final EventBus mEventBus;

    @Inject
    public DataManager(@ApplicationContext Context context, PreferencesHelper preferencesHelper, RetrofitHelper retrofitHelper,
                       MappingHelper mappingHelper , EventBus eventBus, Connectivity connectivity) {
        this.mRetrofitHelper = retrofitHelper;
        this.mPreferencesHelper = preferencesHelper;
        this.mMappingHelper = mappingHelper;
        this.mContext = context;
        this.mConnectivity = connectivity;
        this.mEventBus = eventBus;

        mContext.registerReceiver(new ConncectivityChangedReceiver(), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        mContext.registerReceiver(new ConncectivityChangedReceiver(), new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED"));

    }


    //********Internal Data Operations*****************************


    @Override
    public boolean isConnected() {
        return mConnectivity.isConnected(mContext);
    }


    public void invalidateSessionCookies(){
        mRetrofitHelper.clearSessionCookies();
    }

    /**
     * Deletes all cached cookies. Most important the cookie used for authentication.
     */
    @Override
    public void invalidateAllCookies() {
        mRetrofitHelper.clearAllCookies();
    }

    @Override
    public boolean isUserAuthenticated() {
        return mRetrofitHelper.isSessionCookieExpired();
    }


    /**
     * Removes all data from shared preferences
     */
    public void removeCachedData(){
        mPreferencesHelper.clear();
    }

    private <T,V> ProcessedResponse<V> handleResponse(Response<T> response, V body, int expectedCode) throws IllegalAccessException, InstantiationException {
        if(expectedCode == response.code()){
            return new ProcessedResponse<>(response.code(), body);
        } else {
            return new ProcessedResponse<>(response.code(), body, response.message());
        }
    }

    private <T> Single<ProcessedResponse<T>> handleCache(T cachedObject, Class objectType){
        if (cachedObject != null){
            return Single.create(e -> e.onSuccess(new ProcessedResponse<>(200, cachedObject)));
        } else if (objectType != Void.TYPE){
            return Single.create(e -> e.onSuccess(new ProcessedResponse<>(400, (T) objectType.newInstance(), "No cached instance found")));
        } else {
            return Single.create(e -> e.onSuccess(new ProcessedResponse<>(400, null, "No cached instance found")));
        }
    }

    //*************************Observers************************************

    private class ConncectivityChangedReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            mEventBus.post(new ConnectivityChangedEvent(
                    mConnectivity.isConnected(mContext)
                    , mConnectivity.isConnectedMobile(mContext)
                    , mConnectivity.isConnectedWifi(mContext)
                    , mConnectivity.isConnectedFast(mContext)));
        }
    }

    //*************************Getter & Setter & Event Throwing ************

}