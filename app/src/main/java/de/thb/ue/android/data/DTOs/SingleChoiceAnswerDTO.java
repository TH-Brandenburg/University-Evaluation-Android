package de.thb.ue.android.data.DTOs;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Created by Admin on 15.05.2016.
 */
@JsonTypeName("SingleChoiceAnswerDTO")
public class SingleChoiceAnswerDTO {

    private String questionText;

    private ChoiceDTO choice;

    public SingleChoiceAnswerDTO() {

    }

    public SingleChoiceAnswerDTO(String questionText, ChoiceDTO choice) {
        this.questionText = questionText;
        this.choice = choice;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public ChoiceDTO getChoice() {
        return choice;
    }

    public void setChoice(ChoiceDTO choice) {
        this.choice = choice;
    }
}
