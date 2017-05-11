package de.thb.ue.android.utility.customized_classes;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Path;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.thb.ue.android.data.IDataManager;
import de.thb.ue.android.data.VOs.ChoiceVO;
import de.thb.ue.android.data.VOs.QuestionsVO;
import de.thb.ue.android.data.VOs.SingleChoiceQuestionVO;
import de.thb.ue.android.data.VOs.TextQuestionVO;
import de.thb.ue.android.injection.ActivityContext;
import de.thb.ue.android.ui.evaluation.choice.ButtonFragment;
import de.thb.ue.android.ui.evaluation.send.SendFragment;
import de.thb.ue.android.ui.evaluation.studypath.PathFragment;
import de.thb.ue.android.ui.evaluation.text.TextFragment;
import thb.de.ue.android.R;

/**
 * Created by scorp on 15.02.2017.
 */

public class BasePagerAdapter extends FragmentStatePagerAdapter {

    private final Context mContext;
    private final IDataManager mDataManager;
    private final List<Page> mPages;
    private int pageCount = -1;

    @Inject
    public BasePagerAdapter(FragmentManager fm, @ActivityContext Context context, IDataManager dataManager) {
        super(fm);
        this.mContext = context;
        this.mDataManager = dataManager;
        this.mPages = initializePages();
    }

    @Override
    public Fragment getItem(int position) {
        Page page = mPages.get(position);
        switch (page.pageKind){
            case pathFragment:
                return PathFragment.newInstance(page.position);
            case buttonFragment:
                return ButtonFragment.newInstance(page.position, page.question, (ArrayList<ChoiceVO>) page.choices);
            case textFragment:
                return TextFragment.newInstance(page.position, page.question);
            default:
                return SendFragment.newInstance(page.position);
        }
    }

    @Override
    public int getCount() {
        if(pageCount == -1){
            pageCount = mDataManager.getCachedQuestions().getSingleChoiceQuestionVOs().size() + mDataManager.getCachedQuestions().getTextQuestions().size() + 2;
        }

        return pageCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Resources res = mContext.getResources();

        if(position == 0) {
            return res.getString(R.string.tab_view_button_choose);
        } else if((position + 1) < getCount()){
            return res.getString(R.string.tab_view_button_question) + " " + (position) + "/" + (getCount() - 2);
        } else {
            return res.getString(R.string.tab_view_button_send);
        }
    }

    private List<Page> initializePages() {
        List<Page> result = new ArrayList<>();
        QuestionsVO questionsVo = mDataManager.getCachedQuestions();
        List<SingleChoiceQuestionVO> scQuestions = questionsVo.getSingleChoiceQuestionVOs();
        List<TextQuestionVO> textQuestions = questionsVo.getTextQuestions();
        int position = 0;

        result.add(new Page(position, null, PageKind.pathFragment, null));

        if(questionsVo.getTextQuestionsFirst()){
            for(TextQuestionVO textQuestion : textQuestions){
                position++;
                result.add(new Page(position, textQuestion.getQuestionText(), PageKind.textFragment, null));
            }
            for(SingleChoiceQuestionVO scQuestion : scQuestions){
                position++;
                result.add(new Page(position, scQuestion.getQuestion(), PageKind.buttonFragment, scQuestion.getChoices()));
            }

        } else {
            for(SingleChoiceQuestionVO scQuestion : scQuestions){
                position++;
                result.add(new Page(position, scQuestion.getQuestion(), PageKind.buttonFragment, scQuestion.getChoices()));
            }
            for(TextQuestionVO textQuestion : textQuestions){
                position++;
                result.add(new Page(position, textQuestion.getQuestionText(), PageKind.textFragment, null));
            }
        }

        result.add(new Page(position + 1, null, PageKind.sendFragment, null));
        return result;
    }

    private class Page {
        int position;
        String question;
        PageKind pageKind;
        List<ChoiceVO> choices;

        Page(int position, String question, PageKind pageKind, List<ChoiceVO> choices) {
            this.position = position;
            this.question = question;
            this.pageKind = pageKind;
            this.choices = choices;
        }
    }

    private enum PageKind{
        sendFragment,
        buttonFragment,
        pathFragment,
        textFragment
    }

}
