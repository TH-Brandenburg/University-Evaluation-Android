package de.thb.ue.android.ui.evaluation.choice;

import android.content.Context;

import java.util.List;

import javax.inject.Inject;

import de.thb.ue.android.data.IDataManager;
import de.thb.ue.android.data.VOs.ChoiceVO;
import de.thb.ue.android.injection.ActivityContext;
import de.thb.ue.android.ui.base.BasePresenter;

/**
 * Created by scorp on 05.05.2017.
 */

public class ButtonPresenter extends BasePresenter<ButtonMvpView> {

    private IDataManager mDataManager;
    private Context mContext;
    private int mPosition;
    private List<ChoiceVO> mChoices;
    private String mQuestion;

    @Inject
    public ButtonPresenter(IDataManager dataManager, @ActivityContext Context context) {
        mDataManager = dataManager;
        mContext = context;
    }

    boolean questionGotNoCommentOption(){
        for(ChoiceVO choice : mChoices){
            if(choice.getGrade() == 0){
                return true;
            }
        }
        return false;
    }



    int getChoiceCount(){
        return mChoices.size();
    }

    /**
     * Returns the grade of the choice at the specified mPosition within the collection of choices.
     * @param index mPosition of the choice within the collection of choices
     * @return
     */
    int getChoiceGrade(int index){
        return mChoices.get(index).getGrade();
    }


    public int getmPosition() {
        return mPosition;
    }

    public void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    public List<ChoiceVO> getmChoices() {
        return mChoices;
    }

    public void setmChoices(List<ChoiceVO> mChoices) {
        this.mChoices = mChoices;
    }

    public String getmQuestion() {
        return mQuestion;
    }

    public void setmQuestion(String mQuestion) {
        this.mQuestion = mQuestion;
    }
}
