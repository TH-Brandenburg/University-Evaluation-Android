package de.fhb.campusapp.eval.activities;

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

import de.fhb.campusapp.eval.utility.DataHolder;
import de.fhb.campusapp.eval.utility.Utility;
import fhb.de.campusappevaluationexp.R;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_enlarge_image)
public class EnlargeImageActivity extends RoboActionBarActivity {

    public static final String IMAGE_FILE_PATH = "IMAGE_FILE_PATH";

    @InjectView(R.id.enlarged_image_view)
    ImageView mImageView;

    @InjectView(R.id.my_awesome_toolbar_2)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
//                        .centerCrop()
                        .into(mImageView);
                Utility.animateView(mImageView, View.VISIBLE, 1.0f, 100);
            }
        });

        // petty hack needed for support actionBars which do not have a menu.
        // bug of android.
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageView.setImageBitmap(null);
                onBackPressed();
            }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar_enlarge_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
