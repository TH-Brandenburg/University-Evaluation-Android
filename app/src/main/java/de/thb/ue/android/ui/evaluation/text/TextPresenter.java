package de.thb.ue.android.ui.evaluation.text;

import android.content.Context;

import javax.inject.Inject;

import de.thb.ue.android.data.IDataManager;
import de.thb.ue.android.injection.ActivityContext;
import de.thb.ue.android.ui.base.BasePresenter;

/**
 * Created by scorp on 05.05.2017.
 */

public class TextPresenter extends BasePresenter<TextMvpView> {

    private final IDataManager mDataManager;
    private final Context mContext;
    private int mPosition;
    private String mQuestion;

    @Inject
    public TextPresenter(IDataManager dataManager, @ActivityContext Context context) {
        super();
        this.mDataManager = dataManager;
        this.mContext = context;
    }

    public void setmPosition(int position) {
        this.mPosition = position;
    }

    public int getmPosition() {
        return mPosition;
    }

    public String getmQuestion() {
        return mQuestion;
    }

    public void setmQuestion(String mQuestion) {
        this.mQuestion = mQuestion;
    }
}
