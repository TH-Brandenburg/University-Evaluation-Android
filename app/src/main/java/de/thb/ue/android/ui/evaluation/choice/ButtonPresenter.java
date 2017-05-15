package de.thb.ue.android.ui.evaluation.choice;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.thb.ue.android.data.IDataManager;
import de.thb.ue.android.data.VOs.ChoiceVO;
import de.thb.ue.android.injection.ActivityContext;
import de.thb.ue.android.ui.base.BasePresenter;
import de.thb.ue.android.ui.evaluation.EvaluationMvpView;
import de.thb.ue.android.utility.customized_classes.SingleChoiceButton;
import eu.davidea.flexibleadapter.items.IFlexible;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import thb.de.ue.android.R;

/**
 * Created by scorp on 05.05.2017.
 */

public class ButtonPresenter extends BasePresenter<ButtonMvpView> {

    private IDataManager mDataManager;
    private Context mContext;
    private int mPosition;
    private List<ChoiceVO> mChoices;
//    private List<ButtonItem> mButtonItems;
    private String mQuestion;

    @Inject
    public ButtonPresenter(IDataManager dataManager, @ActivityContext Context context) {
        mDataManager = dataManager;
        mContext = context;
//        mButtonItems = new ArrayList<>(7);
    }

    /**
     * Returns the number of choices.
     * The "no comment" choice is excluded.
     * @return
     */
    int getRealChoiceCount(){
        if(hasNoCommentOption()){
            return mChoices.size() - 1;
        } else {
            return mChoices.size();
        }
    }

    /**
     * Returns the grade of the choice at the specified mPosition within the collection of choices.
     * @param index mPosition of the choice within the collection of choices
     * @return
     */
    int getChoiceGrade(int index){
        return mChoices.get(index).getGrade();
    }

    String getChoiceText(int index) { return mChoices.get(index).getChoiceText(); }

    ChoiceVO getChoice(int index) { return mChoices.get(index); }

    @Nullable
    ChoiceVO getChoiceByGrade(int grade) {
        for(ChoiceVO choice : mChoices){
            if(choice.getGrade() == grade){
                return choice;
            }
        }

        return null;
    }

    boolean hasNoCommentOption(){
        for(ChoiceVO choice : mChoices){
            if(choice.getGrade() == 0){
                return true;
            }
        }

        return false;
    }

    boolean hasOptimumInMiddle(){
        for(ChoiceVO choice : mChoices){
            if(choice.getGrade() < 0){
                return true;
            }
        }

        return false;
    }

    void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    void setmChoices(List<ChoiceVO> mChoices) {
        this.mChoices = mChoices;
    }

    String getmQuestion() {
        return mQuestion;
    }

    void setmQuestion(String mQuestion) {
        this.mQuestion = mQuestion;
    }

//    void toggleButton(){
//        toggleButton(getCurrentAnswer());
//    }

    void answerQuestion(ChoiceVO choice) {
        mDataManager.putSCAnswer(mQuestion, choice)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    ChoiceVO getCurrentAnswer(){
        return mDataManager.getCurrentScAnswer(mQuestion);
    }

//    void generateChoiceButtons(){
//        if(!hasOptimumInMiddle()){
//            switch (getRealChoiceCount()){
//                case 2:
//                    confTwoChoice();
//                    break;
//                case 3:
//                    confThreeChoice();
//                    break;
//                case 4:
//                    confFourChoice();
//                    break;
//                case 5:
//                    confFiveChoice();
//                    break;
//                case 6:
//                    confSixChoice();
//                    break;
//                case 7:
//                    confSevenChoice();
//                    break;
//            }
//        } else {
//            switch (getRealChoiceCount()){
//                case 3:
//                    confThreeChoiceMiddle();
//                    break;
//                case 5:
//                    confFiveChoiceMiddle();
//                    break;
//                case 7:
//                    confSevenChoiceMiddle();
//                    break;
//            }
//        }
//    }
//
//
//    private void confSevenChoiceMiddle(){
//
//        mButtonItems.add(createVeryNegativeButton(getChoiceByGrade(7)));
//        mButtonItems.add(createSlightlyNegativeButton(getChoiceByGrade(5)));
//        mButtonItems.add(createSlightlyPositiveButton(getChoiceByGrade(3)));
//        mButtonItems.add(createVeryPositiveButton(getChoiceByGrade(1)));
//        mButtonItems.add(createSlightlyPositiveButton(getChoiceByGrade(-3)));
//        mButtonItems.add(createSlightlyNegativeButton(getChoiceByGrade(-5)));
//        mButtonItems.add(createVeryNegativeButton(getChoiceByGrade(-7)));
//
//        getMvpView().updateDataSet(mButtonItems);
//    }
//
//    private void confSevenChoice() {
//        mButtonItems.add(createVeryPositiveButton(getChoiceByGrade(1)));
//        mButtonItems.add(createPositiveButton(getChoiceByGrade(2)));
//        mButtonItems.add(createSlightlyPositiveButton(getChoiceByGrade(3)));
//        mButtonItems.add(createNeutralButton(getChoiceByGrade(4)));
//        mButtonItems.add(createSlightlyNegativeButton(getChoiceByGrade(5)));
//        mButtonItems.add(createNegativeButton(getChoiceByGrade(6)));
//        mButtonItems.add(createVeryNegativeButton(getChoiceByGrade(7)));
//
//        getMvpView().updateDataSet(mButtonItems);
//    }
//
//    private void confSixChoice() {
//        mButtonItems.add(createVeryPositiveButton(getChoiceByGrade(1)));
//        mButtonItems.add(createPositiveButton(getChoiceByGrade(2)));
//        mButtonItems.add(createSlightlyPositiveButton(getChoiceByGrade(3)));
//        mButtonItems.add(createSlightlyNegativeButton(getChoiceByGrade(4)));
//        mButtonItems.add(createNegativeButton(getChoiceByGrade(5)));
//        mButtonItems.add(createVeryNegativeButton(getChoiceByGrade(6)));
//
//        getMvpView().updateDataSet(mButtonItems);
//    }
//
//    private void confFiveChoiceMiddle(){
//        mButtonItems.add(createVeryNegativeButton(getChoiceByGrade(5)));
//        mButtonItems.add(createNegativeButton(getChoiceByGrade(3)));
//        mButtonItems.add(createVeryPositiveButton(getChoiceByGrade(1)));
//        mButtonItems.add(createNegativeButton(getChoiceByGrade(-3)));
//        mButtonItems.add(createVeryNegativeButton(getChoiceByGrade(-5)));
//
//        getMvpView().updateDataSet(mButtonItems);
//    }
//
//    private void confFiveChoice() {
//        mButtonItems.add(createVeryPositiveButton(getChoiceByGrade(1)));
//        mButtonItems.add(createPositiveButton(getChoiceByGrade(2)));
//        mButtonItems.add(createNeutralButton(getChoiceByGrade(3)));
//        mButtonItems.add(createNegativeButton(getChoiceByGrade(4)));
//        mButtonItems.add(createVeryNegativeButton(getChoiceByGrade(5)));
//
//        getMvpView().updateDataSet(mButtonItems);
//    }
//
//    private void confFourChoice() {
//        mButtonItems.add(createVeryPositiveButton(getChoiceByGrade(1)));
//        mButtonItems.add(createSlightlyPositiveButton(getChoiceByGrade(2)));
//        mButtonItems.add(createSlightlyNegativeButton(getChoiceByGrade(3)));
//        mButtonItems.add(createVeryNegativeButton(getChoiceByGrade(4)));
//
//        getMvpView().updateDataSet(mButtonItems);
//    }
//
//    private void confThreeChoiceMiddle() {
//        mButtonItems.add(createVeryNegativeButton(getChoiceByGrade(3)));
//        mButtonItems.add(createVeryPositiveButton(getChoiceByGrade(1)));
//        mButtonItems.add(createVeryNegativeButton(getChoiceByGrade(-3)));
//
//        getMvpView().updateDataSet(mButtonItems);
//
//    }
//
//    private void confThreeChoice() {
//        mButtonItems.add(createVeryPositiveButton(getChoiceByGrade(1)));
//        mButtonItems.add(createNeutralButton(getChoiceByGrade(2)));
//        mButtonItems.add(createVeryNegativeButton(getChoiceByGrade(3)));
//
//        getMvpView().updateDataSet(mButtonItems);
//    }
//
//    private void confTwoChoice() {
//        mButtonItems.add(createVeryPositiveButton(getChoiceByGrade(1)));
//        mButtonItems.add(createVeryNegativeButton(getChoiceByGrade(2)));
//
//        getMvpView().updateDataSet(mButtonItems);
//    }
//
//    private void buttonAction(SingleChoiceButton v) {
//        SingleChoiceButton b = v;
//        answerQuestion(b.getmChoice());
//        getMvpView().nextPage();
//        toggleButton(b.getmChoice());
//    }

//    private void toggleButton(ChoiceVO choice) {
//        for(int i = 0; i < mButtonItems.size(); i++){
//            ButtonItem button = mButtonItems.get(i);
//            button.setmIsSelected(false);
//            if(choice.equals(button.getmChoice())){
//                button.setmIsSelected(true);
//            }
//        }
//    }

//    private ButtonItem createVeryNegativeButton(ChoiceVO choice){
//        return new ButtonItem(choice.getChoiceText(), choice
//                , R.drawable.very_negative_btn_state_list
//                , R.color.color_bright_font
//                , v -> {
//            buttonAction((SingleChoiceButton) v);
//        });
//    }
//
//    private ButtonItem createNegativeButton(ChoiceVO choice){
//        return new ButtonItem(choice.getChoiceText(), choice
//                , R.drawable.negative_btn_state_list
//                , R.color.color_bright_font
//                , v -> {
//            buttonAction((SingleChoiceButton) v);
//        });
//    }
//
//    private ButtonItem createSlightlyNegativeButton(ChoiceVO choice){
//        return new ButtonItem(choice.getChoiceText(), choice
//                , R.drawable.slightly_negative_btn_state_list
//                , R.color.color_bright_font
//                , v -> {
//            buttonAction((SingleChoiceButton) v);
//        });
//    }
//
//    private ButtonItem createNeutralButton(ChoiceVO choice){
//        return new ButtonItem(choice.getChoiceText(), choice
//                , R.drawable.neutral_btn_state_list
//                , R.color.color_grey_font
//                , v -> {
//            buttonAction((SingleChoiceButton) v);
//        });
//    }
//
//    private ButtonItem createSlightlyPositiveButton(ChoiceVO choice){
//        return new ButtonItem(choice.getChoiceText(), choice
//                , R.drawable.slightly_positive_btn_state_list
//                , R.color.color_bright_font
//                , v -> {
//            buttonAction((SingleChoiceButton) v);
//        });
//    }
//
//    private ButtonItem createPositiveButton(ChoiceVO choice){
//        return new ButtonItem(choice.getChoiceText(), choice
//                , R.drawable.positive_btn_state_list
//                , R.color.color_bright_font
//                , v -> {
//            buttonAction((SingleChoiceButton) v);
//        });
//    }
//
//    private ButtonItem createVeryPositiveButton(ChoiceVO choice){
//        return new ButtonItem(choice.getChoiceText(), choice
//                , R.drawable.very_positive_btn_state_list
//                , R.color.color_bright_font
//                , v -> {
//            buttonAction((SingleChoiceButton) v);
//        });
//    }
}
