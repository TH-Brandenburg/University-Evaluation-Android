package de.fhb.campusapp.eval.ui.sendfragment;

import javax.inject.Inject;

import de.fhb.campusapp.eval.data.IDataManager;
import de.fhb.campusapp.eval.ui.base.BasePresenter;
import de.fhb.campusapp.eval.utility.vos.AnswersVO;
import de.fhb.campusapp.eval.utility.vos.TextAnswerVO;

/**
 * Created by Sebastian Müller on 14.10.2016.
 */
public class SendPresenter extends BasePresenter<SendMvpView> {

    private final IDataManager mDataManager;

    @Inject
    public SendPresenter(IDataManager dataManager) {
        super();
        this.mDataManager = dataManager;
    }

    public void sendButtonPressed(){
        int total = mDataManager.getmQuestionsVO().getMultipleChoiceQuestionVOs().size() + mDataManager.getmQuestionsVO().getTextQuestions().size();
        int answered = 0;
        String subject = mDataManager.getmAnswersVO().getStudyPath();
        boolean subjectChoosen = !(subject == null || subject.isEmpty());
        AnswersVO answersVO = mDataManager.getmAnswersVO();

        // insist that a subject is chosen
        if(!subjectChoosen){
            getMvpView().showSubjectNotChosenDialog();
        } else {
            //count only non empty texts and photos as answered
            for(TextAnswerVO answer : answersVO.getTextAnswers()){
                if(mDataManager.isTextQuestionAnswered(answer.getQuestionText())){
                    answered++;
                }
            }

            answered += answersVO.getMcAnswers().size();

            if(answered < total){
                mDataManager.setmRecolorNavigation(true);
                getMvpView().showQuestionsNotAnsweredDialog(answered, total);
            } else {
                mDataManager.setmRecolorNavigation(false);
                beforeServerCommunication();
            }
        }
    }

    /**
     * Set the current question to something different so the app does not act
     * as if the last question was still displayed.
     */
    void setCurrentQuestion(){
        mDataManager.setmCurrentQuestion("Send");
    }

    void beforeServerCommunication(){
        mDataManager.broadcastBeforeServerCommunication();
    }

}
