package de.fhb.campusapp.eval.utility.vos;

import java.util.List;

/**
 * Created by Admin on 15.05.2016.
 */
public class QuestionsVO {
    private List<String> studyPaths;
    private List<TextQuestionVO> textQuestions;
    private List<MultipleChoiceQuestionVO> multipleChoiceQuestionVOs;
    private Boolean textQuestionsFirst;

    public QuestionsVO(){}

    public QuestionsVO(List<String> studyPaths, List<TextQuestionVO> textQuestions, List<MultipleChoiceQuestionVO> multipleChoiceQuestionVOs, Boolean textQuestionsFirst) {
        this.studyPaths = studyPaths;
        this.textQuestions = textQuestions;
        this.multipleChoiceQuestionVOs = multipleChoiceQuestionVOs;
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

    public List<MultipleChoiceQuestionVO> getMultipleChoiceQuestionVOs() {
        return multipleChoiceQuestionVOs;
    }

    public void setMultipleChoiceQuestionVOs(List<MultipleChoiceQuestionVO> multipleChoiceQuestionVOs) {
        this.multipleChoiceQuestionVOs = multipleChoiceQuestionVOs;
    }

    public Boolean getTextQuestionsFirst() {
        return textQuestionsFirst;
    }

    public void setTextQuestionsFirst(Boolean textQuestionsFirst) {
        this.textQuestionsFirst = textQuestionsFirst;
    }
}
