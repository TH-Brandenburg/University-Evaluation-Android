package de.fhb.campusapp.eval.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import de.fhb.campusapp.eval.activities.ScanActivity;
import de.fhb.campusapp.eval.utility.EventBus;
import de.fhb.campusapp.eval.utility.Events.RestartQRScanningEvent;
import de.fhb.campusapp.eval.utility.Events.StartServerCommunicationEvent;
import fhb.de.campusappevaluationexp.R;
import roboguice.fragment.RoboDialogFragment;


public class MessageFragment extends RoboDialogFragment {

    public static final String TITLE = "TITLE";
    public static final String MESSAGE = "MESSAGE";
    public static final String CANCELABLE = "CANCELABLE";
    public static final String ACTION = "ACTION";
    public static final String CLOSE = "CLOSE";
    public static final String GO_TO_SCAN = "GOTTOSCAN";
    public static final String COMMUNICATOR = "COMMUNICATOR";

    private String mTitle;
    private String mMessage;
    private boolean mCancelable;
    private Option mOption;
    private boolean mButtonClicked = false;
    private MessageFragmentCommunicator mActivityCommunicator;

//    private IRetryCommunicator mCommunicator;

    public static MessageFragment newInstance(String title, String message, boolean cancelable, Option option) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(MESSAGE, message);
        args.putBoolean(CANCELABLE, cancelable);
        args.putSerializable(ACTION, option);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivityCommunicator = (MessageFragmentCommunicator) activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        if (this.getArguments() != null) {
            Bundle args = this.getArguments();
            mTitle = args.getString(TITLE);
            mMessage = args.getString(MESSAGE);
            mCancelable = args.getBoolean(CANCELABLE);
            mOption = (Option) args.getSerializable(ACTION);
        }

        setCancelable(mCancelable);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (mOption == Option.None) {
            builder.setMessage(mMessage)
                    .setTitle(mTitle)
                    .setCancelable(mCancelable)
                    .setNeutralButton(R.string.close_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mButtonClicked = true;
                        }
                    });
        } else if (mOption == Option.CloseApp) {
            builder.setMessage(mMessage)
                    .setTitle(mTitle)
                    .setCancelable(mCancelable)
                    .setNeutralButton(R.string.close_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            initAppKill();
                            mButtonClicked = true;
                        }
                    });
        } else if (mOption == Option.RetryCommunication) {
            builder.setMessage(mMessage)
                    .setTitle(mTitle)
                    .setCancelable(mCancelable)
                    .setPositiveButton(R.string.retry_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (mOption == Option.RetryCommunication) {
                                mActivityCommunicator.onStartServerCommunication();
                                mButtonClicked = true;
                            }
                        }
                    })
                    .setNegativeButton(R.string.close_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mActivityCommunicator.onRestartQRScanning();
                            mButtonClicked = true;
                        }
                    });
        } else if (mOption == Option.GoToScan) {
            builder.setMessage(mMessage)
                    .setTitle(mTitle)
                    .setCancelable(mCancelable)
                    .setNeutralButton(R.string.retry_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            gotToScan();
                            mButtonClicked = true;
                        }
                    });
        } else if (mOption == Option.RetryScan) {
            builder.setMessage(mMessage)
                    .setTitle(mTitle)
                    .setCancelable(mCancelable)
                    .setNeutralButton(R.string.retry_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mActivityCommunicator.onRestartQRScanning();
                            mButtonClicked = true;
                        }
                    });
            // Create the AlertDialog object and return it

        }
        return builder.create();
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(!mButtonClicked){
            mActivityCommunicator.onRestartQRScanning();
        }
    }

    private void initAppKill(){
        Intent intent = new Intent(getActivity().getApplicationContext(), ScanActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(CLOSE, true);
        startActivity(intent);
    }

    private void gotToScan(){
        Intent intent = new Intent(getActivity().getApplicationContext(), ScanActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(GO_TO_SCAN, true);
        startActivity(intent);
    }

    public enum Option {
        None,
        CloseApp,
        RetryCommunication,
        GoToScan,
        RetryScan
    }

    public interface MessageFragmentCommunicator{
        public void onRestartQRScanning();
        public void onStartServerCommunication();
    }
}
