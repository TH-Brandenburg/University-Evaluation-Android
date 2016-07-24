package de.fhb.campusapp.eval.fragments;


import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.fhb.campusapp.eval.utility.DataHolder;
import de.fhb.campusapp.eval.utility.Utility;
import de.fhb.campusapp.eval.utility.vos.AnswersVO;
import de.fhb.campusapp.eval.utility.vos.TextAnswerVO;
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
                int total = DataHolder.getQuestionsVO().getMultipleChoiceQuestionVOs().size() + DataHolder.getQuestionsVO().getTextQuestions().size();
                int answered = 0;
                String subject = DataHolder.getAnswersVO().getStudyPath();
                boolean subjectChoosen = !(subject == null || subject.isEmpty());
                AnswersVO answersVO = DataHolder.getAnswersVO();
                Resources res = getResources();

                // insist that a subject is chosen
                if(!subjectChoosen){
                    RoboDialogFragment newFragment = MessageFragment.newInstance(res.getString(R.string.subject_not_chosen_title), res.getString(R.string.subject_not_chosen_message), true, MessageFragment.Option.None);
                    newFragment.show(getActivity().getSupportFragmentManager(), "Subject not chosen");
                } else {
                    //count only non empty texts and photos as answered
                    for(TextAnswerVO answer : answersVO.getTextAnswers()){
                        if((answer.getAnswerText() != null && !answer.getAnswerText().equals(""))
                                || DataHolder.getCommentaryImageMap().containsKey(answer.getQuestionText())){
                            answered++;
                        }
                    }

                    answered += answersVO.getMcAnswers().size();

                    if(answered < total){
                        mActivityCommunicator.onRecolorUnansweredQuestions();
                        RoboDialogFragment newFragment = SendDialogFragment.newInstance(answered, total);
                        newFragment.show(getActivity().getSupportFragmentManager(), "SendDialog");
                    } else {
                        mActivityCommunicator.onPreServerCommunication();
                    }
                }
            }
        });
    }
    public interface SendFragmentCommunicator{
        public void onPreServerCommunication();
        public void onRecolorUnansweredQuestions();
    }
}
