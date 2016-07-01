package de.fhb.campusapp.eval.utility.vos;

import org.joda.time.Instant;

/**
 * Created by Admin on 11.12.2015.
 */
public class ImageDataVO {

    private String mLargeImageFilePath;
    private String mThumbnailFilePath;
    private String mUploadFilePath;

    public ImageDataVO(String mLargeImageFilePath, String mThumbnailFilePath, String mUploadFilePath) {
        this.mLargeImageFilePath = mLargeImageFilePath;
        this.mThumbnailFilePath = mThumbnailFilePath;
        this.mUploadFilePath = mUploadFilePath;
    }

    public String getmLargeImageFilePath() {
        return mLargeImageFilePath;
    }

    public void setmLargeImageFilePath(String mLargeImageFilePath) {
        this.mLargeImageFilePath = mLargeImageFilePath;
    }

    public String getmThumbnailFilePath() {
        return mThumbnailFilePath;
    }

    public void setmThumbnailFilePath(String mThumbnailFilePath) {
        this.mThumbnailFilePath = mThumbnailFilePath;
    }

    public String getmUploadFilePath() {
        return mUploadFilePath;
    }

    public void setmUploadFilePath(String mUploadFilePath) {
        this.mUploadFilePath = mUploadFilePath;
    }

}
