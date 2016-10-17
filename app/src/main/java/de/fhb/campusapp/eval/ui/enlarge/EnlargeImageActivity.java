package de.fhb.campusapp.eval.ui.enlarge;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.fhb.campusapp.eval.ui.base.BaseActivity;
import de.fhb.campusapp.eval.utility.DataHolder;
import de.fhb.campusapp.eval.utility.Utility;
import fhb.de.campusappevaluationexp.R;

public class EnlargeImageActivity extends BaseActivity implements EnlargeMvpView {

    public static final String IMAGE_FILE_PATH = "IMAGE_FILE_PATH";

    @Inject
    EnlargePresenter mEnlargePresenter;

    @BindView(R.id.enlarged_image_view)
    ImageView mImageView;

    @BindView(R.id.my_awesome_toolbar_2)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.mActicityComponent.bind(this);
        setContentView(R.layout.activity_enlarge_image);
        ButterKnife.bind(this);

        mEnlargePresenter.attachView(this);

        String nullTester = null;
        final String imageFilePath;

        //just in case it became null thanks to android
        DataHolder.setPreferences(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            nullTester = extras.getString(IMAGE_FILE_PATH);
        }

        imageFilePath = nullTester;

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mImageView.setVisibility(View.VISIBLE);

        // call this stuff after UI was built so getWidth and getHeight return correct values
        mImageView.post(new Runnable() {
            @Override
            public void run() {
                Picasso.with(EnlargeImageActivity.this)
                        .load(new File(imageFilePath))
                        .fit()
                        .into(mImageView);
                Utility.animateView(mImageView, View.VISIBLE, 1.0f, 100);
            }
        });

        // petty hack needed for support actionBars which do not have a menu.
        // bug of android.
        mToolbar.setNavigationOnClickListener(view -> {
            mImageView.setImageBitmap(null);
            onBackPressed();
        });
    }

    @Override
    protected void onResume() {
        // DataHolder gets ability to freely serialize/deserialize its variables
        // Android might clear variable in DataHolder while App is in background leading to shit.
        DataHolder.setPreferences(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEnlargePresenter.detachView();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.action_bar_enlarge_image, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
