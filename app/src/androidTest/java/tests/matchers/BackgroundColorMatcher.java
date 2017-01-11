package tests.matchers;

import android.support.annotation.ColorRes;
import android.support.test.espresso.core.deps.guava.io.BaseEncoding;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Created by Sebastian Müller on 25.10.2016.
 */
public class BackgroundColorMatcher {

    public static Matcher<View> withBackgroundColor(@ColorRes final int color){
        return new BoundedMatcher<View, TextView>(TextView.class) {

            int givenColorId;
            int viewColorId;

            @Override
            public void describeTo(Description description) {
                description.appendText("Given color: ");
                description.appendValue(givenColorId);
                description.appendText(" does not match color of textView: " + viewColorId);
            }

            @Override
            protected boolean matchesSafely(TextView view) {
                viewColorId = view.getDrawingCacheBackgroundColor();
                givenColorId = view.getResources().getColor(color);
                return view.getDrawingCacheBackgroundColor() == view.getResources().getColor(color);
            }
        };
    }
}
