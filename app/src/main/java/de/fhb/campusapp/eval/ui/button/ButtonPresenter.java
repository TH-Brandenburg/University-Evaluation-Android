package de.fhb.campusapp.eval.ui.button;


import javax.inject.Inject;

import de.fhb.campusapp.eval.ui.base.BasePresenter;
import de.fhb.campusapp.eval.utility.DataHolder;
import de.fhb.campusapp.eval.utility.vos.ChoiceVO;
import de.fhb.campusapp.eval.utility.vos.MultipleChoiceAnswerVO;

/**
 * Created by Sebastian Müller on 19.11.2016.
 */

public class ButtonPresenter extends BasePresenter<ButtonMvpView> {

    @Inject
    public ButtonPresenter() {
    }

    void processButtonClick(String question, String choice) {
        // add answer to list of answers in AnswersDTO after button has been clicked.
        MultipleChoiceAnswerVO dto = DataHolder.isMcQuestionAnswered(question);

        if (dto == null) {
            ChoiceVO choiceVO = DataHolder.retrieveChoiceVO(question, choice);
            DataHolder.getAnswersVO().getMcAnswers().add(new MultipleChoiceAnswerVO(question, choiceVO));
        } else {
            ChoiceVO choiceVO = DataHolder.retrieveChoiceVO(question, choice);
            dto.setChoice(choiceVO);
        }
    }
}
