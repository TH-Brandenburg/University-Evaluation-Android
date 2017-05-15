package de.thb.ue.android.ui.base;


import android.content.DialogInterface;
import android.view.View;

/**
 * Base interface that any class that wants to act as a View in the MVP (Model View Presenter)
 * pattern must implement. Generally this interface will be extended by a more specific interface
 * that then usually will be implemented by an Activity or Fragment.
 */
public interface MvpView {

    void displayToast(final String toastText);
    void displayGenericErrorDialog(String title, String message);
    void displayGenericActionDialog(String title, String message, DialogInterface.OnClickListener listener, DialogInterface.OnDismissListener dismissListener, boolean dismissable);

}