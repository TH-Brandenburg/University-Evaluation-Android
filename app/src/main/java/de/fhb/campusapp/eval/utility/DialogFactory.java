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

    /**
     * Creates a 2 options dialog.
     * @param context
     * @param title headline of the dialog
     * @param message main text of the dialog
     * @param accept listener for the accept button
     * @param deny listener for deny button
     * @param acceptText text of the positive answer button
     * @param denyText text of the negative answer button
     * @param cancelable weather a click to anywhere but the presented buttons dismisses the dialog
     * @return a created dialog instance. To display it call show()
     */
    public static AlertDialog createAcceptDenyDialog(Context context,
                                                     String title, String message, String acceptText,
                                                     String denyText, boolean cancelable,
                                                     DialogInterface.OnClickListener accept,
                                                     DialogInterface.OnClickListener deny,
                                                     DialogInterface.OnDismissListener dismiss){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(acceptText, accept)
                .setNegativeButton(denyText, deny)
                .setCancelable(cancelable)
                .setOnDismissListener(dismiss);
        return alertDialog.create();
    }

    /**
     * Creates a 2 options dialog.
     * @param context
     * @param title headline of the dialog
     * @param message main text of the dialog
     * @param accept listener for the accept button
     * @param deny listener for deny button
     * @param acceptText text of the positive answer button
     * @param denyText text of the negative answer button
     * @param cancelable weather a click to anywhere but the presented buttons dismisses the dialog
     * @return a created dialog instance. To display it call show()
     */
    public static AlertDialog createAcceptDenyDialog(Context context,
                                                     @StringRes int title, @StringRes int message,
                                                     @StringRes int acceptText, @StringRes int denyText,
                                                     boolean cancelable,
                                                     DialogInterface.OnClickListener accept,
                                                     DialogInterface.OnClickListener deny,
                                                     DialogInterface.OnDismissListener dismiss){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(acceptText, accept)
                .setNegativeButton(denyText, deny)
                .setCancelable(cancelable)
                .setOnDismissListener(dismiss);
        return alertDialog.create();
    }

    /**
     * Creates a 2 options dialog. The options are set to "accept" and "deny". It is non dismissable.
     * @param context
     * @param title headline of the dialog
     * @param message main text of the dialog
     * @param accept listener for the accept button
     * @param deny listener for deny button
     * @return a created dialog instance. To display it call show()
     */
    public static AlertDialog createAcceptDenyDialog(Context context, String title, String message, DialogInterface.OnClickListener accept, DialogInterface.OnClickListener deny){
        return createAcceptDenyDialog(context, title, message, "Accept", "Deny", false, accept, deny, null);
    }

    /**
     * Creates a 2 options dialog. The options are set to "accept" and "deny". It is non dismissable.
     * @param context
     * @param titleResource headline of the dialog
     * @param messageResource main text of the dialog
     * @param accept listener for the accept button
     * @param deny listener for deny button
     * @return a created dialog instance. To display it call show()
     */
    public static AlertDialog createAcceptDenyDialog(Context context, @StringRes int titleResource, @StringRes int messageResource, DialogInterface.OnClickListener accept, DialogInterface.OnClickListener deny){
        return createAcceptDenyDialog(context, context.getString(titleResource), context.getString(messageResource), accept, deny);
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
