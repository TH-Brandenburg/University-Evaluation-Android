package de.thb.ue.android.data.DTOs;

import java.util.List;

/**
 * Created by Admin on 15.05.2016.
 */
public class QuestionsDTO {
    private List<String> studyPaths;
    private List<TextQuestionDTO> textQuestions;
    private List<SingleChoiceQuestionDTO> singleChoiceQuestionDTOs;
    private Boolean textQuestionsFirst;

    public QuestionsDTO(){}

    public QuestionsDTO(List<String> studyPaths, List<TextQuestionDTO> textQuestions, List<SingleChoiceQuestionDTO> singleChoiceQuestionDTOs, Boolean textQuestionsFirst) {
        this.studyPaths = studyPaths;
        this.textQuestions = textQuestions;
        this.singleChoiceQuestionDTOs = singleChoiceQuestionDTOs;
        this.textQuestionsFirst = textQuestionsFirst;
    }

    public List<String> getStudyPaths() {
        return studyPaths;
    }

    public void setStudyPaths(List<String> studyPaths) {
        this.studyPaths = studyPaths;
    }

    public List<TextQuestionDTO> getTextQuestions() {
        return textQuestions;
    }

    public void setTextQuestions(List<TextQuestionDTO> textQuestions) {
        this.textQuestions = textQuestions;
    }

    public List<SingleChoiceQuestionDTO> getSingleChoiceQuestionDTOs() {
        return singleChoiceQuestionDTOs;
    }

    public void setSingleChoiceQuestionDTOs(List<SingleChoiceQuestionDTO> singleChoiceQuestionDTOs) {
        this.singleChoiceQuestionDTOs = singleChoiceQuestionDTOs;
    }

    public Boolean getTextQuestionsFirst() {
        return textQuestionsFirst;
    }

    public void setTextQuestionsFirst(Boolean textQuestionsFirst) {
        this.textQuestionsFirst = textQuestionsFirst;
    }
}
