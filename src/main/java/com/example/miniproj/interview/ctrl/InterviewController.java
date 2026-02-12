package com.example.miniproj.interview.ctrl;


import com.example.miniproj.interview.domain.dto.*;
import com.example.miniproj.interview.service.InterviewService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/interview")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/create")
    public ResponseEntity<List<QuestionResponseDTO>> create(
        @RequestBody ResumeRequestDTO dto,
        Authentication authentication) {
        
        try {
            List<QuestionResponseDTO> dtos = interviewService.createInterview(dto, authentication.getName());
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/feedback")
    public ResponseEntity<FeedbackBatchResponseDTO> feedback(
        @RequestBody AnswerBatchRequestDTO dto, Authentication auth) {
        try {
            return ResponseEntity.ok(interviewService.processBatchAnswers(dto, auth.getName()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // 기능 3: 과거 면접 기록 상세 조회
    @GetMapping("/detail/{resumeId}")
    public ResponseEntity<FeedbackBatchResponseDTO> getDetail(
        @PathVariable("resumeId") Integer resumeId, 
        Authentication auth) {
        
        try {
            // 서비스에 만든 조회 로직 호출
            FeedbackBatchResponseDTO detail = interviewService.getInterviewDetail(resumeId, auth.getName());
            return ResponseEntity.ok(detail);
        } catch (IllegalArgumentException e) {
            // 해당 ID의 자소서가 없는 경우
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SecurityException e) {
            // 본인 자소서가 아닌 경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // 기능 4: 특정 면접의 질문 목록만 가져오기 (연습용)
    @GetMapping("/questions/{resumeId}")
    public ResponseEntity<List<QuestionResponseDTO>> getQuestions(
        @PathVariable("resumeId") Integer resumeId, 
        Authentication auth) {
        
        try {
            List<QuestionResponseDTO> questions = interviewService.getInterviewQuestions(resumeId, auth.getName());
            return ResponseEntity.ok(questions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // 기능 5: 사용자 평가(level) 업데이트
    @PutMapping("/level")
    public ResponseEntity<String> updateLevel(@RequestBody InterviewLevelRequestDTO dto, Authentication auth) {
        try {
            interviewService.updateLevel(dto, auth.getName());
            return ResponseEntity.ok("성공적으로 반영되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}