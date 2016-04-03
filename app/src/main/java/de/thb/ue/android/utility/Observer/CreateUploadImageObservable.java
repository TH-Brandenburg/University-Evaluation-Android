package de.thb.ue.android.utility.Observer;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.thb.ca.dto.util.TextQuestionDTO;
import de.thb.ue.android.utility.DataHolder;
import de.thb.ue.android.utility.ImagePathsVO;
import de.thb.ue.android.utility.Utility;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by Admin on 11.12.2015.
 */
public class CreateUploadImageObservable{

    public Observable<Pair<String, String>> prepareImageUploadInBackground(final Context context){

        Observable<Pair<String, String>> observable = Observable.create(new Observable.OnSubscribe<Pair<String, String>>(){
            @Override
            public void call(Subscriber<? super Pair<String, String>> subscriber) {

                //loop through all text questions and test if their is an entry in
                //the commentaryImageMap. If a match is found use the path to the original image
                //to compress it and store the path to the other image paths
                for(TextQuestionDTO textQuestionDto : DataHolder.getQuestionsDTO().getTextQuestions()){
                    if(DataHolder.getCommentaryImageMap().containsKey(textQuestionDto.getQuestionText())){
                        ImagePathsVO pathsObj = DataHolder.getCommentaryImageMap().get(textQuestionDto.getQuestionText());
                        try {
                            File uploadFile = Utility.createImageFile(Utility.removeSpecialCharacters(textQuestionDto.getQuestionText()), context);
                            Bitmap resizedImage = Utility.resizeImage(pathsObj.getmLargeImageFilePath(), 800, 768);
                            FileOutputStream os = new FileOutputStream(uploadFile);
                            resizedImage.compress(Bitmap.CompressFormat.JPEG, 70, os);
                            os.close();
                            subscriber.onNext(new Pair<>(textQuestionDto.getQuestionText(), uploadFile.getPath()));
                        } catch (IOException e) {
                            subscriber.onError(e);
                        }
                    }
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
        return observable;
    }
}
