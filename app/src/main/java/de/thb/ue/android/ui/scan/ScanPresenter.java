package de.thb.ue.android.ui.scan;

import android.Manifest;
import android.content.Context;
import android.content.res.Resources;

import com.abhi.barcode.frag.libv2.ScanResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.buchandersenn.android_permission_manager.PermissionManager;

import java.io.IOException;

import javax.inject.Inject;

import de.thb.ue.android.data.IDataManager;
import de.thb.ue.android.data.VOs.ProcessedResponse;
import de.thb.ue.android.data.VOs.QuestionsVO;
import de.thb.ue.android.injection.ApplicationContext;
import de.thb.ue.android.ui.base.BasePresenter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import thb.de.ue.android.R;

/**
 * Created by scorp on 05.05.2017.
 */

public class ScanPresenter extends BasePresenter<ScanMvpView> {

    IDataManager mDataManager;
    ObjectMapper mMapper;
    Context mContext;
    Resources mResources;

    @Inject
    public ScanPresenter(IDataManager dataManager, ObjectMapper mapper, @ApplicationContext Context context) {
        mDataManager = dataManager;
        mMapper = mapper;
        mContext = context;
        mResources = context.getResources();
    }

    void processScanResult(ScanResult result, PermissionManager permissionManager){
//        try {
//            ScanResultVO vo = mMapper.readValue(result.getRawResult().getText(), ScanResultVO.class);

        permissionManager.with(Manifest.permission.INTERNET)
                .onPermissionGranted(() ->{
                    getMvpView().showProgressOverlay();
                    getMvpView().setSubtitle(R.string.scan_send);
                    mDataManager.getQuestions("testToken", "testHost")
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<ProcessedResponse<QuestionsVO>>() {
                                @Override
                                public void onSuccess(ProcessedResponse<QuestionsVO> response) {
                                    if(response.getCode() == 200){
                                        getMvpView().hideProgressOverlay();
                                        getMvpView().setSubtitle(R.string.scan_forward);
                                        getMvpView().goToEvaluation();
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                })
                .onPermissionShowRationale(permissionRequest ->{
                    String title = mResources.getString(R.string.internet_explanation_title);
                    String message = mResources.getString(R.string.internet_explanation_message);
                    getMvpView().displayGenericErrorDialog(title, message);
                })
                .request();

//        } catch (IOException e) {
//            getMvpView().displayGenericActionDialog(
//                     mResources.getString(R.string.wrong_qr_code_error_title)
//                    , mResources.getString(R.string.wrong_qr_code_error_message)
//                    , (dialog, which) -> getMvpView().restartScanning()
//                    , dialog -> getMvpView().restartScanning()
//                    , true);
//        }
    }

    private class ScanResultVO{
        private String voteToken;
        private String host;

        public ScanResultVO(String voteToken, String host, String deviceID) {
            this.voteToken = voteToken;
            this.host = host;
        }

        public ScanResultVO() {
        }

        public String getVoteToken() {
            return voteToken;
        }

        public void setVoteToken(String voteToken) {
            this.voteToken = voteToken;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }
    }
}
