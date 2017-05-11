package de.thb.ue.android.data;

import de.thb.ue.android.data.VOs.AnswersVO;
import de.thb.ue.android.data.VOs.ChoiceVO;
import de.thb.ue.android.data.VOs.ProcessedResponse;
import de.thb.ue.android.data.VOs.QuestionsVO;
import de.thb.ue.android.data.VOs.SingleChoiceAnswerVO;
import de.thb.ue.android.data.VOs.TextAnswerVO;
import de.thb.ue.android.utility.TestQuestionnaire;
import io.reactivex.Single;

/**
 * Created by Sebastian Müller on 12.11.2016.
 */
public interface IDataManager {

    //*************Networking****************

    boolean isConnected();

    Single<ProcessedResponse<QuestionsVO>> getQuestions(String voteToken, String host);
    Single<ProcessedResponse<Void>> sendAnswers();

    //*************Internal Operations*******

    void putTextAnswer(String question, int questionId, String answer);
    void putSCAnswer(String question, ChoiceVO answer);
    QuestionsVO getCachedQuestions();


    /**
     * Removes the transient data from shared preferences.
     */
    void removeCachedData();


}
