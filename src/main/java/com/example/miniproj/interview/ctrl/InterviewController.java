package com.example.miniproj.interview.ctrl;

import com.example.miniproj.interview.domain.dto.AnswerRequestDto;
import com.example.miniproj.interview.domain.dto.FeedbackResponseDto;
import com.example.miniproj.interview.domain.dto.QuestionResponseDto;
import com.example.miniproj.interview.domain.dto.ResumeRequestDto;
import com.example.miniproj.interview.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interview") // 기본 주소 설정
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    // 기능 1: 자소서 입력 및 AI 면접 질문 생성
    // 주소: POST http://localhost:8080/api/interview/init
    @PostMapping("/init")
    public ResponseEntity<List<QuestionResponseDto>> initInterview(@RequestBody ResumeRequestDto requestDto) {
        
        // 서비스 로직 실행 (저장 -> AI 생성 -> 저장 -> 반환)
        List<QuestionResponseDto> response = interviewService.createInterview(requestDto);
        
        // 결과 반환 (200 OK)
        return ResponseEntity.ok(response);
    }

    // 기능 2: 답변 제출 및 피드백 받기
    // 주소: POST http://localhost:8080/api/interview/answer
    @PostMapping("/answer")
    public ResponseEntity<FeedbackResponseDto> submitAnswer(@RequestBody AnswerRequestDto requestDto) {
        
        FeedbackResponseDto response = interviewService.processAnswer(requestDto);
        
        return ResponseEntity.ok(response);
    }
}