package de.fhb.campusapp.eval.data.local;

import android.content.res.Resources;
import android.support.v4.util.Pair;

import org.apache.commons.lang3.tuple.Triple;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import de.fhb.ca.dto.QuestionsDTO;
import de.fhb.ca.dto.ResponseDTO;
import de.fhb.ca.dto.util.ErrorType;
import fhb.de.campusappevaluationexp.R;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Sebastian Müller on 09.10.2016.
 */
public class RetrofitHelper {



    public Pair<String, String> processNetworkError(Throwable t, Resources resources){
        Pair<String, String> result = null;
        if(t != null){
            t.printStackTrace();

            if (t.getClass() == ConnectException.class) {
                result = Pair.create(resources.getString(R.string.no_network_title), resources.getString(R.string.no_network_message));

//                MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.no_network_title), mResources.getString(R.string.no_network_message), false, MessageFragment.Option.RetryCommunication);
//                fragment.show(getSupportFragmentManager(), "NoInternet");
            } else if(t.getClass() == SocketTimeoutException.class || t.getClass() == SocketException.class){
                result = Pair.create(resources.getString(R.string.socket_timeout_title), resources.getString(R.string.socket_timeout_message));

//                MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.socket_timeout_title), mResources.getString(R.string.socket_timeout_message), true, MessageFragment.Option.RetryCommunication);
//                fragment.show(getSupportFragmentManager(), "ServerNotResponding");
            } else {
                result = Pair.create(resources.getString(R.string.some_network_error_title), resources.getString(R.string.some_network_error_message));

//                MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.some_network_error_title), mResources.getString(R.string.some_network_error_message), true, MessageFragment.Option.RetryCommunication);
//                fragment.show(getSupportFragmentManager(), "SomeError");
            }
        }
        return result;
    }

    public Triple<String, String, String> processRequestError(Response<QuestionsDTO> response, Resources resources, Retrofit retrofit) {
        int statusCode = response.code();
        Triple<String, String, String> result = null;

        try {
            ResponseBody body = response.errorBody();
            // annotation array must be created in order to prevent nullPointer
            ResponseDTO dto = (ResponseDTO) retrofit.responseBodyConverter(ResponseDTO.class, new Annotation[1] ).convert(body);

            if (dto != null && dto.getType() == ErrorType.INVALID_TOKEN) {
                result = Triple.of(resources.getString(R.string.invalid_token_title), resources.getString(R.string.invalid_token_message), "RETRY_SCAN");
            } else if (dto != null && dto.getType() == ErrorType.TOKEN_ALLREADY_USED) {
                result = Triple.of(resources.getString(R.string.token_already_used_title), resources.getString(R.string.token_already_used_message), "RETRY_SCAN");
            } else if (dto != null && dto.getType() == ErrorType.EVALUATION_CLOSED) {
                result = Triple.of(resources.getString(R.string.evaluation_closed_title), resources.getString(R.string.evaluation_closed_message), "RETRY_SCAN");
            } else if (dto != null && dto.getType() == ErrorType.UNKNOWN_ERROR) {
                result = Triple.of(resources.getString(R.string.unknown_error_title), resources.getString(R.string.unknown_error_message), "RETRY_SCAN");
            } else if (dto != null && dto.getType() == ErrorType.MALFORMED_REQUEST) {
                result = Triple.of(resources.getString(R.string.unknown_error_title), resources.getString(R.string.unknown_error_message), "RETRY_SCAN");
            } else {
                // Check of status codes and display information to user
                if (statusCode == HttpURLConnection.HTTP_BAD_GATEWAY || statusCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                    result = Triple.of(resources.getString(R.string.error_500_502_title), resources.getString(R.string.error_500_502_message), "RETRY_COMMUNICATION");
                } else if (statusCode == HttpURLConnection.HTTP_UNAVAILABLE) {
                    result = Triple.of(resources.getString(R.string.error_503_title), resources.getString(R.string.error_503_message), "RETRY_COMMUNICATION");
                } else if (statusCode == HttpURLConnection.HTTP_FORBIDDEN || statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
                    result = Triple.of(resources.getString(R.string.error_404_403_title), resources.getString(R.string.error_404_403_message), "RETRY_COMMUNICATION");
                } else if (statusCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                    result = Triple.of(resources.getString(R.string.unknown_error_title), resources.getString(R.string.unknown_error_message), "RETRY_COMMUNICATION");
                }
            }
        } catch (IOException | IllegalArgumentException e ) {
            e.printStackTrace();
        }
        return result;
    }
}
