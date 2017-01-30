package de.fhb.campusapp.eval.ui.textfragment;

import android.Manifest;
import android.util.Log;

import com.github.buchandersenn.android_permission_manager.PermissionManager;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

import javax.inject.Inject;

import de.fhb.campusapp.eval.custom.CustomEditText;
import de.fhb.campusapp.eval.data.IDataManager;
import de.fhb.campusapp.eval.ui.base.BasePresenter;
import de.fhb.campusapp.eval.utility.vos.ImageDataVO;
import de.fhb.campusapp.eval.utility.vos.TextAnswerVO;

/**
 * Created by Sebastian Müller on 14.10.2016.
 */
public class TextPresenter extends BasePresenter<TextMvpView> {

    private final IDataManager mDataManager;

    @Inject
    public TextPresenter(IDataManager dataManager) {
        super();
        this.mDataManager = dataManager;
    }

    String getTextAnswer(String question){
        TextAnswerVO answerVO = mDataManager.retrieveTextAnswerVO(question);

        if(answerVO != null){
            return answerVO.getAnswerText();
        }
        return "";
    }

    void processAndStoreAnswer(String answer, String question, int questionId) {
        //clean text of unnecessary information before storing
        answer = answer.trim();
        StringUtils.replace(answer, "\n\r", "");
        StringUtils.replace(answer, "\n", "");

        //store the answer in answerDTO or refresh it if already present
        if(!mDataManager.isTextQuestionAnswered(question) && mDataManager.retrieveTextAnswerVO(question) == null){
            mDataManager.getmAnswersVO().getTextAnswers().add(new TextAnswerVO(questionId, question, answer));
        } else {
            mDataManager.retrieveTextAnswerVO(question).setAnswerText(answer);
        }
        Log.i("INFO","TextQuestion Answer stored: " + answer);

    }

    File startCameraIntent(PermissionManager manager){
        boolean permissionsGranted = getMvpView().isCameraPermissionGranted()
                                  && getMvpView().isStoragePermissionGranted();

        File intentImage = null;

        if(permissionsGranted){
            intentImage = getMvpView().startCameraIntent();
        } else {
            this.requestCameraAndStoragePermission(manager);
        }

        return intentImage;
    }

    private void requestCameraAndStoragePermission(PermissionManager manager){
        manager.with(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onPermissionGranted(() -> startCameraIntent(manager))
                .onPermissionShowRationale(request -> getMvpView().showCameraAndStorageExplanation(request))
                .request();
    }

    void deleteImageInBackground(ImageDataVO imageData){
        mDataManager.deleteImagePairInBackground(imageData).subscribe();
    }

    public ImageDataVO removeFromImageMap(String question){
        return mDataManager.removeFromImageMap(question);
    }

    ImageDataVO putIntoImageMap(String question, ImageDataVO data){
        return mDataManager.putIntoImageMap(question, data);
    }

    boolean isInImageMap(String question){
        return mDataManager.isInImageMap(question);
    }

    ImageDataVO getFromImageMap(String question){
        return mDataManager.getFromImageMap(question);
    }

    void setCurrentQuestion(String question){
        mDataManager.setmCurrentQuestion(question);
    }

    boolean isNumberQuestion(String question){
        return mDataManager.retrieveTextQuestionVO(question).getOnlyNumbers();
    }

    public int getMaxTextLength(String question){
        return mDataManager.retrieveTextQuestionVO(question).getMaxLength();
    }

    int getCurrentlyStoredPosition(){
        return mDataManager.getmCurrentPagerPosition();
    }
}
