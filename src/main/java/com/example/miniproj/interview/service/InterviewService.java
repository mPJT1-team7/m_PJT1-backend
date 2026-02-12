package com.example.miniproj.interview.service;

import com.example.miniproj.interview.dao.InterviewRepository;
import com.example.miniproj.interview.domain.dto.*;
import com.example.miniproj.interview.domain.entity.InterviewEntity;
import com.example.miniproj.resume.dao.ResumeRepository;
import com.example.miniproj.resume.domain.entity.ResumeEntity;
import com.example.miniproj.user.dao.UserRepository;
import com.example.miniproj.user.domain.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final ResumeRepository resumeRepository;
    private final InterviewRepository interviewRepository;
    private final UserRepository userRepository;
    private final OpenAiService openAiService;

    // 기능 1: 자소서 저장 + 질문 5개 생성
    @Transactional
    public List<QuestionResponseDTO> createInterview(ResumeRequestDTO requestDto, String authEmail) {
        UserEntity user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        if (!user.getEmail().equals(authEmail)) throw new SecurityException("권한 없음");

        // 저장
        ResumeEntity resume = ResumeEntity.builder()
                .user(user)
                .category(requestDto.getCategory())
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .answer_state(false)
                .created_at(LocalDate.now())
                .build();
        resumeRepository.save(resume);

        // AI 질문
        List<String> questions = openAiService.generateQuestions(resume.getCategory(), resume.getContent());
        List<QuestionResponseDTO> dtos = new ArrayList<>();

        // 질문 저장
        for (String q : questions) {
            InterviewEntity entity = interviewRepository.save(InterviewEntity.builder()
                    .resume(resume)
                    .question(q)
                    .level(0)
                    .created_at(LocalDate.now())
                    .build());
            dtos.add(QuestionResponseDTO.builder()
                .interviewId(
                    entity.getInterviewId())
                    .question(q)
                    .build());
        }
        return dtos;
    }

    // 기능 2: 답변 5개 저장 + 피드백 생성
    @Transactional
    public FeedbackBatchResponseDTO processBatchAnswers(AnswerBatchRequestDTO requestDto, String authEmail) {
        Integer firstId = requestDto.getAnswers().get(0).getInterviewId();
        InterviewEntity first = interviewRepository.findById(firstId.intValue()).orElseThrow();
        ResumeEntity resume = first.getResume();

        if (!resume.getUser().getEmail().equals(authEmail)) throw new SecurityException("권한 없음");

        List<FeedbackBatchResponseDTO.FeedbackItem> feedbackItems = new ArrayList<>();
        for (AnswerBatchRequestDTO.AnswerItem item : requestDto.getAnswers()) {
            InterviewEntity interview = interviewRepository.findById(item.getInterviewId().intValue()).orElseThrow();
            interview.setAnswer(item.getAnswerContent());
            
            String feedback = openAiService.generateFeedback(interview.getQuestion(), interview.getAnswer(), resume.getCategory(), resume.getContent());
            interview.setFeedback(feedback);
            interviewRepository.save(interview);

            feedbackItems.add(FeedbackBatchResponseDTO.FeedbackItem.builder()
                    .interviewId(interview.getInterviewId())
                    .question(interview.getQuestion())
                    .answer(interview.getAnswer())
                    .feedback(feedback)
                    .level(interview.getLevel()).build());
        }

        resume.setAnswer_state(true);

        return FeedbackBatchResponseDTO.builder()
            .userEmail(authEmail)
            .resumeContent(resume.getContent())
            .title(resume.getTitle())
            .progress(resume.getProgress())
            .feedbacks(feedbackItems)
            .build();
    }
    
    // 기능 3: 저장된 면접 기록 조회 (기능 2와 동일한 반환값)
    @Transactional(readOnly = true) // 단순 조회이므로 readOnly 설정
    public FeedbackBatchResponseDTO getInterviewDetail(Integer resumeId, String authEmail) {
        
        // 1. 자소서 조회
        ResumeEntity resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 자소서를 찾을 수 없습니다."));

        // 2. 권한 체크 (본인 확인)
        if (!resume.getUser().getEmail().equals(authEmail)) {
            throw new SecurityException("조회 권한이 없습니다.");
        }

        // 3. 자소서에 딸린 질문/답변/피드백(InterviewEntity)들을 DTO로 변환
        List<FeedbackBatchResponseDTO.FeedbackItem> feedbackItems = new ArrayList<>();
        
        for (InterviewEntity it : resume.getInterview()) {
            feedbackItems.add(FeedbackBatchResponseDTO.FeedbackItem.builder()
                    .interviewId(it.getInterviewId())
                    .question(it.getQuestion())
                    .answer(it.getAnswer())
                    .feedback(it.getFeedback())
                    .level(it.getLevel())
                    .build());
        }

        // 4. 기능 2에서 완성한 DTO 구조 그대로 반환
        return FeedbackBatchResponseDTO.builder()
                .userEmail(authEmail)
                .resumeContent(resume.getContent())
                .title(resume.getTitle())
                .progress(resume.getProgress())
                .feedbacks(feedbackItems)
                .category(resume.getCategory())
                .build();
    }

    // 기능 4: 모의 면접을 위한 질문 목록만 조회
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getInterviewQuestions(Integer resumeId, String authEmail) {
        
        // 1. 자소서 조회 및 권한 체크
        ResumeEntity resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 자소서를 찾을 수 없습니다."));
        
        if (!resume.getUser().getEmail().equals(authEmail)) {
            throw new SecurityException("조회 권한이 없습니다.");
        }

        // 2. 질문들만 뽑아서 QuestionResponseDTO 리스트로 변환
        List<QuestionResponseDTO> questionList = new ArrayList<>();
        for (InterviewEntity it : resume.getInterview()) {
            questionList.add(QuestionResponseDTO.builder()
                    .interviewId(it.getInterviewId())
                    .question(it.getQuestion())
                    .build());
        }

        return questionList;
    }


    @Transactional
    public void updateLevel(InterviewLevelRequestDTO dto, String email) {
        InterviewEntity entity = interviewRepository.findById(dto.getInterviewId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 면접 문항입니다. id=" + dto.getInterviewId()));
            
        // 2. 권한 체크 (본인 확인)
        if (!entity.getResume().getUser().getEmail().equals(email)) {
            throw new SecurityException("조회 권한이 없습니다.");
        }
        entity.setLevel(dto.getLevel());
        interviewRepository.save(entity);
    }
}