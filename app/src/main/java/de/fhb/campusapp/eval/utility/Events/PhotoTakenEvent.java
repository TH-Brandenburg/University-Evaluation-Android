package de.fhb.campusapp.eval.utility.Events;

/**
 * Created by Admin on 15.12.2015.
 */
public class PhotoTakenEvent {
    private String mImagePath;
    private String mQuestion;

    public PhotoTakenEvent(String mImagePath, String mQuestion) {
        this.mImagePath = mImagePath;
        this.mQuestion = mQuestion;
    }

    public String getmQuestion() {

        return mQuestion;
    }

    public void setmQuestion(String mQuestion) {
        this.mQuestion = mQuestion;
    }

    public String getmImagePath() {
        return mImagePath;
    }

    public void setmImagePath(String mImagePath) {
        this.mImagePath = mImagePath;
    }
}
