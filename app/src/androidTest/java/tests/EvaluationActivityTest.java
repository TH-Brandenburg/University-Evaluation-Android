package tests;

import android.Manifest;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.RootMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;


import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import de.fhb.campusapp.eval.ui.eval.EvaluationActivity;
import de.fhb.campusapp.eval.utility.DataHolder;
import de.fhb.campusapp.eval.utility.DebugConfigurator;
import de.fhb.campusapp.eval.utility.vos.ImageDataVO;
import de.fhb.campusapp.eval.utility.vos.MultipleChoiceAnswerVO;
import de.fhb.campusapp.eval.utility.vos.QuestionsVO;
import fhb.de.campusappevaluationexp.R;
import tests.testutil.PermissionGranter;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isSelected;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static tests.matchers.BackgroundcolorMatcher.withBackgroundColor;

/**
 * Created by Sebastian Müller on 23.10.2016.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class EvaluationActivityTest {

    @Rule
    public final IntentsTestRule<EvaluationActivity> main = new IntentsTestRule<>(EvaluationActivity.class);

    @Before
    public void setup(){
        DataHolder.deleteAllData();
        DataHolder.setQuestionsVO(new QuestionsVO(
                DebugConfigurator.getDemoStudyPaths(),
                DebugConfigurator.getDemoTextQuestions(),
                DebugConfigurator.getDemoMultipleChoiceQuestionDTOs(),
                false
        ));
    }

    // divide into many tests???
    @Test
    public void testCorrectQuestionnaireGeneration() throws InterruptedException {
        List<ViewInteraction> interactions = new ArrayList<>(8);

        //first slide is tested separately

        //second slide
        loopSwipeLeft(1);
        interactions.add(onView(allOf(withId(R.id.no_comment_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.top_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.bottom_button), isCompletelyDisplayed())));

        iteratingExistenceCheck(interactions);
        interactions.clear();

        //third slide
        loopSwipeLeft(1);
        interactions.add(onView(allOf(withId(R.id.no_comment_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.top_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.middle_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.bottom_button), isCompletelyDisplayed())));

        iteratingExistenceCheck(interactions);
        interactions.clear();

        //fourth slide
        loopSwipeLeft(1);
        interactions.add(onView(allOf(withId(R.id.no_comment_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.top_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.middle_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.bottom_button), isCompletelyDisplayed())));

        iteratingExistenceCheck(interactions);
        interactions.clear();

        //fifth slide
        loopSwipeLeft(1);
        interactions.add(onView(allOf(withId(R.id.no_comment_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.top_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.upper_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.lower_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.bottom_button), isCompletelyDisplayed())));


        iteratingExistenceCheck(interactions);
        interactions.clear();

        //sixth slide
        loopSwipeLeft(1);
        interactions.add(onView(allOf(withId(R.id.no_comment_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.top_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.upper_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.middle_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.lower_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.bottom_button), isCompletelyDisplayed())));


        iteratingExistenceCheck(interactions);
        interactions.clear();

        //seventh slide
        loopSwipeLeft(1);
        interactions.add(onView(allOf(withId(R.id.no_comment_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.top_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.upper_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.middle_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.lower_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.bottom_button), isCompletelyDisplayed())));

        iteratingExistenceCheck(interactions);
        interactions.clear();

        //eight slide
        loopSwipeLeft(1);
        interactions.add(onView(allOf(withId(R.id.no_comment_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.top_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.upper_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.upper_middle_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.lower_middle_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.lower_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.bottom_button), isCompletelyDisplayed())));

        iteratingExistenceCheck(interactions);
        interactions.clear();

        //ninth slide
        loopSwipeLeft(1);
        interactions.add(onView(allOf(withId(R.id.no_comment_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.top_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.upper_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.upper_middle_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.middle_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.lower_middle_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.lower_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.bottom_button), isCompletelyDisplayed())));

        iteratingExistenceCheck(interactions);
        interactions.clear();

        //tenth slide
        loopSwipeLeft(1);
        interactions.add(onView(allOf(withId(R.id.no_comment_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.top_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.upper_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.upper_middle_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.middle_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.lower_middle_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.lower_button), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.bottom_button), isCompletelyDisplayed())));

        iteratingExistenceCheck(interactions);
        interactions.clear();

        //eleventh slide
        loopSwipeLeft(1);
        interactions.add(onView(allOf(withId(R.id.question_text_view), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.comment_thumbnail), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.camera_button), isCompletelyDisplayed())));

        iteratingExistenceCheck(interactions);
        interactions.clear();

        //twelfth slide
        loopSwipeLeft(1);
        interactions.add(onView(allOf(withId(R.id.question_text_view), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.comment_thumbnail), isCompletelyDisplayed())));
        interactions.add(onView(allOf(withId(R.id.camera_button), isCompletelyDisplayed())));

        iteratingExistenceCheck(interactions);
        interactions.clear();

        //thirteenth slide
        loopSwipeLeft(1);
        interactions.add(onView(allOf(withId(R.id.send_button), isCompletelyDisplayed())));

        iteratingExistenceCheck(interactions);
    }

    @Test
    public void testInnerSectionFragment(){
        onData(allOf(is(instanceOf(String.class)), is("Informatik"))).perform(click());

        //check for correct data in DataHolder
        assertThat(DataHolder.getAnswersVO().getStudyPath(), is("Informatik"));
    }

    @Test
    public void testAutoSwipingByButtonClick() throws InterruptedException {
        onData(allOf(is(instanceOf(String.class)), is("Informatik"))).perform(click());
        Thread.sleep(500L);
        ViewInteraction interaction2 = onView(allOf(withId(R.id.question_text_view), withText("Interface for question with 2 + 1 possible answers.")));
        interaction2.check(matches(isCompletelyDisplayed())); // -> auto swipe worked
    }

    @Test
    public void testButtonFragments() throws InterruptedException {
        onView(withId(R.id.button_pager)).perform(swipeLeft());
        Thread.sleep(300L);

        //second slide
        ViewInteraction interaction = onView(allOf(withId(R.id.bottom_button), isCompletelyDisplayed())).perform(click());
        performAutoSweepReversal();
        interaction.check(matches(isSelected()));

        ViewInteraction interaction2 = onView(allOf(withId(R.id.top_button), isCompletelyDisplayed())).perform(click());
        performAutoSweepReversal();
        interaction2.check(matches(isSelected()));
        interaction.check(matches(not(isSelected())));

        //swipe left to third slide
        onView(withId(R.id.button_pager)).perform(swipeLeft());
        Thread.sleep(300L);

        //third slide
        ViewInteraction interaction3 = onView(allOf(withId(R.id.middle_button), isCompletelyDisplayed())).perform(click());
        performAutoSweepReversal();
        interaction3.check(matches(isSelected()));
        interaction3.check(matches(withText("Neutral answer")));

        ViewInteraction interaction4 = onView(allOf(withId(R.id.no_comment_button), isCompletelyDisplayed()));
        interaction4.check(matches(withText("No comment")));

        //check for correct data in DataHolder
        MultipleChoiceAnswerVO secondSlide = DataHolder.isMcQuestionAnswered("Interface for question with 2 + 1 possible answers.");
        assertThat(secondSlide.getChoice().getChoiceText(), is("Positive answer"));

        MultipleChoiceAnswerVO firstSlide = DataHolder.isMcQuestionAnswered("Interface for question with 3 + 1 possible answers.");
        assertThat(firstSlide.getChoice().getChoiceText(), is("Neutral answer"));
    }

    @Test
    public void testTabStripSwipeNavigation(){
        //given
        ViewInteraction interaction = onView(withId(R.id.inner_section_list_view));
        interaction.check(matches(isCompletelyDisplayed()));

        //when
        onView(withId(R.id.button_pager_tab_strip)).perform(swipeLeft());

        //then
        ViewInteraction interaction2 = onView(allOf(withId(R.id.question_text_view), withText("Interface for question with 2 + 1 possible answers.")));
        interaction2.check(matches(isCompletelyDisplayed()));
    }

    @Test
    public void testToolbarNavigationVisuals(){
        //given
        onView(withId(R.id.question_search)).perform(click());

        //when
        ViewInteraction interaction2 = onView(withText("Choose your subject"))
                .inRoot(RootMatchers.isPlatformPopup());
        ViewInteraction interaction3 = onView(withText("Interface for question with 2 + 1 possible answers."))
                .inRoot(RootMatchers.isPlatformPopup());

        //then
        interaction2.check(matches(withBackgroundColor(R.color.campusapptheme_color_negative_red)));
        interaction3.check(matches(withBackgroundColor(R.color.campusapptheme_color_transparent)));
    }

    @Test
    public void testToolbarNavigationToTextQuestion(){
        //given
        ViewInteraction interaction = onView(withId(R.id.inner_section_list_view));
        interaction.check(matches(isCompletelyDisplayed()));

        //when
        onView(withId(R.id.question_search)).perform(click());
        onView(withText("10. This shows the interface for a question which can be answered by text or with a photo."))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());

        //then
        ViewInteraction interaction2 = onView(allOf(withId(R.id.question_text_view), isCompletelyDisplayed()));
        interaction2.check(matches(withText("This shows the interface for a question which can be answered by text or with a photo.")));
    }
//
    @Test
    public void testToolbarNavigationToSCQuestion(){
        //given
        ViewInteraction interaction = onView(withId(R.id.inner_section_list_view));
        interaction.check(matches(isCompletelyDisplayed()));

        //when
        onView(withId(R.id.question_search)).perform(click());
        onView(withText("7. Interface for question with 6 + 1 possible answers."))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());

        //then
        ViewInteraction interaction2 = onView(allOf(withId(R.id.question_text_view), isCompletelyDisplayed()));
        interaction2.check(matches(withText("Interface for question with 6 + 1 possible answers.")));
    }
//
    @Test
    public void testToolbarNavigationToSend(){
        //given
        ViewInteraction interaction = onView(withId(R.id.inner_section_list_view));
        interaction.check(matches(isCompletelyDisplayed()));

        //when
        onView(withId(R.id.question_search)).perform(click());
        onView(withText("Send"))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());

        //then
        ViewInteraction interaction2 = onView(allOf(withId(R.id.send_button), isCompletelyDisplayed()));
        interaction2.check(matches(withText(R.string.send_button)));
    }
//
    @Test
    public void testTextFragments() throws InterruptedException {
        //*******************input letters in numeric input field************************

        //given
        loopSwipeLeft(11);

        //when (this question only allows numericals to be entered)
        ViewInteraction interaction1 = onView(allOf(withId(R.id.edit_text), isCompletelyDisplayed()));
        interaction1.perform(typeText("asds"));

        //then
        interaction1.check(matches(withText("")));

        //*******************input numerics into numeric input field************************

        //when
        interaction1.perform(typeText("1234"));

        //then
        interaction1.check(matches(withText("1234")));

        //*******************take a picture************************

        //when
        ViewInteraction interaction2 = onView(allOf(withId(R.id.comment_thumbnail), isCompletelyDisplayed()));
        interaction2.perform(click());

        PermissionGranter.allowPermissionsIfNeeded(Manifest.permission.CAMERA);
        PermissionGranter.allowPermissionsIfNeeded(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        onView(withId(R.id.cwac_cam2_picture)).perform(click());
        Thread.sleep(4000L);

        //then
        ImageDataVO image = DataHolder.getCommentaryImageMap().get("This shows a question where only numbers are allowed and whose input capacity is limited to 4");
        assertThat(image, is(notNullValue()));
        assertThat(image.getmLargeImageFilePath(), allOf(is(notNullValue()), is(not(""))));

        //*******************enlarge taken picture and return************************

        //when
        onView(allOf(withId(R.id.comment_thumbnail), isCompletelyDisplayed())).perform(click());

        //then
        onView(withId(R.id.enlarged_image_view)).check(matches(isCompletelyDisplayed()));
        pressBack();
        onView(allOf(withId(R.id.edit_text), isCompletelyDisplayed()));
    }

    @Test
    public void testNoSubjectChosenDialog(){

    }

    @Test
    public void testNotAllQuestionsAnswered(){

    }



    private void performAutoSweepReversal() throws InterruptedException {
        onView(withId(R.id.button_pager)).perform(swipeRight()); //reverse automated swiping after button click
    }

    private void loopSwipeLeft(int loops) throws InterruptedException {
        for(int i = 0; i < loops; i++){
            onView(withId(R.id.button_pager)).perform(swipeLeft());
        }
    }

    private void iteratingExistenceCheck(List<ViewInteraction> interactions){
        for(ViewInteraction interaction : interactions){
            interaction.check(matches(is(notNullValue())));
        }
    }
}
