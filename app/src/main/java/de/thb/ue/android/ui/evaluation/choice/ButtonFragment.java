package de.thb.ue.android.ui.evaluation.choice;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.thb.ue.android.data.VOs.ChoiceVO;
import de.thb.ue.android.ui.base.BaseFragment;
import thb.de.ue.android.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ButtonFragment extends BaseFragment implements ButtonMvpView{
    private static final String POSITION = "POSITION";
    private static final String QUESTION = "QUESTION";
    private static final String CHOICES = "CHOICES";

    @Inject
    ButtonPresenter mPresenter;

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

        mFragmentComponent.bind(this);
        mPresenter.attachView(this);

        if (this.getArguments() != null) {
            Bundle args = this.getArguments();
            mPresenter.setmPosition(args.getInt(POSITION));
            mPresenter.setmQuestion(args.getString(QUESTION));
            mPresenter.setmChoices((ArrayList<ChoiceVO>) args.getSerializable(CHOICES));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view;

        // this is a hack! Unfortunately the Presenter is initialized to late.
//        boolean commentOption = false;
//        Bundle args = this.getArguments();
//        for(ChoiceVO choice : (ArrayList<ChoiceVO>) args.getSerializable(CHOICES)){
//            if(choice.getGrade() == 0){
//                commentOption = true;
//            }
//        }


        if(mPresenter.questionGotNoCommentOption()){
            view = NoCommentLayoutChooser(inflater, container);
        } else {
            view = NoCommentlessLayoutChooser(inflater, container);
        }

        mViewUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
    private View NoCommentlessLayoutChooser(LayoutInflater inflater, ViewGroup container) {
        View view = null;
        if((mPresenter.getChoiceCount()) % 2 == 1 && mPresenter.getChoiceGrade((int)Math.floor(mPresenter.getChoiceCount() / 2)) == 1){
            switch(mPresenter.getChoiceCount()){
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
            switch(mPresenter.getChoiceCount()){
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
        if((mPresenter.getChoiceCount()-1) % 2 == 1 && mPresenter.getChoiceGrade(mPresenter.getChoiceCount() / 2) == 1){
            switch(mPresenter.getChoiceCount()-1){
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
            switch(mPresenter.getChoiceCount()-1){
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

}
