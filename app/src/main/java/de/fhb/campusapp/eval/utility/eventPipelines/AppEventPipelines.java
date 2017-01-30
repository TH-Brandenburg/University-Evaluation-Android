package de.fhb.campusapp.eval.utility.eventpipelines;

import android.util.Log;
import android.util.Pair;

import de.fhb.campusapp.eval.utility.vos.ImageDataVO;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

/**
 * Created by Sebastian Müller on 19.11.2016.
 */

public class AppEventPipelines {

    private int firstPagingEventCounter = 0;

    private PublishSubject<Pair<Integer, Integer>> firstPagingEventSubject = PublishSubject.create();
    private PublishSubject<Void> secondsPagingEventSubject = PublishSubject.create();
    private PublishSubject<Boolean> keyboardNeededSubject = PublishSubject.create();
    private PublishSubject<ImageDataVO> pictureTakenSubject = PublishSubject.create();
    private PublishSubject<Void> beforeServerCommunicationSubject = PublishSubject.create();

    public void broadcastFirstPagingEvent(int newPosition, int oldPosition){
        firstPagingEventSubject.onNext(new Pair<>(newPosition, oldPosition));
        firstPagingEventCounter++;
    }

    public Observable<Pair<Integer, Integer>> receiveFirstPagingEvent(){
       return firstPagingEventSubject.elementAtOrDefault(firstPagingEventCounter, new Pair<>(0, 0));

    }

    public void broadcastSecondPagingEvent(){
        secondsPagingEventSubject.onNext(null);
    }

    public Observable<Void> receiveSecondPagingEvent(){
        return secondsPagingEventSubject;
    }

    public void broadcastKeyboardNeeded(boolean isNeeded){
        keyboardNeededSubject.onNext(isNeeded);
    }

    public Observable<Boolean> receiveKeyboardNeeded(){
        return keyboardNeededSubject;
    }

    public void broadcastPictureTaken(ImageDataVO imageData){
        pictureTakenSubject.onNext(imageData);
    }

    public Observable<ImageDataVO> receivePictureTaken(){
        return pictureTakenSubject;
    }

    public void broadcastBeforeServerCoomunication(){
        beforeServerCommunicationSubject.onNext(null);
    }

    public Observable<Void> receiveBeforeServerCommunication(){
        return beforeServerCommunicationSubject;
    }


}
