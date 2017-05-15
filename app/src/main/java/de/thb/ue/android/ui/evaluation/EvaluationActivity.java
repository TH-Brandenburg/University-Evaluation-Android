package de.thb.ue.android.ui.evaluation;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.ImageHolder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.thb.ue.android.ui.base.BaseActivity;
import de.thb.ue.android.ui.evaluation.choice.ButtonFragment;
import de.thb.ue.android.ui.evaluation.send.SendFragment;
import de.thb.ue.android.ui.evaluation.studypath.PathFragment;
import de.thb.ue.android.ui.evaluation.text.TextFragment;
import de.thb.ue.android.utility.Utils;
import de.thb.ue.android.utility.customized_classes.BasePagerAdapter;
import thb.de.ue.android.R;

public class EvaluationActivity extends BaseActivity implements EvaluationMvpView {

    @BindView(R.id.the_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.progress_overlay)
    View mProgressOverlay;

    @BindView(R.id.button_pager)
    ViewPager mViewPager;

    @BindView(R.id.nav_drawer)
    FrameLayout mNavDrawerLayout;

    @Inject
    BasePagerAdapter mPagerAdapter;

    Drawer mDrawer;

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

        configureDrawer(savedInstanceState);

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    mDrawer.setSelectionAtPosition(mViewPager.getCurrentItem());
                    manageSoftKeyboard();
                    updateDrawer();
                }
            }
        });
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        if(mPagerAdapter.getPageClassType(mViewPager.getCurrentItem()) == TextFragment.class){
//            IconicsDrawable cameraIcon = new IconicsDrawable(this, FontAwesome.Icon.faw_camera)
//                    .color(Utils.getColorCompat(this, R.color.color_bright_font))
//                    .sizeDp(24)
//                    .paddingDp(4);
//
//
//            getMenuInflater().inflate(R.menu.action_bar_camera, menu);
//            menu.getItem(0).setIcon(cameraIcon);
//        } else {
//            menu.removeItem(R.menu.action_bar_camera);
//        }
//
//        return super.onPrepareOptionsMenu(menu);
//    }

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

    @Override
    public void onBackPressed() {
        previousPage();
    }

    @Override
    public void nextPage() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
    }

    @Override
    public void previousPage() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
    }

    @Override
    public void gotToPage(int position) {
        if(position >= 0){
            mViewPager.setCurrentItem(position);
        }
    }

    private void hideSoftKeyboard() {
        // Hide the keyboard.
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(mViewPager.getWindowToken(), 0);
    }

    private void showSoftKeyboard() {
        // Hide the keyboard.
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .showSoftInputFromInputMethod(mViewPager.getWindowToken(), 0);
    }

    private void manageSoftKeyboard() {
        Class pageTye = mPagerAdapter.getPageClassType(mViewPager.getCurrentItem());
        if (pageTye == ButtonFragment.class || pageTye == SendFragment.class || pageTye == PathFragment.class) {
            hideSoftKeyboard();
        } else {
            showSoftKeyboard();
        }
    }

    /**
     * Adds the entries to the navigation drawer.
     * Creates an entry for every question, the study path choosingf page and the send page.
     */
    private void configureDrawer(Bundle savedInstanceState){
        List<String> questions = mEvaluationPresenter.getQuestionTexts();
        int count = 0;

        mDrawer = new DrawerBuilder(this)
                .withRootView(mNavDrawerLayout)
                .withToolbar(mToolbar)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withCloseOnClick(true)
                .withSavedInstance(savedInstanceState)
                .withTranslucentStatusBar(false)
                .withDisplayBelowStatusBar(true)
                .build();



        PrimaryDrawerItem pathItem = new PrimaryDrawerItem().withName("Studiengangsauswahl").withIdentifier(0);
        mDrawer.addItem(pathItem);

        for(int i = 0; i < questions.size(); i++){
            PrimaryDrawerItem questionItem = new PrimaryDrawerItem().withBadge(Integer.toString(i+1)).withName(questions.get(i)).withIdentifier(i+1);
            mDrawer.addItem(questionItem);
            count = i+1;
        }

        PrimaryDrawerItem sendItem = new PrimaryDrawerItem().withName("Senden").withIdentifier(count+1);
        mDrawer.addItem(sendItem);

        mDrawer.setOnDrawerItemClickListener((view, position, drawerItem) -> {
            gotToPage(position);
            mDrawer.closeDrawer();
            return true;
        });
    }

    private void updateDrawer() {
        Set<String> answers =  mEvaluationPresenter.getAnsweredQuestions();
        IconicsDrawable icon = new IconicsDrawable(EvaluationActivity.this)
                .icon(FontAwesome.Icon.faw_check)
                .color(Utils.getColorCompat(EvaluationActivity.this, R.color.color_positive_green))
                .sizeDp(24)
                .paddingDp(4);

        for(int i = 0; i < mDrawer.getDrawerItems().size(); i++){
            PrimaryDrawerItem item = (PrimaryDrawerItem) mDrawer.getDrawerItems().get(i);

            if(answers.contains(item.getName().getText())){
                mDrawer.updateIcon(item.getIdentifier(), new ImageHolder(icon));
            }

        }

        if(!mEvaluationPresenter.getStudyPath().isEmpty()){
            mDrawer.updateIcon(0, new ImageHolder(icon));
        }
    }
}
