package de.thb.ue.android.interfaces;

import de.thb.ca.dto.QuestionsDTO;
import de.thb.ca.dto.RequestDTO;
//import retrofit.Call;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by Admin on 01.12.2015.
 */
public interface RetroRequestService {

    //retrofit 1.9
    @POST("/v1/questions")
    public void requestQuestions(@Body RequestDTO dto, Callback<QuestionsDTO> callback);

    //retrofit 2.0 Beta2
//    @POST("/v1/questions")
//    public Call<QuestionsDTO> requestQuestions(@Body RequestDTO dto);
}
