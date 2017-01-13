package de.fhb.campusapp.eval.utility;

import java.util.ArrayList;
import java.util.List;

import de.fhb.campusapp.eval.utility.vos.ChoiceVO;
import de.fhb.campusapp.eval.utility.vos.MultipleChoiceQuestionVO;
import de.fhb.campusapp.eval.utility.vos.TextQuestionVO;

/**
 * Created by Sebastian Müller on 22.12.2015.
 */
public class DebugConfigurator {

    public static final String genericVoteToken = "BlaBla";
    public static final String genericID = "genericID";

    public static List<TextQuestionVO> getDemoTextQuestions() {
        return new ArrayList<TextQuestionVO>() {{
            add(new TextQuestionVO(1, "This shows the interface for a question which can be answered by text or with a photo.", false, 100));
            add(new TextQuestionVO(1, "This shows a question where only numbers are allowed and whose input capacity is limited to 4", true, 4));
        }};
    }

    public static List<MultipleChoiceQuestionVO> getDemoMultipleChoiceQuestionDTOs() {
        List<MultipleChoiceQuestionVO> out = new ArrayList<>();
        out.add(new MultipleChoiceQuestionVO("Interface for question with 2 + 1 possible answers.", new ArrayList<ChoiceVO>() {{
            add(new ChoiceVO("No comment", (short) 0));
            add(new ChoiceVO("Positive answer", (short) 1));
            add(new ChoiceVO("Negative answer", (short) 2));
        }}));

        out.add(new MultipleChoiceQuestionVO("Interface for question with 3 + 1 possible answers.", new ArrayList<ChoiceVO>() {{
            add(new ChoiceVO("No comment", (short) 0));
            add(new ChoiceVO("Positive answer", (short) 1));
            add(new ChoiceVO("Neutral answer", (short) 2));
            add(new ChoiceVO("Negative answer", (short) 3));
        }}));

        out.add(new MultipleChoiceQuestionVO("Interface for question with 3 + 1 possible answers. The best answer placed in the middle.", new ArrayList<ChoiceVO>() {{
            add(new ChoiceVO("No comment", (short) 0));
            add(new ChoiceVO("Negative answer", (short) 3));
            add(new ChoiceVO("Positive answer", (short) 1));
            add(new ChoiceVO("Negative answer", (short) -3));
        }}));

        out.add(new MultipleChoiceQuestionVO("Interface for question with 4 + 1 possible answers.", new ArrayList<ChoiceVO>() {{
            add(new ChoiceVO("No comment", (short) 0));
            add(new ChoiceVO("Positive answer", (short) 1));
            add(new ChoiceVO("Slightly positive answer", (short) 2));
            add(new ChoiceVO("Slightly negative answer", (short) 3));
            add(new ChoiceVO("Negative answer", (short) 4));
        }}));

        out.add(new MultipleChoiceQuestionVO("Interface for question with 5 + 1 possible answers.", new ArrayList<ChoiceVO>() {{
            add(new ChoiceVO("No comment", (short) 0));
            add(new ChoiceVO("positive answer", (short) 1));
            add(new ChoiceVO("Slightly positive answer", (short) 2));
            add(new ChoiceVO("neutral answer", (short) 3));
            add(new ChoiceVO("Slightly negative answer", (short) 4));
            add(new ChoiceVO("Negative answer", (short) 5));
        }}));

        out.add(new MultipleChoiceQuestionVO("Interface for question with 5 + 1 possible answers. The best answer placed in the middle.", new ArrayList<ChoiceVO>() {{
            add(new ChoiceVO("No comment", (short) 0));
            add(new ChoiceVO("Negative answer", (short) 5));
            add(new ChoiceVO("Slightly negative answer", (short) 3));
            add(new ChoiceVO("positive answer", (short) 1));
            add(new ChoiceVO("Slightly negative answer", (short) -3));
            add(new ChoiceVO("Negative answer", (short) -5));
        }}));

        out.add(new MultipleChoiceQuestionVO("Interface for question with 6 + 1 possible answers.", new ArrayList<ChoiceVO>() {{
            add(new ChoiceVO("No comment", (short) 0));
            add(new ChoiceVO("Very positive answer", (short) 1));
            add(new ChoiceVO("positive answer", (short) 2));
            add(new ChoiceVO("Slightly positive answer", (short) 3));
            add(new ChoiceVO("Slightly negative answer", (short) 4));
            add(new ChoiceVO("Negative answer", (short) 5));
            add(new ChoiceVO("Very negative answer", (short) 6));
        }}));

        out.add(new MultipleChoiceQuestionVO("Interface for question with 7 + 1 possible answers.", new ArrayList<ChoiceVO>() {{
            add(new ChoiceVO("No comment", (short) 0));
            add(new ChoiceVO("Very positive answer", (short) 1));
            add(new ChoiceVO("positive answer", (short) 2));
            add(new ChoiceVO("Slightly positive answer", (short) 3));
            add(new ChoiceVO("Neutral answer", (short) 4));
            add(new ChoiceVO("Slightly negative answer", (short) 5));
            add(new ChoiceVO("Negative answer", (short) 6));
            add(new ChoiceVO("Very negative answer", (short) 7));
        }}));


        out.add(new MultipleChoiceQuestionVO("Interface for question with 7 + 1 possible answers. The best answer placed in the middle.", new ArrayList<ChoiceVO>() {{
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

    public static List<String> getDemoStudyPaths() {
        List<String> out = new ArrayList<>();
        out.add("Applied Computer Science");
        out.add("Informatik");
        out.add("Medizininformatik");
        out.add("Digitale Medien");
        out.add("Medieninformatik");

        return out;
    }
}
