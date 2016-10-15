package de.fhb.campusapp.eval.ui.sendfragment;

import de.fhb.campusapp.eval.ui.base.MvpView;

/**
 * Created by Admin on 14.10.2016.
 */
public interface SendMvpView extends MvpView{
    void recolorUnansweredQuestions();
    void onPreServerCommunication();
    void showSubjectNotChosenDialog();
    void showQuestionsNotAnsweredDialog(int answered , int total);
}
