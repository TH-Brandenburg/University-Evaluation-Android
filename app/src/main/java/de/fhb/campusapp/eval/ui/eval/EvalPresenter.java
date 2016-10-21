package de.fhb.campusapp.eval.ui.eval;

import android.Manifest;
import android.app.Dialog;
import android.content.res.Resources;
import android.support.v4.util.Pair;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.buchandersenn.android_permission_manager.PermissionManager;
import com.squareup.otto.Subscribe;

import org.apache.commons.lang3.tuple.Triple;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import de.fhb.ca.dto.AnswersDTO;
import de.fhb.ca.dto.ResponseDTO;
import de.fhb.campusapp.eval.data.local.RetrofitHelper;
import de.fhb.campusapp.eval.interfaces.RetroRespondService;
import de.fhb.campusapp.eval.ui.base.BasePresenter;
import de.fhb.campusapp.eval.utility.ClassMapper;
import de.fhb.campusapp.eval.utility.DataHolder;
import de.fhb.campusapp.eval.utility.DialogFactory;
import de.fhb.campusapp.eval.utility.EventBus;
import de.fhb.campusapp.eval.utility.Events.NetworkErrorEvent;
import de.fhb.campusapp.eval.utility.Events.RequestErrorEvent;
import de.fhb.campusapp.eval.utility.Events.RequestSuccessEvent;
import de.fhb.campusapp.eval.utility.FeatureSwitch;
import de.fhb.campusapp.eval.utility.vos.ChoiceVO;
import de.fhb.campusapp.eval.utility.vos.ImageDataVO;
import de.fhb.campusapp.eval.utility.vos.MultipleChoiceAnswerVO;
import de.fhb.campusapp.eval.utility.vos.MultipleChoiceQuestionVO;
import de.fhb.campusapp.eval.utility.vos.TextAnswerVO;
import fhb.de.campusappevaluationexp.R;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Sebastian Müller on 09.10.2016.
 */
public class EvalPresenter extends BasePresenter<EvalMvpView>{

    @Inject
    Resources mResources;

    @Inject
    RetrofitHelper mRetrofitHelper;

    Retrofit mRetrofit;

    @Inject
    public EvalPresenter(Resources resources) {
        super();
        mResources = resources;
    }

    /*
 * Executes request retrieving questions and choices from REST server
 * */

    private void performAnswerRequest() {

        if(FeatureSwitch.DEBUG_ACTIVE && DataHolder.getHostName() == null){
            getMvpView().hideProgressOverlay();
            getMvpView().showDebugMessage();
            return;
        }

        mRetrofit = new Retrofit.Builder()
                .baseUrl(DataHolder.getHostName() + '/')
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        //zip commentary pictures if there are any
        ArrayList<File> imageFileList = new ArrayList<>();
        for(ImageDataVO pathObj : DataHolder.getCommentaryImageMap().values()){
            if(pathObj.getmUploadFilePath() != null){
                imageFileList.add(new File(pathObj.getmUploadFilePath()));
            }
        }
        File zippedImages = getMvpView().zipPictureFiles(imageFileList);

//      manuel mapping to Json since I do not trust retrofit to do that
        ObjectMapper mapper = new ObjectMapper();
        String jsonAnswers = null;

        try {
            AnswersDTO dto = ClassMapper.answersVOToAnswerDTOMapper(DataHolder.getAnswersVO());
            jsonAnswers = mapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), zippedImages);
        RequestBody answerBody = RequestBody.create(MediaType.parse("multipart/form-data"), jsonAnswers);

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("images", zippedImages.getName(), fileBody);

        RetroRespondService respondService = mRetrofit.create(RetroRespondService.class);
        Call<ResponseDTO> response = respondService.sendAnswersWithPictures(answerBody, filePart);

        response.enqueue(new RetrofitCallback());
    }


    public void requestInternetPermissionAndConnectServer(PermissionManager manager){
        manager.with(Manifest.permission.INTERNET)
                .onPermissionGranted(() -> performAnswerRequest())
                .onPermissionDenied(() -> getMvpView().callSaveTerminateTask())
                .onPermissionShowRationale(request -> getMvpView().showInternetExplanationDialog(request))
                .request();
    }

    /**
     * Gives every question a representation in AnswersDTO which never got one assigned by the user.
     */
    public void setUnasweredQuestions(){
        //server expects all questions to have an entry in answers dto. Add all the user did not set
        for(TextAnswerVO answerVO : DataHolder.getAnswersVO().getTextAnswers()){
            if(DataHolder.isTextQuestionAnswered(answerVO.getQuestionText()) == null){
                DataHolder.getAnswersVO().getTextAnswers().add(new TextAnswerVO(answerVO.getQuestionID(), answerVO.getQuestionText(), ""));
            }
        }

        //loop through all mcQuestions
        for(MultipleChoiceQuestionVO questionVO : DataHolder.getQuestionsVO().getMultipleChoiceQuestionVOs()){
            //test if any wasnt answered by the user
            if(DataHolder.isMcQuestionAnswered(questionVO.getQuestion()) == null){
                ChoiceVO noCommentChoice = DataHolder.retrieveChoiceByGrade(questionVO.getQuestion(), 0);
                DataHolder.getAnswersVO().getMcAnswers().add(new MultipleChoiceAnswerVO(questionVO.getQuestion(), noCommentChoice));
            }
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onRequestSuccess(RequestSuccessEvent event){
        getMvpView().hideProgressOverlay();
        getMvpView().showSuccessDialog();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onNetworkError(NetworkErrorEvent event){
        Throwable t = event.getRetrofitError();
        boolean dialogsCanceable = true;
        int statusCode = 0;
        ResponseDTO dto = null;
        getMvpView().hideProgressOverlay();


        Pair<String, String> errorText = mRetrofitHelper.processNetworkError(t, mResources);
        getMvpView().showNetworkErrorDialog(errorText.first, errorText.second);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onRequestError(RequestErrorEvent<ResponseDTO> event){
        Triple<String, String, String> errorText = mRetrofitHelper.processRequestError(event.getResposne(), mResources, mRetrofit);
        getMvpView().hideProgressOverlay();

        if(errorText.getRight().equals("RETRY_SCAN")){
           getMvpView().showRequestErrorRestartDialog(errorText.getLeft(), errorText.getMiddle());
        } else if(errorText.getRight().equals("RETRY_COMMUNICATION")){
           getMvpView().showRequestErrorRetryDialog(errorText.getLeft(), errorText.getMiddle());
        }
    }

    private class RetrofitCallback implements Callback<ResponseDTO> {

        @Override
        public void onResponse(Call<ResponseDTO> call, Response<ResponseDTO> response) {
            if(response.isSuccessful()){
                EventBus.get().post(new RequestSuccessEvent<>(response.body() ,response));
            } else {
                EventBus.get().post(new RequestErrorEvent<>(response));
            }
        }

        @Override
        public void onFailure(Call<ResponseDTO> call, Throwable t) {
            EventBus.get().post(new NetworkErrorEvent(t));
        }
    }

}
