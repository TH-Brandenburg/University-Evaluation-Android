package de.thb.ue.android.data.DTOs;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;

/**
 * Created by Admin on 15.05.2016.
 */
@JsonTypeName("SingleChoiceQuestionDTO")
public class SingleChoiceQuestionDTO {
    private String question;
    private List<ChoiceDTO> choices;

    public SingleChoiceQuestionDTO() {
    }

    public SingleChoiceQuestionDTO(String question, List<ChoiceDTO> choices) {
        this.question = question;
        this.choices = choices;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<ChoiceDTO> getChoices() {
        return choices;
    }

    public void setChoices(List<ChoiceDTO> choices) {
        this.choices = choices;
    }
}
