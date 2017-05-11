package de.thb.ue.android.ui.evaluation;

import android.content.Context;

import javax.inject.Inject;

import de.thb.ue.android.data.IDataManager;
import de.thb.ue.android.injection.ApplicationContext;
import de.thb.ue.android.ui.base.BasePresenter;
import de.thb.ue.android.ui.base.MvpView;

/**
 * Created by scorp on 05.05.2017.
 */

public class EvaluationPresenter extends BasePresenter<MvpView> {

    private final IDataManager mDataManager;
    private final Context mContext;

    @Inject
    public EvaluationPresenter(IDataManager dataManager, @ApplicationContext Context context) {
        super();
        this.mDataManager = dataManager;
        this.mContext = context;
    }
}
