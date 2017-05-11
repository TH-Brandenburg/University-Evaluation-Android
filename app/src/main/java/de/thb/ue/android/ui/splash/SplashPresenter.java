package de.thb.ue.android.ui.splash;

import javax.inject.Inject;

import de.thb.ue.android.data.IDataManager;
import de.thb.ue.android.ui.base.BasePresenter;
import de.thb.ue.android.utility.customized_classes.CheckableCookieJar;


/**
 * Created by Sebastian Müller on 03.01.2017.
 */

public class SplashPresenter extends BasePresenter<SplashMvpView> {

    private final IDataManager mDataManager;
    private final CheckableCookieJar mCookieJar;

    @Inject
    SplashPresenter(IDataManager dataManager, CheckableCookieJar cookieJar) {
        mDataManager = dataManager;
        mCookieJar = cookieJar;
    }

    boolean isConnected(){
        return mDataManager.isConnected();
    }
}
