package de.fhb.campusapp.eval.fragments;


import android.app.Activity;
import android.graphics.Color;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

import de.fhb.campusapp.eval.interfaces.PagerAdapterPageEvent;
import de.fhb.campusapp.eval.interfaces.PagerAdapterSetPrimary;
import de.fhb.campusapp.eval.utility.DataHolder;
import de.fhb.campusapp.eval.utility.EventBus;
import de.fhb.campusapp.eval.utility.Events.ClickedChoiceButtonEvent;
import de.fhb.campusapp.eval.utility.Utility;
import de.fhb.campusapp.eval.utility.vos.ChoiceVO;
import de.fhb.campusapp.eval.utility.vos.MultipleChoiceAnswerVO;
import fhb.de.campusappevaluationexp.R;
import roboguice.inject.InjectView;

public class ButtonFragment extends BaseFragment implements PagerAdapterPageEvent {
    public static final String QUESTION = "QUESTION";
    public static final String CHOICES = "CHOICES";
    public static final String POSITION = "POSITION";

    private String mQuestion;
    private ArrayList<ChoiceVO> mChoices;
    private ArrayList<Button> mButtons;
    private int mPosition;
    private View mRootView;
    private ViewTreeObserver.OnGlobalLayoutListener mGlobalObserver;
    private DisplayMetrics mDisplayMetrics;

    @InjectView(R.id.question_text_view)
    private TextView mQuestionTextView;

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

    @Override
    public void onAttach(Activity activity) {
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

        //just add a new entry for this fragments question into answers dto
//        MultipleChoiceAnswerDTO mcAnswer = DataHolder.isMcQuestionAnswered(mQuestion);
//        if(mcAnswer == null){
//            DataHolder.getAnswersVO().getMcAnswers().add(new MultipleChoiceAnswerDTO(mQuestion, DataHolder.retrieveChoiceByGrade(mQuestion, 0)));
//        }
        mDisplayMetrics = getResources().getDisplayMetrics();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout corresponding to number of choices
        // Choose a layout where central button is very positive answer if choice in the middle has grade of 1
        if((mChoices.size()-1) % 2 == 1 && mChoices.get((int)Math.ceil(mChoices.size() / 2)).getGrade() == 1){
            switch(mChoices.size()-1){
                case 3:
                    mRootView = inflater.inflate(R.layout.fragment_button_3_positive_middle, container, false);
                    break;
                case 5:
                    mRootView = inflater.inflate(R.layout.fragment_button_5_positive_middle, container, false);
                    break;
                case 7:
                    mRootView = inflater.inflate(R.layout.fragment_button_7_positive_middle, container, false);
                    break;
            }
        } else {
            switch(mChoices.size()-1){
                case 2:
                    mRootView = inflater.inflate(R.layout.fragment_button_2, container, false);
                    break;
                case 3:
                    mRootView = inflater.inflate(R.layout.fragment_button_3, container, false);
                    break;
                case 4:
                    mRootView = inflater.inflate(R.layout.fragment_button_4, container, false);
                    break;
                case 5:
                    mRootView = inflater.inflate(R.layout.fragment_button_5, container, false);
                    break;
                case 6:
                    mRootView = inflater.inflate(R.layout.fragment_button_6, container, false);
                    break;
                case 7:
                    mRootView = inflater.inflate(R.layout.fragment_button_7, container, false);
                    break;
            }
        }

        mGlobalObserver = initObserver(mRootView);
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalObserver);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mQuestionTextView.setText(mQuestion);
        mQuestionTextView.setMovementMethod(new ScrollingMovementMethod());

      /*  if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mQuestionTextView.setMaxLines(2);
        } else if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            mQuestionTextView.setMaxLines(2);
        }*/
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
//                button.setHeight((mWindowHeight - mQuestionTextView.getHeight()) / mButtons.size());
                button.setHeight(20);
//                if(mChoices.get(choiceCounter).getGrade() == 0 && mChoices.get(choiceCounter).getChoiceText() != null){
//                      StringBuilder builder = new StringBuilder();
//                    for(int i = 0; i < mChoices.get(choiceCounter).getChoiceText().length(); i++){
//                        builder.append(mChoices.get(choiceCounter).getChoiceText().charAt(i));
//                        builder.append("\n");
//                    }
//                    button.setText(builder.toString());
//                } /*else {*/
                button.setText(mChoices.size() > choiceCounter ? mChoices.get(choiceCounter).getChoiceText() : "");
//                }
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

    private void hideSoftKeyboard(){
        if(this.getView() != null){
            Utility.hideSoftKeyboard(this.getView(), getActivity());
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

    @Override
    public void onDestroyView() {
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

    @Override
    public void onGettingPrimary(int oldPosition) {

    }

    @Override
    public void onLeavingPrimary(int newPosition) {

    }

    private class InnerOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            toggleSelectedButton((Button) view);
            // add answer to list of answers in AnswersDTO after button has been clicked.
            MultipleChoiceAnswerVO dto = DataHolder.isMcQuestionAnswered(mQuestion);

          /*  // remove \n commands from "no comment" button
           String choiceText = ((Button) view).getText().toString().replaceAll("\\n", "");*/
            String choiceText = ((Button) view).getText().toString();

            if(dto == null){
                ChoiceVO choiceVO = DataHolder.retrieveChoiceVO(mQuestion, choiceText);
                DataHolder.getAnswersVO().getMcAnswers().add(new MultipleChoiceAnswerVO(mQuestion, choiceVO));
            } else {
                ChoiceVO choiceVO = DataHolder.retrieveChoiceVO(mQuestion, choiceText);
                dto.setChoice(choiceVO);
            }
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

//    public interface ButtonFragmentCommunicator{
//        public void questionAnswered();
//    }

}
