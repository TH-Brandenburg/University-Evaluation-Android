package de.fhb.campusapp.eval.custom;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.fhb.campusapp.eval.data.IDataManager;
import de.fhb.campusapp.eval.injection.ActivityContext;
import de.fhb.campusapp.eval.interfaces.ProgressCommunicator;
import de.fhb.campusapp.eval.data.DataManager;
import de.fhb.campusapp.eval.utility.FeatureSwitch;
import de.fhb.campusapp.eval.utility.vos.QuestionsVO;
import fhb.de.campusappevaluationexp.R;

/**
 * Created by Sebastian Mueller on 23.05.2015.
 */
public class CustomWindowPopupAdapter extends ArrayAdapter<String> {

    private final IDataManager mDataManager;

    /**
     * All items that must be placed in the navigation menu.
     */
    private  List<String> navListEntries ;

    @Inject
    public CustomWindowPopupAdapter(@ActivityContext Context context, IDataManager dataManager, int resource) {
        super(context, resource);
        this.mDataManager = dataManager;
        this.navListEntries = this.constructNavList();
        super.addAll(navListEntries);
    }

    /**
     * Prepares the appeareance of the navigation list.
     * Returns a list containing all question texts of all question types.
     * @return
     */
    private List<String> constructNavList() {
        ArrayList<String> navList = new ArrayList<>(23);

        // choosing the subject comes always first
        navList.add(getContext().getResources().getString(R.string.tab_view_button_choose));
        navList.addAll(mDataManager.retrieveAllQuestionTexts());
        navList.add(getContext().getResources().getString(R.string.send_button));
        return navList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QuestionsVO dto = mDataManager.getmQuestionsVO();
        ListView listView = (ListView) parent;
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setDrawSelectorOnTop(true);

        ViewHolder holder;

        if(convertView == null){
            /* There is no view at this position, we create a new one.
            In this case by inflating an xml layout */
            convertView = View.inflate(getContext(), R.layout.nav_list, null);
            TextView textView = (TextView) convertView.findViewById(R.id.nav_list_text_view);

            if(position != 0 && position != this.navListEntries.size() - 1){
                textView.setText(position + ". " + this.navListEntries.get(position));
            } else {
                textView.setText(this.navListEntries.get(position));
            }
            holder = new ViewHolder();
            holder.textView = textView;

            convertView.setTag(holder);
        } else {
            /* We recycle a View that already exists */
            holder = (ViewHolder) convertView.getTag();
            if(position != 0 && position != this.navListEntries.size() - 1){
                holder.textView.setText(position + ". " + this.navListEntries.get(position));
            } else {
                holder.textView.setText(this.navListEntries.get(position));
            }

        }
        //mark the question currently displayed
        if(FeatureSwitch.NAVIGATION_MARK_SELECTED){
            if(position == mDataManager.getmCurrentPagerPosition()){
                holder.textView.setBackgroundResource(R.color.campusapptheme_color_negative_red);
            } else { // or make it white again if it is no longer
                holder.textView.setBackgroundResource(R.color.campusapptheme_color_transparent);
            }
        }

        if(mDataManager.ismRecolorNavigation() || FeatureSwitch.NAVIGATION_ALWAYS_MARK_UNASWERED){
            recolorNavigationList(position, holder);
        }
        return convertView;
    }

    /*
    Recolors the navigation list. DUH
    Colors all unanswered questions.
    Color can be changed in appropriate xml source file.
     */
    private void recolorNavigationList(int position, ViewHolder holder) {
        //the +1s (plural) because of the PathFragment at the beginning
        if(position < navListEntries.size()){
                boolean answered = mDataManager.isQuestionAnswered(navListEntries.get(position));

                if(position != 0
                        && position != navListEntries.size() - 1
                        && position != mDataManager.getmCurrentPagerPosition()
                        && !answered ){ //mark unanswered questions

                    holder.textView.setBackgroundResource(R.color.campusapptheme_color_not_answered_yet);
                } else if(position != mDataManager.getmCurrentPagerPosition()){ //let answered questions appear white again
                    holder.textView.setBackgroundResource(R.color.campusapptheme_color_transparent);
                } else if(FeatureSwitch.NAVIGATION_MARK_SELECTED){ // mark the question currently displayed -> ignore if it is answered or not
                    holder.textView.setBackgroundResource(R.color.campusapptheme_color_negative_red);
                }

        }
    }

    //I really dont know why this is needed but it works so who cares?
    //After 12+ hours i don´t want to cope with that.
    //Feel free to play around though. Maybe you can tell me. And maybe I am even interested.
    private static class ViewHolder{
        public TextView textView;
    }
}
