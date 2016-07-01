package de.fhb.campusapp.eval.utility.vos;

import java.util.List;

/**
 * Created by Admin on 15.05.2016.
 */
public class MultipleChoiceQuestionVO {
    private String question;
    private List<ChoiceVO> choices;

    public MultipleChoiceQuestionVO() {
    }

    public MultipleChoiceQuestionVO(String question, List<ChoiceVO> choices) {
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
