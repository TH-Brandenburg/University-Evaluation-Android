package de.thb.ue.android.custom;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import de.thb.ca.dto.MultipleChoiceQuestionDTO;
import de.thb.ca.dto.util.ChoiceDTO;
import de.thb.ca.dto.util.TextQuestionDTO;
import de.thb.ue.android.fragments.ButtonFragment;
import de.thb.ue.android.fragments.SendFragment;
import de.thb.ue.android.fragments.TextFragment;
import de.thb.ue.android.interfaces.PagerAdapterPageEvent;
import de.thb.ue.android.utility.DataHolder;
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
        if(DataHolder.getQuestionsDTO().getTextQuestionsFirst()){
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
        //create as many pages as there are questions + 1 for the send button
        return DataHolder.getMCQuestionTexts().size() + DataHolder.getQuestionTexts().size() + 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Resources res = mContext.getResources();

        if((position + 1) < getCount()){
           return res.getString(R.string.tab_view_button_question) + " " + (position + 1) + "/" + (getCount() - 1);
        } else {
           return res.getString(R.string.tab_view_button_send);
        }
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object){
        super.setPrimaryItem(container, position, object);
//        mCurrentFragment = (Fragment) object;
        if(object instanceof PagerAdapterPageEvent){
            ((PagerAdapterPageEvent) object).onGettingPrimary();
        }

        if(mOldPosition != position && getFragmentAtPosition(mOldPosition) != null && getFragmentAtPosition(mOldPosition) instanceof PagerAdapterPageEvent){
            ((PagerAdapterPageEvent) getFragmentAtPosition(mOldPosition)).onLeavingPrimary();
        }
        mOldPosition = position;
    }

    public Fragment getFragmentAtPosition(int position){
        return mPageReferenceMap.get(position);
    }

    private Fragment placeTextQuestionsFirst(int i){
        Fragment fragment;
        List<MultipleChoiceQuestionDTO> multipleChoiceQuestions = DataHolder.getMCQuestionTexts();
        List<TextQuestionDTO> textQuestions = DataHolder.getQuestionTexts();

        if(i < textQuestions.size()){
            TextQuestionDTO dto = textQuestions.get(i);
            fragment = TextFragment.newInstance(dto.getQuestionText(), i, dto.getQuestionID());
//            mFragmentsEditable.put(i, true);
            mPageReferenceMap.put(i, fragment);
//            mFragmentsEditable.put(i, false);
        } else if(i < (multipleChoiceQuestions.size() + textQuestions.size())){
            String question = multipleChoiceQuestions.get(i - textQuestions.size()).getQuestion();
            ArrayList<ChoiceDTO> choices = (ArrayList<ChoiceDTO>) multipleChoiceQuestions.get(i - textQuestions.size()).getChoices();
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
        List<MultipleChoiceQuestionDTO> multipleChoiceQuestions = DataHolder.getMCQuestionTexts();
        List<TextQuestionDTO> textQuestions = DataHolder.getQuestionTexts();

        if(i < multipleChoiceQuestions.size()){
            String question = multipleChoiceQuestions.get(i).getQuestion();
            ArrayList<ChoiceDTO> choices = (ArrayList<ChoiceDTO>) multipleChoiceQuestions.get(i).getChoices();
            fragment = ButtonFragment.newInstance(question, choices, i);
            mPageReferenceMap.put(i, fragment);
//            mFragmentsEditable.put(i, false);
        } else if(i < (multipleChoiceQuestions.size() + textQuestions.size())){
            TextQuestionDTO dto = textQuestions.get(i - multipleChoiceQuestions.size());
            fragment = TextFragment.newInstance(dto.getQuestionText(), i, dto.getQuestionID());
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