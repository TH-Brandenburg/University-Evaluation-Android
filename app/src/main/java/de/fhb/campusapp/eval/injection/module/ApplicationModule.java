package de.fhb.campusapp.eval.injection.module;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.ViewCompat;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.fhb.campusapp.eval.data.DataManager;
import de.fhb.campusapp.eval.data.IDataManager;
import de.fhb.campusapp.eval.data.local.PreferencesHelper;
import de.fhb.campusapp.eval.data.local.RetrofitHelper;
import de.fhb.campusapp.eval.injection.ApplicationContext;
import de.fhb.campusapp.eval.utility.ClassMapper;
import de.fhb.campusapp.eval.utility.eventpipelines.NetworkEventPipelines;
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
    ClassMapper provideMapper() { return new ClassMapper(); }

    @Provides @Singleton
    Resources provideResources(){ return mApplication.getResources(); }

    @Provides @Singleton
    ObjectMapper provideObjectMapper(){
        return new ObjectMapper();
    }

    @Provides @Singleton
    RetrofitHelper provideRetrofitHelper(@ApplicationContext Context context){
        return new RetrofitHelper(context);
    }

    @Provides @Singleton
    PreferencesHelper providePreferencesHelper(ObjectMapper mapper){
        return new PreferencesHelper(mapper, mApplication);
    }

    @Provides @Singleton
    IDataManager provideDataManager(PreferencesHelper preferencesHelper
            , RetrofitHelper retrofitHelper, NetworkEventPipelines networkEventPipelines){
        return new DataManager(preferencesHelper, retrofitHelper, networkEventPipelines);
    }

    @Provides @Singleton
    NetworkEventPipelines provideNetworkEventPipelines(){
        return new NetworkEventPipelines();
    }
}
