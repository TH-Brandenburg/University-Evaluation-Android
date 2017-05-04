package de.thb.ue.android.utility.eventbus.events;

/**
 * Informs about the state of internet connectivity the app currently operates under.
 *
 *
 * Created by scorp on 16.03.2017.
 */

public class ConnectivityChangedEvent extends Event{

    /**
     * Weather the phone is connected to a network
     */
    private boolean isConnected;

    /**
     * Weather it receives mobile internet
     */
    private boolean isConnectedMobile;

    /**
     * Weather it is connected to internet via WiFi
     */
    private boolean isConnectedWiFi;

    /**
     * Weather the internet connections is fast
     */
    private boolean isConnectedFast;

    public ConnectivityChangedEvent(boolean isConnected, boolean isConnectedMobile, boolean isConnectedWiFi, boolean isConnectedFast) {
        this.isConnected = isConnected;
        this.isConnectedMobile = isConnectedMobile;
        this.isConnectedWiFi = isConnectedWiFi;
        this.isConnectedFast = isConnectedFast;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public boolean isConnectedMobile() {
        return isConnectedMobile;
    }

    public boolean isConnectedWiFi() {
        return isConnectedWiFi;
    }

    public boolean isConnectedFast() {
        return isConnectedFast;
    }
}
