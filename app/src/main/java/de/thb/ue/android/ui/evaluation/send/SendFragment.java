package de.thb.ue.android.ui.evaluation.send;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.thb.ue.android.ui.base.BaseFragment;
import thb.de.ue.android.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SendFragment extends BaseFragment implements SendMvpView {
    private static final String POSITION = "POSITION";


    @BindView(R.id.send_button)
    Button mSendButton;

    @Inject
    SendPresenter mPresenter;

    Unbinder mViewUnbinder;


    public SendFragment() {
        // Required empty public constructor
    }

    public static SendFragment newInstance(int position) {
        SendFragment fragment = new SendFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_send, container, false);
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
