package de.thb.ue.android.utility;

import java.util.ArrayList;
import java.util.List;

import de.thb.ue.android.data.VOs.ChoiceVO;
import de.thb.ue.android.data.VOs.QuestionsVO;
import de.thb.ue.android.data.VOs.SingleChoiceQuestionVO;
import de.thb.ue.android.data.VOs.TextQuestionVO;

/**
 * Created by scorp on 05.05.2017.
 */

public class TestQuestionnaire {


    public static QuestionsVO getTestQuestionnaire(){
        return new QuestionsVO(getDemoStudyPaths(), getDemoTextQuestions(), getDemoSingleChoiceQuestions(), false);
    }

    private static List<TextQuestionVO> getDemoTextQuestions() {
        return new ArrayList<TextQuestionVO>() {{
            add(new TextQuestionVO(1, "This shows the interface for a question which can be answered by text or with a photo.", false, 100));
            add(new TextQuestionVO(1, "This shows a question where only numbers are allowed and whose input capacity is limited to 4", true, 4));
        }};
    }

    private static List<SingleChoiceQuestionVO> getDemoSingleChoiceQuestions() {
        List<SingleChoiceQuestionVO> out = new ArrayList<>();

        out.add(new SingleChoiceQuestionVO("Interface for question with 2 possible answers.", new ArrayList<ChoiceVO>() {{
            add(new ChoiceVO("Positive answer", (short) 1));
            add(new ChoiceVO("Negative answer", (short) 2));
        }}));

        out.add(new SingleChoiceQuestionVO("Interface for question with 3 possible answers.", new ArrayList<ChoiceVO>() {{
            add(new ChoiceVO("Positive answer", (short) 1));
            add(new ChoiceVO("Neutral answer", (short) 2));
            add(new ChoiceVO("Negative answer", (short) 3));
        }}));

        out.add(new SingleChoiceQuestionVO("Interface for question with 3 possible answers. The best answer placed in the middle.", new ArrayList<ChoiceVO>() {{
            add(new ChoiceVO("Negative answer", (short) 3));
            add(new ChoiceVO("Positive answer", (short) 1));
            add(new ChoiceVO("Negative answer", (short) -3));
        }}));

        out.add(new SingleChoiceQuestionVO("Interface for question with 4 possible answers.", new ArrayList<ChoiceVO>() {{
            add(new ChoiceVO("Positive answer", (short) 1));
            add(new ChoiceVO("Slightly positive answer", (short) 2));
            add(new ChoiceVO("Slightly negative answer", (short) 3));
            add(new ChoiceVO("Negative answer", (short) 4));
        }}));

        out.add(new SingleChoiceQuestionVO("Interface for question with 5 possible answers.", new ArrayList<ChoiceVO>() {{
            add(new ChoiceVO("positive answer", (short) 1));
            add(new ChoiceVO("Slightly positive answer", (short) 2));
            add(new ChoiceVO("neutral answer", (short) 3));
            add(new ChoiceVO("Slightly negative answer", (short) 4));
            add(new ChoiceVO("Negative answer", (short) 5));
        }}));

        out.add(new SingleChoiceQuestionVO("Interface for question with 5 possible answers. The best answer placed in the middle.", new ArrayList<ChoiceVO>() {{
            add(new ChoiceVO("Negative answer", (short) 5));
            add(new ChoiceVO("Slightly negative answer", (short) 3));
            add(new ChoiceVO("positive answer", (short) 1));
            add(new ChoiceVO("Slightly negative answer", (short) -3));
            add(new ChoiceVO("Negative answer", (short) -5));
        }}));

        out.add(new SingleChoiceQuestionVO("Interface for question with 6 possible answers.", new ArrayList<ChoiceVO>() {{
            add(new ChoiceVO("Very positive answer", (short) 1));
            add(new ChoiceVO("positive answer", (short) 2));
            add(new ChoiceVO("Slightly positive answer", (short) 3));
            add(new ChoiceVO("Slightly negative answer", (short) 4));
            add(new ChoiceVO("Negative answer", (short) 5));
            add(new ChoiceVO("Very negative answer", (short) 6));
        }}));

        out.add(new SingleChoiceQuestionVO("Interface for question with 7 possible answers.", new ArrayList<ChoiceVO>() {{
            add(new ChoiceVO("Very positive answer", (short) 1));
            add(new ChoiceVO("positive answer", (short) 2));
            add(new ChoiceVO("Slightly positive answer", (short) 3));
            add(new ChoiceVO("Neutral answer", (short) 4));
            add(new ChoiceVO("Slightly negative answer", (short) 5));
            add(new ChoiceVO("Negative answer", (short) 6));
            add(new ChoiceVO("Very negative answer", (short) 7));
        }}));


        out.add(new SingleChoiceQuestionVO("Interface for question with 7 possible answers. The best answer placed in the middle.", new ArrayList<ChoiceVO>() {{
            add(new ChoiceVO("Very Negative answer", (short) 7));
            add(new ChoiceVO("Negative answer", (short) 5));
            add(new ChoiceVO("Slightly negative answer", (short) 3));
            add(new ChoiceVO("positive answer", (short) 1));
            add(new ChoiceVO("Slightly negative answer", (short) -3));
            add(new ChoiceVO("Negative answer", (short) -5));
            add(new ChoiceVO("Very Negative answer", (short) -7));
        }}));

        out.add(new SingleChoiceQuestionVO("Interface for question with 2 + 1 possible answers.", new ArrayList<ChoiceVO>() {{
            add(new ChoiceVO("No comment", (short) 0));
            add(new ChoiceVO("Positive answer", (short) 1));
            add(new ChoiceVO("Negative answer", (short) 2));
        }}));

        out.add(new SingleChoiceQuestionVO("Interface for question with 3 + 1 possible answers.", new ArrayList<ChoiceVO>() {{
            add(new ChoiceVO("No comment", (short) 0));
            add(new ChoiceVO("Positive answer", (short) 1));
            add(new ChoiceVO("Neutral answer", (short) 2));
            add(new ChoiceVO("Negative answer", (short) 3));
        }}));

        out.add(new SingleChoiceQuestionVO("Interface for question with 3 + 1 possible answers. The best answer placed in the middle.", new ArrayList<ChoiceVO>() {{
            add(new ChoiceVO("No comment", (short) 0));
            add(new ChoiceVO("Negative answer", (short) 3));
            add(new ChoiceVO("Positive answer", (short) 1));
            add(new ChoiceVO("Negative answer", (short) -3));
        }}));

        out.add(new SingleChoiceQuestionVO("Interface for question with 4 + 1 possible answers.", new ArrayList<ChoiceVO>() {{
            add(new ChoiceVO("No comment", (short) 0));
            add(new ChoiceVO("Positive answer", (short) 1));
            add(new ChoiceVO("Slightly positive answer", (short) 2));
            add(new ChoiceVO("Slightly negative answer", (short) 3));
            add(new ChoiceVO("Negative answer", (short) 4));
        }}));

        out.add(new SingleChoiceQuestionVO("Interface for question with 5 + 1 possible answers.", new ArrayList<ChoiceVO>() {{
            add(new ChoiceVO("No comment", (short) 0));
            add(new ChoiceVO("positive answer", (short) 1));
            add(new ChoiceVO("Slightly positive answer", (short) 2));
            add(new ChoiceVO("neutral answer", (short) 3));
            add(new ChoiceVO("Slightly negative answer", (short) 4));
            add(new ChoiceVO("Negative answer", (short) 5));
        }}));

        out.add(new SingleChoiceQuestionVO("Interface for question with 5 + 1 possible answers. The best answer placed in the middle.", new ArrayList<ChoiceVO>() {{
            add(new ChoiceVO("No comment", (short) 0));
            add(new ChoiceVO("Negative answer", (short) 5));
            add(new ChoiceVO("Slightly negative answer", (short) 3));
            add(new ChoiceVO("positive answer", (short) 1));
            add(new ChoiceVO("Slightly negative answer", (short) -3));
            add(new ChoiceVO("Negative answer", (short) -5));
        }}));

        out.add(new SingleChoiceQuestionVO("Interface for question with 6 + 1 possible answers.", new ArrayList<ChoiceVO>() {{
            add(new ChoiceVO("No comment", (short) 0));
            add(new ChoiceVO("Very positive answer", (short) 1));
            add(new ChoiceVO("positive answer", (short) 2));
            add(new ChoiceVO("Slightly positive answer", (short) 3));
            add(new ChoiceVO("Slightly negative answer", (short) 4));
            add(new ChoiceVO("Negative answer", (short) 5));
            add(new ChoiceVO("Very negative answer", (short) 6));
        }}));

        out.add(new SingleChoiceQuestionVO("Interface for question with 7 + 1 possible answers.", new ArrayList<ChoiceVO>() {{
            add(new ChoiceVO("No comment", (short) 0));
            add(new ChoiceVO("Very positive answer", (short) 1));
            add(new ChoiceVO("positive answer", (short) 2));
            add(new ChoiceVO("Slightly positive answer", (short) 3));
            add(new ChoiceVO("Neutral answer", (short) 4));
            add(new ChoiceVO("Slightly negative answer", (short) 5));
            add(new ChoiceVO("Negative answer", (short) 6));
            add(new ChoiceVO("Very negative answer", (short) 7));
        }}));


        out.add(new SingleChoiceQuestionVO("Interface for question with 7 + 1 possible answers. The best answer placed in the middle.", new ArrayList<ChoiceVO>() {{
            add(new ChoiceVO("No comment", (short) 0));
            add(new ChoiceVO("Very Negative answer", (short) 7));
            add(new ChoiceVO("Negative answer", (short) 5));
            add(new ChoiceVO("Slightly negative answer", (short) 3));
            add(new ChoiceVO("positive answer", (short) 1));
            add(new ChoiceVO("Slightly negative answer", (short) -3));
            add(new ChoiceVO("Negative answer", (short) -5));
            add(new ChoiceVO("Very Negative answer", (short) -7));
        }}));
        return out;
    }

    private static List<String> getDemoStudyPaths() {
        List<String> out = new ArrayList<>();
        out.add("Applied Computer Science");
        out.add("Informatik");
        out.add("Medizininformatik");
        out.add("Digitale Medien");
        out.add("Medieninformatik");

        return out;
    }
}
