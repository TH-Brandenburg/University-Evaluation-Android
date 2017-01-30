package de.fhb.campusapp.eval.ui.path;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import de.fhb.campusapp.eval.data.IDataManager;
import de.fhb.campusapp.eval.ui.base.BasePresenter;
import fhb.de.campusappevaluationexp.R;

/**
 * Created by Admin on 19.11.2016.
 */

public class PathPresenter extends BasePresenter<PathMvpView> {

    private final IDataManager mDataManager;

    @Inject
    public PathPresenter(IDataManager dataManager) {
        super();
        this.mDataManager = dataManager;
    }

    public ArrayList<String> getStudyPaths(){
        return (ArrayList<String>) mDataManager.getmQuestionsVO().getStudyPaths();
    }

    public void setStudyPath(String studyPath){
        mDataManager.getmAnswersVO().setStudyPath(studyPath);
    }
}
