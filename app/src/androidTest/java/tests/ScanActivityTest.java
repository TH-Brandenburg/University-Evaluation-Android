package tests;

import android.Manifest;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.RootMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import de.fhb.campusapp.eval.ui.scan.ScanActivity;
import de.fhb.campusapp.eval.data.DataManager;
import fhb.de.campusappevaluationexp.R;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import tests.testutil.JsonStorage;
import tests.testutil.PermissionGranter;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Created by Sebastian Müller on 23.10.2016.
 *
 * Note that the presence of the progressbar is not explicitly tested as this would afford changes
 * in the production code. (does not count as visible as it cannot be displayed due to its continuous
 * animation which whom espresso is unable to deal with)
 * Its presence can still be recognised: The screen becomes darker because of the overlay.
 *
 * It is rather tested implicitly -> the progressbar signals espresso that the app is not idle
 * thus stopping the framework from continuing testing through network calls.
 * If a test fails check first if the progress overlay is properly displayed as its absence causes timing bug.
 *
 * (Has the added benefit of not needing Thread.sleep calls or implementing IdleResource and altering the production code)
 *
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
@Ignore
public class ScanActivityTest {

    @Rule
    public final IntentsTestRule<ScanActivity> main = new IntentsTestRule<>(ScanActivity.class);
    MockWebServer server = null;

    @Before
    public void setup() throws IOException {
        server = new MockWebServer ();
        server.start(0);

        HttpUrl baseUrl = server.url("v1/questions");
        DataManager.setHostName("http://" + baseUrl.host() + ":" + baseUrl.port());
    }

      @Test
    public void testDebugButton(){
        //when
        onView(withId(R.id.mock_questionnaire)).perform(click());

        //then
        intended(IntentMatchers.hasComponent("de.fhb.campusapp.eval.ui.eval.EvaluationActivity"));
    }

    @Test
    public void testSendingDataSuccessfully() throws InterruptedException, IOException {
        //given
        MockResponse response = new MockResponse()
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .setBody(JsonStorage.questionsDTOJson)
                .setResponseCode(200);

        server.enqueue(response);

        //when
        clickRequestButton();

        //then
        RecordedRequest request = server.takeRequest();
        assertThat(request.getPath(), is("/v1/questions"));
        assertThat(request.getMethod(), is("POST"));

        intended(IntentMatchers.hasComponent("de.fhb.campusapp.eval.ui.eval.EvaluationActivity"));
    }

    @Test
    @Ignore
    public void testServerTimeout() throws InterruptedException {
        //given
        MockResponse response = new MockResponse()
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .setBody(JsonStorage.questionsDTOJson)
                .setResponseCode(200)
                .throttleBody(1024L, 10, TimeUnit.SECONDS);

        server.enqueue(response);

        //when
        clickRequestButton();

        //then
        testClickingDialog(R.string.unknown_error_title, android.R.id.button3, false);

    }

    @Test
    public void testRequestReturnsBadGateWay() throws InterruptedException {
        //given
        server.enqueue(new MockResponse().setResponseCode(502).setBody("{}"));
        server.enqueue(new MockResponse().setResponseCode(502).setBody("{}"));
        server.enqueue(new MockResponse().setResponseCode(502).setBody("{}"));
        server.enqueue(new MockResponse().setResponseCode(502).setBody("{}"));


        //***********************Accepting*******************************

        //when
        clickRequestButton();

        //then
        testClickingDialog(R.string.error_500_502_title, android.R.id.button1, true);
        onView(withId(android.R.id.button2)).perform(click());

        //***********************Denying*******************************

        //when
        clickRequestButton();

        //then
        testClickingDialog(R.string.error_500_502_title, android.R.id.button2, false);

        //***********************Dismissing*******************************

        //when
        clickRequestButton();

        //then
        testDismissingDialog(R.string.error_500_502_title, false);

    }

    @Test
    public void testRequestReturnsInternalError() throws InterruptedException {
        //given
        server.enqueue(new MockResponse().setResponseCode(500).setBody("{}"));
        server.enqueue(new MockResponse().setResponseCode(500).setBody("{}"));
        server.enqueue(new MockResponse().setResponseCode(500).setBody("{}"));
        server.enqueue(new MockResponse().setResponseCode(500).setBody("{}"));

        //***********************Accepting*******************************

        //when
        clickRequestButton();

        //then
        testClickingDialog(R.string.error_500_502_title, android.R.id.button1, true);
        onView(withId(android.R.id.button2)).perform(click());

        //***********************Denying*******************************

        //when
        clickRequestButton();

        //then
        testClickingDialog(R.string.error_500_502_title, android.R.id.button2, false);

        //***********************Dismissing*******************************

        //when
        clickRequestButton();

        //then
        testDismissingDialog(R.string.error_500_502_title, false);
    }

    @Test
    public void testRequestReturnsUnavailable() throws InterruptedException {
        //given
        server.enqueue(new MockResponse().setResponseCode(503).setBody("{}"));
        server.enqueue(new MockResponse().setResponseCode(503).setBody("{}"));
        server.enqueue(new MockResponse().setResponseCode(503).setBody("{}"));
        server.enqueue(new MockResponse().setResponseCode(503).setBody("{}"));

        //***********************Accepting*******************************

        //when
        clickRequestButton();

        //then
        testClickingDialog(R.string.error_503_title, android.R.id.button1, true);
        onView(withId(android.R.id.button2)).perform(click());

        //***********************Denying*******************************

        //when
        clickRequestButton();

        //then
        testClickingDialog(R.string.error_503_title, android.R.id.button2, false);

        //***********************Dismissing*******************************

        //when
        clickRequestButton();

        //then
        testDismissingDialog(R.string.error_503_title, false);
    }

    @Test
    public void testRequestReturnsForbidden() throws InterruptedException {
        //given
        server.enqueue(new MockResponse().setResponseCode(403).setBody("{}"));
        server.enqueue(new MockResponse().setResponseCode(403).setBody("{}"));
        server.enqueue(new MockResponse().setResponseCode(403).setBody("{}"));
        server.enqueue(new MockResponse().setResponseCode(403).setBody("{}"));

        //***********************Accepting*******************************

        //when
        clickRequestButton();

        //then
        testClickingDialog(R.string.error_404_403_title, android.R.id.button1, true);
        onView(withId(android.R.id.button2)).perform(click());

        //***********************Denying*******************************

        //when
        clickRequestButton();

        //then
        testClickingDialog(R.string.error_404_403_title, android.R.id.button2, false);

        //***********************Dismissing*******************************

        //when
        clickRequestButton();

        //then
        testDismissingDialog(R.string.error_404_403_title, false);
    }

    @Test
    public void testRequestReturnsNotFound() throws InterruptedException {
        //given
        server.enqueue(new MockResponse().setResponseCode(404).setBody("{}"));
        server.enqueue(new MockResponse().setResponseCode(404).setBody("{}"));
        server.enqueue(new MockResponse().setResponseCode(404).setBody("{}"));
        server.enqueue(new MockResponse().setResponseCode(404).setBody("{}"));

        //***********************Accepting*******************************

        //when
        clickRequestButton();

        //then
        testClickingDialog(R.string.error_404_403_title, android.R.id.button1, true);
        onView(withId(android.R.id.button2)).perform(click());

        //***********************Denying*******************************

        //when
        clickRequestButton();

        //then
        testClickingDialog(R.string.error_404_403_title, android.R.id.button2, false);

        //***********************Dismissing*******************************

        //when
        clickRequestButton();

        //then
        testDismissingDialog(R.string.error_404_403_title, false);
    }

    @Test
    public void testRequestReturnsNonCoveredError() throws InterruptedException {
        //given
        server.enqueue(new MockResponse().setResponseCode(405).setBody("{}"));
        server.enqueue(new MockResponse().setResponseCode(405).setBody("{}"));
        server.enqueue(new MockResponse().setResponseCode(405).setBody("{}"));
        server.enqueue(new MockResponse().setResponseCode(405).setBody("{}"));


        //***********************Accepting*******************************

        //when
        clickRequestButton();

        //then
        testClickingDialog(R.string.unknown_error_title, android.R.id.button1, true);
        onView(withId(android.R.id.button2)).perform(click());

        //***********************Denying*******************************

        //when
        clickRequestButton();

        //then
        testClickingDialog(R.string.unknown_error_title, android.R.id.button2, false);

        //***********************Dismissing*******************************

        //when
        clickRequestButton();

        //then
        testDismissingDialog(R.string.unknown_error_title, false);
    }

    @Ignore
    @Test
    public void testNoInternetConnectivity(){

    }

    @Test
    public void testRetrievingErrorTypeMinus1() throws InterruptedException {
        //given
        MockResponse response = new MockResponse()
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .setBody(JsonStorage.responseDTOJsonMinus1)
                .setResponseCode(400);

        server.enqueue(response);
        server.enqueue(response);

        //when
        clickRequestButton();

        //then
        testClickingDialog(R.string.unknown_error_title, android.R.id.button3, false);

        //***********************Dismissing*******************************

        //when
        clickRequestButton();

        //then
        testDismissingDialog(R.string.unknown_error_title, false);

    }

    @Test
    public void testRetrievingErrorType1() throws InterruptedException {
        //given
        MockResponse response = new MockResponse()
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .setBody(JsonStorage.responseDTOJson1)
                .setResponseCode(400);

        server.enqueue(response);
        server.enqueue(response);

        //when
        clickRequestButton();


        //then
        testClickingDialog(R.string.invalid_token_title, android.R.id.button3, false);

        //***********************Dismissing*******************************

        //when
        clickRequestButton();

        //then
        testDismissingDialog(R.string.invalid_token_title, false);

    }


    @Test
    public void testRetrievingErrorType3() throws InterruptedException {
        //given
        MockResponse response = new MockResponse()
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .setBody(JsonStorage.responseDTOJson3)
                .setResponseCode(400);

        server.enqueue(response);
        server.enqueue(response);

        //when
        clickRequestButton();

        //then
        testClickingDialog(R.string.token_already_used_title, android.R.id.button3, false);

        //***********************Dismissing*******************************

        //when
        clickRequestButton();

        //then
        testDismissingDialog(R.string.token_already_used_title, false);


    }

    @Test
    public void testRetrievingErrorType4() throws InterruptedException {
        //given
        MockResponse response = new MockResponse()
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .setBody(JsonStorage.responseDTOJson4)
                .setResponseCode(400);

        server.enqueue(response);
        server.enqueue(response);

        //when
        clickRequestButton();

        //then
        testClickingDialog(R.string.evaluation_closed_title, android.R.id.button3, false);

        //***********************Dismissing*******************************

        //when
        clickRequestButton();

        //then
        testDismissingDialog(R.string.evaluation_closed_title, false);


    }

    @Test
    public void testRetrievingErrorType5() throws InterruptedException {
        //given
        MockResponse response = new MockResponse()
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .setBody(JsonStorage.responseDTOJson5)
                .setResponseCode(400);

        server.enqueue(response);
        server.enqueue(response);

        //when
        clickRequestButton();

        //then
        testClickingDialog(R.string.unknown_error_title, android.R.id.button3, false);

        //***********************Dismissing*******************************

        //when
        clickRequestButton();

        //then
        testDismissingDialog(R.string.unknown_error_title, false);

    }

    @After
    public void tearDown() throws IOException {
            server.shutdown();
    }

    private void clickRequestButton(){
        PermissionGranter.allowPermissionsIfNeeded(Manifest.permission.INTERNET);
        onView(withId(R.id.mock_qr_code_reading)).perform(click());
    }

    private void testClickingDialog(@StringRes int dialogTitle, @IdRes int buttonToPress, boolean dialogStillExisting) throws InterruptedException {

        onView(withText(dialogTitle))
                .inRoot(RootMatchers.isDialog())
                .check(matches(isDisplayed()));

        onView(withId(buttonToPress))
                .inRoot(RootMatchers.isDialog())
                .perform(click());

        if(dialogStillExisting){
            onView(withText(dialogTitle)).check(matches(isDisplayed()));
        } else {
            onView(withText(dialogTitle)).check(doesNotExist());
            //test if scanning is working
            onView(withClassName(is("com.google.zxing.client.android.ViewfinderView"))).check(matches(isDisplayed()));
            onView(withClassName(is("android.view.SurfaceView"))).check(matches(isDisplayed()));
        }

    }

    private void testDismissingDialog(@StringRes int dialogTitle, boolean dialogStillExisting) throws InterruptedException {

        ViewInteraction interaction = onView(withText(dialogTitle))
                .inRoot(RootMatchers.isDialog())
                .check(matches(isDisplayed()));

        interaction.perform(pressBack());


        if(dialogStillExisting){
            onView(withText(dialogTitle)).check(matches(isDisplayed()));
        } else {
            onView(withText(dialogTitle)).check(doesNotExist());
            //test if scanning is working
            onView(withClassName(is("com.google.zxing.client.android.ViewfinderView"))).check(matches(isDisplayed()));
            onView(withClassName(is("android.view.SurfaceView"))).check(matches(isDisplayed()));
        }
    }


}
