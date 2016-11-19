package de.fhb.campusapp.eval.ui.sendfragment;

import javax.inject.Inject;

import de.fhb.campusapp.eval.ui.base.BasePresenter;
import de.fhb.campusapp.eval.data.DataManager;
import de.fhb.campusapp.eval.utility.vos.AnswersVO;
import de.fhb.campusapp.eval.utility.vos.TextAnswerVO;

/**
 * Created by Sebastian Müller on 14.10.2016.
 */
public class SendPresenter extends BasePresenter<SendMvpView> {

    @Inject
    public SendPresenter(){
        super();
    }


    public void sendButtonPressed(){
        int total = DataManager.getmQuestionsVO().getMultipleChoiceQuestionVOs().size() + DataManager.getmQuestionsVO().getTextQuestions().size();
        int answered = 0;
        String subject = DataManager.getAnswersVO().getStudyPath();
        boolean subjectChoosen = !(subject == null || subject.isEmpty());
        AnswersVO answersVO = DataManager.getAnswersVO();

        // insist that a subject is chosen
        if(!subjectChoosen){
            getMvpView().showSubjectNotChosenDialog();
        } else {
            //count only non empty texts and photos as answered
            for(TextAnswerVO answer : answersVO.getTextAnswers()){
                if((answer.getAnswerText() != null && !answer.getAnswerText().equals(""))
                        || DataManager.getCommentaryImageMap().containsKey(answer.getQuestionText())){
                    answered++;
                }
            }

            answered += answersVO.getMcAnswers().size();

            if(answered < total){
                getMvpView().recolorUnansweredQuestions();
                getMvpView().showQuestionsNotAnsweredDialog(answered, total);
            } else {
                getMvpView().onPreServerCommunication();
            }
        }
    }

}
