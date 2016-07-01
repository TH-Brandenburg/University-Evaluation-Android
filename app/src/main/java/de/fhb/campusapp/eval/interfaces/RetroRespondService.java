package de.fhb.campusapp.eval.interfaces;

//import com.squareup.okhttp.RequestBody;

import de.fhb.ca.dto.ResponseDTO;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


/**
 * Created by Admin on 01.12.2015.
 */
public interface RetroRespondService {

//    @Headers("Content-Transfer-Encoding: binary")
    @Multipart
    @POST("v1/answers")
    public Call<ResponseDTO> sendAnswersWithPictures(@Part("answers-dto")RequestBody answers, @Part MultipartBody.Part images);


/*
    @Multipart
    @POST("/v1/photo")
    public Call<ResponseDTO> sendZippedPhotos(@Part("photos") TypedFile zippedPhotos);
*/

//    @POST("/v1/answers")
//    public Call<AnswersDTO> sendAnswers(@Body AnswersDTO answers);
//
//    @Multipart
//    @POST("/v1/answers")
//    public Call<ResponseDTO> sendAnswersWithPictures(@Part("answers-dto")RequestBody answersDTO, @Part("images\"; filename=\"zippedImages.zip") RequestBody zippedImages);
//
//    @Multipart
//    @POST("/v1/answers")
//    public Call<ResponseDTO> sendPictures(@Part("images\"; filename=\"zippedImages.zip") RequestBody image);

}
