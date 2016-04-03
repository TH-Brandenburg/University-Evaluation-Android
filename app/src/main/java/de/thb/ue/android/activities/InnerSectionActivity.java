package de.thb.ue.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import de.thb.ue.android.utility.DataHolder;
import fhb.de.campusappevaluationexp.R;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_inner_section)
public class InnerSectionActivity extends RoboActionBarActivity implements ListView.OnItemClickListener{

    @InjectView(R.id.inner_section_list_view)
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        //just in case it became null thanks to android
        DataHolder.setPreferences(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        ArrayList<String> intermediateList = new ArrayList<>(Arrays.asList("Flappy", "Bird"));
        ArrayList<String> realList = (ArrayList<String>) DataHolder.getQuestionsDTO().getStudyPaths();
        mListView.setAdapter(new ArrayAdapter<>(this, R.layout.array_adapter, realList == null ? intermediateList : realList));
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        // DataHolder gets ability to freely serialize/deserialize its variables
        // Android might clear variable in DataHolder while App is in background leading to shit.
        DataHolder.setPreferences(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView textView = (TextView) view;
        mListView.setItemChecked(mListView.getPositionForView(view), true);
        DataHolder.getAnswersDTO().setStudyPath(textView.getText().toString());
        Intent intent = new Intent(this, EvaluationActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

    }
}
