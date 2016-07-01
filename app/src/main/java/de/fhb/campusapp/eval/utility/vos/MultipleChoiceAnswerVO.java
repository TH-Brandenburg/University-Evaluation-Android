package de.fhb.campusapp.eval.utility.vos;

/**
 * Created by Admin on 15.05.2016.
 */
public class MultipleChoiceAnswerVO {

    private String questionText;

    private ChoiceVO choice;

    public MultipleChoiceAnswerVO() {

    }

    public MultipleChoiceAnswerVO(String questionText, ChoiceVO choice) {
        this.questionText = questionText;
        this.choice = choice;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public ChoiceVO getChoice() {
        return choice;
    }

    public void setChoice(ChoiceVO choice) {
        this.choice = choice;
    }
}
