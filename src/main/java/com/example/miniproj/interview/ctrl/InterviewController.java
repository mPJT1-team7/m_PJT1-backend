package com.example.miniproj.interview.ctrl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.miniproj.interview.domain.dto.InterviewLevelRequestDTO;
import com.example.miniproj.interview.service.InterviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    // 문항별 만족도 수정 API
    @PutMapping("/level")
    public ResponseEntity<String> updateLevel(@RequestBody InterviewLevelRequestDTO dto) {
        try {
            interviewService.updateLevel(dto);
            return ResponseEntity.ok("성공적으로 반영되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}