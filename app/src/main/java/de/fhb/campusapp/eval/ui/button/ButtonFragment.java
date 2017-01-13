package de.fhb.campusapp.eval.ui.button;


import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.fhb.campusapp.eval.interfaces.PagerAdapterPageEvent;
import de.fhb.campusapp.eval.interfaces.PagerAdapterSetPrimary;
import de.fhb.campusapp.eval.ui.base.BaseActivity;
import de.fhb.campusapp.eval.ui.base.BaseFragment;
import de.fhb.campusapp.eval.utility.DataHolder;
import de.fhb.campusapp.eval.utility.EventBus;
import de.fhb.campusapp.eval.utility.Events.ClickedChoiceButtonEvent;
import de.fhb.campusapp.eval.utility.Utility;
import de.fhb.campusapp.eval.utility.vos.ChoiceVO;
import de.fhb.campusapp.eval.utility.vos.MultipleChoiceAnswerVO;
import fhb.de.campusappevaluationexp.R;

public class ButtonFragment extends BaseFragment implements ButtonMvpView, PagerAdapterPageEvent {
    public static final String QUESTION = "QUESTION";
    public static final String CHOICES = "CHOICES";
    public static final String POSITION = "POSITION";

    private String mQuestion;
    private ArrayList<ChoiceVO> mChoices;
    private ArrayList<Button> mButtons;
    private int mPosition;
    private View mRootView;
    private ViewTreeObserver.OnGlobalLayoutListener mGlobalObserver;

    @Inject
    ButtonPresenter mButtonPresenter;

    @Inject
    DisplayMetrics mDisplayMetrics;

    @BindView(R.id.question_text_view)
    TextView mQuestionTextView;

    public static ButtonFragment newInstance(String question, ArrayList<ChoiceVO> choices, int position) {
        ButtonFragment fragment = new ButtonFragment();
        Bundle args = new Bundle();
        args.putString(QUESTION, question);
        args.putSerializable(CHOICES, choices);
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public ButtonFragment() {
        // Required empty public constructor
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && getActivity() != null){
            Utility.setKeyboardOverlapping(getActivity());
        }
    }

    //********************** Life cycle methods ***************************

    @Override
    public void onAttach(Activity activity){
        ((BaseActivity)getActivity()).mActicityComponent.bind(this);
        mButtonPresenter.attachView(this);

        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(this.getArguments() != null){
            Bundle args = this.getArguments();
            mQuestion =  args.getString(QUESTION);
            mChoices = (ArrayList<ChoiceVO>) args.getSerializable(CHOICES);
            mPosition = args.getInt(POSITION);
        }

        mDisplayMetrics = getResources().getDisplayMetrics();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view;

        if(hasNoCommentOption()){
           view = NoCommentLayoutChooser(inflater, container);
        } else {
           view = NoCommentlessLayoutChooser(inflater, container);
        }

        ButterKnife.bind(this, view);
        mGlobalObserver = initObserver(view);
        mRootView = view;
        view.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalObserver);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mQuestionTextView.setText(mQuestion);
        mQuestionTextView.setMovementMethod(new ScrollingMovementMethod());
    }



    @Override
    public void onDestroyView() {
        mButtonPresenter.detachView();
        if(mRootView != null){
            if(Build.VERSION.SDK_INT < 16){
                mRootView.getViewTreeObserver().removeGlobalOnLayoutListener(mGlobalObserver);
            } else {
                mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(mGlobalObserver);
            }
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //************************ interface implementations ***********************


    @Override
    public void onGettingPrimary(int oldPosition) {
        DataHolder.setCurrentPagerPosition(mPosition);
        DataHolder.setCurrentQuestion(mQuestion);
    }

    @Override
    public void onLeavingPrimary(int newPosition) {

    }

    //************************ utility methods ***********************

    private boolean hasNoCommentOption(){
        for(ChoiceVO choice : mChoices){
            if(choice.getGrade() == 0){
                return true;
            }
        }
        return false;
    }

    private void hideAllButtons(){
        if (mButtons != null && mButtons.size() > 0) {
            for (Button button : mButtons) {
                button.setVisibility(View.GONE);
            }
        } else {
            Log.e("BUTTONERROR", "button could not be initialised! List of buttons was null or empty");
        }
    }

    private void showAllButtons(){
        if (mButtons != null && mButtons.size() > 0) {
            for (Button button : mButtons) {
                button.setVisibility(View.VISIBLE);
            }
        } else {
            Log.e("BUTTONERROR", "button could not be initialised! List of buttons was null or empty");
        }
    }

    /**
     * Choose a layout without no comment button
     * @param inflater
     * @param container
     */
    private View NoCommentlessLayoutChooser(LayoutInflater inflater, ViewGroup container) {
        View view = null;
        if((mChoices.size()) % 2 == 1 && mChoices.get((int)Math.floor(mChoices.size() / 2)).getGrade() == 1){
            switch(mChoices.size()){
                case 3:
                    view = inflater.inflate(R.layout.fragment_button_3_positive_middle, container, false);
                    break;
                case 5:
                    view = inflater.inflate(R.layout.fragment_button_5_positive_middle, container, false);
                    break;
                case 7:
                    view = inflater.inflate(R.layout.fragment_button_7_positive_middle, container, false);
                    break;
            }
            // choose a layout where top button is very positive answer if above does not apply
        } else {
            switch(mChoices.size()){
                case 2:
                    view = inflater.inflate(R.layout.fragment_button_2, container, false);
                    break;
                case 3:
                    view = inflater.inflate(R.layout.fragment_button_3, container, false);
                    break;
                case 4:
                    view = inflater.inflate(R.layout.fragment_button_4, container, false);
                    break;
                case 5:
                    view = inflater.inflate(R.layout.fragment_button_5, container, false);
                    break;
                case 6:
                    view = inflater.inflate(R.layout.fragment_button_6, container, false);
                    break;
                case 7:
                    view = inflater.inflate(R.layout.fragment_button_7, container, false);
                    break;
            }
        }
        return view;
    }

    /**
     * Choose a layout containing a no comment button.
     * @param inflater
     * @param container
     */
    private View NoCommentLayoutChooser(LayoutInflater inflater, ViewGroup container) {
        View view = null;
        // Inflate layout corresponding to number of choices
        // Choose a layout where central button is very positive answer if choice in the middle has grade of 1
        if((mChoices.size()-1) % 2 == 1 && mChoices.get(mChoices.size() / 2).getGrade() == 1){
            switch(mChoices.size()-1){
                case 3:
                    view = inflater.inflate(R.layout.fragment_button_3_positive_middle_nc, container, false);
                    break;
                case 5:
                    view = inflater.inflate(R.layout.fragment_button_5_positive_middle_nc, container, false);
                    break;
                case 7:
                    view = inflater.inflate(R.layout.fragment_button_7_positive_middle_nc, container, false);
                    break;
            }
            // choose a layout where top button is very positive answer if above does not apply
        } else {
            switch(mChoices.size()-1){
                case 2:
                    view = inflater.inflate(R.layout.fragment_button_2_nc, container, false);
                    break;
                case 3:
                    view = inflater.inflate(R.layout.fragment_button_3_nc, container, false);
                    break;
                case 4:
                    view = inflater.inflate(R.layout.fragment_button_4_nc, container, false);
                    break;
                case 5:
                    view = inflater.inflate(R.layout.fragment_button_5_nc, container, false);
                    break;
                case 6:
                    view = inflater.inflate(R.layout.fragment_button_6_nc, container, false);
                    break;
                case 7:
                    view = inflater.inflate(R.layout.fragment_button_7_nc, container, false);
                    break;
            }
        }
        return view;
    }

    private ViewTreeObserver.OnGlobalLayoutListener initObserver(final View rootView){
        return new ViewTreeObserver.OnGlobalLayoutListener() {
            boolean buttonsConfigured = false;

            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int heightDiff = rootView.getRootView().getHeight() - (r.bottom - r.top);
                heightDiff = Utility.convertPixelsToDp(heightDiff, mDisplayMetrics);

                // get all buttons within the layout
                mButtons = getButtonsOfLayout(rootView);
                if(!buttonsConfigured){
                    configureButtons();
                    buttonsConfigured = true;
                }

                if (heightDiff > 100) { // if more than 100 dp, its probably a keyboard...
                    //ok now we know the keyboard is up...
                    hideAllButtons();
                }else{
                    //ok now we know the keyboard is down...
                    showAllButtons();
                }
            }
        };
    }

    private void configureButtons() {
        int choiceCounter = 0;
        if (mButtons != null && mButtons.size() > 0) {
            for (Button button : mButtons) {
                button.setHeight(20);
                button.setText(mChoices.size() > choiceCounter ? mChoices.get(choiceCounter).getChoiceText() : "");
                button.setOnClickListener(new InnerOnClickListener());
                // always select the button which was previously clicked on this question
                for (MultipleChoiceAnswerVO dto : DataHolder.getAnswersVO().getMcAnswers()){
                    if(dto.getQuestionText().equals(mQuestion) && button.getText().equals(dto.getChoice().getChoiceText())){
                        toggleSelectedButton(button);
                    }
                }
                choiceCounter++;
            }

        } else {
            Log.e("BUTTONERROR", "button could not be initialised! List of buttons was null or empty");
        }
    }

    private ArrayList<Button> getButtonsOfLayout(View rootView){
        ViewGroup viewGroup = (ViewGroup) rootView;
        ArrayList<Button> resultList = new ArrayList<>(6);


        for(int i = 0; i < viewGroup.getChildCount(); i++){
            View view = viewGroup.getChildAt(i);
            if (view instanceof Button){
                resultList.add((Button) view);
            }

            if(view instanceof ScrollView){
                View layout = ((ViewGroup) view).getChildAt(0);
                for(int j = 0; j < ((ViewGroup)layout).getChildCount(); j++){
                    View view2 = ((ViewGroup) layout).getChildAt(j);
                    if(view2 instanceof Button){
                        resultList.add((Button) view2);
                    }
                }
            }
        }
        return resultList;
    }

    private class InnerOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            toggleSelectedButton((Button) view);

            String choiceText = ((Button) view).getText().toString();
            mButtonPresenter.processButtonClick(mQuestion, choiceText);

            EventBus.get().post(new ClickedChoiceButtonEvent());
            //notify navigationList that a new answer was given
            ((PagerAdapterSetPrimary) getActivity()).setPrimaryFragment(mPosition + 1);
        }
    }

    private void toggleSelectedButton(Button selectedButton){
        for(Button deselectedButton : mButtons){
            deselectedButton.setSelected(false);
        }
        selectedButton.setSelected(true);

    }

}
