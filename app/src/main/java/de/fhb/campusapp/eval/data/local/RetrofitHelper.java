package de.fhb.campusapp.eval.data.local;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.util.Pair;

import com.squareup.otto.Subscribe;

import org.apache.commons.lang3.tuple.Triple;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import de.fhb.ca.dto.QuestionsDTO;
import de.fhb.ca.dto.RequestDTO;
import de.fhb.ca.dto.ResponseDTO;
import de.fhb.ca.dto.util.ErrorType;
import de.fhb.campusapp.eval.injection.ApplicationContext;
import de.fhb.campusapp.eval.utility.ClassMapper;
import de.fhb.campusapp.eval.utility.EventBus;
import de.fhb.campusapp.eval.utility.Events.NetworkErrorEvent;
import de.fhb.campusapp.eval.utility.Events.RequestErrorEvent;
import de.fhb.campusapp.eval.utility.Events.RequestSuccessEvent;
import de.fhb.campusapp.eval.utility.vos.AnswersVO;
import de.fhb.campusapp.eval.utility.vos.QuestionsVO;
import fhb.de.campusappevaluationexp.R;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by Sebastian Müller on 09.10.2016.
 */
public class RetrofitHelper {

    Resources mResources;

    Retrofit mRetrofit;

    @Inject
    public RetrofitHelper(@ApplicationContext Context context) {
        mResources = context.getResources();
    }

    private OkHttpClient createOkHttpClient(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)
                .connectTimeout(3, TimeUnit.SECONDS)
                .build();

        return okHttpClient;
    }

    private Retrofit createOrGetRetrofitInstance(String hostName){
        // create retrofit instance after receiving host address
        if(mRetrofit == null){
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(hostName + "/")
                    .client(createOkHttpClient())
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
        }
        return mRetrofit;
    }

    public Observable<Response<QuestionsDTO>> performGetQuestionsRequest(AnswersVO answersVO, String uuid, String hostName) {
        RequestDTO requestDTO = new RequestDTO(answersVO.getVoteToken(), uuid);
        RetroQuestionsRequestService requestService = createOrGetRetrofitInstance(hostName).create(RetroQuestionsRequestService.class);
        return requestService.requestQuestions(requestDTO);
    }

    public void performPostAnswersRequest(String hostName){
//        RetroAnswersRespondService respondService = createOrGetRetrofitInstance(hostName).create(RetroAnswersRespondService.class);
//        Call<QuestionsDTO> response = respondService.sendAnswersWithPictures();
//        response.enqueue(new RetrofitCallback());
    }

    public Pair<String, String> processNetworkError(Throwable t){
        Pair<String, String> result = null;
        if(t != null){
            t.printStackTrace();

            if (t.getClass() == ConnectException.class) {
                result = Pair.create(mResources.getString(R.string.no_network_title), mResources.getString(R.string.no_network_message));
            } else if(t.getClass() == SocketTimeoutException.class || t.getClass() == SocketException.class){
                result = Pair.create(mResources.getString(R.string.socket_timeout_title), mResources.getString(R.string.socket_timeout_message));
            } else {
                result = Pair.create(mResources.getString(R.string.some_network_error_title), mResources.getString(R.string.some_network_error_message));
            }
        }
        return result;
    }

    public Triple<String, String, String> processRequestError(Response<?> response) {
        int statusCode = response.code();
        Triple<String, String, String> result = null;
        ResponseDTO dto = null;
        try {
            ResponseBody body = response.errorBody();
            // annotation array must be created in order to prevent nullPointer
            dto = (ResponseDTO) mRetrofit.responseBodyConverter(ResponseDTO.class, new Annotation[1] ).convert(body);
        } catch (IOException | IllegalArgumentException e ) {
            e.printStackTrace();
        }

            if (dto != null && dto.getType() == ErrorType.INVALID_TOKEN) {
                result = Triple.of(mResources.getString(R.string.invalid_token_title), mResources.getString(R.string.invalid_token_message), "RETRY_SCAN");
            } else if (dto != null && dto.getType() == ErrorType.TOKEN_ALLREADY_USED) {
                result = Triple.of(mResources.getString(R.string.token_already_used_title), mResources.getString(R.string.token_already_used_message), "RETRY_SCAN");
            } else if (dto != null && dto.getType() == ErrorType.EVALUATION_CLOSED) {
                result = Triple.of(mResources.getString(R.string.evaluation_closed_title), mResources.getString(R.string.evaluation_closed_message), "RETRY_SCAN");
            } else if (dto != null && dto.getType() == ErrorType.UNKNOWN_ERROR) {
                result = Triple.of(mResources.getString(R.string.unknown_error_title), mResources.getString(R.string.unknown_error_message), "RETRY_SCAN");
            } else if (dto != null && dto.getType() == ErrorType.MALFORMED_REQUEST) {
                result = Triple.of(mResources.getString(R.string.unknown_error_title), mResources.getString(R.string.unknown_error_message), "RETRY_SCAN");
            } else {
                // Check of status codes and display information to user
                if (statusCode == HttpURLConnection.HTTP_BAD_GATEWAY || statusCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                    result = Triple.of(mResources.getString(R.string.error_500_502_title), mResources.getString(R.string.error_500_502_message), "RETRY_COMMUNICATION");
                } else if (statusCode == HttpURLConnection.HTTP_UNAVAILABLE) {
                    result = Triple.of(mResources.getString(R.string.error_503_title), mResources.getString(R.string.error_503_message), "RETRY_COMMUNICATION");
                } else if (statusCode == HttpURLConnection.HTTP_FORBIDDEN || statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
                    result = Triple.of(mResources.getString(R.string.error_404_403_title), mResources.getString(R.string.error_404_403_message), "RETRY_COMMUNICATION");
                } else {
                    result = Triple.of(mResources.getString(R.string.unknown_error_title), mResources.getString(R.string.unknown_error_message), "RETRY_COMMUNICATION");
                }
            }

        return result;
    }

//    private class RetrofitCallback implements Callback<QuestionsDTO> {
//
//        @Override
//        public void onResponse(Call<QuestionsDTO> call, Response<QuestionsDTO> response) {
//
//            if(response.isSuccessful()){
//                EventBus.get().post(new RequestSuccessEvent<>(response.body(), response));
//            } else {
//                EventBus.get().post(new RequestErrorEvent<>(response));
//            }
//        }
//
//        @Override
//        public void onFailure(Call<QuestionsDTO> call, Throwable t) {
//            EventBus.get().post(new NetworkErrorEvent(t));
//        }
//    }

    private interface RetroQuestionsRequestService {
        @POST("v1/questions")
        Observable<Response<QuestionsDTO>> requestQuestions(@Body RequestDTO dto);
    }

    private interface RetroAnswersRespondService {
        @Multipart
        @POST("v1/answers")
        Observable<ResponseDTO> sendAnswersWithPictures(@Part("answers-dto") RequestBody answers, @Part MultipartBody.Part images);
    }
}
