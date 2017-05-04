package de.thb.ue.android.injection.module;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.util.DisplayMetrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.thb.ue.android.data.DataManager;
import de.thb.ue.android.data.IDataManager;
import de.thb.ue.android.data.local.MappingHelper;
import de.thb.ue.android.data.local.PreferencesHelper;
import de.thb.ue.android.data.remote.RetrofitHelper;
import de.thb.ue.android.injection.ApplicationContext;
import de.thb.ue.android.utility.Connectivity;
import de.thb.ue.android.utility.FeatureSwitch;
import de.thb.ue.android.utility.customized_classes.CheckableCookieJar;
import de.thb.ue.android.utility.eventbus.EventBus;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;


/**
 * Created by Sebastian Müller on 14.10.2016.
 */
@Module
public class ApplicationModule {

    protected final Application mApplication;

    public ApplicationModule(Application application) { mApplication = application; }

    @Provides
    Application provideApplication(){ return mApplication; }

    @Provides @ApplicationContext
    Context provideContext() { return mApplication; }

    @Provides @Singleton
    Resources provideResources(){ return mApplication.getResources(); }

    @Provides @Singleton
    ObjectMapper provideObjectMapper(){
        ObjectMapper mapper = new ObjectMapper();
        return mapper;
    }

    @Provides @Singleton
    RetrofitHelper provideRetrofitHelper(Retrofit retrofit, CheckableCookieJar cookieJar){
        return new RetrofitHelper(retrofit, cookieJar);
    }

    @Provides @Singleton
    PreferencesHelper providePreferencesHelper(ObjectMapper mapper){
        return new PreferencesHelper(mapper, mApplication);
    }

    @Provides @Singleton
    MappingHelper provideMappingHelper(){
        return new MappingHelper();
    }

    @Provides @Singleton
    IDataManager provideDataManager(@ApplicationContext Context context, PreferencesHelper preferencesHelper, RetrofitHelper retrofitHelper, MappingHelper mappingHelper, Connectivity connectivity, EventBus eventBus){
        return new DataManager(context, preferencesHelper, retrofitHelper, mappingHelper, eventBus, connectivity);
    }

    @Provides @Singleton
    Connectivity provideConnectivity(){
        return new Connectivity();
    }

    @Provides @Singleton
    DisplayMetrics provideDisplayMetrics(){
        return mApplication.getResources().getDisplayMetrics();
    }

    @Provides @Singleton
    CheckableCookieJar provideCookieJar(){
        return new CheckableCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(mApplication));
    }

    @Provides @Singleton
    OkHttpClient provideOkHttpClient(CheckableCookieJar cookieJar){
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();

        builder.readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .cookieJar(cookieJar) // catches cookies and appends them to outgoing requests
                .followRedirects(false);

        if(FeatureSwitch.DEBUG_MODE){
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

            builder.addInterceptor(loggingInterceptor);
        }
        return builder.build();
    }

    @Provides @Singleton
    Retrofit provideRetrofitInstance(OkHttpClient httpClient){
        String hostName;

        if(FeatureSwitch.DEBUG_MODE){
            hostName = "http://192.168.178.73:9999";
        } else {
            hostName = "https://www.timum.de";
        }

        // create retrofit instance after receiving host address
        return new Retrofit.Builder()
                .baseUrl(hostName + "/")
                .client(httpClient)
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @Provides @Singleton
    EventBus provideEventBus(){
        return new EventBus();
    }

    @Provides @Singleton
    IntentFilter provideConnectivityFilter(){
        return new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    }


}
