package de.thb.ue.android.data.VOs;

import java.util.List;

/**
 * Created by Admin on 15.05.2016.
 */
public class QuestionsVO {
    private List<String> studyPaths;
    private List<TextQuestionVO> textQuestions;
    private List<SingleChoiceQuestionVO> singleChoiceQuestionVOs;
    private Boolean textQuestionsFirst;

    public QuestionsVO(){}

    public QuestionsVO(List<String> studyPaths, List<TextQuestionVO> textQuestions, List<SingleChoiceQuestionVO> singleChoiceQuestionVOs, Boolean textQuestionsFirst) {
        this.studyPaths = studyPaths;
        this.textQuestions = textQuestions;
        this.singleChoiceQuestionVOs = singleChoiceQuestionVOs;
        this.textQuestionsFirst = textQuestionsFirst;
    }

    public List<String> getStudyPaths() {
        return studyPaths;
    }

    public void setStudyPaths(List<String> studyPaths) {
        this.studyPaths = studyPaths;
    }

    public List<TextQuestionVO> getTextQuestions() {
        return textQuestions;
    }

    public void setTextQuestions(List<TextQuestionVO> textQuestions) {
        this.textQuestions = textQuestions;
    }

    public List<SingleChoiceQuestionVO> getSingleChoiceQuestionVOs() {
        return singleChoiceQuestionVOs;
    }

    public void setSingleChoiceQuestionVOs(List<SingleChoiceQuestionVO> singleChoiceQuestionVOs) {
        this.singleChoiceQuestionVOs = singleChoiceQuestionVOs;
    }

    public Boolean getTextQuestionsFirst() {
        return textQuestionsFirst;
    }

    public void setTextQuestionsFirst(Boolean textQuestionsFirst) {
        this.textQuestionsFirst = textQuestionsFirst;
    }
}
