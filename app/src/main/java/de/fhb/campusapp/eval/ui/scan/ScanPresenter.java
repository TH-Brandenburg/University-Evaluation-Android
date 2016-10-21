package de.fhb.campusapp.eval.ui.scan;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.util.Pair;
import android.view.View;

import com.github.buchandersenn.android_permission_manager.PermissionManager;
import com.squareup.otto.Subscribe;

import org.apache.commons.lang3.tuple.Triple;

import javax.inject.Inject;

import de.fhb.ca.dto.QuestionsDTO;
import de.fhb.ca.dto.RequestDTO;
import de.fhb.campusapp.eval.data.local.RetrofitHelper;
import de.fhb.campusapp.eval.interfaces.RetroRequestService;
import de.fhb.campusapp.eval.ui.base.BasePresenter;
import de.fhb.campusapp.eval.ui.eval.EvaluationActivity;
import de.fhb.campusapp.eval.utility.ClassMapper;
import de.fhb.campusapp.eval.utility.DataHolder;
import de.fhb.campusapp.eval.utility.DialogFactory;
import de.fhb.campusapp.eval.utility.EventBus;
import de.fhb.campusapp.eval.utility.Events.NetworkErrorEvent;
import de.fhb.campusapp.eval.utility.Events.RequestErrorEvent;
import de.fhb.campusapp.eval.utility.Events.RequestSuccessEvent;
import de.fhb.campusapp.eval.utility.Utility;
import de.fhb.campusapp.eval.utility.vos.QuestionsVO;
import fhb.de.campusappevaluationexp.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Sebastian Müller on 16.10.2016.
 */
public class ScanPresenter extends BasePresenter<ScanMvpView> {

    @Inject
    Resources mResources;

    @Inject
    RetrofitHelper mRetrofitHelper;

    Retrofit mRetrofit;

    @Inject
    public ScanPresenter(Resources mResources) {
        super();
        this.mResources = mResources;
    }

    public Retrofit createOrGetRetrofit(){
        // create retrofit instance after receiving host address
        if(mRetrofit == null){
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(DataHolder.getHostName() + "/")
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
        }

        return mRetrofit;
    }

    /**
     * Executes request retrieving questions and choices from REST server
     */
    public boolean performQuestionRequest() {

        createOrGetRetrofit();

        getMvpView().changeToolbarTitle(mResources.getString(R.string.scan_send));
        getMvpView().showProgressOverlay();

        RequestDTO requestDTO = new RequestDTO(DataHolder.getAnswersVO().getVoteToken(), DataHolder.getUuid());
        RetroRequestService requestService = mRetrofit.create(RetroRequestService.class);
        Call<QuestionsDTO> response = requestService.requestQuestions(requestDTO);
        response.enqueue(new RetrofitCallback());
        return true;
    }

    public void requestInternetPermissionAndConnectServer(PermissionManager manager){
        manager.with(Manifest.permission.INTERNET)
                .onPermissionGranted(() -> performQuestionRequest())
                .onPermissionDenied(() -> getMvpView().callSaveTerminateTask())
                .onPermissionShowRationale(request -> getMvpView().showInternetExplanation(request))
                .request();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onRequestSuccess(RequestSuccessEvent<QuestionsDTO> event){

        QuestionsVO vo = ClassMapper.questionsDTOToQuestionsVOMapper(event.getRequestedObject());
        DataHolder.setQuestionsVO(vo);
        // Hide progress overlay (with animation):
        getMvpView().hideProgressOverlay();
        getMvpView().startEvaluationActivity();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onRequestError(RequestErrorEvent event){
        Triple<String, String, String> errorText = mRetrofitHelper.processRequestError(event.getResposne(), mResources, mRetrofit);

        if(errorText.getRight().equals("RETRY_SCAN")){
            getMvpView().showRetryScanDialog(errorText.getLeft(), errorText.getMiddle());
        } else if(errorText.getRight().equals("RETRY_COMMUNICATION")){
            getMvpView().showRetryServerCommunicationDialog(errorText.getLeft(), errorText.getMiddle());
        }
        getMvpView().hideProgressOverlay();
    }



    @Subscribe
    @SuppressWarnings("unused")
    public void onNetworkError(NetworkErrorEvent event){
        getMvpView().setRequestRunning(false);
        Throwable t = event.getRetrofitError();
        Pair<String, String> errorText = mRetrofitHelper.processNetworkError(t, mResources);

        getMvpView().showNetworkErrorDialog(errorText.first, errorText.second);
        getMvpView().changeToolbarTitle(R.string.scan_search);
        getMvpView().hideProgressOverlay();

    }

    private class RetrofitCallback implements Callback<QuestionsDTO> {

        @Override
        public void onResponse(Call<QuestionsDTO> call, Response<QuestionsDTO> response) {
            getMvpView().changeToolbarTitle(mResources.getString(R.string.scan_search));
            getMvpView().setRequestRunning(false);

            if(response.isSuccessful()){
                EventBus.get().post(new RequestSuccessEvent<>(response.body(), response));
            } else {
                EventBus.get().post(new RequestErrorEvent<>(response));
            }
        }

        @Override
        public void onFailure(Call<QuestionsDTO> call, Throwable t) {
            EventBus.get().post(new NetworkErrorEvent(t));
        }
    }
}
