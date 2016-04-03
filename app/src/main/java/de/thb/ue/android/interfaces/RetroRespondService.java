package de.thb.ue.android.interfaces;

//import com.squareup.okhttp.RequestBody;

import de.thb.ca.dto.AnswersDTO;
import de.thb.ca.dto.ResponseDTO;
//import retrofit.Call;
import retrofit.Callback;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

/**
 * Created by Admin on 01.12.2015.
 */
public interface RetroRespondService {

    @Headers("Content-Transfer-Encoding: binary")
    @Multipart
    @POST("/v1/answers")
    public void sendAnswersWithPictures(@Part("images") TypedFile images, @Part("answers-dto")TypedString answers, Callback<ResponseDTO> responseDTO);


    @Multipart
    @POST("/v1/photo")
    public void sendZippedPhotos(@Part("photos") TypedFile zippedPhotos, Callback<ResponseDTO> callback);

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
