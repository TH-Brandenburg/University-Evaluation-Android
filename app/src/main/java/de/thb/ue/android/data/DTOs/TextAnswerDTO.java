package de.thb.ue.android.data.DTOs;

/**
 * Created by Admin on 15.05.2016.
 */
public class TextAnswerDTO {
    private int questionID;
    private String questionText;
    private String answerText;

    public TextAnswerDTO() {
    }

    public TextAnswerDTO(int questionID, String questionText, String answerText) {
        this.questionID = questionID;
        this.questionText = questionText;
        this.answerText = answerText;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
}
