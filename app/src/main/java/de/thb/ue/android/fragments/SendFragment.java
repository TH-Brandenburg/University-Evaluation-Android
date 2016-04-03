package de.thb.ue.android.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.thb.ca.dto.AnswersDTO;
import de.thb.ca.dto.MultipleChoiceQuestionDTO;
import de.thb.ca.dto.util.*;
import de.thb.ue.android.utility.DataHolder;
import de.thb.ue.android.utility.Utility;
import fhb.de.campusappevaluationexp.R;
import roboguice.fragment.RoboDialogFragment;
import roboguice.inject.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SendFragment extends BaseFragment {

    public static final String POSITION = "POSITION";

    @InjectView(R.id.send_button)
    private Button mSendButton;

    private int mPosition;
    private SendFragmentCommunicator mActivityCommunicator;

    public SendFragment() { }

    public static SendFragment newInstance(int position) {
        SendFragment fragment = new SendFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            Utility.setKeyboardOverlapping(getActivity());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivityCommunicator = (SendFragmentCommunicator) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(this.getArguments() != null){
            Bundle args = this.getArguments();
            mPosition = args.getInt(POSITION);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_send, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int total = DataHolder.getQuestionsDTO().getMultipleChoiceQuestionDTOs().size() + DataHolder.getQuestionsDTO().getTextQuestions().size();
                int answered = 0;
                AnswersDTO answersDTO = DataHolder.getAnswersDTO();

                //count only non empty texts and photos as answered
                for(TextAnswerDTO answer : answersDTO.getTextAnswers()){
                    if((answer.getAnswerText() != null && !answer.getAnswerText().equals(""))
                            || DataHolder.getCommentaryImageMap().containsKey(answer.getQuestionText())){
                        answered++;
                    }
                }

                answered += answersDTO.getMcAnswers().size();

                if(answered < total){
                    mActivityCommunicator.onRecolorUnansweredQuestions();
                    RoboDialogFragment newFragment = SendDialogFragment.newInstance(answered, total);
                    newFragment.show(getActivity().getSupportFragmentManager(), "SendDialog");
                } else {
                    mActivityCommunicator.onPreServerCommunication();
                }
            }
        });
    }
    public interface SendFragmentCommunicator{
        public void onPreServerCommunication();
        public void onRecolorUnansweredQuestions();
    }
}
