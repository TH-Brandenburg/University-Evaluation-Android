package de.thb.ue.android.ui.evaluation.studypath;

import android.content.Context;

import java.util.List;

import javax.inject.Inject;

import de.thb.ue.android.data.IDataManager;
import de.thb.ue.android.injection.ActivityContext;
import de.thb.ue.android.ui.base.BasePresenter;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by scorp on 05.05.2017.
 */

public class PathPresenter extends BasePresenter<PathMvpView> {
    private final IDataManager mDataManager;
    private final Context mContext;

    private int mPosition;

    @Inject
    public PathPresenter(IDataManager dataManager, @ActivityContext Context context) {
        super();
        this.mDataManager = dataManager;
        this.mContext = context;
    }

    public int getmPosition() {
        return mPosition;
    }

    public void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    List<String> getStudyPaths(){
        return mDataManager.getCachedQuestions().getStudyPaths();
    }

    void setStudyPath(String studyPath){
        mDataManager.putStudyPath(studyPath)
                .subscribeOn(Schedulers.computation())
                .subscribe();
    }
}
