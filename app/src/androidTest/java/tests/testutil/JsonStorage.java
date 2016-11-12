package tests.testutil;

/**
 * Created by Sebastian Müller on 26.10.2016.
 */
public class JsonStorage {

    public static final String requestDTOJson = "{\n" +
            "\t\"voteToken\": \"6e7d740b-06ae-43a4-a231-b685b3b43351\",\n" +
            "\t\"deviceID\": \"111111111\"\n" +
            "}";

    public static final String responseDTOJsonMinus1 = "{\n" +
            "  \"message\": \"Already voted (ParticipantException 6 )\",\n" +
            "  \"type\": -1\n" +
            "}";

    public static final String responseDTOJson1 = "{\n" +
            "  \"message\": \"Already voted (ParticipantException 6 )\",\n" +
            "  \"type\": 1\n" +
            "}";

    public static final String responseDTOJson2 = "{\n" +
            "  \"message\": \"Already voted (ParticipantException 6 )\",\n" +
            "  \"type\": 2\n" +
            "}";

    public static final String responseDTOJson3 = "{\n" +
            "  \"message\": \"Already voted (ParticipantException 6 )\",\n" +
            "  \"type\": 3\n" +
            "}";

    public static final String responseDTOJson4 = "{\n" +
            "  \"message\": \"Already voted (ParticipantException 6 )\",\n" +
            "  \"type\": 4\n" +
            "}";

    public static final String responseDTOJson5 = "{\n" +
            "  \"message\": \"Already voted (ParticipantException 6 )\",\n" +
            "  \"type\": 5\n" +
            "}";

    public static final String responseDTOJson6 = "{\n" +
            "  \"message\": \"Already voted (ParticipantException 6 )\",\n" +
            "  \"type\": 6\n" +
            "}";

    public static final String questionsDTOJson = "{\n" +
            "  \"studyPaths\": [\n" +
            "    \"Betriebswirtschaftslehre\",\n" +
            "    \"Security Management\",\n" +
            "    \"Technologie- und Innovationsmanagement\",\n" +
            "    \"Wirtschaftsinformatik\"\n" +
            "  ],\n" +
            "  \"textQuestions\": [\n" +
            "    {\n" +
            "      \"questionID\": 42,\n" +
            "      \"questionText\": \"Was fanden Sie positiv?\",\n" +
            "      \"onlyNumbers\": false,\n" +
            "      \"maxLength\": 1000\n" +
            "    },\n" +
            "    {\n" +
            "      \"questionID\": 43,\n" +
            "      \"questionText\": \"Was fanden Sie negativ?\",\n" +
            "      \"onlyNumbers\": false,\n" +
            "      \"maxLength\": 1000\n" +
            "    },\n" +
            "    {\n" +
            "      \"questionID\": 44,\n" +
            "      \"questionText\": \"Welche Verbesserungsvorschläge würden Sie machen?\",\n" +
            "      \"onlyNumbers\": false,\n" +
            "      \"maxLength\": 1000\n" +
            "    },\n" +
            "    {\n" +
            "      \"questionID\": 45,\n" +
            "      \"questionText\": \"Weitere Anmerkungen?\",\n" +
            "      \"onlyNumbers\": false,\n" +
            "      \"maxLength\": 1000\n" +
            "    }\n" +
            "  ],\n" +
            "  \"multipleChoiceQuestionDTOs\": [\n" +
            "    {\n" +
            "      \"question\": \"Haben Sie die LV regelmäßig besucht?\",\n" +
            "      \"choices\": [\n" +
            "        {\n" +
            "          \"choiceText\": \"k.A\",\n" +
            "          \"grade\": 0\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"immer\",\n" +
            "          \"grade\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"oft\",\n" +
            "          \"grade\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"mittel\",\n" +
            "          \"grade\": 3\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"selten\",\n" +
            "          \"grade\": 4\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"nie\",\n" +
            "          \"grade\": 5\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"question\": \"Haben Sie Interesse an dem Fach?\",\n" +
            "      \"choices\": [\n" +
            "        {\n" +
            "          \"choiceText\": \"k.A\",\n" +
            "          \"grade\": 0\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"sehr groß\",\n" +
            "          \"grade\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"groß\",\n" +
            "          \"grade\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"mittel\",\n" +
            "          \"grade\": 3\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"klein\",\n" +
            "          \"grade\": 4\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"sehr klein\",\n" +
            "          \"grade\": 5\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"question\": \"Wie fanden Sie das Niveau der Lehrveranstaltung?\",\n" +
            "      \"choices\": [\n" +
            "        {\n" +
            "          \"choiceText\": \"k.A\",\n" +
            "          \"grade\": 0\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"zu hoch\",\n" +
            "          \"grade\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"hoch\",\n" +
            "          \"grade\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"optimal\",\n" +
            "          \"grade\": 3\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"niedrig\",\n" +
            "          \"grade\": 4\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"zu niedrig\",\n" +
            "          \"grade\": 5\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"question\": \"Seine/Ihre Sprache und Ausdrucksweise sind stets klar verständlich.\",\n" +
            "      \"choices\": [\n" +
            "        {\n" +
            "          \"choiceText\": \"k.A\",\n" +
            "          \"grade\": 0\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"stimme zu\",\n" +
            "          \"grade\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"stimme eher zu\",\n" +
            "          \"grade\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"unentschieden\",\n" +
            "          \"grade\": 3\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"stimme eher nicht zu\",\n" +
            "          \"grade\": 4\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"stimme nicht zu\",\n" +
            "          \"grade\": 5\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"question\": \"Er/Sie kann schwierige Sachverhalte verständlich erklären.\",\n" +
            "      \"choices\": [\n" +
            "        {\n" +
            "          \"choiceText\": \"k.A\",\n" +
            "          \"grade\": 0\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"stimme zu\",\n" +
            "          \"grade\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"stimme eher zu\",\n" +
            "          \"grade\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"unentschieden\",\n" +
            "          \"grade\": 3\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"stimme eher nicht zu\",\n" +
            "          \"grade\": 4\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"stimme nicht zu\",\n" +
            "          \"grade\": 5\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"question\": \"Ging er/sie auf Fragen innerhalb der LV ein?\",\n" +
            "      \"choices\": [\n" +
            "        {\n" +
            "          \"choiceText\": \"k.A\",\n" +
            "          \"grade\": 0\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"immer\",\n" +
            "          \"grade\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"oft\",\n" +
            "          \"grade\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"mittel\",\n" +
            "          \"grade\": 3\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"selten\",\n" +
            "          \"grade\": 4\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"nie\",\n" +
            "          \"grade\": 5\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"question\": \"War er/sie stets gut auf die LV vorbereitet?\",\n" +
            "      \"choices\": [\n" +
            "        {\n" +
            "          \"choiceText\": \"k.A\",\n" +
            "          \"grade\": 0\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"immer\",\n" +
            "          \"grade\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"oft\",\n" +
            "          \"grade\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"mittel\",\n" +
            "          \"grade\": 3\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"selten\",\n" +
            "          \"grade\": 4\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"nie\",\n" +
            "          \"grade\": 5\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"question\": \"Welche Gesamtnote geben Sie dem Dozenten/der Dozentin?\",\n" +
            "      \"choices\": [\n" +
            "        {\n" +
            "          \"choiceText\": \"k.A\",\n" +
            "          \"grade\": 0\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"sehr gut\",\n" +
            "          \"grade\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"gut\",\n" +
            "          \"grade\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"befriedigend\",\n" +
            "          \"grade\": 3\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"ausreichend\",\n" +
            "          \"grade\": 4\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"ungenügend\",\n" +
            "          \"grade\": 5\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"question\": \"Welche Gesamtnote geben Sie den Lehrunterlagen?\",\n" +
            "      \"choices\": [\n" +
            "        {\n" +
            "          \"choiceText\": \"k.A\",\n" +
            "          \"grade\": 0\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"sehr gut\",\n" +
            "          \"grade\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"gut\",\n" +
            "          \"grade\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"befriedigend\",\n" +
            "          \"grade\": 3\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"ausreichend\",\n" +
            "          \"grade\": 4\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"ungenügend\",\n" +
            "          \"grade\": 5\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"question\": \"Die Stoffpräsentation der LV war stets klar und gut strukturiert.\",\n" +
            "      \"choices\": [\n" +
            "        {\n" +
            "          \"choiceText\": \"k.A\",\n" +
            "          \"grade\": 0\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"stimme zu\",\n" +
            "          \"grade\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"stimme eher zu\",\n" +
            "          \"grade\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"unentschieden\",\n" +
            "          \"grade\": 3\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"stimme eher nicht zu\",\n" +
            "          \"grade\": 4\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"stimme nicht zu\",\n" +
            "          \"grade\": 5\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"question\": \"Wie war die Stoffmenge im Verhältnis zur verfügbaren Zeit?\",\n" +
            "      \"choices\": [\n" +
            "        {\n" +
            "          \"choiceText\": \"k.A\",\n" +
            "          \"grade\": 0\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"sehr viel Stoff\",\n" +
            "          \"grade\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"viel Stoff\",\n" +
            "          \"grade\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"optimal\",\n" +
            "          \"grade\": 3\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"wenig Stoff\",\n" +
            "          \"grade\": 4\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"sehr wenig Stoff\",\n" +
            "          \"grade\": 5\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"question\": \"Die Übung war nützlich. Sie war sehr gut geeignet, die Vorlesungsinhalte zu verdeutlichen und zu vertiefen.\",\n" +
            "      \"choices\": [\n" +
            "        {\n" +
            "          \"choiceText\": \"k.A\",\n" +
            "          \"grade\": 0\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"stimme zu\",\n" +
            "          \"grade\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"stimme eher zu\",\n" +
            "          \"grade\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"unentschieden\",\n" +
            "          \"grade\": 3\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"stimme eher nicht zu\",\n" +
            "          \"grade\": 4\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"stimme nicht zu\",\n" +
            "          \"grade\": 5\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"question\": \"Wie beurteilen Sie den Übungsanteil im Vergleich zum Vorlesungsanteil?\",\n" +
            "      \"choices\": [\n" +
            "        {\n" +
            "          \"choiceText\": \"k.A\",\n" +
            "          \"grade\": 0\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"sehr viel Übung\",\n" +
            "          \"grade\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"viel Übung\",\n" +
            "          \"grade\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"optimal\",\n" +
            "          \"grade\": 3\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"wenig Übung\",\n" +
            "          \"grade\": 4\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"zu wenig Übung\",\n" +
            "          \"grade\": 5\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"question\": \"Wie beurteilen Sie den Medieneinsatz der LV? (Beamer, Tafel, Overheadprojektor, Mobil-Telefone...)\",\n" +
            "      \"choices\": [\n" +
            "        {\n" +
            "          \"choiceText\": \"k.A\",\n" +
            "          \"grade\": 0\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"sehr viel Medien\",\n" +
            "          \"grade\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"viel Medien\",\n" +
            "          \"grade\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"optimal\",\n" +
            "          \"grade\": 3\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"wenig Medien\",\n" +
            "          \"grade\": 4\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"sehr wenig Medien\",\n" +
            "          \"grade\": 5\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"question\": \"Wie beurteilen Sie Ihren persönlichen Lernerfolg in dieser LV?\",\n" +
            "      \"choices\": [\n" +
            "        {\n" +
            "          \"choiceText\": \"k.A\",\n" +
            "          \"grade\": 0\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"sehr groß\",\n" +
            "          \"grade\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"groß\",\n" +
            "          \"grade\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"mittel\",\n" +
            "          \"grade\": 3\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"klein\",\n" +
            "          \"grade\": 4\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"sehr klein\",\n" +
            "          \"grade\": 5\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"question\": \"Welche Gesamtnote geben Sie der LV?\",\n" +
            "      \"choices\": [\n" +
            "        {\n" +
            "          \"choiceText\": \"k.A\",\n" +
            "          \"grade\": 0\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"sehr gut\",\n" +
            "          \"grade\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"gut\",\n" +
            "          \"grade\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"befriedigend\",\n" +
            "          \"grade\": 3\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"ausreichend\",\n" +
            "          \"grade\": 4\n" +
            "        },\n" +
            "        {\n" +
            "          \"choiceText\": \"ungenügend\",\n" +
            "          \"grade\": 5\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ],\n" +
            "  \"textQuestionsFirst\": false\n" +
            "}";



}
