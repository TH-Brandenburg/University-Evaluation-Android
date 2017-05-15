package de.thb.ue.android.ui.evaluation.choice;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.thb.ue.android.data.VOs.ChoiceVO;
import de.thb.ue.android.ui.base.BaseFragment;
import de.thb.ue.android.ui.evaluation.EvaluationActivity;
import de.thb.ue.android.ui.evaluation.EvaluationMvpView;
import de.thb.ue.android.utility.customized_classes.BaseFlexibleAdapter;
import de.thb.ue.android.utility.customized_classes.SingleChoiceButton;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import eu.davidea.flexibleadapter.items.IFlexible;
import thb.de.ue.android.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ButtonFragment extends BaseFragment implements ButtonMvpView{
    private static final String POSITION = "POSITION";
    private static final String QUESTION = "QUESTION";
    private static final String CHOICES = "CHOICES";

    @BindView(R.id.question_text_view)
    TextView mQuestionTextView;

//    @BindView(R.id.button_recycler_view)
//    RecyclerView mRecyclerView;

//    @BindView(R.id.no_comment_button)
//    SingleChoiceButton mNoCommentButton;

    @Inject
    ButtonPresenter mPresenter;

    @Inject
    BaseFlexibleAdapter mFlexibleAdapter;

//    @Inject
//    SmoothScrollLinearLayoutManager mSmoothLinearLayoutManager;

    Unbinder mViewUnbinder;

    public ButtonFragment() {
        // Required empty public constructor
    }

    public static ButtonFragment newInstance(int position, String question, ArrayList<ChoiceVO> choices) {
        ButtonFragment fragment = new ButtonFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        args.putString(QUESTION, question);
        args.putSerializable(CHOICES, choices);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //this is a hack!
        //this logic should not be here but in the presenter instead
        //to resolve this we need a new layout strategy
//        if (this.getArguments() != null) {
//            Bundle args = this.getArguments();
//            List<ChoiceVO> choices = (ArrayList<ChoiceVO>) args.getSerializable(CHOICES);
//            mChoiceCount = choices.size();
//            for(ChoiceVO choice : choices){
//                if(choice.getGrade() == 0){
//                    mCommentOption = true;
//                }
//            }
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        View view = inflater.inflate(R.layout.fragment_button, container, false);

        View view;
        int choiceCount = 0;
        int middleGrade = 0;
        boolean hasNoCommentOption = false;

        // a bit ugly that this is needed
        // unfortunately during this call the activity is not present yet
        // and thus the Presenter is not initalized.
        // As the interface of this fragment depends on external data,
        // normally acessible through the presenter,
        // this would otherwise lead to NPEs.

        // a solution to this would be to dynamically generate the interface contents based on the data.
        // this was tried but was found to be too ressource demanding.
        if (this.getArguments() != null) {
            Bundle args = this.getArguments();
            List<ChoiceVO> choices = (ArrayList<ChoiceVO>) args.getSerializable(CHOICES);
            choiceCount = choices.size();
            middleGrade = choices.get((int)Math.floor(choiceCount / 2)).getGrade();
            for(ChoiceVO choice : choices){
                if(choice.getGrade() == 0){
                    hasNoCommentOption = true;
                }
            }
        }

        if(hasNoCommentOption){
            view = NoCommentLayoutChooser(inflater, container, choiceCount, middleGrade);
        } else {
            view = NoCommentlessLayoutChooser(inflater, container, choiceCount, middleGrade);
        }
        mViewUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentComponent.bind(this);
        mPresenter.attachView(this);

        if (this.getArguments() != null) {
                Bundle args = this.getArguments();
                mPresenter.setmPosition(args.getInt(POSITION));
                mPresenter.setmQuestion(args.getString(QUESTION));
                mPresenter.setmChoices((ArrayList<ChoiceVO>) args.getSerializable(CHOICES));
        }

//        mRecyclerView.setLayoutManager(mSmoothLinearLayoutManager);
//        mRecyclerView.setAdapter(mFlexibleAdapter);

//        mFlexibleAdapter.setAnimationOnScrolling(false);

//        mPresenter.generateChoiceButtons();

        mQuestionTextView.setText(mPresenter.getmQuestion());

//        if(mPresenter.hasNoCommentOption()){

//            mNoCommentButton.setVisibility(View.VISIBLE);
//            mNoCommentButton.setmChoice(mPresenter.getChoiceByGrade(0));
//            mNoCommentButton.setText(mPresenter.getChoiceByGrade(0).getChoiceText());

//            mNoCommentButton.addOnClickListener(v -> buttonAction((SingleChoiceButton) v));
//      }

        List<SingleChoiceButton> buttons = getButtonsOfLayout(getView());

        for(int i = 0; i < buttons.size(); i++){
            buttons.get(i).setText(mPresenter.getChoiceText(i));
            buttons.get(i).setmChoice(mPresenter.getChoice(i));
            buttons.get(i).addOnClickListener(v -> buttonAction((SingleChoiceButton) v, buttons));
        }

        for(SingleChoiceButton button : buttons){

            if(mPresenter.getCurrentAnswer().equals(button.getmChoice())){
                button.setSelected(true);
            } else {
                button.setSelected(false);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewUnbinder.unbind();
        mPresenter.detachView();
    }


    /**
     * Choose a layout without no comment button
     * @param inflater
     * @param container
     */
    private View NoCommentlessLayoutChooser(LayoutInflater inflater, ViewGroup container, int choiceCount, int middleGrade) {
        View view = null;
        if((choiceCount) % 2 == 1 && middleGrade == 1){
            switch(choiceCount){
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
            switch(choiceCount){
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
    private View NoCommentLayoutChooser(LayoutInflater inflater, ViewGroup container, int choiceCount, int middleGrade) {
        View view = null;
        // Inflate layout corresponding to number of choices
        // Choose a layout where central button is very positive answer if choice in the middle has grade of 1
        if((choiceCount-1) % 2 == 1 && middleGrade == 1){
            switch(choiceCount-1){
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
            switch(choiceCount-1){
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


    private List<SingleChoiceButton> getButtonsOfLayout(View rootView){
        ViewGroup viewGroup = (ViewGroup) rootView;
        List<SingleChoiceButton> resultList = new ArrayList<>(6);


        for(int i = 0; i < viewGroup.getChildCount(); i++){
            View view = viewGroup.getChildAt(i);
            if (view instanceof SingleChoiceButton){
                resultList.add((SingleChoiceButton) view);
            }

            if(view instanceof ScrollView){
                View layout = ((ViewGroup) view).getChildAt(0);
                for(int j = 0; j < ((ViewGroup)layout).getChildCount(); j++){
                    View view2 = ((ViewGroup) layout).getChildAt(j);
                    if(view2 instanceof SingleChoiceButton){
                        resultList.add((SingleChoiceButton) view2);
                    }
                }
            }
        }
        return resultList;
    }

//    public void updateDataSet(List<ButtonItem> items){
//        mFlexibleAdapter.updateDataSet(items, false);
//    }
//
//    public void updateButton(int position){
//        mFlexibleAdapter.notifyItemChanged(position);
//    }

    @Override
    public void nextPage() {
        ((EvaluationActivity) getActivity()).nextPage();
    }

    private void buttonAction(SingleChoiceButton v, List<SingleChoiceButton> buttons) {
        SingleChoiceButton b = v;
        mPresenter.answerQuestion(b.getmChoice());
        nextPage();
        toggleButton(b.getmChoice(), buttons);
    }

    private void toggleButton(ChoiceVO choice, List<SingleChoiceButton> buttons) {
        for(int i = 0; i < buttons.size(); i++){
            SingleChoiceButton button = buttons.get(i);
            button.setSelected(false);
            if(choice.equals(button.getmChoice())){
                button.setSelected(true);
            }
        }
    }
}
