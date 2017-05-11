package de.thb.ue.android.ui.evaluation;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.thb.ue.android.ui.base.BaseActivity;
import de.thb.ue.android.utility.customized_classes.BasePagerAdapter;
import thb.de.ue.android.R;

public class EvaluationActivity extends BaseActivity implements EvaluationMvpView {

    @BindView(R.id.the_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.progress_overlay)
    View mProgressOverlay;

    @BindView(R.id.button_pager)
    ViewPager mViewPager;

    @Inject
    BasePagerAdapter mPagerAdapter;


    @Inject
    EvaluationPresenter mEvaluationPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);
        ButterKnife.bind(this);
        super.mActicityComponent.bind(this);
        super.fixOrientationToPortrait();
        mEvaluationPresenter.attachView(this);

        setSupportActionBar(mToolbar);
        setSubtitle(R.string.action_bar_title);


        mViewPager.setAdapter(mPagerAdapter);
    }

    @Override
    public void hideProgressOverlay() {
        mProgressOverlay.animate()
                .alpha(0)
                .setDuration(200);
    }

    @Override
    public void showProgressOverlay() {
        mProgressOverlay.animate()
                .alpha(1)
                .setDuration(200);
    }

    @Override
    public void setSubtitle(@StringRes int subtitle) {
        if(getSupportActionBar() != null){
            getSupportActionBar().setSubtitle(subtitle);
        }
    }
}
