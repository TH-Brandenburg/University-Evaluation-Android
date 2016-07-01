package de.fhb.campusapp.eval.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;

import de.fhb.campusapp.eval.interfaces.RequestCommunicator;
import de.fhb.campusapp.eval.utility.EventBus;
import de.fhb.campusapp.eval.utility.Events.PreServerCommunicationEvent;
import de.fhb.campusapp.eval.utility.Events.StartServerCommunicationEvent;
import fhb.de.campusappevaluationexp.R;
import roboguice.fragment.RoboDialogFragment;

public class SendDialogFragment extends RoboDialogFragment {

    public final static String QUESTIONS_ANSWERED = "QUESTIONS_ANSWERED";
    public final static String QUESTIONS_TOTAL = "QUESTIONS_TOTAL";
    private SendDialogFragmentCommunicator mActivityCommunicator;


    public static SendDialogFragment newInstance(int answeredQuestion, int totalQuestions) {
        SendDialogFragment fragment = new SendDialogFragment();
        Bundle args = new Bundle();
        args.putInt(QUESTIONS_ANSWERED, answeredQuestion);
        args.putInt(QUESTIONS_TOTAL, totalQuestions);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivityCommunicator = (SendDialogFragmentCommunicator) activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        int total = 0;
        int answered = 0;
        Bundle bundle = getArguments();
        if(bundle != null){
            answered = bundle.getInt(QUESTIONS_ANSWERED);
            total = bundle.getInt(QUESTIONS_TOTAL);
        }

        String message = getString(R.string.send_dialogue_answers_uncomplete, answered, total);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton(R.string.send_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        EventBus.getEventBus().post(new PreServerCommunicationEvent());
                        mActivityCommunicator.onPreServerCommunication();
                    }
                })
                .setNegativeButton(R.string.abort_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public interface SendDialogFragmentCommunicator{
        public void onPreServerCommunication();
    }
}
