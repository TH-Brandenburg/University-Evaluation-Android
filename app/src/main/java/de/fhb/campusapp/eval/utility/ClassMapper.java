package de.fhb.campusapp.eval.utility;

import java.util.ArrayList;

import de.fhb.ca.dto.AnswersDTO;
import de.fhb.ca.dto.MultipleChoiceQuestionDTO;
import de.fhb.ca.dto.QuestionsDTO;
import de.fhb.ca.dto.ResponseDTO;
import de.fhb.ca.dto.util.ChoiceDTO;
import de.fhb.ca.dto.util.MultipleChoiceAnswerDTO;
import de.fhb.ca.dto.util.TextAnswerDTO;
import de.fhb.ca.dto.util.TextQuestionDTO;
import de.fhb.campusapp.eval.utility.vos.AnswersVO;
import de.fhb.campusapp.eval.utility.vos.ChoiceVO;
import de.fhb.campusapp.eval.utility.vos.MultipleChoiceAnswerVO;
import de.fhb.campusapp.eval.utility.vos.MultipleChoiceQuestionVO;
import de.fhb.campusapp.eval.utility.vos.QuestionsVO;
import de.fhb.campusapp.eval.utility.vos.TextAnswerVO;
import de.fhb.campusapp.eval.utility.vos.TextQuestionVO;

/**
 * Created by Sebastian Müller on 15.05.2016.
 */
public class ClassMapper{

    public static AnswersVO answersDTOToAnswersVOMapper(AnswersDTO dto){
        AnswersVO vo = new AnswersVO();
        ArrayList<MultipleChoiceAnswerVO> convertedMcChoiceAnswers = new ArrayList<>();
        ArrayList<TextAnswerVO> convertedTextAnswers = new ArrayList<>();

        for(MultipleChoiceAnswerDTO answerDTO : dto.getMcAnswers()){
            convertedMcChoiceAnswers.add(mcAnswerDTOToMcAnswerVO(answerDTO));
        }

        for(TextAnswerDTO textAnswerDTO : dto.getTextAnswers()){
            convertedTextAnswers.add(textAnswerDTOToTextAnswerVO(textAnswerDTO));
        }

        vo.setDeviceID(dto.getDeviceID());
        vo.setStudyPath(dto.getStudyPath());
        vo.setVoteToken(dto.getVoteToken());
        vo.setMcAnswers(convertedMcChoiceAnswers);
        vo.setTextAnswers(convertedTextAnswers);

        return vo;
    }


    public static QuestionsVO questionsDTOToQuestionsVOMapper(QuestionsDTO dto){
        QuestionsVO vo = new QuestionsVO();
        ArrayList<MultipleChoiceQuestionVO> convertedMCQuestions = new ArrayList<>();
        ArrayList<TextQuestionVO> convertedTextQuestions = new ArrayList<>();

        for(MultipleChoiceQuestionDTO questionDTO : dto.getMultipleChoiceQuestionDTOs()){
            convertedMCQuestions.add(mcQuestionDTOToMcQuestionVO(questionDTO));
        }

        for(TextQuestionDTO textQuestionDTO : dto.getTextQuestions()){
            convertedTextQuestions.add(textQuestionDTOToTextQuestionVO(textQuestionDTO));
        }

        vo.setMultipleChoiceQuestionVOs(convertedMCQuestions);
        vo.setTextQuestions(convertedTextQuestions);
        vo.setStudyPaths(dto.getStudyPaths());
        vo.setTextQuestionsFirst(dto.getTextQuestionsFirst());

        return vo;
    }

    static TextAnswerVO textAnswerDTOToTextAnswerVO(TextAnswerDTO dto){
        return new TextAnswerVO(dto.getQuestionID(), dto.getQuestionText(), dto.getAnswerText());
    }

    static TextQuestionVO textQuestionDTOToTextQuestionVO(TextQuestionDTO dto){
        return new TextQuestionVO(dto.getQuestionID(), dto.getQuestionText(), dto.getOnlyNumbers(), dto.getMaxLength());
    }

    static MultipleChoiceAnswerVO mcAnswerDTOToMcAnswerVO(MultipleChoiceAnswerDTO dto){
        return new MultipleChoiceAnswerVO(dto.getQuestionText(), choiceDTOToChoiceVO(dto.getChoice()));
    }

    static MultipleChoiceQuestionVO mcQuestionDTOToMcQuestionVO(MultipleChoiceQuestionDTO dto){
        ArrayList<ChoiceVO> convertedList = new ArrayList<ChoiceVO>();
        for(ChoiceDTO choice : dto.getChoices()){
            convertedList.add(choiceDTOToChoiceVO(choice));
        }
        return new MultipleChoiceQuestionVO(dto.getQuestion(), convertedList);
    }

    static ChoiceVO choiceDTOToChoiceVO(ChoiceDTO dto){
        return new ChoiceVO(dto.getChoiceText(), dto.getGrade());
    }

    public static AnswersDTO answersVOToAnswerDTOMapper(AnswersVO vo){
        AnswersDTO dto = new AnswersDTO();
        ArrayList<MultipleChoiceAnswerDTO> convertedMCAnswers = new ArrayList<>();
        ArrayList<TextAnswerDTO> convertedTextAnswers = new ArrayList<>();

        for(MultipleChoiceAnswerVO mcVO : vo.getMcAnswers()){
            convertedMCAnswers.add(mcAnswerVOTomcAnswerDTOMapper(mcVO));
        }

        for(TextAnswerVO textVO : vo.getTextAnswers()){
            convertedTextAnswers.add(textAnswerVOTOTextAnswerDTOMapper(textVO));
        }

        dto.setDeviceID(vo.getDeviceID());
        dto.setVoteToken(vo.getVoteToken());
        dto.setStudyPath(vo.getStudyPath());
        dto.setMcAnswers(convertedMCAnswers);
        dto.setTextAnswers(convertedTextAnswers);

        return dto;
    }

    static MultipleChoiceAnswerDTO mcAnswerVOTomcAnswerDTOMapper(MultipleChoiceAnswerVO vo){
        return new MultipleChoiceAnswerDTO(vo.getQuestionText(), choiceVOToChoiceDTO(vo.getChoice()));
    }

    static TextAnswerDTO textAnswerVOTOTextAnswerDTOMapper(TextAnswerVO vo){
        return new TextAnswerDTO(vo.getQuestionID(), vo.getQuestionText(), vo.getAnswerText());
    }

    static ChoiceDTO choiceVOToChoiceDTO(ChoiceVO vo){
        return new ChoiceDTO(vo.getChoiceText(), vo.getGrade());
    }



}
