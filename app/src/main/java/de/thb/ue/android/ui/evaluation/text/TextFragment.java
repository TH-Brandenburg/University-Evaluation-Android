package de.thb.ue.android.ui.evaluation.text;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.thb.ue.android.injection.ActivityContext;
import de.thb.ue.android.ui.base.BaseFragment;
import de.thb.ue.android.ui.evaluation.send.SendFragment;
import thb.de.ue.android.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TextFragment extends BaseFragment implements TextMvpView {
    private static final String POSITION = "POSITION";
    private static final String QUESTION = "QUESTION";


    @Inject
    TextPresenter mPresenter;

    Unbinder mViewUnbinder;

    public TextFragment() {
        // Required empty public constructor
    }

    public static TextFragment newInstance(int position, String question) {
        TextFragment fragment = new TextFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        args.putString(QUESTION, question);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentComponent.bind(this);
        mPresenter.attachView(this);

        if (this.getArguments() != null) {
            Bundle args = this.getArguments();
            mPresenter.setmPosition(args.getInt(POSITION));
            mPresenter.setmQuestion(args.getString(QUESTION));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_text_view, container, false);
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
