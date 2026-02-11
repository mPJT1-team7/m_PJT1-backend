// 프론트로 보낼 데이터: 생성된 질문 리스트
package com.example.miniproj.interview.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuestionResponseDto {
    private Long interviewId; // DB에 저장된 질문 ID
    private String question;  // AI가 생성한 질문 내용
}