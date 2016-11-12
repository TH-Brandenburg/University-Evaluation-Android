package de.fhb.campusapp.eval.interfaces;

import de.fhb.ca.dto.QuestionsDTO;
import de.fhb.ca.dto.RequestDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

//import retrofit.Call;

/**
 * Created by Admin on 01.12.2015.
 */
public interface RetroRequestService {

    @POST("v1/questions")
    public Call<QuestionsDTO> requestQuestions(@Body RequestDTO dto);

}
