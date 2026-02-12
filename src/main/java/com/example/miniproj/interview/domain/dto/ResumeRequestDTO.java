// 프론트에서 받는 데이터: 사용자ID, 직무, 제목, 내용

package com.example.miniproj.interview.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResumeRequestDTO {
    private Integer userId;     // 작성자 ID (테스트용)
    private String category; // 직무 (예: Backend, Frontend)
    private String title;    // 자소서 제목
    private String content;  // 자소서 내용
}