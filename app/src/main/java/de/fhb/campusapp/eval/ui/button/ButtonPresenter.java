package de.fhb.campusapp.eval.ui.button;


import javax.inject.Inject;

import de.fhb.campusapp.eval.data.IDataManager;
import de.fhb.campusapp.eval.ui.base.BasePresenter;
import de.fhb.campusapp.eval.utility.vos.ChoiceVO;
import de.fhb.campusapp.eval.utility.vos.MultipleChoiceAnswerVO;

/**
 * Created by Sebastian Müller on 19.11.2016.
 */

public class ButtonPresenter extends BasePresenter<ButtonMvpView> {

    private final IDataManager mDataManager;

    @Inject
    public ButtonPresenter(IDataManager dataManager) {
        super();
        this.mDataManager = dataManager;
    }

    void processButtonClick(String question, String choice) {
        // add answer to list of answers in AnswersVO after button has been clicked.
        MultipleChoiceAnswerVO dto = mDataManager.retrieveMcAnswer(question);
        if (!mDataManager.isMcQuestionAnswered(question)) {
            ChoiceVO choiceVO = mDataManager.retrieveChoiceVOByQuestionText(question, choice);
            mDataManager.getmAnswersVO().getMcAnswers().add(new MultipleChoiceAnswerVO(question, choiceVO));
        } else {
            ChoiceVO choiceVO = mDataManager.retrieveChoiceVOByQuestionText(question, choice);
            dto.setChoice(choiceVO);
        }
    }

    boolean isButtonToBeToggled(String question,String buttonText){
        for (MultipleChoiceAnswerVO dto : mDataManager.getmAnswersVO().getMcAnswers()){
            if(dto.getQuestionText().equals(question) && buttonText.equals(dto.getChoice().getChoiceText())){
                return true;
            }
        }
        return false;
    }

    void setCurrentQuestion(String question){
        mDataManager.setmCurrentQuestion(question);
    }


}
