package com.example.miniproj.interview.domain.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class FeedbackBatchResponseDTO {
    private String userEmail;      // 0. 사용자 이메일
    private String resumeContent;  // 1. 자기소개서 내용
    private String title;
    private String progress;
    private List<FeedbackItem> feedbacks;
    private String category;

    @Getter
    @Builder
    public static class FeedbackItem {
        private Integer interviewId;  // 2. 질문번호
        private String question;   // 3. 질문
        private String answer;     // 4. 답변
        private String feedback;   // 5. 피드백
        private int level;         // 6. 사용자 평가
    }
}