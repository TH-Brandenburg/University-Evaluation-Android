package de.fhb.campusapp.eval.injection.module;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

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
import de.fhb.campusapp.eval.utility.eventpipelines.AppEventPipelines;
import de.fhb.campusapp.eval.utility.eventpipelines.NetworkEventPipelines;

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
    IDataManager provideDataManager(PreferencesHelper preferencesHelper, RetrofitHelper retrofitHelper,
                                    NetworkEventPipelines networkEventPipelines, AppEventPipelines appControllEvents, @ApplicationContext Context context){
        return new DataManager(preferencesHelper, retrofitHelper, context, networkEventPipelines, appControllEvents);
    }

    @Provides @Singleton
    NetworkEventPipelines provideNetworkEventPipelines(){
        return new NetworkEventPipelines();
    }

    @Provides @Singleton
    AppEventPipelines provideAppEventPipelines(){return new AppEventPipelines();}

    @Provides @Singleton
    DisplayMetrics provideDisplayMetrics(){
        return mApplication.getResources().getDisplayMetrics();
    }
}
