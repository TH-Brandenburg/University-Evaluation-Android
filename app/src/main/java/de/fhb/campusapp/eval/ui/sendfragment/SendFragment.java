package de.fhb.campusapp.eval.ui.sendfragment;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.fhb.campusapp.eval.ui.EvaluationApplication;
import de.fhb.campusapp.eval.ui.base.BaseActivity;
import de.fhb.campusapp.eval.ui.base.BaseFragment;
import de.fhb.campusapp.eval.ui.eval.EvaluationActivity;
import de.fhb.campusapp.eval.utility.DialogFactory;
import de.fhb.campusapp.eval.utility.Utility;
import fhb.de.campusappevaluationexp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SendFragment extends BaseFragment implements SendMvpView{

    public static final String POSITION = "POSITION";

    @BindView(R.id.send_button)
    Button mSendButton;

    private int mPosition;
    private SendFragmentCommunicator mActivityCommunicator;

    @Inject
    public SendPresenter mSendPresenter;

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
        if(isVisibleToUser && getActivity() != null){
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
        ((BaseActivity)getActivity()).mActicityComponent.bind(this);

        if(this.getArguments() != null){
            Bundle args = this.getArguments();
            mPosition = args.getInt(POSITION);
        }

//        mSendPresenter = new SendPresenter();
        mSendPresenter.attachView(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_send, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroy() {
        mSendPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSendButton.setOnClickListener(view1 -> mSendPresenter.sendButtonPressed());
    }

    @Override
    public void recolorUnansweredQuestions() {
        mActivityCommunicator.onRecolorUnansweredQuestions();
    }

    @Override
    public void onPreServerCommunication() {
        mActivityCommunicator.onPreServerCommunication();
    }

    /*****************************************
     * SendMvpView Interface Implementations *
     *****************************************/

    @Override
    public void showSubjectNotChosenDialog() {
        Dialog dialog = DialogFactory.createSimpleOkErrorDialog(getActivity()
                , R.string.subject_not_chosen_title
                , R.string.subject_not_chosen_message
                , true);

        dialog.show();
    }

    @Override
    public void showQuestionsNotAnsweredDialog(int answered, int total) {
        Dialog dialog = DialogFactory.createAcceptDenyDialog(getActivity()
                , ""
                , getResources().getString(R.string.send_dialogue_answers_uncomplete, answered, total)
                , getResources().getString(R.string.send_anyway_button)
                , getResources().getString(R.string.abort_button)
                , true
                , (dialogInterface, i) -> { onPreServerCommunication(); } //accept
                , null
                , null);
        dialog.show();
    }

    public interface SendFragmentCommunicator{
        void onPreServerCommunication();
        void onRecolorUnansweredQuestions();
    }
}
