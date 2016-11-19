package de.fhb.campusapp.eval.ui.textfragment;

import android.Manifest;

import com.github.buchandersenn.android_permission_manager.PermissionManager;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

import javax.inject.Inject;

import de.fhb.campusapp.eval.custom.CustomEditText;
import de.fhb.campusapp.eval.ui.base.BasePresenter;
import de.fhb.campusapp.eval.data.DataManager;
import de.fhb.campusapp.eval.utility.vos.TextAnswerVO;

/**
 * Created by Sebastian Müller on 14.10.2016.
 */
public class TextPresenter extends BasePresenter<TextMvpView> {

    private TextFragment.TextFragmentCommunicator mActivityCommunicator;

    @Inject
    public TextPresenter() {
    }

    public void processAndStoreAnswer(CustomEditText editText, String question, int questionId) {
        //clean text of unnecessary information before storing
        String answer = editText.getText().toString();
        answer = answer.trim();
        StringUtils.replace(answer, "\n\r", "");
        StringUtils.replace(answer, "\n", "");

        //store the answer in answerDTO or refresh it if already present
        TextAnswerVO answerDTO = DataManager.isTextQuestionAnswered(question);
        if(answerDTO == null){
            DataManager.getAnswersVO().getTextAnswers().add(new TextAnswerVO(questionId, question, answer));
        } else {
            answerDTO.setAnswerText(answer);
        }
    }

    public File startCameraIntent(PermissionManager manager){
        boolean permissionsGranted = getMvpView().isCameraPermissionGranted() && getMvpView().isStoragePermissionGranted();

        File intentImage = null;

        if(permissionsGranted){
            intentImage = getMvpView().startCameraIntent();
        } else {
            this.requestCameraAndStoragePermission(manager);
        }

        mActivityCommunicator.setIntentImage(intentImage);
        return intentImage;
    }

    private void requestCameraAndStoragePermission(PermissionManager manager){
        manager.with(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onPermissionGranted(() -> startCameraIntent(manager))
                .onPermissionShowRationale(request -> getMvpView().showCameraAndStorageExplanation(request))
                .request();
    }

    public void setmActivityCommunicator(TextFragment.TextFragmentCommunicator mActivityCommunicator) {
        this.mActivityCommunicator = mActivityCommunicator;
    }
}
