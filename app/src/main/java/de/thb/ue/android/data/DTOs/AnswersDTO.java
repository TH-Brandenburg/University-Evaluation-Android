package de.thb.ue.android.data.DTOs;

import java.util.List;

/**
 * Created by Admin on 15.05.2016.
 */
public class AnswersDTO {

    private String voteToken;

    private String studyPath;

    private List<TextAnswerDTO> textAnswers;

    private List<SingleChoiceAnswerDTO> mcAnswers;

    private String deviceID;

    public AnswersDTO() {

    }

    public AnswersDTO(String voteToken, String studyPath, List<TextAnswerDTO> textAnswers, List<SingleChoiceAnswerDTO> mcAnswers, String deviceID) {
        this.voteToken = voteToken;
        this.studyPath = studyPath;
        this.textAnswers = textAnswers;
        this.mcAnswers = mcAnswers;
        this.deviceID = deviceID;
    }

    public String getVoteToken() {
        return voteToken;
    }

    public void setVoteToken(String voteToken) {
        this.voteToken = voteToken;
    }

    public String getStudyPath() {
        return studyPath;
    }

    public void setStudyPath(String studyPath) {
        this.studyPath = studyPath;
    }

    public List<TextAnswerDTO> getTextAnswers() {
        return textAnswers;
    }

    public void setTextAnswers(List<TextAnswerDTO> textAnswers) {
        this.textAnswers = textAnswers;
    }

    public List<SingleChoiceAnswerDTO> getMcAnswers() {
        return mcAnswers;
    }

    public void setMcAnswers(List<SingleChoiceAnswerDTO> mcAnswers) {
        this.mcAnswers = mcAnswers;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }
}
