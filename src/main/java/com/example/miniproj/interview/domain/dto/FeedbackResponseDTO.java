// 사용자에게 줄 피드백
package com.example.miniproj.interview.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeedbackResponseDTO {
    private Long interviewId;
    private String question; // 원래 질문
    private String answer;   // 내가 쓴 답변
    private String feedback; // AI의 조언
}