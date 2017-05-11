package de.thb.ue.android.data.DTOs;

/**
 * Created by Sebastian Müller on 15.05.2016.
 */
public class TextQuestionDTO {
    private int questionID;
    private String questionText;
    private Boolean onlyNumbers;
    private Integer maxLength;

    public TextQuestionDTO() {}

    public TextQuestionDTO(int questionID, String questionText, Boolean onlyNumbers, Integer maxLength) {
        this.questionID = questionID;
        this.questionText = questionText;
        this.onlyNumbers = onlyNumbers;
        this.maxLength = maxLength;
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

    public Boolean getOnlyNumbers() {
        return onlyNumbers;
    }

    public void setOnlyNumbers(Boolean onlyNumbers) {
        this.onlyNumbers = onlyNumbers;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }
}
