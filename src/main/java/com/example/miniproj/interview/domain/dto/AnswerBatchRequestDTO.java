package com.example.miniproj.interview.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class AnswerBatchRequestDTO {
    private List<AnswerItem> answers; // 5개의 답변 리스트

    @Getter
    @NoArgsConstructor
    public static class AnswerItem {
        private Integer interviewId;      // 질문 ID
        private String answerContent;  // 사용자의 답변
    }
}