package de.thb.ue.android.utility;

import java.util.ArrayList;
import java.util.List;

import de.thb.ca.dto.MultipleChoiceQuestionDTO;
import de.thb.ca.dto.util.ChoiceDTO;

/**
 * Created by Admin on 22.12.2015.
 */
public class DebugConfigurator {
    public static final boolean DEBUG_ACTIVE = true;

    public static final String genericVoteToken = "BlaBla";
    public static final String genericID = "genericID";

    public static List<String> getDemoQuestions() {
        return new ArrayList<String>() {{
            add("This shows the interface for a question which can be answered by text or with a photo.");
            add("This shows how text questions behave when next to each other.");
        }};
    }

    public static List<MultipleChoiceQuestionDTO> getDemoMultipleChoiceQuestionDTOs() {
        List<MultipleChoiceQuestionDTO> out = new ArrayList<>();
        out.add(new MultipleChoiceQuestionDTO("Interface for question with 2 + 1 possible answers.", new ArrayList<ChoiceDTO>() {{
            add(new ChoiceDTO("No comment", (short) 0));
            add(new ChoiceDTO("Positive answer", (short) 1));
            add(new ChoiceDTO("Negative answer", (short) 2));
        }}));

        out.add(new MultipleChoiceQuestionDTO("Interface for question with 3 + 1 possible answers.", new ArrayList<ChoiceDTO>() {{
            add(new ChoiceDTO("No comment", (short) 0));
            add(new ChoiceDTO("Positive answer", (short) 1));
            add(new ChoiceDTO("Neutral answer", (short) 2));
            add(new ChoiceDTO("Negative answer", (short) 3));
        }}));

        out.add(new MultipleChoiceQuestionDTO("Interface for question with 3 + 1 possible answers. The best answer placed in the middle.", new ArrayList<ChoiceDTO>() {{
            add(new ChoiceDTO("No comment", (short) 0));
            add(new ChoiceDTO("Negative answer", (short) 3));
            add(new ChoiceDTO("Positive answer", (short) 1));
            add(new ChoiceDTO("Negative answer", (short) 3));
        }}));

        out.add(new MultipleChoiceQuestionDTO("Interface for question with 4 + 1 possible answers.", new ArrayList<ChoiceDTO>() {{
            add(new ChoiceDTO("No comment", (short) 0));
            add(new ChoiceDTO("Positive answer", (short) 1));
            add(new ChoiceDTO("Slightly positive answer", (short) 2));
            add(new ChoiceDTO("Slightly negative answer", (short) 3));
            add(new ChoiceDTO("Negative answer", (short) 4));
        }}));

        out.add(new MultipleChoiceQuestionDTO("Interface for question with 5 + 1 possible answers.", new ArrayList<ChoiceDTO>() {{
            add(new ChoiceDTO("No comment", (short) 0));
            add(new ChoiceDTO("positive answer", (short) 1));
            add(new ChoiceDTO("Slightly positive answer", (short) 2));
            add(new ChoiceDTO("neutral answer", (short) 3));
            add(new ChoiceDTO("Slightly negative answer", (short) 4));
            add(new ChoiceDTO("Negative answer", (short) 5));
        }}));

        out.add(new MultipleChoiceQuestionDTO("Interface for question with 5 + 1 possible answers. The best answer placed in the middle.", new ArrayList<ChoiceDTO>() {{
            add(new ChoiceDTO("No comment", (short) 0));
            add(new ChoiceDTO("Negative answer", (short) 5));
            add(new ChoiceDTO("Slightly negative answer", (short) 3));
            add(new ChoiceDTO("positive answer", (short) 1));
            add(new ChoiceDTO("Slightly negative answer", (short) 3));
            add(new ChoiceDTO("Negative answer", (short) 5));
        }}));

        out.add(new MultipleChoiceQuestionDTO("Interface for question with 6 + 1 possible answers.", new ArrayList<ChoiceDTO>() {{
            add(new ChoiceDTO("No comment", (short) 0));
            add(new ChoiceDTO("Very positive answer", (short) 1));
            add(new ChoiceDTO("positive answer", (short) 2));
            add(new ChoiceDTO("Slightly positive answer", (short) 3));
            add(new ChoiceDTO("Slightly negative answer", (short) 4));
            add(new ChoiceDTO("Negative answer", (short) 5));
            add(new ChoiceDTO("Very negative answer", (short) 6));
        }}));

        out.add(new MultipleChoiceQuestionDTO("Interface for question with 7 + 1 possible answers.", new ArrayList<ChoiceDTO>() {{
            add(new ChoiceDTO("No comment", (short) 0));
            add(new ChoiceDTO("Very positive answer", (short) 1));
            add(new ChoiceDTO("positive answer", (short) 2));
            add(new ChoiceDTO("Slightly positive answer", (short) 3));
            add(new ChoiceDTO("Neutral answer", (short) 4));
            add(new ChoiceDTO("Slightly negative answer", (short) 5));
            add(new ChoiceDTO("Negative answer", (short) 6));
            add(new ChoiceDTO("Very negative answer", (short) 7));
        }}));


        out.add(new MultipleChoiceQuestionDTO("Interface for question with 6 + 1 possible answers. The best answer placed in the middle.", new ArrayList<ChoiceDTO>() {{
            add(new ChoiceDTO("No comment", (short) 0));
            add(new ChoiceDTO("Very Negative answer", (short) 7));
            add(new ChoiceDTO("Negative answer", (short) 5));
            add(new ChoiceDTO("Slightly negative answer", (short) 3));
            add(new ChoiceDTO("positive answer", (short) 1));
            add(new ChoiceDTO("Slightly negative answer", (short) 3));
            add(new ChoiceDTO("Negative answer", (short) 5));
            add(new ChoiceDTO("Very Negative answer", (short) 7));
        }}));
        return out;
    }

    public static List<String> getDemoStudyPaths() {
        List<String> out = new ArrayList<>();
        out.add("Applied Computer Science");
        out.add("Informatik");
        out.add("Medizininformatik");
        out.add("Medieninformatik");
        out.add("Informatik");
        out.add("Digitale Medien");
        out.add("Medieninformatik");

        return out;
    }
}
