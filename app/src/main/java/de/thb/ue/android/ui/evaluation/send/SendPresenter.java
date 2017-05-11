package de.thb.ue.android.ui.evaluation.send;

import android.content.Context;

import javax.inject.Inject;

import de.thb.ue.android.data.IDataManager;
import de.thb.ue.android.injection.ActivityContext;
import de.thb.ue.android.ui.base.BasePresenter;

/**
 * Created by scorp on 05.05.2017.
 */

public class SendPresenter extends BasePresenter<SendMvpView> {

    private final IDataManager mDataManager;
    private final Context mContext;
    private int mPosition;

    @Inject
    public SendPresenter(IDataManager dataManager, @ActivityContext Context context) {
        super();
        this.mDataManager = dataManager;
        this.mContext = context;
    }

    void setmPosition(int position) {
        this.mPosition = position;
    }

    int getmPosition() {
        return mPosition;
    }
}
