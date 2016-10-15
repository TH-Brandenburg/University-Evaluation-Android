/*
package de.fhb.campusapp.eval.utility.Observer;

*/
/**
 * Created by Admin on 28.11.2015.
 *//*


import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.fhb.campusapp.eval.custom.CustomFragmentStatePagerAdapter;
import de.fhb.campusapp.eval.ui.textfragment.TextFragment;
import de.fhb.campusapp.eval.utility.Utility;
import fhb.de.campusappevaluationexp.R;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.schedulers.Schedulers;

*/
/**
 * Asynchronous load of image thumbnail for use in TextFragments
 *//*


public class ImageLoadObserver implements Observer<String> {

    File mLargeImageFile;
    Context mContext;
    ViewPager mViewPager;
    CustomFragmentStatePagerAdapter mStatePagerAdapter;
    TextFragment mTextFragment;

    public ImageLoadObserver (File largeImageFile ,Context context, ViewPager viewPager, CustomFragmentStatePagerAdapter statePagerAdapter){
        mLargeImageFile = largeImageFile;
        mContext = context;
        mViewPager = viewPager;
        mStatePagerAdapter = statePagerAdapter;
    }

    public ImageLoadObserver(File largeImageFile ,Context context, TextFragment textFragment){
        mLargeImageFile = largeImageFile;
        mContext = context;
        mTextFragment = textFragment;
    }

    public Observable<String> loadImageInBackground(File largeImageFile, final int reqWidth, final int reqHeight){

        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>(){
            @Override
            public void call(Subscriber<? super String> subscriber) {
                File thumbnailFile = Utility.createImageFile(mLargeImageFile.getName() + "_thumbnail", mContext);
                try {
                    Bitmap thumbnailBitmap = Utility.resizeImage(mLargeImageFile.getPath(), reqWidth, reqHeight);
                    thumbnailBitmap = Utility.rotateBitmap(mLargeImageFile.getPath(), thumbnailBitmap);
                    FileOutputStream os = new FileOutputStream(thumbnailFile);
                    thumbnailBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                    os.close();
                    subscriber.onNext(thumbnailFile.getPath());
                    subscriber.onCompleted();
                } catch (FileNotFoundException e) {
                    subscriber.onError(e);
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
        return observable;
    }

    @Override
    public void onCompleted() {
        mContext = null;
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        //do not accidentally leak a context
        mContext = null;
    }

    @Override
    public void onNext(final String thumbnailImagePath) {
        //update imageView of displayed TextFragment
        if(mViewPager != null && mStatePagerAdapter != null){
            ((TextFragment)mStatePagerAdapter.getFragmentAtPosition(mViewPager.getCurrentItem())).updateImageView(mLargeImageFile.getPath(), thumbnailImagePath , true);
        } else if(mTextFragment != null){
            mTextFragment.updateImageView(mLargeImageFile.getPath(), thumbnailImagePath, true);
        }
        mContext = null;
    }


}*/
