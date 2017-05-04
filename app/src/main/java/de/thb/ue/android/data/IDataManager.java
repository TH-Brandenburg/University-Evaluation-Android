package de.thb.ue.android.data;

/**
 * Created by Sebastian Müller on 12.11.2016.
 */
public interface IDataManager {

    //*************Networking****************

    boolean isConnected();

    void invalidateSessionCookies();

    void invalidateAllCookies();

    boolean isUserAuthenticated();

    //*************Internal Operations*******

    /**
     * Removes the transient data from shared preferences.
     */
    void removeCachedData();


}
