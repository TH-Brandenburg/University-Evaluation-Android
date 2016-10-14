package de.fhb.campusapp.eval.utility;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;

import fhb.de.campusappevaluationexp.R;


public final class DialogFactory {

    public static Dialog createSimpleOkErrorDialog(Context context, String title, String message, DialogInterface.OnClickListener listener, DialogInterface.OnDismissListener dismissListener, boolean cancelable) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(R.string.ok_button, listener)
                .setCancelable(cancelable)
                .setOnDismissListener(dismissListener);

        return alertDialog.create();
    }

    public static Dialog createSimpleOkErrorDialog(Context context, String title, String message, DialogInterface.OnClickListener listener) {
        return createSimpleOkErrorDialog(context, title, message, listener, null, true);
    }

    public static Dialog createSimpleOkErrorDialog(Context context,
                                                   @StringRes int titleResource,
                                                   @StringRes int messageResource) {

        return createSimpleOkErrorDialog(context,
                context.getString(titleResource),
                context.getString(messageResource), null, null, true);
    }

    public static Dialog createSimpleOkErrorDialog(Context context,
                                                   @StringRes int titleResource,
                                                   @StringRes int messageResource,
                                                   DialogInterface.OnClickListener listener,
                                                   DialogInterface.OnDismissListener dismissListener) {

        return createSimpleOkErrorDialog(context,
                context.getString(titleResource),
                context.getString(messageResource),
                listener, dismissListener, true);
    }

    public static Dialog createSimpleOkErrorDialog(Context context,
                                                   @StringRes int titleResource,
                                                   @StringRes int messageResource,
                                                   DialogInterface.OnClickListener listener,
                                                   boolean cancelable) {

        return createSimpleOkErrorDialog(context,
                context.getString(titleResource),
                context.getString(messageResource),
                listener, null, cancelable);
    }

    public static Dialog createSimpleOkErrorDialog(Context context,
                                                   @StringRes int titleResource,
                                                   @StringRes int messageResource,
                                                   boolean cancelable) {

        return createSimpleOkErrorDialog(context,
                context.getString(titleResource),
                context.getString(messageResource), null, null, cancelable);
    }

    public static Dialog createSimpleOkErrorDialog(Context context,
                                                   @StringRes int titleResource,
                                                   @StringRes int messageResource,
                                                   DialogInterface.OnClickListener listener,
                                                   DialogInterface.OnDismissListener dismissListener,
                                                   boolean cancelable) {

        return createSimpleOkErrorDialog(context,
                context.getString(titleResource),
                context.getString(messageResource),
                listener, dismissListener, cancelable);
    }

    public static Dialog createGenericErrorDialog(Context context, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.generic_error_title))
                .setMessage(message)
                .setNeutralButton(R.string.ok_button, null);

        return alertDialog.create();
    }

    public static Dialog createGenericErrorDialog(Context context, @StringRes int messageResource) {
        return createGenericErrorDialog(context, context.getString(messageResource));
    }

    public static AlertDialog createAcceptDenyDialog(Context context, String title, String message, DialogInterface.OnClickListener accept, DialogInterface.OnClickListener deny){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Accept", accept)
                .setNegativeButton("Deny", deny);
        return alertDialog.create();
    }

    public static AlertDialog createAcceptDenyDialog(Context context, @StringRes int titleRessource, @StringRes int messageRessource, DialogInterface.OnClickListener accept, DialogInterface.OnClickListener deny){
        return createAcceptDenyDialog(context, context.getString(titleRessource), context.getString(messageRessource), accept, deny);
    }

    public static ProgressDialog createProgressDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        return progressDialog;
    }

    public static ProgressDialog createProgressDialog(Context context,
                                                      @StringRes int messageResource) {
        return createProgressDialog(context, context.getString(messageResource));
    }

}
