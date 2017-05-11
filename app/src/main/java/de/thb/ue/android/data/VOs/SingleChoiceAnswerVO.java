package de.thb.ue.android.data.VOs;

/**
 * Created by Admin on 15.05.2016.
 */
public class SingleChoiceAnswerVO {

    private String questionText;

    private ChoiceVO choice;

    public SingleChoiceAnswerVO() {

    }

    public SingleChoiceAnswerVO(String questionText, ChoiceVO choice) {
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
