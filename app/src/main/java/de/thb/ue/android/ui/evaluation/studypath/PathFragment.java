package de.thb.ue.android.ui.evaluation.studypath;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.thb.ue.android.ui.base.BaseFragment;
import de.thb.ue.android.ui.evaluation.EvaluationMvpView;
import thb.de.ue.android.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PathFragment extends BaseFragment implements PathMvpView {
    private static final String POSITION = "POSITION";

    @BindView(R.id.study_path_list_view)
    ListView mPathListView;

    @Inject
    PathPresenter mPresenter;

    @Inject
    ArrayAdapter<String> mArrayAdapter;

    Unbinder mViewUnbinder;

    public PathFragment() {
        // Required empty public constructor
    }

    public static PathFragment newInstance(int position) {
        PathFragment fragment = new PathFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        super.mFragmentComponent.bind(this);
        mPresenter.attachView(this);

        if (this.getArguments() != null) {
            Bundle args = this.getArguments();
            mPresenter.setmPosition(args.getInt(POSITION));
        }

        mArrayAdapter.addAll(mPresenter.getStudyPaths());

        mPathListView.setAdapter(mArrayAdapter);
        mPathListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mPathListView.setOnItemClickListener((parent, view1, position, id) -> {
            TextView textView = (TextView) view1;
            mPathListView.setItemChecked(mPathListView.getPositionForView(textView), true);
            mPresenter.setStudyPath(textView.getText().toString());
            ((EvaluationMvpView) getActivity()).nextPage();
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_studypath, container, false);
        mViewUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewUnbinder.unbind();
        mPresenter.detachView();
    }
}
