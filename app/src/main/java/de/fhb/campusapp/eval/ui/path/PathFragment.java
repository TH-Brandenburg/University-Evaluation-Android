package de.fhb.campusapp.eval.ui.path;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.fhb.campusapp.eval.interfaces.PagerAdapterSetPrimary;
import de.fhb.campusapp.eval.ui.base.BaseActivity;
import de.fhb.campusapp.eval.ui.base.BaseFragment;
import de.fhb.campusapp.eval.ui.sendfragment.SendFragment;
import de.fhb.campusapp.eval.utility.DataHolder;
import de.fhb.campusapp.eval.utility.EventBus;
import de.fhb.campusapp.eval.utility.Events.ClickedChoiceButtonEvent;
import fhb.de.campusappevaluationexp.R;

public class PathFragment extends BaseFragment implements ListView.OnItemClickListener, PathMvpView {
    private static final String POSITION = "POSITION";


    @BindView(R.id.inner_section_list_view)
    ListView mListView;

    @Inject
    PathPresenter mPathPresenter;

    private int mPosition;


    public static PathFragment newInstance(int position) {
        PathFragment fragment = new PathFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((BaseActivity)getActivity()).mActicityComponent.bind(this);
        mPathPresenter.attachView(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (this.getArguments() != null) {
            Bundle args = this.getArguments();
            mPosition = args.getInt(POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inner_section, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inflate the layout for this fragment
        ArrayList<String> intermediateList = new ArrayList<>(Arrays.asList("Flappy", "Bird"));
        ArrayList<String> realList = (ArrayList<String>) DataHolder.getQuestionsVO().getStudyPaths();
        mListView.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.nav_list, realList == null ? intermediateList : realList));
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setOnItemClickListener(this);
    }


    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onDestroy() {
        mPathPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView textView = (TextView) view;
        mListView.setItemChecked(mListView.getPositionForView(view), true);
        DataHolder.getAnswersVO().setStudyPath(textView.getText().toString());
        EventBus.get().post(new ClickedChoiceButtonEvent());

        //notify navigationList that a new answer was given
        ((PagerAdapterSetPrimary) getActivity()).setPrimaryFragment(mPosition + 1);
    }
}