package de.fhb.campusapp.eval.utility.eventpipelines;

import android.support.v4.util.Pair;

import org.apache.commons.lang3.tuple.Triple;

import javax.inject.Inject;

import de.fhb.ca.dto.ResponseDTO;
import de.fhb.campusapp.eval.utility.vos.QuestionsVO;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by Sebastian Müller on 14.11.2016.
 */
public class NetworkEventPipelines {
    private PublishSubject<ResponseDTO> responseSubject = PublishSubject.create();
    private PublishSubject<QuestionsVO> questionsSubject = PublishSubject.create();
    private PublishSubject<Triple<String, String, String>> requestErrorSubject = PublishSubject.create();
    private PublishSubject<Pair<String, String>> networkErrorSubject = PublishSubject.create();

    @Inject
    public NetworkEventPipelines() {
    }

    public void broadcastQuestionsVO(QuestionsVO vo){
        questionsSubject.onNext(vo);
    }

    public Observable<QuestionsVO> receiveQuestionsVO(){
        return questionsSubject;
    }

    public void broadcastResponseDTO(ResponseDTO dto){
        responseSubject.onNext(dto);
    }

    public Observable<ResponseDTO> receiveResponseDTO(){
        return responseSubject;
    }

    public void broadcastRequestError(Triple<String, String, String> msg){
        requestErrorSubject.onNext(msg);
    }

    public Observable<Triple<String, String, String>> receiveRequestError(){
        return requestErrorSubject;
    }

    public void broadcastNetworkError(Pair<String, String> msg){
        networkErrorSubject.onNext(msg);
    }

    public Observable<Pair<String, String>> receiveNetworkError(){
        return networkErrorSubject;
    }
}
