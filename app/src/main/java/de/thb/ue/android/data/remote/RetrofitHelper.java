package de.thb.ue.android.data.remote;

import javax.inject.Inject;

import de.thb.ue.android.utility.customized_classes.CheckableCookieJar;
import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Sebastian Müller on 09.10.2016.
 */
public class RetrofitHelper {

    private Retrofit mRetrofit;
    private CheckableCookieJar mCookieJar;

    private final static String API_VERSION = "1";

    @Inject
    public RetrofitHelper(Retrofit retrofit, CheckableCookieJar cookieJar) {
        mRetrofit = retrofit;
        mCookieJar = cookieJar;
    }

    public void clearSessionCookies(){
        mCookieJar.clearSession();
    }

    public void clearAllCookies(){
        mCookieJar.clear();
    }

    public boolean isSessionCookieExpired(){
        return mCookieJar.isSessionCookieExpired();
    }

    //*********** Authentication *************************

    public Single<Response<Void>> performAuthentication(String password, String username) {
        AuthenticationService authService = mRetrofit.create(AuthenticationService.class);
        return authService.authenticate(password, username);
    }


    //*********** Retrofit Interfaces *************************



    private interface AuthenticationService {
        @FormUrlEncoded
        @POST("auth/p/userpass")
        Single<Response<Void>> authenticate(@Field("password") String password, @Field("userName") String username);

    }
}
