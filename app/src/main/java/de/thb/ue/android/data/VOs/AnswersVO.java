package de.thb.ue.android.data.VOs;

import java.util.List;

/**
 * Created by Admin on 15.05.2016.
 */
public class AnswersVO {

    private String voteToken;

    private String studyPath;

    private List<TextAnswerVO> textAnswers;

    private List<SingleChoiceAnswerVO> ScAnswers;

//    private List<AnswerVO> answers;

    private String deviceID;

    public AnswersVO() {

    }

    public AnswersVO(String voteToken, String studyPath, /*List<AnswerVO> answers,*/ List<TextAnswerVO> textAnswers, List<SingleChoiceAnswerVO> ScAnswers, String deviceID) {
        this.voteToken = voteToken;
        this.studyPath = studyPath;
//        this.answers = answers;
        this.textAnswers = textAnswers;
        this.ScAnswers = ScAnswers;
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

    public List<SingleChoiceAnswerVO> getScAnswers() {
        return ScAnswers;
    }

    public void setScAnswers(List<SingleChoiceAnswerVO> mcAnswers) {
        this.ScAnswers = mcAnswers;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }
}
