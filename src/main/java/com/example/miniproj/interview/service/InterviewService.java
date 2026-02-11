package com.example.miniproj.interview.service;

import com.example.miniproj.interview.dao.InterviewRepository;
import com.example.miniproj.interview.domain.dto.AnswerRequestDto;
import com.example.miniproj.interview.domain.dto.FeedbackResponseDto;
import com.example.miniproj.interview.domain.dto.QuestionResponseDto;
import com.example.miniproj.interview.domain.entity.InterviewEntity;
import com.example.miniproj.resume.dao.ResumeRepository;
import com.example.miniproj.interview.domain.dto.ResumeRequestDto;
import com.example.miniproj.resume.domain.entity.ResumeEntity;
import com.example.miniproj.user.dao.UserRepository;
import com.example.miniproj.user.domain.entity.UserEntity;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.security.autoconfigure.SecurityProperties.User;
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

    

    // 기능 1: 자소서 저장 + AI 질문 생성
    @Transactional
    public List<QuestionResponseDto> createInterview(ResumeRequestDto requestDto) {

        // 1. 사용자 조회
        UserEntity user = userRepository.findById(requestDto.getUserId().intValue())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다. ID=" + requestDto.getUserId()));

        // 2. 자소서 저장
        // (주의: ResumeEntity 내부 필드명은 제가 확인할 수 없어서 일반적인 이름(setUser 등)을 사용했습니다.
        // 만약 ResumeEntity도 InterviewEntity처럼 필드명이 다르다면 수정이 필요합니다.)
        ResumeEntity resume = new ResumeEntity();
        resume.setUser(user);
        resume.setCategory(requestDto.getCategory());
        resume.setTitle(requestDto.getTitle());
        resume.setContent(requestDto.getContent());
        resume.setProgress("연습 중");
        resume.setAnswer_state(false);
        resume.setBookmark(false);
        resume.setCreated_at(null);
        
        resumeRepository.save(resume);

        // 3. AI 질문 생성
        List<String> questions = openAiService.generateQuestions(
                requestDto.getCategory(), 
                requestDto.getContent()
        );

        // 4. 질문 저장 및 반환
        List<QuestionResponseDto> responseList = new ArrayList<>();

        for (String questionText : questions) {
            // [수정] Entity의 Builder 사용 및 필드명(created_at) 매칭
            InterviewEntity interview = InterviewEntity.builder()
                    .resume(resume)
                    .question(questionText)
                    .level(0)
                    .created_at(LocalDate.now()) // 팀원이 만든 변수명 created_at
                    .build();
            
            interviewRepository.save(interview); // 저장 시 interviewId(Integer) 생성됨

            // [수정] Integer ID -> Long DTO 변환
            responseList.add(QuestionResponseDto.builder()
                    .interviewId(Long.valueOf(interview.getInterviewId())) 
                    .question(questionText)
                    .build());
        }
        return responseList; 
    }

    // 기능 2: 답변 제출 및 피드백
    @Transactional
    public FeedbackResponseDto processAnswer(AnswerRequestDto requestDto) {
        
        // [수정] DTO(Long) -> Repository(Integer) 형변환 조회
        InterviewEntity interview = interviewRepository.findById(requestDto.getInterviewId().intValue())
                .orElseThrow(() -> new IllegalArgumentException("질문이 존재하지 않습니다. ID=" + requestDto.getInterviewId()));

        // 답변 업데이트
        interview.setAnswer(requestDto.getAnswerContent());
        
        // AI 피드백 생성
        String feedbackResult = openAiService.generateFeedback(
                interview.getQuestion(), 
                requestDto.getAnswerContent()
        );

        // 피드백 업데이트
        interview.setFeedback(feedbackResult);
        interviewRepository.save(interview);

        // 결과 반환
        return FeedbackResponseDto.builder()
                .interviewId(Long.valueOf(interview.getInterviewId())) // Integer -> Long
                .question(interview.getQuestion())
                .answer(interview.getAnswer())
                .feedback(interview.getFeedback())
                .build();
    }
}