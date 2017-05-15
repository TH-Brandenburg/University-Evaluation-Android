package de.thb.ue.android.data.VOs;

import java.util.List;

/**
 * Created by Admin on 15.05.2016.
 */
public class SingleChoiceQuestionVO extends Question{
    private String question;
    private List<ChoiceVO> choices;

    public SingleChoiceQuestionVO() {
    }

    public SingleChoiceQuestionVO(String question, List<ChoiceVO> choices) {
        this.question = question;
        this.choices = choices;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<ChoiceVO> getChoices() {
        return choices;
    }

    public void setChoices(List<ChoiceVO> choices) {
        this.choices = choices;
    }
}
