package de.fhb.campusapp.eval.custom;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhb.campusapp.eval.fragments.ButtonFragment;
import de.fhb.campusapp.eval.fragments.InnerSectionFragment;
import de.fhb.campusapp.eval.fragments.SendFragment;
import de.fhb.campusapp.eval.fragments.TextFragment;
import de.fhb.campusapp.eval.interfaces.PagerAdapterPageEvent;
import de.fhb.campusapp.eval.utility.DataHolder;
import de.fhb.campusapp.eval.utility.vos.ChoiceVO;
import de.fhb.campusapp.eval.utility.vos.MultipleChoiceQuestionVO;
import de.fhb.campusapp.eval.utility.vos.TextQuestionVO;
import fhb.de.campusappevaluationexp.R;


/**
 * Created by Sebastian Mueller
 */



// Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
public class CustomFragmentStatePagerAdapter extends FragmentStatePagerAdapter{

    private HashMap<Integer, Fragment> mPageReferenceMap = new HashMap<>(10);
    /*
    * saves position of fragment in ViewPager which came before the currently selected Fragment.
    * MUST be initialised with -1
    * */
    private int mOldPosition = -1;
    private Context mContext;

    public CustomFragmentStatePagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;

    }

    @Override
    public Fragment getItem(int i) {
        if(DataHolder.getQuestionsVO().getTextQuestionsFirst()){
            return placeTextQuestionsFirst(i);
        } else {
            return placeMCQuestionsFirst(i);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        mPageReferenceMap.remove(position);
    }

    @Override
    public int getCount() {
        //create as many pages as there are questions + 1 for the send button and +1 for choosing subject
        if(DataHolder.getMCQuestionTexts() != null || DataHolder.getQuestionTexts() != null){
            return DataHolder.getMCQuestionTexts().size() + DataHolder.getQuestionTexts().size() + 2;
        } else {
            return 1;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Resources res = mContext.getResources();

        if(position == 0) {
            return res.getString(R.string.tab_view_button_choose);
        } else if((position + 1) < getCount()){
           return res.getString(R.string.tab_view_button_question) + " " + (position) + "/" + (getCount() - 1);
        } else {
           return res.getString(R.string.tab_view_button_send);
        }
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object){
        super.setPrimaryItem(container, position, object);
//        mCurrentFragment = (Fragment) object;
        if(object instanceof PagerAdapterPageEvent){
            ((PagerAdapterPageEvent) object).onGettingPrimary(mOldPosition);
        }

        if(mOldPosition != position && getFragmentAtPosition(mOldPosition) != null && getFragmentAtPosition(mOldPosition) instanceof PagerAdapterPageEvent){
            ((PagerAdapterPageEvent) getFragmentAtPosition(mOldPosition)).onLeavingPrimary(position);
        }
        mOldPosition = position;
    }

    public Fragment getFragmentAtPosition(int position){
        return mPageReferenceMap.get(position);
    }

    private Fragment placeTextQuestionsFirst(int i){
        Fragment fragment;
        List<MultipleChoiceQuestionVO> multipleChoiceQuestions = DataHolder.getMCQuestionTexts();
        List<TextQuestionVO> textQuestions = DataHolder.getQuestionTexts();
        // this +1/-1 stuff all hails from the inclusion of the innerSectionFragment
        if (i == 0){
            fragment = InnerSectionFragment.newInstance(i);
            mPageReferenceMap.put(i, fragment);
        } else if(i < textQuestions.size() + DataHolder.getPositionOffset()){
            TextQuestionVO dto = textQuestions.get(i - DataHolder.getPositionOffset());
            fragment = TextFragment.newInstance(dto.getQuestionText(), i, dto.getQuestionID());
//            mFragmentsEditable.put(i, true);
            mPageReferenceMap.put(i, fragment);
//            mFragmentsEditable.put(i, false);
        } else if(i < (multipleChoiceQuestions.size() + textQuestions.size()) + DataHolder.getPositionOffset()){
            String question = multipleChoiceQuestions.get((i - DataHolder.getPositionOffset()) - textQuestions.size()).getQuestion();
            ArrayList<ChoiceVO> choices = (ArrayList<ChoiceVO>) multipleChoiceQuestions.get((i - DataHolder.getPositionOffset()) - textQuestions.size()).getChoices();
            fragment = ButtonFragment.newInstance(question, choices, i);
            mPageReferenceMap.put(i, fragment);
        } else {
            fragment = SendFragment.newInstance(i);
//            mFragmentsEditable.put(i, false);
            mPageReferenceMap.put(i, fragment);
        }
        return fragment;
    }

    private Fragment placeMCQuestionsFirst(int i){
        Fragment fragment;
        List<MultipleChoiceQuestionVO> multipleChoiceQuestions = DataHolder.getMCQuestionTexts();
        List<TextQuestionVO> textQuestions = DataHolder.getQuestionTexts();
        // this +1/-1stuff all hails from the inclusion of the innerSectionFragment
        if (i == 0) {
            fragment = InnerSectionFragment.newInstance(i);
            mPageReferenceMap.put(i, fragment);
        } else if(i < multipleChoiceQuestions.size() + DataHolder.getPositionOffset()){
            String question = multipleChoiceQuestions.get(i - DataHolder.getPositionOffset()).getQuestion();
            ArrayList<ChoiceVO> choices = (ArrayList<ChoiceVO>) multipleChoiceQuestions.get(i - DataHolder.getPositionOffset()).getChoices();
            fragment = ButtonFragment.newInstance(question, choices, i);
            mPageReferenceMap.put(i, fragment);
//            mFragmentsEditable.put(i, false);
        } else if(i < (multipleChoiceQuestions.size() + textQuestions.size()) + DataHolder.getPositionOffset()){
            TextQuestionVO vo = textQuestions.get((i - DataHolder.getPositionOffset()) - multipleChoiceQuestions.size());
            fragment = TextFragment.newInstance(vo.getQuestionText(), i, vo.getQuestionID());
//            mFragmentsEditable.put(i, true);
            mPageReferenceMap.put(i, fragment);
        } else {
            fragment = SendFragment.newInstance(i);
//            mFragmentsEditable.put(i, false);
            mPageReferenceMap.put(i, fragment);
        }
        return fragment;
    }


}