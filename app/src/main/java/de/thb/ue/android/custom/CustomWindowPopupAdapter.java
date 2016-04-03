package de.thb.ue.android.custom;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import de.thb.ca.dto.QuestionsDTO;
import de.thb.ue.android.interfaces.ProgressCommunicator;
import de.thb.ue.android.utility.DataHolder;
import fhb.de.campusappevaluationexp.R;

/**
 * Created by Sebastian Mueller on 23.05.2015.
 */
public class CustomWindowPopupAdapter extends ArrayAdapter<String> {

    private ProgressCommunicator progressCommunicator;

    /**
     * QuestionTexts in the form of:
     * 1. question balala
     * 2. bknaalsd
     * 3. alsasd
     * etc.
     */
    private List<String> enumeratedQuestions;
    private List<String> plainQuestions;
//    private ArrayList questions;
//    private View current = null;

    public CustomWindowPopupAdapter(Context context, int resource, List<String> enumeratedQuestions) {
        super(context, resource, enumeratedQuestions);
        this.enumeratedQuestions = enumeratedQuestions;
        this.plainQuestions = DataHolder.retrieveAllQuestionTexts();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QuestionsDTO dto = DataHolder.getQuestionsDTO();
        ListView listView = (ListView) parent;
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setDrawSelectorOnTop(true);

        ViewHolder holder;

        if(convertView == null){
            /* There is no view at this position, we create a new one.
            In this case by inflating an xml layout */
            convertView = View.inflate(getContext(), R.layout.array_adapter, null);
            TextView textView = (TextView) convertView.findViewById(R.id.array_adapter_text_view);

            textView.setText(this.enumeratedQuestions.get(position));
            holder = new ViewHolder();
            holder.textView = textView;

            convertView.setTag(holder);
        } else {
            /* We recycle a View that already exists */
            holder = (ViewHolder) convertView.getTag();
            holder.textView.setText(this.enumeratedQuestions.get(position));

        }
        //mark the question currently displayed
        if(position == progressCommunicator.getProgress()){
            holder.textView.setBackgroundResource(R.color.campusapptheme_color_negative_red);
        } else { // or make it white again if it is no longer
            holder.textView.setBackgroundResource(R.color.campusapptheme_color_transparent);
        }

        if(DataHolder.isRecolorNavigationList()){
            if(position < plainQuestions.size()){
                boolean answered = DataHolder.isQuestionAnswered(plainQuestions.get(position));
                if(!answered && position != progressCommunicator.getProgress()){ //mark unanswered questions
                    holder.textView.setBackgroundResource(R.color.campusapptheme_color_not_answered_yet);
                } else if(position != progressCommunicator.getProgress()){ //let answered questions appear white again
                    holder.textView.setBackgroundResource(R.color.campusapptheme_color_transparent);
                } else { // mark the question currently displayed -> ignore if it is answered or not
                    holder.textView.setBackgroundResource(R.color.campusapptheme_color_negative_red);
                }
            }
        }
        return convertView;
    }

    public void setProgressCommunicator(ProgressCommunicator progressCommunicator) {
        this.progressCommunicator = progressCommunicator;
    }

    //I really dont know why this is needed but it works so who cares?
    //After 12+ hours i dotn want to cope with that.
    //Feel free to play around though. Maybe you can tell me. And maybe I am even interested.
    private static class ViewHolder{
        public TextView textView;
    }
}
