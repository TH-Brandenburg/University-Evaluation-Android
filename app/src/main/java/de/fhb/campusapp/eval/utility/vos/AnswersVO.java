package de.fhb.campusapp.eval.utility.vos;

import java.util.List;

/**
 * Created by Admin on 15.05.2016.
 */
public class AnswersVO {

    private String voteToken;

    private String studyPath;

    private List<TextAnswerVO> textAnswers;

    private List<MultipleChoiceAnswerVO> mcAnswers;

    private String deviceID;

    public AnswersVO() {

    }

    public AnswersVO(String voteToken, String studyPath, List<TextAnswerVO> textAnswers, List<MultipleChoiceAnswerVO> mcAnswers, String deviceID) {
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

    public List<TextAnswerVO> getTextAnswers() {
        return textAnswers;
    }

    public void setTextAnswers(List<TextAnswerVO> textAnswers) {
        this.textAnswers = textAnswers;
    }

    public List<MultipleChoiceAnswerVO> getMcAnswers() {
        return mcAnswers;
    }

    public void setMcAnswers(List<MultipleChoiceAnswerVO> mcAnswers) {
        this.mcAnswers = mcAnswers;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }
}
