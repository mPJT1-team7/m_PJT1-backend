// 사용자가 보낼 답변
package com.example.miniproj.interview.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AnswerRequestDto {
    private Long interviewId; // 어떤 질문에 대한 답인지 식별하기 위함
    private String answerContent; // 사용자의 답변 내용
}