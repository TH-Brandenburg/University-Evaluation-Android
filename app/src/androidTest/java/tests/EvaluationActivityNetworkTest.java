package tests;

import android.Manifest;
import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.test.espresso.Root;
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
import org.junit.rules.MethodRule;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import de.fhb.campusapp.eval.ui.eval.EvaluationActivity;
import de.fhb.campusapp.eval.utility.DataHolder;
import de.fhb.campusapp.eval.utility.DebugConfigurator;
import de.fhb.campusapp.eval.utility.vos.QuestionsVO;
import fhb.de.campusappevaluationexp.R;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import tests.testutil.JsonStorage;
import tests.testutil.PermissionGranter;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

/**
 * Created by Sebastian Müller on 27.10.2016.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class EvaluationActivityNetworkTest {

    private class CustomIntentsTestRule<T extends Activity> extends IntentsTestRule<T>{

        public CustomIntentsTestRule(Class<T> activityClass) {
            super(activityClass);
        }

        @Override
        protected void beforeActivityLaunched() {
            //prepare app
            DataHolder.deleteAllData();
            DataHolder.setQuestionsVO(new QuestionsVO(
                    DebugConfigurator.getDemoStudyPaths(),
                    DebugConfigurator.getDemoTextQuestions(),
                    DebugConfigurator.getDemoMultipleChoiceQuestionDTOs(),
                    false
            ));
            super.beforeActivityLaunched();
        }
    }

    @Rule
    public CustomIntentsTestRule<EvaluationActivity> main = new CustomIntentsTestRule<>(EvaluationActivity.class);;
    MockWebServer server = null;

    @Before
    public void setup() throws IOException, InterruptedException {

        //prepare server
        server = new MockWebServer ();
        server.start(0);

        HttpUrl baseUrl = server.url("v1/answers");
        DataHolder.setHostName("http://" + baseUrl.host() + ":" + baseUrl.port());

        onData(allOf(is(instanceOf(String.class)), is("Informatik"))).perform(click());
        navigateToEnd();
    }

    @Test
    public void testSendingDataSuccessfully() throws InterruptedException, IOException {
        //given
        MockResponse response = new MockResponse()
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .setBody(JsonStorage.responseDTOJson2)
                .setResponseCode(200);

        server.enqueue(response);

        //when
        clickSendAndDialogButton();

        //then
        RecordedRequest request = server.takeRequest();
        assertThat(request.getPath(), is("/v1/answers"));
        assertThat(request.getMethod(), is("POST"));

        onView(withText(R.string.answers_transmission_success_title))
                .inRoot(RootMatchers.isDialog())
                .check(matches(isDisplayed()));
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
        clickSendAndDialogButton();

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
        clickSendAndDialogButton();

        //then
        testClickingDialog(R.string.error_500_502_title, android.R.id.button1, true);
        onView(withId(android.R.id.button2)).perform(click());

        //***********************Denying*******************************

        //when
        clickSendAndDialogButton();

        //then
        testClickingDialog(R.string.error_500_502_title, android.R.id.button2, false);

        //***********************Dismissing*******************************

        //when
        clickSendAndDialogButton();

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
        clickSendAndDialogButton();

        //then
        testClickingDialog(R.string.error_500_502_title, android.R.id.button1, true);
        onView(withId(android.R.id.button2)).perform(click());

        //***********************Denying*******************************

        //when
        clickSendAndDialogButton();

        //then
        testClickingDialog(R.string.error_500_502_title, android.R.id.button2, false);

        //***********************Dismissing*******************************

        //when
        clickSendAndDialogButton();

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
        clickSendAndDialogButton();

        //then
        testClickingDialog(R.string.error_503_title, android.R.id.button1, true);
        onView(withId(android.R.id.button2)).perform(click());

        //***********************Denying*******************************

        //when
        clickSendAndDialogButton();

        //then
        testClickingDialog(R.string.error_503_title, android.R.id.button2, false);

        //***********************Dismissing*******************************

        //when
        clickSendAndDialogButton();

        //then
        testDismissingDialog(R.string.error_503_title, false);
    }

    @Test
    public void  testRequestReturnsForbidden() throws InterruptedException {
        //given
        server.enqueue(new MockResponse().setResponseCode(403).setBody("{}"));
        server.enqueue(new MockResponse().setResponseCode(403).setBody("{}"));
        server.enqueue(new MockResponse().setResponseCode(403).setBody("{}"));
        server.enqueue(new MockResponse().setResponseCode(403).setBody("{}"));

        //***********************Accepting*******************************

        //when
        clickSendAndDialogButton();

        //then
        testClickingDialog(R.string.error_404_403_title, android.R.id.button1, true);
        onView(withId(android.R.id.button2)).perform(click());

        //***********************Denying*******************************

        //when
        clickSendAndDialogButton();

        //then
        testClickingDialog(R.string.error_404_403_title, android.R.id.button2, false);

        //***********************Dismissing*******************************

        //when
        clickSendAndDialogButton();

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
        clickSendAndDialogButton();

        //then
        testClickingDialog(R.string.error_404_403_title, android.R.id.button1, true);
        onView(withId(android.R.id.button2)).perform(click());

        //***********************Denying*******************************

        //when
        clickSendAndDialogButton();

        //then
        testClickingDialog(R.string.error_404_403_title, android.R.id.button2, false);

        //***********************Dismissing*******************************

        //when
        clickSendAndDialogButton();

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
        clickSendAndDialogButton();

        //then
        testClickingDialog(R.string.unknown_error_title, android.R.id.button1, true);
        onView(withId(android.R.id.button2)).perform(click());

        //***********************Denying*******************************

        //when
        clickSendAndDialogButton();

        //then
        testClickingDialog(R.string.unknown_error_title, android.R.id.button2, false);

        //***********************Dismissing*******************************

        //when
        clickSendAndDialogButton();

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

        //***********************Dismissing*******************************

        //when
        clickSendAndDialogButton();

        //then
        testDismissingDialog(R.string.unknown_error_title, true);

//        intended(IntentMatchers.hasComponent("de.fhb.campusapp.eval.ui.scan.ScanActivity"));

        //***********************Accepting*******************************

        testClickingDialog(R.string.unknown_error_title, android.R.id.button3, false);

        intended(IntentMatchers.hasComponent("de.fhb.campusapp.eval.ui.scan.ScanActivity"));

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

        //***********************Dismissing*******************************

        //when
        clickSendAndDialogButton();

        //then
        testDismissingDialog(R.string.invalid_token_title, true);

//        intended(IntentMatchers.hasComponent("de.fhb.campusapp.eval.ui.scan.ScanActivity"));

        //***********************Accepting*******************************

        testClickingDialog(R.string.invalid_token_title, android.R.id.button3, false);

        intended(IntentMatchers.hasComponent("de.fhb.campusapp.eval.ui.scan.ScanActivity"));
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

        //***********************Dismissing*******************************

        //when
        clickSendAndDialogButton();

        //then
        testDismissingDialog(R.string.token_already_used_title, true);

//        intended(IntentMatchers.hasComponent("de.fhb.campusapp.eval.ui.scan.ScanActivity"));

        //***********************Accepting*******************************

        testClickingDialog(R.string.token_already_used_title, android.R.id.button3, false);

        intended(IntentMatchers.hasComponent("de.fhb.campusapp.eval.ui.scan.ScanActivity"));

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

        //***********************Dismissing*******************************

        //when
        clickSendAndDialogButton();

        //then
        testDismissingDialog(R.string.evaluation_closed_title, true);

//        intended(IntentMatchers.hasComponent("de.fhb.campusapp.eval.ui.scan.ScanActivity"));

        //***********************Accepting*******************************

        testClickingDialog(R.string.evaluation_closed_title, android.R.id.button3, false);

        intended(IntentMatchers.hasComponent("de.fhb.campusapp.eval.ui.scan.ScanActivity"));

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

        //***********************Dismissing*******************************

        //when
        clickSendAndDialogButton();

        //then
        testDismissingDialog(R.string.unknown_error_title, true);

//        intended(IntentMatchers.hasComponent("de.fhb.campusapp.eval.ui.scan.ScanActivity"));

        //***********************Accepting*******************************

        testClickingDialog(R.string.unknown_error_title, android.R.id.button3, false);

        intended(IntentMatchers.hasComponent("de.fhb.campusapp.eval.ui.scan.ScanActivity"));
    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }

    private void clickSendAndDialogButton(){
        PermissionGranter.allowPermissionsIfNeeded(Manifest.permission.INTERNET);
        onView(withId(R.id.send_button)).perform(click());
        onView(withId(android.R.id.button1))
                .inRoot(RootMatchers.isDialog())
                .perform(click());
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
        }
    }

    private void navigateToEnd(){
        onView(withId(R.id.question_search)).perform(click());
        onView(withText("Send"))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());
    }

    private void navigateToStart(){
        onView(withId(R.id.question_search)).perform(click());
        onView(withText("Choose your subject"))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());
    }
}
