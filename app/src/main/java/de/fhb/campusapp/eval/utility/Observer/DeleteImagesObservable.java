package de.fhb.campusapp.eval.utility.Observer;

/**
 * Created by Admin on 28.11.2015.
 */

import java.io.File;
import java.util.HashMap;

import de.fhb.campusapp.eval.utility.vos.ImageDataVO;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Asynchronous load of image thumbnail for use in TextFragments
 */

public class DeleteImagesObservable {

    public Observable<Void> deleteImagePairInBackground(final ImageDataVO pathObj){

        Observable<Void> observable = Observable.create(new Observable.OnSubscribe<Void>(){
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                if(pathObj.getmThumbnailFilePath() != null){
                    File thumbnail = new File(pathObj.getmThumbnailFilePath());
                    thumbnail.delete();
                }
                if(pathObj.getmLargeImageFilePath() != null){
                    File largeImage = new File(pathObj.getmLargeImageFilePath());
                    largeImage.delete();
                }
                if(pathObj.getmUploadFilePath() != null){
                    File uploadImage = new File(pathObj.getmUploadFilePath());
                    uploadImage.delete();
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
        return observable;
    }

    public Observable<Void> deleteImageMapInBackground(final HashMap<String, ImageDataVO> imageMap){

        Observable<Void> observable = Observable.create(new Observable.OnSubscribe<Void>(){
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                for(ImageDataVO pathsObj : imageMap.values()){
                    if(pathsObj.getmThumbnailFilePath() != null){
                        File image2 = new File(pathsObj.getmThumbnailFilePath());
                        image2.delete();
                    }
                    if(pathsObj.getmLargeImageFilePath() != null){
                        File image3 = new File(pathsObj.getmLargeImageFilePath());
                        image3.delete();
                    }
                    if(pathsObj.getmUploadFilePath() != null){
                        File image1 = new File(pathsObj.getmUploadFilePath());
                        image1.delete();
                    }
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
        return observable;
    }
}